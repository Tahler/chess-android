package edu.neumont.pro180.chess.core.view;

import java.util.List;

import edu.neumont.pro180.chess.core.model.Move;
import edu.neumont.pro180.chess.core.model.Piece;

public interface View {
    void notifyIsInCheck();
    void highlightMoves(List<Move> moves);
    Move readMove();
    Piece.Type getPawnPromotion();
}
