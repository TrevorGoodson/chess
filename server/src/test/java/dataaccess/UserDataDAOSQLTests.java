package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;

public class UserDataDAOSQLTests {
    UserDataDAO userDataDAO = new UserDataDAOSQL();

    @Test
    public void clearTest() {
        try {
            userDataDAO.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createUserTest() {
        try {
            userDataDAO.clear();
            userDataDAO.createUser(new UserData("Sydney", "1234", ""));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
