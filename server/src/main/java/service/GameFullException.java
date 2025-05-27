package service;

public class GameFullException extends RuntimeException {
    public GameFullException(String message) {
        super(message);
    }
    public GameFullException() {}
}
