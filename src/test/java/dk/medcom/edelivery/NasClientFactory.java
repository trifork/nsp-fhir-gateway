package dk.medcom.edelivery;

import dk.medcom.edelivery.integration.dgws.AugmentedJAXBContextInterceptor;
import dk.medcom.edelivery.integration.dgws.DGWSHeaderFactory;
import dk.medcom.edelivery.integration.dgws.DGWSInterceptor;
import dk.medcom.edelivery.integration.dgws.SOAPFaultCorrectingInterceptor;
import dk.nsi._2012._12.nas.idlist.IDList;
import dk.nsi._2012._12.nas.idlist.IDListService;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.message.Message;
import org.oasis_open.docs.wsn.bw_2.*;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import java.util.*;

public class NasClientFactory {

    private final DGWSHeaderFactory headerFactory;
    private final String nasBaseUrl;

    public NasClientFactory(String nasBaseUrl, DGWSHeaderFactory headerFactory) {
        this.nasBaseUrl = nasBaseUrl;
        this.headerFactory = headerFactory;
    }

    public IDList idList() {
        var port = new IDListService().getSoap();
        configure(ClientProxy.getClient(port), "/idlist/service");
        return port;
    }


    public CreatePullPoint createPullPoint() {
        var port = new PullPointFactoryService().getSoap();
        configure(ClientProxy.getClient(port), "/pullpointfactory/service");
        return port;
    }

    public NotificationProducer notificationProducer() {
        var port = new NotificationProducerService().getSoap();
        configure(ClientProxy.getClient(port), "/subscriptionmanager/service");
        return port;
    }

    public NotificationProducer notificationProducer(W3CEndpointReference id) {
        var port = id.getPort(NotificationProducer.class);
        configure(ClientProxy.getClient(port), null);
        return port;
    }

    public PullPoint pullPoint(W3CEndpointReference id, Map<String, List<String>> headers) {
        var port = id.getPort(PullPoint.class);

        Client client = ClientProxy.getClient(port);
        if (headers != null) {
            client.getRequestContext().put(Message.PROTOCOL_HEADERS, headers);
        }
        configure(ClientProxy.getClient(port), null);

        return port;
    }

    private void configure(Client client, String path) {
        client.getInInterceptors().add(new LoggingInInterceptor());
        client.getOutInterceptors().add(new LoggingOutInterceptor());
        client.getOutInterceptors().add(new AugmentedJAXBContextInterceptor());
        client.getOutInterceptors().add(new DGWSInterceptor(headerFactory));
        client.getInFaultInterceptors().add(new SOAPFaultCorrectingInterceptor());
        client.getInFaultInterceptors().add(new LoggingInInterceptor());

        if (path != null) {
            client.getRequestContext().put(org.apache.cxf.message.Message.ENDPOINT_ADDRESS, nasBaseUrl + path);
        }
    }


}
