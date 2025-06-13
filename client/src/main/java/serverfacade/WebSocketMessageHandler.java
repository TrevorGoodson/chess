package serverfacade;

import chess.ChessGame;
import chess.ChessGame.TeamColor;
import com.google.gson.Gson;
import ui.DisplayBoard;
import ui.GameUI;
import websocket.messages.ServerMessage;
import static websocket.messages.ServerMessage.ServerMessageType.*;
import static chess.ChessGame.TeamColor.*;

public class WebSocketMessageHandler {
    private TeamColor teamColor = null;

    public WebSocketMessageHandler() {}

    public WebSocketMessageHandler(TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    public void sendMessage(ServerMessage serverMessage, GameUI client) {
        if (serverMessage.getServerMessageType() != LOAD_GAME) {
            System.out.println(serverMessage.getMessage());
            return;
        }

        ChessGame game = loadGame(serverMessage.getMessage());
        if (client != null) {
            client.setChessGame(game);
        }
    }

    private ChessGame loadGame(String chessGameJSON) {
        ChessGame chessGame = new Gson().fromJson(chessGameJSON, ChessGame.class);
        if (teamColor == null || teamColor == WHITE) {
            new DisplayBoard(chessGame).whitePOV();
        }
        else {
            new DisplayBoard(chessGame).blackPOV();
        }
        return chessGame;
    }
}
