package service;

import requestresult.ClearResult;

public class ClearService extends Service {
    public ClearService() {}

    public ClearResult clear() {
        authDataDAO.clear();
        gameDataDAO.clear();
        userDataDAO.clear();
        return new ClearResult();
    }
}
