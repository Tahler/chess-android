package edu.neumont.pro180.chess.core.controller;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import edu.neumont.pro180.chess.core.model.Board;
import edu.neumont.pro180.chess.core.model.Color;
import edu.neumont.pro180.chess.core.model.Move;
import edu.neumont.pro180.chess.core.model.Tile;
import edu.neumont.pro180.chess.core.view.SwingView;
import edu.neumont.pro180.chess.core.view.View;

public class clientController implements View.Listener {
    private final AtomicReference<Board> board;
    //Should we validate, or leave that to the real controller?
//    private final MoveValidator validator;
    private final View view;
    private final Thread clientThread;
    private final Queue<byte[]> sendmessage;
    private final AtomicBoolean done;

    
    public clientController(View v) {
        this.board = new AtomicReference<Board>(new Board());
//        this.validator = new MoveValidator(board.get());
        this.view = v;
        this.view.setListener(this);
//        this.view.displayBoard(board.get());
        this.sendmessage = new LinkedBlockingQueue<byte[]>();
        this.done = new AtomicBoolean(false);
        clientThread = new Thread(new Runnable(){
			@Override
			public void run() {
				//find the server first
				/*
				List<byte[]> localaddresses = new ArrayList<byte[]>();
				try{
				for(Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces(); n.hasMoreElements(); ){
					NetworkInterface i = n.nextElement();
					for(InterfaceAddress a : i.getInterfaceAddresses()){
						short netmask = a.getNetworkPrefixLength();
						localaddresses.add(a.getAddress().getAddress());
						for(short p=netmask>24?netmask:24; p<32; p++){
							byte[] tmp = new byte[]{localaddresses.get(localaddresses.size()-1)[0], localaddresses.get(localaddresses.size()-1)[1], localaddresses.get(localaddresses.size()-1)[2], localaddresses.get(localaddresses.size()-1)[3]};
							for(short j=netmask>16?netmask:16; j<24; j++){
								for(short k=netmask>8?netmask:8; k<16; k++){
									for(short m=netmask; m<8; m++){
										
									}
								}
							}
						}
					}
        		}
				}catch(SocketException e){
					e.printStackTrace();
				}
				*/
				
				//once found, connect to the server
				try(Socket s = new Socket("0.0.0.0", 31415);){
					DataInputStream is = new DataInputStream(s.getInputStream());
					DataOutputStream os = new DataOutputStream(s.getOutputStream());
					board.set(readBoard(is));
					view.displayBoard(board.get());
					while(!done.get()){
						//receive one message
						char request = (char) is.read();
						switch(request){
						case 'B':{//displayBoard
							board.set(readBoard(is));
							view.displayBoard(board.get());
						}break;
						case 'C':{//notifyCheck
							view.notifyCheck();
						}break;
						case 'G':{//notifyGameOver
							ObjectInputStream objstr = new ObjectInputStream(is);
							Color winner = (Color) objstr.readObject();
							view.notifyGameOver(winner);
							done.set(true);
						}break;
						case 'H':{//hilightTiles
							ObjectInputStream objstr = new ObjectInputStream(is);
							List<Move> h = (List<Move>) objstr.readObject();
				            List<Tile> ends = new ArrayList<>();
				            for (Move m : h) ends.add(m.getEnd());
							view.highlightTiles(h.get(0).getStart(), ends);
						}break;
						case 'P':{//getPawnPromotion
							ObjectOutputStream objstr = new ObjectOutputStream(os);
							objstr.writeObject(view.getPawnPromotion());
							os.flush();
						}break;
						case 'p':{//ping
							//server was just checking up on us
						}break;
						default:
							System.err.println("Bad request received from server: "+request);
						}
						
						//send many messages
						Queue<byte[]> msgs = new LinkedList<byte[]>();
						while(sendmessage.size()>0){
							msgs.add(sendmessage.poll());
						}
						os.writeInt(msgs.size());
						for(byte[] m : msgs){
							os.write(m);
							os.flush();
							switch(m[0]){
							case 'T':{//tileSelected
								//TODO: tileSelected

							}break;
							case 'M':{//moveSelected
								//TODO: moveSelected

							}break;
							default:
								System.err.println("Sending an unknown message to server, may break communications :"+m[0]);
							}
						}
					}
				}catch(IOException e){
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
				
			}
		});
        clientThread.start();
    }
    
    private Board readBoard(InputStream is) throws IOException, ClassNotFoundException{
    	char buf;
    	if((buf=(char)is.read()) != 'B'){
    		throw new ClassNotFoundException("No Board in InputStream (identifier was '"+buf+"')");
    	}
    	ObjectInputStream bytstr = new ObjectInputStream(is);
    	return (Board) bytstr.readObject();
    }

    @Override
    public void tileSelected(Tile tile) {
        if (tile != null) {
            System.out.println(tile.x + ", " + tile.y);
            
            try{
                ByteArrayOutputStream bytstr = new ByteArrayOutputStream();
                bytstr.write('T');
            	ObjectOutputStream objstr = new ObjectOutputStream(bytstr);
            	objstr.writeObject(tile);
                objstr.close();
            	sendmessage.add(bytstr.toByteArray());
            }catch(IOException e){
            	e.printStackTrace();
            }
        }
    }

    @Override
    public void moveSelected(Move move) {
        System.out.println("View sent a move");
        try{
        	ByteArrayOutputStream bytstr = new ByteArrayOutputStream();
        	bytstr.write('M');
        	ObjectOutputStream objstr = new ObjectOutputStream(bytstr);
        	objstr.writeObject(move);
        	objstr.close();
        	sendmessage.add(bytstr.toByteArray());
        }catch(IOException e){
        	e.printStackTrace();
        }
    }
    
    
    public static void main(String[] args){
    	new clientController(new SwingView());
    }
}
