package dk.medcom.edelivery.fhir;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Communication;
import org.hl7.fhir.r4.model.StringType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SimpleMessageBundleProviderIT {

    @Autowired
    private SimpleMessageBundleProvider provider;

    @Test
    void can_save_and_get_bundle() {
        String message = "Ok, Computer";

        var communication = new Communication();
        communication.addPayload()
                .setContent(new StringType(message));

        var bundle = new Bundle()
                .setType(Bundle.BundleType.MESSAGE);
        bundle.addEntry().setResource(communication);

        var saved = provider.saveMessageBundle(bundle);
        var id = saved.getIdElement();
        var result = provider.getMessageBundle(id);

        assertNotNull(result);
        assertEquals(message, ((Communication) result.getEntryFirstRep().getResource()).getPayloadFirstRep().getContent().toString());
        assertDoesNotThrow(() -> UUID.fromString(id.getIdPart()));
    }
}
