package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import usererrorexceptions.*;
import spark.*;

public abstract class Handler implements Route {
    protected abstract Record parseRequest(Request req);
    protected abstract Record handleRequest(Record request) throws UserErrorException, DataAccessException;

    @Override
    public Object handle(Request req, Response response) throws Exception {
        Record request = parseRequest(req);
        Record result;
        try {
            result = handleRequest(request);
        }
        catch (UserErrorException e) {
            var decoder = new UserErrorExceptionDecoder();
            response.status(decoder.getHTTPStatusCode(e));
            String errorMessage = decoder.getMessage(e);
            int errorCode = decoder.getCode(e);
            return new Gson().toJson(new ErrorMessage("Error: " + errorMessage, errorCode));
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
