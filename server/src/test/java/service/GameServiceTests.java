package test.java.service;

import org.junit.jupiter.api.Test;
import requestresult.*;
import service.*;
import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    @Test
    public void createGame() {
        var gameService = new GameService();
        var userService = new UserService();
        RegisterResult r = userService.register(new RegisterRequest("Trevor", "123", ""));
        CreateGameResult g = gameService.createGame(new CreateGameRequest(r.authData().authToken(), "Game 1"));
        System.out.println(g.gameID());
    }
}
