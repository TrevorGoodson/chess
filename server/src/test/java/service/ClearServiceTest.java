package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.Test;
import requestresultrecords.*;
//import service.exceptions.IncompleteRequestException;
//import service.exceptions.NotLoggedInException;
import usererrorexceptions.*;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {
    @Test
    public void clearTest() {
        try {
            var user = new UserService().register(new RegisterRequest("Connor", "FrogLog", "t@gmail.com"));
            new GameService().createGame(new CreateGameRequest(user.authToken(), "Game 1"));
            new ClearService().clear();
            assertThrows(NotLoggedInException.class, () -> new GameService().listGames(new ListRequest(user.authToken())));
            var user2 = new UserService().register(new RegisterRequest("Connor2", "FrogLog", "t@gmail.com"));
            assertEquals(0, new GameService().listGames(new ListRequest(user2.authToken())).games().size());
        } catch (UserErrorException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
