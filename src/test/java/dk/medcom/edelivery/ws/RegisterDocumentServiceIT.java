package dk.medcom.edelivery.ws;

import dk.medcom.edelivery.TestDataHelper;
import dk.medcom.edelivery.integration.dds.RegisterDocumentService;
import dk.medcom.edelivery.model.Metadata;
import dk.medcom.edelivery.model.mapping.RegisterDocumentSetMetadataMapper;
import dk.medcom.edelivery.model.mapping.SBDHMetadataMapper;
import org.junit.jupiter.api.Test;
import org.openehealth.ipf.commons.core.OidGenerator;
import org.openehealth.ipf.commons.ihe.xds.core.stub.ebrs30.lcm.SubmitObjectsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class RegisterDocumentServiceIT {

    public static final Metadata.Coding FORMAT_CODE_MESSAGE = new Metadata.Coding("urn:ad:dk:medcom:message:full", "1.2.208.184.100.10", "dk:medcom:meddelelse");
    public static final Metadata.Coding CLASS_CODE_MESSAGE = new Metadata.Coding("005", "1.2.208.184.100.9", "meddelelse");
    public static final Metadata.Coding CONFIDENTIALITY_CODE_NORMAL = new Metadata.Coding("N", "2.16.840.1.113883.5.25", "normal");
    public static final Metadata.Coding HEALTH_CARE_FACILITY_TYPE_CODE_GENERAL_PRACTICE = new Metadata.Coding("394761003", "2.16.840.1.113883.6.96", "lægepraksis (snomed:  almen lægepraksis)");
    public static final Metadata.Coding TYPE_CODE_MEDCOM_OIOXML = new Metadata.Coding("MedCom-OIOXML", "2.16.840.1.113883.6.1", "MedCom-OIOXML");
    @Autowired
    RegisterDocumentService registerDocumentService;

    @Autowired
    RegisterDocumentSetMetadataMapper mapper;

    @Test
    void call() {
        var metadata = new Metadata()
                .setProvenanceInfo(new Metadata.ProvenanceInfo()
                        .setAuthorInfo(new Metadata.AuthorInfo()
                                .setOrganization(new Metadata.Coding("8071000016009", "1.2.208.176.1.1", "Odense Universitetshospital – Svendborg"))
                                .setName(new Metadata.PersonName()
                                        .setFirst("Hans")
                                        .setLast("Andersen")))
                        .setPatientInfo(new Metadata.PatientInfo()
                                .setName(new Metadata.PersonName()
                                        .setFirst("Karen")
                                        .setLast("Blixen"))
                                .setGender("K")
                                .setDateOfBirth(LocalDateTime.of(1960, Month.APRIL, 15, 0, 0))))
                .setDescription(new Metadata.Description()
                        .setFormatCode(FORMAT_CODE_MESSAGE)
                        .setClassCode(CLASS_CODE_MESSAGE)
                        .setConfidentialityCode(CONFIDENTIALITY_CODE_NORMAL)
                        .setHealthCareFacilityTypeCode(HEALTH_CARE_FACILITY_TYPE_CODE_GENERAL_PRACTICE)
                        .setTypeCode(TYPE_CODE_MEDCOM_OIOXML)
                        .setLanguageCode("da-DK"))
                .setCpr("0506504003")
                .setEntryId(UUID.randomUUID().toString())
                .setUniqueId(OidGenerator.uniqueOid().toString())
                .setTitle("En testrapport")
                .setSubmissionTime(LocalDateTime.now())
                .setPayloadInfo(new Metadata.PayloadInfo()
                        .setMimeType("text/xml")
                        .setSize(16_000L)
                        .setHash("theHash"));

        var submitObjectsRequest = mapper.toSubmitObjectsRequest(metadata);

        String status = registerDocumentService.registerDocument(submitObjectsRequest);

        assertEquals("urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success", status);
    }

    @Test
    void call_2_example() {
        var header = TestDataHelper.getStandardBusinessDocumentHeader();

        var metadata = SBDHMetadataMapper.toMetadata(header)
                .setUniqueId(OidGenerator.uniqueOid().toString())
                .setPayloadInfo(new Metadata.PayloadInfo()
                        .setMimeType("text/xml")
                        .setSize(16_000L)
                        .setHash("theHash"));

        var submitObjectsRequest = mapper.toSubmitObjectsRequest(metadata);

        String status = registerDocumentService.registerDocument(submitObjectsRequest);

        assertEquals("urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success", status);
    }
}
