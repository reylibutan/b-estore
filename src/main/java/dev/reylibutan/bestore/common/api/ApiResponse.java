package dev.reylibutan.bestore.common.api;

import lombok.Data;

@Data
public class ApiResponse {

  private ApiStatusType status;
  private final Object data;

  public ApiResponse(Object data) {
    this.data = data;
    this.status = ApiStatusType.SUCCESS;
  }
}
