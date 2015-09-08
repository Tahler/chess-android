package edu.neumont.pro180.chess.core.view;

import java.util.List;

import edu.neumont.pro180.chess.core.model.Board;
import edu.neumont.pro180.chess.core.model.Color;
import edu.neumont.pro180.chess.core.model.Move;
import edu.neumont.pro180.chess.core.model.Piece;
import edu.neumont.pro180.chess.core.model.Tile;

public interface View {

    public void displayBoard(Board board);

    void notifyLightIsInCheck();

    void notifyDarkIsInCheck();

    /**
     * @param result The color of the winner, or null if the game resulted as a stalemate
     */
    public void notifyGameOver(Color result);

    public void highlightTiles(Tile start, List<Tile> ends);

    public Piece.Type getPawnPromotion();

    public void setListener(Listener listener);

    public static interface Listener {
    	void changeView(View v);
        void tileSelected(Tile tile);
        void moveSelected(Move move);
    }
}
