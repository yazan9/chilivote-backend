package chilivote.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class DuplicateVoteException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DuplicateVoteException() 
    {
        super("Duplicate Vote");
    }
}