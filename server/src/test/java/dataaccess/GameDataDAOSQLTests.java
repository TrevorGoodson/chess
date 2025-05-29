package test.java.dataaccess;

import chess.*;
import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.Test;

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
}
