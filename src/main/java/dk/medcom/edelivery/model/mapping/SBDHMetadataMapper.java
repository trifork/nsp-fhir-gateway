package dk.medcom.edelivery.model.mapping;

import dk.medcom.edelivery.model.Metadata;
import dk.medcom.edelivery.model.ScopeIdentifiers;
import dk.medcom.edelivery.model.ScopeTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Hl7v2Based;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Identifiable;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Organization;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.XcnName;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.Partner;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.PartnerIdentification;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.Scope;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Supplier;

public class SBDHMetadataMapper {

    private static final Logger log = LogManager.getLogger(SBDHMetadataMapper.class);

    private final StandardBusinessDocumentHeader sbdh;
    private final SBDHAdapter adapter;

    private SBDHMetadataMapper(StandardBusinessDocumentHeader standardBusinessDocumentHeader) {
        this.sbdh = standardBusinessDocumentHeader;
        this.adapter = SBDHAdapter.from(sbdh);
    }

    public static Metadata toMetadata(StandardBusinessDocumentHeader sbdh) {
        return new SBDHMetadataMapper(sbdh).toMetadata();
    }

    private Metadata toMetadata() {
        return new Metadata()
                .setEntryId(getEntryUUID())
                .setCpr(getPatientId())
                .setTitle(getTitle())
                .setAddressingInfo(new Metadata.AddressingInfo()
                        .setSender(getSender())
                        .setReceiver(getReceiver()))
                .setProvenanceInfo(new Metadata.ProvenanceInfo()
                        .setPatientInfo(getSourcePatientInfo())
                        .setSourcePatientId(getSourcePatientId())
                        .setAuthorInfo(getAuthorInfo())
                        .setLegalAuthenticator(getLegaAuthenticator())
                        .setCreationTime(getCreationTime()))
                .setDescription(new Metadata.Description()
                        .setClassCode(getClassCode())
                        .setTypeCode(getTypeCode())
                        .setConfidentialityCode(getConfidentialityCode())
                        .setFormatCode(getFormatCode())
                        .setHealthCareFacilityTypeCode(getHealthCareFacilityTypeCode())
                        .setLanguageCode(getLanguageCode())
                        .setPracticeSettingCode(getPracticeSettingCode()))
                .setDocumentInfo(new Metadata.DocumentInfo()
                        .setCreationDateTime(getDocumentCreationDateTime())
                        .setIdentifier(getInstanceIdentifier())
                        .setStandard(getStandard())
                        .setType(getType())
                        .setTypeVersion(getTypeVersion()))
                .setPayloadInfo(new Metadata.PayloadInfo()
                        .setMimeType(getMimeType()))
                .setExchangeInfo(new Metadata.ExchangeInfo()
                        .setDocumentId(getDocumentId())
                        .setProcessId(getProcessId())
                        .setSenderId(getSenderId())
                        .setReceiverId(getReceiverId())
                        .setStatisticalCode(getStatisticalCode())
                        .setIntendedRecipient(getIntendedRecipient()))
                .setSubmissionTime(LocalDateTime.now());
    }

    private String getReceiver() {
        return findFirstIdentifier(sbdh.getReceiver());
    }

    private String getSender() {
        return findFirstIdentifier(sbdh.getSender());
    }

    private String findFirstIdentifier(List<Partner> partners) {
        return partners.stream()
                .map(Partner::getIdentifier)
                .map(PartnerIdentification::getValue)
                .findFirst().orElseThrow();
    }


    private String getTypeVersion() {
        return sbdh.getDocumentIdentification().getTypeVersion();
    }

    private String getType() {
        return sbdh.getDocumentIdentification().getType();
    }

    private String getStandard() {
        return sbdh.getDocumentIdentification().getStandard();
    }

    private String getInstanceIdentifier() {
        return sbdh.getDocumentIdentification().getInstanceIdentifier();
    }

    private ZonedDateTime getDocumentCreationDateTime() {
        return sbdh.getDocumentIdentification()
                .getCreationDateAndTime()
                .toGregorianCalendar()
                .toZonedDateTime();
    }

    // == Business scopes == //

    // TODO: 07-12-2020 fallback / throw / null for missing scopes?

    // == eDelivery Exchange == //
    private String getDocumentId() {
        return adapter.getInstanceIdentifier(ScopeTypes.DOCUMENTID, ScopeIdentifiers.DK_MESSAGING_DOCID)
                .orElseThrow();
    }

    private String getProcessId() {
        return adapter.getInstanceIdentifier(ScopeTypes.PROCESSID, ScopeIdentifiers.DK_MESSAGING_PROCID)
                .orElseThrow();
    }


    // == Healthcare Exchange == //
    // TODO: 17-12-2020 handle dynamic identifier for senderid and receiverid
    private String getSenderId() {
        return adapter.getScopes(ScopeTypes.SENDERID)
                .findFirst()
                .map(Scope::getInstanceIdentifier)
                .orElseThrow();
    }

    private String getReceiverId() {
        return adapter.getScopes(ScopeTypes.RECEIVERID)
                .findFirst()
                .map(Scope::getInstanceIdentifier)
                .orElseThrow();
    }

