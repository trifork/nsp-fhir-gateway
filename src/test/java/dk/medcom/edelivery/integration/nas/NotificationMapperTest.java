package dk.medcom.edelivery.integration.nas;

import dk.nsi.advis.v10.NotifyContent;
import org.apache.cxf.staxutils.StaxUtils;
import org.junit.jupiter.api.Test;
import org.oasis_open.docs.wsn.b_2.Notify;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationMapperTest {

    @Test
    void map_to_Notify() throws JAXBException {
        var notification = new Notification()
                .setPersonIdentifier(new PersonIdentifier().setStandard("1.2.208.176.1.2").setValue("0506504003"))
                .setDocumentId(new UUID(132L, 456L).toString())
                .setStandard("urn:dk:healthcare:medcom:oioxml:schema:xsd:HospitalReferral")
                .setOriginalSender("0088:5790000121526")
                .setFinalRecipient("0088:5790000201389")
                .setSenderId("132456789")
                .setReceiverId("987654321")
                .setType("XREF01")
                .setTypeVersion("XH0130R");

        Notify notify = NotificationMapper.toNotify(notification);

        var jaxbContext = JAXBContext.newInstance(Notify.class, Notification.class, NotifyContent.class);

        var stringWriter = new StringWriter();

        // marshal and unmarshal to ensure that 'any types' are valid
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(notify, stringWriter);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        var unmarshalledNotify = unmarshaller.unmarshal(StaxUtils.createXMLStreamReader(new StringReader(stringWriter.toString())), Notify.class).getValue();

        assertEquals(1, unmarshalledNotify.getNotificationMessage().size());
        var messageHolder = unmarshalledNotify.getNotificationMessage().get(0);
        var message = messageHolder.getMessage();

        assertTrue(message.getAny() instanceof NotifyContent);
        var notifyContent = (NotifyContent) message.getAny();

        assertTrue(notifyContent.getAny() instanceof Notification);
        var embeddedNotification = (Notification) notifyContent.getAny();

        assertEquals(notification, embeddedNotification);

        System.out.println(stringWriter);
    }
}
