package service;

import org.junit.jupiter.api.Test;
import requestresult.*;
import service.*;

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
        } catch (IncompleteRequestException e) {
            throw new RuntimeException(e);
        }
    }
}
