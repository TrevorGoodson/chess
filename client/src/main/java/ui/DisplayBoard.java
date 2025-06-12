package ui;

import chess.ChessGame;
import chess.ChessPiece;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static ui.EscapeSequences.*;

public class DisplayBoard {
    private final ChessGame game;
    private boolean currentSquareWhite = true;

    public DisplayBoard(ChessGame game) {
        this.game = game;
    }

    public void whitePOV() {
        printColumnLabels(WHITE);
        for (int i = 0; i < 8; ++i) {
            ChessPiece[] row = game.getBoard().getRowToDisplay(i);
            printRowLabel(i);
            for (int j = 0; j < 8; ++j) {
                printPiece(row[j]);
            }
            currentSquareWhite = !currentSquareWhite;
            System.out.print(RESET_BG_COLOR);
            System.out.print(RESET_TEXT_COLOR);
            printRowLabel(i);
            System.out.print("\n");
        }
        printColumnLabels(WHITE);
    }

    public void blackPOV() {
        printColumnLabels(BLACK);
        for (int i = 7; i >= 0; --i) {
            ChessPiece[] row = game.getBoard().getRowToDisplay(i);
            printRowLabel(i);
            for (int j = 7; j >= 0; --j) {
                printPiece(row[j]);
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

    private void printPiece(ChessPiece piece) {
        if (currentSquareWhite) {
            System.out.print(SET_BG_COLOR_LIGHT_GREY);
        }
        else {
            System.out.print(SET_BG_COLOR_DARK_GREY);
        }
        if (piece != null && piece.getTeamColor() == WHITE) {
            System.out.print(SET_TEXT_COLOR_WHITE);
        }
        else {
            System.out.print(SET_TEXT_COLOR_BLACK);
        }
        currentSquareWhite = !currentSquareWhite;
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
