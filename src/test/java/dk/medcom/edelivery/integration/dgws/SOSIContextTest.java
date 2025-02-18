package dk.medcom.edelivery.integration.dgws;

import dk.sosi.seal.model.IDCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SOSIContextTest {

    @Mock
    SOSIIDCardProvider sosiIdCardProviderStub;

    @Mock
    IDCard idCardStub;

    SOSIConfigurationProperties properties;
    private SOSIContext sosiContext;

    @BeforeEach
    void setUp() {
        when(sosiIdCardProviderStub.getNewIdCard()).thenReturn(idCardStub);

        this.properties = new SOSIConfigurationProperties();
        this.sosiContext = new SOSIContext(properties, sosiIdCardProviderStub);
    }

    @Test
    void returns_new_id_card_on_first_call() {
        sosiContext.getIdCard();

        verify(sosiIdCardProviderStub).getNewIdCard();
    }

    @Test
    void returns_cached_id_card_if_valid_in_time_on_second_call() {
        when(idCardStub.isValidInTime()).thenReturn(true);

        sosiContext.getIdCard();
        sosiContext.getIdCard();

        verify(sosiIdCardProviderStub).getNewIdCard();
    }

    @Test
    void returns_new_id_card_if_cached_id_card_is_not_valid_in_time_on_second_call() {
        when(idCardStub.isValidInTime()).thenReturn(false);

        sosiContext.getIdCard();
        sosiContext.getIdCard();

        verify(sosiIdCardProviderStub, times(2)).getNewIdCard();
    }

    @Test
    void returns_cached_id_card_if_card_expiration_is_not_validated_on_second_call() {
        properties.setValidateIDCardExpiration(false);

        sosiContext.getIdCard();
        sosiContext.getIdCard();

        verify(sosiIdCardProviderStub).getNewIdCard();
    }
}
