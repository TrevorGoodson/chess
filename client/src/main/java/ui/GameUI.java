package ui;

import chess.ChessGame;
import chess.ChessGame.TeamColor;
import chess.ChessMove;
import serverfacade.ConnectionException;
import serverfacade.ServerFacade;
import serverfacade.WebSocketFacade;
import serverfacade.WebSocketMessageHandler;

import java.util.List;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static chess.ChessGame.TeamColor.*;

public class GameUI extends UserInterface {
    private final TeamColor teamColor;
    private final Integer gameID;
    private final WebSocketFacade webSocketFacade;
    private final String authToken;
    private ChessGame chessGame;

    public GameUI(TeamColor teamColor, Integer gameID, WebSocketFacade webSocketFacade, String authToken) {
        this.teamColor = teamColor;
        this.gameID = gameID;
        this.webSocketFacade = webSocketFacade;
        this.authToken = authToken;
    }

    public void run() {
        System.out.print("\033[H\033[2J");
        String prompt = "Let's play! Type \"help\" for options.\n";
        Scanner inputScanner = new Scanner(System.in);
        WebSocketMessageHandler ws = new WebSocketMessageHandler(teamColor);
        webSocketFacade.setNotificationHandler(ws);

        while (true) {
            System.out.print(prompt);
            String response = inputScanner.nextLine();

            prompt = switch (response) {
                case "make move" -> makeMove();
                default -> "Unknown command. Please try again.\n";
            };
        }

    }

    private String makeMove() {
        List<String> responses = gatherUserInputForRequest(new String[] {"Move"});
        ChessMove chessMove = ChessMove.parseMove(responses.getFirst());
        if (chessMove == null) {
            return "Valid moves are in the form \"a1 -> b2\".\n";
        }

        try {
            webSocketFacade.makeMove(authToken, chessMove, gameID, teamColor);
        } catch (ConnectionException e) {
            return CONNECTION_DOWN_PROMPT;
        }

        return "";
    }


}
