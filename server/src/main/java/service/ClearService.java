package service;

import dataaccess.AuthDataDAO;
import dataaccess.GameDataDAO;
import dataaccess.UserDataDAO;
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
