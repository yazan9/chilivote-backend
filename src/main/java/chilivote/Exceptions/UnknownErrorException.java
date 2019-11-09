package chilivote.Exceptions;

public class UnknownErrorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnknownErrorException() 
    {
        super("An Unknown Error occured");
    }
}