package WebSocket;

import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDataDAO;
import dataaccess.AuthDataDAOSQL;
import dataaccess.DataAccessException;
import dataaccess.GameDataDAOSQL;
import model.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import usererrorexceptions.GameNotFoundException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import static websocket.messages.ServerMessage.ServerMessageType.*;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final GameManager games = new GameManager();
    private final AuthDataDAO authDataDAO = new AuthDataDAOSQL();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case CONNECT -> handleConnectCommand(userGameCommand, session);
            case MAKE_MOVE -> handleMakeMove(userGameCommand);
            case LEAVE -> {
            }
            case RESIGN -> {
            }
            case OBSERVER_CONNECT -> handleObserver(userGameCommand, session);
        }
    }

    @OnWebSocketClose
    public void onWebSocketClose(Session session, int integer, String message) throws IOException, DataAccessException {
        games.cleanUpConnections();
    }

    private void handleConnectCommand(UserGameCommand userGameCommand, Session session) throws DataAccessException, IOException {
        AuthData authData = authDataDAO.getAuthData(userGameCommand.getAuthToken());
        String username = authData.username();
        games.addPlayer(username, userGameCommand.getGameID(), userGameCommand.getTeamColor(), session);
        games.notifyGame(userGameCommand.getGameID(), new ServerMessage(NOTIFICATION, username + " joined the game."));
    }

    private void handleMakeMove(UserGameCommand userGameCommand) throws DataAccessException, IOException {
        try {
            games.makeMove(userGameCommand.getGameID(), userGameCommand.getTeamColor(), userGameCommand.getChessMove());
        } catch (InvalidMoveException e) {
            games.notifyPlayer(userGameCommand.getGameID(), userGameCommand.getTeamColor(), "Invalid move");
        }
    }

    private void handleObserver(UserGameCommand userGameCommand, Session session) throws DataAccessException, IOException {
        GameData gameData = new GameDataDAOSQL().findGame(userGameCommand.getGameID());
        AuthData authData = new AuthDataDAOSQL().getAuthData(userGameCommand.getAuthToken());
        ServerMessage serverMessage = new ServerMessage(LOAD_GAME, new Gson().toJson(gameData.game()));
        session.getRemote().sendString(new Gson().toJson(serverMessage));
        games.addObserver(authData.username(), userGameCommand.getGameID(), session);
    }
}
