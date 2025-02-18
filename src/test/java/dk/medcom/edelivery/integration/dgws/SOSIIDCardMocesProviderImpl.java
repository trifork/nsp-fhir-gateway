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
import org.w3c.dom.Document;

import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Objects;

@Component
public class SOSIIDCardMocesProviderImpl implements SOSIIDCardProvider {

    private static final Logger log = LogManager.getLogger(SOSIIDCardMocesProviderImpl.class);

    private final SOSIConfigurationProperties properties;
    private final SOSIFactory factory;
    private final CredentialVault credentialVault;
    private final SimpleSOAPClient soapClient = new SimpleSOAPClient();

    public SOSIIDCardMocesProviderImpl(SOSIConfigurationProperties properties, SOSIFactory factory, CredentialVault credentialVault) {
        this.properties = properties;
        properties.setKeystorePath("LasseDam.p12");
        this.factory = Objects.requireNonNull(factory);
        this.credentialVault = Objects.requireNonNull(credentialVault);
    }

    public IDCard getNewIdCard() {
        SecurityTokenRequest request = getSecurityTokenRequest();
        SecurityTokenResponse securityTokenResponse = sendSecurityTokenRequest(request);
        return securityTokenResponse.getIDCard();
    }

    private SecurityTokenRequest getSecurityTokenRequest() {
        CareProvider careProvider = new CareProvider(SubjectIdentifierTypeValues.CVR_NUMBER, "20921897", "TRIFORK A/S");
        IDCard requestCard = null;
        SecurityTokenRequest request = factory.createNewSecurityTokenRequest();
        try {
            requestCard = createUserIDCard(request, careProvider);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
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

    private IDCard createUserIDCard(SecurityTokenRequest request, CareProvider careProvider/*, X509Certificate certificate*/) throws KeyStoreException {
        String cpr = "0506704003";
        String givenName = "Lasse";
        String surName = "Læge-Dam";
        String occupation = "Test";
        String role = "test";
        String authorizationCode = "FYT03";

        String alias = "lasse læge-dam";
        UserInfo userinfo = new UserInfo(cpr, givenName, surName, null, occupation, role, authorizationCode);
        X509Certificate certificate = (X509Certificate) credentialVault.getKeyStore().getCertificate(alias);
        UserIDCard newUserIDCard = factory.createNewUserIDCard("TRUST2408 Systemtest XIX CA", userinfo, careProvider, AuthenticationLevel.MOCES_TRUSTED_USER, null, null, certificate, null);

        request.setIDCard(newUserIDCard);
        addSignature(request, alias, certificate);

        return newUserIDCard;
    }

    private void addSignature(SecurityTokenRequest request, String alias, X509Certificate certificate) {
        Document document = request.serialize2DOMDocument();
        byte[] bytesForSigning = request.getIDCard().getBytesForSigning(document);
        PrivateKey key = null;
        try {
            key = (PrivateKey) factory.getCredentialVault().getKeyStore().getKey(alias, "Test1234".toCharArray());
            Signature sig = Signature.getInstance("SHA1WithRSA");
            sig.initSign(key);
            sig.update(bytesForSigning);
            byte[] signedBytes = sig.sign();
            String signature = Base64.getEncoder().encodeToString(signedBytes);
            request.getIDCard().injectSignature(signature, certificate);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