    private String getStatisticalCode() {
        return adapter.getInstanceIdentifier(ScopeTypes.STATISTICALCODE, ScopeIdentifiers.DK_MEDCOM_MESSAGING)
                .orElseThrow();
    }


    // == Document Share Exchange == //

    // TODO: 08-12-2020 :  should leagal authenticato be part of this object?

    /**
     * The humans and/or machines that authored the
     * document. This attribute contains the sub-attributes:
     * authorInstitution, authorPerson, authorRole,
     * authorSpecialty and authorTelecommunication.
     */
    private Metadata.AuthorInfo getAuthorInfo() {
        var organization = adapter.getInstanceIdentifier(ScopeTypes.AUTHORINSTITUTION, ScopeIdentifiers.DK_DDS_AUTHOR_INSTITUTION)
                .map(string -> Hl7v2Based.parse(string, Organization.class))
                .orElseThrow();

        return new Metadata.AuthorInfo().setOrganization(new Metadata.Coding(
                organization.getIdNumber(), organization.getAssigningAuthority().getUniversalId(), organization.getOrganizationName()));
    }

    /**
     * The code specifying the high-level use
     * classification of the document type (e.g., Report,
     * Summary, Images, Treatment Plan, Patient
     * Preferences, Workflow).
     */
    private Metadata.Coding getClassCode() {
        return getCoding(ScopeTypes.CLASSCODE);
    }

    /**
     * The code specifying the level of confidentiality of
     * the document.
     */
    private Metadata.Coding getConfidentialityCode() {
        return getCoding(ScopeTypes.CONFIDENTIALITYCODE);
    }

