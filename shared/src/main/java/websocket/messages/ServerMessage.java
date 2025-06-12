package websocket.messages;

import websocket.WebSocketMessage;

import java.util.Objects;

import static websocket.WebSocketMessage.messageType.SERVER_MESSAGE;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage extends WebSocketMessage {
    ServerMessageType serverMessageType;
    String message;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION,
        LOGIN
    }

    public ServerMessage(ServerMessageType type) {
        super.type = SERVER_MESSAGE;
        this.serverMessageType = type;
    }

    public ServerMessage(ServerMessageType serverMessageType, String message) {
        super.type = SERVER_MESSAGE;
        this.serverMessageType = serverMessageType;
        this.message = message;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage that)) {
            return false;
        }
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
