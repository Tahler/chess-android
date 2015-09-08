package edu.neumont.pro180.chess.core.view;

import java.util.ArrayList;
import java.util.List;

import edu.neumont.pro180.chess.core.model.Board;
import edu.neumont.pro180.chess.core.model.Color;
import edu.neumont.pro180.chess.core.model.Move;
import edu.neumont.pro180.chess.core.model.Piece.Type;
import edu.neumont.pro180.chess.core.model.Tile;

public class multiView implements View{
	private final List<View> views;
	private int current;
	private View.Listener controller;
	private static enum CATCHTYPE { VIEW1, VIEW2, VIEWS };
	private class CatchViewToControl implements View.Listener{
		public final CATCHTYPE type;
		public CatchViewToControl(CATCHTYPE typ){
			type = typ;
		}
	    @Override
	    public void changeView(View v){//TODO: should we ignore this?
//	    	this.view = v;
//	        this.view.setListener(this);
//	        this.view.displayBoard(board);
	    }
		@Override
		public void tileSelected(Tile tile) {
			switch(type){
			case VIEW1:
				if(current==0){
					controller.tileSelected(tile);
				}
				break;
			case VIEW2:
				if(current==1){
					controller.tileSelected(tile);
				}
				break;
			case VIEWS:
				//views cannot change the board
			}
		}
		@Override
		public void moveSelected(Move move) {
			switch(type){
			case VIEW1:
				if(current==0){
					controller.moveSelected(move);
				}
				break;
			case VIEW2:
				if(current==1){
					controller.moveSelected(move);
				}
				break;
			case VIEWS:
				//views cannot change the board
			}
		}
	}
	private CatchViewToControl view1catcher;
	private CatchViewToControl view2catcher;
	private CatchViewToControl viewscatcher;
	
	public multiView(){
		view1catcher = new CatchViewToControl(CATCHTYPE.VIEW1);
		view2catcher = new CatchViewToControl(CATCHTYPE.VIEW2);
		viewscatcher = new CatchViewToControl(CATCHTYPE.VIEWS);
		this.views = new ArrayList<View>();
	}
	public multiView(View...views){
		this();
		for(View v : views){
			this.views.add(v);
		}
	}
	public void addView(View v){
		this.views.add(v);
	}
	
	@Override
	public void displayBoard(Board pieces) {
		if(pieces!=null){
			if(pieces.getCurrentTurnColor().equals(Color.LIGHT)){
				current = 0;
			}
			else{
				current = 1;
			}
			for(View v : views){
				v.displayBoard(pieces);
			}
		}
	}

	@Override
	public void notifyLightIsInCheck() {
		for(View v : views){
			v.notifyLightIsInCheck();
		}
	}

	@Override
	public void notifyDarkIsInCheck() {
		for(View v : views){
			v.notifyDarkIsInCheck();
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
		if(views.size()>current){
			views.get(current).highlightTiles(start, ends);
		}
	}
	@Override
	public Type getPawnPromotion() {
		if(views.size()>current){
			return views.get(current).getPawnPromotion();
		}
		return null;
	}
	@Override
	public void setListener(Listener listener) {
		controller = listener;
		for(int i=0; i<views.size(); i++){
			if(i==0){
				views.get(i).setListener(view1catcher);
			}
			else if(i==1){
				views.get(i).setListener(view2catcher);
			}
			else{
				views.get(i).setListener(viewscatcher);
			}
		}
	}

	@Override
	public void notifySpeechError() {
		views.get(current).notifySpeechError();
	}

	@Override
	public void notifyInvalidMove(Move move) {
		views.get(current).notifyInvalidMove(move);
	}

}
