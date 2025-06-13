package WebSocket;

import chess.ChessGame.TeamColor;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import model.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import static chess.ChessGame.TeamColor.*;
import static websocket.messages.ServerMessage.ServerMessageType.*;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final GameManager games = new GameManager();
    private final AuthDataDAO authDataDAO = new AuthDataDAOSQL();
    private final GameDataDAO gameDataDAO = new GameDataDAOSQL();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case CONNECT -> handleConnectCommand(userGameCommand, session);
            case MAKE_MOVE -> handleMakeMove(userGameCommand, session);
            case RESIGN -> handleResign(userGameCommand);
            case OBSERVER_CONNECT -> handleObserver(userGameCommand, session);
        }
    }

    @OnWebSocketClose
    public void onWebSocketClose(Session session, int integer, String message) throws IOException, DataAccessException {
        games.cleanUpConnections();
    }

    private void handleConnectCommand(UserGameCommand userGameCommand, Session session) throws DataAccessException, IOException {
        AuthData authData = authDataDAO.getAuthData(userGameCommand.getAuthToken());
        GameData gameData = gameDataDAO.findGame(userGameCommand.getGameID());

        if (gameData == null || authData == null) {
            sendError(session, "Error: invalid request.");
            return;
        }

        String username = authData.username();
        TeamColor teamColor = (username.equals(gameData.whiteUsername())) ? WHITE : BLACK;

//        ChessGame.TeamColor teamColor = userGameCommand.getTeamColor();
//        if (teamColor == null) {
//            teamColor = gameData.;
//        }

        games.addPlayer(username, userGameCommand.getGameID(), teamColor, session);

        String color = (teamColor == WHITE) ? "white" : "black";
        games.notifyGame(userGameCommand.getGameID(),
                         new ServerMessage(NOTIFICATION, username + " joined the game as the " + color + " player."),
                         session);
    }

    private void handleResign(UserGameCommand userGameCommand) throws IOException, DataAccessException {
        games.resign(userGameCommand.getGameID(), userGameCommand.getTeamColor());
    }

    private void handleMakeMove(UserGameCommand userGameCommand, Session session) throws DataAccessException, IOException {
        AuthData authData = authDataDAO.getAuthData(userGameCommand.getAuthToken());
        GameData gameData = gameDataDAO.findGame(userGameCommand.getGameID());
        if (authData == null || gameData == null) {
            sendError(session, "Error: bad request.");
            return;
        }

        String username = authData.username();
        TeamColor teamColor = (username.equals(gameData.whiteUsername())) ? WHITE : BLACK;

        try {
            games.makeMove(userGameCommand.getGameID(), teamColor, userGameCommand.getMove());
        } catch (InvalidMoveException e) {
            sendError(session, "Error: invalid move");
        }
    }

    private void handleObserver(UserGameCommand userGameCommand, Session session) throws DataAccessException, IOException {
        GameData gameData = new GameDataDAOSQL().findGame(userGameCommand.getGameID());
        AuthData authData = new AuthDataDAOSQL().getAuthData(userGameCommand.getAuthToken());

        ServerMessage loadGame = new ServerMessage(LOAD_GAME, gameData.game());
        new Connection(authData.username(), session).send(loadGame);

        ServerMessage newObserver = new ServerMessage(NOTIFICATION, authData.username() + " started watching the game.");
        games.addObserver(authData.username(), userGameCommand.getGameID(), session);
        games.notifyGame(userGameCommand.getGameID(), newObserver, session);
    }

    private void sendError(Session session, String message) throws IOException {
        ServerMessage errorMessage = new ServerMessage(ERROR, message).updateToError();
        new Connection("", session).send(errorMessage);
    }
}
