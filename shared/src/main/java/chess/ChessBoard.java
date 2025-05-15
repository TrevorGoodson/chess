package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import static chess.ChessGame.TeamColor;
import static chess.ChessPiece.PieceType.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {}

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        if (checkRange(position)) {
            board[8 - position.getRow()][position.getColumn() - 1] = piece;
        }
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position or if the position is out of range
     */
    public ChessPiece getPiece(ChessPosition position) {
        if (!checkRange(position)) {
            return null;
        }
        return board[8 - position.getRow()][position.getColumn() - 1];
    }

    /**
     * Gets the color of a chess piece on the board
     * @param position position of the chess piece
     * @return null (if no piece is there) or the color of the piece (ChessGame.TeamColor)
     */
    public ChessGame.TeamColor getPieceColor(ChessPosition position) {
        ChessPiece piece = getPiece(position);
        if (piece == null) {
            return null;
        }
        return piece.getTeamColor();
    }

    /**
     * Checks whether a given position is on the board or not
     * @param position the position to check
     * @return boolean value: on the board: true, else: false
     */
    public boolean checkRange(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        return (row >= 1) && (row <= 8) && (col >= 1) && (col <= 8);
    }

    public Collection<Pair<ChessPiece, ChessPosition>> allPiecesOnTeam(TeamColor color) {
        Collection<Pair<ChessPiece, ChessPosition>> pieces = new ArrayList<>();
        for (int row = 0; row < 8; ++row) {
            for (int col = 0; col < 8; ++col) {
                if (board[row][col] == null) {
                    continue;
                }
                ChessPiece piece = board[row][col];
                var position = new ChessPosition(8 - row, col + 1);
                if (piece.getTeamColor() == color) {
                    pieces.add(new Pair<>(piece, position));
                }
            }
        }
        return pieces;
    }

    public boolean isKingGone(TeamColor color) {
        for (var row : board) {
            for (var piece : row) {
                if (piece == null) {
                    continue;
                }
                if (piece.getPieceType() == KING && piece.getTeamColor() == color) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int col = 1; col <= 8; ++col) {
            addPiece(new ChessPosition(2, col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(7, col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
        addPiece(new ChessPosition(1, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, KING));
        addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, KING));
    }

    public ChessBoard copy() {
        var newBoard = new ChessBoard();
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (board[i][j] != null) {
                    newBoard.board[i][j] = board[i][j].copy();
                }
            }
        }
        return newBoard;
    }

    @Override
    public String toString() {
        var boardString = new StringBuilder();

        for (int row = 8; row >= 1; --row) {
            boardString.append("|");
            for (int col = 1; col <= 8; ++col) {
                var piece = getPiece(new ChessPosition(row, col));
                boardString.append((piece == null) ? " " : piece.toString());
                boardString.append("|");
//                if (col != 8) {
//                    boardString.append(" ");
//                }
            }
            if (row != 1) {
                boardString.append("\n");
            }
        }

        return boardString.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        var otherBoard = (ChessBoard) obj;
        return Arrays.deepEquals(board, otherBoard.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
