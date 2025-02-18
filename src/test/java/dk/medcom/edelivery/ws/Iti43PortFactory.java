package dk.medcom.edelivery.ws;

import dk.medcom.edelivery.integration.CustomInterceptorProvider;
import dk.medcom.edelivery.integration.dgws.DGWSHeaderFactory;
import dk.medcom.edelivery.integration.dgws.DGWSInterceptor;
import org.openehealth.ipf.commons.ihe.ws.JaxWsRequestClientFactory;
import org.openehealth.ipf.commons.ihe.ws.WsTransactionConfiguration;
import org.openehealth.ipf.commons.ihe.xds.core.audit.XdsSubmitAuditDataset;
import org.openehealth.ipf.commons.ihe.xds.iti43.Iti43PortType;

import javax.xml.namespace.QName;

public class Iti43PortFactory {

    public static final WsTransactionConfiguration<XdsSubmitAuditDataset> ITI_43_WS_CONFIG = new WsTransactionConfiguration<>(
            "xds-iti42",
            "Retrieve Document Set",
            false,
            null,
            null,
            new QName("urn:ihe:iti:xds-b:2007", "DocumentRepository_Service", "ihe"),
            Iti43PortType.class,
            new QName("urn:ihe:iti:xds-b:2007", "DocumentRepository_Binding_Soap12", "ihe"),
            false,
            "wsdl/iti43.wsdl",
            true,
            false,
            false,
            false);

    public static Iti43PortType createWithoutSecurity(String url) {
        return create(url, new CustomInterceptorProvider());
    }

    public static Iti43PortType createWithSecurity(String url, DGWSHeaderFactory headerFactory) {
        CustomInterceptorProvider customInterceptorProvider = new CustomInterceptorProvider();
        customInterceptorProvider.getOutInterceptors().add(new DGWSInterceptor(headerFactory));
        return create(url, customInterceptorProvider);
    }

    private static Iti43PortType create(String url, CustomInterceptorProvider customInterceptorProvider) {
        return  (Iti43PortType) new JaxWsRequestClientFactory<>(
                ITI_43_WS_CONFIG,
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
