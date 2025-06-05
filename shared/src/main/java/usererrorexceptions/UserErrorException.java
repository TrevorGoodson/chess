package usererrorexceptions;

public class UserErrorException extends Exception {
    public UserErrorException(String message) {
        super(message);
    }
    public UserErrorException() {}
}
