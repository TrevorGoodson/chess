package test.java.service;

import org.junit.jupiter.api.Test;
import requestresult.LoginRequest;
import requestresult.LogoutRequest;
import requestresult.RegisterRequest;
import requestresult.RegisterResult;
import service.NotLoggedInException;
import service.UserService;
import service.UsernameTakenException;
import service.WrongPasswordException;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {

    @Test
    public void registerNewUser() {
        RegisterResult r = new UserService().register(new RegisterRequest("Trevor", "FrogLog", "t@gmail.com"));
        assertEquals("Trevor", r.authData().username());
        System.out.println("Auth token:" + r.authData().authToken());
    }

    @Test
    public void reRegisterUser() {
        var userService = new UserService();
        RegisterResult r1 = userService.register(new RegisterRequest("Trevor", "1234", "a@b.com"));
        assertThrows(UsernameTakenException.class, () -> {userService.register(new RegisterRequest("Trevor", "abcd", "b@a.com"));});
    }

    @Test
    public void logout() {
        var userService = new UserService();
        RegisterResult r = userService.register(new RegisterRequest("Trevor", "FrogLog", ""));
        userService.logout(new LogoutRequest(r.authData().authToken()));
    }

    @Test
    public void logoutButNotLoggedIn() {
        var userService = new UserService();
        assertThrows(NotLoggedInException.class, () -> userService.logout(new LogoutRequest("Not a real authToken")));
    }

    @Test
    public void login() {
        var userService = new UserService();
        RegisterResult r = userService.register(new RegisterRequest("Trevor", "FrogLog", ""));
        userService.logout(new LogoutRequest(r.authData().authToken()));
        userService.login(new LoginRequest("Trevor", "FrogLog"));
    }

    @Test
    public void loginWrongPassword() {
        var userService = new UserService();
        RegisterResult r = userService.register(new RegisterRequest("Trevor", "FrogLog", ""));
        userService.logout(new LogoutRequest(r.authData().authToken()));
        assertThrows(WrongPasswordException.class, () -> userService.login(new LoginRequest("Trevor", "Wrong Password!")));
    }
}