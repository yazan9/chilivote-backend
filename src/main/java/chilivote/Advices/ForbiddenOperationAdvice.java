package chilivote.Advices;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import chilivote.Exceptions.*;

@ControllerAdvice
class ForbiddenOperationAdvice {

  @ResponseBody
  @ExceptionHandler(ForbiddenOperationException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  String UserNotFoundHandler(ForbiddenOperationException ex) {
    return ex.getMessage();
  }
}