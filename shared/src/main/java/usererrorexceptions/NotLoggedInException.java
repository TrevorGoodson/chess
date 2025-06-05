package usererrorexceptions;

public class NotLoggedInException extends UserErrorException {
    public NotLoggedInException(String message) {
        super(message);
    }
    public NotLoggedInException() {}
}
