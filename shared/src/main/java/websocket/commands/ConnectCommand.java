package websocket.commands;

import static websocket.commands.UserGameCommand.CommandType.CONNECT;

public class ConnectCommand extends UserGameCommand {
    public ConnectCommand(String authToken, Integer gameID) {
        super(CONNECT, authToken, gameID);
    }
}
