package serverfacade;

import chess.ChessGame;
import chess.ChessGame.TeamColor;
import com.google.gson.Gson;
import ui.DisplayBoard;
import websocket.messages.ServerMessage;
import static websocket.messages.ServerMessage.ServerMessageType.*;
import static chess.ChessGame.TeamColor.*;

public class WebSocketMessageHandler {
    private TeamColor teamColor = null;

    public WebSocketMessageHandler() {}

    public WebSocketMessageHandler(TeamColor teamColor) {
        this.teamColor = teamColor;
    }
    public void sendMessage(ServerMessage serverMessage) {
        if (serverMessage.getServerMessageType() == LOAD_GAME) {
            loadGame(serverMessage.getMessage());
            return;
        }
        System.out.println(serverMessage.getMessage());
    }

    private void loadGame(String chessGameJSON) {
        if (teamColor == null || teamColor == WHITE) {
            new DisplayBoard(new Gson().fromJson(chessGameJSON, ChessGame.class)).whitePOV();
        }
        else {
            new DisplayBoard(new Gson().fromJson(chessGameJSON, ChessGame.class)).blackPOV();
        }
    }
}
