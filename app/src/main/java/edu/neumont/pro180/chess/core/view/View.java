package edu.neumont.pro180.chess.core.view;

import java.util.List;

import edu.neumont.pro180.chess.core.model.Board;
import edu.neumont.pro180.chess.core.model.Color;
import edu.neumont.pro180.chess.core.model.Move;
import edu.neumont.pro180.chess.core.model.Piece;
import edu.neumont.pro180.chess.core.model.Tile;

public interface View {

    void displayBoard(Board board);

    void notifyCheck();

    /**
     * @param result The color of the winner, or null if the game resulted as a stalemate
     */
    void notifyGameOver(Color result);

    void highlightTiles(Tile start, List<Tile> ends);

    Piece.Type getPawnPromotion();

    void setListener(Listener listener);

    interface Listener {
        void tileSelected(Tile tile);
        void moveSelected(Move move);
    }
}
