package client;

import exceptions.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.ServerFacade;


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
    public void clearTest() {
        try {
            int port = server.port();
            new ServerFacade(port).clear();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

}
