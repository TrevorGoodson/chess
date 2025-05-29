package service;

import dataaccess.DataAccessException;
import requestresult.ClearResult;

public class ClearService extends Service {
    public ClearService() {}

    /**
     * Deletes all data in database
     * @return An empty record to indicate success
     */
    public ClearResult clear() {
        try {
            authDataDAO.clear();
            gameDataDAO.clear();
            userDataDAO.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return new ClearResult();
    }
}
