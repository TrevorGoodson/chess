package dataaccess;

import model.UserData;
import java.util.*;

public class UserDataDAOSQL extends DataAccessSQL implements UserDataDAO {
    @Override
    public UserData getUser(String username) throws DataAccessException {
        List<Map<String, Object>> table = executeSelect("UserData", "username", username);
        if (table.isEmpty()) {
            throw new DataAccessException("Username not found");
        }
        Map<String, Object> userData = table.getFirst();
        return new UserData((String) userData.get("username"),
                            (String) userData.get("password"),
                            (String) userData.get("email"));
    }

    @Override
    public void createUser(UserData newUser) throws DataAccessException {
        String sqlStatement = "INSERT INTO UserData (username, passwordHash, email) VALUES (?, ?, ?)";
        //FIXME: actually hash the password
        executeUpdate(sqlStatement, newUser.username(), newUser.password(), newUser.email());
    }

    @Override
    public void clear() throws DataAccessException {
        String sqlStatement = "TRUNCATE UserData";
        executeUpdate(sqlStatement);
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
