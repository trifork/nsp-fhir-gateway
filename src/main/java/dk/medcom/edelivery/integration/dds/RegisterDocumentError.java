package dk.medcom.edelivery.integration.dds;

public class RegisterDocumentError extends RuntimeException {

    public RegisterDocumentError(String message) {
        super(message);
    }
}
