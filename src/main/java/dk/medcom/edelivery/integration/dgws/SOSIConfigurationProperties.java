package dk.medcom.edelivery.integration.dgws;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sosi")
public class SOSIConfigurationProperties {

    private String stsUrl = "http://test2-cnsp.ekstern-test.nspop.dk:8080/sts/services/NewSecurityTokenService";
    private String keystorePath = "cert/MedCom - medcom-nsp-test-sosi.p12";
    private String keystorePassword = "Test1234";
    private String keystoreAlias = "medcom-nsp-sosi (funktionscertifikat)";
    private boolean validateIDCardExpiration = true;
    private String careProviderName = "MedCom";
    private String careProviderCvr = "26919991";
    private String sosiSystemName = "MedCom eDelivery Gateway";
    private int timeout = 60000;

    public String getStsUrl() {
        return stsUrl;
    }

    public void setStsUrl(String stsUrl) {
        this.stsUrl = stsUrl;
    }

    public String getKeystorePath() {
        return keystorePath;
    }

    public void setKeystorePath(String keystorePath) {
        this.keystorePath = keystorePath;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public void setKeystorePassword(String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }

    public String getKeystoreAlias() {
        return keystoreAlias;
    }

    public void setKeystoreAlias(String keystoreAlias) {
        this.keystoreAlias = keystoreAlias;
    }

    public boolean isValidateIDCardExpiration() {
        return validateIDCardExpiration;
    }

    public void setValidateIDCardExpiration(boolean validateIDCardExpiration) {
        this.validateIDCardExpiration = validateIDCardExpiration;
    }

    public String getCareProviderName() {
        return careProviderName;
    }

    public void setCareProviderName(String careProviderName) {
        this.careProviderName = careProviderName;
    }

    public String getCareProviderCvr() {
        return careProviderCvr;
    }

    public void setCareProviderCvr(String careProviderCvr) {
        this.careProviderCvr = careProviderCvr;
    }

    public String getSosiSystemName() {
        return sosiSystemName;
    }

    public void setSosiSystemName(String sosiSystemName) {
        this.sosiSystemName = sosiSystemName;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
