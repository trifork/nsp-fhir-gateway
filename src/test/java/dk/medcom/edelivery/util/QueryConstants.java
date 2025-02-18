package dk.medcom.edelivery.util;

public class QueryConstants {

    // Constants for FindDocuments query
    public static final String XDSDocumentEntryPatientId = "$XDSDocumentEntryPatientId";
    public static final String XDSDocumentEntryClassCode = "$XDSDocumentEntryClassCode";
    public static final String XDSDocumentEntryTypeCode = "$XDSDocumentEntryTypeCode";
    public static final String XDSDocumentEntryPracticeSettingCode = "$XDSDocumentEntryPracticeSettingCode";
    public static final String XDSDocumentEntryCreationTimeFrom = "$XDSDocumentEntryCreationTimeFrom";
    public static final String XDSDocumentEntryCreationTimeTo = "$XDSDocumentEntryCreationTimeTo";
    public static final String XDSDocumentEntryServiceStartTimeFrom = "$XDSDocumentEntryServiceStartTimeFrom";
    public static final String XDSDocumentEntryServiceStartTimeTo = "$XDSDocumentEntryServiceStartTimeTo";
    public static final String XDSDocumentEntryServiceStopTimeFrom = "$XDSDocumentEntryServiceStopTimeFrom";
    public static final String XDSDocumentEntryServiceStopTimeTo = "$XDSDocumentEntryServiceStopTimeTo";
    public static final String XDSDocumentEntryHealthcareFacilityTypeCode = "$XDSDocumentEntryHealthcareFacilityTypeCode";
    public static final String XDSDocumentEntryEventCodeList = "$XDSDocumentEntryEventCodeList";
    public static final String XDSDocumentEntryConfidentialityCode = "$XDSDocumentEntryConfidentialityCode";
    public static final String XDSDocumentEntryAuthorPerson = "$XDSDocumentEntryAuthorPerson";
    public static final String XDSDocumentEntryFormatCode = "$XDSDocumentEntryFormatCode";
    public static final String XDSDocumentEntryStatus = "$XDSDocumentEntryStatus";
    public static final String XDSDocumentEntryType = "$XDSDocumentEntryType";

    // Various constants
    public static final String QUERY_TYPE_FINDDOCUMENTS = "urn:uuid:14d4debf-8f97-4251-9a74-a90016b0af0d";
    public static final String QUERY_TYPE_GETDOCUMENTS = "urn:uuid:5c4f972b-d56b-40ac-a5fc-c8ca9b40b9d4";
    public static final String ENTRY_TYPE_STABLE_DOCUMENT = "('urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1')";
}
