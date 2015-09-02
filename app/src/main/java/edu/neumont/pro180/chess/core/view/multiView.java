package edu.neumont.pro180.chess.core.view;

import java.util.ArrayList;
import java.util.List;

import edu.neumont.pro180.chess.core.model.Color;
import edu.neumont.pro180.chess.core.model.Move;
import edu.neumont.pro180.chess.core.model.Piece;
import edu.neumont.pro180.chess.core.model.Piece.Type;
import edu.neumont.pro180.chess.core.model.Tile;

public class multiView implements View{
	private final List<View> views;
	
	public multiView(View...views){
		this.views = new ArrayList<View>();
		for(View v : views){
			this.views.add(v);
		}
		
	}
	
	@Override
	public void displayBoard(Piece[][] pieces) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void notifyCheck() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void notifyGameOver(Color result) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void highlightTiles(Tile start, List<Tile> ends) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Move readMove() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Type getPawnPromotion() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setListener(Listener listener) {
		// TODO Auto-generated method stub
		
	}

}
