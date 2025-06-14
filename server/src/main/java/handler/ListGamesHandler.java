package handler;

import dataaccess.DataAccessException;
import requestresultrecords.ListRequest;
import service.GameService;
//import service.exceptions.IncompleteRequestException;
import usererrorexceptions.*;
import spark.Request;


public class ListGamesHandler extends Handler {
    @Override
    protected Record parseRequest(Request req) {
        return new ListRequest(req.headers("Authorization"));
    }

    @Override
    protected Record handleRequest(Record request) throws UserErrorException, DataAccessException {
        return new GameService().listGames((ListRequest) request);
    }
}
