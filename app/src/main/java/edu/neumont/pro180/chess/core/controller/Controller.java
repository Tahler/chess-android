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

    // Control flow
    public void play() {
        do {
            Move move = view.readMove();
            // If the move is valid
            if (validator.getAllValidMoves(move.getStart()).contains(move)) {
                board.makeMove(move);
            } else {
                continue; // retry until a valid move is made
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

            view.displayBoard(board.getPieces());
        } while (!validator.isOver());
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
//                if (validator.isInCheck() && !validator.isOver()) view.notifyIsInCheck();
//            } catch (IllegalMoveException e) {
////                view.print(e.getMessage());
//            }
//        } while (!validator.isOver());
//
//        Color result = validator.getResult();
////        view.print((result == null) ? "Stalemate!" : "Checkmate! The winner is " + result + "!");
//    }

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
        if (validator.getAllValidMoves(move.getStart()).contains(move)) {
            board.makeMove(move);
        } else {
            System.out.println();
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

        // Send the new Piece[][] to the view
        view.displayBoard(board.getPieces());

//        if (validator.isOver()) // End the game
    }
}
