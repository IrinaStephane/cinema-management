package com.hei.school.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
    return build(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<Map<String, Object>> handleForbidden(ForbiddenException ex) {
    return build(HttpStatus.FORBIDDEN, ex.getMessage());
  }

  @ExceptionHandler(EmailAlreadyUsedException.class)
  public ResponseEntity<Map<String, Object>> handleEmailAlreadyUsed(EmailAlreadyUsedException ex) {
    return build(HttpStatus.CONFLICT, ex.getMessage());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
    return build(HttpStatus.FORBIDDEN, ex.getMessage());
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<Map<String, Object>> handleValidationException(ValidationException ex) {
    return build(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
    return build(HttpStatus.BAD_REQUEST, "Validation failed: " + ex.getMessage());
  }

  private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message) {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", Instant.now());
    body.put("status", status.value());
    body.put("error", status.getReasonPhrase());
    body.put("message", message);
    return ResponseEntity.status(status).body(body);
  }
}
