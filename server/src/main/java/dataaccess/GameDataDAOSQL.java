package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public class GameDataDAOSQL extends DataAccessSQL implements GameDataDAO{
    @Override
    public ArrayList<GameData> getAllGames() {
        return null;
    }

    @Override
    public int createGame(String gameName) {
        return 0;
    }

    @Override
    public GameData findGame(int gameID) {
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
              `gameJSON` varchar(1024) NOT NULL,
              PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };
    }
}
