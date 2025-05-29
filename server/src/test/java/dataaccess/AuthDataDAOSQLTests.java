package test.java.dataaccess;

import dataaccess.*;
import model.AuthData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDataDAOSQLTests {
    AuthDataDAO authDataDAO = new AuthDataDAOSQL();

    @Test
    public void clearTest() {
        try {
            authDataDAO.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void insertTest() {
        try {
            authDataDAO.clear();
            authDataDAO.addAuthData(new AuthData("sample_auth_token", "sample_username"));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void insertTestRepeat() {
        try {
            authDataDAO.clear();
            authDataDAO.addAuthData(new AuthData("sample_auth_token", "sample_username"));
            assertThrows(DataAccessException.class, () -> authDataDAO.addAuthData(new AuthData("sample_auth_token", "sample_username")));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void insertTestNullValues() {
        try {
            authDataDAO.clear();
            authDataDAO.addAuthData(new AuthData("sample_auth_token", "sample_username"));
            assertThrows(DataAccessException.class, () -> authDataDAO.addAuthData(new AuthData("sample_auth_token", null)));
            assertThrows(DataAccessException.class, () -> authDataDAO.addAuthData(new AuthData(null, null)));
            assertThrows(DataAccessException.class, () -> authDataDAO.addAuthData(new AuthData(null, "sample_username")));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void selectTest() {
        try {
            authDataDAO.clear();
            String authToken = "sample_auth_token";
            String username = "sample_username";
            authDataDAO.addAuthData(new AuthData(authToken, username));
            AuthData a = authDataDAO.getAuthData(authToken);
            assertEquals(authToken, a.authToken());
            assertEquals(username, a.username());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void deleteTest() {
        try {
            authDataDAO.clear();
            String authToken = "sample_auth_token3";
            String username = "sample_username3";
            authDataDAO.addAuthData(new AuthData(authToken, username));
            AuthData a = authDataDAO.getAuthData(authToken);
            authDataDAO.deleteAuthData(a);
            AuthData b = authDataDAO.getAuthData(authToken);
            assertNull(b);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
