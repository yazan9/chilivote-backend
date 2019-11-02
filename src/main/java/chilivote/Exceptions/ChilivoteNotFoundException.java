package chilivote.Exceptions;

public class ChilivoteNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ChilivoteNotFoundException(Integer id) 
    {
        super("Could not find Chilivote with id = " + id);
    }
}