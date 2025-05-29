package handler;

import requestresultrecords.LogoutRequest;
import service.IncompleteRequestException;
import service.UserService;
import spark.Request;


public class LogoutHandler extends Handler {
    @Override
    protected Record parseRequest(Request req) {
        return new LogoutRequest(req.headers("Authorization"));
    }

    @Override
    protected Record handleRequest(Record request) throws IncompleteRequestException {
        return new UserService().logout((LogoutRequest) request);
    }
}
