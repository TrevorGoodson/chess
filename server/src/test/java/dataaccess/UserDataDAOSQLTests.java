package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
            userDataDAO.createUser(new UserData("Anne", "1234", ""));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createDuplicateUserTest() {
        try {
            userDataDAO.clear();
            userDataDAO.createUser(new UserData("Sydney", "1234", ""));
            assertThrows(DataAccessException.class, () -> userDataDAO.createUser(new UserData("Sydney", "1234", "")));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getUser() {
        try {
            userDataDAO.clear();
            UserData inputUserData = new UserData("username", "password", "email");
            userDataDAO.createUser(inputUserData);
            UserData outputUserData = userDataDAO.getUser("username");
            assertEquals(inputUserData, outputUserData);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getNullUser() {
        try {
            userDataDAO.clear();
            assertThrows(DataAccessException.class, () -> userDataDAO.getUser("username"));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
