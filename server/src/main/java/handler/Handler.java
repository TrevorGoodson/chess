package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
//import service.exceptions.*;
import usererrorexceptions.*;
import spark.Request;
import spark.Response;
import spark.Route;

public abstract class Handler implements Route {
    protected abstract Record parseRequest(Request req);
    protected abstract Record handleRequest(Record request) throws IncompleteRequestException, DataAccessException;

    private record ErrorMessage(String message, int code) {
        public ErrorMessage(String message) {
            this(message, 0);
        }
    }

    @Override
    public Object handle(Request req, Response response) throws Exception {
        Record request = parseRequest(req);
        Record result;
        try {
            result = handleRequest(request);
        }
        catch (UsernameTakenException e) {
            response.status(403);
            return new Gson().toJson(new ErrorMessage("Error: username is already taken", 3));
        } catch (GameFullException e) {
            response.status(403);
            return new Gson().toJson(new ErrorMessage("Error: team already assigned"));
        } catch (NotLoggedInException e) {
            response.status(401);
            return new Gson().toJson(new ErrorMessage("Error: unauthorized"));
        } catch (WrongUsernameException e) {
            response.status(401);
            return new Gson().toJson(new ErrorMessage("Error: wrong username!", 2));
        } catch (WrongPasswordException e) {
            response.status(401);
            return new Gson().toJson(new ErrorMessage("Error: wrong password", 1));
        } catch (IncompleteRequestException e) {
            response.status(400);
            return new Gson().toJson(new ErrorMessage("Error: bad request"));
        } catch (DataAccessException e) {
            response.status(500);
            return new Gson().toJson(new ErrorMessage("Error: internal server error"));
        } catch (Error | Exception e) {
            response.status(500);
            return new Gson().toJson(new ErrorMessage("Unknown error: " + e));
        }
        return new Gson().toJson(result);
    }
}
