package handler;

import chess.ChessGame;
import com.google.gson.Gson;
import requestresult.JoinGameRequest;
import service.GameService;
import spark.Request;

import static chess.ChessGame.TeamColor.*;

public class JoinGameHandler extends Handler {
    @Override
    protected Record parseRequest(Request req) {
        record PartialRequest(String playerColor, int gameID) {}
        var partialRequest = new Gson().fromJson(req.body(), PartialRequest.class);
        ChessGame.TeamColor color = switch (partialRequest.playerColor()) {
            case "white" -> WHITE;
            case "black" -> BLACK;
            default -> throw new RuntimeException("Invalid Create Game Request");
        };
        String authToken = req.headers("Authorization");
        return new JoinGameRequest(authToken, color, partialRequest.gameID);
    }

    @Override
    protected Record handleRequest(Record request) {
        return new GameService().joinGame((JoinGameRequest) request);
    }
}
