package chess;

import java.util.*;

public class ChessMoveCalculator {
    private final ChessBoard board;
    private final ChessPosition position;
    private final ChessPiece.PieceType type;
    private final ChessGame.TeamColor pieceColor;

    public ChessMoveCalculator(ChessBoard board, ChessPiece piece, ChessPosition position) {
        this.board = board;
        this.position = position;
        type = piece.getPieceType();
        pieceColor = piece.getTeamColor();
    }

    public Collection<ChessMove> calculateMoves() {
        if (type == ChessPiece.PieceType.BISHOP) {
            return bishopMoves();
        }
        if (type == ChessPiece.PieceType.ROOK) {
            return rookMoves();
        }
        if (type == ChessPiece.PieceType.QUEEN) {
            return queenMoves();
        }
        if (type == ChessPiece.PieceType.KING) {
            return kingMoves();
        }
        if (type == ChessPiece.PieceType.KNIGHT) {
            return knightMoves();
        }
        if (type == ChessPiece.PieceType.PAWN) {
            return pawnMoves();
        }
        throw new RuntimeException("Unknown piece type");
    }

    private Collection<ChessMove> bishopMoves() {
        return longRangeMove(board, position, new int[][]{{1,1}, {1,-1}, {-1,1}, {-1,-1}});
    }

    private Collection<ChessMove> rookMoves() {
        return longRangeMove(board, position, new int[][]{{0,1}, {0,-1}, {-1,0}, {1,0}});
    }

    private Collection<ChessMove> queenMoves() {
        return longRangeMove(board, position,
                new int[][]{{0,1}, {0,-1}, {-1,0}, {1,0}, {1,1}, {1,-1}, {-1,1}, {-1,-1}});
    }

    private Collection<ChessMove> kingMoves() {
        return shortRangeMove(board, position,
                new int[][]{{0,1}, {0,-1}, {-1,0}, {1,0}, {1,1}, {1,-1}, {-1,1}, {-1,-1}});
    }

    private Collection<ChessMove> knightMoves() {
        return shortRangeMove(board, position,
                new int[][]{{2,1}, {1,2}, {-1,2}, {2,-1}, {1,-2}, {-2,1}, {-1,-2}, {-2,-1}});
    }

    private Collection<ChessMove> pawnMoves() {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int row = position.getRow();
        int column = position.getColumn();
        boolean colorIsWhite = (pieceColor == ChessGame.TeamColor.WHITE);
        int direction = colorIsWhite ? 1 : -1;

        //checks for the normal/double move
        var normalMove = new ChessPosition(row + direction, column);
        if (board.checkRange(normalMove) && board.getPiece(normalMove) == null) {
            validMoves.add(new ChessMove(position, normalMove, null));

            if (colorIsWhite && row == 2 || (!colorIsWhite && row == 7)) {
                var doubleMove = new ChessPosition(row + (2*direction), column);
                if (board.getPiece(doubleMove) == null) {
                    validMoves.add(new ChessMove(position, doubleMove, null));
                }
            }
        }

        //checks for attacks
        int[][] attackDirections = new int[][]{{direction,1}, {direction,-1}};
        for (var attackDirection : attackDirections) {
            var attackCheck = new ChessPosition(row + attackDirection[0], column + attackDirection[1]);
            if (board.checkRange(attackCheck) && board.getPiece(attackCheck) != null &&
                    pieceColor != board.getPieceColor(attackCheck)) {
                validMoves.add(new ChessMove(position, attackCheck, null));
            }
        }

        //checks for promotion
        if (!validMoves.isEmpty()) {
            int firstMoveRow = validMoves.iterator().next().getEndPosition().getRow();
            if ((colorIsWhite && firstMoveRow == 8) ||
                    (!colorIsWhite && firstMoveRow == 1)) {
                for (ChessMove move : new ArrayList<>(validMoves)) {
                    move.setPromotionPiece(ChessPiece.PieceType.KNIGHT);
                    ChessPosition endPosition = move.getEndPosition();
                    validMoves.add(new ChessMove(position, endPosition, ChessPiece.PieceType.BISHOP));
                    validMoves.add(new ChessMove(position, endPosition, ChessPiece.PieceType.ROOK));
                    validMoves.add(new ChessMove(position, endPosition, ChessPiece.PieceType.QUEEN));
                }
            }
        }

        return validMoves;
    }

    private Collection<ChessMove> longRangeMove(ChessBoard board, ChessPosition myPosition, int[][] directions) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        for (int[] direction : directions) {
            int numSteps = 0;
            ChessPosition nextPosition;
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
        Collection<ChessMove> validMoves = new ArrayList<>();
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
