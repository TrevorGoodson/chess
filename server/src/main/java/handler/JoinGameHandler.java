package handler;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import requestresultrecords.JoinGameRequest;
import service.GameService;
//import service.exceptions.IncompleteRequestException;
import usererrorexceptions.*;
import spark.Request;

import static chess.ChessGame.TeamColor.*;

public class JoinGameHandler extends Handler {
    @Override
    protected Record parseRequest(Request req) {
        record PartialRequest(String playerColor, Integer gameID) {}
        PartialRequest partialRequest;
        partialRequest = new Gson().fromJson(req.body(), PartialRequest.class);
        ChessGame.TeamColor color = switch (String.valueOf(partialRequest.playerColor()).toUpperCase()) {
            case "WHITE" -> WHITE;
            case "BLACK" -> BLACK;
            default -> null;
        };
        String authToken = req.headers("Authorization");
        return new JoinGameRequest(authToken, color, partialRequest.gameID);
    }

    @Override
    protected Record handleRequest(Record request) throws UserErrorException, DataAccessException {
        return new GameService().joinGame((JoinGameRequest) request);
    }
}
