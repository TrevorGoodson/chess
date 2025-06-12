package websocket;

public abstract class WebSocketMessage {
    public messageType type;

    public enum messageType {
        USER_GAME,
        SERVER_MESSAGE
    }
}
