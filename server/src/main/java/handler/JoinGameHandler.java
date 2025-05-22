package handler;

import dataaccess.GameDataDAO;
import requestresult.JoinGameRequest;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class JoinGameHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        new GameService().joinGame(new JoinGameRequest(null, null, 0));
        return null;
    }
}
