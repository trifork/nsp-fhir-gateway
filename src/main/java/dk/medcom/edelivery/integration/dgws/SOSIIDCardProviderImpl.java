package dk.medcom.edelivery.integration.dgws;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.*;
import dk.sosi.seal.model.constants.SubjectIdentifierTypeValues;
import dk.sosi.seal.vault.CredentialVault;
import dk.sosi.seal.xml.XmlUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Qualifier("SOSIIDCardProviderImpl")
public class SOSIIDCardProviderImpl implements SOSIIDCardProvider {

    private static final Logger log = LogManager.getLogger(SOSIIDCardProviderImpl.class);

    private final SOSIConfigurationProperties properties;
    private final SOSIFactory factory;
    private final CredentialVault credentialVault;
    private final SimpleSOAPClient soapClient = new SimpleSOAPClient();

    public SOSIIDCardProviderImpl(SOSIConfigurationProperties properties, SOSIFactory factory, CredentialVault credentialVault) {
        this.properties = properties;
        this.factory = Objects.requireNonNull(factory);
        this.credentialVault = Objects.requireNonNull(credentialVault);
    }

    public IDCard getNewIdCard() {
        SecurityTokenRequest request = getSecurityTokenRequest();
        SecurityTokenResponse securityTokenResponse = sendSecurityTokenRequest(request);
        return securityTokenResponse.getIDCard();
    }

    private SecurityTokenRequest getSecurityTokenRequest() {
        CareProvider careProvider = new CareProvider(SubjectIdentifierTypeValues.CVR_NUMBER, properties.getCareProviderCvr(), properties.getCareProviderName());
        IDCard requestCard = createRequestIdCard(careProvider);
        SecurityTokenRequest request = factory.createNewSecurityTokenRequest();
        request.setIDCard(requestCard);
        return request;
    }

    private SecurityTokenResponse sendSecurityTokenRequest(SecurityTokenRequest request) {
        String xml = XmlUtil.node2String(request.serialize2DOMDocument(), false, true);
        String response = soapClient.sendRequest(properties.getStsUrl(), "", xml, properties.getTimeout());
        SecurityTokenResponse securityTokenResponse = factory.deserializeSecurityTokenResponse(response);

        if (securityTokenResponse.isFault()) {
            log.error("FaultActor: {}", securityTokenResponse.getFaultActor());
            log.error("FaultCode: {}", securityTokenResponse.getFaultCode());
            log.error("FaultString: {}", securityTokenResponse.getFaultString());
            throw new SOSIException("Security token response is faulty: " + securityTokenResponse.getFaultString());
        }

        if (securityTokenResponse.getIDCard() == null) {
            throw new SOSIException("The response from the STS did not contain an IDCard:\n" + response);
        }

        return securityTokenResponse;
    }

    private IDCard createRequestIdCard(CareProvider careProvider) {
        IDCard requestCard = factory.createNewSystemIDCard(
                properties.getSosiSystemName(),
                careProvider,
                AuthenticationLevel.VOCES_TRUSTED_SYSTEM,
                null,
                null,
                credentialVault.getSystemCredentialPair().getCertificate(),
                null);
        if (requestCard == null) {
            throw new SOSIException("Failed to create a new systemIDCard");
        }
        return requestCard;
    }

}
