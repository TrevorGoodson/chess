package test.java.service;

import org.junit.jupiter.api.Test;
import requestresult.*;
import service.*;

import static chess.ChessGame.TeamColor.*;
import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    @Test
    public void createGame() {
        try {
            var gameService = new GameService();
            var userService = new UserService();
            RegisterResult r = userService.register(new RegisterRequest("Trevor", "123", ""));
            CreateGameResult g = gameService.createGame(new CreateGameRequest(r.authToken(), "Game 1"));
            System.out.println(g.gameID());
        } catch (IncompleteRequestException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createGameNotLoggedIn() {
        var gameService = new GameService();
        assertThrows(NotLoggedInException.class, () -> gameService.createGame(new CreateGameRequest("Wrong authToken", "Game 1")));
    }

    @Test
    public void listGames() {
        try {
            var gameService = new GameService();
            var userService = new UserService();
            var clearService = new ClearService();
            clearService.clear();
            RegisterResult r = userService.register(new RegisterRequest("Heather", "123", ""));
            gameService.createGame(new CreateGameRequest(r.authToken(), "Game 1"));
            gameService.createGame(new CreateGameRequest(r.authToken(), "Game 2"));
            gameService.createGame(new CreateGameRequest(r.authToken(), "Game 3"));
            gameService.createGame(new CreateGameRequest(r.authToken(), "Game 4"));
            ListResult l = gameService.listGames(new ListRequest(r.authToken()));
            assertEquals(4, l.games().size());
        } catch (IncompleteRequestException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void listGamesNotLoggedIn() {
        var gameService = new GameService();
        assertThrows(NotLoggedInException.class, () -> gameService.listGames(new ListRequest("Wrong authToken")));
    }

    @Test
    public void joinGame() {
        try {
            var userService = new UserService();
            var gameService = new GameService();
            RegisterResult r1 = userService.register(new RegisterRequest("Gavin", "123", ""));
            RegisterResult r2 = userService.register(new RegisterRequest("Ian", "123", ""));
            CreateGameResult g = gameService.createGame(new CreateGameRequest(r1.authToken(), "Game 1"));
            gameService.joinGame(new JoinGameRequest(r1.authToken(), WHITE, g.gameID()));
            gameService.joinGame(new JoinGameRequest(r2.authToken(), BLACK, g.gameID()));
            ListResult l = gameService.listGames(new ListRequest(r1.authToken()));
            System.out.println(l.games());
        } catch (IncompleteRequestException e) {
            throw new RuntimeException(e);
        }
    }
}
