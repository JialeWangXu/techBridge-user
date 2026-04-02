package es.techbridge.techbridgeuser.resources.dtos.validations;

public class Validations {
    public static final String MOBILE = "\\+\\d{8,15}|\\d{9}|\\d{1}|\\d{2}";
    public static final String MOBILE_RX = "^" + MOBILE + "$";
    private Validations() {
        //Empty
    }
}

