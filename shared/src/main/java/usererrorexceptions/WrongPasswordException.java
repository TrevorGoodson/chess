package usererrorexceptions;

public class WrongPasswordException extends UserErrorException {
    public WrongPasswordException(String message) {
        super(message);
    }
    public WrongPasswordException() {}
}
