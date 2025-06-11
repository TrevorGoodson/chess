package serverfacade;

import com.google.gson.Gson;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    WebSocketMessageHandler notificationHandler;

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

    public WebSocketFacade(String url, WebSocketMessageHandler webSocketMessageHandler) throws ConnectionException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = webSocketMessageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler((MessageHandler.Whole<String>) message -> {
                ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                webSocketMessageHandler.sendMessage(serverMessage);
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ConnectionException(ex.getMessage());
        }
    }
}
