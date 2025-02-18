package dk.medcom.edelivery.integration.domibus.model;

import org.springframework.util.StringUtils;

import javax.activation.DataHandler;
import java.util.*;

/**
 * This class represents the datamodel of the domibus backend transport layer.
 * <p>
 * In order to simplify the process of transforming data from/to the backend this
 * datamodel was introduced. A transformer for mapping this model into the complex
 * ebMS3 datamodel is already implemented.
 * Therefore for a new backend implementation it is only required to implement a simple transformation from/to the
 * backend model and this simple datamodel.
 *
 * @author Christian Koch, Stefan Mueller
 */
public class Submission {

    private final Set<Party> fromParties = new HashSet<>();
    private final Set<Party> toParties = new HashSet<>();

    private final Set<Payload> payloads = new LinkedHashSet<>();
    private final Collection<TypedProperty> messageProperties = new ArrayList<>();
    private String action;
    private String service;
    private String serviceType;
    private String conversationId;
    private String messageId;
    private String refToMessageId;
    private String agreementRef;
    private String agreementRefType;
    private String fromRole;
    private String toRole;
    private String mpc;

    private Party finalRecipient;
    private Party originalSender;
    private String endpoint;

    /**
     * Getter for mpc
     * <p>
     * "Message Partition Channels (MPCs) allow for partitioning the flow
     * of messages from a Sending MSH to a Receiving MSH into several
     * flows that can be controlled separately and consumed differently."
     *
     * @return a string identifying the Message Partition Chanel
     */
    public String getMpc() {
        return mpc;
    }

    /**
     * Setter for mpc
     * <p>
     * "Message Partition Channels (MPCs) allow for partitioning the flow
     * of messages from a Sending MSH to a Receiving MSH into several
     * flows that can be controlled separately and consumed differently."
     *
     * @param mpc string identifying the Message Partition Chanel
     */
    public void setMpc(String mpc) {
        this.mpc = mpc;
    }

    /**
     * Getter for action
     * <p>
     * "This element is a string identifying an operation or an activity within a Service. Its actual semantics is
     * beyond the scope of this specification. Action SHALL be unique within the Service in which it is defined.
     * The value of the Action element is specified by the designer of the service."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
     *
     * @return a string identifying an operation or an activity within a Service that may support several of these.
     */
    public String getAction() {
        return this.action;
    }

    /**
     * Setter for action
     * <p>
     * "This element is a string identifying an operation or an activity within a Service. Its actual semantics is
     * beyond the scope of this specification. Action SHALL be unique within the Service in which it is defined.
     * The value of the Action element is specified by the designer of the service."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
     *
     * @param action a string identifying an operation or an activity within a Service that may support several of these.
     * @return
     */
    public Submission setAction(final String action) {
        if (StringUtils.isEmpty(action)) {
            throw new IllegalArgumentException("action must not be empty");
        }
        this.action = action;
        return this;
    }

    /**
     * Getter for agreementref
     * <p>
     * "AgreementRef is a string value that identifies the agreement that governs the exchange. The P-Mode
     * under which the MSH operates for this message should be aligned with this agreement.
     * The value of an AgreementRef element MUST be unique within a namespace mutually agreed by the two
     * parties. This could be a concatenation of the From and To PartyId values, a URI containing the Internet
     * domain name of one of the parties, or a namespace offered and managed by some other naming or
     * registry service. It is RECOMMENDED that the AgreementRef be a URI."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
     *
     * @return a string that identifies the entity or artifact governing the exchange of messages between the parties.
     */
    public String getAgreementRef() {
        return this.agreementRef;
    }

    /**
     * Setter for agreementref
     * <p>
     * "AgreementRef is a string value that identifies the agreement that governs the exchange. The P-Mode
     * under which the MSH operates for this message should be aligned with this agreement.
     * The value of an AgreementRef element MUST be unique within a namespace mutually agreed by the two
     * parties. This could be a concatenation of the From and To PartyId values, a URI containing the Internet
     * domain name of one of the parties, or a namespace offered and managed by some other naming or
     * registry service. It is RECOMMENDED that the AgreementRef be a URI."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
     *
     * @param agreementRef a string that identifies the entity or artifact governing the exchange of messages between the parties.
     */
    public Submission setAgreementRef(final String agreementRef) {
        this.agreementRef = agreementRef;
        return this;
    }

