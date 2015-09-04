package edu.neumont.pro180.chess.core.view;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.neumont.pro180.chess.core.model.Board;
import edu.neumont.pro180.chess.core.model.Color;
import edu.neumont.pro180.chess.core.model.Move;
import edu.neumont.pro180.chess.core.model.Piece;
import edu.neumont.pro180.chess.core.model.Piece.Type;
import edu.neumont.pro180.chess.core.model.Tile;

public class serverView implements View{
	Thread serverThread;
	AtomicBoolean done;
	View.Listener listener;
	
	public serverView(){
		done = new AtomicBoolean(false);
		serverThread = new Thread(new Runnable(){
			@Override
			public void run() {
				
			}
		});
		serverThread.start();
	}
	
	@Override
	public void displayBoard(Board pieces) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void notifyCheck() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void notifyGameOver(Color result) {
		done.set(true);
	}
	@Override
	public void highlightTiles(Tile start, List<Tile> ends) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Type getPawnPromotion() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setListener(Listener listener) {
		this.listener = listener;
	}

}
