package chilivote.advices;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import chilivote.exceptions.*;

@ControllerAdvice
class BlogNotFoundAdvice {

  @ResponseBody
  @ExceptionHandler(UserNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  String UserNotFoundHandler(UserNotFoundException ex) {
    return ex.getMessage();
  }
}