package handler;

import com.google.gson.Gson;
import requestresult.RegisterRequest;
import service.UserService;
import spark.*;

public class RegisterHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        var registerRequest = new Gson().fromJson(request.body(), requestresult.RegisterRequest.class);
        var registerResult = new UserService().register(registerRequest);
        return new Gson().toJson(registerResult);
    }
}
