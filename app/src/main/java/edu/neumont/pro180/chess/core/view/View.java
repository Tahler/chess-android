package edu.neumont.pro180.chess.core.view;

import java.text.ParseException;

import edu.neumont.pro180.chess.core.model.Move;
import edu.neumont.pro180.chess.core.model.Piece;

public interface View {
    Move readMove() throws ParseException;
    Piece.Type getPawnPromotion();

}
