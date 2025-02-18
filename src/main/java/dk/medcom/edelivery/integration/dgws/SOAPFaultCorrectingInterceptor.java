package dk.medcom.edelivery.integration.dgws;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.staxutils.StaxUtils;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;

public class SOAPFaultCorrectingInterceptor extends AbstractPhaseInterceptor<Message> {

    public SOAPFaultCorrectingInterceptor() {
        super(Phase.PRE_UNMARSHAL);
    }

    @Override
    public void handleMessage(Message message) {
        try {
            var document = StaxUtils.read(message.getContent(XMLStreamReader.class));

            var fixed = StaxUtils.toString(document).replace("soap:Server", "Server");

            message.setContent(XMLStreamReader.class, StaxUtils.createXMLStreamReader(new StringReader(fixed)));
        } catch (XMLStreamException e) {
            throw new IllegalStateException(e);
        }
    }
}
