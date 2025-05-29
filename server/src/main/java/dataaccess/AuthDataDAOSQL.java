package dataaccess;

import model.AuthData;

import java.util.*;

public class AuthDataDAOSQL extends DataAccessSQL implements AuthDataDAO{
    @Override
    public void addAuthData(AuthData authData) throws DataAccessException {
        String authToken = authData.authToken();
        String username = authData.username();
        String sqlStatement = "INSERT INTO AuthData (authToken, username) VALUES (?, ?)";
        executeUpdate(sqlStatement, authToken, username);
    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {
        List<Map<String, Object>> table = executeSelect("AuthData", "authToken", authToken);
        if (table.isEmpty()) {
            return null;
        }
        String username = (String) table.getFirst().get("username");
        return new AuthData(authToken, username);
    }

    @Override
    public void deleteAuthData(AuthData authData) throws DataAccessException {
        String sqlStatement = "DELETE FROM AuthData WHERE authToken=?";
        executeUpdate(sqlStatement, authData.authToken());
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE AuthData";
        executeUpdate(statement);
    }

    @Override
    protected String[] getCreateStatements() {
        return new String[] {
            """
            CREATE TABLE IF NOT EXISTS  AuthData (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };
    }
}