    /**
     * Getter for agreementRefType
     * <p>
     * "This OPTIONAL attribute indicates how the parties sending and receiving the message will interpret the value of
     * the reference (e.g. the value could be "ebcppa2.1" for parties using a CPA-based agreement representation). There
     * is no restriction on the value of the type attribute; this choice is left to profiles of this specification. If
     * the type attribute is not present, the content of the eb:AgreementRef element MUST be a URI."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
     *
     * @return a string indicating how the parties sending and receiving the message will interpret the value of the reference {@link #agreementRef}
     */
    public String getAgreementRefType() {
        return this.agreementRefType;
    }

    /**
     * Setter for agreementRefType
     * <p>
     * "This OPTIONAL attribute indicates how the parties sending and receiving the message will interpret the value of
     * the reference (e.g. the value could be "ebcppa2.1" for parties using a CPA-based agreement representation). There
     * is no restriction on the value of the type attribute; this choice is left to profiles of this specification. If
     * the type attribute is not present, the content of the eb:AgreementRef element MUST be a URI."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
     *
     * @param agreementRefType a string indicating how the parties sending and receiving the message will interpret the value of the reference {@link #agreementRef}
     */
    public Submission setAgreementRefType(final String agreementRefType) {
        this.agreementRefType = agreementRefType;
        return this;
    }

    /**
     * Getter for conversationId
     * <p>
     * "The Party initiating a conversation determines the value of the ConversationId element that SHALL be
     * reflected in all messages pertaining to that conversation. The actual semantics of this value is beyond the
     * scope of this specification. Implementations SHOULD provide a facility for mapping between their
     * identification scheme and a ConversationId generated by another implementation."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
     *
     * @return a string identifying the set of related messages that make up a conversation between Parties
     */
    public String getConversationId() {
        return this.conversationId;
    }

    /**
     * Setter for conversationId
     * If not set a random value will be generated.
     * <p>
     * "The Party initiating a conversation determines the value of the ConversationId element that SHALL be
     * reflected in all messages pertaining to that conversation. The actual semantics of this value is beyond the
     * scope of this specification. Implementations SHOULD provide a facility for mapping between their
     * identification scheme and a ConversationId generated by another implementation."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
     *
     * @param conversationId a string identifying the set of related messages that make up a conversation between Parties
     */
    public void setConversationId(final String conversationId) {
        this.conversationId = conversationId;
    }

    /**
     * Getter for fromRole
     * <p>
     * "The REQUIRED eb:Role element occurs once, and identifies the authorized role
     * (fromAuthorizedRole or toAuthorizedRole) of the Party sending (when present as a child of the From element) or
     * receiving (when present as a child of the To element) the message. The value of the Role element is a non- empty
     * string, with a default value of http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/defaultRole. Other
     * possible values are subject to partner agreement."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
     *
     * @return a string identifying the authorized role of the Party sending
     */
    public String getFromRole() {
        return this.fromRole;
    }

    /**
     * Setter for fromRole
     * <p>
     * "The REQUIRED eb:Role element occurs once, and identifies the authorized role
     * (fromAuthorizedRole or toAuthorizedRole) of the Party sending (when present as a child of the From element) or
     * receiving (when present as a child of the To element) the message. The value of the Role element is a non- empty
     * string, with a default value of http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/defaultRole. Other
     * possible values are subject to partner agreement."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
     *
     * @param fromRole a string identifying the authorized role of the Party sending
     * @return
     */
    public Submission setFromRole(final String fromRole) {
        if (!StringUtils.hasLength(fromRole)) {
            throw new IllegalArgumentException("from role must not be empty");
        }
        this.fromRole = fromRole;
        return this;
    }

    /**
     * Getter for messageId
     * <p>
     * "This REQUIRED element has a value representing – for each message - a globally unique identifier conforming to
     * MessageId [RFC2822]. Note: In the Message-Id and Content-Id MIME headers, values are always surrounded by angle
     * brackets. However references in mid: or cid: scheme URI's and the MessageId and RefToMessageId elements MUST NOT
     * include these delimiters."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
     *
     * @return a string representing a globally unique identifier conforming to MessageId [RFC2822]
     */
    public String getMessageId() {
        return this.messageId;
    }

