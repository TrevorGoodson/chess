package service;

import dataaccess.*;

public abstract class Service {
    protected AuthDataDAO authDataDAO = new AuthDataDAOMemory();
    protected GameDataDAO gameDataDAO = new GameDataDAOMemory();
    protected UserDataDAO userDataDAO = new UserDataDAOMemory();

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
