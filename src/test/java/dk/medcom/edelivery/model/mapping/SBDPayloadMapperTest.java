package dk.medcom.edelivery.model.mapping;

import dk.medcom.edelivery.TestFileHelper;
import org.hl7.fhir.r4.model.Binary;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Communication;
import org.junit.jupiter.api.Test;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.BinaryContentType;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

import javax.activation.DataHandler;

import static org.junit.jupiter.api.Assertions.*;

class SBDPayloadMapperTest {

    @Test
    void throws_exception_for_unsupported_object_type() {
        BinaryContentType bct = new BinaryContentType();
        bct.setValue("This is not a valid object".getBytes());
        assertThrows(IllegalArgumentException.class, () -> SBDPayloadMapper.toBundle(bct));
    }

    @Test
    void map_binary_non_fhir_xml_payload() {
        StandardBusinessDocument document = getSBD("test-messages/SBDH.konfiguration.20201106.XREF01.eksempel.xml");

        Bundle bundle = SBDPayloadMapper.toBundle(document.getBinaryContent().get(0));

        assertEquals(Bundle.BundleType.MESSAGE, bundle.getType());
        assertEquals(1, bundle.getEntry().size());

        var entry = bundle.getEntryFirstRep();
        assertEquals(Binary.class, entry.getResource().getClass());

        var binary = (Binary) entry.getResource();
        assertEquals("application/xml", binary.getContentType());
        assertNotNull(binary.getData());
    }

    @Test
    void map_binary_non_fhir_pdf_payload() {
        // payload not actually pdf, but mimetype is.
        StandardBusinessDocument document = getSBD("test-messages/SBDH.konfiguration.20201106.XREF01.eksempel.pdf.xml");

        Bundle bundle = SBDPayloadMapper.toBundle(document.getBinaryContent().get(0));

        assertEquals(Bundle.BundleType.MESSAGE, bundle.getType());
        assertEquals(1, bundle.getEntry().size());

        var entry = bundle.getEntryFirstRep();
        assertEquals(Binary.class, entry.getResource().getClass());

        var binary = (Binary) entry.getResource();
        assertEquals("application/pdf", binary.getContentType());
        assertNotNull(binary.getData());
    }

    @Test
    void map_utf8_xml_payload() {
        StandardBusinessDocument document = getSBD("test-messages/sbdh-with-utf8-content.xml");

        Bundle bundle = SBDPayloadMapper.toBundle(document.getBinaryContent().get(0));

        assertEquals(Bundle.BundleType.MESSAGE, bundle.getType());
        assertEquals(1, bundle.getEntry().size());
        String contentString = ((Communication) bundle.getEntry().get(0).getResource()).getPayload().get(0).getContentStringType().getValue();
        assertEquals("Hej MedCom! ÆØÅæøå", contentString);
    }

    @Test
    void map_iso88591_xml_payload() {
        StandardBusinessDocument document = getSBD("test-messages/sbdh-with-iso88591-content.xml");

        Bundle bundle = SBDPayloadMapper.toBundle(document.getBinaryContent().get(0));

        assertEquals(Bundle.BundleType.MESSAGE, bundle.getType());
        assertEquals(1, bundle.getEntry().size());
        String contentString = ((Communication) bundle.getEntry().get(0).getResource()).getPayload().get(0).getContentStringType().getValue();
        assertEquals("Hej MedCom! ÆØÅæøå", contentString);
    }

    @Test
    void map_binary_fhir_xml_payload() {
        StandardBusinessDocument document = getSBD("test-messages/SBDH.konfiguration.20201106.XREF01.eksempel.fhir-xml.xml");

        Bundle bundle = SBDPayloadMapper.toBundle(document.getBinaryContent().get(0));

        assertEquals(Bundle.BundleType.MESSAGE, bundle.getType());
        assertEquals(2, bundle.getEntry().size());
    }

    @Test
    void map_binary_fhir_json_payload() {
        StandardBusinessDocument document = getSBD("test-messages/SBDH.konfiguration.20201106.XREF01.eksempel.fhir-json.xml");

        BinaryContentType content = document.getBinaryContent().get(0);
        Bundle bundle = SBDPayloadMapper.toBundle(content);

        assertEquals(Bundle.BundleType.MESSAGE, bundle.getType());
        assertEquals(2, bundle.getEntry().size());
    }

