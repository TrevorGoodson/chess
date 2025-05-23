package dataaccess;

import chess.ChessGame;
import model.GameData;
import chess.ChessGame.TeamColor;
import static chess.ChessGame.TeamColor.*;
import java.util.ArrayList;

public class GameDataDAO {
    private final static ArrayList<GameData> gameData = new ArrayList<>();

    public GameDataDAO() {}

    /**
     * Retrieves all chess games from database.
     * @return All chess games as ArrayList<GameData>
     */
    public ArrayList<GameData> getAllGames() {
        return gameData;
    }

    /**
     * Creates a new game and adds it to the database.
     * @param gameName the name of the game
     * @return the game ID of the new chess game
     */
    public int createGame(String gameName) {
        int gameID = generateGameID();
        gameData.add(new GameData(gameID, null, null, gameName, new ChessGame()));
        return gameID;
    }

    private int generateGameID() {
        int maxID = 0;
        for (var gameDatum : gameData) {
            if (gameDatum.gameID() > maxID) {
                maxID = gameDatum.gameID();
            }
        }
        return maxID + 1;
    }

    /**
     * Finds a chess game in the database.
     * @param gameID the game ID
     * @return the corresponding GameData or null if the game is not found
     */
    public GameData findGame(int gameID) {
        for (var gameDatum : gameData) {
            if (gameDatum.gameID() == gameID) {
                return gameDatum;
            }
        }
        return null;
    }

    /**
     * Adds a user to a chess game
     * @param gameID of the chess game
     * @param username of the user
     * @param color that the user wants to play as
     * @throws DataAccessException if the gameID doesn't match any in the database or there's already a player playing that color
     */
    public void addUser(int gameID, String username, TeamColor color) throws DataAccessException {
        var game = findGame(gameID);
        if (game == null) {
            throw new DataAccessException("Game not found!");
        }
        var player = switch (color) {
            case WHITE -> game.whiteUsername();
            case BLACK -> game.blackUsername();
        };
        if (player != null) {
            throw new DataAccessException("Game already full!");
        }
        String whiteUser = (color == WHITE) ? username : game.whiteUsername();
        String blackUser = (color == BLACK) ? username : game.blackUsername();
        var newGame = new GameData(gameID, whiteUser, blackUser, game.gameName(), game.game());
        gameData.remove(game);
        gameData.add(newGame);
    }

    /**
     * Clears all the games from the database
     */
    public void clear() {
        gameData.clear();
    }
}
