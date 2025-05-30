package chess;

import java.util.*;
import static chess.ChessGame.TeamColor.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;
    private final ArrayList<ChessMove> gameMoveHistory;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
        gameMoveHistory = null;
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type, ArrayList<ChessMove> gameMoveHistory) {
        this.type = type;
        this.pieceColor = pieceColor;
        this.gameMoveHistory = gameMoveHistory;
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

    public ArrayList<ChessMove> getGameMoveHistory() {
        return gameMoveHistory;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     * Also does not consider en passant
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return new ChessMoveCalculator(board, this, myPosition, gameMoveHistory).calculateMoves();
    }

    //AI assisted
    public ChessPiece copy() {
        return new ChessPiece(pieceColor, type, gameMoveHistory);
    }

    @Override
    public String toString() {
        String pieceString = switch (type) {
            case KING -> "K";
            case QUEEN -> "Q";
            case PAWN -> "P";
            case ROOK -> "R";
            case BISHOP -> "B";
            case KNIGHT -> "N";
        };
        return (pieceColor == WHITE) ? pieceString : pieceString.toLowerCase();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        var piece = (ChessPiece) obj;
        return (pieceColor == piece.pieceColor) && (type == piece.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
