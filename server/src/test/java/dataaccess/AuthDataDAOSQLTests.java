package test.java.dataaccess;

import dataaccess.*;
import model.AuthData;
import org.junit.jupiter.api.Test;

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
}
