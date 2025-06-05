package service;

import dataaccess.DataAccessException;
import dataaccess.GameDataDAOSQL;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import requestresultrecords.*;
import usererrorexceptions.*;

import static chess.ChessGame.TeamColor.*;
import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    @BeforeAll
    public static void clearGames() {
        try {
            new GameDataDAOSQL().clear();
            new GameDataDAOSQL().clear();
            new GameDataDAOSQL().clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createGame() {
        try {
            var gameService = new GameService();
            var userService = new UserService();
            RegisterResult r = userService.register(new RegisterRequest("Trevor", "123", ""));
            CreateGameResult g = gameService.createGame(new CreateGameRequest(r.authToken(), "Game 1"));
            System.out.println(g.gameID());
        } catch (UserErrorException | DataAccessException e) {
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
        } catch (UserErrorException | DataAccessException e) {
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
        } catch (UserErrorException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void joinGameButTaken() {
        try {
            var userService = new UserService();
            var gameService = new GameService();
            RegisterResult r1 = userService.register(new RegisterRequest("Nick", "123", ""));
            RegisterResult r2 = userService.register(new RegisterRequest("Robert", "123", ""));
            CreateGameResult g = gameService.createGame(new CreateGameRequest(r1.authToken(), "Game 1"));
            gameService.joinGame(new JoinGameRequest(r1.authToken(), WHITE, g.gameID()));
            assertThrows(GameFullException.class, () ->gameService.joinGame(new JoinGameRequest(r2.authToken(), WHITE, g.gameID())));
        } catch (UserErrorException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
