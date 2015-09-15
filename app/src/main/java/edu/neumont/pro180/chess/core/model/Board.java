package edu.neumont.pro180.chess.core.model;

public class Board extends AbstractBoard {
	private static final long serialVersionUID = 1L;
	// The color of the player whose turn it is
    private Color currentTurnColor;

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
        getMoveHistory().add(move);
    }

    private void switchTurn() {
        if (currentTurnColor.equals(Color.LIGHT)) {
            currentTurnColor = Color.DARK;
        } else {
            currentTurnColor = Color.LIGHT;
        }
    }
}
