package ui;

import exceptions.ResponseException;
import requestresultrecords.LogoutRequest;
import serverfacade.ServerFacade;

import java.util.Scanner;

public class LoggedInUI {
    private ServerFacade serverFacade;
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
}
