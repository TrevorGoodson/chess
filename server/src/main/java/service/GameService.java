package service;

import dataaccess.*;
import model.GameData;
import requestresult.*;

import java.util.ArrayList;

public class GameService extends Service {
    public GameService() {}

    public CreateGameResult createGame(CreateGameRequest c) throws NotLoggedInException, IncompleteRequestException {
        assertRequestComplete(c);
        var authData = authDataDAO.getAuthData(c.authToken());
        if (authData == null) {
            throw new NotLoggedInException();
        }
        return new CreateGameResult(gameDataDAO.createGame(c.gameName()));
    }

    public JoinGameResult joinGame(JoinGameRequest j) throws NotLoggedInException, GameNotFoundException, IncompleteRequestException {
        assertRequestComplete(j);
        var authData = authDataDAO.getAuthData(j.authToken());
        if (authData == null) {
            throw new NotLoggedInException();
        }
        GameData game = gameDataDAO.findGame(j.gameID());
        if (game == null) {
            throw new GameNotFoundException();
        }
        String currentUser = switch (j.playerColor()) {
            case WHITE -> game.whiteUsername();
            case BLACK -> game.blackUsername();
        };
        if (currentUser != null) {
            throw new GameFullException();
        }
        try {
            gameDataDAO.addUser(j.gameID(), authData.username(), j.playerColor());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return new JoinGameResult();
    }

    public ListResult listGames(ListRequest l) throws NotLoggedInException, IncompleteRequestException {
        assertRequestComplete(l);
        if (authDataDAO.getAuthData(l.authToken()) == null) {
            throw new NotLoggedInException();
        }
        ArrayList<GameData> games = gameDataDAO.getAllGames();
        var listResult = new ListResult(new ArrayList<>());
        for (var game : games) {
            listResult.games().add(new ListSingleGame(game.gameID(),
                                                      game.gameName(),
                                                      game.whiteUsername(),
                                                      game.blackUsername()));
        }
        return listResult;
    }
}
