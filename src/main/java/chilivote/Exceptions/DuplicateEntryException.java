package chilivote.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class DuplicateEntryException extends RuntimeException {
    private static final long serialVersionUID = 1003L;
    public DuplicateEntryException(){
        super("Duplicate Entry");
    }

    public DuplicateEntryException(String message){
        super(message);
    }
}