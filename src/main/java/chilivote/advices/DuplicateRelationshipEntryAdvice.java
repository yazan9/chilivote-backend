package chilivote.advices;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import chilivote.exceptions.*;

@ControllerAdvice
class DuplicateRelationshipEntryAdvice {

  @ResponseBody
  @ExceptionHandler(DuplicateRelationshipEntryException.class)
  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  String uplicateRelationshipEntryHandler(DuplicateRelationshipEntryException ex) {
    return ex.getMessage();
  }
}