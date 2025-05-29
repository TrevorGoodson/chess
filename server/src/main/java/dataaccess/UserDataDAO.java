package dataaccess;

import model.UserData;

public interface UserDataDAO {
    /**
     * Finds a user in the database.
     * @param username the username of the user.
     * @return the user data or null if none is found.
     */
    UserData getUser(String username) throws DataAccessException;

    /**
     * Adds a new user to the database.
     * @param newUser the user data to be added
     */
    void createUser(UserData newUser) throws DataAccessException;

    /**
     * Clears all users from the database.
     */
    void clear() throws DataAccessException;
}
