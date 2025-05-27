package handler;

import com.google.gson.Gson;
import requestresult.RegisterRequest;
import service.IncompleteRequestException;
import service.UserService;
import spark.*;

public class RegisterHandler extends Handler {
    @Override
    protected Record parseRequest(Request req) {
        return new Gson().fromJson(req.body(), RegisterRequest.class);
    }

    @Override
    protected Record handleRequest(Record request) throws IncompleteRequestException {
        return new UserService().register((RegisterRequest) request);
    }
}
