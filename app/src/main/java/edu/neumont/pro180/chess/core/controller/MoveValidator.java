package edu.neumont.pro180.chess.core.controller;

import java.util.ArrayList;
import java.util.List;

import edu.neumont.pro180.chess.core.model.AbstractBoard;
import edu.neumont.pro180.chess.core.model.Board;
import edu.neumont.pro180.chess.core.model.BoardBuffer;
import edu.neumont.pro180.chess.core.model.Color;
import edu.neumont.pro180.chess.core.model.Move;
import edu.neumont.pro180.chess.core.model.Piece;
import edu.neumont.pro180.chess.core.model.Tile;
import edu.neumont.pro180.chess.exception.IllegalMoveException;

public class MoveValidator {
    private AbstractBoard board;

    public MoveValidator(Board board) {
        this.board = board;
    }

    /**
     * Succeeds if valid, otherwise throws an exception
     * @param move The move to be validated
     * @throws IllegalMoveException Thrown if the move is invalid
     */
    public void validate(Move move) throws IllegalMoveException {
        if (!getAllValidMoves(move.getStart()).contains(move)) {
            throw new IllegalMoveException("The " + board.getPieceAt(move.getStart()).toStringTeam() + " cannot move to " + move.getEnd() + "!");
        }
    }

    /**
     * Merges moves and attacks, then removes any of them that would leave the king in check.
     * @param p The tile location of the potential mover
     * @return A list of legal moves for the piece in that tile.
     */
    public List<Move> getAllValidMoves(Tile p) {
        List<Move> moves = new ArrayList<>();
        Piece piece = board.getPieceAt(p);
        if (piece == null) return moves; // If there's no piece, there are no valid moves.
        if (!piece.getColor().equals(((Board) board).getCurrentTurnColor())) return moves; // If it is not that piece's team's turn, there are no valid moves.

        moves = merge(getValidMoves(p), getValidAttacks(p));
        // Filter by checking if the king would end up in check
        for (int i = 0; i < moves.size(); i++) {
            if (wouldPlaceKingInCheck(moves.get(i))) {
                moves.remove(i);
                i--;
            }
        }

        return moves;
    }

    /**
     * Delegates to the getXMoves/Attacks methods.
     * Filters those moves.
     */
    private List<Move> getValidMoves(Tile p) {
        List<Move> moves = new ArrayList<>();
        Piece mover = board.getPieceAt(p.x, p.y);

        if (mover == null) return moves;
        // Retrieve the possible moves based on the piece's type
        switch (mover.getType()) {
            case PAWN:
                moves = getPawnMoves(p);
                break;
            case ROOK:
                moves = getRookMoves(p);
                break;
            case KNIGHT:
                moves = getKnightMoves(p);
                break;
            case BISHOP:
                moves = getBishopMoves(p);
                break;
            case QUEEN:
                moves = getQueenMoves(p);
                break;
            case KING:
                moves = getKingMoves(p);
                break;
        }

        // Filter the moves
        for (int i = 0; i < moves.size(); i++) {
            Move move = moves.get(i);
            // Remove from the possible moves if attempting to move on top of its own color
            Piece captured = board.getPieceAt(move.getEnd().x, move.getEnd().y);
            if (captured != null && captured.getColor().equals(mover.getColor())) {
                moves.remove(i);
                i--;
            }
        }

        return moves;
    }
    private List<Move> getValidAttacks(Tile p) {
        List<Move> attacks = new ArrayList<>();

        Piece mover = board.getPieceAt(p.x, p.y);
        if (mover == null) return attacks; // If there is no piece at the tile, there are no attacks to be made from there

        // Retrieve the possible attacks based on the piece's type's move shapes
        switch (mover.getType()) {
            case PAWN:
                attacks = getPawnAttacks(p);
                break;
            case ROOK:
                attacks = getRookAttacks(p);
                break;
            case KNIGHT:
                attacks = getKnightAttacks(p);
                break;
            case BISHOP:
                attacks = getBishopAttacks(p);
                break;
            case QUEEN:
                attacks = getQueenAttacks(p);
                break;
            case KING:
                attacks = getKingAttacks(p);
                break;
        }

        // Filter the attacks
        for (int i = 0; i < attacks.size(); i++) {
            Move attack = attacks.get(i);
            Piece attacker = board.getPieceAt(attack.getStart());
            Piece attacked = board.getPieceAt(attack.getEnd());

            // En passant checking
            Move lastMove = board.getLastMove();
            Tile enemyPawnLocation = lastMove.getEnd();
            if (attacker.getColor().equals(Color.LIGHT)) {
                // allowed through if dark just moved a pawn two spaces forward
                if (lastMove.getMover().getType().equals(Piece.Type.PAWN)
                        && lastMove.getStart().y == 1 && enemyPawnLocation.y == 3) {
                    if ((p.x == enemyPawnLocation.x + 1 || p.x == enemyPawnLocation.x - 1) // left or right
                            && p.y == enemyPawnLocation.y) {
                        continue;
                    }
                }
            } else {
                // allowed through if light just moved a pawn two spaces forward
                if (lastMove.getMover().getType().equals(Piece.Type.PAWN)
                        && lastMove.getStart().y == 6 && enemyPawnLocation.y == 4) {
                    if ((p.x == enemyPawnLocation.x + 1 || p.x == enemyPawnLocation.x - 1) // left or right
                            && p.y == enemyPawnLocation.y) {
                        continue;
                    }
                }
            }

            if (attacked == null || attacked.getColor().equals(mover.getColor())) { // if no piece would be taken of if the attacked piece is of the same color, remove
                attacks.remove(i);
                i--;
            }
        }

        return attacks;
    }

