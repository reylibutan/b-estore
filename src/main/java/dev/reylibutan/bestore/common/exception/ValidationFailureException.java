package dev.reylibutan.bestore.common.exception;

public class ValidationFailureException extends RuntimeException {

  public ValidationFailureException(String message) {
    super(message);
  }
}
