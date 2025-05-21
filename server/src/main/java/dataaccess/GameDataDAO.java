package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.ArrayList;

public class GameDataDAO {
    private final ArrayList<GameData> gameData = new ArrayList<>();

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
     * @param username the username of the creator
     * @param startAsWhite a boolean value indicating whether the user wants to start as white or not
     * @return the game ID of the new chess game
     */
    public int createGame(String gameName, String username, boolean startAsWhite) {
        String whiteUsername = (startAsWhite) ? username : null;
        String blackUsername = (startAsWhite) ? null : username;
        int gameID = generateGameID();
        gameData.add(new GameData(gameID, whiteUsername, blackUsername, gameName, new ChessGame()));
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
     * Adds a user to a chess game in the database.
     * @param gameID the game to be joined
     * @param username the username of the user who is joining the game
     * @throws DataAccessException if the game isn't found or the game is full
     */
    public void addUser(int gameID, String username) throws DataAccessException {
        for (var gameDatum : gameData) {
            if (gameDatum.gameID() != gameID) {
                continue;
            }
            if (gameDatum.blackUsername() == null) {
                GameData newGame = gameDatum.changeBlackUsername(username);
                gameData.remove(gameDatum);
                gameData.add(newGame);
                return;
            }
            if (gameDatum.whiteUsername() == null) {
                GameData newGame = gameDatum.changeWhiteUsername(username);
                gameData.remove(gameDatum);
                gameData.add(newGame);
                return;
            }
            throw new DataAccessException("Game already full!");
        }
        throw new DataAccessException("Game not found!");
    }

    /**
     * Clears all the games from the database
     */
    public void clear() {
        gameData.clear();
    }
}
