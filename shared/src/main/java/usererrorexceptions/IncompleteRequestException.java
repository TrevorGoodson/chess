package usererrorexceptions;

public class IncompleteRequestException extends UserErrorException {
  public IncompleteRequestException(String message) {
        super(message);
    }
  public IncompleteRequestException() {}
}
