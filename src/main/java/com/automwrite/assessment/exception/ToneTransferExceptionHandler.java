package com.automwrite.assessment.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ToneTransferExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = DocumentParsingException.class)
  protected ResponseEntity<Object> documentParsingException(RuntimeException ex, WebRequest request) {
    return handleExceptionInternal(ex, "Book already exists", new HttpHeaders(),
        HttpStatus.BAD_REQUEST, request);
  }



}
