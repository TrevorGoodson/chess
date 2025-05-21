package model;

import chess.ChessGame;

public record GameData(int gameID,
                       String whiteUsername,
                       String blackUsername,
                       String gameName,
                       ChessGame game) {

    /**
     * Creates a new GameData with an updated username for white
     * @param username the new username
     * @return the new game data
     */
    public GameData changeWhiteUsername(String username) {
        return new GameData(gameID, username, blackUsername, gameName, game);
    }

    /**
     * Creates a new GameData with an updated username for black
     * @param username the new username
     * @return the new game data
     */
    public GameData changeBlackUsername(String username) {
        return new GameData(gameID, whiteUsername, username, gameName, game);
    }
}
