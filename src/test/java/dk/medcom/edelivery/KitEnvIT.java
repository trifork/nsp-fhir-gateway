package dk.medcom.edelivery;

import ca.uhn.fhir.context.FhirContext;
import dk.medcom.edelivery.integration.dgws.DGWSHeaderFactory;
import dk.medcom.edelivery.integration.dgws.SOSIConfigurationProperties;
import dk.medcom.edelivery.integration.dgws.SOSIIDCardMocesProviderImpl;
import dk.medcom.edelivery.integration.dgws.SOSIIDCardProvider;
import dk.medcom.edelivery.integration.domibus.model.Payload;
import dk.medcom.edelivery.integration.domibus.model.Submission;
import dk.medcom.edelivery.integration.edelivery.GatewayConfigurationProperties;
import dk.medcom.edelivery.integration.edelivery.SubmissionFactory;
import dk.medcom.edelivery.model.mapping.SBDPayloadMapper;
import dk.medcom.edelivery.model.mapping.SBDUnmarshaller;
import dk.medcom.edelivery.testing.DomibusWSService;
import dk.medcom.edelivery.util.DDSUtil;
import dk.medcom.edelivery.util.PayloadUtil;
import dk.medcom.edelivery.ws.Iti18PortFactory;
import dk.medcom.edelivery.ws.Iti43PortFactory;
import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.SignatureUtil;
import dk.sosi.seal.vault.ClasspathCredentialVault;
import dk.sosi.seal.vault.CredentialVault;
import org.hl7.fhir.r4.model.Binary;
import org.hl7.fhir.r4.model.Bundle;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openehealth.ipf.commons.ihe.xds.core.ebxml.ebxml30.EbXMLFactory30;
import org.openehealth.ipf.commons.ihe.xds.core.ebxml.ebxml30.RetrieveDocumentSetRequestType;
import org.openehealth.ipf.commons.ihe.xds.core.ebxml.ebxml30.RetrieveDocumentSetResponseType;
import org.openehealth.ipf.commons.ihe.xds.core.requests.RetrieveDocumentSet;
import org.openehealth.ipf.commons.ihe.xds.core.stub.ebrs30.query.AdhocQueryResponse;
import org.openehealth.ipf.commons.ihe.xds.core.stub.ebrs30.rim.ExtrinsicObjectType;
import org.openehealth.ipf.commons.ihe.xds.core.stub.ebrs30.rim.SlotType1;
import org.openehealth.ipf.commons.ihe.xds.core.transform.requests.RetrieveDocumentSetRequestTransformer;
import org.openehealth.ipf.commons.ihe.xds.iti18.Iti18PortType;
import org.openehealth.ipf.commons.ihe.xds.iti43.Iti43PortType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static dk.medcom.edelivery.TestConstants.*;
import static dk.medcom.edelivery.model.ScopeTypes.*;
import static dk.medcom.edelivery.model.mapping.RegisterDocumentSetMetadataMapper.SERVICE_START_PREPONE_HOURS;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class KitEnvIT {

    private static final RetrieveDocumentSetRequestTransformer transformer = new RetrieveDocumentSetRequestTransformer(new EbXMLFactory30());
    private static final String gatewayUrl = "https://ap-dds.nsptest.medcom.dk/domibus/services/backend";

    @TestConfiguration
    static class MocesConfiguration {

        @Bean
        @Primary
        @Qualifier("SOSIIDCardProviderImpl")
        public SOSIIDCardProvider sosiidCardProvider(SOSIConfigurationProperties properties, SOSIFactory factory, CredentialVault credentialVault) {
            return new SOSIIDCardMocesProviderImpl(properties, factory, credentialVault);
        }

        @Bean
        @Primary
        CredentialVault credentialVaultMoces(SOSIConfigurationProperties properties) {
            return new ClasspathCredentialVault(
                    SignatureUtil.setupCryptoProviderForJVM(),
                    "LasseDam.p12",
                    properties.getKeystorePassword()
            );
        }
    }

    @Autowired
    SOSIConfigurationProperties properties;

    @Autowired
    GatewayConfigurationProperties gatewayProperties;

    @Autowired
    DGWSHeaderFactory headerFactory;

    @Autowired
    DDSUtil ddsUtil;

    DomibusWSService domibusWSService;

    Iti43PortType iti43PortType;

    Iti18PortType iti18PortType;

    @BeforeEach
    void setup() {
        domibusWSService = DomibusWSService.getService(gatewayUrl);
        iti18PortType = Iti18PortFactory.createWithSecurity("https://dds-nsp.nsptest.medcom.dk/ddsregistry/service", headerFactory);
        properties.setKeystorePath("LasseDam.p12");
        iti43PortType = Iti43PortFactory.createWithSecurity("https://medcom-gateway.nsptest.medcom.dk/ws", headerFactory);
    }

    /* Verifies that documents are shared correctly on DDS when sent to medcom-gateway:
    * 1. Sent document to DDS Domibus, which forwards it to the gateway
    * 2. Wait until metadata shows up in DDS using ITI-18
    * 3. Fetch the actual document using DDS ITI-43 (which requests it from medcom-gateway FHIR repo)
    * 4. Verify that the fetched document matches the one sent in step 1
    */
    @Test
    void validateDDSData() throws Exception {

        // 1. Send document to medcom-gateway, which must shwre it in DDS
        StandardBusinessDocumentHeader sbdh = TestDataHelper.getStandardBusinessDocumentHeader();
        Submission submission = getGatewaySubmission(sbdh);
        domibusWSService.sendSubmitRequest(submission);

        // 2. Look up the document in DDS registry
        List<ExtrinsicObjectType> result = lookupMetadataInDDS(sbdh);

        // 3. Fetch actual document
        Optional<String> externalIdentifier = result.get(0).getExternalIdentifier().stream()
                .filter(id -> "XDSDocumentEntry.uniqueId".equals(id.getName().getLocalizedString().get(0).getValue()))
                .map(id -> id.getValue()).findFirst();
        System.out.println("Now fetching actual document, id=" + externalIdentifier.get());
        Bundle ddsBundle = getBundleFromDDS(externalIdentifier.get());
        String ddsBundlePayload = bundleToPayload(ddsBundle);

        // 4. Payload found in DDS should match the original one sent
        Bundle originalBundle = SBDPayloadMapper.toBundle(getStandardBusinessDocument(submission).getBinaryContent().get(0));
        String originalBundlePayload = bundleToPayload(originalBundle);
        assertEquals(originalBundlePayload, ddsBundlePayload);
    }

    @NotNull
    private List<ExtrinsicObjectType> lookupMetadataInDDS(StandardBusinessDocumentHeader sbdh) throws InterruptedException {
        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.systemDefault());
        int preponedSecondsFromService = SERVICE_START_PREPONE_HOURS * (60 * 60);
        int gatewayDelay = 2*60*60;
        int secondsToSubtract = preponedSecondsFromService + gatewayDelay + 60;
        String serviceStartTimeFrom = DATE_TIME_FORMATTER.format(new Date().toInstant().minusSeconds(secondsToSubtract));

        AdhocQueryResponse response = iti18PortType.documentRegistryRegistryStoredQuery(ddsUtil.createITI18QueryFindDocuments(getInstanceIdentifierByType(sbdh, PATIENTID), serviceStartTimeFrom));

        int requestCount = 0;
        int maxRequests = 20;
        while (continueAsking(response) && ++requestCount < maxRequests) {
            System.out.println("No result yet for attempt " + requestCount + ". Retrying...");
            Thread.sleep(5 * 1000);
            response = iti18PortType.documentRegistryRegistryStoredQuery(ddsUtil.createITI18QueryFindDocuments(getInstanceIdentifierByType(sbdh, PATIENTID), serviceStartTimeFrom));
        }
        if (requestCount >= maxRequests) Assertions.fail("No response for query");

        assertTrue(response.getTotalResultCount().intValue() > 0);

        String expectedPatientId = getInstanceIdentifierByType(sbdh, PATIENTID);
        String expectedTitle = getInstanceIdentifierByType(sbdh, TITLE);
        String expectedLanguage = getInstanceIdentifierByType(sbdh, LANGUAGECODE);
        String expectedAuthor = getInstanceIdentifierByType(sbdh, AUTHORINSTITUTION);

        List<ExtrinsicObjectType> result = response.getRegistryObjectList().getIdentifiable().stream()
                .filter(o -> o.getValue() instanceof ExtrinsicObjectType)
                .map(o -> (ExtrinsicObjectType) o.getValue())
                .filter(o -> expectedPatientId.equals(getFirstSlotValueBySlotName(o, "sourcePatientId")))
                .filter(o -> expectedLanguage.equals(getFirstSlotValueBySlotName(o, "languageCode")))
                .filter(o -> expectedTitle.equals(o.getName().getLocalizedString().get(0).getValue()))
                .filter(o -> !isUnitTestId(o))
                .filter(o -> expectedAuthor.equals(getFirstValueByClassificationScheme(o, "urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d")))
                .collect(Collectors.toList());

        System.out.println("Matchings results: " + result.size());
        assertTrue(result.size() > 0);
        return result;
    }

    private boolean continueAsking(AdhocQueryResponse response) {
        return response.getTotalResultCount().intValue() == 0 ||
                response.getTotalResultCount().intValue() == 1 && responseIsFromUnitTest(response);
    }

    private boolean responseIsFromUnitTest(AdhocQueryResponse response) {
        return response.getRegistryObjectList().getIdentifiable().stream()
                .filter(o -> o.getValue() instanceof ExtrinsicObjectType)
                .map(o -> (ExtrinsicObjectType) o.getValue())
                .anyMatch(o -> isUnitTestId(o));
    }

    // Don't consider the metadata registered through unit testing
    private boolean isUnitTestId(ExtrinsicObjectType o) {
        String testId = "2.25.125634173210771630954014177888431177186";
        return o.getExternalIdentifier().stream()
                .filter(id -> "XDSDocumentEntry.uniqueId".equals(id.getName().getLocalizedString().get(0).getValue()))
                .anyMatch(id -> testId.equals(id.getValue()));
    }

    private Bundle getBundleFromDDS(String externalIdentifier) throws IOException {
        RetrieveDocumentSet request = ddsUtil.getRetrieveDocumentSet(externalIdentifier);
        RetrieveDocumentSetResponseType responseType =
                iti43PortType.documentRepositoryRetrieveDocumentSet((RetrieveDocumentSetRequestType) transformer.toEbXML(request).getInternal());

        RetrieveDocumentSetResponseType.DocumentResponse documentResponse = responseType.getDocumentResponse().get(0);

        String ddsBundleStr = IOUtils.toString(documentResponse.getDocument().getDataSource().getInputStream(), StandardCharsets.UTF_8.name());
        return (FhirContext.forR4().newXmlParser()).parseResource(Bundle.class, ddsBundleStr);
    }

    private String bundleToPayload(Bundle bundle) {
        Binary binary = (Binary) bundle.getEntry().get(0).getResource();
        return new String(binary.getData());
    }

    private StandardBusinessDocument getStandardBusinessDocument(Submission submission) {
        Payload payload = submission.getPayloads().stream().findFirst().orElseThrow();
        return SBDUnmarshaller.unmarshal(payload.getPayloadDatahandler());
    }

    private String getInstanceIdentifierByType(StandardBusinessDocumentHeader sbdh, String type) {
        return sbdh.getBusinessScope().getScope().stream().filter(s -> type.equals(s.getType())).findFirst().get().getInstanceIdentifier();
    }

    private String getFirstValueByClassificationScheme(ExtrinsicObjectType extrinsicObject, String classificationScheme) {
        var first = extrinsicObject.getClassification().stream().filter(eo -> classificationScheme.equals(eo.getClassificationScheme())).findFirst();
        if (first.isPresent()) {
            return first.get().getSlot().get(0).getValueList().getValue().get(0);
        } else {
            return null;
        }
    }

    private String getFirstSlotValueBySlotName(ExtrinsicObjectType extrinsicObject, String name) {
        Optional<SlotType1> first = extrinsicObject.getSlot().stream().filter(eo -> name.equals(eo.getName())).findFirst();
        if (first.isPresent()) {
            return first.get().getValueList().getValue().get(0);
        } else {
            return null;
        }
    }

    private Submission getGatewaySubmission(StandardBusinessDocumentHeader sbdh) {
        return SubmissionFactory.submission(gatewayProperties, "medcom-edelivery-dds (funktionscertifikat)", null, TEST_ACTION)
                .setService(BDX_NOPROCESS)
                .setOriginalSender("0088:9999999999902", ISO6523_ACTORID_UPIS)
                .setFinalRecipient("0088:9999999999901", ISO6523_ACTORID_UPIS)
                .addPayload(PayloadUtil.getXmlContent("test-messages/full-sbdh-example.xml"));
//                .addPayload(PayloadUtil.getXmlContent("test-messages/regionh_samples/XDIS13_SBDH.xml"));
//                .addPayload(PayloadUtil.getXmlContent("test-messages/regionh_samples/XDIS17_SBDH.xml"));
//                .addPayload(PayloadUtil.getXmlContent("test-messages/regionh_samples/XDIS18_SBDH.xml"));
//                .addPayload(PayloadUtil.getXmlContent("test-messages/regionh_samples/XDIS20_SBDH.xml"));
//                .addPayload(PayloadUtil.getXmlContent("test-messages/regionh_samples/XDIS21_SBDH.xml"));
    }

}
