package edu.neumont.pro180.chess.core.model;

public class Move {
    private final Tile start;
    private final Tile end;
    private Piece mover;

    public Move(Integer x1, Integer y1, Integer x2, Integer y2) {
        this(new Tile(x1, y1), new Tile(x2, y2));
    }

    public Move(Tile start, Tile end) {
        this.start = start;
        this.end = end;
    }

    public Tile getStart() {
        return start;
    }
    public Tile getEnd() {
        return end;
    }

    public void setMover(Piece mover) {
        this.mover = mover;
    }

    @Override
    public String toString() {
        if (mover != null) return "Moving the " + mover.toStringTeam() + " from " + start.toString() + " to " + end.toString();
        else return "Moving from " + start.toString() + " to " + end.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Move move = (Move) o;

        if (start != null ? !start.equals(move.start) : move.start != null) return false;
        return !(end != null ? !end.equals(move.end) : move.end != null);
    }

    @Override
    public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }

    public Piece getMover() {
        return mover;
    }

    //    @Override
//    public Iterator<Tile> iterator() {
//        Board board = Board.getInstance();
//        Collection<Tile> path = new ArrayList<>();
//
//        int x = start.getY();
//        int y = start.getX();
//
//        int endX = end.getY();
//        int endY = end.getX();
//
//        // 1 for further down, -1 for further back, 0 for static
//        int dirX = ((endX > x) ? 1 : (x == endX) ? 0 : -1);
//        int dirY = ((endY > y) ? 1 : (y == endY) ? 0 : -1);
//
//        Tile cursor = start;
//        while (cursor != end) {
//            // Add before: the path should not include the starting tile.
//            x += dirX;
//            y += dirY;
//
//            // Reassign the cursor tile
//            cursor = board.getTile(x, y);
//
//            // Finally, add the cursor tile
//            path.add(cursor);
//        }
//
//        return path.iterator();
//    }
}
