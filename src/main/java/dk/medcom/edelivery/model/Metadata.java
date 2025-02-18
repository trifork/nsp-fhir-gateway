package dk.medcom.edelivery.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.medcom.edelivery.util.Localization;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Code;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Metadata {

    private String entryId;
    private String uniqueId;
    private LocalDateTime submissionTime;

    private String cpr;
    private String title;

    private AddressingInfo addressingInfo;

    private PayloadInfo payloadInfo;

    private Description description;

    private DocumentInfo documentInfo;

    private ExchangeInfo exchangeInfo;

    private ProvenanceInfo provenanceInfo;

    public AddressingInfo getAddressingInfo() {
        return addressingInfo;
    }

    public Metadata setAddressingInfo(AddressingInfo addressingInfo) {
        this.addressingInfo = addressingInfo;
        return this;
    }

    public ProvenanceInfo getProvenanceInfo() {
        return provenanceInfo;
    }

    public Metadata setProvenanceInfo(ProvenanceInfo provenanceInfo) {
        this.provenanceInfo = provenanceInfo;
        return this;
    }

    public ExchangeInfo getExchangeInfo() {
        return exchangeInfo;
    }

    public Metadata setExchangeInfo(ExchangeInfo exchangeInfo) {
        this.exchangeInfo = exchangeInfo;
        return this;
    }

    public Description getDescription() {
        return description;
    }

    public Metadata setDescription(Description description) {
        this.description = description;
        return this;
    }

    public DocumentInfo getDocumentInfo() {
        return documentInfo;
    }

    public Metadata setDocumentInfo(DocumentInfo documentInfo) {
        this.documentInfo = documentInfo;
        return this;
    }

    public String getEntryId() {
        return entryId;
    }

    public Metadata setEntryId(String entryId) {
        this.entryId = entryId;
        return this;
    }

    public PayloadInfo getPayloadInfo() {
        return payloadInfo;
    }

    public Metadata setPayloadInfo(PayloadInfo payloadInfo) {
        this.payloadInfo = payloadInfo;
        return this;
    }

    public String getCpr() {
        return cpr;
    }

    public Metadata setCpr(String cpr) {
        this.cpr = cpr;
        return this;
    }

    public LocalDateTime getSubmissionTime() {
        return submissionTime;
    }

    public Metadata setSubmissionTime(LocalDateTime submissionTime) {
        this.submissionTime = submissionTime;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Metadata setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public Metadata setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
        return this;
    }

    public static class PayloadInfo {
        private String mimeType;
        private Long size;
        private String hash;

        public String getMimeType() {
            return mimeType;
        }

        public PayloadInfo setMimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public Long getSize() {
            return size;
        }

        public PayloadInfo setSize(Long size) {
            this.size = size;
            return this;
        }

        public String getHash() {
            return hash;
        }

        public PayloadInfo setHash(String hash) {
            this.hash = hash;
            return this;
        }
    }

    public static class Description {

        private Coding classCode;
        private Coding typeCode;
        private Coding confidentialityCode;
        private Coding formatCode;
        private Coding healthCareFacilityTypeCode;
        private String languageCode;
        private String practiceSettingCode;

        public Coding getTypeCode() {
            return typeCode;
        }

        public Description setTypeCode(Coding typeCode) {
            this.typeCode = typeCode;
            return this;
        }

        public Coding getConfidentialityCode() {
            return confidentialityCode;
        }

        public Description setConfidentialityCode(Coding confidentialityCode) {
            this.confidentialityCode = confidentialityCode;
            return this;
        }

        public Coding getFormatCode() {
            return formatCode;
        }

        public Description setFormatCode(Coding formatCode) {
            this.formatCode = formatCode;
            return this;
        }

        public Coding getHealthCareFacilityTypeCode() {
            return healthCareFacilityTypeCode;
        }

        public Description setHealthCareFacilityTypeCode(Coding healthCareFacilityTypeCode) {
            this.healthCareFacilityTypeCode = healthCareFacilityTypeCode;
            return this;
        }

        public Coding getClassCode() {
            return classCode;
        }

        public Description setClassCode(Coding classCode) {
            this.classCode = classCode;
            return this;
        }

        public String getLanguageCode() {
            return languageCode;
        }

        public Description setLanguageCode(String languageCode) {
            this.languageCode = languageCode;
            return this;
        }

        public String getPracticeSettingCode() {
            return practiceSettingCode;
        }

        public Description setPracticeSettingCode(String practiceSettingCode) {
            this.practiceSettingCode = practiceSettingCode;
            return this;
        }
    }

    public static class AuthorInfo {

        private Coding organization;
        private PersonName name;

        public Coding getOrganization() {
            return organization;
        }

        public AuthorInfo setOrganization(Coding organization) {
            this.organization = organization;
            return this;
        }

        public PersonName getName() {
            return name;
        }

        public AuthorInfo setName(PersonName name) {
            this.name = name;
            return this;
        }
    }

    public static class PersonName {

        private String first;
        private String last;

        public String getFirst() {
            return first;
        }

        public PersonName setFirst(String first) {
            this.first = first;
            return this;
        }

        public String getLast() {
            return last;
        }

        public PersonName setLast(String last) {
            this.last = last;
            return this;
        }
    }

    public static class PatientInfo {

        private PersonName name;
        private LocalDateTime dateOfBirth;
        private String gender;

        public PersonName getName() {
            return name;
        }

        public PatientInfo setName(PersonName name) {
            this.name = name;
            return this;
        }

        public LocalDateTime getDateOfBirth() {
            return dateOfBirth;
        }

        public PatientInfo setDateOfBirth(LocalDateTime dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public String getGender() {
            return gender;
        }

        public PatientInfo setGender(String gender) {
            this.gender = gender;
            return this;
        }
    }

    public static class Coding {
        private final String code;
        private final String codeSystem;
        private final String displayName;

        @JsonCreator
        public Coding(
                @JsonProperty("code") String code,
                @JsonProperty("codeSystem") String codeSystem,
                @JsonProperty("displayName") String displayName
        ) {
            this.code = code;
            this.codeSystem = codeSystem;
            this.displayName = displayName;
        }

        public Code toXdsCode() {
            return new Code(
                    code,
                    Localization.toDanishLocalizedString(displayName),
                    codeSystem
            );
        }

        public String getCode() {
            return code;
        }

        public String getCodeSystem() {
            return codeSystem;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public static class ExchangeInfo {
        private String documentId;
        private String processId;
        private String senderId;
        private String receiverId;
        private String statisticalCode;
        private String intendedRecipient;

        public String getDocumentId() {
            return documentId;
        }

        public ExchangeInfo setDocumentId(String documentId) {
            this.documentId = documentId;
            return this;
        }

        public String getProcessId() {
            return processId;
        }

        public ExchangeInfo setProcessId(String processId) {
            this.processId = processId;
            return this;
        }

        public String getSenderId() {
            return senderId;
        }

        public ExchangeInfo setSenderId(String senderId) {
            this.senderId = senderId;
            return this;
        }

        public String getReceiverId() {
            return receiverId;
        }

        public ExchangeInfo setReceiverId(String receiverId) {
            this.receiverId = receiverId;
            return this;
        }

        public String getStatisticalCode() {
            return statisticalCode;
        }

        public ExchangeInfo setStatisticalCode(String statisticalCode) {
            this.statisticalCode = statisticalCode;
            return this;
        }

        public String getIntendedRecipient() {
            return intendedRecipient;
        }

        public ExchangeInfo setIntendedRecipient(String intendedRecipient) {
            this.intendedRecipient = intendedRecipient;
            return this;
        }
    }

    public static class ProvenanceInfo {
        private AuthorInfo authorInfo;
        private PersonName authorName;
        private LocalDateTime creationTime;
        private String legalAuthenticator;
        private String sourcePatientId;
        private PatientInfo patientInfo;

        public AuthorInfo getAuthorInfo() {
            return authorInfo;
        }

        public ProvenanceInfo setAuthorInfo(AuthorInfo authorInfo) {
            this.authorInfo = authorInfo;
            return this;
        }

        public PersonName getAuthorName() {
            return authorName;
        }

        public ProvenanceInfo setAuthorName(PersonName authorName) {
            this.authorName = authorName;
            return this;
        }

        public LocalDateTime getCreationTime() {
            return creationTime;
        }

        public ProvenanceInfo setCreationTime(LocalDateTime creationTime) {
            this.creationTime = creationTime;
            return this;
        }

        public String getLegalAuthenticator() {
            return legalAuthenticator;
        }

        public ProvenanceInfo setLegalAuthenticator(String legalAuthenticator) {
            this.legalAuthenticator = legalAuthenticator;
            return this;
        }

        public String getSourcePatientId() {
            return sourcePatientId;
        }

        public ProvenanceInfo setSourcePatientId(String sourcePatientId) {
            this.sourcePatientId = sourcePatientId;
            return this;
        }

        public PatientInfo getPatientInfo() {
            return patientInfo;
        }

        public ProvenanceInfo setPatientInfo(PatientInfo patientInfo) {
            this.patientInfo = patientInfo;
            return this;
        }
    }

    public static class DocumentInfo {
        private String identifier;
        private String standard;
        private String type;
        private String typeVersion;
        private ZonedDateTime creationDateTime;

        public String getIdentifier() {
            return identifier;
        }

        public DocumentInfo setIdentifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        public String getStandard() {
            return standard;
        }

        public DocumentInfo setStandard(String standard) {
            this.standard = standard;
            return this;
        }

        public String getType() {
            return type;
        }

        public DocumentInfo setType(String type) {
            this.type = type;
            return this;
        }

        public String getTypeVersion() {
            return typeVersion;
        }

        public DocumentInfo setTypeVersion(String typeVersion) {
            this.typeVersion = typeVersion;
            return this;
        }

        public ZonedDateTime getCreationDateTime() {
            return creationDateTime;
        }

        public DocumentInfo setCreationDateTime(ZonedDateTime creationDateTime) {
            this.creationDateTime = creationDateTime;
            return this;
        }
    }

    public static class AddressingInfo {

        private String sender;
        private String receiver;

        public String getSender() {
            return sender;
        }

        public AddressingInfo setSender(String sender) {
            this.sender = sender;
            return this;
        }

        public String getReceiver() {
            return receiver;
        }

        public AddressingInfo setReceiver(String receiver) {
            this.receiver = receiver;
            return this;
        }
    }

}
