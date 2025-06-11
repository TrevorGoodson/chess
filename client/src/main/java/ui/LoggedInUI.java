package ui;

import chess.ChessGame;
import requestresultrecords.*;
import serverfacade.ServerFacade;
import usererrorexceptions.UserErrorException;
import usererrorexceptions.UserErrorExceptionDecoder;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static java.lang.Integer.parseInt;

public class LoggedInUI extends UserInterface{
    private final ServerFacade serverFacade;
    String authToken;
    Map<Integer, Integer> listNumToGameID = new TreeMap<>();

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
                case "observe" -> prompt = observe();
                default -> prompt = "Unknown command. Please try again.\n";
            }
        }
    }

    private String observe() {
        List<String> responses = gatherUserInputForRequest(new String[] {"Game number"});
        int listNum;
        try {
            listNum = parseInt(responses.getFirst());
            if (listNum > listNumToGameID.size() || listNum < 1) {
                throw new RuntimeException();
            }
        } catch (RuntimeException e) {
            return "Please enter a valid game number (a number, not a word like \"three\")\nTo see available games, type \"list games\".\n";
        }
        new DisplayBoard().whitePOV();
        return "Success!\n";
    }

    private void logout() {
        var logoutRequest = new LogoutRequest(authToken);
        try {
            serverFacade.logout(logoutRequest);
        } catch (UserErrorException e) {
            System.out.println("Something went wrong!" + new UserErrorExceptionDecoder().getMessage(e));
        }
        System.out.println("Success!");
    }

    private String createGame() {
        List<String> responses = gatherUserInputForRequest(new String[]{"game name"});
        var createGameRequest = new CreateGameRequest(authToken, responses.getFirst());
        CreateGameResult createGameResult;
        try {
            createGameResult = serverFacade.createGame(createGameRequest);
        } catch (UserErrorException e) {
            return new UserErrorExceptionDecoder().getMessage(e);
        }
        return "Success!\n";
    }

    private String listGames() {
        var listRequest = new ListRequest(authToken);
        ListResult listResult;
        try {
            listResult = serverFacade.listGames(listRequest);
        } catch (UserErrorException e) {
            return new UserErrorExceptionDecoder().getMessage(e) + "\n";
        }
        return createPrettyGameList(listResult);
    }

    private String createPrettyGameList(ListResult listResult) {
        if (listResult.games().isEmpty()) {
            return "No games currently on the server! Type \"create game\" to make a game.\n";
        }
        StringBuilder builder = new StringBuilder();
        Integer gameNum = 0;
        for (ListSingleGame game : listResult.games()) {
            gameNum++;
            builder.append(gameNum);
            listNumToGameID.put(gameNum, game.gameID());
            builder.append(". ");
            builder.append("Game name: ");
            builder.append(game.gameName());
            builder.append(" | White Player: ");
            builder.append(game.whiteUsername());
            builder.append(" | Black Player: ");
            builder.append(game.blackUsername());
            builder.append("\n");
        }
        return builder.toString();
    }

    private String joinGame() {
        List<String> responses = gatherUserInputForRequest(new String[] {"Game number", "Team Color"});
        int listNum;
        try {
            listNum = parseInt(responses.getFirst());
            if (listNum > listNumToGameID.size() || listNum < 1) {
                throw new RuntimeException();
            }
        } catch (RuntimeException e) {
            return "Please enter a valid game number (a number, not a word like \"three\")\nTo see available games, type \"list games\".\n";
        }
        int gameID = listNumToGameID.get(listNum);
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
        catch (UserErrorException e) {
            return new UserErrorExceptionDecoder().getMessage(e) + "\n";
        }
        if (color == WHITE) {
            new DisplayBoard().whitePOV();
        }
        else {
            new DisplayBoard().blackPOV();
        }
        return "Success!\n";
    }
}
