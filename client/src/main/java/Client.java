import chess.*;
import ui.PreLoggedInUI;

import static java.lang.Integer.parseInt;

public class Client {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        new PreLoggedInUI().run(parseInt(args[0]));
    }
}