package edu.neumont.pro180.chess.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.neumont.pro180.chess.core.model.Piece.Type;

public abstract class AbstractBoard implements Serializable{
	private static final long serialVersionUID = 1L;
	private Piece[][] pieces;
    // Pieces captured by light - the pieces themselves are dark
    private List<Piece> lightCapturedPieces;
    // Pieces captured by dark - the pieces themselves are light
    private List<Piece> darkCapturedPieces;

    public Tile lightKingLocation;
    public Tile darkKingLocation;

    public AbstractBoard() {
        // Initialize tiles
        pieces = new Piece[8][8];

        // Initialize pieces
        for (int i = 0; i < pieces.length; i++) {
            pieces[1][i] = new Piece(Type.PAWN, Color.DARK);
            pieces[6][i] = new Piece(Type.PAWN, Color.LIGHT);
        }

        pieces[0][0] = pieces[0][7] = new Piece(Type.ROOK, Color.DARK);
        pieces[0][1] = pieces[0][6] = new Piece(Type.KNIGHT, Color.DARK);
        pieces[0][2] = pieces[0][5] = new Piece(Type.BISHOP, Color.DARK);
        pieces[0][3] = new Piece(Type.QUEEN, Color.DARK);
        pieces[0][4] = new Piece(Type.KING, Color.DARK);
        darkKingLocation = new Tile(4, 0);

        pieces[7][0] = pieces[7][7] = new Piece(Type.ROOK, Color.LIGHT);
        pieces[7][1] = pieces[7][6] = new Piece(Type.KNIGHT, Color.LIGHT);
        pieces[7][2] = pieces[7][5] = new Piece(Type.BISHOP, Color.LIGHT);
        pieces[7][3] = new Piece(Type.QUEEN, Color.LIGHT);
        pieces[7][4] = new Piece(Type.KING, Color.LIGHT);
        lightKingLocation = new Tile(4, 7);

        lightCapturedPieces = new ArrayList<>();
        darkCapturedPieces = new ArrayList<>();
    }

    /**
     * Make a copy of another board's properties
     * @param otherBoard The board to be copied.
     */
    public AbstractBoard(AbstractBoard otherBoard) {
        Piece[][] otherPieces = otherBoard.getPieces();
        this.pieces = new Piece[8][8];
        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < pieces[i].length; j++) {
                pieces[i][j] = otherPieces[i][j];
            }
        }

        lightKingLocation = otherBoard.lightKingLocation;
        darkKingLocation = otherBoard.darkKingLocation;
        
        lightCapturedPieces = new ArrayList<>();
        darkCapturedPieces = new ArrayList<>();
        lightCapturedPieces.addAll(otherBoard.lightCapturedPieces);
        darkCapturedPieces.addAll(otherBoard.darkCapturedPieces);
    }

    public Piece getPieceAt(Tile location) {
        return getPieceAt(location.x, location.y);
    }
    public Piece getPieceAt(Integer x, Integer y) {
        if (x >= 0 && x < 8 && y >= 0 && y < 8) return pieces[y][x];
        else throw new IllegalArgumentException("The tile (" + x + ", " + y + ") is not on the board!");
    }

    public void executeMove(Move move) {
        Piece mover = getPieceAt(move.getStart());
        move.setMover(mover); // I don't like this. but sets the mover for the move (helpful with the move's toString())
        if (mover.getType().equals(Piece.Type.KING)) {
            if (mover.getColor().equals(Color.LIGHT)) {
                lightKingLocation = move.getEnd();
            } else {
                darkKingLocation = move.getEnd();
            }
        }

        int x1 = move.getStart().x;
        int y1 = move.getStart().y;
        int x2 = move.getEnd().x;
        int y2 = move.getEnd().y;

        Piece captured = pieces[y2][x2];
        if(captured != null) {
            if (captured.getColor().equals(Color.LIGHT)) darkCapturedPieces.add(captured);
            else lightCapturedPieces.add(captured);
        }

        pieces[y2][x2] = pieces[y1][x1];
        pieces[y1][x1] = null;

        // Castling
        Move castle = null;
        // Dark short castle
        if (y1 == 0 && x1 == 4 && x2 == 6) castle = new Move(7, 0, 5, 0);
        // Dark long castle
        else if (y1 == 0 && x1 == 4 && x2 == 2) castle = new Move(0, 0, 3, 0);
        // Light short castle
        else if (y1 == 7 && x1 == 4 && x2 == 6) castle = new Move(7, 7, 5, 7);
        // Light long castle
        else if (y1 == 7 && x1 == 4 && x2 == 2) castle = new Move(0, 7, 3, 7);

        if (castle != null) {
            executeMove(castle); // recurse, moving the rook now
        }
    }

    public Piece[][] getPieces() {
        return pieces;
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

    /**
     * Returns a string representation of the board, complete with piece representations as well on it.
     *
     * Example:
     *
     *     a   b   c   d   e   f   g   h
     *   ---------------------------------
     * 8 | - | k | r | - | - | - | - | - |
     *   ---------------------------------
     * 7 | - | - | - | - | - | - | - | - |
     *   ---------------------------------
     * 6 | - | - | - | - | - | - | - | - |
     *   ---------------------------------
     * 5 | - | - | - | - | - | - | - | - |
     *   ---------------------------------
     * 4 | - | - | - | - | - | - | - | - |
     *   ---------------------------------
     * 3 | - | - | - | - | - | - | - | - |
     *   ---------------------------------
     * 2 | - | - | - | - | - | - | - | - |
     *   ---------------------------------
     * 1 | - | - | - | - | - | - | - | - |
     *   ---------------------------------
     *
     * @return The string representation of the board, including new lines
     */
    public String toString() {
        String board = "" +
                "    a   b   c   d   e   f   g   h  " + "\n" +
                "  ---------------------------------" + "\n";

        for (int i = 0; i < pieces.length; i++) {
            board += (8 - i) + " "; // "8 "
            for (int j = 0; j < pieces[0].length; j++) {
                Piece p = pieces[i][j];
                board += "| " + ((p == null) ? "-" : p.toCharTeam()) + " ";
            }
            board += "|" + "\n";
            board += "  ---------------------------------" + "\n";
        }

//        return board;
        return board.substring(0, board.length() - 1); // TODO: this only removes the \n
    }
}
