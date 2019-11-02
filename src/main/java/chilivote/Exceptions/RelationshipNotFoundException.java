package chilivote.Exceptions;

public class RelationshipNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RelationshipNotFoundException() 
    {
        super("Relationship not found");
    }
}