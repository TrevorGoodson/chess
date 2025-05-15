package chess;

import java.util.*;
import static chess.ChessPiece.PieceType.*;
import static chess.ChessGame.TeamColor.*;

public class ChessMoveCalculator {
    private final ChessBoard board;
    private final ChessPosition startPosition;
    private final ChessPiece.PieceType type;
    private final ChessGame.TeamColor color;
    private final boolean hasMoved;

    public ChessMoveCalculator(ChessBoard board, ChessPiece piece, ChessPosition position) {
        this.board = board;
        this.startPosition = position;
        type = piece.getPieceType();
        color = piece.getTeamColor();
        hasMoved = piece.getHasMoved();
    }

    public Collection<ChessMove> calculateMoves() {
        return switch (type) {
            case KING -> kingMoves();
            case QUEEN -> queenMoves();
            case BISHOP -> bishopMoves();
            case ROOK -> rookMoves();
            case PAWN -> pawnMoves();
            case KNIGHT -> knightMoves();
            default -> throw new RuntimeException("Error: unknown piece type");
        };
    }

    private Collection<ChessMove> bishopMoves() {
        return longRangeMove(new int[][]{{1,1}, {1,-1}, {-1,1}, {-1,-1}});
    }

    private Collection<ChessMove> rookMoves() {
        return longRangeMove(new int[][]{{0,1}, {0,-1}, {-1,0}, {1,0}});
    }

    private Collection<ChessMove> queenMoves() {
        return longRangeMove(new int[][]{{0,1}, {0,-1}, {-1,0}, {1,0}, {1,1}, {1,-1}, {-1,1}, {-1,-1}});
    }

    private Collection<ChessMove> kingMoves() {
        var basicMoves = shortRangeMove(new int[][]{{0,1}, {0,-1}, {-1,0}, {1,0}, {1,1}, {1,-1}, {-1,1}, {-1,-1}});
        basicMoves.addAll(castleCheck());
        return basicMoves;
    }

    private ArrayList<ChessMove> castleCheck() {
        var castleOptions = new ArrayList<ChessMove>();
        if (hasMoved) return castleOptions;

        int row = startPosition.getRow();
        int kingStartColumn = 5;

        for (int i = 0; i < 2; ++i) {
            int rookStepOneColumn = (i==0) ? 7 : 2;
            int direction = (i==0) ? 1 : -1;
            var kingStepOne = new ChessPosition(row, kingStartColumn + direction);
            var rookStepOne = new ChessPosition(row, rookStepOneColumn);
            var kingEndPosition = new ChessPosition(row,kingStartColumn + (direction * 2));

            //checks if the rook is there & hasn't moved
            var rook = board.getPiece(new ChessPosition(row, (i==0) ? 8 : 1));
            if (rook == null || rook.getHasMoved()) {
                continue;
            }

            //checks if the spaces are clear between the king and the rook
            if (board.getPiece(kingStepOne) != null
                    || board.getPiece(kingEndPosition) != null
                    || board.getPiece(rookStepOne) != null) {
                continue;
            }

            castleOptions.add(new ChessMove(startPosition, kingEndPosition));
        }
        return castleOptions;
    }

    private Collection<ChessMove> knightMoves() {
        return shortRangeMove(new int[][]{{2,1}, {1,2}, {-1,2}, {2,-1}, {1,-2}, {-2,1}, {-1,-2}, {-2,-1}});
    }

    private Collection<ChessMove> pawnMoves() {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int row = startPosition.getRow();
        int column = startPosition.getColumn();
        int direction = (color == WHITE) ? 1 : -1;

        //checks forward moves
        var forwardMove = new ChessPosition(row + direction, column);
        if (board.checkRange(forwardMove) && board.getPiece(forwardMove) == null) {
            validMoves.add(new ChessMove(startPosition, forwardMove, null));

            var doubleMove = new ChessPosition(row + 2 * direction, column);
            if (row == (color == WHITE ? 2 : 7) && board.getPiece(doubleMove) == null) {
                validMoves.add(new ChessMove(startPosition, doubleMove, null));
            }
        }

        //checks attacks
        int[][] captureDirections = {{direction,1}, {direction,-1}};
        for (var capture : captureDirections) {
            var capturePosition = new ChessPosition(row + capture[0], column + capture[1]);
            if (board.checkRange(capturePosition)
                    && board.getPiece(capturePosition) != null &&
                    color != board.getPieceColor(capturePosition)) {
                validMoves.add(new ChessMove(startPosition, capturePosition, null));
            }
        }

        //checks for promotions
        if (!validMoves.isEmpty() &&
            validMoves.iterator().next().getEndPosition().getRow() == (color == WHITE ? 8 : 1)) {
            for (ChessMove move : new ArrayList<>(validMoves)) {
                move.setPromotionPiece(KNIGHT);
                ChessPosition endPosition = move.getEndPosition();
                validMoves.add(new ChessMove(startPosition, endPosition, BISHOP));
                validMoves.add(new ChessMove(startPosition, endPosition, ROOK));
                validMoves.add(new ChessMove(startPosition, endPosition, QUEEN));
            }
        }

        return validMoves;
    }

    private Collection<ChessMove> longRangeMove(int[][] directions) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        for (var direction : directions) {
            int row = startPosition.getRow();
            int col = startPosition.getColumn();
            ChessPosition nextPosition;
            do {
                row += direction[0];
                col += direction[1];
                nextPosition = new ChessPosition(row, col);
                if (checkMove(nextPosition)) {
                    validMoves.add(new ChessMove(startPosition, nextPosition, null));
                }
            } while (board.checkRange(nextPosition) && board.getPiece(nextPosition) == null);
        }
        return validMoves;
    }

    private ArrayList<ChessMove> shortRangeMove(int[][] directions) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        int row = startPosition.getRow();
        int column = startPosition.getColumn();
        for (int[] direction : directions) {
            ChessPosition nextPosition = new ChessPosition(row + direction[0], column + direction[1]);
            if (checkMove(nextPosition)) {
                validMoves.add(new ChessMove(startPosition, nextPosition, null));
            }
        }
        return validMoves;
    }

    private boolean checkMove(ChessPosition move) {
        if (!board.checkRange(move)) {
            return false;
        }
        return (board.getPiece(move) == null || color != board.getPieceColor(move));
    }
}
