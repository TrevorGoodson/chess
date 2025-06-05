package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import requestresultrecords.*;
import usererrorexceptions.*;

import java.util.ArrayList;
import java.util.List;
import static chess.ChessGame.TeamColor.*;

public class GameService extends Service {
    public GameService() {}

    /**
     * Creates a chess game. Does not add the creating user as a player.
     * @param createGameRequest A record that holds the AuthToken of user and the name of the game to be created
     * @return A record that holds the game ID
     * @throws NotLoggedInException If the user is not logged in
     * @throws IncompleteRequestException If any input fields are null
     */
    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws UserErrorException, DataAccessException {
        assertRequestComplete(createGameRequest);
        verifyUser(createGameRequest.authToken());
        int gameID = gameDataDAO.createGame(createGameRequest.gameName());
        return new CreateGameResult(gameID);
    }

    /**
     * Adds a user to a chess game.
     * @param joinRequest A record that holds an AuthToken, the team to be joined (chess.ChessGame.TeamColor), and the ID of the desired game.
     * @return An empty record.
     * @throws NotLoggedInException If the user is not logged in.
     * @throws GameNotFoundException If the game ID is invalid
     * @throws IncompleteRequestException If any input fields are null.
     */
    public JoinGameResult joinGame(JoinGameRequest joinRequest) throws UserErrorException, DataAccessException {
        assertRequestComplete(joinRequest);
        AuthData authData = verifyUser(joinRequest.authToken());

        GameData game= gameDataDAO.findGame(joinRequest.gameID());
        if (game == null) {
            throw new GameNotFoundException();
        }

        String currentUser = (joinRequest.playerColor() == WHITE) ? game.whiteUsername() : game.blackUsername();
        if (currentUser != null) {
            throw new GameFullException();
        }
        gameDataDAO.addUser(joinRequest.gameID(), authData.username(), joinRequest.playerColor());
        return new JoinGameResult();
    }

    /**
     * Gets the list of games from the database.
     * @param listRequest A record that holds the user's AuthToken
     * @return The list of games (ArrayList<> of a record that holds the game ID, the game name, and the username for both players in the game)
     * @throws NotLoggedInException If the user is not logged in.
     * @throws IncompleteRequestException If any input fields are null.
     */
    public ListResult listGames(ListRequest listRequest) throws UserErrorException, DataAccessException {
        assertRequestComplete(listRequest);
        if (authDataDAO.getAuthData(listRequest.authToken()) == null) {
            throw new NotLoggedInException();
        }

        List<GameData> games = gameDataDAO.getAllGames();
        List<ListSingleGame> gameList = new ArrayList<>();
        for (var game : games) {
            gameList.add(new ListSingleGame(game.gameID(),
                                            game.gameName(),
                                            game.whiteUsername(),
                                            game.blackUsername()));
        }
        return new ListResult(gameList);
    }
}
