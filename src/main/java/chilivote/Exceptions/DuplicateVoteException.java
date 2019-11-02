package chilivote.Exceptions;

public class DuplicateVoteException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DuplicateVoteException() 
    {
        super("Duplicate Vote");
    }
}