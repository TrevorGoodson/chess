package dataaccess;

import model.UserData;
import java.util.ArrayList;

public class UserDataDAO {
    private final static ArrayList<UserData> userData = new ArrayList<>();

    public UserDataDAO() {}

    /**
     * Finds a user in the database.
     * @param username the username of the user.
     * @return the user data or null if none is found.
     */
    public UserData getUser(String username) {
        for (var userDatum : userData) {
            if (userDatum.username().equals(username)) {
                return userDatum;
            }
        }
        return null;
    }

    /**
     * Adds a new user to the database.
     * @param newUser the user data to be added
     */
    public void createUser(UserData newUser) throws DataAccessException {
        if (getUser(newUser.username()) != null) {
            throw new DataAccessException("Username already taken.");
        }
        userData.add(newUser);
    }

    /**
     * Clears all users from the database.
     */
    public void clear() {
        userData.clear();
    }
}
