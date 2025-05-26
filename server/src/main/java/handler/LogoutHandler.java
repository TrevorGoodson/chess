package handler;

import com.google.gson.Gson;
import requestresult.LogoutRequest;
import service.UserService;
import spark.Request;


public class LogoutHandler extends Handler {
    @Override
    protected Record parseRequest(Request req) {
        return new LogoutRequest(req.headers("Authorization"));
        //return new Gson().fromJson(req.headers().iterator().next(), LogoutRequest.class);
    }

    @Override
    protected Record handleRequest(Record request) {
        return new UserService().logout((LogoutRequest) request);
    }
}
