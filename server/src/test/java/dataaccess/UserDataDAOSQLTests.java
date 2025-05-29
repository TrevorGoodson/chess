package dataaccess;

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

//    @Test
//    public void addUserTest() {
//        userDataDAO.createUser(new );
//    }
}
