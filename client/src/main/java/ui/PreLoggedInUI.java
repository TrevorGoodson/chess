package ui;

import requestresultrecords.*;
import serverfacade.ServerFacade;
import usererrorexceptions.*;

import java.util.List;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class PreLoggedInUI extends UserInterface {
    private static final String DEFAULT_PROMPT = "Welcome to Chess!\nType \"help\" for options\n";

    public static void main(String[] args) {
        new PreLoggedInUI().run(parseInt(args[0]));
    }

    public void run(int port) {
        ServerFacade serverFacade = new ServerFacade(port);
        String prompt = DEFAULT_PROMPT;
        Scanner inputScanner = new Scanner(System.in);

        while (true) {
            System.out.print(prompt);
            String response = inputScanner.nextLine();
            switch (response) {
                case "help" -> prompt = """
                        "help": see options
                        "quit": exit the program
                        "login": log into the program
                        "register": if you don't have an account, make one here!
                        """;
                case "quit" -> {
                    return;
                }
                case "register" -> prompt = register(serverFacade, inputScanner) + DEFAULT_PROMPT;
                case "login" -> prompt = login(serverFacade, inputScanner) + DEFAULT_PROMPT;
                default -> prompt = "Unknown command, please try again!\n";
            }
        }
    }

    private String register(ServerFacade serverFacade, Scanner inputScanner) {
        List<String> responses = gatherUserInputForRequest(new String[] {"desired username", "password", "email"});
        var registerRequest = new RegisterRequest(responses.getFirst(), responses.get(1), responses.getLast());
        RegisterResult registerResult;
        try {
            registerResult = serverFacade.register(registerRequest);
        } catch (UserErrorException e) {
            return new UserErrorExceptionDecoder().getMessage(e);
        }
        new LoggedInUI(serverFacade, registerResult.authToken()).run();
        return "\n";
    }

    private String login(ServerFacade serverFacade, Scanner inputScanner) {
        List<String> responses = gatherUserInputForRequest(new String[] {"username", "password"});
        var loginRequest = new LoginRequest(responses.getFirst(), responses.getLast());
        LoginResult loginResult;
        try {
            loginResult = serverFacade.login(loginRequest);
        } catch (UserErrorException e) {
            return new UserErrorExceptionDecoder().getMessage(e);
        }
        new LoggedInUI(serverFacade, loginResult.authToken()).run();
        return "\n";
    }
}