    private List<Move> getPawnMoves(Tile p) {
        List<Move> moves = new ArrayList<>();

        // Light pawns move upward
        if (board.getPieceAt(p).getColor().equals(Color.LIGHT)) {
            Piece other = board.getPieceAt(p.x, p.y - 1);
            if (other == null) { // Pawn can move forward one space as long as no piece is blocking
                moves.add(new Move(p.x, p.y, p.x, p.y - 1));
                if (p.y == 6) { // If the pawn is in it's starting place
                    other = board.getPieceAt(p.x, p.y - 2);
                    if (other == null) {
                        moves.add(new Move(p.x, p.y, p.x, p.y - 2)); // Pawn can move forward two spots
                    }
                }
            }
        } else {
            Piece other = board.getPieceAt(p.x, p.y + 1);
            if (other == null) { // Pawn can move forward one space as long as no piece is blocking
                moves.add(new Move(p.x, p.y, p.x, p.y + 1));
                if (p.y == 1) { // If the pawn is in it's starting place
                    other = board.getPieceAt(p.x, p.y + 2);
                    if (other == null) {
                        moves.add(new Move(p.x, p.y, p.x, p.y + 2)); // Pawn can move forward two spots
                    }
                }
            }
        }

        return moves;
    }
    private List<Move> getPawnAttacks(Tile p) {
        List<Move> moves = new ArrayList<>();

        // Light moves northbound
        if (board.getPieceAt(p.x, p.y).getColor().equals(Color.LIGHT)) {
            if (p.y - 1 >= 0) {
                if (p.x + 1 <= 7) moves.add(new Move(p.x, p.y, p.x + 1, p.y - 1));
                if (p.x - 1 >= 0) moves.add(new Move(p.x, p.y, p.x - 1, p.y - 1));
            }
        // Dark moves southbound
        } else {
            if (p.y + 1 <= 7) {
                if (p.x + 1 <= 7) moves.add(new Move(p.x, p.y, p.x + 1, p.y + 1));
                if (p.x - 1 >= 0) moves.add(new Move(p.x, p.y, p.x - 1, p.y + 1));
            }
        }

        return moves;
    }
    private List<Move> getRookMoves(Tile p) {
        List<Move> moves = new ArrayList<>();

        int i;

        // Up
        i = 1;
        while (p.y - i >= 0) {
            moves.add(new Move(p.x, p.y, p.x, p.y - i));
            if (board.getPieceAt(p.x, p.y - i) != null) break;
            i++;
        }

        // Down
        i = 1;
        while (p.y + i <= 7) {
            moves.add(new Move(p.x, p.y, p.x, p.y + i));
            if (board.getPieceAt(p.x, p.y + i) != null) break;
            i++;
        }

        // Left
        i = 1;
        while (p.x - i >= 0) {
            moves.add(new Move(p.x, p.y, p.x - i, p.y));
            if (board.getPieceAt(p.x - i, p.y) != null) break;
            i++;
        }

        // Right
        i = 1;
        while (p.x + i <= 7) {
            moves.add(new Move(p.x, p.y, p.x + i, p.y));
            if (board.getPieceAt(p.x + i, p.y) != null) break;
            i++;
        }

        return moves;
    }
    private List<Move> getRookAttacks(Tile p) {
        return getRookMoves(p);
    }
    private List<Move> getKnightMoves(Tile p) {
        List<Move> moves = new ArrayList<>();

        if (p.x + 1 <= 7) {
            if (p.y + 2 <= 7) moves.add(new Move(p.x, p.y, p.x+1, p.y+2));
            if (p.y - 2 >= 0) moves.add(new Move(p.x, p.y, p.x+1, p.y-2));
            if (p.x + 2 <= 7) {
                if (p.y + 1 <= 7) moves.add(new Move(p.x, p.y, p.x + 2, p.y + 1));
                if (p.y - 1 >= 0) moves.add(new Move(p.x, p.y, p.x + 2, p.y - 1));
            }
        }
        if (p.x - 1 >= 0) {
            if (p.y + 2 <= 7) moves.add(new Move(p.x, p.y, p.x - 1, p.y + 2));
            if (p.y - 2 >= 0) moves.add(new Move(p.x, p.y, p.x - 1, p.y - 2));
            if (p.x - 2 >= 0) {
                if (p.y + 1 <= 7) moves.add(new Move(p.x, p.y, p.x - 2, p.y + 1));
                if (p.y - 1 >= 0) moves.add(new Move(p.x, p.y, p.x - 2, p.y - 1));
            }
        }

        return moves;
    }
    private List<Move> getKnightAttacks(Tile p) {
        return getKnightMoves(p);
    }
    private List<Move> getBishopMoves(Tile p) {
        List<Move> moves = new ArrayList<>();

        int i;

        // Northwest
        i = 1;
        while (p.x - i >= 0 && p.y + i <= 7) {
            moves.add(new Move(p.x, p.y, p.x - i, p.y + i));
            if (board.getPieceAt(p.x - i, p.y + i) != null) break;
            i++;
        }

        // Northeast
        i = 1;
        while (p.x + i <= 7 && p.y + i <= 7) {
            moves.add(new Move(p.x, p.y, p.x + i, p.y + i));
            if (board.getPieceAt(p.x + i, p.y + i) != null) break;
            i++;
        }

        // Southwest
        i = 1;
        while (p.x - i >= 0 && p.y - i >= 0) {
            moves.add(new Move(p.x, p.y, p.x - i, p.y - i));
            if (board.getPieceAt(p.x - i, p.y - i) != null) break;
            i++;
        }

        // Southeast
        i = 1;
        while (p.x + i <= 7 && p.y - i >= 0) {
            moves.add(new Move(p.x, p.y, p.x + i, p.y - i));
            if (board.getPieceAt(p.x + i, p.y - i) != null) break;
            i++;
        }

        return moves;
    }
    private List<Move> getBishopAttacks(Tile p) {
        return getBishopMoves(p);
    }
    private List<Move> getQueenMoves(Tile p) {
        return merge(getRookMoves(p), getBishopMoves(p));
    }
    private List<Move> getQueenAttacks(Tile p) {
        return getQueenMoves(p);
    }
    private List<Move> getKingMoves(Tile p) {
        return merge(getKingBasicMoves(p), getKingCastleMoves(p));
    }
    private List<Move> getKingBasicMoves(Tile p) {
        List<Move> moves = new ArrayList<>();

        if (p.x + 1 <= 7) {
            moves.add(new Move(p.x, p.y, p.x + 1, p.y));
            if (p.y + 1 <= 7) moves.add(new Move(p.x, p.y, p.x + 1, p.y + 1));
            if (p.y - 1 >= 0) moves.add(new Move(p.x, p.y, p.x + 1, p.y - 1));
        }
        if (p.x - 1 >= 0) {
            moves.add(new Move(p.x, p.y, p.x - 1, p.y));
            if (p.y + 1 <= 7) moves.add(new Move(p.x, p.y, p.x - 1, p.y + 1));
            if (p.y - 1 >= 0) moves.add(new Move(p.x, p.y, p.x - 1, p.y - 1));
        }
        if (p.y + 1 <= 7) moves.add(new Move(p.x, p.y, p.x, p.y + 1));
        if (p.y - 1 >= 0) moves.add(new Move(p.x, p.y, p.x, p.y - 1));

        return moves;
    }
    private List<Move>
    getKingCastleMoves(Tile p) {
        List<Move> moves = new ArrayList<>();
        Piece king = board.getPieceAt(p);
        if (king.hasMoved()) return moves; // If the king has moved, it cannot castle!

        Color kingColor = board.getPieceAt(p).getColor();

        // Castling moves
        Piece rook;
        if (kingColor.equals(Color.LIGHT)) {
            if (p.x == 4 && p.y == 7 && !isAttacked(4, 7)) {
                // Short
                rook = board.getPieceAt(7, 7);
                if (rook != null && !rook.hasMoved() &&
                        board.getPieceAt(5, 7) == null && !isAttacked(5, 7) &&
                        board.getPieceAt(6, 7) == null && !isAttacked(6, 7) &&
                        rook.getType().equals(Piece.Type.ROOK) && rook.getColor().equals(Color.LIGHT)) {
                    moves.add(new Move(4, 7, 6, 7));
                }
                // Long
                rook = board.getPieceAt(0, 7);
                if (rook != null && !rook.hasMoved() &&
                        board.getPieceAt(3, 7) == null && !isAttacked(3, 7) &&
                        board.getPieceAt(2, 7) == null && !isAttacked(2, 7) &&
                        board.getPieceAt(1, 7) == null &&
                        rook.getType().equals(Piece.Type.ROOK) && rook.getColor().equals(Color.LIGHT)) {
                    moves.add(new Move(4, 7, 2, 7));
                }
            }
        } else {
            if (p.x == 4 && p.y == 0 && !isAttacked(4, 0)) { // Can't castle out of check
                // Short
                rook = board.getPieceAt(7, 0);
                if (rook != null && !rook.hasMoved() &&
                        board.getPieceAt(5, 0) == null && !isAttacked(5, 0) && // Can't castle through check
                        board.getPieceAt(6, 0) == null && !isAttacked(6, 0) &&
                        rook.getType().equals(Piece.Type.ROOK) && rook.getColor().equals(Color.DARK)) {
                    moves.add(new Move(4, 0, 6, 0));
                }
                // Long
                rook = board.getPieceAt(0, 0);
                if (rook != null && !rook.hasMoved() &&
                        board.getPieceAt(3, 0) == null && !isAttacked(3, 0) && // Can't castle through check
                        board.getPieceAt(2, 0) == null && !isAttacked(2, 0) &&
                        board.getPieceAt(1, 0) == null &&
                        rook.getType().equals(Piece.Type.ROOK) && rook.getColor().equals(Color.DARK)) {
                    moves.add(new Move(4, 0, 2, 0));
                }
            }
        }

        return moves;
    }
    private List<Move> getKingAttacks(Tile p) {
        return getKingBasicMoves(p);
    }

