package service;

import model.*;
import dataaccess.*;
import requestresult.RegisterRequest;
import requestresult.*;
import java.util.UUID;

public class UserService extends Service {
    public UserService() {}

    /**
     * Registers a user in the database.
     * @param registerRequest A record that holds the username, password, and email of the new user.
     * @return A record that holds a new AuthToken and the user's username.
     * @throws UsernameTakenException If the username is already in the database.
     * @throws IncompleteRequestException If any input fields are null.
     */
    public RegisterResult register(RegisterRequest registerRequest) throws UsernameTakenException, IncompleteRequestException {
        assertRequestComplete(registerRequest);
        if (userDataDAO.getUser(registerRequest.username()) != null) {
            throw new UsernameTakenException();
        }
        try {
            userDataDAO.createUser(new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String authToken = logUserIn(registerRequest.username());
        return new RegisterResult(registerRequest.username(), authToken);
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
        try {
            String newAuthToken;
            do {
                newAuthToken = UUID.randomUUID().toString();
            } while (authDataDAO.getAuthData(newAuthToken) != null);
            return newAuthToken;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public LogoutResult logout(LogoutRequest r) throws NotLoggedInException, IncompleteRequestException {
        assertRequestComplete(r);
        AuthData authData = verifyUser(r.authToken());
        try {
            authDataDAO.deleteAuthData(authData);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return new LogoutResult();
    }
}