    /**
     * The time the author created the document
     */
    private LocalDateTime getCreationTime() {
        return adapter.getInstanceIdentifier(ScopeTypes.CREATIONTIME, ScopeIdentifiers.DK_DDS_METADATA)
                .map(value -> LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
                .orElseThrow(validationError(ScopeTypes.CREATIONTIME, ScopeIdentifiers.DK_DDS_METADATA));
    }

    /**
     * A globally unique identifier used to manage the entry.
     */
    private String getEntryUUID() {
        return adapter.getInstanceIdentifier(ScopeTypes.ENTRYUUID, ScopeIdentifiers.DK_DDS_METADATA)
                .orElseThrow(validationError(ScopeTypes.ENTRYUUID, ScopeIdentifiers.DK_DDS_METADATA));
    }

    @NotNull
    private Supplier<SBDHIntegrityException> validationError(String scopeType, String scopeIdentifier) {
        return () -> new SBDHIntegrityException("Missing scope type '" + scopeType + "' with identifier '" + scopeIdentifier + "'");
    }

    /**
     * The code specifying the detailed technical format of the document.
     */
    private Metadata.Coding getFormatCode() {
        return getCoding(ScopeTypes.FORMATCODE);
    }

    /**
     * This code represents the type of organizational
     * setting of the clinical encounter during which the
     * documented act occurred.
     */
    private Metadata.Coding getHealthCareFacilityTypeCode() {
        return getCoding(ScopeTypes.HEALTHCARE_FACILITY_TYPE_CODE);
    }

    /**
     * Specifies the human language of character data in
     * the document.
     */
    private String getLanguageCode() {
        return adapter.getInstanceIdentifier(ScopeTypes.LANGUAGECODE, ScopeIdentifiers.DK_DDS_METADATA)
                .orElse("da-DK");
    }

    /**
     * Represents a participant within an authorInstitution
     * who has legally authenticated or attested the
     * document.
     */
    private String getLegaAuthenticator() {
        return adapter.getInstanceIdentifier(ScopeTypes.LEGALAUTHENTICATOR, ScopeIdentifiers.DK_DDS_METADATA)
                .orElseThrow(validationError(ScopeTypes.LEGALAUTHENTICATOR, ScopeIdentifiers.DK_DDS_METADATA));
    }

    /**
     * MIME type of the document
     */
    private String getMimeType() {
        return adapter.getInstanceIdentifier(ScopeTypes.MIMETYPE, ScopeIdentifiers.DK_DDS_METADATA)
                .orElseThrow(validationError(ScopeTypes.MIMETYPE, ScopeIdentifiers.DK_DDS_METADATA));
    }

    // TODO: 08-12-2020 : this should always be stable?
    /**
     * The type of DocumentEntry (e.g., On-Demand DocumentEntry).
     */
    private String getObjectType() {
        return adapter.getInstanceIdentifier(ScopeTypes.OBJECTTYPE, ScopeIdentifiers.DK_DDS_METADATA)
                .orElseThrow(validationError(ScopeTypes.OBJECTTYPE, ScopeIdentifiers.DK_DDS_METADATA));
    }

    /**
     * The patientId represents the subject of care of the document.
     */
    private String getPatientId() {
        return adapter.getInstanceIdentifier(ScopeTypes.PATIENTID, ScopeIdentifiers.DK_DDS_METADATA)
                .map(SBDHMetadataMapper::toCpr)
                .orElseThrow(validationError(ScopeTypes.PATIENTID, ScopeIdentifiers.DK_DDS_METADATA));
    }

    /**
     * The code specifying the clinical specialty where the
     * act that resulted in the document was performed
     * (e.g., Family Practice, Laboratory, Radiology).
     */
    private String getPracticeSettingCode() {
        return adapter.getInstanceIdentifier(ScopeTypes.PRACTICESETTINGCODE, ScopeIdentifiers.DK_DDS_METADATA)
                .orElseThrow(validationError(ScopeTypes.PRACTICESETTINGCODE, ScopeIdentifiers.DK_DDS_METADATA));
    }

    /**
     * The sourcePatientId represents the subject of care’s
     * medical record identifier (e.g., Patient Id) in the
     * local patient identifier domain of the creating
     * entity.
     */
    private String getSourcePatientId() {
        return adapter.getInstanceIdentifier(ScopeTypes.SOURCEPATIENTID, ScopeIdentifiers.DK_DDS_METADATA)
                .orElseThrow(validationError(ScopeTypes.SOURCEPATIENTID, ScopeIdentifiers.DK_DDS_METADATA));
    }

    /**
     * This attribute contains demographic information of
     * the source patient to whose medical record this
     * document belongs.
     */
    private Metadata.PatientInfo getSourcePatientInfo() {
        return new Metadata.PatientInfo()
                .setName(adapter.getInstanceIdentifier(ScopeTypes.SOURCEPATIENTINFO, ScopeIdentifiers.DK_DDS_METADATA_PID_5).map(SBDHMetadataMapper::toPersonName).orElseThrow(validationError(ScopeTypes.SOURCEPATIENTINFO, ScopeIdentifiers.DK_DDS_METADATA_PID_5)))
                .setDateOfBirth(adapter.getInstanceIdentifier(ScopeTypes.SOURCEPATIENTINFO, ScopeIdentifiers.DK_DDS_METADATA_PID_7)
                        .map(value -> LocalDate.parse(value, DateTimeFormatter.BASIC_ISO_DATE).atStartOfDay())
                        .orElseThrow(validationError(ScopeTypes.SOURCEPATIENTINFO, ScopeIdentifiers.DK_DDS_METADATA_PID_7)))
                .setGender(adapter.getInstanceIdentifier(ScopeTypes.SOURCEPATIENTINFO, ScopeIdentifiers.DK_DDS_METADATA_PID_8).orElseThrow(validationError(ScopeTypes.SOURCEPATIENTINFO, ScopeIdentifiers.DK_DDS_METADATA_PID_8)));
    }

    /**
     * The title of the document.
     */
    private String getTitle() {
        return adapter.getInstanceIdentifier(ScopeTypes.TITLE, ScopeIdentifiers.DK_DDS_METADATA).orElseThrow(validationError(ScopeTypes.TITLE, ScopeIdentifiers.DK_DDS_METADATA));
    }

    /**
     * The code specifying the precise type of document
     * from the user perspective (e.g., LOINC code).
     */
    private Metadata.Coding getTypeCode() {
        return getCoding(ScopeTypes.TYPECODE);
    }

    /**
     * The organizations or persons for whom the SubmissionSet is intended.
     */
    private String getIntendedRecipient() {
        return adapter.getInstanceIdentifier(ScopeTypes.INTENDEDRECIPIENT, ScopeIdentifiers.DK_DDS_METADATA)
                .orElseThrow(validationError(ScopeTypes.INTENDEDRECIPIENT, ScopeIdentifiers.DK_DDS_METADATA));
    }

    // == utility methods == //

    private Metadata.Coding getCoding(String type) {
        return new Metadata.Coding(
                adapter.getInstanceIdentifier(type, ScopeIdentifiers.DK_DDS_CODE).orElseThrow(validationError(type, ScopeIdentifiers.DK_DDS_CODE)),
                adapter.getInstanceIdentifier(type, ScopeIdentifiers.DK_DDS_CODE_SYSTEM).orElseThrow(validationError(type, ScopeIdentifiers.DK_DDS_CODE_SYSTEM)),
                adapter.getInstanceIdentifier(type, ScopeIdentifiers.DK_DDS_DISPLAY_NAME).orElseThrow(validationError(type, ScopeIdentifiers.DK_DDS_DISPLAY_NAME))
        );
    }

    public static Metadata.PersonName toPersonName(String name) {
        var parse = Hl7v2Based.parse(name, XcnName.class);

        if (parse == null) {
            logMissingSBDH(ScopeTypes.SOURCEPATIENTINFO, ScopeIdentifiers.DK_DDS_METADATA_PID_5);
        }

        return new Metadata.PersonName()
                .setFirst(parse.getGivenName())
                .setLast(parse.getFamilyName());
    }

    private static void logMissingSBDH(String scopeType, String scopeIdentifier) {
        log.warn("Missing SDBH scope data necessary for DDS sharing: Type='" + scopeType
                + "', Identifier='" + scopeIdentifier + "'");

    }

    public static String toCpr(String identifiable) {
     return Hl7v2Based.parse(identifiable, Identifiable.class).getId();
    }
}
