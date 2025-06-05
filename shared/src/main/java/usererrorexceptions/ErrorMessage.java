package usererrorexceptions;

public record ErrorMessage(String message, int code) {
    public ErrorMessage(String message) {
        this(message, 0);
    }
}
