package edu.neumont.pro180.chess.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.neumont.pro180.chess.core.model.Piece.Type;

public class Board extends AbstractBoard implements Serializable{
	private static final long serialVersionUID = 1L;
	// The color of the player whose turn it is
    private Color currentTurnColor;
    private List<Move> moveHistory = new ArrayList<>();

    public Board() {
        super();
        currentTurnColor = Color.LIGHT;
    }

    public Color getCurrentTurnColor() {
        return currentTurnColor;
    }

    /**
     * Executes a move on the board and adds it to the history of moves.
     * It does not validate the move passed in.
     * @param move A pre-validated move
     */
    public void makeMove(Move move) {
        super.executeMove(move);
        move.getMover().move(); // Set its boolean hasMoved to true
        switchTurn();
        moveHistory.add(move);
    }
//    public void undoMove() { // TODO: presents problems with castling
//        Move lastMove = moveHistory.remove(moveHistory.size() - 1);   // remove from the history
//        Move move = new Move(lastMove.getEnd(), lastMove.getStart()); // the opposite of the undone move
//        executeMove(move);
//        switchTurn();
//    }
//    public Move getLastMove() {
//        return moveHistory.get(moveHistory.size() - 1);
//    }

    private void switchTurn() {
        if (currentTurnColor.equals(Color.LIGHT)) {
            currentTurnColor = Color.DARK;
        } else {
            currentTurnColor = Color.LIGHT;
        }
    }

    public List<Move> getMoveHistory() {
        return moveHistory;
    }
}
