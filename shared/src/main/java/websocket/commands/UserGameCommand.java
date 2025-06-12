package websocket.commands;

import chess.ChessGame;
import chess.ChessMove;
import websocket.WebSocketMessage;

import java.util.Objects;

import static websocket.WebSocketMessage.messageType.USER_GAME;
import static chess.ChessGame.TeamColor;

/**
 * Represents a command a user can send the server over a websocket
 *
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand extends WebSocketMessage {

    public final CommandType commandType;
    protected final String authToken;
    protected final Integer gameID;
    protected final TeamColor teamColor;
    protected final ChessMove chessMove;

    public UserGameCommand(CommandType commandType, String authToken, Integer gameID) {
        super.type = USER_GAME;
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
        teamColor = null;
        chessMove = null;
    }

    public UserGameCommand(CommandType commandType, String authToken, Integer gameID, TeamColor teamColor) {
        super.type = USER_GAME;
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
        this.teamColor = teamColor;
        chessMove = null;
    }

    public UserGameCommand(CommandType commandType, String authToken, Integer gameID, TeamColor teamColor, ChessMove chessMove) {
        super.type = USER_GAME;
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
        this.teamColor = teamColor;
        this.chessMove = chessMove;
    }

    public enum CommandType {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Integer getGameID() {
        return gameID;
    }

    public TeamColor getTeamColor() {
        return teamColor;
    }

    public ChessMove getChessMove() {
        return chessMove;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserGameCommand)) {
            return false;
        }
        UserGameCommand that = (UserGameCommand) o;
        return getCommandType() == that.getCommandType() &&
                Objects.equals(getAuthToken(), that.getAuthToken()) &&
                Objects.equals(getGameID(), that.getGameID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthToken(), getGameID());
    }
}
