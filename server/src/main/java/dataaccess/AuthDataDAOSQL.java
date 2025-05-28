package dataaccess;

import model.AuthData;

public class AuthDataDAOSQL extends DataAccessSQL implements AuthDataDAO{
    @Override
    public void addAuthData(AuthData authData) throws DataAccessException {

    }

    @Override
    public AuthData getAuthData(String authToken) {
        return null;
    }

    @Override
    public void deleteAuthData(AuthData authData) throws DataAccessException {

    }

    @Override
    public void clear() {

    }

    @Override
    protected String[] getCreateStatements() {
        return new String[] {
                """
            CREATE TABLE IF NOT EXISTS  AuthData (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`),
              INDEX(authToken)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };
    }
}
