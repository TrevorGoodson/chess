package WebSocket;

import chess.ChessGame;
import chess.ChessGame.TeamColor;
import dataaccess.DataAccessException;
import dataaccess.GameDataDAO;
import dataaccess.GameDataDAOSQL;
import org.eclipse.jetty.websocket.api.Session;
import java.util.concurrent.ConcurrentHashMap;

import static chess.ChessGame.TeamColor.*;

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

}
