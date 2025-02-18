package dk.medcom.edelivery.ws;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import dk.medcom.edelivery.UseTestContainers;
import dk.medcom.edelivery.fhir.IdUtil;
import dk.medcom.edelivery.fhir.MessageBundleFacade;
import dk.medcom.edelivery.integration.dds.DDSConfigurationProperties;
import dk.medcom.edelivery.integration.dgws.DGWSHeaderFactory;
import dk.medcom.edelivery.model.mapping.PayloadInfoMapper;
import dk.medcom.edelivery.util.Hashing;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Communication;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.StringType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openehealth.ipf.commons.ihe.xds.core.ebxml.ebxml30.EbXMLFactory30;
import org.openehealth.ipf.commons.ihe.xds.core.ebxml.ebxml30.RetrieveDocumentSetRequestType;
import org.openehealth.ipf.commons.ihe.xds.core.ebxml.ebxml30.RetrieveDocumentSetResponseType;
import org.openehealth.ipf.commons.ihe.xds.core.requests.DocumentReference;
import org.openehealth.ipf.commons.ihe.xds.core.requests.RetrieveDocumentSet;
import org.openehealth.ipf.commons.ihe.xds.core.responses.Status;
import org.openehealth.ipf.commons.ihe.xds.core.transform.requests.RetrieveDocumentSetRequestTransformer;
import org.openehealth.ipf.commons.ihe.xds.iti43.Iti43PortType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import javax.activation.DataHandler;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@UseTestContainers
class Iti43IT {

    private static final RetrieveDocumentSetRequestTransformer transformer = new RetrieveDocumentSetRequestTransformer(new EbXMLFactory30());
    private static final IParser parser = FhirContext.forR4().newXmlParser();

    @LocalServerPort
    private int port;

    @Autowired
    private MessageBundleFacade messageBundleFacade;

    @Autowired
    private DGWSHeaderFactory headerFactory;

    @Autowired
    private DDSConfigurationProperties properties;

    @Autowired
    private PayloadInfoMapper payloadInfoMapper;

    private Iti43PortType secureClient;
    private Iti43PortType insecureClient;

    @BeforeEach
    void setUp() {
        secureClient = Iti43PortFactory.createWithSecurity("http://localhost:" + port + "/ws", headerFactory);
//        secureClient = Iti43PortFactory.createWithSecurity("https://medcom-gateway.nsptest.medcom.dk/ws", headerFactory);
        insecureClient = Iti43PortFactory.createWithoutSecurity("http://localhost:" + port + "/ws");
    }

    @Test
    void retrieveMessageBundle() {
        String message = "Ok, Computer";

        var communication = new Communication();
        communication.addPayload()
                .setContent(new StringType(message));

        var bundle = new Bundle()
                .setType(Bundle.BundleType.MESSAGE);
        bundle.addEntry().setResource(communication);

        var saved = messageBundleFacade.saveMessageBundle(bundle);
        var id = saved.getIdElement();

        var payloadInfo = payloadInfoMapper.calculatePayloadInfo(saved);

        RetrieveDocumentSet request = getRetrieveDocumentSet(id);

        RetrieveDocumentSetResponseType responseType = secureClient.documentRepositoryRetrieveDocumentSet(transform(request));

        var rawBytes = readDocumentAsBytes(responseType.getDocumentResponse().get(0).getDocument());
        Bundle retrievedBundle = parseToBundle(rawBytes);
        assertAll(
                () -> assertEquals(Status.SUCCESS.getOpcode30(), responseType.getRegistryResponse().getStatus()),
                () -> assertEquals(1, responseType.getDocumentResponse().size()),
                () -> assertEquals(id.getIdPart(), IdUtil.fromOIDString(responseType.getDocumentResponse().get(0).getDocumentUniqueId()).getIdPart()),
                () -> assertEquals(id.getIdPart(), retrievedBundle.getIdElement().getIdPart()),
                () -> assertEquals(message, ((Communication) retrievedBundle.getEntryFirstRep().getResource()).getPayloadFirstRep().getContentStringType().toString()),
                () -> assertEquals(payloadInfo.getMimeType(), responseType.getDocumentResponse().get(0).getMimeType()),
                () -> assertEquals(payloadInfo.getSize(), rawBytes.length),
                () -> assertEquals(payloadInfo.getHash(), Hashing.sha1HexBinary(rawBytes))
        );
    }

