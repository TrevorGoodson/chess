package handler;

import spark.Request;
import spark.Response;
import spark.Route;
import service.ClearService;

public class ClearHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        new ClearService().clear();
        return null;
    }
}
