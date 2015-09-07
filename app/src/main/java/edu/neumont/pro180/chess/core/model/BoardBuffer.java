package edu.neumont.pro180.chess.core.model;

/**
 * Used only by the MoveValidator to test if a move would put the king in check
 */
public class BoardBuffer extends AbstractBoard {
	private static final long serialVersionUID = 1L;

	public BoardBuffer(AbstractBoard board) {
        super(board);
    }
}
