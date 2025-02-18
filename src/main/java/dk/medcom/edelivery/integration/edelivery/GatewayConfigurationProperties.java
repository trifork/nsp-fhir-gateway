package dk.medcom.edelivery.integration.edelivery;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("gateway")
public class GatewayConfigurationProperties {

    static String GATEWAY_TYPE_X_DOMAIN = "x-domain";
    static String GATEWAY_TYPE_SDN_DOMAIN = "sdn";
    static String GATEWAY_TYPE_FOD_DOMAIN = "fod";

    private String type;
    private String partyIdType;
    private String partyType;
    private String name;
    private String originalSender;
    private String ddsRecipient;
    private String agreementRef;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isXDomain() {
        return GATEWAY_TYPE_X_DOMAIN.equals(type);
    }

    public boolean isSDN() {
        return GATEWAY_TYPE_SDN_DOMAIN.equals(type);
    }

    public boolean isFOD() {
        return GATEWAY_TYPE_FOD_DOMAIN.equals(type);
    }

    public String getPartyIdType() {
        return partyIdType;
    }

    public void setPartyIdType(String partyIdType) {
        this.partyIdType = partyIdType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPartyType() {
        return partyType;
    }

    public void setPartyType(String partyType) {
        this.partyType = partyType;
    }

    public String getOriginalSender() {
        return originalSender;
    }

    public void setOriginalSender(String originalSender) {
        this.originalSender = originalSender;
    }

    public String getDdsRecipient() {
        return ddsRecipient;
    }

    public void setDdsRecipient(String ddsRecipient) {
        this.ddsRecipient = ddsRecipient;
    }

    public String getAgreementRef() {
        return agreementRef;
    }

    public void setAgreementRef(String agreementRef) {
        this.agreementRef = agreementRef;
    }
}
