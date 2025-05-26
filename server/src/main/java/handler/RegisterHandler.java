package handler;

import com.google.gson.Gson;
import requestresult.RegisterRequest;
import requestresult.RegisterResult;
import service.UserService;
import service.UsernameTakenException;
import spark.*;

public class RegisterHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        var registerRequest = new Gson().fromJson(request.body(), requestresult.RegisterRequest.class);
        RegisterResult registerResult;
        try {
            registerResult = new UserService().register(registerRequest);
        } catch (UsernameTakenException e) {
            return request;
        }

        return new Gson().toJson(registerResult);
    }
}
