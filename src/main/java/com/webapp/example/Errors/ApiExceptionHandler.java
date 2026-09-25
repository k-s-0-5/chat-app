package com.webapp.example.Errors;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(AccountNotFoundException.class)
  public ResponseEntity<ProblemDetail> accountNotFound(
      AccountNotFoundException ex, HttpServletRequest request) {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    problemDetail.setTitle("Account not found");
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
  }

  @ExceptionHandler(MessageNotFoundException.class)
  public ResponseEntity<ProblemDetail> messageNotFound(
      MessageNotFoundException ex, HttpServletRequest request) {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    problemDetail.setTitle("Message not found");
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
  }

  @MessageExceptionHandler(MethodArgumentNotValidException.class)
  public void argumentInvalid(MethodArgumentNotValidException ex) {
    // TO-DO return info to user
  }

  @MessageExceptionHandler(CryptoException.class)
  public void cryptoError(CryptoException ex) {
    // TO-DO return crypto information
  }

  // @ExceptionHandler(Exception.class)
  // public ProblemDetail genericError(Exception ex, HttpServletRequest request) {
  //     return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An internal
  // error occurred.");
  // }
}
