package WebSocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String username, Session session) {
        var connection = new Connection(username, session);
        connections.put(username, connection);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

    public void broadcast(String excludeUsername, ServerMessage serverMessage) throws IOException {
        cleanUpConnections();

        for (var connection : connections.values()) {
            if (!connection.username().equals(excludeUsername)) {
                connection.send(serverMessage);
            }
        }
    }

    private void cleanUpConnections() {
        var removeList = new ArrayList<Connection>();

        for (var connection : connections.values()) {
            if (!connection.session().isOpen()) {
                removeList.add(connection);
            }
        }

        for (var connection : removeList) {
            connections.remove(connection.username());
        }
    }
}
