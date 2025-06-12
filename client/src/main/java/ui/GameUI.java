package ui;

import chess.ChessGame.TeamColor;
import serverfacade.ServerFacade;
import serverfacade.WebSocketFacade;

import java.util.List;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static chess.ChessGame.TeamColor.*;

public class GameUI extends UserInterface {
    private final TeamColor teamColor;
    private final ServerFacade serverFacade;
    private final WebSocketFacade webSocketFacade;
    private final String authToken;

    public GameUI(TeamColor teamColor, ServerFacade serverFacade, WebSocketFacade webSocketFacade, String authToken) {
        this.teamColor = teamColor;
        this.serverFacade = serverFacade;
        this.webSocketFacade = webSocketFacade;
        this.authToken = authToken;
    }

    public void run() {
        System.out.print("\033[H\033[2J");
        displayBoard();
        String prompt = "Let's play! Type \"help\" for options.\n";
        Scanner inputScanner = new Scanner(System.in);

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

    }

    private void displayBoard() {
        if (teamColor == WHITE) {
            new DisplayBoard().whitePOV();
        }
        else {
            new DisplayBoard().blackPOV();
        }
    }
}
