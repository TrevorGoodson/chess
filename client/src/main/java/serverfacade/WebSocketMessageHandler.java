package serverfacade;

import websocket.messages.ServerMessage;

public class WebSocketMessageHandler {
    public void sendMessage(ServerMessage serverMessage) {
        System.out.print(serverMessage.getMessage());
    }
}
