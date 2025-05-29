package test.java.dataaccess;

import dataaccess.*;
import model.AuthData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDataDAOSQLTests {
    @Test
    public void insertTest() {
        AuthDataDAO authDataDAO = new AuthDataDAOSQL();
        try {
            authDataDAO.addAuthData(new AuthData("sample_auth_token", "sample_username"));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void selectTest() {
        AuthDataDAO authDataDAO = new AuthDataDAOSQL();
        try {
            String authToken = "sample_auth_token";
            String username = "sample_username";
            //authDataDAO.addAuthData(new AuthData(authToken, username));
            AuthData a = authDataDAO.getAuthData(authToken);
            assertEquals(authToken, a.authToken());
            assertEquals(username, a.username());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
