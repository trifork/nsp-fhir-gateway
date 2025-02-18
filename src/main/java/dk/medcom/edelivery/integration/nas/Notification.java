package dk.medcom.edelivery.integration.nas;

import javax.xml.bind.annotation.*;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "personIdentifier", "documentId", "originalSender", "finalRecipient", "senderId", "receiverId", "standard", "type", "typeVersion"
})
@XmlRootElement(namespace = Notification.ADVIS_NAMESPACE, name = Notification.ADVIS_LOCALNAME)
public class Notification {

    public static final String ADVIS_NAMESPACE = "http://medcom.dk/nsp-2g";
    public static final String ADVIS_LOCALNAME = "Notification";

    @XmlElement(name = "personIdentifier", namespace = ADVIS_NAMESPACE)
    private PersonIdentifier personIdentifier;

    @XmlElement(name = "documentId", namespace = ADVIS_NAMESPACE)
    private String documentId;

    @XmlElement(name = "originalSender", namespace = ADVIS_NAMESPACE)
    private String originalSender;

    @XmlElement(name = "finalRecipient", namespace = ADVIS_NAMESPACE)
    private String finalRecipient;

    @XmlElement(name = "senderId", namespace = ADVIS_NAMESPACE)
    private String senderId;

    @XmlElement(name = "receiverId", namespace = ADVIS_NAMESPACE)
    private String receiverId;

    @XmlElement(name = "standard", namespace = ADVIS_NAMESPACE)
    private String standard;

    @XmlElement(name = "type", namespace = ADVIS_NAMESPACE)
    private String type;

    @XmlElement(name = "typeVersion", namespace = ADVIS_NAMESPACE)
    private String typeVersion;

    public String getDocumentId() {
        return documentId;
    }

    public Notification setDocumentId(String documentId) {
        this.documentId = documentId;
        return this;
    }

    public String getOriginalSender() {
        return originalSender;
    }

    public Notification setOriginalSender(String originalSender) {
        this.originalSender = originalSender;
        return this;
    }

    public String getFinalRecipient() {
        return finalRecipient;
    }

    public Notification setFinalRecipient(String finalRecipient) {
        this.finalRecipient = finalRecipient;
        return this;
    }

    public String getSenderId() {
        return senderId;
    }

    public Notification setSenderId(String senderId) {
        this.senderId = senderId;
        return this;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public Notification setReceiverId(String receiverId) {
        this.receiverId = receiverId;
        return this;
    }

    public String getStandard() {
        return standard;
    }

    public Notification setStandard(String standard) {
        this.standard = standard;
        return this;
    }

    public String getType() {
        return type;
    }

    public Notification setType(String type) {
        this.type = type;
        return this;
    }

    public String getTypeVersion() {
        return typeVersion;
    }

    public Notification setTypeVersion(String typeVersion) {
        this.typeVersion = typeVersion;
        return this;
    }

    public PersonIdentifier getPersonIdentifier() {
        return personIdentifier;
    }

    public Notification setPersonIdentifier(PersonIdentifier personIdentifier) {
        this.personIdentifier = personIdentifier;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(personIdentifier, that.personIdentifier) &&
                Objects.equals(documentId, that.documentId) &&
                Objects.equals(originalSender, that.originalSender) &&
                Objects.equals(finalRecipient, that.finalRecipient) &&
                Objects.equals(senderId, that.senderId) &&
                Objects.equals(receiverId, that.receiverId) &&
                Objects.equals(standard, that.standard) &&
                Objects.equals(type, that.type) &&
                Objects.equals(typeVersion, that.typeVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personIdentifier, documentId, originalSender, finalRecipient, senderId, receiverId, standard, type, typeVersion);
    }

}
