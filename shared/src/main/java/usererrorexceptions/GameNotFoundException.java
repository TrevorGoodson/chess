package usererrorexceptions;

public class GameNotFoundException extends UserErrorException {
    public GameNotFoundException(String message) {
        super(message);
    }
    public GameNotFoundException() {}
}
