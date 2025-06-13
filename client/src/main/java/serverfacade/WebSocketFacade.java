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
    private Session session;
    private WebSocketMessenger webSocketMessenger;
    private GameUI client;
    private final URI webSocketUrl;

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

    public WebSocketFacade(int port, WebSocketMessenger webSocketMessageHandler) throws ConnectionException {
        try {
            webSocketUrl = new URI("ws" + ServerFacade.getServerUrl() + port + "/ws");
            this.webSocketMessenger = webSocketMessageHandler;

            connectToServer();

        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ConnectionException(ex.getMessage());
        }
    }

    private void connectToServer() throws DeploymentException, IOException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, webSocketUrl);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                webSocketMessenger.sendMessage(serverMessage, client);
            }
        });
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

    public void makeMove(String authToken, ChessMove chessMove, Integer gameID) throws ConnectionException {
        try {
            UserGameCommand userGameCommand = new UserGameCommand(authToken, gameID, chessMove);
            send(userGameCommand);
        } catch (IOException e) {
            throw new ConnectionException();
        }
    }

    private void send(UserGameCommand userGameCommand) throws IOException {
        if (!session.isOpen()) {
            try {
                connectToServer();
            } catch (DeploymentException e) {
                throw new IOException(e);
            }
        }
        session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
    }

    public void setWebSocketMessenger(WebSocketMessenger webSocketMessenger) {
        this.webSocketMessenger = webSocketMessenger;
    }

    public void endSession() throws IOException {
        session.close();
    }

    public void resign(Integer gameID, TeamColor teamColor) throws ConnectionException {
        try {
            UserGameCommand userGameCommand = new UserGameCommand(RESIGN, null, gameID, teamColor);
            send(userGameCommand);
        } catch (IOException e) {
            throw new ConnectionException();
        }
    }
}
