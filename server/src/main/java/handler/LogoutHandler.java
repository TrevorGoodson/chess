package handler;

import dataaccess.DataAccessException;
import requestresultrecords.LogoutRequest;
//import service.exceptions.IncompleteRequestException;
import usererrorexceptions.*;
import service.UserService;
import spark.Request;


public class LogoutHandler extends Handler {
    @Override
    protected Record parseRequest(Request req) {
        return new LogoutRequest(req.headers("Authorization"));
    }

    @Override
    protected Record handleRequest(Record request) throws UserErrorException, DataAccessException {
        return new UserService().logout((LogoutRequest) request);
    }
}
