package client;

import chess.ChessGame;
import org.junit.jupiter.api.*;
import requestresultrecords.*;
import server.Server;
import serverfacade.ServerFacade;
import usererrorexceptions.*;


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
        } catch (UserErrorException e) {
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
        } catch (UserErrorException e) {
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

            serverFacade.register(registerRequest1);
            assertThrows(UsernameTakenException.class, () -> serverFacade.register(registerRequest2));
        } catch (UserErrorException e) {
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
        } catch (UserErrorException e) {
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
            assertThrows(NotLoggedInException.class, () -> serverFacade.logout(new LogoutRequest("bad authToken")));
        } catch (UserErrorException e) {
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
        } catch (UserErrorException e) {
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
        } catch (UserErrorException e) {
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
        } catch (UserErrorException e) {
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
            assertThrows(NotLoggedInException.class, () -> serverFacade.createGame(createGameRequest));
        } catch (UserErrorException e) {
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
        } catch (UserErrorException e) {
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
        } catch (UserErrorException e) {
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
        } catch (UserErrorException e) {
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
            assertThrows(GameFullException.class, () -> serverFacade.joinGame(joinGameRequest));
        } catch (UserErrorException e) {
            throw new RuntimeException(e);
        }
    }
}
