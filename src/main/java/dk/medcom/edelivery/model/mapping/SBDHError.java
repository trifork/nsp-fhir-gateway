package dk.medcom.edelivery.model.mapping;

public enum SBDHError {

    INTERNAL_ERROR("101", "Internal error", ""),
    SBDH_INTEGRITY("102", "SBDH integrity error", "");

    String type;
    String reason;
    String message;

    SBDHError(String type, String reason, String message) {
        this.type = type;
        this.reason = reason;
        this.message = message;
    }

    public SBDHError withMessage(String message) {
        this.message = message;
        return this;
    }
}