    /**
     * Setter for messageId
     * The prefix added to the messageId, in case of sending from domibus, is configurable
     * <p>
     * "This REQUIRED element has a value representing – for each message - a globally unique identifier conforming to
     * MessageId [RFC2822]. Note: In the Message-Id and Content-Id MIME headers, values are always surrounded by angle
     * brackets. However references in mid: or cid: scheme URI's and the MessageId and RefToMessageId elements MUST NOT
     * include these delimiters."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
     *
     * @param messageId a string representing a globally unique identifier conforming to MessageId [RFC2822]
     * @return
     */
    public Submission setMessageId(final String messageId) {
        this.messageId = messageId;
        return this;
    }

    /**
     * Getter for refToMessageId
     * <p>
     * "This OPTIONAL element occurs at most once. When present, it MUST contain the MessageId value of an ebMS Message
     * to which this message relates, in a way that conforms to the MEP in use."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
     *
     * @return a string containing the {@link #messageId} value of an ebMS Message to which this messages relates
     */
    public String getRefToMessageId() {
        return this.refToMessageId;
    }

    /**
     * Setter for refToMessageId
     * <p>
     * "This OPTIONAL element occurs at most once. When present, it MUST contain the MessageId value of an ebMS Message
     * to which this message relates, in a way that conforms to the MEP in use."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
     *
     * @param refToMessageId a string containing the {@link #messageId} value of an ebMS Message to which this messages relates
     */
    public void setRefToMessageId(final String refToMessageId) {
        this.refToMessageId = refToMessageId;
    }

    /**
     * Getter for service
     * <p>
     * "This element identifies the service that acts on the message. Its actual semantics is beyond the scope of
     * this specification. The designer of the service may be a standards organization, or an individual or
     * enterprise."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
     *
     * @return a string identifying the service that acts on the message
     */
    public String getService() {
        return this.service;
    }

    /**
     * Setter for service
     * <p>
     * "This element identifies the service that acts on the message. Its actual semantics is beyond the scope of
     * this specification. The designer of the service may be a standards organization, or an individual or
     * enterprise."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
     *
     * @param service a string identifying the service that acts on the message
     * @return
     */
    public Submission setService(final String service) {
        if (!StringUtils.hasLength(service)) {
            throw new IllegalArgumentException("service must not be empty");
        }
        this.service = service;
        return this;
    }

    /**
     * Getter for serviceType
     * <p>
     * "The Service element MAY contain a single @type attribute, that indicates how the parties sending and
     * receiving the message will interpret the value of the element. There is no restriction on the value of the
     * type attribute. If the type attribute is not present, the content of the Service element MUST be a URI."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
     *
     * @return a string that indicates how the parties sending and receiving the message will interpret the {@link #service} value
     */
    public String getServiceType() {
        return this.serviceType;
    }

    /**
     * Setter serviceType
     * <p>
     * "The Service element MAY contain a single @type attribute, that indicates how the parties sending and
     * receiving the message will interpret the value of the element. There is no restriction on the value of the
     * type attribute. If the type attribute is not present, the content of the Service element MUST be a URI."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
     *
     * @param serviceType a string that indicates how the parties sending and receiving the message will interpret the {@link #service} value
     * @return
     */
    public Submission setServiceType(final String serviceType) {
        this.serviceType = serviceType;
        return this;
    }

    /**
     * Getter toRole
     * <p>
     * "The REQUIRED eb:Role element occurs once, and identifies the authorized role
     * (fromAuthorizedRole or toAuthorizedRole) of the Party sending (when present as a child of the From element) or
     * receiving (when present as a child of the To element) the message. The value of the Role element is a non- empty
     * string, with a default value of http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/defaultRole. Other
     * possible values are subject to partner agreement."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
     *
     * @return a string identifying the authorized role of the receiving Party
     */
    public String getToRole() {
        return this.toRole;
    }

    /**
     * Setter toRole
     * <p>
     * "The REQUIRED eb:Role element occurs once, and identifies the authorized role
     * (fromAuthorizedRole or toAuthorizedRole) of the Party sending (when present as a child of the From element) or
     * receiving (when present as a child of the To element) the message. The value of the Role element is a non- empty
     * string, with a default value of http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/defaultRole. Other
     * possible values are subject to partner agreement."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
     *
     * @param toRole a string identifying the authorized role of the receiving Party
     * @return
     */
    public Submission setToRole(final String toRole) {
        this.toRole = toRole;
        return this;
    }

