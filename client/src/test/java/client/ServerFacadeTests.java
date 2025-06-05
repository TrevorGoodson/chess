package client;

import chess.ChessGame;
import exceptions.ResponseException;
import org.junit.jupiter.api.*;
import requestresultrecords.*;
import server.Server;
import serverfacade.ServerFacade;
import usererrorexceptions.UsernameTakenException;
import usererrorexceptions.WrongUsernameException;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static final int OS_ASSIGNED_PORT = 0;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(OS_ASSIGNED_PORT);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    @Order(1)
    public void clearTest() {
        try {
            int port = server.port();
            new ServerFacade(port).clear();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(2)
    public void registerTest() {
        try {
            int port = server.port();
            var serverFacade = new ServerFacade(port);
            serverFacade.clear();
            RegisterRequest registerRequest = new RegisterRequest("Trevor", "1234", "");
            RegisterResult registerResult = serverFacade.register(registerRequest);
            assertEquals(registerRequest.username(), registerResult.username());
            assertNotNull(registerResult.authToken());
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(3)
    public void registerTestDuplicateUsername() {
        try {
            int port = server.port();
            var serverFacade = new ServerFacade(port);
            serverFacade.clear();
            RegisterRequest registerRequest1 = new RegisterRequest("Trevor", "1234", "1");
            RegisterRequest registerRequest2 = new RegisterRequest("Trevor", "5678", "2");

            RegisterResult registerResult = serverFacade.register(registerRequest1);
            assertThrows(UsernameTakenException.class, () -> serverFacade.register(registerRequest2));
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(4)
    public void logoutTest() {
        try {
            int port = server.port();
            var serverFacade = new ServerFacade(port);
            serverFacade.clear();
            var registerRequest = new RegisterRequest("Trevor", "1234", "1");
            RegisterResult registerResult = serverFacade.register(registerRequest);
            var logoutRequest = new LogoutRequest(registerResult.authToken());
            serverFacade.logout(logoutRequest);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(5)
    public void logoutNotLoggedInTest() {
        try {
            int port = server.port();
            var serverFacade = new ServerFacade(port);
            serverFacade.clear();
            assertThrows(ResponseException.class, () -> serverFacade.logout(new LogoutRequest("bad authToken")));
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(6)
    public void loginTest() {
        try {
            int port = server.port();
            var serverFacade = new ServerFacade(port);
            serverFacade.clear();
            var registerRequest = new RegisterRequest("Trevor", "1234", "1");
            RegisterResult registerResult = serverFacade.register(registerRequest);
            var logoutRequest = new LogoutRequest(registerResult.authToken());
            serverFacade.logout(logoutRequest);
            var loginRequest = new LoginRequest("Trevor", "1234");
            LoginResult loginResult = serverFacade.login(loginRequest);
            assertNotNull(loginResult.authToken());
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(7)
    public void loginBadUsernameTest() {
        try {
            int port = server.port();
            var serverFacade = new ServerFacade(port);
            serverFacade.clear();
            assertThrows(WrongUsernameException.class, () -> serverFacade.login(new LoginRequest("bad username", "equally bad password")));
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(8)
    public void createGameTest() {
        try {
            int port = server.port();
            var serverFacade = new ServerFacade(port);
            serverFacade.clear();
            RegisterRequest registerRequest = new RegisterRequest("Trevor", "1234", "1");
            RegisterResult registerResult = serverFacade.register(registerRequest);
            CreateGameRequest createGameRequest = new CreateGameRequest(registerResult.authToken(), "game1");
            CreateGameResult createGameResult = serverFacade.createGame(createGameRequest);
            assertNotNull(createGameResult);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(9)
    public void createGameNotLoggedInTest() {
        try {
            int port = server.port();
            var serverFacade = new ServerFacade(port);
            serverFacade.clear();
            CreateGameRequest createGameRequest = new CreateGameRequest("bad authToken", "game1");
            assertThrows(ResponseException.class, () -> serverFacade.createGame(createGameRequest));
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(9)
    public void listGamesTest() {
        try {
            int port = server.port();
            var serverFacade = new ServerFacade(port);
            serverFacade.clear();
            RegisterRequest registerRequest = new RegisterRequest("Trevor", "1234", "1");
            RegisterResult registerResult = serverFacade.register(registerRequest);
            String authToken = registerResult.authToken();
            CreateGameRequest createGameRequest = new CreateGameRequest(authToken, "game1");
            serverFacade.createGame(createGameRequest);
            var listRequest = new ListRequest(authToken);
            ListResult listResult = serverFacade.listGames(listRequest);
            assertNotEquals(0, listResult.games().size());
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(10)
    public void listNoGamesTest() {
        try {
            int port = server.port();
            var serverFacade = new ServerFacade(port);
            serverFacade.clear();
            RegisterRequest registerRequest = new RegisterRequest("Trevor", "1234", "1");
            RegisterResult registerResult = serverFacade.register(registerRequest);
            String authToken = registerResult.authToken();
            var listRequest = new ListRequest(authToken);
            ListResult listResult = serverFacade.listGames(listRequest);
            assertEquals(0, listResult.games().size());
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(11)
    public void joinGameTest() {
        try {
            int port = server.port();
            var serverFacade = new ServerFacade(port);
            serverFacade.clear();
            RegisterRequest registerRequest = new RegisterRequest("Trevor", "1234", "1");
            RegisterResult registerResult = serverFacade.register(registerRequest);
            String authToken = registerResult.authToken();
            var createGameRequest = new CreateGameRequest(authToken, "game1");
            CreateGameResult createGameResult = serverFacade.createGame(createGameRequest);
            JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, ChessGame.TeamColor.WHITE, createGameResult.gameID());
            assertNotNull(serverFacade.joinGame(joinGameRequest));
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(12)
    public void joinFullGameTest() {
        try {
            int port = server.port();
            var serverFacade = new ServerFacade(port);
            serverFacade.clear();
            RegisterRequest registerRequest = new RegisterRequest("Trevor", "1234", "1");
            RegisterResult registerResult = serverFacade.register(registerRequest);
            String authToken = registerResult.authToken();
            var createGameRequest = new CreateGameRequest(authToken, "game1");
            CreateGameResult createGameResult = serverFacade.createGame(createGameRequest);
            JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, ChessGame.TeamColor.WHITE, createGameResult.gameID());
            serverFacade.joinGame(joinGameRequest);
            assertThrows(ResponseException.class, () -> serverFacade.joinGame(joinGameRequest));
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }
}
