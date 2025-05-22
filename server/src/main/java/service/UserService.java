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

    public RegisterResult register(RegisterRequest r) throws UsernameTakenException {
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
        return new RegisterResult(authData);
    }

    private String generateToken() {
        String newAuthToken;
        do {
            newAuthToken = UUID.randomUUID().toString();
        } while (authDataDAO.getAuthData(newAuthToken) == null);
        return newAuthToken;
    }

    public LoginResult login(LoginRequest r) throws WrongUsernameException, WrongPasswordException {
        var user = userDataDAO.getUser(r.username());
        if (user == null) {
            throw new WrongUsernameException();
        }
        if (user.password().equals(r.password())) {
            throw new WrongPasswordException();
        }
        return new LoginResult(r.username(), generateToken());
    }

    public LogoutResult logout(LogoutRequest r) throws NotLoggedInException {
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
