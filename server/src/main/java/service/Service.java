package service;

import dataaccess.AuthDataDAO;
import dataaccess.GameDataDAO;
import dataaccess.UserDataDAO;

public abstract class Service {
    protected AuthDataDAO authDataDAO = new AuthDataDAO();
    protected GameDataDAO gameDataDAO = new GameDataDAO();
    protected UserDataDAO userDataDAO = new UserDataDAO();

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
}
