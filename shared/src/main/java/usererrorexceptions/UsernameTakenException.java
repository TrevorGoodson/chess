package usererrorexceptions;

public class UsernameTakenException extends UserErrorException {
    public UsernameTakenException(String message) {
        super(message);
    }
    public UsernameTakenException() {}
}
