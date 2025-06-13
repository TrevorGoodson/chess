package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.*;
import java.util.*;
import chess.ChessGame.TeamColor;

import static chess.ChessGame.TeamColor.*;

public class GameDataDAOSQL extends DataAccessSQL implements GameDataDAO {
    @Override
    public List<GameData> getAllGames() throws DataAccessException {
        List<GameData> gameList = new ArrayList<>();
        String sqlStatement = "SELECT * FROM GameData";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlStatement);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ChessGame game = deSerializeGame(rs.getString("chessGameJSON"));
                gameList.add(new GameData(rs.getInt("gameID"),
                             rs.getString("whiteUsername"),
                             rs.getString("blackUsername"),
                             rs.getString("gameName"),
                             game));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to read data:" + e.getMessage(), e);
        }
        return gameList;
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        String sqlStatement = "INSERT INTO GameData (whiteUsername, blackUsername, gameName, chessGameJSON) VALUES (?, ?, ?, ?)";
        return executeUpdate(sqlStatement, null, null, gameName, serializeGame(chessGame));
    }

    public String serializeGame(ChessGame game) {
        return new Gson().toJson(game);
    }

    public ChessGame deSerializeGame(String game) {
        return new Gson().fromJson(game, ChessGame.class);
    }

    @Override
    public GameData findGame(int gameID) throws DataAccessException {
        List<Map<String, Object>> table = executeSelect("GameData", "gameID", gameID);
        if (table.isEmpty()) {
            return null;
        }
        Map<String, Object> gameData = table.getFirst();
        ChessGame game = deSerializeGame((String) gameData.get("chessGameJSON"));
        return new GameData(gameID,
                            (String) gameData.get("whiteUsername"),
                            (String) gameData.get("blackUsername"),
                            (String) gameData.get("gameName"),
                            game);
    }

    @Override
    public void addUser(int gameID, String username, TeamColor color) throws DataAccessException {
        Map<String, Object> gameData = getGameData(gameID);
        String desiredTeam = (color == WHITE) ? "whiteUsername" : "blackUsername";
        if (gameData.get(desiredTeam) != null) {
            throw new DataAccessException("Team already taken");
        }
        String sqlStatement = "UPDATE GameData SET " + desiredTeam + " = ? WHERE gameID = ?";
        executeUpdate(sqlStatement, username, gameID);
    }

    @Override
    public void removeUser(int gameID, TeamColor color) throws DataAccessException {
        Map<String, Object> gameData = getGameData(gameID);
        String desiredTeam = (color == WHITE) ? "whiteUsername" : "blackUsername";
        if (gameData.get(desiredTeam) == null) {
            throw new DataAccessException("Player not found");
        }
        String sqlStatement = "UPDATE GameData SET " + desiredTeam + " = NULL WHERE gameID = ?";
        executeUpdate(sqlStatement, gameID);
    }

    private Map<String, Object> getGameData(int gameID) throws DataAccessException {
        List<Map<String, Object>> table = executeSelect("GameData", "gameID", gameID);
        if (table.isEmpty()) {
            throw new DataAccessException("Game not found");
        }
        return table.getFirst();
    }

    @Override
    public void clear() throws DataAccessException {
        String sqlStatement = "TRUNCATE GameData";
        executeUpdate(sqlStatement);
    }

    @Override
    public void updateGame(int gameID, ChessGame chessGame) throws DataAccessException {
        String sqlStatement = "UPDATE GameData SET chessGameJSON = ? WHERE gameID = ?";
        executeUpdate(sqlStatement, serializeGame(chessGame), gameID);
    }

    @Override
    protected String[] getCreateStatements() {
        return new String[] {
            """
            CREATE TABLE IF NOT EXISTS  GameData (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `chessGameJSON` varchar(4096) NOT NULL,
              PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };
    }

    public static void main(String[] args) {
        System.out.print(new GameDataDAOSQL().serializeGame(new ChessGame()));
    }
}
