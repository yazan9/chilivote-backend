package chilivote.Exceptions;

public class AnswerNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AnswerNotFoundException(Integer id) 
    {
        super("Could not find Answer with id = " + id);
    }
}