    /**
     * Returns a {@link java.util.Set} of {@link Party} elements representing the fromParties
     * of this plugin. A fromParty contains information describing the originating party.
     *
     * @return a {@link java.util.Set} of {@link Party} elements containing information describing the originating party
     */
    public Set<Party> getFromParties() {
        return this.fromParties;
    }

    /**
     * This method adds one message property to the plugin. The optional type attribute is not set.
     * <p>
     * "Its actual semantics is beyond the scope of this specification. The element is intended to be consumed outside
     * the ebMS-specified functions. It may contain some information that qualifies or abstracts message data, or that
     * allows for binding the message to some business process. A representation in the header of such properties allows
     * for more efficient monitoring, correlating, dispatching and validating functions (even if these are out of scope
     * of ebMS specification) that do not require payload access."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
     *
     * @param name  a string representing the name of the message property
     * @param value a string representing the value of the message property
     */
    public void addMessageProperty(final String name, final String value) {
        this.messageProperties.add(new TypedProperty(name, value));
    }

    /**
     * This method adds one message property to the plugin.
     * <p>
     * "Its actual semantics is beyond the scope of this specification. The element is intended to be consumed outside
     * the ebMS-specified functions. It may contain some information that qualifies or abstracts message data, or that
     * allows for binding the message to some business process. A representation in the header of such properties allows
     * for more efficient monitoring, correlating, dispatching and validating functions (even if these are out of scope
     * of ebMS specification) that do not require payload access."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
     * <p>
     * This method allows he adding of a typed property as described in the
     * <a href="https://issues.oasis-open.org/browse/EBXMLMSG-2">ebMS3 errata</a>} Note that submissions containing typed properties
     * might be rejected by ebMS3 gateways that do not support said errata.
     *
     * @param name  a string representing the name of the message property
     * @param value a string representing the value of the message property
     * @param type  a string representing the type of the message property
     */
    public void addMessageProperty(final String name, final String value, final String type) {
        this.messageProperties.add(new TypedProperty(name, value, type));
    }

    /**
     * Getter for all message properties of this plugin.
     * <p>
     * "Its actual semantics is beyond the scope of this specification. The element is intended to be consumed outside
     * the ebMS-specified functions. It may contain some information that qualifies or abstracts message data, or that
     * allows for binding the message to some business process. A representation in the header of such properties allows
     * for more efficient monitoring, correlating, dispatching and validating functions (even if these are out of scope
     * of ebMS specification) that do not require payload access."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
     *
     * @return an object of type {@link java.util.Properties} representing the message properties of this plugin
     */
    public Collection<TypedProperty> getMessageProperties() {
        return this.messageProperties;
    }

    /**
     * Returns a {@link java.util.Set} of {@link Party} elements representing the toParties
     * of this plugin. A toParty contains information describing the destination party.
     *
     * @return a {@link java.util.Set} of {@link Party} elements containing information describing the destination party.
     */
    public Set<Party> getToParties() {
        return this.toParties;
    }

    /**
     * Returns a {@link java.util.Set} of {@link Payload} elements representing the payloads
     * of this plugin. A {@link Payload} contains information describing the payload and the payload itself.
     *
     * @return a {@link java.util.Set} of {@link Payload} elements representing the payloads of this plugin
     */
    public Set<Payload> getPayloads() {
        return this.payloads;
    }

    /**
     * This method adds one payload to the {@link java.util.Set} of {@link Payload} elements.
     * In this case the parameter contentId and the payload data itself were set. By default a payload added with this method
     * is considered to be NOT part of the soap body.
     *
     * @param contentId          a string that is the [RFC2392] Content-ID URI of the payload object referenced
     * @param payloadDatahandler a DataHandler array with the payload data
     */
    public void addPayload(final String contentId, final DataHandler payloadDatahandler) {
        this.addPayload(contentId, payloadDatahandler, null, false, null, null);
    }

    public Submission addPayload(final Payload payload) {
        this.payloads.add(payload);
        return this;
    }

