package usererrorexceptions;

public class NotLoggedInException extends RuntimeException {
    public NotLoggedInException(String message) {
        super(message);
    }
    public NotLoggedInException() {}
}
