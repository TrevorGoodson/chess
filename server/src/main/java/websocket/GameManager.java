package websocket;

import chess.*;
import chess.ChessGame.TeamColor;
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

    public void notifyGame(Integer gameID, ServerMessage serverMessage, Session excludeSession) throws IOException {
        if (!LIVE_GAMES.containsKey(gameID)) {
            return;
        }
        Connection whiteConnection = LIVE_GAMES.get(gameID).getWhiteConnection();
        Connection blackConnection = LIVE_GAMES.get(gameID).getBlackConnection();

        if (whiteConnection != null && !whiteConnection.session().equals(excludeSession)) {
            whiteConnection.send(serverMessage);
        }
        if (blackConnection != null && !blackConnection.session().equals(excludeSession)) {
            blackConnection.send(serverMessage);
        }
        for (var observer : LIVE_GAMES.get(gameID).observers.values()) {
            if (!observer.session().equals(excludeSession)) {
                observer.send(serverMessage);
            }
        }
    }

    public void makeMove(Integer gameID, TeamColor teamColor, ChessMove chessMove) throws InvalidMoveException,
                                                                                          IOException,
                                                                                          DataAccessException {
        if (!LIVE_GAMES.containsKey(gameID)) {
            return;
        }

        ChessGame chessGame = LIVE_GAMES.get(gameID).getChessGame();
        chessGame.makeMove(chessMove);
        gameDataDAO.updateGame(gameID, chessGame);
    }

    public void removeUser(Integer gameID, TeamColor teamColor) {
        if (!LIVE_GAMES.containsKey(gameID)) {
            return;
        }
        if (teamColor == null) {
            return;
        }

        ChessGameData chessGameData = LIVE_GAMES.get(gameID);
        if (teamColor == WHITE) {
            chessGameData.setWhiteConnection(null);
            chessGameData.setWhiteUsername(null);
        } else {
            chessGameData.setBlackConnection(null);
            chessGameData.setBlackUsername(null);
        }
    }

    public void removeObserver(Integer gameID, Session session) {
        if (!LIVE_GAMES.containsKey(gameID)) {
            return;
        }
        LIVE_GAMES.get(gameID).observers.remove(session);
    }

    public Integer findGameID(Session session) {
        for (Integer gameID : LIVE_GAMES.keySet()) {
            var game = LIVE_GAMES.get(gameID);
            Connection white = game.getWhiteConnection();
            Connection black = game.getBlackConnection();
            if (white != null && white.session().equals(session) ||
                black != null && black.session().equals(session) ||
                game.observers.containsKey(session)
            ) {
                return gameID;
            }
        }
        return null;
    }

    public String findUsername(Integer gameID, Session session) {
        if (!LIVE_GAMES.containsKey(gameID)) {
            return null;
        }
        var game = LIVE_GAMES.get(gameID);
        Connection white = game.getWhiteConnection();
        Connection black = game.getBlackConnection();
        if (white != null && white.session().equals(session)) {
            return game.getWhiteUsername();
        }
        else if (black != null && black.session().equals(session)) {
            return game.getBlackUsername();
        }

        Connection observer = game.observers.get(session);
        if (observer == null) {
            return null;
        }
        return observer.username();

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

        ServerMessage resignAnnouncement
                = new ServerMessage(NOTIFICATION, username + " has resigned! " + opposingUser + " has won!");
        notifyGame(gameID, resignAnnouncement, null);
    }

    public void cleanUpGames() {
        var removeList = new ArrayList<Integer>();

        for (Integer gameID : LIVE_GAMES.keySet()) {
            var game = LIVE_GAMES.get(gameID);
            if (game.getWhiteUsername() == null &&
                game.getBlackUsername() == null &&
                game.observers.isEmpty()
            ) {
                removeList.add(gameID);
            }
        }

        for (Integer gameID : removeList) {
            LIVE_GAMES.remove(gameID);
        }
    }
}
