package dk.medcom.edelivery.util;

import org.openehealth.ipf.commons.ihe.xds.core.ebxml.ebxml30.EbXMLFactory30;
import org.openehealth.ipf.commons.ihe.xds.core.ebxml.ebxml30.EbXMLNonconstructiveDocumentSetRequest30;
import org.openehealth.ipf.commons.ihe.xds.core.ebxml.ebxml30.RetrieveDocumentSetRequestType;
import org.openehealth.ipf.commons.ihe.xds.core.ebxml.ebxml30.RetrieveDocumentSetResponseType;
import org.openehealth.ipf.commons.ihe.xds.core.requests.RegisterDocumentSet;
import org.openehealth.ipf.commons.ihe.xds.core.requests.RetrieveDocumentSet;
import org.openehealth.ipf.commons.ihe.xds.core.responses.RetrievedDocumentSet;
import org.openehealth.ipf.commons.ihe.xds.core.stub.ebrs30.lcm.SubmitObjectsRequest;
import org.openehealth.ipf.commons.ihe.xds.core.transform.requests.RegisterDocumentSetTransformer;
import org.openehealth.ipf.commons.ihe.xds.core.transform.requests.RetrieveDocumentSetRequestTransformer;
import org.openehealth.ipf.commons.ihe.xds.core.transform.responses.RetrieveDocumentSetResponseTransformer;

public class Transformation {

    private static final EbXMLFactory30 ebXMLFactory = new EbXMLFactory30();
    private static final RetrieveDocumentSetRequestTransformer retrieveDocumentSetRequestTransformer = new RetrieveDocumentSetRequestTransformer(ebXMLFactory);
    private static final RetrieveDocumentSetResponseTransformer retrieveDocumentSetResponseTransformer = new RetrieveDocumentSetResponseTransformer(ebXMLFactory);
    private static final RegisterDocumentSetTransformer registerDocumentSetTransformer = new RegisterDocumentSetTransformer(ebXMLFactory);

    private Transformation() {
    }

    public static RetrieveDocumentSet transform(RetrieveDocumentSetRequestType in) {
        return retrieveDocumentSetRequestTransformer.fromEbXML(new EbXMLNonconstructiveDocumentSetRequest30<>(in));
    }

    public static RetrieveDocumentSetResponseType transform(RetrievedDocumentSet response) {
        return (RetrieveDocumentSetResponseType) retrieveDocumentSetResponseTransformer.toEbXML(response).getInternal();
    }

    public static SubmitObjectsRequest transform(RegisterDocumentSet registerDocumentSet) {
        return (SubmitObjectsRequest) (registerDocumentSetTransformer.toEbXML(registerDocumentSet).getInternal());
    }
}
