package dk.medcom.edelivery.integration.tracking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TrackingStatusTest {

    @Test
    void maps_to_json() throws JsonProcessingException {

        var trackingStatus = new TrackingStatus()
                .setApplicationId("application-id")
                .setEquipmentId("equipment-id")
                .setUuid("34569bb5-315f-4f1d-a33e-3ec35115df91")
                .setTimestamp("2019-01-01T00:00:00.000")
                .setPartner("partner")
                .setDirection(TrackingStatus.Direction.IB)
                .setRemotePartner("remote")
                .setSender("123456")
                .setReceiver("654321")
                .setDocumentId(UUID.randomUUID().toString())
                .setDocumentType("?")
                .setOriginalDocumentReference(UUID.randomUUID().toString())
                .setMessageType(UUID.randomUUID().toString())
                .setMessageType("NotificationOfAdmission");

        var json = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(trackingStatus);

        var fieldNames = List.of(
                "ApplId",
                "EquipmentId",
                "Uuid",
                "Ts",
                "Partner",
                "Direction",
                "RemotePartner",
                "Sender",
                "Receiver",
                "Icref",
                "Brvstat",
                "Orgicref",
                "MessageType"
        );

        assertTrue(fieldNames.stream().allMatch(json::contains));
        System.out.println(json);
    }
}