    public Boolean wouldPlaceKingInCheck(Move potentialMove) {
        Piece mover = board.getPieceAt(potentialMove.getStart());
        Color color = mover.getColor();

        Tile kingLocation;
        if (mover.getType().equals(Piece.Type.KING)) kingLocation = potentialMove.getEnd(); // If moving the king, check the end location
        else kingLocation = (color.equals(Color.LIGHT)) ? board.lightKingLocation : board.darkKingLocation; // otherwise check the king's current location

        return wouldBeAttacked(potentialMove, kingLocation);
    }
    /**
     * Makes a new 'testing' board, makes the move, then checks if the specified tile is attacked on that board
     * @param potentialMove The move to be checked
     * @param potentiallyAttackedLocation The potentially attacked location to be checked
     * @return True if the location would be attacked following that move.
     */
    private Boolean wouldBeAttacked(Move potentialMove, Tile potentiallyAttackedLocation) {
        AbstractBoard realBoard = this.board; // Save the current state of the board

        this.board = new BoardBuffer(realBoard); // Switch to an 'imaginary' board

        // Make the move on the imaginary board, and determine if the move would end with the tile attacked
        board.executeMove(potentialMove);
        boolean isAttacked = isAttacked(potentiallyAttackedLocation);

        this.board = realBoard; // Switch back
        return isAttacked; // Return the result
    }

