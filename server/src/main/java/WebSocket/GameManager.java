package WebSocket;

import chess.*;
import chess.ChessGame.TeamColor;
import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static chess.ChessGame.TeamColor.*;
import static websocket.messages.ServerMessage.ServerMessageType.*;

public class GameManager {
    GameDataDAO gameDataDAO = new GameDataDAOSQL();
    private static final ConcurrentHashMap<Integer, ChessGameData> LIVE_GAMES = new ConcurrentHashMap<>();

    public void addPlayer(String username, Integer gameID, TeamColor teamColor, Session session) throws IOException, DataAccessException {
        cleanUpConnections();

        if (LIVE_GAMES.containsKey(gameID)) {
            addPlayerToExistingGame(username, gameID, teamColor, session);
        }
        else {
            addLiveGame(username, gameID, teamColor, session);
        }

    }

    private void addPlayerToExistingGame(String username, Integer gameID, TeamColor teamColor, Session session) {
        ChessGameData chessGameData = LIVE_GAMES.get(gameID);
        Connection newPlayer = new Connection(username, session);

        if (teamColor == WHITE && chessGameData.getWhiteUsername() != null ||
            teamColor == BLACK && chessGameData.getBlackUsername() != null
        ) {
            return;
        }

        if (teamColor == WHITE) {
            chessGameData.setWhiteUsername(username);
            chessGameData.setWhiteConnection(newPlayer);
        }
        else {
            chessGameData.setBlackUsername(username);
            chessGameData.setBlackConnection(newPlayer);
        }
    }

