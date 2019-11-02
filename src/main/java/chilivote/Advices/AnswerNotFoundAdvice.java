package chilivote.Advices;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import chilivote.Exceptions.*;

@ControllerAdvice
class AnswerNotFoundAdvice {

  @ResponseBody
  @ExceptionHandler(AnswerNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  String AnswerNotFoundHandler(AnswerNotFoundException ex) {
    return ex.getMessage();
  }
}