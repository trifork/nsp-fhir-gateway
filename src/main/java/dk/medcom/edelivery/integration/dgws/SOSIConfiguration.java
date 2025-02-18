package dk.medcom.edelivery.integration.dgws;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.SignatureUtil;
import dk.sosi.seal.vault.ClasspathCredentialVault;
import dk.sosi.seal.vault.CredentialVault;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class SOSIConfiguration {

    @Bean
    CredentialVault credentialVault(SOSIConfigurationProperties properties) {
        System.setProperty("dk.sosi.seal.vault.CredentialVault#Alias", properties.getKeystoreAlias());

        return new ClasspathCredentialVault(
                SignatureUtil.setupCryptoProviderForJVM(),
                properties.getKeystorePath(),
                properties.getKeystorePassword()
        );
    }

    @Bean
    SOSIFactory sosiFactory(CredentialVault credentialVault) {
        Properties props = SignatureUtil.setupCryptoProviderForJVM();
        return new SOSIFactory(credentialVault, props);
    }

}
