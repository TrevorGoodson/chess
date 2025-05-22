package requestresult;

import chess.ChessGame.TeamColor;

public record JoinGameRequest(String authToken, TeamColor playerColor, int gameID) {}
