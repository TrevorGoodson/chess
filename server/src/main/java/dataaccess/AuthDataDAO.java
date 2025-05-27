package dataaccess;

import model.AuthData;

import java.time.DateTimeException;
import java.util.ArrayList;


public class AuthDataDAO {
    private final static ArrayList<AuthData> authData = new ArrayList<AuthData>();

    public AuthDataDAO() {}

    /**
     * Adds an AuthData to the database.
     * @param authData the AuthData to add
     * @throws DataAccessException if the AuthToken is already registered
     */
    public void addAuthData(AuthData authData) throws DataAccessException {
        if (getAuthData(authData.authToken()) != null) {
            throw new DataAccessException("AuthToken already in use.");
        }
        AuthDataDAO.authData.add(authData);
    }

    public void printData() {
        for (var authDatum : authData) {
            System.out.println("AuthData:" + authDatum);
        }
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
        var iterator = AuthDataDAO.authData.iterator();
        while (iterator.hasNext()) {
            var authDatum = iterator.next();
            if (authData.equals(authDatum)) {
                iterator.remove();
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
