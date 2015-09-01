package edu.neumont.pro180.chess.core.controller;

import java.util.ArrayList;
import java.util.List;

import edu.neumont.pro180.chess.core.model.Board;
import edu.neumont.pro180.chess.core.model.Color;
import edu.neumont.pro180.chess.core.model.Move;
import edu.neumont.pro180.chess.core.model.Piece;
import edu.neumont.pro180.chess.core.model.Tile;
import edu.neumont.pro180.chess.core.view.View;

/**
 * The controller.
 * Instructs the view interface to interact with the user and retrieve moves.
 * Validates moves before executing them on the board.
 */
public class Controller implements View.Listener {
    private final Board board;
    private final MoveValidator validator;
    private final View view;

    public Controller(View view) {
        this.board = new Board();
        this.validator = new MoveValidator(board);
        this.view = view;
        this.view.setListener(this);
        this.view.displayBoard(board.getPieces());
    }

    @Override
    public void tileSelected(Tile tile) {
        if (tile != null) {
            System.out.println(tile.x + ", " + tile.y);

            List<Move> validMovesAtTile = validator.getAllValidMoves(tile);

            // Send the move destinations to the view
            List<Tile> ends = new ArrayList<>();
            for (Move m : validMovesAtTile) ends.add(m.getEnd());
            view.highlightTiles(tile, ends);
        }
    }

    @Override
    public void moveSelected(Move move) {
        System.out.println("View sent a move");

        // If the move is valid
        // The move is already practically valid through the view. Consider removing this if statement
        if (validator.getAllValidMoves(move.getStart()).contains(move)) {
            board.makeMove(move);
        } else {
            System.err.println("Something very weird happened - the move was invalid");
            return; // retry until a valid move is made
        }

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

        if (validator.isInCheck()) view.notifyCheck();

        // Send the new Piece[][] to the view
        view.displayBoard(board.getPieces());

        // If this move has now ended the game, end the game.
        if (validator.isOver()) view.notifyGameOver(validator.getResult());
    }
}