    private void addLiveGame(String username, Integer gameID, TeamColor teamColor, Session session) {
        String whiteUsername = null;
        String blackUsername = null;
        Connection whiteConnection = null;
        Connection blackConnection = null;

        if (teamColor == WHITE) {
            whiteUsername = username;
            whiteConnection = new Connection(username, session);
        }
        else {
            blackUsername = username;
            blackConnection = new Connection(username, session);
        }

        ChessGame game;
        try {
            game = gameDataDAO.findGame(gameID).game();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        ChessGameData chessGameData = new ChessGameData(whiteUsername,
                                                        blackUsername,
                                                        whiteConnection,
                                                        blackConnection,
                                                        game);
        LIVE_GAMES.put(gameID, chessGameData);
    }

    public void addObserver(String username, Integer gameID, Session session) throws DataAccessException {
        Connection observer = new Connection(username, session);

        if (LIVE_GAMES.containsKey(gameID)) {
            LIVE_GAMES.get(gameID).observers.put(session, observer);
        }
        else {
            GameData gameData = gameDataDAO.findGame(gameID);
            LIVE_GAMES.put(gameID, new ChessGameData(null, null, null, null, gameData.game()));
            LIVE_GAMES.get(gameID).observers.put(session, observer);
        }
    }

    public void notifyGame(Integer gameID, ServerMessage serverMessage, String username) throws IOException {
        if (!LIVE_GAMES.containsKey(gameID)) {
            return;
        }
        Connection whiteConnection = LIVE_GAMES.get(gameID).getWhiteConnection();
        Connection blackConnection = LIVE_GAMES.get(gameID).getBlackConnection();

        if (whiteConnection != null && !whiteConnection.username().equals(username)) {
            whiteConnection.send(serverMessage);
        }
        if (blackConnection != null && !blackConnection.username().equals(username)) {
            blackConnection.send(serverMessage);
        }
        for (var observer : LIVE_GAMES.get(gameID).observers.values()) {
            if (!observer.username().equals(username)) {
                observer.send(serverMessage);
            }
        }
    }

    public void notifyPlayer(Integer gameID, TeamColor teamColor, String message) throws IOException {
        if (!LIVE_GAMES.containsKey(gameID)) {
            return;
        }
        ServerMessage serverMessage = new ServerMessage(NOTIFICATION, message);
        Connection playerConnection = (teamColor == WHITE) ? LIVE_GAMES.get(gameID).getWhiteConnection() :
                                                             LIVE_GAMES.get(gameID).getBlackConnection();
        if (playerConnection != null) {
            playerConnection.send(serverMessage);
        }
    }

    public void makeMove(Integer gameID, TeamColor teamColor, ChessMove chessMove) throws InvalidMoveException,
                                                                                          IOException,
                                                                                          DataAccessException {
        if (!LIVE_GAMES.containsKey(gameID)) {
            return;
        }

        ChessGame chessGame = LIVE_GAMES.get(gameID).getChessGame();
        if (chessGame.isGameOver()) {
            notifyPlayer(gameID, teamColor, "The game has ended!");
        }
        if (teamColor != chessGame.getTeamTurn()) {
            notifyPlayer(gameID, teamColor, "You can only play on your turn!");
            return;
        }

        chessGame.makeMove(chessMove);
        gameDataDAO.updateGame(gameID, chessGame);
        String username = (teamColor == WHITE) ? LIVE_GAMES.get(gameID).getWhiteUsername() : LIVE_GAMES.get(gameID).getBlackUsername();

        notifyGame(gameID, new ServerMessage(LOAD_GAME, new Gson().toJson(chessGame)), "");
        notifyGame(gameID, new ServerMessage(NOTIFICATION, username + " has played " + chessMove), username);

        TeamColor opposingTeamColor = (teamColor == WHITE) ? BLACK : WHITE;
        if (chessGame.isInCheck(opposingTeamColor)) {
            notifyGame(gameID, new ServerMessage(NOTIFICATION, username + " has put his opponent in check!"), "");
        }
    }

    public void cleanUpConnections() throws IOException, DataAccessException {
        var removeList = new ArrayList<Integer>();

        for (var gameID : LIVE_GAMES.keySet()) {
            var game = LIVE_GAMES.get(gameID);
            Connection white = game.getWhiteConnection();
            Connection black = game.getBlackConnection();
            if (white != null && !white.session().isOpen()) {
                gameDataDAO.removeUser(gameID, WHITE);
                game.setWhiteConnection(null);
                notifyGame(gameID, new ServerMessage(NOTIFICATION, game.getWhiteUsername() + " has left the game."), "");
                game.setWhiteUsername(null);
            }
            if (black != null && !black.session().isOpen()) {
                gameDataDAO.removeUser(gameID, BLACK);
                game.setBlackConnection(null);
                notifyGame(gameID, new ServerMessage(NOTIFICATION, game.getBlackUsername() + " has left the game."), "");
                game.setBlackUsername(null);
            }

            cleanUpObservers(gameID);

            if (game.getWhiteConnection() == null && game.getBlackConnection() == null && game.observers.isEmpty()) {
                removeList.add(gameID);
            }

            cleanUpDatabase();
        }

        for (var gameID : removeList) {
            LIVE_GAMES.remove(gameID);
        }
    }

    private void cleanUpObservers(Integer gameID) throws IOException {
        if (!LIVE_GAMES.containsKey(gameID)) {
            return;
        }

        ChessGameData game = LIVE_GAMES.get(gameID);
        var removeList = new ArrayList<Session>();
        for (Session observer : game.observers.keySet()) {
            if (!observer.isOpen()) {
                removeList.add(observer);
                notifyGame(gameID, new ServerMessage(NOTIFICATION, game.observers.get(observer).username() + " has stopped watching."), "");
            }
        }

        for (Session observer : removeList) {
            game.observers.remove(observer);
        }
    }

    private void cleanUpDatabase() throws DataAccessException {
        List<GameData> dbGames = gameDataDAO.getAllGames();
        for (GameData game : dbGames) {
            if (LIVE_GAMES.containsKey(game.gameID())) {
                continue;
            }
            if (game.whiteUsername() != null) {
                gameDataDAO.removeUser(game.gameID(), WHITE);
            }
            if (game.blackUsername() != null) {
                gameDataDAO.removeUser(game.gameID(), BLACK);
            }
        }
    }

    public void resign(Integer gameID, TeamColor teamColor) throws DataAccessException, IOException {
        if (!LIVE_GAMES.containsKey(gameID)) {
            return;
        }

        ChessGame chessGame = LIVE_GAMES.get(gameID).getChessGame();
        chessGame.resign(teamColor);
        gameDataDAO.updateGame(gameID, chessGame);

        String username;
        String opposingUser;
        if (teamColor == WHITE) {
            username = LIVE_GAMES.get(gameID).getWhiteUsername();
            opposingUser = LIVE_GAMES.get(gameID).getBlackUsername();
        }
        else {
            username = LIVE_GAMES.get(gameID).getBlackUsername();
            opposingUser = LIVE_GAMES.get(gameID).getWhiteUsername();
        }
        notifyGame(gameID, new ServerMessage(NOTIFICATION, username + " has resigned! " + opposingUser + " has won!"), "");
    }
}
