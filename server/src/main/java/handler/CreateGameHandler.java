package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import requestresultrecords.CreateGameRequest;
import service.GameService;
import service.IncompleteRequestException;
import spark.Request;


public class CreateGameHandler extends Handler {
    @Override
    protected Record parseRequest(Request req) {
        record PartialRequest(String gameName) {}
        var gameName = new Gson().fromJson(req.body(), PartialRequest.class);
        String authToken = req.headers("Authorization");
        return new CreateGameRequest(authToken, gameName.gameName());
    }

    @Override
    protected Record handleRequest(Record request) throws IncompleteRequestException, DataAccessException {
        return new GameService().createGame((CreateGameRequest) request);
    }
}
