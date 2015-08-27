package edu.neumont.pro180.chess.core.model;

public class Piece {

    public enum Type {
        PAWN,
        ROOK,
        KNIGHT,
        BISHOP,
        QUEEN,
        KING
    }

    private final Color color;
    private Type type;
    private Boolean hasMoved;

    public Piece(Type type, Color color) {
        this.type = type;
        this.color = color;
        this.hasMoved = false;
    }

    public Color getColor() {
        return color;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Boolean hasMoved() {
        return hasMoved;
    }

    public void move() {
        this.hasMoved = true;
    }
    /**
     * @return A string representation of this piece, not acknowledging color.
     */
    public String toString() {
        switch (type) {
            case PAWN:
                return "PAWN";
            case ROOK:
                return "ROOK";
            case KNIGHT:
                return "KNIGHT";
            case BISHOP:
                return "BISHOP";
            case QUEEN:
                return "QUEEN";
            case KING:
                return "KING";
            default:
                return "NONE";
        }
    }

    /**
     * Similar to toString(), but a single character
     * @return The character representation of this piece, not acknowledging color.
     */
    public Character toChar() {
        switch (type) {
            case PAWN:
                return 'P';
            case ROOK:
                return 'R';
            case KNIGHT:
                return 'N';
            case BISHOP:
                return 'B';
            case QUEEN:
                return 'Q';
            case KING:
                return 'K';
            default:
                return '-';
        }
    }

    /**
     * Precedes the toString() with the respective "LIGHT" or "DARK"
     * @return The string representation of this piece, acknowledging color.
     */
    public String toStringTeam() {
        switch (color) {
            case LIGHT:
                return "LIGHT " + toString();
            case DARK:
                return "DARK " + toString();
            default:
                return "COLORLESS"; // Should never be returned, piece's color cannot be null
        }
    }

    /**
     * A light piece will be lowercase.
     * A dark piece will be uppercase.
     * @return The character representation of this piece, acknowledging color.
     */
    public Character toCharTeam() {
        switch (color) {
            case LIGHT:
                return Character.toLowerCase(toChar());
            case DARK:
                return Character.toUpperCase(toChar());
            default:
                return '-'; // Should never be returned, piece's color cannot be null
        }
    }
}
