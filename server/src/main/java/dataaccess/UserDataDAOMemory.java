package dataaccess;

import model.UserData;
import java.util.ArrayList;

public class UserDataDAOMemory implements UserDataDAO {
    private final static ArrayList<UserData> userData = new ArrayList<>();

    public UserDataDAOMemory() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public UserData getUser(String username) {
        for (var userDatum : userData) {
            if (userDatum.username().equals(username)) {
                return userDatum;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createUser(UserData newUser) throws DataAccessException {
        if (getUser(newUser.username()) != null) {
            throw new DataAccessException("Username already taken.");
        }
        userData.add(newUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        userData.clear();
    }
}
