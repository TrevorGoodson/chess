package usererrorexceptions;

public class WrongPasswordException extends RuntimeException {
    public WrongPasswordException(String message) {
        super(message);
    }
    public WrongPasswordException() {}
}
