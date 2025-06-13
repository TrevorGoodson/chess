package ui;

import chess.ChessGame;
import requestresultrecords.*;
import serverfacade.ConnectionException;
import serverfacade.ServerFacade;
import serverfacade.WebSocketFacade;
import serverfacade.WebSocketMessageHandler;
import usererrorexceptions.UserErrorException;
import usererrorexceptions.UserErrorExceptionDecoder;

import java.util.*;

import static chess.ChessGame.TeamColor.*;
import static chess.ChessGame.TeamColor;
import static java.lang.Integer.parseInt;

public class LoggedInUI extends UserInterface{
    private final ServerFacade serverFacade;
    private final int port;
    String authToken;
    Map<Integer, Integer> listNumToGameID = new TreeMap<>();

    public LoggedInUI(ServerFacade serverFacade, int port, String authToken) {
        this.serverFacade = serverFacade;
        this.port = port;
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
        try {
            int gameID = listNumToGameID.get(listNum);
            WebSocketFacade webSocketFacade = new WebSocketFacade(port, new WebSocketMessageHandler());
            webSocketFacade.startObserving(gameID, authToken);
            new GameUI(null,
                    gameID,
                    webSocketFacade,
                    authToken).run();
        } catch (ConnectionException e) {
            return CONNECTION_DOWN_PROMPT;
        }
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
            builder.append((game.whiteUsername() == null) ? "Available!" : game.whiteUsername());
            builder.append(" | Black Player: ");
            builder.append((game.blackUsername() == null) ? "Available!" : game.blackUsername());
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
        TeamColor color = switch (responses.getLast().toUpperCase()) {
            case "WHITE" -> WHITE;
            case "BLACK" -> BLACK;
            default -> null;
        };
        if (color == null) {
            return "Please enter a valid team color.\n";
        }

        var joinGameRequest = new JoinGameRequest(authToken, color, gameID);
        JoinGameResult joinGameResult;
        WebSocketFacade webSocketFacade;
        try {
            webSocketFacade = new WebSocketFacade(port, new WebSocketMessageHandler(color));
            joinGameResult = serverFacade.joinGame(joinGameRequest);
            webSocketFacade.joinGame(authToken, gameID, color);
        }
        catch (UserErrorException e) {
            return new UserErrorExceptionDecoder().getMessage(e) + "\n";
        } catch (ConnectionException e) {
            return CONNECTION_DOWN_PROMPT;
        }

        new GameUI(color, gameID, webSocketFacade, authToken).run();

        return "Success!\n";
    }
}
