package serverfacade;

import chess.ChessMove;
import com.google.gson.Gson;
import ui.GameUI;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static chess.ChessGame.TeamColor;
import static websocket.commands.UserGameCommand.CommandType.*;

public class WebSocketFacade extends Endpoint {
    Session session;
    WebSocketMessageHandler notificationHandler;
    GameUI client;

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

    public WebSocketFacade(int port, WebSocketMessageHandler webSocketMessageHandler) throws ConnectionException {
        try {
            URI socketURI = new URI("ws" + ServerFacade.getServerUrl() + port + "/ws");
            this.notificationHandler = webSocketMessageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    webSocketMessageHandler.sendMessage(serverMessage, client);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ConnectionException(ex.getMessage());
        }
    }

    public void linkClient(GameUI client) {
        this.client = client;
    }

    public void startObserving(Integer gameID, String authToken) throws ConnectionException {
        try {
            UserGameCommand userGameCommand = new UserGameCommand(OBSERVER_CONNECT, authToken, gameID);
            send(userGameCommand);
        } catch (IOException e) {
            throw new ConnectionException();
        }
    }

    public void joinGame(String authToken, Integer gameID, TeamColor teamColor) throws ConnectionException {
        try {
            UserGameCommand userGameCommand = new UserGameCommand(CONNECT, authToken, gameID, teamColor);
            send(userGameCommand);
        } catch (IOException e) {
            throw new ConnectionException();
        }
    }

    public void makeMove(String authToken, ChessMove chessMove, Integer gameID, TeamColor teamColor) throws ConnectionException {
        try {
            UserGameCommand userGameCommand = new UserGameCommand(MAKE_MOVE, authToken, gameID, teamColor, chessMove);
            send(userGameCommand);
        } catch (IOException e) {
            throw new ConnectionException();
        }
    }

    private void send(UserGameCommand userGameCommand) throws IOException {
        session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
    }

    public void setNotificationHandler(WebSocketMessageHandler webSocketMessageHandler) {
        this.notificationHandler = webSocketMessageHandler;
    }
}
