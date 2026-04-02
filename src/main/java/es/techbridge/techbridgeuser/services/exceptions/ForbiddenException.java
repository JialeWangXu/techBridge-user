package es.techbridge.techbridgeuser.services.exceptions;

public class ForbiddenException extends RuntimeException {

    private static final String DESCRIPTION = "Forbidden exception";
    public ForbiddenException(String message) {
        super(DESCRIPTION+". "+message);
    }
}
