package dataaccess;

import chess.ChessGame;
import model.GameData;
import chess.ChessGame.TeamColor;
import static chess.ChessGame.TeamColor.*;
import java.util.*;

public class GameDataDAOMemory implements GameDataDAO{
    private static ArrayList<GameData> gameData = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public List<GameData> getAllGames() {
        return gameData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int createGame(String gameName) {
        int gameID = generateGameID();
        gameData.add(new GameData(gameID, null, null, gameName, new ChessGame()));
        return gameID;
    }

    /**
     * {@inheritDoc}
     */
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
     * {@inheritDoc}
     */
    @Override
    public GameData findGame(int gameID) {
        for (var gameDatum : gameData) {
            if (gameDatum.gameID() == gameID) {
                return gameDatum;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        gameData.clear();
    }
}
