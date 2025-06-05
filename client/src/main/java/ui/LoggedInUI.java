package ui;

import chess.ChessGame;
import exceptions.ResponseException;
import requestresultrecords.*;
import serverfacade.ServerFacade;
import usererrorexceptions.GameFullException;
import usererrorexceptions.GameNotFoundException;

import java.util.List;
import java.util.Scanner;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
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
                        "list games": gives you all the games on the server
                        "play": join a game as a player
                        "observe": join a game as an observer
                        """;
                case "create game" -> prompt = createGame();
                case "list games" -> prompt = listGames();
                case "play" -> prompt = joinGame();
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

    private String listGames() {
        var listRequest = new ListRequest(authToken);
        ListResult listResult;
        try {
            listResult = serverFacade.listGames(listRequest);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        return createPrettyGameList(listResult);
    }

    private String createPrettyGameList(ListResult listResult) {
        StringBuilder builder = new StringBuilder();
        for (ListSingleGame game : listResult.games()) {
            builder.append("Game name: ");
            builder.append(game.gameName());
            builder.append(" | Game ID: ");
            builder.append(game.gameID());
            builder.append("\nWhite Player: ");
            builder.append(game.whiteUsername());
            builder.append(" | Black Player: ");
            builder.append(game.blackUsername());
            builder.append("\n\n");
        }
        return builder.toString();
    }

    private String joinGame() {
        List<String> responses = gatherUserInputForRequest(new String[] {"Game ID", "Team Color"});
        int gameID = parseInt(responses.getFirst());
        ChessGame.TeamColor color = switch (responses.getLast().toUpperCase()) {
            case "WHITE" -> WHITE;
            case "BLACK" -> BLACK;
            default -> null;
        };
        if (color == null) {
            return "Please enter a valid team color.\n";
        }
        var joinGameRequest = new JoinGameRequest(authToken, color, gameID);
        JoinGameResult joinGameResult;
        try {
            joinGameResult = serverFacade.joinGame(joinGameRequest);
        }
        catch (GameFullException e) {
            return "Team not available.\n";
        } catch (GameNotFoundException e) {
            return "Please enter a valid game ID.\n";
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        new DisplayBoard().whitePOV();
        System.out.print("\n");
        new DisplayBoard().blackPOV();
        return "Success!\n";
    }
}
