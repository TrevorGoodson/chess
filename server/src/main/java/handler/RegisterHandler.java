package handler;

import com.google.gson.Gson;
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
            response.status(403);
            return new Gson().toJson(new ErrorMessage("Username is already taken"));
        } catch (Exception e) {
            return new Gson().toJson(new ErrorMessage("Unknown error: " + e));
        }
        return new Gson().toJson(registerResult);
    }
}
