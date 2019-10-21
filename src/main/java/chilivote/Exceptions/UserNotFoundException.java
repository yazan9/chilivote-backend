package chilivote.Exceptions;

public class UserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserNotFoundException(Integer id) 
    {
        super("Could not find user with id = " + id);
    }
}