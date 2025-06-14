package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import requestresultrecords.RegisterRequest;
import usererrorexceptions.*;
import service.UserService;
import spark.*;

public class RegisterHandler extends Handler {
    @Override
    protected Record parseRequest(Request req) {
        return new Gson().fromJson(req.body(), RegisterRequest.class);
    }

    @Override
    protected Record handleRequest(Record request) throws UserErrorException, DataAccessException {
        return new UserService().register((RegisterRequest) request);
    }
}
