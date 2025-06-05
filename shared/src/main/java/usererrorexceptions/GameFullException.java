package usererrorexceptions;

public class GameFullException extends UserErrorException {
    public GameFullException(String message) {
        super(message);
    }
    public GameFullException() {}
}