    @Test
    void parse_avi_sbdh_samples() {
        checkValues(getSBD("test-messages/ovi/SBDH.konfiguration.20201113.-.XREF01.eksempel.xml"));
        checkValues(getSBD("test-messages/ovi/SBDH.konfiguration.20201208.-.XREF01 Ack .eksempel.xml"));
        checkValues(getSBD("test-messages/SBDH.konfiguration.20201106.XREF01.eksempel.pdf.xml"));
        checkValues(getSBD("test-messages/new-example.xml"));
    }

    @Test
    void map_xdis13() {
        StandardBusinessDocument document = getSBD("test-messages/regionh_samples/XDIS13_SBDH.xml");

        Bundle bundle = SBDPayloadMapper.toBundle(document.getBinaryContent().get(0));

        assertEquals(1, bundle.getEntry().size());
        assertTrue(bundle.getEntry().get(0).getResource() instanceof Binary);
        String payload = new String(((Binary) bundle.getEntry().get(0).getResource()).getData());
        assertFalse(payload.contains("StandardBusinessDocument"));
        assertFalse(payload.contains("VANSEnvelope"));
        assertTrue(payload.contains("BookingConfirmation"));
    }

    @Test
    void map_xdis17() {
        StandardBusinessDocument document = getSBD("test-messages/regionh_samples/XDIS17_SBDH.xml");

        Bundle bundle = SBDPayloadMapper.toBundle(document.getBinaryContent().get(0));

        assertEquals(1, bundle.getEntry().size());
        assertTrue(bundle.getEntry().get(0).getResource() instanceof Binary);
        String payload = new String(((Binary) bundle.getEntry().get(0).getResource()).getData());
        assertFalse(payload.contains("StandardBusinessDocument"));
        assertFalse(payload.contains("VANSEnvelope"));
        assertTrue(payload.contains("NotificationOfDischarge"));
    }

    @Test
    void map_vans_xdis20() {
        StandardBusinessDocument document = getSBD("test-messages/regionh_samples/XDIS20_SBDH.xml");

        Bundle bundle = SBDPayloadMapper.toBundle(document.getBinaryContent().get(0));

        assertEquals(1, bundle.getEntry().size());
        assertTrue(bundle.getEntry().get(0).getResource() instanceof Binary);
        String payload = new String(((Binary) bundle.getEntry().get(0).getResource()).getData());
        assertFalse(payload.contains("VANSEnvelope"));
        assertTrue(payload.contains("NotificationOfAdmission"));
    }

    @Test
    void map_vans_xdis21() {
        StandardBusinessDocument document = getSBD("test-messages/regionh_samples/XDIS21_SBDH.xml");

        Bundle bundle = SBDPayloadMapper.toBundle(document.getBinaryContent().get(0));

        assertEquals(1, bundle.getEntry().size());
        assertTrue(bundle.getEntry().get(0).getResource() instanceof Binary);
        String payload = new String(((Binary) bundle.getEntry().get(0).getResource()).getData());
        assertFalse(payload.contains("VANSEnvelope"));
        assertTrue(payload.contains("ProgressOfCarePlan"));
    }

    @Test
    void map_vans_xdis18() {
        StandardBusinessDocument document = getSBD("test-messages/regionh_samples/XDIS18_SBDH.xml");

        Bundle bundle = SBDPayloadMapper.toBundle(document.getBinaryContent().get(0));

        assertEquals(1, bundle.getEntry().size());
        assertTrue(bundle.getEntry().get(0).getResource() instanceof Binary);
        String payload = new String(((Binary) bundle.getEntry().get(0).getResource()).getData());
        assertFalse(payload.contains("VANSEnvelope"));
        assertTrue(payload.contains("ReportOfDischarge"));
    }

    private void checkValues(StandardBusinessDocument document) {
        assertNotNull(document.getBinaryContent().get(0).getEncoding());
        assertNotNull(document.getBinaryContent().get(0).getMimeType());
        assertNotNull(document.getBinaryContent().get(0).getValue());
    }

    private StandardBusinessDocument getSBD(String path) {
        DataHandler dataHandler = TestFileHelper.getDataHandler(path, "application/xml");
        return SBDUnmarshaller.unmarshal(dataHandler);
    }

}
