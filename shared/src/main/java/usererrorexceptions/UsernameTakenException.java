package usererrorexceptions;

public class UsernameTakenException extends RuntimeException {
    public UsernameTakenException(String message) {
        super(message);
    }
    public UsernameTakenException() {}
}
