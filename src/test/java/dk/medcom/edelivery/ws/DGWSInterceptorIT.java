package dk.medcom.edelivery.ws;

import dk.medcom.edelivery.integration.dgws.*;
import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.vault.CredentialVault;
import org.apache.cxf.binding.soap.Soap11;
import org.apache.cxf.binding.soap.SoapMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import javax.xml.namespace.QName;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DGWSInterceptorIT {

    @TestConfiguration
    static class VocesConfiguration {
        @Bean
        @Primary
        public SOSIIDCardProvider sosiidCardProvider(SOSIConfigurationProperties properties, SOSIFactory factory, CredentialVault credentialVault) {
            return new SOSIIDCardProviderImpl(properties, factory, credentialVault);
        }
    }

    @Autowired
    private DGWSHeaderFactory headerFactory;

    private DGWSInterceptor dgwsInterceptor;

    @BeforeEach
    void setUp() {
        dgwsInterceptor = new DGWSInterceptor(headerFactory);
    }

    @Test
    void adds_security_and_medcom_headers_to_request() {
        SoapMessage message = new SoapMessage(Soap11.getInstance());

        dgwsInterceptor.handleMessage(message);

        assertAll(
                () -> assertEquals(2, message.getHeaders().size()),
                () -> assertNotNull(message.getHeader(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security"))),
                () -> assertNotNull(message.getHeader(new QName("http://www.medcom.dk/dgws/2006/04/dgws-1.0.xsd", "Header")))
        );
    }
}
