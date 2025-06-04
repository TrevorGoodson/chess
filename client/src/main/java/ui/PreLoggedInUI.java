package ui;

import exceptions.ResponseException;
import requestresultrecords.*;
import serverfacade.ServerFacade;
import usererrorexceptions.*;

import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class PreLoggedInUI {
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
        System.out.print("desired username: ");
        String username = inputScanner.nextLine();
        System.out.print("password: ");
        String password = inputScanner.nextLine();
        System.out.print("email: ");
        String email = inputScanner.nextLine();
        var registerRequest = new RegisterRequest(username, password, email);
        RegisterResult registerResult;
        try {
            registerResult = serverFacade.register(registerRequest);
        } catch (UsernameTakenException e) {
            return "Username already taken!\n";
        } catch (ResponseException e) {
            return "Unknown error:" + e.getMessage() + "\n";
        }
        return registerResult.authToken();
    }

    private String login(ServerFacade serverFacade, Scanner inputScanner) {
        System.out.print("username: ");
        String username = inputScanner.nextLine();
        System.out.print("password: ");
        String password = inputScanner.nextLine();
        var loginRequest = new LoginRequest(username, password);
        LoginResult loginResult;
        try {
            loginResult = serverFacade.login(loginRequest);
        } catch (WrongPasswordException e) {
            return "Wrong password!\n";
        } catch (WrongUsernameException e) {
            return "Unknown username!\n";
        } catch (ResponseException e) {
            return "Unknown error:" + e.getMessage() + "\n";
        }
        return loginResult.authToken();
    }
}
