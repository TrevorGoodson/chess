package dataaccess;

import model.AuthData;

public interface AuthDataDAO {
    /**
     * Adds an AuthData to the database.
     * @param authData the AuthData to add
     * @throws DataAccessException if the AuthToken is already registered
     */
    void addAuthData(AuthData authData) throws DataAccessException;

    /**
     * Finds an AuthData in the database
     * @param authToken the corresponding AuthToken
     * @return AuthData if found or null if not
     */
    AuthData getAuthData(String authToken) throws DataAccessException;

    /**
     * Removes an AuthData to the database.
     * @param authData the AuthData to remove
     * @throws DataAccessException if the AuthToken is not registered
     */
    void deleteAuthData(AuthData authData) throws DataAccessException;

    /**
     * Clears all AuthData from the database
     */
    void clear() throws DataAccessException;
}
