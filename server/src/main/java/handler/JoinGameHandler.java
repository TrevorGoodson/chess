package handler;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.GameDataDAO;
import requestresult.JoinGameRequest;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class JoinGameHandler extends Handler {
    @Override
    protected Record parseRequest(Request req) {
        record PartialRequest(String playerColor, String gameID) {}
        var partialRequest = new Gson().fromJson(req.body(), PartialRequest.class);
        ChessGame.TeamColor color = switch (partialRequest.playerColor()) {
            case "white" -> WHITE;
            case "black" -> BLACK;
            default -> throw new RuntimeException("Invalid Create Game Request");
        };
        return null;
    }

    @Override
    protected Record handleRequest(Record request) {
        return null;
    }
}
