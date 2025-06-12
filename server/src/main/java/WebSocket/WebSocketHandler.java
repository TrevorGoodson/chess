package WebSocket;

import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDataDAO;
import dataaccess.AuthDataDAOSQL;
import dataaccess.DataAccessException;
import model.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final GameManager games = new GameManager();
    private final AuthDataDAO authDataDAO = new AuthDataDAOSQL();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch(userGameCommand.getCommandType()) {
            case CONNECT -> {
                handleConnectCommand(userGameCommand, session);
            }
            case MAKE_MOVE -> {
                handleMakeMove(userGameCommand, session);
            }
            case LEAVE -> {
            }
            case RESIGN -> {
            }
        }


    }

    private void handleConnectCommand(UserGameCommand userGameCommand, Session session) throws DataAccessException, IOException {
        AuthData authData = authDataDAO.getAuthData(userGameCommand.getAuthToken());
        String username = authData.username();
        games.addPlayer(username, userGameCommand.getGameID(), userGameCommand.getTeamColor(), session);
        games.notifyGame(userGameCommand.getGameID(), username + " joined the game.");
    }

    private void handleMakeMove(UserGameCommand userGameCommand, Session session) throws DataAccessException, IOException {
        AuthData authData = authDataDAO.getAuthData(userGameCommand.getAuthToken());
        String username = authData.username();
        try {
            games.makeMove(userGameCommand.getGameID(), userGameCommand.getTeamColor(), userGameCommand.getChessMove());
        } catch (InvalidMoveException e) {
            games.notifyPlayer(userGameCommand.getGameID(), userGameCommand.getTeamColor(), "Invalid move");
        }
    }
}
