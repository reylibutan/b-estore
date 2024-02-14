package dev.reylibutan.bestore.common.api;

import dev.reylibutan.bestore.common.api.ApiErrorResponse.ApiErrorResponseBuilder;
import dev.reylibutan.bestore.common.exception.ResourceNotFoundException;
import dev.reylibutan.bestore.common.exception.ValidationFailureException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GenericExceptionHandler extends ResponseEntityExceptionHandler {

  @Value("${validation.common.bad_request_body}")
  private String ERR_MSG_VALIDATION_BAD_REQUEST_BODY;

  @Value("${validation.common.bad_url_param}")
  private String ERR_MSG_VALIDATION_BAD_URL_PARAM;

  @ExceptionHandler(ResourceNotFoundException.class)
  protected ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
    ApiErrorResponse apiError = ApiErrorResponse
        .notFound()
        .message(ex.getMessage())
        .withExtractedUriFrom(request)
        .build();

    return ResponseEntity.badRequest().body(apiError);
  }

  @ExceptionHandler(ValidationFailureException.class)
  protected ResponseEntity<Object> handleValidationExceptions(ValidationFailureException ex, WebRequest request) {
    ApiErrorResponse apiError = getBadApiErrorResponseWithPath(ex.getMessage(), request).build();
    return ResponseEntity.badRequest().body(apiError);
  }

  @Override
  protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    ApiErrorResponse apiError = getBadApiErrorResponseWithPath(ERR_MSG_VALIDATION_BAD_URL_PARAM, request)
        .withExtractedDetailedErrorMessagesFrom(ex)
        .build();
    return ResponseEntity.badRequest().body(apiError);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    ApiErrorResponse apiError = getBadApiErrorResponseWithPath(ERR_MSG_VALIDATION_BAD_REQUEST_BODY, request)
        .withExtractedDetailedErrorMessagesFrom(ex)
        .build();
    return ResponseEntity.badRequest().body(apiError);
  }

  private ApiErrorResponseBuilder getBadApiErrorResponseWithPath(String message, WebRequest request) {
    return ApiErrorResponse
        .badRequest()
        .message(message)
        .withExtractedUriFrom(request);
  }
}
