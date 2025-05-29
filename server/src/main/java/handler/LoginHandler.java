package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import requestresultrecords.LoginRequest;
import service.exceptions.IncompleteRequestException;
import service.UserService;
import spark.Request;

public class LoginHandler extends Handler {
    @Override
    protected Record parseRequest(Request req) {
        return new Gson().fromJson(req.body(), LoginRequest.class);
    }

    @Override
    protected Record handleRequest(Record request) throws IncompleteRequestException, DataAccessException {
        return new UserService().login((LoginRequest) request);
    }
}
