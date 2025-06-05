package ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class UserInterface {
    protected List<String> gatherUserInputForRequest(String[] promptList) {
        List<String> responseList = new ArrayList<>();
        var inputScanner = new Scanner(System.in);
        for (String prompt : promptList) {
            System.out.print(prompt);
            System.out.print(": ");
            responseList.add(inputScanner.nextLine());
        }
        return responseList;
    }
}
