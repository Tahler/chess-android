package edu.neumont.pro180.chess.core.model;

import java.util.ArrayList;
import java.util.List;

public class Board extends AbstractBoard {
	private static final long serialVersionUID = 1L;
	// The color of the player whose turn it is
    private Color currentTurnColor;
    // Pieces captured by light - the pieces themselves are dark
    private List<Piece> lightCapturedPieces;
    // Pieces captured by dark - the pieces themselves are light
    private List<Piece> darkCapturedPieces;

    public Board() {
        super();
        currentTurnColor = Color.LIGHT;
        lightCapturedPieces = new ArrayList<>();
        darkCapturedPieces = new ArrayList<>();
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
        Piece captured = getPieceAt(move.getCaptured());
        if(captured != null) {
            if (captured.getColor().equals(Color.LIGHT)) darkCapturedPieces.add(captured);
            else lightCapturedPieces.add(captured);
        }
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

    /**
     * @return The list of pieces captured by LIGHT. The pieces contained will be DARK.
     */
    public List<Piece> getLightCapturedPieces() {
        return lightCapturedPieces;
    }

    /**
     * @return The list of pieces captured by DARK. The pieces contained will be LIGHT.
     */
    public List<Piece> getDarkCapturedPieces() {
        return darkCapturedPieces;
    }
}
