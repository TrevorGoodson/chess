package handler;

import com.google.gson.Gson;
import requestresult.LoginRequest;
import service.UserService;
import spark.Request;

public class LoginHandler extends Handler {
    @Override
    protected Record parseRequest(Request req) {
        return new Gson().fromJson(req.body(), LoginRequest.class);
    }

    @Override
    protected Record handleRequest(Record request) {
        return new UserService().login((LoginRequest) request);
    }
}
