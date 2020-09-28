package chilivote.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class AnswerNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AnswerNotFoundException(Integer id) 
    {
        super("Could not find Answer with id = " + id);
    }
}