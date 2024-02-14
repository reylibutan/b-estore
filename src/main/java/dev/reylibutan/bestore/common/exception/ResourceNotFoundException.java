package dev.reylibutan.bestore.common.exception;

import jakarta.persistence.EntityNotFoundException;

public class ResourceNotFoundException extends EntityNotFoundException {

  public ResourceNotFoundException(String message) {
    super(message);
  }
}
