package service;

import model.*;
import dataaccess.*;
import requestresult.RegisterRequest;
import requestresult.*;
import java.util.UUID;

public class UserService {
    UserDataDAO userDataDAO = new UserDataDAO();
    AuthDataDAO authDataDAO = new AuthDataDAO();

    public UserService() {}

    private static void assertNoFieldsNull(Record record) throws IncompleteRequestException {
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

    public RegisterResult register(RegisterRequest r) throws UsernameTakenException, IncompleteRequestException {
        assertNoFieldsNull(r);
        if (userDataDAO.getUser(r.username()) != null) {
            throw new UsernameTakenException();
        }
        try {
            userDataDAO.createUser(new UserData(r.username(), r.password(), r.email()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String authToken = generateToken();
        var authData = new AuthData(authToken, r.username());
        try {
            authDataDAO.addAuthData(authData);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new RegisterResult(authData.username(), authData.authToken());
    }

    private String generateToken() {
        String newAuthToken;
        do {
            newAuthToken = UUID.randomUUID().toString();
        } while (authDataDAO.getAuthData(newAuthToken) != null);
        return newAuthToken;
    }

    public LoginResult login(LoginRequest r) throws WrongUsernameException, WrongPasswordException, IncompleteRequestException {
        assertNoFieldsNull(r);
        var user = userDataDAO.getUser(r.username());
        if (user == null) {
            throw new WrongUsernameException();
        }
        if (!user.password().equals(r.password())) {
            throw new WrongPasswordException();
        }
        String authToken = generateToken();
        var authData = new AuthData(authToken, r.username());
        try {
            authDataDAO.addAuthData(authData);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new LoginResult(r.username(), authToken);
    }

    public LogoutResult logout(LogoutRequest r) throws NotLoggedInException, IncompleteRequestException {
        assertNoFieldsNull(r);
        var authData = authDataDAO.getAuthData(r.authToken());
        if (authData == null) {
            throw new NotLoggedInException();
        }
        try {
            authDataDAO.deleteAuthData(authData);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return new LogoutResult();
    }
}
