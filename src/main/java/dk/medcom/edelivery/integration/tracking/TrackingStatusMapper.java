package dk.medcom.edelivery.integration.tracking;

import dk.medcom.edelivery.model.ScopeIdentifiers;
import dk.medcom.edelivery.model.ScopeTypes;
import dk.medcom.edelivery.model.mapping.SBDHAdapter;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.Partner;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.PartnerIdentification;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class TrackingStatusMapper {

    public static final String TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    private TrackingStatusMapper() {
    }

    public static TrackingStatus toTrackingStatus(StandardBusinessDocumentHeader sbdh) {
        var adapter = SBDHAdapter.from(sbdh);

        var messageIdentifier = sbdh != null ? adapter.getInstanceIdentifier(ScopeTypes.MESSAGEIDENTIFIER, ScopeIdentifiers.DK_MEDCOM_MESSAGING).orElseThrow() : null;
        return new TrackingStatus()
                .setApplicationId("MedCom Meddelelseshotel")
                .setEquipmentId("NSP MedCom Gateway")
                .setPartner("Nsp") // Suggested by Carsten Bonde (KMD) 2021-02-08
                .setUuid(UUID.randomUUID().toString())
                .setTimestamp(DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN).format(ZonedDateTime.now(ZoneId.systemDefault())))
                .setDirection(TrackingStatus.Direction.IB)
                .setSender(sbdh != null ? sbdh.getSender().stream().findFirst().map(Partner::getIdentifier).map(PartnerIdentification::getValue).orElseThrow() : null)
                .setReceiver(sbdh != null ? sbdh.getReceiver().stream().findFirst().map(Partner::getIdentifier).map(PartnerIdentification::getValue).orElseThrow() : null)
                .setDocumentId(messageIdentifier)
                .setDocumentType(sbdh != null ? sbdh.getDocumentIdentification().getType() : null)
                .setOriginalDocumentReference(sbdh != null && adapter.getRequestingDocumentIdentifier() != null ? adapter.getRequestingDocumentIdentifier() : "<none>")
                .setMessageReference(messageIdentifier)
                .setMessageType(sbdh != null ? sbdh.getDocumentIdentification().getStandard() : null)
                .setRemotePartner("remote");
    }

}
