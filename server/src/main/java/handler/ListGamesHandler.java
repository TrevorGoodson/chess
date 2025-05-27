package handler;

import requestresult.ListRequest;
import service.GameService;
import service.IncompleteRequestException;
import spark.Request;


public class ListGamesHandler extends Handler {
    @Override
    protected Record parseRequest(Request req) {
        return new ListRequest(req.headers("Authorization"));
    }

    @Override
    protected Record handleRequest(Record request) throws IncompleteRequestException {
        return new GameService().listGames((ListRequest) request);
    }
}
