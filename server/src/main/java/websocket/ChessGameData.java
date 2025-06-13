package websocket;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;

import java.util.*;

public class ChessGameData {
    String whiteUsername;
    String blackUsername;
    Connection whiteConnection;
    Connection blackConnection;
    ChessGame chessGame;
    Map<Session, Connection> observers = new HashMap<>();

    public ChessGameData(String whiteUsername, String blackUsername, Connection whiteConnection, Connection blackConnection, ChessGame chessGame) {
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.whiteConnection = whiteConnection;
        this.blackConnection = blackConnection;
        this.chessGame = chessGame;
    }

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

    public void setWhiteConnection(Connection whiteConnection) {
        this.whiteConnection = whiteConnection;
    }

    public void setBlackConnection(Connection blackConnection) {
        this.blackConnection = blackConnection;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public Connection getWhiteConnection() {
        return whiteConnection;
    }

    public Connection getBlackConnection() {
        return blackConnection;
    }

    public ChessGame getChessGame() {
        return chessGame;
    }
}