    /**
     * This method adds one payload to the {@link java.util.Set} of {@link Payload} elements.
     * In this case the parameter contentId, a {@link java.util.Set} of payload properties and the payload data itself were set.
     * By default a payload added with this method is considered to be NOT part of the soap body.
     * <p>
     * Payload properties will be mapped into eb:PartProperties in ebMS described here:
     * "This element has zero or more eb:Property child elements. An eb:Property element is of xs:anySimpleType
     * (e.g. string, URI) and has a REQUIRED @name attribute, the value of which must be agreed between partners. Its
     * actual semantics is beyond the scope of this specification. The element is intended to be consumed outside the
     * ebMS specified functions. It may contain meta-data that qualifies or abstracts the payload data. A representation
     * in the header of such properties allows for more efficient monitoring, correlating, dispatching and validating
     * functions (even if these are out of scope of ebMS specification) that do not require payload access."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
     *
     * @param contentId          a string that is the [RFC2392] Content-ID URI of the payload object referenced
     * @param payloadDatahandler a DataHandler array with the payload data
     * @param payloadProperties  a {@link java.util.Properties} object representing the properties of this payload
     */
    public void addPayload(final String contentId, final DataHandler payloadDatahandler, final Collection<TypedProperty> payloadProperties) {
        this.addPayload(contentId, payloadDatahandler, payloadProperties, false, null, null);
    }

    /**
     * This method adds one payload to the {@link java.util.Set} of {@link Payload} elements.
     * In this case it is possible to set all parameters available for a payload.
     * <p>
     * <ol>
     * <li><b>contentId:</b> Will be mapped the href attribute of the eb:partInfo element in ebMS described here:
     * This OPTIONAL attribute has a value that is the [RFC2392] Content-ID URI of the payload object
     * referenced, an xml:id fragment identifier, or the URL of an externally referenced resource; for example,
     * "cid:foo@example.com" or "#idref". The absence of the attribute href in the element eb:PartInfo indicates
     * that the payload part being referenced is the SOAP Body element itself. For example, a declaration of the
     * following form simply indicates that the entire SOAP Body is to be considered a payload part in this ebMS
     * message.</li>
     * <li><b>payloadData:</b> A DataHandler containing a DataSource for the paload.</li>
     * <li><b>payloadProperties:</b> Payload properties will be mapped into eb:PartProperties in ebMS described here:
     * "This element has zero or more eb:Property child elements. An eb:Property element is of xs:anySimpleType
     * (e.g. string, URI) and has a REQUIRED @name attribute, the value of which must be agreed between partners. Its
     * actual semantics is beyond the scope of this specification. The element is intended to be consumed outside the
     * ebMS specified functions. It may contain meta-data that qualifies or abstracts the payload data. A representation
     * in the header of such properties allows for more efficient monitoring, correlating, dispatching and validating
     * functions (even if these are out of scope of ebMS specification) that do not require payload access."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)</li>
     * <li><b>inBody:</b> if {@code true} the payload will be the content of the soap body.
     * If {@code false} it will be handled as an attachment of the soap message according to SwA specification.</li>
     * <li><b>description:</b> a description property. Will be mapped into a eb:Description element in ebMS</li>
     * <li><b>schemalocation:</b> "URI of schema(s) that define the instance document identified in the parent PartInfo
     * element. If the item being referenced has schema(s) of some kind that describe it (e.g. an XML Schema, DTD
     * and/or a database schema), then the Schema element SHOULD be present as a child of the PartInfo element.
     * It provides a means of identifying the schema and its version defining the payload object identified by the
     * parent PartInfo element. This metadata MAY be used to validate the Payload Part to which it refers, but the
     * MSH is NOT REQUIRED to do so ..."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)</li>
     * </ol>
     *
     * @param contentId          a string that is the [RFC2392] Content-ID URI of the payload object referenced
     * @param payloadDatahandler a DataHandler containing a DataSource for the paload.
     * @param payloadProperties  a {@link java.util.Properties} object representing the properties of this payload
     * @param inBody             if {@code true} the payload will be the content of the soap body.If {@code false} it will be handled as an attachment of the soap message according to SwA specification
     * @param description        a string representing a description property
     * @param schemaLocation     a string representing a URI of schema(s) that define the instance document
     */
    public void addPayload(final String contentId, final DataHandler payloadDatahandler, final Collection<TypedProperty> payloadProperties, final boolean inBody, final Description description, final String schemaLocation) {
        this.payloads.add(new Payload(contentId, payloadDatahandler, payloadProperties, inBody, description, schemaLocation));
    }

