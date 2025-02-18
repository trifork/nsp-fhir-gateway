package dk.medcom.edelivery.model.mapping;

import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dk.medcom.edelivery.TestDataHelper;
import dk.medcom.edelivery.TestFileHelper;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Communication;
import org.hl7.fhir.r4.model.StringType;
import org.junit.jupiter.api.Test;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.ObjectFactory;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.Scope;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import javax.xml.bind.JAXB;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SBDHMetadataMapperTest {


    @Test
    void full_example() throws JsonProcessingException {

        StandardBusinessDocumentHeader sbdh = getSBDH();

        var metadata = SBDHMetadataMapper.toMetadata(sbdh);

        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(metadata));
    }

    @Test
    void missing_scope_fails() {
        StandardBusinessDocumentHeader sbdh = getSBDH();

        List<Scope> scopes = sbdh.getBusinessScope().getScope();
        List<Scope> withoutEntryUUID = scopes.stream().filter(s -> !"ENTRYUUID".equals(s.getType())).collect(Collectors.toList());
        sbdh.getBusinessScope().getScope().clear();
        sbdh.getBusinessScope().withScope(withoutEntryUUID);

        SBDHIntegrityException thrown = assertThrows(SBDHIntegrityException.class, () -> SBDHMetadataMapper.toMetadata(sbdh));
        assertThat(thrown).hasMessageContaining("Missing scope type 'ENTRYUUID' with identifier 'dk-dds-metadata'");
    }

    private StandardBusinessDocumentHeader getSBDH() {
        ObjectFactory objectFactory = new ObjectFactory();

        StandardBusinessDocumentHeader sbdh = TestDataHelper.getStandardBusinessDocumentHeader();

        String message = "Hej MedCom!";

        var communication = new Communication();
        communication.addPayload()
                .setContent(new StringType(message));

        var bundle = new Bundle()
                .setType(Bundle.BundleType.MESSAGE);
        bundle.addEntry().setResource(communication);

        var xml = FhirContext.forR4().newXmlParser().encodeResourceToString(bundle);

        var document = objectFactory.createStandardBusinessDocument()
                .withStandardBusinessDocumentHeader(sbdh)
                .withBinaryContent(objectFactory.createBinaryContentType()
                                .withMimeType("text/xml")
                                .withEncoding("UTF-8")
                                .withValue(xml.getBytes(StandardCharsets.UTF_8)));

        var writer = new StringWriter();
        JAXB.marshal(document, writer);

        System.out.println(writer.toString());
        return sbdh;
    }

    @Test
    void map_regionh_vans() {
        String[] files = {
                "test-messages/regionh_samples/XDIS13_SBDH.xml",
                "test-messages/regionh_samples/XDIS17_SBDH.xml",
                "test-messages/regionh_samples/XDIS18_SBDH.xml", // VANSEnvelope
                "test-messages/regionh_samples/XDIS20_SBDH.xml",
                "test-messages/regionh_samples/XDIS21_SBDH.xml" // VANSEnvelope
        };
        Arrays.stream(files)
                .map(file -> TestFileHelper.getDataHandler(file, "application/xml"))
                .forEach(dh -> assertNotNull(SBDHMetadataMapper.toMetadata(SBDUnmarshaller.unmarshal(dh).getStandardBusinessDocumentHeader())));
    }

}
