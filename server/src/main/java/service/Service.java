package service;

import dataaccess.*;
import model.AuthData;
import service.exceptions.IncompleteRequestException;
import service.exceptions.NotLoggedInException;

public abstract class Service {
    protected static AuthDataDAO authDataDAO = new AuthDataDAOMemory();
    protected static GameDataDAO gameDataDAO = new GameDataDAOMemory();
    protected static UserDataDAO userDataDAO = new UserDataDAOMemory();

    protected static void assertRequestComplete(Record record) throws IncompleteRequestException {
        if (record == null) {
            throw new IncompleteRequestException();
        }

        for (var component : record.getClass().getRecordComponents()) {
            try {
                var value = component.getAccessor().invoke(record);
                if (value == null) {
                    throw new IncompleteRequestException();
                }
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Reflection failure when checking record fields", e);
            }
        }
    }

    protected AuthData verifyUser(String authToken) throws NotLoggedInException {
        AuthData authData;
        try {
            authData = authDataDAO.getAuthData(authToken);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        if (authData == null) {
            throw new NotLoggedInException();
        }
        return authData;
    }
}
