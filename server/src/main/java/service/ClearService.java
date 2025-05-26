package service;

import dataaccess.AuthDataDAO;
import dataaccess.GameDataDAO;
import dataaccess.UserDataDAO;
import requestresult.ClearResult;

public class ClearService {
    AuthDataDAO authDataDAO = new AuthDataDAO();
    GameDataDAO gameDataDAO = new GameDataDAO();
    UserDataDAO userDataDAO = new UserDataDAO();

    public ClearService() {}

    public ClearResult clear() {
        authDataDAO.clear();
        gameDataDAO.clear();
        userDataDAO.clear();
        return new ClearResult();
    }
}
