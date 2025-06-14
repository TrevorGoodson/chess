package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requestresultrecords.*;
import usererrorexceptions.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    @BeforeEach
    public void clearData() {
        try {
            new ClearService().clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void registerNewUser() {
        try {
            RegisterResult r = new UserService().register(new RegisterRequest("Ian", "FrogLog", "t@gmail.com"));
            assertEquals("Ian", r.username());
            System.out.println("Auth token:" + r.authToken());
        } catch (UserErrorException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void reRegisterUser() {
        try {
            var userService = new UserService();
            RegisterResult r1 = userService.register(new RegisterRequest("Gavin", "1234", "a@b.com"));
            assertThrows(UsernameTakenException.class, () -> {
                userService.register(new RegisterRequest("Gavin", "abcd", "b@a.com"));
            });
        } catch (UserErrorException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void logout() {
        try {
            var userService = new UserService();
            RegisterResult r = userService.register(new RegisterRequest("Miles", "FrogLog", ""));
            userService.logout(new LogoutRequest(r.authToken()));
        } catch (UserErrorException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void logoutButNotLoggedIn() {
        var userService = new UserService();
        assertThrows(NotLoggedInException.class, () -> userService.logout(new LogoutRequest("Not a real authToken")));
    }

    @Test
    public void login() {
        try {
            var userService = new UserService();
            RegisterResult r = userService.register(new RegisterRequest("Bartholomew", "FrogLog", ""));
            userService.logout(new LogoutRequest(r.authToken()));
            userService.login(new LoginRequest("Bartholomew", "FrogLog"));
        } catch (UserErrorException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void loginWrongPassword() {
        try {
            var userService = new UserService();
            RegisterResult r = userService.register(new RegisterRequest("Jeanette", "FrogLog", ""));
            userService.logout(new LogoutRequest(r.authToken()));
            assertThrows(WrongPasswordException.class, () -> userService.login(new LoginRequest("Jeanette", "Wrong Password!")));
        } catch (UserErrorException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}