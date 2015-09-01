package edu.neumont.pro180.chess.core.view;

import java.util.List;

import edu.neumont.pro180.chess.core.model.Move;
import edu.neumont.pro180.chess.core.model.Piece;
import edu.neumont.pro180.chess.core.model.Tile;

public interface View {
    void displayBoard(Piece[][] pieces);
    void notifyIsInCheck();
    void highlightTiles(Tile start, List<Tile> ends);
    Move readMove();
    Piece.Type getPawnPromotion();

    void setListener(Listener listener);
    interface Listener {
        void tileSelected(Tile tile);
        void moveSelected(Move move);
    }
}
