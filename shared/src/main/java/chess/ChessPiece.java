package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (type == PieceType.BISHOP) {
            return bishopMoves(board, myPosition);
        }
        return new ArrayList<>(); //note: just an empty list! (fix later)
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new HashSet<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        for (int i = 1; i < 8; ++i) {
            var upRight = new ChessPosition(currentRow + i, currentCol + i);
            var upLeft = new ChessPosition(currentRow + i, currentCol - i);
            var downRight = new ChessPosition(currentRow - i, currentCol + i);
            var downLeft = new ChessPosition(currentRow - i, currentCol - i);
            if (board.checkRange(upRight) && board.getPiece(upRight) == null) {
                validMoves.add(new ChessMove(myPosition, upRight, type));
            }
            if (board.checkRange(upLeft) && board.getPiece(upLeft) == null) {
                validMoves.add(new ChessMove(myPosition, upLeft, type));
            }
            if (board.checkRange(downRight) && board.getPiece(downRight) == null) {
                validMoves.add(new ChessMove(myPosition, downRight, type));
            }
            if (board.checkRange(downLeft) && board.getPiece(downLeft) == null) {
                validMoves.add(new ChessMove(myPosition, downLeft, type));
            }
        }
        return validMoves;
    }
}
