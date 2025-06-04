package service;

import model.*;
import dataaccess.*;
import org.mindrot.jbcrypt.BCrypt;
import requestresultrecords.RegisterRequest;
import requestresultrecords.*;
//import service.exceptions.*;
import usererrorexceptions.*;

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
    public RegisterResult register(RegisterRequest registerRequest) throws UsernameTakenException, IncompleteRequestException, DataAccessException {
        assertRequestComplete(registerRequest);
        if (userDataDAO.getUser(registerRequest.username()) != null) {
            throw new UsernameTakenException();
        }
        String hashedPassword = hashPassword(registerRequest.password());
        userDataDAO.createUser(new UserData(registerRequest.username(), hashedPassword, registerRequest.email()));
        String authToken = logUserIn(registerRequest.username());
        return new RegisterResult(registerRequest.username(), authToken);
    }

    public LoginResult login(LoginRequest r) throws WrongUsernameException,
                                                    WrongPasswordException,
                                                    IncompleteRequestException,
                                                    DataAccessException {
        assertRequestComplete(r);
        UserData user = userDataDAO.getUser(r.username());
        if (user == null) {
            throw new WrongUsernameException();
        }
        if (!BCrypt.checkpw(r.password(), user.password())) {
            throw new WrongPasswordException();
        }
        String authToken = logUserIn(r.username());
        return new LoginResult(r.username(), authToken);
    }

    private String logUserIn(String username) throws DataAccessException {
        String authToken = generateToken();
        var authData = new AuthData(authToken, username);
        authDataDAO.addAuthData(authData);
        return authToken;
    }

    public static void main(String[] args) {
        String password1 = UserService.hashPassword("test_password");
        String password2 = UserService.hashPassword("test_password");
        System.out.println(password1);
        System.out.println(password2);
        System.out.println(BCrypt.checkpw("test_password", password1));
    }

    private static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private String generateToken() throws DataAccessException {
        String newAuthToken;
        do {
            newAuthToken = UUID.randomUUID().toString();
        } while (authDataDAO.getAuthData(newAuthToken) != null);
        return newAuthToken;
    }

    public LogoutResult logout(LogoutRequest r) throws NotLoggedInException, IncompleteRequestException, DataAccessException {
        assertRequestComplete(r);
        AuthData authData = verifyUser(r.authToken());
        authDataDAO.deleteAuthData(authData);
        return new LogoutResult();
    }
}