    /**
     * TODO: this might be easier if:
     * 1. Iterate through the 8 possible squares the king can move to.
     * 2. At each square, pretend the king is one of the other pieces and check if it attacks an enemy piece. For example, we can branch out along the diagonals to see if we come across an enemy bishop. If we do, it's not legal to move to that square.
     * 3. Collect the moves to the squares where the above check does not come across any enemy pieces.
     */
    public Boolean isAttacked(AbstractBoard board, Tile potentiallyAttackedLocation) {
        Piece attacked = board.getPieceAt(potentiallyAttackedLocation);
        if (attacked == null) return false;
        Color victimColor = attacked.getColor();
        Piece potentialAttacker;
        // If any possible next move on the board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                potentialAttacker = board.getPieceAt(i, j);
                // If there is a potential attacker of the opposite color, we must investigate
                if (potentialAttacker != null && !potentialAttacker.getColor().equals(victimColor)) {
                    List<Move> attacks = getValidAttacks(new Tile(i, j));
                    for (Move move : attacks) {
                        if (move.getEnd().equals(potentiallyAttackedLocation)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    public Boolean isAttacked(AbstractBoard board, Integer x, Integer y) {
        return isAttacked(board, new Tile(x, y));
    }
    public Boolean isAttacked(Tile location) {
        return this.isAttacked(board, location);
    }
    public Boolean isAttacked(Integer x, Integer y) {
        return isAttacked(new Tile(x, y));
    }

    public Boolean lightIsInCheck() {
        return isAttacked(board.lightKingLocation);
    }

    public Boolean darkIsInCheck() {
        return isAttacked(board.darkKingLocation);
    }

    public Boolean isInCheck() {
        return lightIsInCheck() || darkIsInCheck();
    }

    /**
     * True if there are no valid moves for the current player
     */
    public boolean isOver() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board.getPieceAt(i, j);
                if (piece != null && getAllValidMoves(new Tile(i, j)).size() != 0) return false;
            }
        }

        return true;
    }

    public Color getResult() {
        if (!isOver()) return null;

        if (isAttacked(board.darkKingLocation)) return Color.LIGHT;
        if (isAttacked(board.lightKingLocation)) return Color.DARK;

        return null; // Stalemate at this point
    }

    /**
     * A useful tool for merging two lists
     * @param a First list
     * @param b Second list
     * @return The merged list
     */
    private List<Move> merge(List<Move> a, List<Move> b) {
        List<Move> moves = new ArrayList<>();
        if (a != null) moves.addAll(a);
        if (b != null) moves.addAll(b);
        return moves;
    }
}
