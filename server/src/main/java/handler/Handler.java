package handler;

import com.google.gson.Gson;
import service.*;
import spark.Request;
import spark.Response;
import spark.Route;

public abstract class Handler implements Route {
    protected abstract Record parseRequest(Request req);
    protected abstract Record handleRequest(Record request);

    private record ErrorMessage(String message) {}

    @Override
    public Object handle(Request req, Response response) throws Exception {
        var request = parseRequest(req);
        Record result;
        try {
            result = handleRequest(request);
        }
        catch (UsernameTakenException e) {
            response.status(403);
            return new Gson().toJson(new ErrorMessage("Error: username is already taken"));
        } catch (NotLoggedInException | WrongPasswordException e) {
            response.status(401);
            return new Gson().toJson(new ErrorMessage("Error: unauthorized"));
        } catch (Exception e) {
            response.status(400);
            return new Gson().toJson(new ErrorMessage("Unknown error: " + e));
        }
        return new Gson().toJson(result);
    }
}
