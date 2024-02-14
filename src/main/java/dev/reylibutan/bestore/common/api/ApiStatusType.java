package dev.reylibutan.bestore.common.api;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ApiStatusType {

  SUCCESS("success"),
  FAIL("fail"),
  ERROR("error");

  private final String status;

  ApiStatusType(String status) {
    this.status = status;
  }

  @Override
  @JsonValue
  public String toString() {
    return status;
  }
}
