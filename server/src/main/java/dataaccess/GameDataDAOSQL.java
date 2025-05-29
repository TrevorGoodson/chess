package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.util.ArrayList;

public class GameDataDAOSQL extends DataAccessSQL implements GameDataDAO{
    @Override
    public ArrayList<GameData> getAllGames() {
        return null;
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        String sqlStatement = "INSERT INTO GameData (whiteUsername, blackUsername, gameName, chessGameJSON) VALUES (?, ?, ?, ?)";
        int gameID = executeUpdate(sqlStatement, null, null,gameName, serializeGame(chessGame));
        return gameID;
    }

    public String serializeGame(ChessGame game) {
        return new Gson().toJson(game);
    }

    public ChessGame deSerializeGame(String game) {
        return new Gson().fromJson(game, ChessGame.class);
    }

    @Override
    public GameData findGame(int gameID) throws DataAccessException {
        String sqlStatement = "SELECT whiteUsername, blackUsername, gameName, chessGameJSON FROM GameData WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sqlStatement)) {
            ps.setInt(1, gameID);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new GameData(gameID,
                                        rs.getString("whiteUsername"),
                                        rs.getString("blackUsername"),
                                        rs.getString("gameName"),
                                        deSerializeGame(rs.getString("chessGameJSON")));
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Unable to read data:" + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void addUser(int gameID, String username, ChessGame.TeamColor color) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {
        String sqlStatement = "TRUNCATE GameData";
        executeUpdate(sqlStatement);
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
}
