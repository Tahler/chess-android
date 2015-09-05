package edu.neumont.pro180.chess.core.view;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import edu.neumont.pro180.chess.core.model.Board;
import edu.neumont.pro180.chess.core.model.Color;
import edu.neumont.pro180.chess.core.model.Move;
import edu.neumont.pro180.chess.core.model.Piece;
import edu.neumont.pro180.chess.core.model.Piece.Type;
import edu.neumont.pro180.chess.core.model.Tile;

public class serverView implements View{
	private final Thread serverThread;
	private final Thread messagepingThread;
	private final AtomicBoolean done;
	private View.Listener listener;
	private final Queue<byte[]> sendmessages;
	private final AtomicReference<Piece.Type> promote;
	private final AtomicReference<Board> board;
	private final List<Tile> hilights;
	private final AtomicLong lastmessagetime;
	
	
	public serverView(){
		done = new AtomicBoolean(false);
		sendmessages = new LinkedBlockingQueue<byte[]>();
		promote = new AtomicReference<Piece.Type>(null);
		board = new AtomicReference<Board>(null);
		hilights = new ArrayList<Tile>();
		lastmessagetime = new AtomicLong(-1);
		serverThread = new Thread(new Runnable(){
			@Override
			public void run() {
				try(ServerSocket ss = new ServerSocket(31415);){
					while(!done.get()){
						//only accept one client at a time, if he disconnects another may finish the game for him
						try(Socket s = ss.accept();){
							DataInputStream is = new DataInputStream(s.getInputStream());
							DataOutputStream os = new DataOutputStream(s.getOutputStream());
							os.write('B');
							writeBoard(board.get(), os);
							os.flush();
							while(!s.isClosed()){
								//send one message
								byte[] msg = sendmessages.poll();
								os.write(msg);
								os.flush();
								switch(msg[0]){
								case 'B'://displayBoard
								case 'C'://notifyCheck
								case 'G'://notifyGameOver
								case 'H'://hilightTiles
								case 'p'://ping
								break;
								case 'P':{//getPawnPromotion
									promote.set(readType(is));
								}break;
								default:
									System.err.println("Sending unknwon message to client :"+msg[0]);
								}
								lastmessagetime.set(new Date().getTime());
								
								//receive many messages
								for(int i=is.readInt(); i>0; i--){
									char request = (char) is.read();
									switch(request){
									case 'T':{//tileSelected
										listener.tileSelected(readTile(is));
									}break;
									case 'M':{//moveSelected
										listener.moveSelected(readMove(is));
									}break;
									/*
									//We could have these here to allow the clientController to query us on the current state.
									case 'B':{//displayBoard
									}break;
									case 'C':{//notifyCheck
									}break;
									case 'G':{//notifyGameOver
									}break;
									case 'H':{//hilightTiles
									}break;
									case 'P':{//getPawnPromotion
									}break;
									case 'p':{//ping
										//just check-up on the client
									}break;
									 */
									default:
										System.err.println("Bad request received from client: "+request);
									}
									lastmessagetime.set(new Date().getTime());
								}
							}
						}catch(IOException | ClassNotFoundException e){
							e.printStackTrace();
						}
					}
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		});
		serverThread.start();
		messagepingThread = new Thread(new Runnable(){
			public static final int PINGWAIT = 2000;
			public static final int PINGSLEEP = PINGWAIT;
			@Override
			public void run() {
				while(!done.get()){
					if(sendmessages.size()==0 && new Date().getTime()-lastmessagetime.get() > PINGWAIT){
						sendmessages.add(new byte[]{'p'});
					}
					try {
						Thread.sleep(PINGSLEEP);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		messagepingThread.start();
	}
	
	private void writeBoard(Board b, OutputStream os) throws IOException{
		ByteArrayOutputStream bytstr = new ByteArrayOutputStream();
		bytstr.write('B');
		ObjectOutputStream objstr = new ObjectOutputStream(bytstr);
		objstr.writeObject(b);
		objstr.close();
		os.write(bytstr.toByteArray());
	}
	private void writeColor(Color c, OutputStream os) throws IOException{
		ByteArrayOutputStream bytstr = new ByteArrayOutputStream();
		ObjectOutputStream objstr = new ObjectOutputStream(bytstr);
		objstr.writeObject(c);
		objstr.close();
		os.write(bytstr.toByteArray());
	}
	private void writeTileList(List<Tile> l, OutputStream os) throws IOException{
		ByteArrayOutputStream bytstr = new ByteArrayOutputStream();
		ObjectOutputStream objstr = new ObjectOutputStream(bytstr);
		objstr.writeObject(l);
		objstr.close();
		os.write(bytstr.toByteArray());
	}
	private Piece.Type readType(InputStream is) throws IOException, ClassNotFoundException{
		ObjectInputStream objstr = new ObjectInputStream(is);
		return (Type) objstr.readObject();
	}
	private Tile readTile(InputStream is) throws ClassNotFoundException, IOException{
		ObjectInputStream objstr = new ObjectInputStream(is);
		return (Tile) objstr.readObject();
	}
	private Move readMove(InputStream is) throws ClassNotFoundException, IOException{
		ObjectInputStream objstr = new ObjectInputStream(is);
		return (Move) objstr.readObject();
	}
	
	@Override
	public void displayBoard(Board pieces) {
		board.set(pieces);
		ByteArrayOutputStream bytstr = new ByteArrayOutputStream();
		try {
			writeBoard(pieces, bytstr);
			sendmessages.add(bytstr.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void notifyCheck() {
		sendmessages.add(new byte[]{'C'});
	}
	@Override
	public void notifyGameOver(Color result) {
		done.set(result!=null);
		ByteArrayOutputStream bytstr = new ByteArrayOutputStream();
		try {
			bytstr.write('G');
			writeColor(result, bytstr);
			sendmessages.add(bytstr.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void highlightTiles(Tile start, List<Tile> ends) {
		synchronized(hilights){
			hilights.clear();
			hilights.add(start);
			hilights.addAll(ends);
			ByteArrayOutputStream bytstr = new ByteArrayOutputStream();
			try {
				bytstr.write('H');
				writeTileList(hilights, bytstr);
				sendmessages.add(bytstr.toByteArray());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public Type getPawnPromotion() {
		promote.set(null);
		sendmessages.add(new byte[]{'P'});
		Type ret;
		//wait for a response from the client
		while((ret=promote.get())==null){
			Thread.yield();
		}
		return ret;
	}
	@Override
	public void setListener(Listener listener) {
		this.listener = listener;
	}

}
