package ui;

import exceptions.ResponseException;
import requestresultrecords.*;
import serverfacade.ServerFacade;
import usererrorexceptions.WrongPasswordException;
import usererrorexceptions.WrongUsernameException;

import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class UserInterface {
    private static final String[] PRE_LOGIN_COMMANDS = {"help", "quit", "login", "register"};
    private static final int OS_ASSIGNED_PORT = 0;

    public static void main(String[] args) {
        new UserInterface().run(parseInt(args[0]));
    }

    public void run(int port) {
        ServerFacade serverFacade = new ServerFacade(port);
        String prompt = "Welcome to Chess!\nType \"help\" for options\n";
        Scanner inputScanner = new Scanner(System.in);
        String[] commandSet = PRE_LOGIN_COMMANDS;

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
                case "register" -> prompt = register(serverFacade, inputScanner);
                case "login" -> prompt = login(serverFacade, inputScanner);
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
        } catch (ResponseException e) {
            return "Username already taken!\n";
        } catch (Exception e) {
            return "Unknown error:" + e.getMessage();
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
            throw new RuntimeException(e);
        }
        return loginResult.authToken();
    }
}
