package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import java.util.*;

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
