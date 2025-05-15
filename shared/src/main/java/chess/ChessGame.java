package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static chess.ChessGame.TeamColor.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board = new ChessBoard();
    private boolean whitesTurn = true;

    public ChessGame() {
        board.resetBoard();
    }

    public ChessGame(ChessBoard board, boolean whitesTurn) {
        this.board = board;
        this.whitesTurn = whitesTurn;
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
        if (!board.checkRange(startPosition)) {
            return null;
        }
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null || piece.getTeamColor() != getTeamTurn()) {
            return null;
        }

        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        for (var possibleMove : possibleMoves) {
            ChessGame testGame = copy();
            testGame.makeMoveNoSafetyChecks(possibleMove);
            if (!testGame.isInCheck(getTeamTurn())) {
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
        ChessPosition end = move.getEndPosition();
        Collection<ChessMove> moves = validMoves(start);
        if (!moves.contains(move)) {
            throw new InvalidMoveException();
        }

        makeMoveNoSafetyChecks(move);
    }

    private void makeMoveNoSafetyChecks(ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);
        board.addPiece(start, null);
        board.addPiece(end, piece);
        whitesTurn = !whitesTurn;
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
            ChessGame testGame = copy();
            Collection<ChessMove> testMoves = piece.pieceMoves(board, position);
            for (var testMove : testMoves) {
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
        return isInCheck(teamColor) && !anyValidMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !anyValidMoves(teamColor) && !isInCheck(teamColor);
    }

    private boolean anyValidMoves(TeamColor teamColor) {
        Collection<Pair<ChessPiece, ChessPosition>> teamPieces = board.allPiecesOnTeam(teamColor);
        //System.out.println(teamPieces);
        for (var pair : teamPieces) {
            ChessPosition position = pair.second();
            ChessGame testGame = copy();
            testGame.whitesTurn = (teamColor == WHITE);
            Collection<ChessMove> possibleMoves = testGame.validMoves(position);
            if (possibleMoves == null) {
                System.out.println(testGame.board.toString());
                System.out.println(position.toString());
                System.out.println(board.getPiece(position).toString());
                throw new RuntimeException("Whoops");
            }
            if (!possibleMoves.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public ChessGame copy() {
        return new ChessGame(board.copy(), whitesTurn);
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