    /**
     * This method adds one originating party to the {@link java.util.Set} of {@link Party} elements.
     * <p>
     * Paramters are:
     * <ol>
     * <li><b>partyId:</b> This is mapped into eb:From/eb:PartyId in ebMS described here:
     * "This element has a string value content that identifies a party, or that is one of the identifiers of this party."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)</li>
     * <li><b>partyIdType:</b> This is mapped into eb:From/eb:PartyId[@type] in ebMS descibed here:
     * "... The type attribute indicates the domain of names to which the string in the content of the PartyId
     * element belongs. It is RECOMMENDED that the value of the type attribute be a URI. It is further RECOMMENDED
     * that these values be taken from the EDIRA , EDIFACT or ANSI ASC X12 registries. Technical specifications for
     * the first two registries can be found at and [ISO6523] and [ISO9735], respectively. Further discussion of
     * PartyId types and methods of construction can be found in an appendix of [ebCPPA21]. The value of any
     * given @type attribute MUST be unique within a list of PartyId elements."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)</li>
     * </ol>
     *
     * @param partyId     a string that identifies the originating party
     * @param partyIdType a string that identifies the type of the partyId
     */
    public Submission addFromParty(final String partyId, final String partyIdType) {
        this.fromParties.add(new Party(partyId, partyIdType));
        return this;
    }

    /**
     * This method adds one destination party to the {@link java.util.Set} of {@link Party} elements.
     * <p>
     * Paramters are:
     * <ol>
     * <li><b>partyId:</b> This is mapped into eb:To/eb:PartyId in ebMS described here:
     * "This element has a string value content that identifies a party, or that is one of the identifiers of this party."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)</li>
     * <li><b>partyIdType:</b> This is mapped into eb:To/eb:PartyId[@type] in ebMS descibed here:
     * "... The type attribute indicates the domain of names to which the string in the content of the PartyId
     * element belongs. It is RECOMMENDED that the value of the type attribute be a URI. It is further RECOMMENDED
     * that these values be taken from the EDIRA , EDIFACT or ANSI ASC X12 registries. Technical specifications for
     * the first two registries can be found at and [ISO6523] and [ISO9735], respectively. Further discussion of
     * PartyId types and methods of construction can be found in an appendix of [ebCPPA21]. The value of any
     * given @type attribute MUST be unique within a list of PartyId elements."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)</li>
     * </ol>
     *  @param partyId     a string that identifies the destination party
     * @param partyIdType a string that identifies the type of the partyId
     * @return
     */
    public Submission addToParty(final String partyId, final String partyIdType) {
        this.toParties.add(new Party(partyId, partyIdType));
        return this;
    }


    public Party getFinalRecipient() {
        return finalRecipient;
    }

    public Submission setFinalRecipient(String finalRecipient, String type) {
        this.finalRecipient = new Party(finalRecipient, type);
        return this;
    }

    public Party getOriginalSender() {
        return originalSender;
    }

    public Submission setOriginalSender(String originalSender, String originalSenderType) {
        return setOriginalSender(new Party(originalSender, originalSenderType));
    }

    public Submission setOriginalSender(Party originalSender) {
        this.originalSender = originalSender;
        return this;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public Submission setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    @Override
    public String toString() {
        return "Submission{" +
                "fromParties=" + fromParties +
                ", toParties=" + toParties +
                ", payloads=" + payloads +
                ", messageProperties=" + messageProperties +
                ", action='" + action + '\'' +
                ", service='" + service + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", conversationId='" + conversationId + '\'' +
                ", messageId='" + messageId + '\'' +
                ", refToMessageId='" + refToMessageId + '\'' +
                ", agreementRef='" + agreementRef + '\'' +
                ", agreementRefType='" + agreementRefType + '\'' +
                ", fromRole='" + fromRole + '\'' +
                ", toRole='" + toRole + '\'' +
                ", mpc='" + mpc + '\'' +
                ", finalRecipient=" + finalRecipient +
                ", originalSender='" + originalSender + '\'' +
                ", endpoint='" + endpoint + '\'' +
                '}';
    }
}
