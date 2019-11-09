package chilivote.Exceptions;

public class DuplicateRelationshipEntryException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DuplicateRelationshipEntryException() 
    {
        super("Duplicate Follow");
    }
}