package service;

import dataaccess.*;
import model.GameData;
import requestresult.*;

public class GameService {
    AuthDataDAO authDataDAO = new AuthDataDAO();
    GameDataDAO gameDataDAO = new GameDataDAO();

    public GameService() {}

    public CreateGameResult createGame(CreateGameRequest c) throws NotLoggedInException {
        var authData = authDataDAO.getAuthData(c.authToken());
        if (authData == null) {
            throw new NotLoggedInException();
        }
        return new CreateGameResult(gameDataDAO.createGame(c.gameName()));
    }

    public JoinGameResult joinGame(JoinGameRequest j) throws NotLoggedInException, GameNotFoundException{
        var authData = authDataDAO.getAuthData(j.authToken());
        if (authData == null) {
            throw new NotLoggedInException();
        }
        GameData game = gameDataDAO.findGame(j.gameID());
        if (game == null) {
            throw new GameNotFoundException();
        }
        try {
            gameDataDAO.addUser(j.gameID(), authData.username(), j.playerColor());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return new JoinGameResult();
    }

    public ListResult listGames(ListRequest l) throws NotLoggedInException {
        if (authDataDAO.getAuthData(l.authToken()) == null) {
            throw new NotLoggedInException();
        }
        return new ListResult(gameDataDAO.getAllGames());
    }
}
