package client;

import exceptions.ResponseException;
import org.junit.jupiter.api.*;
import requestresultrecords.RegisterRequest;
import requestresultrecords.RegisterResult;
import server.Server;
import serverfacade.ServerFacade;

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
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

}
