package handler;

import requestresult.ListRequest;
import service.GameService;
import spark.Request;


public class ListGamesHandler extends Handler {
    @Override
    protected Record parseRequest(Request req) {
        return new ListRequest(req.headers("Authorization"));
    }

    @Override
    protected Record handleRequest(Record request) {
        return new GameService().listGames((ListRequest) request);
    }
}
