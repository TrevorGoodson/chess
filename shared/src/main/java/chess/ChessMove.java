package chess;

import java.util.Objects;
import java.util.Scanner;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    public void setPromotionPiece(ChessPiece.PieceType piece) {
        promotionPiece = piece;
    }

    public String toString() {
        return "[" + startPosition.toString() + " -> " + endPosition.toString() + "]";
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        ChessMove otherMove = (ChessMove) object;

        return (startPosition.equals(otherMove.startPosition)) &&
               (endPosition.equals(otherMove.endPosition)) &&
               (promotionPiece == otherMove.promotionPiece);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }

    public ChessMove copy() {
        return new ChessMove(startPosition, endPosition, promotionPiece);
    }

    public static ChessMove parseMove(String move) {
        if (move.length() != 8) {
            return null;
        }
        if (!move.startsWith(" -> ", 2)) {
            return null;
        }
        ChessPosition startPosition = ChessPosition.parsePosition(move.substring(0,2));
        ChessPosition endPosition = ChessPosition.parsePosition(move.substring(6));
        if (startPosition == null || endPosition == null) {
            return null;
        }
        return new ChessMove(startPosition, endPosition);
    }

    public static void main(String[] args) {
        Scanner inputScanner = new Scanner(System.in);
        String move = inputScanner.nextLine();
        System.out.print(ChessMove.parseMove(move));
    }

}
