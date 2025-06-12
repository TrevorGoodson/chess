package serverfacade;

import websocket.messages.ServerMessage;

public class WebSocketMessageHandler {
    public void sendMessage(ServerMessage serverMessage) {
        System.out.println(serverMessage.getMessage());
    }
}
