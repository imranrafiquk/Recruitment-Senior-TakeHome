package com.automwrite.assessment.exception;

import com.fasterxml.jackson.core.JsonProcessingException;

public class DocumentParsingException extends RuntimeException {

  public DocumentParsingException(JsonProcessingException e) {
    super(e);
  }
}
