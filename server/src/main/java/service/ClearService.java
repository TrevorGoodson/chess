package service;

import dataaccess.DataAccessException;
import requestresultrecords.ClearResult;

public class ClearService extends Service {
    public ClearService() {}

    /**
     * Deletes all data in database
     * @return An empty record to indicate success
     */
    public ClearResult clear() throws DataAccessException {
        authDataDAO.clear();
        gameDataDAO.clear();
        userDataDAO.clear();
        return new ClearResult();
    }
}
