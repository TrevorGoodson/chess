package dataaccess;

import model.AuthData;

import java.util.ArrayList;


public class AuthDataDAOMemory implements AuthDataDAO {
    private final static ArrayList<AuthData> authData = new ArrayList<AuthData>();

    public AuthDataDAOMemory() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAuthData(AuthData authData) throws DataAccessException {
        if (getAuthData(authData.authToken()) != null) {
            throw new DataAccessException("AuthToken already in use.");
        }
        AuthDataDAOMemory.authData.add(authData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthData getAuthData(String authToken) {
        for (var authDatum : authData) {
            if (authToken.equals(authDatum.authToken())) {
                return authDatum;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAuthData(AuthData authData) throws DataAccessException {
        var iterator = AuthDataDAOMemory.authData.iterator();
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
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        authData.clear();
    }
}
