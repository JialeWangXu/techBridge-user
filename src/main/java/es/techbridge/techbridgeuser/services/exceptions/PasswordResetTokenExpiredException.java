package es.techbridge.techbridgeuser.services.exceptions;

public class PasswordResetTokenExpiredException extends BadRequestException {

    public PasswordResetTokenExpiredException() {
        super("Password reset token has expired");
    }
}
