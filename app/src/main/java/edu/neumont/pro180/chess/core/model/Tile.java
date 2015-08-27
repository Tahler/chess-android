package edu.neumont.pro180.chess.core.model;

/**
 * Simply a wrapper for an x and y value on the chess board.
 */
public class Tile {
    public final Integer x; // Column
    public final Integer y; // Row

    public Tile(Integer x, Integer y) throws IndexOutOfBoundsException {
        if (x >= 0 && x < 8) this.x = x;
        else throw new IndexOutOfBoundsException("Cannot create a tile in that row!");

        if (y >= 0 && y < 8) this.y = y;
        else throw new IndexOutOfBoundsException("Cannot create a tile in that column!");
    }

    @Override
    public String toString() {
        return String.valueOf((char) (x + 97)).toUpperCase() + (8 - y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tile tile = (Tile) o;

        if (x != null ? !x.equals(tile.x) : tile.x != null) return false;
        return !(y != null ? !y.equals(tile.y) : tile.y != null);

    }

    @Override
    public int hashCode() {
        int result = x != null ? x.hashCode() : 0;
        result = 31 * result + (y != null ? y.hashCode() : 0);
        return result;
    }
}
