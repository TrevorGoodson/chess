package dataaccess;

import model.UserData;

public class UserDataDAOSQL extends DataAccessSQL implements UserDataDAO {
    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void createUser(UserData newUser) throws DataAccessException {

    }

    @Override
    public void clear() {

    }

    @Override
    protected String[] getCreateStatements() {
        return new String[] {
            """
            CREATE TABLE IF NOT EXISTS  UserData (
              `username` varchar(256) NOT NULL,
              `passwordHash` varchar(256) NOT NULL,
              `email` varchar(320) NOT NULL,
              PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };
    }
}
