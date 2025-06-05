package serverfacade;

import exceptions.ResponseException;
import requestresultrecords.ClearResult;
import usererrorexceptions.UserErrorException;

public class ClearFacade extends ServerFacade {
    public ClearFacade(int port) {
        super(port);
    }

    @Override
    public Record handleRequest(Record request) throws UserErrorException {
        return makeHTTPRequest("DELETE", "db", null, null, ClearResult.class);
    }
}
