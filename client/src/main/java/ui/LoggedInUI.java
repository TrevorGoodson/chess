package ui;

import exceptions.ResponseException;
import requestresultrecords.CreateGameRequest;
import requestresultrecords.CreateGameResult;
import requestresultrecords.LogoutRequest;
import serverfacade.ServerFacade;

import java.util.List;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class LoggedInUI extends UserInterface{
    private final ServerFacade serverFacade;
    String authToken;

    public LoggedInUI(ServerFacade serverFacade, String authToken) {
        this.serverFacade = serverFacade;
        this.authToken = authToken;
    }

    public void run() {
        Scanner inputScanner = new Scanner(System.in);
        String prompt = "Hi there!\nType \"help\" for options\n";

        while (true) {
            System.out.print(prompt);
            String response = inputScanner.nextLine();
            switch (response) {
                case "logout" -> {
                    logout();
                    return;
                }
                case "help" -> prompt = """
                        "logout": logs you out
                        "create game": create a new chess game
                        "list game": gives you all the games on the server
                        "play": join a game as a player
                        "observe": join a game as an observer
                        """;
                case "create game" -> prompt = createGame();
                default -> prompt = "Unknown command. Please try again.\n";
            }
        }
    }

    private void logout() {
        var logoutRequest = new LogoutRequest(authToken);
        try {
            serverFacade.logout(logoutRequest);
        } catch (ResponseException e) {
            System.out.println("Something went wrong!" + e.getMessage());
        }
        System.out.println("Success!");
    }

    private String createGame() {
        List<String> responses = gatherUserInputForRequest(new String[]{"game name"});
        var createGameRequest = new CreateGameRequest(authToken, responses.getFirst());
        CreateGameResult createGameResult;
        try {
            createGameResult = serverFacade.createGame(createGameRequest);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        return "Success! Game ID: " + createGameResult.gameID() + "\n";
    }
}
