package edu.neumont.pro180.chess.core.controller;

import android.app.Activity;
import android.os.Bundle;

import edu.neumont.pro180.chess.R;
import edu.neumont.pro180.chess.core.model.Board;
import edu.neumont.pro180.chess.core.view.View;

/**
 * The controller.
 * Instructs the interface to interact with the user and retrieve moves.
 * Validates moves before executing them on the board.
 */
public class ChessActivity extends Activity {
    private final Board board;
    private final MoveValidator validator;
    private View view;

    public ChessActivity() {
        this.board = new Board();
        this.validator = new MoveValidator(board);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess);
        view = (View) findViewById(R.id.chess_board_view);
    }

//    public void play() {
//        do {
//            Move move;
//            try {
//                move = view.readMove();
//                if (move == null) break;
//                validator.validate(move);
//                board.makeMove(move);
//
//                // Pawn promotion
//                boolean piecePromotion = false;
//                Piece mover = board.getPieceAt(move.getEnd()); // The piece has already moved, so it is in its ending spot
//                if (mover.getType().equals(Piece.Type.PAWN)) {
//                    if (mover.getColor().equals(Color.LIGHT)) {
//                        if (move.getEnd().y == 0) piecePromotion = true;
//                    } else {
//                        if (move.getEnd().y == 7) piecePromotion = true;
//                    }
//                }
//                if (piecePromotion) mover.setType(view.getPawnPromotion());
//
//                // Check checking
////                if (validator.isInCheck() && !validator.isOver()) view.print("Check!");
//            } catch (IllegalMoveException e) {
//                e.printStackTrace();
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        } while (!validator.isOver());
//
//        Color result = validator.getResult();
////        view.print((result == null) ? "Stalemate!" : "Checkmate! The winner is " + result + "!");
//    }
}
