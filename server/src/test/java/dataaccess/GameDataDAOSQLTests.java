package dataaccess;

import chess.*;
import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.Test;

import static chess.ChessGame.TeamColor.*;
import static org.junit.jupiter.api.Assertions.*;

public class GameDataDAOSQLTests {
    GameDataDAOSQL gameDataDAO = new GameDataDAOSQL();
    @Test
    public void clearTest() {
        try {
            gameDataDAO.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void chessGameSerializationTest() {
        ChessGame game = new ChessGame();
        ChessGame game2 = gameDataDAO.deSerializeGame(gameDataDAO.serializeGame(game));
        assertEquals(game, game2);
        try {
            ChessMove move = new ChessMove(new ChessPosition(2, 1), new ChessPosition(4, 1));
            game.makeMove(move);
            game2 = gameDataDAO.deSerializeGame(gameDataDAO.serializeGame(game));
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }
        assertEquals(game, game2);
    }

    @Test
    public void createGameTest() {
        try {
            gameDataDAO.clear();
            int gameID = gameDataDAO.createGame("sample_game_name");
            assertNotEquals(0, gameID);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createGameDuplicate() {
        try {
            gameDataDAO.clear();
            int gameID1 = gameDataDAO.createGame("sample_game_name");
            int gameID2 = gameDataDAO.createGame("sample_game_name");
            int gameID3 = gameDataDAO.createGame("sample_game_name2");
            assertNotEquals(gameID1, gameID2);
            assertNotEquals(gameID1, gameID3);
            assertNotEquals(gameID3, gameID2);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void selectGameTest() {
        try {
            gameDataDAO.clear();
            int gameID = gameDataDAO.createGame("sample_game_name");
            GameData game = gameDataDAO.findGame(gameID);
            assertNotNull(game);
            assertEquals("sample_game_name", game.gameName());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void badSelectGameTest() {
        try {
            gameDataDAO.clear();
            int BAD_GAME_ID = 1;
            GameData game = gameDataDAO.findGame(BAD_GAME_ID);
            assertNull(game);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void selectAllGamesTest() {
        try {
            gameDataDAO.clear();
            for (int i = 0; i < 5; ++i) {
                gameDataDAO.createGame("sample_game_name_" + i);
            }
            assertEquals(5, gameDataDAO.getAllGames().size());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void selectALlGamesEmpty() {
        try {
            gameDataDAO.clear();
            assertEquals(0, gameDataDAO.getAllGames().size());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void addUserTest() {
        try {
            gameDataDAO.clear();
            int gameID = gameDataDAO.createGame("sample_game_name");
            gameDataDAO.addUser(gameID, "username1", WHITE);
            gameDataDAO.addUser(gameID, "username2", BLACK);
            assertNotNull(gameDataDAO.findGame(gameID).whiteUsername());
            assertNotNull(gameDataDAO.findGame(gameID).blackUsername());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void reAddUserTest() {
        try {
            gameDataDAO.clear();
            int gameID = gameDataDAO.createGame("sample_game_name");
            gameDataDAO.addUser(gameID, "username1", WHITE);
            assertThrows(DataAccessException.class, () -> gameDataDAO.addUser(gameID, "username2", WHITE));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
