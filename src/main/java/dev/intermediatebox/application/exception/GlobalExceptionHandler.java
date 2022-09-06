package dev.intermediatebox.application.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(GitHubUsernameNotFoundException.class)
  protected ResponseEntity<Object> handleGitHubUsernameNotFound(RuntimeException ex, WebRequest request) {
    ApiExceptionEntity exception = new ApiExceptionEntity(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    return handleExceptionInternal(ex, exception, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    headers.setContentType(MediaType.APPLICATION_JSON);
    var supportedMediaTypes = ex.getSupportedMediaTypes().stream().map(mediaType -> mediaType.toString()).collect(Collectors.joining(", "));
    log.warn("{}. Supported media type(s): {}", ex.getMessage(), supportedMediaTypes);
    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).headers(headers).body(
        Map.of(
            "status", HttpStatus.NOT_ACCEPTABLE.value(),
            "message", String.format("%s. Supported media type(s): %s", ex.getMessage(), supportedMediaTypes)
        )
    );
  }

  @ExceptionHandler(GenericException.class)
  protected ResponseEntity<Object> handleGenericException(RuntimeException ex, WebRequest request) {
    ApiExceptionEntity exception = new ApiExceptionEntity(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
    return handleExceptionInternal(ex, exception, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  // fallback exception handler
  @ExceptionHandler(Exception.class)
  protected ResponseEntity<Object> handleException(RuntimeException ex, WebRequest request) {
    log.warn("An exception occurred", ex);
    ApiExceptionEntity exception = new ApiExceptionEntity(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
    return handleExceptionInternal(ex, exception, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }
}
