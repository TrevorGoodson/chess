package WebSocket;

import chess.ChessGame;
import chess.ChessGame.TeamColor;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.DataAccessException;
import dataaccess.GameDataDAO;
import dataaccess.GameDataDAOSQL;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import static chess.ChessGame.TeamColor.*;
import static websocket.messages.ServerMessage.ServerMessageType.NOTIFICATION;

public class GameManager {
    GameDataDAO gameDataDAO = new GameDataDAOSQL();
    private static final ConcurrentHashMap<Integer, ChessGameData> LIVE_GAMES = new ConcurrentHashMap<>();

    public void addPlayer(String username, Integer gameID, TeamColor teamColor, Session session) {
        if (LIVE_GAMES.containsKey(gameID)) {
            addPlayerToExistingGame(username, gameID, teamColor, session);
        }
        else {
            addLiveGame(username, gameID, teamColor, session);
        }
    }

    private void addPlayerToExistingGame(String username, Integer gameID, TeamColor teamColor, Session session) {
        ChessGameData chessGameData = LIVE_GAMES.get(gameID);
        String whiteUsername = chessGameData.whiteUsername();
        String blackUsername = chessGameData.blackUsername();
        Connection whiteConnection = chessGameData.whiteConnection();
        Connection blackConnection = chessGameData.blackConnection();
        ChessGame game = chessGameData.chessGame();

        if (teamColor == WHITE && whiteUsername != null ||
            teamColor == BLACK && blackUsername != null
        ) {
            return;
        }

        if (teamColor == WHITE) {
            whiteUsername = username;
            whiteConnection = new Connection(username, session);
        }
        else {
            blackUsername = username;
            blackConnection = new Connection(username, session);
        }

        LIVE_GAMES.remove(gameID);
        LIVE_GAMES.put(gameID, new ChessGameData(whiteUsername, blackUsername, whiteConnection, blackConnection, game));
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

    public void notifyGame(Integer gameID, String message) throws IOException {
        if (!LIVE_GAMES.containsKey(gameID)) {
            return;
        }
        ServerMessage serverMessage = new ServerMessage(NOTIFICATION, message);
        Connection whiteConnection = LIVE_GAMES.get(gameID).whiteConnection();
        Connection blackConnection = LIVE_GAMES.get(gameID).blackConnection();
        if (whiteConnection != null) {
            whiteConnection.send(serverMessage);
        }
        if (blackConnection != null) {
            blackConnection.send(serverMessage);
        }
    }

    public void notifyPlayer(Integer gameID, TeamColor teamColor, String message) throws IOException {
        if (!LIVE_GAMES.containsKey(gameID)) {
            return;
        }
        ServerMessage serverMessage = new ServerMessage(NOTIFICATION, message);
        Connection playerConnection = (teamColor == WHITE) ? LIVE_GAMES.get(gameID).whiteConnection() :
                                                             LIVE_GAMES.get(gameID).blackConnection();
        if (playerConnection != null) {
            playerConnection.send(serverMessage);
        }
    }

    public void makeMove(Integer gameID, TeamColor teamColor, ChessMove chessMove) throws InvalidMoveException, IOException {
        if (!LIVE_GAMES.containsKey(gameID)) {
            return;
        }
        ChessGame chessGame = LIVE_GAMES.get(gameID).chessGame();
        if (teamColor != chessGame.getTeamTurn()) {
            notifyPlayer(gameID, teamColor, "You can only play on your turn!");
            return;
        }
        chessGame.makeMove(chessMove);
    }
}
