package dk.medcom.edelivery.model;

import dk.medcom.edelivery.util.Localization;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.*;

public class RegistrationMetadata {

    private final Metadata metadata;

    public RegistrationMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public Timestamp getSubmissionTime() {
        return Localization.toTimeStamp(metadata.getSubmissionTime());
    }

    public String getUniqueId() {
        return metadata.getUniqueId();
    }

    public Author getAuthor() {
        var authorInfo = metadata.getProvenanceInfo().getAuthorInfo();
        var org = authorInfo.getOrganization();

        var authorOrganisation = new Organization(
                org.getDisplayName(),
                org.getCode(),
                new AssigningAuthority(org.getCodeSystem())
        );

        var author = new Author();
        author.getAuthorInstitution().add(authorOrganisation);
        if (authorInfo.getName() != null)
            author.setAuthorPerson(new Person(null, toXcnName(authorInfo.getName())));

        return author;
    }

    public Identifiable getPatientIdentifier() {
        return new Identifiable(metadata.getCpr(), new AssigningAuthority("1.2.208.176.1.2"));
    }


    public LocalizedString getTitle() {
        return Localization.toDanishLocalizedString(metadata.getTitle());
    }

    public PatientInfo getPatientInfo() {
        var metadataPatientInfo = metadata.getProvenanceInfo().getPatientInfo();
        var patientInfo = new PatientInfo();

        patientInfo.getNames().add(toXcnName(metadataPatientInfo.getName()));
        patientInfo.setDateOfBirth(Localization.toTimeStamp(metadataPatientInfo.getDateOfBirth()));
        patientInfo.setGender(metadataPatientInfo.getGender());

        return patientInfo;
    }

    private XcnName toXcnName(Metadata.PersonName personName) {
        return new XcnName(personName.getLast(), personName.getFirst(), null, null, null, null);
    }

    public String getMimeType() {
        return metadata.getPayloadInfo().getMimeType();
    }

    public Long getSize() {
        return metadata.getPayloadInfo().getSize();
    }

    public String getHash() {
        return metadata.getPayloadInfo().getHash();
    }

    public Code getFormatCode() {
        return metadata.getDescription().getFormatCode().toXdsCode();
    }

    public Code getTypeCode() {
        return metadata.getDescription().getTypeCode().toXdsCode();
    }

    public Code getHealthCareFacilityTypeCode() {
        return metadata.getDescription().getHealthCareFacilityTypeCode().toXdsCode();
    }

    public Code getClassCode() {
        return metadata.getDescription().getClassCode().toXdsCode();
    }

    public Code getConfidentialityCode() {
        return metadata.getDescription().getConfidentialityCode().toXdsCode();
    }

    public String getLanguageCode() {
        return metadata.getDescription().getLanguageCode();
    }
}
