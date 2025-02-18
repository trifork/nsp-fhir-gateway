package dk.medcom.edelivery.integration.dgws;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.Request;
import org.apache.cxf.headers.Header;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

@Component
public class DGWSHeaderFactory {

    private final SOSIContext sosiContext;
    private final SOSIFactory sosiFactory;

    public DGWSHeaderFactory(SOSIContext sosiContext, SOSIFactory sosiFactory) {
        this.sosiContext = sosiContext;
        this.sosiFactory = sosiFactory;
    }


    public List<Header> getDgwsHeaders() {
        Request request = sosiFactory.createNewRequest(false, null);
        request.setIDCard(sosiContext.getIdCard());
        Document sosi = request.serialize2DOMDocument();
        NodeList children = sosi.getDocumentElement().getFirstChild().getChildNodes();

        List<Header> dgwsHeaders = new ArrayList<>();
        for (int i = 0; i < children.getLength(); i++) {
            Node element = children.item(i);
            QName qname = new QName(element.getNamespaceURI(), element.getLocalName());
            dgwsHeaders.add(new Header(qname, element));
        }
        return dgwsHeaders;
    }

}
