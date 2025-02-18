package dk.medcom.edelivery.model.mapping;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import dk.medcom.edelivery.model.Metadata;
import dk.medcom.edelivery.util.Hashing;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class PayloadInfoMapper {

    private final IParser xmlParser;

    public PayloadInfoMapper(FhirContext fhirContext) {
        xmlParser = fhirContext.newXmlParser();
    }

    public Metadata.PayloadInfo calculatePayloadInfo(Bundle bundle) {
        var bytes = xmlParser.encodeResourceToString(bundle).getBytes(StandardCharsets.UTF_8);

        return new Metadata.PayloadInfo()
                .setSize((long) bytes.length)
                .setMimeType(SBDPayloadMapper.TEXT_XML)
                .setHash(Hashing.sha1HexBinary(bytes));
    }
}
