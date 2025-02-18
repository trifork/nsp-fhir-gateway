package dk.medcom.edelivery.testing;

import backend.ecodex.org._1_1.*;
import dk.medcom.edelivery.integration.domibus.model.Payload;
import dk.medcom.edelivery.integration.domibus.model.Submission;
import dk.medcom.edelivery.util.DomibusWSUtil;
import domibus.eu.*;
import domibus.eu.LargePayloadType;
import domibus.eu.SubmitRequest;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import wsplugin.domibus.eu.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;

public class DomibusWSService {

    private final DomibusWSUtil wsUtil;
    private final WebServicePluginInterface domibusService;

    public static DomibusWSService getService(String gatewayUrl) {
        var wsUtil = new DomibusWSUtil();
        QName qname = new QName("http://org.ecodex.backend/1_1/", "BackendService_1_1");
        URL url = getUrl(gatewayUrl);
        var domibusService = new BackendService(url, qname).getBACKENDPORT();
        BindingProvider bindingProvider = (BindingProvider) domibusService;
        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, gatewayUrl);

        return new DomibusWSService(wsUtil, domibusService);
    }

    private static URL getUrl(String gatewayUrl) {
        try {
            return new URL(gatewayUrl + "?wsdl");
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    public DomibusWSService(DomibusWSUtil wsUtil, WebServicePluginInterface domibusService) {
        this.wsUtil = wsUtil;
        this.domibusService = domibusService;
    }

    public void sendSubmitRequest(Submission submission) throws SubmitRequestFault {
        SubmitRequest request = createXmlSendRequest(submission.getPayloads());
        Messaging headerInfo = wsUtil.mapSubmissionToMessaging(submission);

        //debugRequest(headerInfo);
        SubmitResponse submitResponse = domibusService.submitRequest(request, headerInfo);
        //System.out.println("submitResponse.getMessageID=" + submitResponse.getMessageID());
    }

    public List<String> fetchPendingMessageIds() {
        var listPendingMessagesResponse = domibusService.listPendingMessages(
                new JAXBElement<>(
                        new QName("http://org.ecodex.backend/1_1/", "listPendingMessagesRequest"),
                        String.class,
                        ""
                )
        );

        return List.copyOf(listPendingMessagesResponse.getMessageID());
    }

    protected SubmitRequest createXmlSendRequest(Set<Payload> payloads) {
        SubmitRequest sendRequest = new SubmitRequest();
        for (var payload : payloads) {
            LargePayloadType largePayload = new LargePayloadType();
            largePayload.setPayloadId("cid:message");
            largePayload.setContentType("text/xml");
            largePayload.setValue(payload.getPayloadDatahandler());
            sendRequest.getPayload().add(largePayload);
        }
        return sendRequest;
    }

    private void debugRequest(Messaging obj) {
        try {
            Marshaller jaxbMarshaller = JAXBContext.newInstance(obj.getClass()).createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            QName qName = new QName("eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704", "Messaging");
            JAXBElement<Messaging> root = new JAXBElement<Messaging>(qName, Messaging.class, obj);
            StringWriter stringWriter = new StringWriter();
            jaxbMarshaller.marshal(root, stringWriter);
            System.out.println(stringWriter.toString());
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public MessageResponse getMessage(String id) {
        var request = new RetrieveMessageRequest();
        request.setMessageID(id);
        var responseHolder = new Holder<RetrieveMessageResponse>(new RetrieveMessageResponse());
        var messagingHolder = new Holder<Messaging>(new Messaging());
        try {
            domibusService.retrieveMessage(request, responseHolder, messagingHolder);
            return new MessageResponse(messagingHolder.value, responseHolder.value);
        } catch (RetrieveMessageFault retrieveMessageFault) {
            throw new IllegalStateException(retrieveMessageFault);
        } catch (RetrieveMessageFault e) {
            throw new RuntimeException(e);
        }
    }

    public static class MessageResponse {
        private final Messaging messaging;
        private final RetrieveMessageResponse retrieveMessageResponse;

        public MessageResponse(Messaging messaging, RetrieveMessageResponse retrieveMessageResponse) {
            this.messaging = messaging;
            this.retrieveMessageResponse = retrieveMessageResponse;
        }

        public Messaging getMessaging() {
            return messaging;
        }

        public RetrieveMessageResponse getRetrieveMessageResponse() {
            return retrieveMessageResponse;
        }
    }
}
