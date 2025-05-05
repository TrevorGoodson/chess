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
     * Also does not consider en passant
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
        if (type == PieceType.PAWN) {
            return pawnMoves(board, myPosition);
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

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<ChessMove>();
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        boolean colorIsWhite = (pieceColor == ChessGame.TeamColor.WHITE);
        int direction = colorIsWhite ? 1 : -1;

        //checks for the normal/double move
        var normalMove = new ChessPosition(row + direction, column);
        if (board.checkRange(normalMove) && board.getPiece(normalMove) == null) {
            validMoves.add(new ChessMove(myPosition, normalMove, null));

            if (colorIsWhite && row == 2 || (!colorIsWhite && row == 7)) {
                var doubleMove = new ChessPosition(row + (2*direction), column);
                if (board.getPiece(doubleMove) == null) {
                    validMoves.add(new ChessMove(myPosition, doubleMove, null));
                }
            }
        }

        //checks for attacks
        int[][] attackDirections = new int[][]{{direction,1}, {direction,-1}};
        for (var attackDirection : attackDirections) {
            var attackCheck = new ChessPosition(row + attackDirection[0], column + attackDirection[1]);
            if (board.checkRange(attackCheck) && board.getPiece(attackCheck) != null &&
                    pieceColor != board.getPieceColor(attackCheck)) {
                validMoves.add(new ChessMove(myPosition, attackCheck, null));
            }
        }

        //checks for promotion
        if (!validMoves.isEmpty()) {
            int firstMoveRow = validMoves.iterator().next().getEndPosition().getRow();
            if ((colorIsWhite && firstMoveRow == 8) ||
                    (!colorIsWhite && firstMoveRow == 1)) {
                for (ChessMove move : new ArrayList<>(validMoves)) {
                    move.setPromotionPiece(PieceType.KNIGHT);
                    ChessPosition endPosition = move.getEndPosition();
                    validMoves.add(new ChessMove(myPosition, endPosition, PieceType.BISHOP));
                    validMoves.add(new ChessMove(myPosition, endPosition, PieceType.ROOK));
                    validMoves.add(new ChessMove(myPosition, endPosition, PieceType.QUEEN));
                }
            }
        }

        return validMoves;
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
                if (checkMove(board, nextPosition)) {
                    validMoves.add(new ChessMove(myPosition, nextPosition, null));
                }
                if (board.checkRange(nextPosition) && board.getPiece(nextPosition) != null) {
                    break;
                }
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
            if (checkMove(board, nextPosition)) {
                validMoves.add(new ChessMove(myPosition, nextPosition, null));
            }
        }
        return validMoves;
    }

    private boolean checkMove(ChessBoard board, ChessPosition move) {
        if (!board.checkRange(move)) {
            return false;
        }
        return (board.getPiece(move) == null || pieceColor != board.getPieceColor(move));
    }
}
