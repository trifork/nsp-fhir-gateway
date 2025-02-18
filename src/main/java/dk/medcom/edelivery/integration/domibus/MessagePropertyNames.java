package dk.medcom.edelivery.integration.domibus;

public class MessagePropertyNames {

    public static final String JMS_PLUGIN_PROPERTY_PREFIX = "jmsplugin";
    public static final String MPC = "mpc";
    public static final String MESSAGE_ID = "messageId";
    public static final String P1_IN_BODY = "p1InBody";
    public static final String JMS_BACKEND_MESSAGE_TYPE_PROPERTY_KEY = "messageType";
    public static final String MIME_TYPE = "MimeType";
    public static final String ACTION = "action";
    public static final String SERVICE = "service";
    public static final String SERVICE_TYPE = "serviceType";
    public static final String CONVERSATION_ID = "conversationId";
    public static final String AGREEMENT_REF = "agreementRef";
    public static final String REF_TO_MESSAGE_ID = "refToMessageId";
    public static final String FROM_PARTY_ID = "fromPartyId";
    public static final String FROM_PARTY_TYPE = "fromPartyType";
    public static final String FROM_ROLE = "fromRole";
    public static final String TO_PARTY_ID = "toPartyId";
    public static final String TO_PARTY_TYPE = "toPartyType";
    public static final String TO_ROLE = "toRole";
    public static final String PROPERTY_ORIGINAL_SENDER = "originalSender";
    public static final String PROPERTY_FINAL_RECIPIENT = "finalRecipient";
    public static final String PROPERTY_FINAL_RECIPIENT_TYPE = "finalRecipientType";
    public static final String PROPERTY_ENDPOINT = "endPointAddress";
    public static final String PROTOCOL = "protocol";
    public static final String TOTAL_NUMBER_OF_PAYLOADS = "totalNumberOfPayloads";
    public static final String MESSAGE_TYPE_SUBMIT = "submitMessage";
    public static final String MESSAGE_TYPE_SUBMIT_RESPONSE = "submitResponse";
    public static final String MESSAGE_TYPE_INCOMING = "incomingMessage";
    public static final String MESSAGE_TYPE_SEND_SUCCESS = "messageSent";
    public static final String MESSAGE_TYPE_SEND_FAILURE = "messageSendFailure";
    public static final String MESSAGE_TYPE_RECEIVE_FAILURE = "messageReceptionFailure";
    public static final String ERROR_CODE = "errorCode";
    public static final String ERROR_DETAIL = "errorDetail";
    public static final String PROPERTY_PREFIX = "property_";
    public static final String PROPERTY_TYPE_PREFIX = "propertyType_";
    private static final String PAYLOAD_NAME_PREFIX = "payload_";
    public static final String PAYLOAD_NAME_FORMAT = PAYLOAD_NAME_PREFIX + "{0}";
    private static final String PAYLOAD_MIME_TYPE_SUFFIX = "_mimeType";
    public static final String PAYLOAD_MIME_TYPE_FORMAT = PAYLOAD_NAME_FORMAT + PAYLOAD_MIME_TYPE_SUFFIX;
    private static final String PAYLOAD_FILE_NAME_SUFFIX = "_fileName";
    public static final String PAYLOAD_FILE_NAME_FORMAT = PAYLOAD_NAME_FORMAT + PAYLOAD_FILE_NAME_SUFFIX;
    private static final String PAYLOAD_MIME_CONTENT_ID_SUFFIX = "_mimeContentId";
    public static final String PAYLOAD_MIME_CONTENT_ID_FORMAT = PAYLOAD_NAME_FORMAT + PAYLOAD_MIME_CONTENT_ID_SUFFIX;
    public static final String PUT_ATTACHMENTS_IN_QUEUE = "putAttachmentInQueue";
    public static final String PAYLOAD_FILENAME = "FileName";
    public static final String PAYLOAD_PROPERTY_FILE_NAME = "PayloadName";
    public static final String JMS_PAYLOAD_NAME_FORMAT = PAYLOAD_NAME_FORMAT + "_" + PAYLOAD_PROPERTY_FILE_NAME;

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    private MessagePropertyNames() {
    }
}
