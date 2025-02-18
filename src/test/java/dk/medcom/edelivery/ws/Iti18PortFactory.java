package dk.medcom.edelivery.ws;

import dk.medcom.edelivery.integration.CustomInterceptorProvider;
import dk.medcom.edelivery.integration.dgws.DGWSHeaderFactory;
import dk.medcom.edelivery.integration.dgws.DGWSInterceptor;
import dk.medcom.edelivery.integration.dgws.HSUIDInterceptor;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.openehealth.ipf.commons.ihe.ws.JaxWsRequestClientFactory;
import org.openehealth.ipf.commons.ihe.ws.WsTransactionConfiguration;
import org.openehealth.ipf.commons.ihe.xds.core.audit.XdsSubmitAuditDataset;
import org.openehealth.ipf.commons.ihe.xds.iti18.Iti18PortType;

import javax.xml.namespace.QName;

public class Iti18PortFactory {

    public static final WsTransactionConfiguration<XdsSubmitAuditDataset> ITI_18_WS_CONFIG = new WsTransactionConfiguration<>(
            "xds-iti18",
            "Retrieve Document Set",
            false,
            null,
            null,
            new QName("urn:ihe:iti:xds-b:2007", "DocumentRegistry_Service", "ihe"),
            Iti18PortType.class,
            new QName("urn:ihe:iti:xds-b:2007", "DocumentRegistry_Binding_Soap12", "ihe"),
            false,
            "wsdl/iti18.wsdl",
            true,
            false,
            false,
            false);

    public static Iti18PortType createWithoutSecurity(String url) {
        return create(url, new CustomInterceptorProvider());
    }

    public static Iti18PortType createWithSecurity(String url, DGWSHeaderFactory headerFactory) {
        CustomInterceptorProvider customInterceptorProvider = new CustomInterceptorProvider();
        customInterceptorProvider.getOutInterceptors().add(new DGWSInterceptor(headerFactory));
        customInterceptorProvider.getOutInterceptors().add(new HSUIDInterceptor(headerFactory));
        //customInterceptorProvider.getOutInterceptors().add(new LoggingOutInterceptor());
        //customInterceptorProvider.getInInterceptors().add(new LoggingInInterceptor());
        return create(url, customInterceptorProvider);
    }

    private static Iti18PortType create(String url, CustomInterceptorProvider customInterceptorProvider) {
        return  (Iti18PortType) new JaxWsRequestClientFactory<>(
                ITI_18_WS_CONFIG,
                url,
                null,
                null,
                customInterceptorProvider,
                null,
                null,
                null,
                null
        ).getClient();
    }
}
