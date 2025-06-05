package ui;

import chess.ChessGame;

import static ui.EscapeSequences.*;

public class DisplayBoard {
    private ChessGame game = new ChessGame();

    public void whitePOV() {
        printColumnLabels();
    }

    private void printColumnLabels() {
        System.out.print(SET_TEXT_COLOR_DARK_GREY);
        System.out.print(" 12345678 ");
        System.out.print(RESET_BG_COLOR);
    }
}
