package WebSocket;

import chess.ChessGame;

public record ChessGameData(String whiteUsername,
                            String blackUsername,
                            Connection whiteConnection,
                            Connection blackConnection,
                            ChessGame chessGame) {}
