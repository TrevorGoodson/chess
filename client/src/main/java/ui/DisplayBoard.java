package ui;

import chess.ChessGame;
import chess.ChessPiece;

import static chess.ChessGame.TeamColor.WHITE;
import static ui.EscapeSequences.*;

public class DisplayBoard {
    private ChessGame game = new ChessGame();
    private boolean currentSquareWhite = true;

    public void whitePOV() {
        printColumnLabels();
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
        printColumnLabels();
    }

    public void blackPOV() {
        printColumnLabels();
        for (int i = 0; i < 8; ++i) {
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
        printColumnLabels();
    }

    private void printColumnLabels() {
        System.out.print(SET_BG_COLOR_DARK_GREY);
        print(" 12345678 ");
        System.out.print(RESET_BG_COLOR);
        System.out.print("\n");
    }

    private void printRowLabel(int i) {
        System.out.print(SET_BG_COLOR_DARK_GREY);
        print(Character.toString('A' + i));
        System.out.print(RESET_BG_COLOR);
    }

    private void printPiece(ChessPiece piece) {
        if (currentSquareWhite) {
            System.out.print(SET_BG_COLOR_WHITE);
            System.out.print(SET_TEXT_COLOR_BLACK);
        }
        else {
            System.out.print(SET_BG_COLOR_BLACK);
            System.out.print(SET_TEXT_COLOR_WHITE);
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
