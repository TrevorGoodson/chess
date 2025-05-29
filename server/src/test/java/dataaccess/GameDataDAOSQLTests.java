package test.java.dataaccess;

import dataaccess.DataAccessException;
import dataaccess.GameDataDAO;
import dataaccess.GameDataDAOSQL;
import org.junit.jupiter.api.Test;

public class GameDataDAOSQLTests {
    GameDataDAO gameDataDAO = new GameDataDAOSQL();
    @Test
    public void clearTest() {
        try {
            gameDataDAO.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
