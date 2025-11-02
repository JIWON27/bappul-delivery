package com.bappul.delivery.user.common.exception;

import exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import response.ApiResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  private final String VALIDATE_ERROR = "VALIDATE_ERROR";
  private final String SERVER_ERROR = "SERVER_ERROR";

  @ExceptionHandler(ServiceException.class)
  public ResponseEntity<?> handleResponseException(ServiceException ex, HttpServletRequest req){
    HttpStatus status = HttpStatus.resolve(ex.getStatus());
    if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;
    if (status.is4xxClientError()) {
      log.warn("[클라이언트] api={} code={} status={} msg={}", req.getRequestURI(), ex.getCode(), status, ex.getMessage());
    } else {
      log.error("[서버] api={} code={} status={} msg={}", req.getRequestURI(), ex.getCode(), status, ex.getMessage(), ex);
    }
    return ResponseEntity.status(ex.getStatus())
        .body(ApiResponse.error(ex.getCode(), ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> methodArgumentNotValidException(MethodArgumentNotValidException ex){
    log.warn(ex.getMessage(), ex);
    AtomicReference<String> errors = new AtomicReference<>("");
    ex.getBindingResult().getAllErrors().forEach(e -> errors.set(e.getDefaultMessage()));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.badRequest(VALIDATE_ERROR, String.valueOf(errors)));
  }

  @ExceptionHandler(BindException.class)
  public ResponseEntity<?> bindException(BindException ex){
    log.error(ex.getMessage(), ex);
    AtomicReference<String> errors = new AtomicReference<>("");
    ex.getBindingResult().getAllErrors().forEach(e -> errors.set(e.getDefaultMessage()));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.badRequest(VALIDATE_ERROR, String.valueOf(errors)));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> serverException(Exception ex){
    log.error(ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.serverError(SERVER_ERROR, ex.getMessage()));
  }
}
