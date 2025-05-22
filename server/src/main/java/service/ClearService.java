package service;

import dataaccess.AuthDataDAO;
import dataaccess.GameDataDAO;
import dataaccess.UserDataDAO;

public class ClearService {
    AuthDataDAO authDataDAO = new AuthDataDAO();
    GameDataDAO gameDataDAO = new GameDataDAO();
    UserDataDAO userDataDAO = new UserDataDAO();

    public ClearService() {}

    public void clear() {
        authDataDAO.clear();
        gameDataDAO.clear();
        userDataDAO.clear();
    }
}
