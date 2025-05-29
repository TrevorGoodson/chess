package service.exceptions;

public class IncompleteRequestException extends Exception {
  public IncompleteRequestException(String message) {
        super(message);
    }
  public IncompleteRequestException() {}
}
