package dk.medcom.edelivery.model.mapping;

import dk.medcom.edelivery.integration.dds.DDSConfigurationProperties;
import dk.medcom.edelivery.model.Metadata;
import dk.medcom.edelivery.model.RegistrationMetadata;
import dk.medcom.edelivery.util.Localization;
import dk.medcom.edelivery.util.Transformation;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.*;
import org.openehealth.ipf.commons.ihe.xds.core.requests.RegisterDocumentSet;
import org.openehealth.ipf.commons.ihe.xds.core.stub.ebrs30.lcm.SubmitObjectsRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static dk.medcom.edelivery.util.Localization.toDanishLocalizedString;

@Component
public class RegisterDocumentSetMetadataMapper {

    public static final int SERVICE_START_PREPONE_HOURS = 1;
    private final DDSConfigurationProperties properties;

    public RegisterDocumentSetMetadataMapper(DDSConfigurationProperties properties) {
        this.properties = properties;
    }

    public SubmitObjectsRequest toSubmitObjectsRequest(Metadata metadata) {
        var registrationMetadata = new RegistrationMetadata(metadata);

        SubmissionSet submissionSet = getSubmissionSet(registrationMetadata);
        DocumentEntry documentEntry = getDocumentEntry(registrationMetadata);

        Association association = getAssociation(documentEntry, submissionSet);

        RegisterDocumentSet registerDocumentSet = new RegisterDocumentSet();
        registerDocumentSet.setSubmissionSet(submissionSet);
        registerDocumentSet.getDocumentEntries().add(documentEntry);
        registerDocumentSet.getAssociations().add(association);

        return Transformation.transform(registerDocumentSet);
    }

    private Association getAssociation(DocumentEntry documentEntry, SubmissionSet submissionSet) {
        Association association = new Association(AssociationType.HAS_MEMBER, null, submissionSet.getEntryUuid(), documentEntry.getEntryUuid());
        association.assignEntryUuid();
        association.setAvailabilityStatus(AvailabilityStatus.APPROVED);
        association.setLabel(AssociationLabel.ORIGINAL);
        return association;
    }

    private SubmissionSet getSubmissionSet(RegistrationMetadata metaData) {
        SubmissionSet submissionSet = new SubmissionSet();

        submissionSet.setAvailabilityStatus(AvailabilityStatus.APPROVED);
        submissionSet.setHomeCommunityId(properties.getHomeCommunityId());

        submissionSet.assignUniqueId();
        submissionSet.assignEntryUuid();

        // TODO: 14-12-2020 is type code and content type code the same?
        submissionSet.setContentTypeCode(metaData.getTypeCode());
        // TODO: 14-12-2020: what is sourceid for this system?
        submissionSet.setSourceId("1.2.208.184.100.9");

        submissionSet.setSubmissionTime(metaData.getSubmissionTime());
        submissionSet.setPatientId(metaData.getPatientIdentifier());
        submissionSet.setAuthor(metaData.getAuthor());
        submissionSet.setTitle(metaData.getTitle());
        return submissionSet;
    }

    private DocumentEntry getDocumentEntry(RegistrationMetadata metadata) {
        DocumentEntry documentEntry = new DocumentEntry();

        documentEntry.setType(DocumentEntryType.STABLE);

        documentEntry.setRepositoryUniqueId(properties.getRepositoryUniqueId());
        documentEntry.setHomeCommunityId(properties.getHomeCommunityId());

        documentEntry.setFormatCode(metadata.getFormatCode());
        documentEntry.setTypeCode(metadata.getTypeCode());

        documentEntry.setHealthcareFacilityTypeCode(metadata.getHealthCareFacilityTypeCode());
        // TODO: 08-12-2020: find correct practice setting code
        documentEntry.setPracticeSettingCode(new Code("408443003", toDanishLocalizedString("almen medicin"), "2.16.840.1.113883.6.96"));

        documentEntry.setClassCode(metadata.getClassCode());
        documentEntry.getConfidentialityCodes().add(metadata.getConfidentialityCode());

        documentEntry.setLanguageCode(metadata.getLanguageCode());

        documentEntry.setMimeType(metadata.getMimeType());
        documentEntry.setSize(metadata.getSize());
        documentEntry.setHash(metadata.getHash());

        documentEntry.getAuthors().add(metadata.getAuthor());

        documentEntry.setTitle(metadata.getTitle());

        documentEntry.assignEntryUuid();
        documentEntry.setUniqueId(metadata.getUniqueId());

        documentEntry.setCreationTime(metadata.getSubmissionTime());

        documentEntry.setPatientId(metadata.getPatientIdentifier());
        documentEntry.setSourcePatientId(metadata.getPatientIdentifier());
        documentEntry.setSourcePatientInfo(metadata.getPatientInfo());

        // FIXME: 29-12-2020 for debugging
        var now = LocalDateTime.now();
        documentEntry.setServiceStartTime(Localization.toTimeStamp(now.minusHours(SERVICE_START_PREPONE_HOURS)));
        documentEntry.setServiceStopTime(Localization.toTimeStamp(now));

        return documentEntry;
    }
}
