package websocket.commands;

import chess.ChessMove;
import java.util.Objects;
import static chess.ChessGame.TeamColor;
import static websocket.commands.UserGameCommand.CommandType.*;

/**
 * Represents a command a user can send the server over a websocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand {

    public final CommandType commandType;
    protected final String authToken;
    protected final Integer gameID;
    protected final TeamColor teamColor;
    protected final ChessMove move;

    public UserGameCommand(CommandType commandType, String authToken, Integer gameID) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
        teamColor = null;
        move = null;
    }

    public UserGameCommand(CommandType commandType, String authToken, Integer gameID, TeamColor teamColor) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
        this.teamColor = teamColor;
        move = null;
    }

    public UserGameCommand(String authToken, Integer gameID, ChessMove move) {
        this.commandType = MAKE_MOVE;
        this.authToken = authToken;
        this.move = move;
        this.gameID = gameID;
        teamColor = null;
    }

    public UserGameCommand(CommandType commandType, Integer gameID) {
        this.commandType = commandType;
        this.authToken = null;
        this.gameID = gameID;
        this.teamColor = null;
        this.move = null;
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

    public ChessMove getMove() {
        return move;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserGameCommand that)) {
            return false;
        }
        return getCommandType() == that.getCommandType() &&
                Objects.equals(getAuthToken(), that.getAuthToken()) &&
                Objects.equals(getGameID(), that.getGameID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthToken(), getGameID());
    }

    @Override
    public String toString() {
        return "UserGameCommand{" +
                "chessMove=" + move +
                ", teamColor=" + teamColor +
                ", gameID=" + gameID +
                ", authToken='" + authToken + '\'' +
                ", commandType=" + commandType +
                '}';
    }
}
