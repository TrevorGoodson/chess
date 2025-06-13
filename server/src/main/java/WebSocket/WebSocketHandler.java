package WebSocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import model.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import static chess.ChessGame.TeamColor.WHITE;
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
        String username = authData.username();
        ChessGame.TeamColor teamColor = userGameCommand.getTeamColor();
        if (teamColor == null) {
            teamColor = WHITE;
        }
        games.addPlayer(username, userGameCommand.getGameID(), teamColor, session);

        String color = (teamColor == WHITE) ? "white" : "black";
        games.notifyGame(userGameCommand.getGameID(),
                         new ServerMessage(NOTIFICATION, username + " joined the game as the " + color + " player."),
                         username);
    }

    private void handleResign(UserGameCommand userGameCommand) throws IOException, DataAccessException {
        games.resign(userGameCommand.getGameID(), userGameCommand.getTeamColor());
    }

    private void handleMakeMove(UserGameCommand userGameCommand) throws DataAccessException, IOException {
        try {
            games.makeMove(userGameCommand.getGameID(), userGameCommand.getTeamColor(), userGameCommand.getChessMove());
        } catch (InvalidMoveException e) {
            games.notifyPlayer(userGameCommand.getGameID(), userGameCommand.getTeamColor(), "Error: Invalid move");
        }
    }

    private void handleObserver(UserGameCommand userGameCommand, Session session) throws DataAccessException, IOException {
        GameData gameData = new GameDataDAOSQL().findGame(userGameCommand.getGameID());
        AuthData authData = new AuthDataDAOSQL().getAuthData(userGameCommand.getAuthToken());
        ServerMessage serverMessage = new ServerMessage(LOAD_GAME, new Gson().toJson(gameData.game()));
        session.getRemote().sendString(new Gson().toJson(serverMessage));
        games.addObserver(authData.username(), userGameCommand.getGameID(), session);
        games.notifyGame(userGameCommand.getGameID(),
                         new ServerMessage(NOTIFICATION, authData.username() + " started watching the game."),
                         authData.username());
    }
}
