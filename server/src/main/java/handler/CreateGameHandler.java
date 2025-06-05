package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import requestresultrecords.CreateGameRequest;
import requestresultrecords.PartialCreateGameRequest;
import service.GameService;
//import service.exceptions.IncompleteRequestException;
import usererrorexceptions.*;
import spark.Request;


public class CreateGameHandler extends Handler {
    @Override
    protected Record parseRequest(Request req) {
        var gameName = new Gson().fromJson(req.body(), PartialCreateGameRequest.class);
        String authToken = req.headers("Authorization");
        return new CreateGameRequest(authToken, gameName.gameName());
    }

    @Override
    protected Record handleRequest(Record request) throws UserErrorException, DataAccessException {
        return new GameService().createGame((CreateGameRequest) request);
    }
}
