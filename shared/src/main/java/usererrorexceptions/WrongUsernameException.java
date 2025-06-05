package usererrorexceptions;

public class WrongUsernameException extends UserErrorException {
    public WrongUsernameException(String message) {
        super(message);
    }
    public WrongUsernameException() {}
}
