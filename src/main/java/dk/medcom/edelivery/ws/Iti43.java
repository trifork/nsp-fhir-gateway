package dk.medcom.edelivery.ws;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import dk.medcom.edelivery.fhir.IdUtil;
import dk.medcom.edelivery.fhir.MessageBundleFacade;
import dk.medcom.edelivery.integration.dds.DDSConfigurationProperties;
import dk.medcom.edelivery.util.Transformation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.r4.model.Bundle;
import org.openehealth.ipf.commons.ihe.xds.core.ebxml.ebxml30.RetrieveDocumentSetRequestType;
import org.openehealth.ipf.commons.ihe.xds.core.ebxml.ebxml30.RetrieveDocumentSetResponseType;
import org.openehealth.ipf.commons.ihe.xds.core.requests.DocumentReference;
import org.openehealth.ipf.commons.ihe.xds.core.responses.*;
import org.openehealth.ipf.commons.ihe.xds.iti43.Iti43PortType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.jws.WebService;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.Addressing;
import javax.xml.ws.soap.MTOM;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_MTOM_BINDING;

@WebService(targetNamespace = "urn:ihe:iti:xds-b:2007", name = "DocumentRepository_PortType", portName = "DocumentRepository_Port_Soap12")
@BindingType(value = SOAP12HTTP_MTOM_BINDING)
@Addressing
@MTOM
@Service
public class Iti43 implements Iti43PortType {

    private static final Logger log = LogManager.getLogger(Iti43.class);

    private final IParser xmlParser;

    private final MessageBundleFacade messageBundleFacade;
    private final DDSConfigurationProperties properties;

    public Iti43() {
        // required, but unused
        throw new UnsupportedOperationException("No args constructor should not be used");
    }

    @Autowired
    public Iti43(MessageBundleFacade messageBundleFacade, DDSConfigurationProperties properties, FhirContext fhirContext) {
        this.messageBundleFacade = messageBundleFacade;
        this.properties = properties;
        this.xmlParser = fhirContext.newXmlParser();
    }

    @Override
    public RetrieveDocumentSetResponseType documentRepositoryRetrieveDocumentSet(RetrieveDocumentSetRequestType body) {

        var documentSet = Transformation.transform(body);

        List<RetrievedDocument> retrievedDocuments = new ArrayList<>();
        List<ErrorInfo> errors = new ArrayList<>();

        for (var documentReference : documentSet.getDocuments()) {
            try {
                retrievedDocuments.add(fetchDocument(documentReference));
            } catch (RetrieveDocumentError error) {
                errors.add(error.getErrorInfo());
            } catch (Exception e) {
                errors.add(getDefaultErrorInfo(e));
            }
        }

        return createResponse(retrievedDocuments, errors);
    }

    private RetrieveDocumentSetResponseType createResponse(List<RetrievedDocument> retrievedDocuments, List<ErrorInfo> errors) {
        var response = new RetrievedDocumentSet();
        response.getDocuments().addAll(retrievedDocuments);
        response.setErrors(errors);
        response.setStatus(getStatus(retrievedDocuments, errors));

        return Transformation.transform(response);
    }

    private Status getStatus(List<RetrievedDocument> retrievedDocuments, List<ErrorInfo> errors) {
        if (retrievedDocuments.isEmpty())
            return Status.FAILURE;
        else if (errors.isEmpty())
            return Status.SUCCESS;
        else
            return Status.PARTIAL_SUCCESS;
    }

    private ErrorInfo getDefaultErrorInfo(Exception e) {
        return new ErrorInfo(ErrorCode.REPOSITORY_ERROR, e.getMessage(), Severity.ERROR, null, null);
    }

    private RetrievedDocument fetchDocument(DocumentReference documentReference) {
        // TODO: 29-12-2020 : dds does not propagate homecommunity id when registering document
        if (!Objects.equals(documentReference.getHomeCommunityId(), properties.getHomeCommunityId()))
            log.warn(RetrieveDocumentError.invalidHomeCommunity(documentReference).getErrorInfo().getCodeContext());
//            throw RetrieveDocumentError.invalidHomeCommunity(documentReference);

        if (!Objects.equals(documentReference.getRepositoryUniqueId(), properties.getRepositoryUniqueId()))
            throw RetrieveDocumentError.invalidRepository(documentReference);

        var document = new RetrievedDocument();
        document.setRequestData(documentReference);
        document.setDataHandler(getDataHandler(documentReference));
        return document;
    }

    private DataHandler getDataHandler(DocumentReference documentReference) {
        return getMessageBundleDataHandler(documentReference)
                .orElseThrow(() -> RetrieveDocumentError.documentNotFound(documentReference));
    }

    private Optional<DataHandler> getMessageBundleDataHandler(DocumentReference documentReference) {
        return getMessageBundle(documentReference.getDocumentUniqueId())
                .map(xmlParser::encodeResourceToString)
                .map(str -> getDataHandler(str.getBytes(UTF_8)));
    }

    private DataHandler getDataHandler(byte[] bytes) {
        return new DataHandler(new ByteArrayDataSource(bytes, "text/xml"));
    }

    private Optional<Bundle> getMessageBundle(String id) {
        try {
            return Optional.of(messageBundleFacade.getMessageBundle(IdUtil.fromOIDString(id)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
