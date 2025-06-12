package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    public String toString() {
        return String.valueOf((char) ('a' + col - 1)) + row;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ChessPosition otherPosition = (ChessPosition) obj;
        return (row == otherPosition.row) && (col == otherPosition.col);
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    public ChessPosition copy() {
        return new ChessPosition(row, col);
    }

    public static ChessPosition parsePosition(String position) {
        if (position.length() != 2) {
            return null;
        }
        if (!Character.isAlphabetic(position.charAt(0)) || !Character.isDigit(position.charAt(1))) {
            return null;
        }
        int row = position.charAt(1) - '0';
        int col = position.toLowerCase().charAt(0) - 'a' + 1;
        return new ChessPosition(row, col);
    }
}
