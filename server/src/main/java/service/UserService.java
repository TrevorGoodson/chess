package service;

import model.*;
import dataaccess.*;
import requestresult.RegisterRequest;
import requestresult.*;
import java.util.UUID;

public class UserService extends Service {
    public UserService() {}

    public RegisterResult register(RegisterRequest r) throws UsernameTakenException, IncompleteRequestException {
        assertRequestComplete(r);
        if (userDataDAO.getUser(r.username()) != null) {
            throw new UsernameTakenException();
        }
        try {
            userDataDAO.createUser(new UserData(r.username(), r.password(), r.email()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String authToken = logUserIn(r.username());
        return new RegisterResult(r.username(), authToken);
    }

    public LoginResult login(LoginRequest r) throws WrongUsernameException, WrongPasswordException, IncompleteRequestException {
        assertRequestComplete(r);
        var user = userDataDAO.getUser(r.username());
        if (user == null) {
            throw new WrongUsernameException();
        }
        if (!user.password().equals(r.password())) {
            throw new WrongPasswordException();
        }
        String authToken = logUserIn(r.username());
        return new LoginResult(r.username(), authToken);
    }

    private String logUserIn(String username) {
        String authToken = generateToken();
        var authData = new AuthData(authToken, username);
        try {
            authDataDAO.addAuthData(authData);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return authToken;
    }

    private String generateToken() {
        String newAuthToken;
        do {
            newAuthToken = UUID.randomUUID().toString();
        } while (authDataDAO.getAuthData(newAuthToken) != null);
        return newAuthToken;
    }

    public LogoutResult logout(LogoutRequest r) throws NotLoggedInException, IncompleteRequestException {
        assertRequestComplete(r);
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
