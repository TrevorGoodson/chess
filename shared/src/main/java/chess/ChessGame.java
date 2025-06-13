package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static chess.ChessGame.TeamColor.*;
import static chess.ChessPiece.PieceType.*;
import static java.lang.Math.abs;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board = new ChessBoard();
    private boolean gameOver = false;
    private TeamColor winningTeam = null;
    private boolean whitesTurn = true;
    private final ArrayList<ChessMove> history = new ArrayList<>();

    public ChessGame() {
        board.startGame(history);
    }

    public ChessGame(ChessBoard board, boolean whitesTurn) {
        this.board = board;
        this.whitesTurn = whitesTurn;
        board.linkMoveHistory(history);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return whitesTurn ? WHITE : BLACK;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        whitesTurn = (team == WHITE);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        board.linkMoveHistory(history);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition or startPosition is out of range
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (!ChessBoard.checkRange(startPosition)) {
            return null;
        }
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }

        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        TeamColor color = board.getPieceColor(startPosition);
        for (var possibleMove : possibleMoves) {
            if (checkIfCastle(possibleMove)) {
                if (isInCheck(color)) {
                    continue;
                }
                ChessGame testGame = copy();
                var castleStepOne = new ChessPosition(startPosition.getRow(), ((possibleMove.getEndPosition().getColumn() == 7) ? 6 : 4));
                testGame.makeMoveNoSafetyChecks(new ChessMove(startPosition, castleStepOne));
                if (testGame.isInCheck(color)) {
                    continue;
                }
            }
            ChessGame testGame = copy();
            //testGame.board.linkMoveHistory(history);
            testGame.makeMoveNoSafetyChecks(possibleMove);
            if (!testGame.isInCheck(color)) {
                validMoves.add(possibleMove);
            }
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        Collection<ChessMove> moves = validMoves(start);

        if (moves == null ||
            !moves.contains(move) ||
            board.getPiece(start).getTeamColor() != (whitesTurn ? WHITE : BLACK)
        ) {
            throw new InvalidMoveException();
        }
        makeMoveNoSafetyChecks(move);
        history.add(move);

        whitesTurn = !whitesTurn;
    }

    private void makeMoveNoSafetyChecks(ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);

        if (checkIfCastle(move)) {
            int castleRow = start.getRow();
            int rookStartColumn = (end.getColumn() == 7) ? 8 : 1;
            int rookEndColumn = (end.getColumn() == 7) ? 6 : 4;
            var rookStart = new ChessPosition(castleRow, rookStartColumn);
            var rookEnd = new ChessPosition(castleRow, rookEndColumn);
            makeMoveNoSafetyChecks(new ChessMove(rookStart, rookEnd));
        }
        else if (checkIfEnPassant(move)) {
            board.addPiece(new ChessPosition(start.getRow(), end.getColumn()), null);
        }

        board.addPiece(start, null);
        TeamColor color = piece.getTeamColor();
        if (move.getPromotionPiece() != null) {
            board.addPiece(end, new ChessPiece(color, move.getPromotionPiece()));
        }
        else {
            board.addPiece(end, piece);
        }
    }

    private boolean checkIfCastle(ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        if (board.getPiece(start).getPieceType() != KING) {
            return false;
        }
        int distance = abs(start.getColumn() - end.getColumn());
        return (distance == 2);
    }

    private boolean checkIfEnPassant(ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        if (board.getPiece(start).getPieceType() != PAWN) {
            return false;
        }
        return (start.getColumn() != end.getColumn()) && (board.getPiece(end) == null);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor opposingTeamColor = (teamColor == WHITE) ? BLACK : WHITE;
        Collection<Pair<ChessPiece, ChessPosition>> opposingTeamPieces = board.allPiecesOnTeam(opposingTeamColor);
        for (var pair : opposingTeamPieces) {
            ChessPiece piece = pair.first();
            ChessPosition position = pair.second();
            Collection<ChessMove> testMoves = piece.pieceMoves(board, position);
            for (var testMove : testMoves) {
                ChessGame testGame = copy();
                testGame.makeMoveNoSafetyChecks(testMove);
                if (testGame.board.isKingGone(teamColor)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && checkIfNoValidMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return checkIfNoValidMoves(teamColor) && !isInCheck(teamColor);
    }

    private boolean checkIfNoValidMoves(TeamColor teamColor) {
        Collection<Pair<ChessPiece, ChessPosition>> teamPieces = board.allPiecesOnTeam(teamColor);
        //System.out.println(teamPieces);
        for (var pair : teamPieces) {
            ChessPosition position = pair.second();
            ChessGame testGame = copy();
            testGame.whitesTurn = (teamColor == WHITE);
            Collection<ChessMove> possibleMoves = testGame.validMoves(position);
            if (!possibleMoves.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public void resign(TeamColor teamColor) {
        winningTeam = (teamColor == BLACK) ? WHITE : BLACK;
        gameOver = true;
    }

    public TeamColor getWinningTeam() {
        return winningTeam;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public ChessGame copy() {
        return new ChessGame(board.copy(), whitesTurn);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append((whitesTurn) ? "White" : "Black");
        stringBuilder.append("'s turn.\nHistory: ");
        stringBuilder.append(history.toString());
        stringBuilder.append("\n");
        stringBuilder.append(board.toString());
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return whitesTurn == chessGame.whitesTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, whitesTurn);
    }
}
