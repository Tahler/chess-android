package edu.neumont.pro180.chess.core.controller;

import edu.neumont.pro180.chess.core.model.Board;
import edu.neumont.pro180.chess.core.model.Color;
import edu.neumont.pro180.chess.core.model.Move;
import edu.neumont.pro180.chess.core.model.Piece;
import edu.neumont.pro180.chess.core.view.ConsoleIO;
import edu.neumont.pro180.chess.core.view.View;
import edu.neumont.pro180.chess.exception.IllegalMoveException;

import java.text.ParseException;

/**
 * The controller.
 * Instructs the interface to interact with the user and retrieve moves.
 * Validates moves before executing them on the board.
 */
public class Engine {
    private final Board board;
    private final MoveValidator validator;
    private final View view;

    public Engine() {
        this.board = new Board();
        this.validator = new MoveValidator(board);
        this.view = new ConsoleIO();
    }

    public void play() {
        view.print(board);
        do {
            Move move;
            try {
                move = view.readMove();
                if (move == null) break;
                validator.validate(move);
                board.makeMove(move);
                view.print(move.toString());

                // Pawn promotion
                boolean piecePromotion = false;
                Piece mover = board.getPieceAt(move.getEnd()); // The piece has already moved, so it is in its ending spot
                if (mover.getType().equals(Piece.Type.PAWN)) {
                    if (mover.getColor().equals(Color.LIGHT)) {
                        if (move.getEnd().y == 0) piecePromotion = true;
                    } else {
                        if (move.getEnd().y == 7) piecePromotion = true;
                    }
                }
                if (piecePromotion) mover.setType(view.getPawnPromotion());

                // Check checking
                if (validator.isInCheck() && !validator.isOver()) view.print("Check!");
                view.print(board);
            } catch (IllegalMoveException e) {
                view.print(e.getMessage());
            } catch (ParseException e) {
                view.print(e.getMessage());
            }
        } while (!validator.isOver());

        Color result = validator.getResult();
        view.print((result == null) ? "Stalemate!" : "Checkmate! The winner is " + result + "!");
    }
}
