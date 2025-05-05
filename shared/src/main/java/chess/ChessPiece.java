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
        if (type == PieceType.ROOK) {
            return rookMoves(board, myPosition);
        }
        if (type == PieceType.QUEEN) {
            return queenMoves(board, myPosition);
        }
        if (type == PieceType.KING) {
            return kingMoves(board, myPosition);
        }
        if (type == PieceType.KNIGHT) {
            return knightMoves(board, myPosition);
        }
        throw new RuntimeException("Unknown piece type");
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        return longRangeMove(board, myPosition, new int[][]{{1,1}, {1,-1}, {-1,1}, {-1,-1}});
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        return longRangeMove(board, myPosition, new int[][]{{0,1}, {0,-1}, {-1,0}, {1,0}});
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        return longRangeMove(board, myPosition,
                             new int[][]{{0,1}, {0,-1}, {-1,0}, {1,0}, {1,1}, {1,-1}, {-1,1}, {-1,-1}});
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        return shortRangeMove(board, myPosition,
                              new int[][]{{0,1}, {0,-1}, {-1,0}, {1,0}, {1,1}, {1,-1}, {-1,1}, {-1,-1}});
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        return shortRangeMove(board, myPosition,
                new int[][]{{2,1}, {1,2}, {-1,2}, {2,-1}, {1,-2}, {-2,1}, {-1,-2}, {-2,-1}});
    }

    private Collection<ChessMove> longRangeMove(ChessBoard board, ChessPosition myPosition, int[][] directions) {
        Collection<ChessMove> validMoves = new ArrayList<ChessMove>();
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        for (int[] direction : directions) {
            int numSteps = 0;
            ChessPosition nextPosition = null;
            do {
                ++numSteps;
                nextPosition = new ChessPosition(row + (numSteps * direction[0]),
                                                  column + (numSteps * direction[1]));
                if (!board.checkRange(nextPosition)) {
                    continue;
                }
                if (board.getPiece(nextPosition) == null) {
                    validMoves.add(new ChessMove(myPosition, nextPosition, null));
                    continue;
                }
                if (pieceColor != board.getPieceColor(nextPosition)) {
                    validMoves.add(new ChessMove(myPosition, nextPosition, null));
                }
                break;
            } while (board.checkRange(nextPosition));
        }
        return validMoves;
    }

    private Collection<ChessMove> shortRangeMove(ChessBoard board, ChessPosition myPosition, int[][] directions) {
        Collection<ChessMove> validMoves = new ArrayList<ChessMove>();
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        for (int[] direction : directions) {
            ChessPosition nextPosition = new ChessPosition(row + direction[0], column + direction[1]);
            if (!board.checkRange(nextPosition)) {
                continue;
            }
            if (board.getPiece(nextPosition) == null || pieceColor != board.getPieceColor(nextPosition)) {
                validMoves.add(new ChessMove(myPosition, nextPosition, null));
            }
        }
        return validMoves;
    }
}
