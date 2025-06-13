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
            case CONNECT -> handleConnect(userGameCommand, session);
            case MAKE_MOVE -> handleMakeMove(userGameCommand, session);
            case RESIGN -> handleResign(userGameCommand);
        }
    }

    @OnWebSocketClose
    public void onWebSocketClose(Session session, int integer, String message) throws IOException, DataAccessException {
        games.cleanUpConnections();
    }

    private void handleConnect(UserGameCommand userGameCommand, Session session) throws DataAccessException, IOException {
        AuthData authData = authDataDAO.getAuthData(userGameCommand.getAuthToken());
        GameData gameData = gameDataDAO.findGame(userGameCommand.getGameID());

        if (gameData == null || authData == null) {
            sendError(session, "Error: invalid request.");
            return;
        }

        String username = authData.username();
        TeamColor teamColor;
        if (username.equals(gameData.whiteUsername())) {
            teamColor = WHITE;
        } else if (username.equals(gameData.blackUsername())) {
            teamColor = BLACK;
        }
        else {
            teamColor = null;
        }

        ServerMessage loadGame = new ServerMessage(LOAD_GAME, gameData.game());
        new Connection(username, session).send(loadGame);

        if (teamColor == null) {
            addObserver(username, userGameCommand.getGameID(), session);
        }
        else {
            addPLayer(username, userGameCommand.getGameID(), teamColor, session);
        }
    }

    private void addPLayer(String username, Integer gameID, TeamColor teamColor, Session session) throws IOException, DataAccessException {
        games.addPlayer(username, gameID, teamColor, session);
        String color = (teamColor == WHITE) ? "white" : "black";
        ServerMessage newPlayer
                = new ServerMessage(NOTIFICATION, username + " joined the game as the " + color + " player.");
        games.notifyGame(gameID, newPlayer, session);
    }

    private void addObserver(String username, Integer gameID, Session session) throws DataAccessException, IOException {
        games.addObserver(username, gameID, session);
        ServerMessage newObserver
                = new ServerMessage(NOTIFICATION, username + " started watching the game.");
        games.notifyGame(gameID, newObserver, session);
    }

    private void handleResign(UserGameCommand userGameCommand) throws IOException, DataAccessException {
        games.resign(userGameCommand.getGameID(), userGameCommand.getTeamColor());
    }

    private void handleMakeMove(UserGameCommand userGameCommand, Session session) throws DataAccessException, IOException {
        AuthData authData = authDataDAO.getAuthData(userGameCommand.getAuthToken());
        Integer gameID = userGameCommand.getGameID();
        GameData gameData = gameDataDAO.findGame(gameID);

        if (authData == null || gameData == null) {
            sendError(session, "Error: bad request.");
            return;
        }
        if (gameData.game().isGameOver()) {
            sendError(session, "Error: the game is over");
            return;
        }

        String username = authData.username();
        if (isObserver(username, gameData)) {
            sendError(session, "Error: you can only make moves as a player.");
            return;
        }

        String currentPlayer = getCurrentPLayer(gameData);
        if (!username.equals(currentPlayer)) {
            sendError(session, "Error: it's not your turn.");
            return;
        }

        TeamColor teamColor = (username.equals(gameData.whiteUsername())) ? WHITE : BLACK;

        try {
            games.makeMove(gameID, teamColor, userGameCommand.getMove());
        } catch (InvalidMoveException e) {
            sendError(session, "Error: invalid move");
            return;
        }

        gameData = gameDataDAO.findGame(gameID);
        games.notifyGame(gameID, new ServerMessage(LOAD_GAME, gameData.game()), null);

        ServerMessage moveUpdate
                = new ServerMessage(NOTIFICATION, username + " has played " + userGameCommand.getMove());
        games.notifyGame(gameID, moveUpdate, session);

        TeamColor opposingTeamColor = (teamColor == WHITE) ? BLACK : WHITE;
        if (gameData.game().isInCheck(opposingTeamColor)) {
            ServerMessage checkNotice
                    = new ServerMessage(NOTIFICATION, username + " has put his opponent in check!");
            games.notifyGame(gameID, checkNotice, null);
        }
    }

    private String getCurrentPLayer(GameData gameData) {
        if (gameData.game().getTeamTurn() == WHITE) {
            return gameData.whiteUsername();
        }
        else {
            return gameData.blackUsername();
        }
    }

    private boolean isObserver(String username, GameData gameData) {
        return !username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername());
    }

    private void sendError(Session session, String message) throws IOException {
        ServerMessage errorMessage = new ServerMessage(ERROR, message).updateToError();
        new Connection("", session).send(errorMessage);
    }
}
