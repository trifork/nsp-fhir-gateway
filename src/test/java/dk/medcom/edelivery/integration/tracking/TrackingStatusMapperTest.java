package dk.medcom.edelivery.integration.tracking;

import dk.medcom.edelivery.TestDataHelper;
import org.junit.jupiter.api.Test;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.*;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static dk.medcom.edelivery.model.ScopeIdentifiers.DK_MEDCOM_MESSAGING;
import static dk.medcom.edelivery.model.ScopeTypes.MESSAGEIDENTIFIER;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TrackingStatusMapperTest {

    @Test
    void mapMessageGeneralProperties() {

        var sbdh = TestDataHelper.getStandardBusinessDocumentHeader();

        var trackingStatus = TrackingStatusMapper.toTrackingStatus(sbdh);

        assertAll(
                () -> assertEquals("MedCom Meddelelseshotel", trackingStatus.getApplicationId()),
                () -> assertEquals("NSP MedCom Gateway", trackingStatus.getEquipmentId()),
                () -> assertEquals("IB", trackingStatus.getDirection().name()),
                () -> assertDoesNotThrow(() -> UUID.fromString(trackingStatus.getUuid())),
                () -> assertDoesNotThrow(() -> DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(trackingStatus.getTimestamp()))
        );
    }

    @Test
    void mapSbdhPropertiesMessage() {
        var messageId = UUID.randomUUID().toString();
        var sbdh = new StandardBusinessDocumentHeader()
                .withSender(new Partner().withIdentifier(new PartnerIdentification().withValue("theSender")))
                .withReceiver(new Partner().withIdentifier(new PartnerIdentification().withValue("theReceiver")))
                .withDocumentIdentification(new DocumentIdentification()
                        .withStandard("theStandard")
                        .withType("theDocumentType"))
                .withBusinessScope(new BusinessScope()
                        .withScope(
                                TestDataHelper.scope(MESSAGEIDENTIFIER, DK_MEDCOM_MESSAGING, messageId)
                        )
                );

        var trackingStatus = TrackingStatusMapper.toTrackingStatus(sbdh);

        assertAll(
                () -> assertEquals("theSender", trackingStatus.getSender()),
                () -> assertEquals("theReceiver", trackingStatus.getReceiver()),
                () -> assertEquals(messageId, trackingStatus.getDocumentId()),
                () -> assertEquals("theDocumentType", trackingStatus.getDocumentType()),
                () -> assertEquals(messageId, trackingStatus.getMessageReference()),
                () -> assertEquals("theStandard", trackingStatus.getMessageType()),
                () -> assertEquals("<none>", trackingStatus.getOriginalDocumentReference())
        );

    }

    @Test
    void mapSbdhPropertiesReceipt() {
        var messageId = UUID.randomUUID().toString();
        var originalMessageId = UUID.randomUUID().toString();
        var sbdh = new StandardBusinessDocumentHeader()
                .withSender(new Partner().withIdentifier(new PartnerIdentification().withValue("theSender")))
                .withReceiver(new Partner().withIdentifier(new PartnerIdentification().withValue("theReceiver")))
                .withDocumentIdentification(new DocumentIdentification()
                        .withStandard("theStandard")
                        .withType("theDocumentType"))
                .withBusinessScope(new BusinessScope()
                        .withScope(
                                TestDataHelper.scope(MESSAGEIDENTIFIER, DK_MEDCOM_MESSAGING, messageId),
                                new Scope().withScopeInformation(new ObjectFactory().createCorrelationInformation(new CorrelationInformation()
                                        .withRequestingDocumentInstanceIdentifier(originalMessageId)
                                ))
                        )
                );

        var trackingStatus = TrackingStatusMapper.toTrackingStatus(sbdh);

        assertAll(
                () -> assertEquals("theSender", trackingStatus.getSender()),
                () -> assertEquals("theReceiver", trackingStatus.getReceiver()),
                () -> assertEquals(messageId, trackingStatus.getDocumentId()),
                () -> assertEquals("theDocumentType", trackingStatus.getDocumentType()),
                () -> assertEquals(messageId, trackingStatus.getMessageReference()),
                () -> assertEquals("theStandard", trackingStatus.getMessageType()),
                () -> assertEquals(originalMessageId, trackingStatus.getOriginalDocumentReference())
        );

    }
}
