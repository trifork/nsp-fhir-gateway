package dk.medcom.edelivery.integration.dgws;

import dk.sosi.seal.model.IDCard;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Component
public class SOSIContext {

    private final SOSIConfigurationProperties properties;
    private final SOSIIDCardProvider idCardProvider;

    private final AtomicReference<IDCard> idCardAtomicReference = new AtomicReference<>();

    public SOSIContext(SOSIConfigurationProperties properties, @Qualifier("SOSIIDCardProviderImpl") SOSIIDCardProvider idCardProvider) {
        this.properties = properties;
        this.idCardProvider = idCardProvider;
    }

    public IDCard getIdCard() {
        return idCardAtomicReference.updateAndGet(this::updateIDCard);
    }

    private IDCard updateIDCard(IDCard current) {
        return isCurrentIdCardValid(current)
                ? current
                : idCardProvider.getNewIdCard();
    }

    private boolean isCurrentIdCardValid(IDCard current) {
        return (current != null) && (!properties.isValidateIDCardExpiration() || current.isValidInTime());
    }
}
