package handler;

import dataaccess.DataAccessException;
import spark.Request;
import service.ClearService;

public class ClearHandler extends Handler {
    @Override
    protected Record parseRequest(Request req) {
        return null;
    }

    @Override
    protected Record handleRequest(Record request) throws DataAccessException {
        return new ClearService().clear();
    }
}
