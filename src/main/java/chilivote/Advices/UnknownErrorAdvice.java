package chilivote.Advices;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import chilivote.Exceptions.*;

@ControllerAdvice
class UnknownErrorAdvice {

  @ResponseBody
  @ExceptionHandler(UnknownErrorException.class)
  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  String UnknownErrorHandler(UnknownErrorException ex) {
    return ex.getMessage();
  }
}