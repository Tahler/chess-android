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
		for(View v : views){
			v.displayBoard(pieces);
		}
	}
	@Override
	public void notifyCheck() {
		for(View v : views){
			v.notifyCheck();
		}
	}
	@Override
	public void notifyGameOver(Color result) {
		for(View v : views){
			v.notifyGameOver(result);
		}
	}
	@Override
	public void highlightTiles(Tile start, List<Tile> ends) {
		for(View v : views){
			v.highlightTiles(start, ends);
		}
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
		for(View v : views){
			v.setListener(listener);
		}
	}

}
