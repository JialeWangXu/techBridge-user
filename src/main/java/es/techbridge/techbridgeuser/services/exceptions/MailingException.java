package es.techbridge.techbridgeuser.services.exceptions;

public class MailingException extends RuntimeException {
    private static final String DESCRIPTION = "Mailing Exception";

    public MailingException(String detail) {
        super(DESCRIPTION + ". " + detail);
    }

}
