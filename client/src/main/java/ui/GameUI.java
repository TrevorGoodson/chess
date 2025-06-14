package ui;

import chess.ChessGame;
import chess.ChessGame.TeamColor;
import chess.ChessMove;
import chess.ChessPosition;
import serverfacade.ConnectionException;
import serverfacade.WebSocketFacade;
import serverfacade.WebSocketMessenger;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import static chess.ChessGame.TeamColor.*;
import static ui.EscapeSequences.ERASE_SCREEN;

public class GameUI extends UserInterface {
    private final TeamColor teamColor;
    private final Integer gameID;
    private final WebSocketFacade webSocketFacade;
    private final String authToken;
    private ChessGame chessGame = null;

    public GameUI(TeamColor teamColor, Integer gameID, WebSocketFacade webSocketFacade, String authToken) {
        this.teamColor = teamColor;
        this.gameID = gameID;
        this.webSocketFacade = webSocketFacade;
        this.webSocketFacade.linkClient(this);
        this.authToken = authToken;
    }

    public void run() {
        System.out.print(ERASE_SCREEN);
        String prompt = (teamColor != null) ? "Let's play! Type \"help\" for options.\n" : "";
        Scanner inputScanner = new Scanner(System.in);
        WebSocketMessenger ws = new WebSocketMessenger(teamColor);
        webSocketFacade.setWebSocketMessenger(ws);
        String response = "";

        while (!response.equals("leave")) {
            System.out.print(prompt);
            response = inputScanner.nextLine();

            prompt = switch (response) {
                case "help" -> """
                        "redraw": redraws the chess board
                        "leave": leave the game
                        "make move": make a move. Alternatively, you can type in the move directly
                        "resign": resign from the game
                        "highlight": highlight all legal moves
                        """;
                case "make move" -> makeMove();
                case "leave" -> leave();
                case "redraw" -> redraw();
                case "resign" -> resign();
                case "highlight" -> highlight();
                default -> checkForMove(response);
            };
        }
        try {
            webSocketFacade.endSession();
        } catch (IOException e) {
            System.out.print(CONNECTION_DOWN_PROMPT);
        }
    }

    private String leave() {
        try {
            webSocketFacade.leave(gameID, authToken);
        } catch (ConnectionException e) {
            return CONNECTION_DOWN_PROMPT;
        }
        return "";
    }

    private String highlight() {
        List<String> responses = gatherUserInputForRequest(new String[] {"Piece in position"});
        ChessPosition chessPosition = ChessPosition.parsePosition(responses.getFirst());
        if (chessPosition == null) {
            return "Please enter a valid position.\n";
        }
        if (teamColor == WHITE) {
            new DisplayBoard(chessGame).highlightSquares(chessPosition).whitePOV();
        }
        else {
            new DisplayBoard(chessGame).highlightSquares(chessPosition).blackPOV();
        }
        return "";
    }

    private String redraw() {
        if (chessGame == null) {
            return "Please wait while the board loads for the first time.";
        }

        if (teamColor == null || teamColor == WHITE) {
            new DisplayBoard(chessGame).whitePOV();
        }
        else {
            new DisplayBoard(chessGame).blackPOV();
        }
        return "";
    }

    private String resign() {
        try {
            webSocketFacade.resign(gameID, teamColor);
        } catch (ConnectionException e) {
            return CONNECTION_DOWN_PROMPT;
        }
        return "";
    }

    private String checkForMove(String input) {
        ChessMove chessMove = ChessMove.parseMove(input);
        if (chessMove == null) {
            return "Unknown command. Please try again.\n";
        }
        return executeMove(chessMove);
    }

    private String makeMove() {
        List<String> responses = gatherUserInputForRequest(new String[] {"Move"});
        ChessMove chessMove = ChessMove.parseMove(responses.getFirst());
        if (chessMove == null) {
            return "Valid moves are in the form \"a1 -> b2\".\n";
        }

        return executeMove(chessMove);
    }

    private String executeMove(ChessMove chessMove) {
        try {
            webSocketFacade.makeMove(authToken, chessMove, gameID);
            return "";
        } catch (ConnectionException e) {
            return CONNECTION_DOWN_PROMPT;
        }
    }


    public void setChessGame(ChessGame chessGame) {
        this.chessGame = chessGame;
    }
}
