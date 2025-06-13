package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;

public record Connection(String username, Session session) {
    public void send(ServerMessage message) throws IOException {
        session.getRemote().sendString(new Gson().toJson(message));
    }
}