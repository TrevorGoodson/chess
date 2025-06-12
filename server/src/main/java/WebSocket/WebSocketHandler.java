package WebSocket;

import com.google.gson.Gson;
import dataaccess.AuthDataDAO;
import dataaccess.AuthDataDAOSQL;
import dataaccess.DataAccessException;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.WebSocketMessage;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

import static websocket.WebSocketMessage.messageType.*;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final GameManager games = new GameManager();
    private final AuthDataDAO authDataDAO = new AuthDataDAOSQL();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        WebSocketMessage webSocketMessage = new Gson().fromJson(message, WebSocketMessage.class);
        if (webSocketMessage.type == SERVER_MESSAGE) {
            ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
            handleServerMessage(serverMessage, session);
        }
        if (webSocketMessage.type == USER_GAME) {
            UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
            handleUserGameCommand(userGameCommand, session);
        }

    }

    private void handleUserGameCommand(UserGameCommand userGameCommand, Session session) throws DataAccessException, IOException {
        switch(userGameCommand.getCommandType()) {
            case CONNECT -> {
                handleConnectCommand(userGameCommand, session);
            }
            case MAKE_MOVE -> {
            }
            case LEAVE -> {
            }
            case RESIGN -> {
            }
        }
    }

    private void handleConnectCommand(UserGameCommand userGameCommand, Session session) throws DataAccessException, IOException {
        AuthData authData = authDataDAO.getAuthData(userGameCommand.getAuthToken());
        String username = authData.username();
        games.addPlayer(username, userGameCommand.getGameID(), userGameCommand.getTeamColor(), session);
        games.notifyGame(userGameCommand.getGameID(), username + " joined the game.");
    }

    private void handleServerMessage(ServerMessage serverMessage, Session session) throws IOException {
        switch (serverMessage.getServerMessageType()) {
            case LOGIN -> {
                connections.add(serverMessage.getMessage(), session);
            }
            case LOAD_GAME -> {

            }
            case ERROR -> {
                //nothing
            }
            case NOTIFICATION -> {
                sendNotification(serverMessage);
            }
        }
    }

    private void sendNotification(ServerMessage message) throws IOException {
        connections.broadcast("", message);
    }
}
