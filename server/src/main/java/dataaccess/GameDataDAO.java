package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.List;

import static chess.ChessGame.TeamColor;

public interface GameDataDAO {
    /**
     * Retrieves all chess games from database.
     * @return All chess games as ArrayList<GameData>
     */
    List<GameData> getAllGames() throws DataAccessException;

    /**
     * Creates a new game and adds it to the database.
     * @param gameName the name of the game
     * @return the game ID of the new chess game
     */
    int createGame(String gameName) throws DataAccessException;

    /**
     * Finds a chess game in the database.
     * @param gameID the game ID
     * @return the corresponding GameData or null if the game is not found
     */
    GameData findGame(int gameID) throws DataAccessException;

    /**
     * Adds a user to a chess game
     * @param gameID of the chess game
     * @param username of the user
     * @param color that the user wants to play as
     * @throws DataAccessException if the gameID doesn't match any in the database or there's already a player playing that color
     */
    void addUser(int gameID, String username, ChessGame.TeamColor color) throws DataAccessException;

    /**
     * Clears all the games from the database
     */
    void clear() throws DataAccessException;

    void updateGame(int gameID, ChessGame chessGame) throws DataAccessException;

    void removeUser(int gameID, TeamColor color) throws DataAccessException;
}
