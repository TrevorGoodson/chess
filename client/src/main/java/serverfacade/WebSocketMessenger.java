package serverfacade;

import chess.ChessGame;
import chess.ChessGame.TeamColor;
import com.google.gson.Gson;
import ui.DisplayBoard;
import ui.GameUI;
import websocket.messages.ServerMessage;
import static websocket.messages.ServerMessage.ServerMessageType.*;
import static chess.ChessGame.TeamColor.*;

public class WebSocketMessenger {
    private TeamColor teamColor = null;

    public WebSocketMessenger() {}

    public WebSocketMessenger(TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    public void sendMessage(ServerMessage serverMessage, GameUI client) {
        if (serverMessage.getServerMessageType() != LOAD_GAME) {
            System.out.println(serverMessage.getMessage());
            return;
        }

        loadGame(serverMessage.getGame(), client);
    }

    private void loadGame(ChessGame chessGame, GameUI client) {
        if (teamColor == null || teamColor == WHITE) {
            new DisplayBoard(chessGame).whitePOV();
        }
        else {
            new DisplayBoard(chessGame).blackPOV();
        }

        if (client != null) {
            client.setChessGame(chessGame);
        }
    }
}
