package service;

public class IncompleteRequestException extends RuntimeException {
  public IncompleteRequestException(String message) {
        super(message);
    }
  public IncompleteRequestException() {}
}
