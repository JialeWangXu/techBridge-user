package es.techbridge.techbridgeuser.services.exceptions;

public class ActivationTokenExpiredException extends BadRequestException {

    public ActivationTokenExpiredException() {
        super("Activation token has expired");
    }
}
