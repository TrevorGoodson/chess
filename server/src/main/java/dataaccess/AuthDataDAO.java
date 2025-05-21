package dataaccess;

import model.AuthData;

import java.time.DateTimeException;
import java.util.ArrayList;


public class AuthDataDAO {
    private final ArrayList<AuthData> authData = new ArrayList<AuthData>();

    public AuthDataDAO() {}

    /**
     * Adds an AuthData to the database.
     * @param authData the AuthData to add
     * @throws DataAccessException if the AuthToken is already registered
     */
    public void addAuthData(AuthData authData) throws DataAccessException {
        for (var authDatum : this.authData) {
            if (authData.authToken().equals(authDatum.authToken())) {
                throw new DataAccessException("AuthToken already in use.");
            }
        }
        this.authData.add(authData);
    }

    /**
     * Finds an AuthData in the database
     * @param authToken the corresponding AuthToken
     * @return AuthData if found or null if not
     */
    public AuthData getAuthData(String authToken) {
        for (var authDatum : authData) {
            if (authToken.equals(authDatum.authToken())) {
                return authDatum;
            }
        }
        return null;
    }

    /**
     * Removes an AuthData to the database.
     * @param authData the AuthData to remove
     * @throws DataAccessException if the AuthToken is not registered
     */
    public void deleteAuthData(AuthData authData) throws DataAccessException {
        for (var authDatum : this.authData) {
            if (authData.equals(authDatum)) {
                this.authData.remove(authData);
                return;
            }
        }
        throw new DataAccessException("AuthToken not found.");
    }

    /**
     * Clears all AuthData from the database
     */
    public void clear() {
        authData.clear();
    }
}
