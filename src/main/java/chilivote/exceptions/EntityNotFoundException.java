package chilivote.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(long id, String object) {
        super(String.format("Could not find %s with id %s", object, id));
    }

    public EntityNotFoundException(String identifier, String object) {
        super(String.format("Could not find %s with identifier %s", object, identifier));
    }
}
