package dk.medcom.edelivery.integration.tracking;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrackingStatus {

    @JsonProperty("ApplId")
    private String applicationId;

    @JsonProperty("EquipmentId")
    private String equipmentId;

    @JsonProperty("Uuid")
    private String uuid;

    @JsonProperty("Ts")
    private String timestamp;

    @JsonProperty("Partner")
    private String partner;

    @JsonProperty("Direction")
    private Direction direction;

    @JsonProperty("RemotePartner")
    private String remotePartner;

    @JsonProperty("Sender")
    private String sender;

    @JsonProperty("Receiver")
    private String receiver;

    @JsonProperty("Icref")
    private String documentId;

    @JsonProperty("Brvstat")
    private String documentType;

    @JsonProperty("Orgicref")
    private String originalDocumentReference;

    @JsonProperty("MessageRef")
    private String messageReference;

    @JsonProperty("MessageType")
    private String messageType;

    public String getApplicationId() {
        return applicationId;
    }

    public TrackingStatus setApplicationId(String applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public TrackingStatus setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public TrackingStatus setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public TrackingStatus setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getPartner() {
        return partner;
    }

    public TrackingStatus setPartner(String partner) {
        this.partner = partner;
        return this;
    }

    public Direction getDirection() {
        return direction;
    }

    public TrackingStatus setDirection(Direction direction) {
        this.direction = direction;
        return this;
    }

    public String getRemotePartner() {
        return remotePartner;
    }

    public TrackingStatus setRemotePartner(String remotePartner) {
        this.remotePartner = remotePartner;
        return this;
    }

    public String getSender() {
        return sender;
    }

    public TrackingStatus setSender(String sender) {
        this.sender = sender;
        return this;
    }

    public String getReceiver() {
        return receiver;
    }

    public TrackingStatus setReceiver(String receiver) {
        this.receiver = receiver;
        return this;
    }

    public String getDocumentId() {
        return documentId;
    }

    public TrackingStatus setDocumentId(String documentId) {
        this.documentId = documentId;
        return this;
    }

    public String getDocumentType() {
        return documentType;
    }

    public TrackingStatus setDocumentType(String documentType) {
        this.documentType = documentType;
        return this;
    }

    public String getOriginalDocumentReference() {
        return originalDocumentReference;
    }

    public TrackingStatus setOriginalDocumentReference(String originalDocumentReference) {
        this.originalDocumentReference = originalDocumentReference;
        return this;
    }

    public String getMessageReference() {
        return messageReference;
    }

    public TrackingStatus setMessageReference(String messageReference) {
        this.messageReference = messageReference;
        return this;
    }

    public String getMessageType() {
        return messageType;
    }

    public TrackingStatus setMessageType(String messageType) {
        this.messageType = messageType;
        return this;
    }

    /**
     * CR = created <br/>
     * IB = inbound <br/>
     * OB = outbound <br/>
     * FI = final
     */
    public enum Direction {
        CR, IB, OB, FI
    }
}
