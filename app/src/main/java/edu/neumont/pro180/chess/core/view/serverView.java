package edu.neumont.pro180.chess.core.view;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import edu.neumont.pro180.chess.core.controller.Controller;
import edu.neumont.pro180.chess.core.controller.clientController;
import edu.neumont.pro180.chess.core.model.Board;
import edu.neumont.pro180.chess.core.model.Color;
import edu.neumont.pro180.chess.core.model.Move;
import edu.neumont.pro180.chess.core.model.Piece;
import edu.neumont.pro180.chess.core.model.Piece.Type;
import edu.neumont.pro180.chess.core.model.Tile;

public class serverView implements View{
	private final Thread serverThread;
	private final AtomicInteger serverPort;
	private final Thread udpBroadcast;
	private final AtomicBoolean done;
	private View.Listener listener;
	private final List<ConnectionRunnable> clientthreads;
	private final AtomicReference<Piece.Type> promote;
	private final AtomicReference<Board> board;
	private final List<Tile> hilights;
	private final AtomicLong lastmessagetime;
	
	
	public class ConnectionRunnable implements Runnable{
		public final Queue<byte[]> sendmessages;
		public final Socket s;
		public ConnectionRunnable(Socket s){
			this.s = s;
			this.sendmessages = new LinkedBlockingQueue<byte[]>();
		}
		@Override public void run(){
			synchronized(clientthreads){clientthreads.add(this);}
			try{
				DataInputStream is = new DataInputStream(s.getInputStream());
				DataOutputStream os = new DataOutputStream(s.getOutputStream());
				writeBoard(board.get(), os);
				os.flush();
				while(!s.isClosed() && !done.get()){
					//send one message
					byte[] msg;
					if((msg=sendmessages.poll())==null){//no message to send
						msg = new byte[]{'p'};//just ping the client
					}
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
							Tile tmptile = readTile(is);
							//only the first clientthread may select tiles
							if(clientthreads.get(0)==this){listener.tileSelected(tmptile);}
						}break;
						case 'M':{//moveSelected
							Move tmpmove = readMove(is);
							//only the first clientthread may make moves
							if(clientthreads.get(0)==this){listener.moveSelected(tmpmove);}
						}break;
						default:
							System.err.println("Bad request received from client: "+request);
						}
						lastmessagetime.set(new Date().getTime());
					}
					Thread.yield();
				}
			}catch(EOFException e){
				//disconnected
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}finally {
				synchronized(clientthreads){clientthreads.remove(this);}
				try {
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public serverView(){
		done = new AtomicBoolean(false);
		clientthreads = new ArrayList<ConnectionRunnable>();
		promote = new AtomicReference<Piece.Type>(null);
		board = new AtomicReference<Board>(null);
		hilights = new ArrayList<Tile>();
		lastmessagetime = new AtomicLong(-1);
		serverPort = new AtomicInteger(-1);
		serverThread = new Thread(new Runnable(){
			@Override
			public void run() {
				try(ServerSocket ss = new ServerSocket(0);){
					serverPort.set(ss.getLocalPort());
					while(!done.get()){
						Socket s = null;
						try{
							s = ss.accept();//we close s in the ConnectionRunnable, so dont close it here
							new Thread(new ConnectionRunnable(s)).start();
						}catch(IOException e){
							e.printStackTrace();
						}
					}
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		});
		serverThread.start();
		
		udpBroadcast = new Thread(new Runnable(){
			@Override
			public void run() {
				while(!done.get()){
					Integer portnum;
					if((portnum=serverPort.get())>=0){
						ByteArrayOutputStream udpbuf = new ByteArrayOutputStream();
						try{
						DataOutputStream datstr = new DataOutputStream(udpbuf);
						datstr.write('C');
						datstr.write('H');
						datstr.write('E');
						datstr.write('S');
						datstr.write('S');
						datstr.writeInt(portnum);
						}catch(IOException e){
							e.printStackTrace();
						}
						try {
							for(NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())){
								for(InterfaceAddress ia : ni.getInterfaceAddresses()){
									if(ia.getBroadcast()!=null){
										DatagramPacket udppack = new DatagramPacket(udpbuf.toByteArray(), udpbuf.size(), ia.getBroadcast(), 31415);
										
										try(DatagramSocket udpsock = new DatagramSocket()){
											udpsock.send(udppack);
										} catch (IOException e) {
											System.err.println("UDP server IOException "+e.getLocalizedMessage());
//											e.printStackTrace();
										}
									}
								}
							}
						} catch (SocketException e) {
							System.err.println("UDP server SocketException "+e.getLocalizedMessage());
//							e.printStackTrace();
						}
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
//						e.printStackTrace();
					}
				}
			}
		});
		udpBroadcast.start();
	}
	
	private void writeBoard(Board b, OutputStream os) throws IOException{
		DataOutputStream datstr = new DataOutputStream(os);
		ByteArrayOutputStream bytstr = new ByteArrayOutputStream();
		ObjectOutputStream objstr = new ObjectOutputStream(bytstr);
		objstr.writeObject(b);
		objstr.close();
		datstr.write('B');
		datstr.writeInt(bytstr.size());
		datstr.write(bytstr.toByteArray());
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
			sendmessage(bytstr.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void notifyCheck() {
		sendmessage(new byte[]{'C'});
	}
	@Override
	public void notifyGameOver(Color result) {
		done.set(result!=null);
		ByteArrayOutputStream bytstr = new ByteArrayOutputStream();
		try {
			bytstr.write('G');
			writeColor(result, bytstr);
			sendmessage(bytstr.toByteArray());
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
				sendmessage(bytstr.toByteArray());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public Type getPawnPromotion() {
		promote.set(null);
		sendmessage(new byte[]{'P'});
		Type ret;
		long lasttime = new Date().getTime();
		final int reaskTime = 5000;
		//wait for a response from the client
		while((ret=promote.get())==null){
			if(new Date().getTime()-lasttime>reaskTime){
				sendmessage(new byte[]{'P'});
				lasttime = new Date().getTime();
			}
			Thread.yield();
		}
		return ret;
	}
	@Override
	public void setListener(Listener listener) {
		this.listener = listener;
	}
	
	private void sendmessage(byte[] msg){
		if(msg[0]=='B'||msg[0]=='C'||msg[0]=='G'){
			for(ConnectionRunnable r : clientthreads){
				r.sendmessages.add(msg);
			}
		}
		else{
			boolean keeptrying = true;
			while(keeptrying){
				try{
					clientthreads.get(0).sendmessages.add(msg);
					keeptrying = false;
				}catch(IndexOutOfBoundsException e){
					Thread.yield();
				}
			}
		}
	}
	
	
	public static void main(String[] args){
    	new Controller(new multiView(new SwingView(), new serverView()));
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
    	clientController.main(args);
    	clientController.main(args);
	}

}
