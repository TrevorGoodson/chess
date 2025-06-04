package exceptions;

public class NotLoggedInException extends Exception {
    public NotLoggedInException(String message) {
        super(message);
    }
    public NotLoggedInException() {}
}
