package serverfacade;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static websocket.messages.ServerMessage.ServerMessageType.*;
import static chess.ChessGame.TeamColor;
import static websocket.commands.UserGameCommand.CommandType.*;

public class WebSocketFacade extends Endpoint {

    Session session;
    WebSocketMessageHandler notificationHandler;

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
                    webSocketMessageHandler.sendMessage(serverMessage);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ConnectionException(ex.getMessage());
        }
    }

    public void sendNotification(ServerMessage message) throws ConnectionException {
        try {
            session.getBasicRemote().sendText(new Gson().toJson(message));
        } catch (IOException e) {
            throw new ConnectionException();
        }
    }

    public void login(String username) throws ConnectionException {
        try {
            ServerMessage message = new ServerMessage(LOGIN, username);
            session.getBasicRemote().sendText(new Gson().toJson(message));
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
