package dev.reylibutan.bestore.common.api;

import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static dev.reylibutan.bestore.common.api.ApiStatusType.FAIL;
import static java.util.Objects.nonNull;

@Builder
@Getter
public class ApiErrorResponse {

  private ApiStatusType status;
  private String message;
  private List<String> errors;

  private String path;
  private Instant timestamp;

  public static ApiErrorResponseBuilder badRequest() {
    return ApiErrorResponse.builder().status(FAIL).timestamp(Instant.now());
  }

  public static ApiErrorResponseBuilder notFound() {
    return ApiErrorResponse.builder().status(FAIL).timestamp(Instant.now());
  }

  public static class ApiErrorResponseBuilder {

    public ApiErrorResponseBuilder withExtractedDetailedErrorMessagesFrom(TypeMismatchException ex) {
      List<String> errors = null;

      if (nonNull(ex) && StringUtils.hasText(ex.getMessage())) {
        errors = List.of(ex.getMessage());
      }

      this.errors = errors;
      return this;
    }

    public ApiErrorResponseBuilder withExtractedDetailedErrorMessagesFrom(BindException ex) {
      List<String> errors = null;

      if (nonNull(ex) && ex.hasErrors()) {
        errors = new ArrayList<>();
        for (ObjectError error : ex.getAllErrors()) {
          errors.add(error.getDefaultMessage());
        }
      }

      this.errors = errors;
      return this;
    }

    public ApiErrorResponseBuilder withExtractedUriFrom(WebRequest request) {
      if (request instanceof ServletWebRequest servletWebRequest) {
        this.path = servletWebRequest.getRequest().getRequestURI();
      }

      return this;
    }
  }
}
