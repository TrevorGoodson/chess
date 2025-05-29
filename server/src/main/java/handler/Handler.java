package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.*;
import spark.Request;
import spark.Response;
import spark.Route;

public abstract class Handler implements Route {
    protected abstract Record parseRequest(Request req);
    protected abstract Record handleRequest(Record request) throws IncompleteRequestException;

    private record ErrorMessage(String message) {}

    @Override
    public Object handle(Request req, Response response) throws Exception {
        Record request = parseRequest(req);
        Record result;
        try {
            result = handleRequest(request);
        }
        catch (UsernameTakenException e) {
            response.status(403);
            return new Gson().toJson(new ErrorMessage("Error: username is already taken"));
        } catch (GameFullException e) {
            response.status(403);
            return new Gson().toJson(new ErrorMessage("Error: team already assigned"));
        } catch (NotLoggedInException | WrongPasswordException | WrongUsernameException e) {
            response.status(401);
            return new Gson().toJson(new ErrorMessage("Error: unauthorized"));
        } catch (IncompleteRequestException e) {
            response.status(400);
            return new Gson().toJson(new ErrorMessage("Error: bad request"));
        } catch (DataAccessException e) {
            response.status(500);
            return new Gson().toJson(new ErrorMessage("Error: something went wrong with the database"));
        } catch (Exception e) {
            response.status(405);
            return new Gson().toJson(new ErrorMessage("Unknown error: " + e));
        }
        return new Gson().toJson(result);
    }
}
