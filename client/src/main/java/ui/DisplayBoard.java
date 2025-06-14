package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.*;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static ui.EscapeSequences.*;

public class DisplayBoard {
    private final ChessGame game;
    private boolean currentSquareWhite = true;
    private final Collection<ChessPosition> highlightSquares = new HashSet<>();

    public DisplayBoard(ChessGame game) {
        this.game = game;
    }

    public void whitePOV() {
        if (game == null) {
            System.out.println("Oops! null board.");
        }
        System.out.print("\n");
        printColumnLabels(WHITE);
        for (int i = 0; i < 8; ++i) {
            printRowLabel(i);
            for (int j = 0; j < 8; ++j) {
                printPosition(new ChessPosition(8 - i,j + 1));
            }
            currentSquareWhite = !currentSquareWhite;
            System.out.print(RESET_BG_COLOR);
            System.out.print(RESET_TEXT_COLOR);
            printRowLabel(i);
            System.out.print("\n");
        }
        printColumnLabels(WHITE);
    }

    public DisplayBoard highlightSquares(ChessPosition chessPosition) {
        highlightSquares.clear();

        Collection<ChessMove> moves = game.validMoves(chessPosition);
        if (moves == null) {
            return this;
        }

        highlightSquares.add(chessPosition);
        for (var move : moves) {
            highlightSquares.add(move.getEndPosition());
        }
        return this;
    }

    public void blackPOV() {
        if (game == null) {
            System.out.println("Oops! null board.");
        }
        System.out.print("\n");
        printColumnLabels(BLACK);
        for (int i = 7; i >= 0; --i) {
            printRowLabel(i);
            for (int j = 7; j >= 0; --j) {
                printPosition(new ChessPosition(8 - i,j + 1));
            }
            currentSquareWhite = !currentSquareWhite;
            System.out.print(RESET_BG_COLOR);
            System.out.print(RESET_TEXT_COLOR);
            printRowLabel(i);
            System.out.print("\n");
        }
        printColumnLabels(BLACK);
    }

    private void printColumnLabels(ChessGame.TeamColor color) {
        System.out.print(SET_BG_COLOR_DARK_GREY);
        if (color == WHITE) {
            print(" ABCDEFGH ");
        }
        else {
            print(" HGFEDCBA ");
        }
        System.out.print(RESET_BG_COLOR);
        System.out.print("\n");
    }

    private void printRowLabel(int i) {
        System.out.print(SET_BG_COLOR_DARK_GREY);
        print(String.valueOf(8-i));
        System.out.print(RESET_BG_COLOR);
    }

    private void printPosition(ChessPosition position) {
        String squareColor;
        if ((position.getRow() + position.getColumn()) % 2 == 1) {
            squareColor = (highlightSquares.contains(position)) ? SET_BG_COLOR_GREEN : SET_BG_COLOR_LIGHT_GREY;
        }
        else {
            squareColor = (highlightSquares.contains(position)) ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_DARK_GREY;
        }
        System.out.print(squareColor);

        ChessPiece piece = game.getBoard().getPiece(position);
        if (piece != null && piece.getTeamColor() == WHITE) {
            System.out.print(SET_TEXT_COLOR_WHITE);
        }
        else {
            System.out.print(SET_TEXT_COLOR_BLACK);
        }

        print(pieceToPrettyString(piece));
    }

    private void print(String string) {
        for (var character : string.codePoints().toArray()) {
            System.out.print(" ");
            System.out.print(Character.toString(character));
            System.out.print(" ");
        }
    }

    private String pieceToPrettyString(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        }
        if (piece.getTeamColor() == WHITE) {
            return getPieceString(piece, WHITE_PAWN, WHITE_ROOK, WHITE_KING, WHITE_QUEEN, WHITE_KNIGHT, WHITE_BISHOP);
        }
        else {
            return getPieceString(piece, BLACK_PAWN, BLACK_ROOK, BLACK_KING, BLACK_QUEEN, BLACK_KNIGHT, BLACK_BISHOP);
        }
    }

    private String getPieceString(ChessPiece piece, String pawn, String rook, String king, String queen, String knight, String bishop) {
        return switch (piece.getPieceType()) {
            case PAWN -> pawn;
            case ROOK -> rook;
            case KING -> king;
            case QUEEN -> queen;
            case KNIGHT -> knight;
            case BISHOP -> bishop;
        };
    }
}