    @Test
    void testDocumentUnavailable() {
        var requestType = transform(getRetrieveDocumentSet(new IdType(UUID.randomUUID().toString())));

        var responseType = secureClient.documentRepositoryRetrieveDocumentSet(requestType);

        assertAll(
                () -> assertEquals(Status.FAILURE.getOpcode30(), responseType.getRegistryResponse().getStatus()),
                () -> assertEquals(0, responseType.getDocumentResponse().size()),
                () -> assertEquals(1, responseType.getRegistryResponse().getRegistryErrorList().getRegistryError().size()),
                () -> assertEquals("XDSDocumentUniqueIdError", responseType.getRegistryResponse().getRegistryErrorList().getRegistryError().get(0).getErrorCode())
        );
    }

    @Test @Disabled
    void testInvalidHomeCommunityId() {
        var request = getRetrieveDocumentSet(new IdType(UUID.randomUUID().toString()));

        request.getDocuments().forEach(d -> d.setHomeCommunityId("2.25.0"));
        var requestType = transform(request);

        var responseType = secureClient.documentRepositoryRetrieveDocumentSet(requestType);

        assertAll(
                () -> assertEquals(Status.FAILURE.getOpcode30(), responseType.getRegistryResponse().getStatus()),
                () -> assertEquals(0, responseType.getDocumentResponse().size()),
                () -> assertEquals(1, responseType.getRegistryResponse().getRegistryErrorList().getRegistryError().size()),
                () -> assertEquals("XDSInvalidRequestException", responseType.getRegistryResponse().getRegistryErrorList().getRegistryError().get(0).getErrorCode())
        );
    }

    @Test
    void testInvalidRepositoryUniqueyId() {
        var request = getRetrieveDocumentSet(new IdType(UUID.randomUUID().toString()));

        request.getDocuments().forEach(d -> d.setRepositoryUniqueId("2.25.0"));
        var requestType = transform(request);

        var responseType = secureClient.documentRepositoryRetrieveDocumentSet(requestType);

        assertAll(
                () -> assertEquals(Status.FAILURE.getOpcode30(), responseType.getRegistryResponse().getStatus()),
                () -> assertEquals(0, responseType.getDocumentResponse().size()),
                () -> assertEquals(1, responseType.getRegistryResponse().getRegistryErrorList().getRegistryError().size()),
                () -> assertEquals("XDSInvalidRequestException", responseType.getRegistryResponse().getRegistryErrorList().getRegistryError().get(0).getErrorCode())

        );
    }

    @Test
    void requestWithoutSecurityAreRejected() {
        RetrieveDocumentSetRequestType requestType = transform(getRetrieveDocumentSet(new IdType(UUID.randomUUID().toString())));

        var thrown = assertThrows(SOAPFaultException.class, () -> insecureClient.documentRepositoryRetrieveDocumentSet(requestType));

        assertTrue(thrown.getFault().getFaultString().contains("CertificateMissing"));

    }

    private Bundle readDocumentAsBundle(DataHandler dataHandler) {
        try (InputStream inputStream = dataHandler.getInputStream()) {
            return parser.parseResource(Bundle.class, inputStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Bundle parseToBundle(byte[] in) {
        try (InputStream inputStream = new ByteArrayInputStream(in)) {
            return parser.parseResource(Bundle.class, inputStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private byte[] readDocumentAsBytes(DataHandler dataHandler) {
        try (InputStream inputStream = dataHandler.getInputStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private RetrieveDocumentSet getRetrieveDocumentSet(IIdType id) {
        RetrieveDocumentSet request = new RetrieveDocumentSet();

        String repositoryUniqueId = properties.getRepositoryUniqueId();
        String homeCommunityId = properties.getHomeCommunityId();
        String documentUniqueId = IdUtil.toOIDString(id);

        request.getDocuments().add(new DocumentReference(repositoryUniqueId, documentUniqueId, homeCommunityId));
        return request;
    }

    private RetrieveDocumentSetRequestType transform(RetrieveDocumentSet request) {
        return (RetrieveDocumentSetRequestType) transformer.toEbXML(request).getInternal();
    }
}
