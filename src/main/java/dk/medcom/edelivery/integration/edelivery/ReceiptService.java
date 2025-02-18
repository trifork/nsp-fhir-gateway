package dk.medcom.edelivery.integration.edelivery;

import dk.medcom.edelivery.integration.domibus.model.Submission;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

public interface ReceiptService {
    void sendReceipt(Submission original, StandardBusinessDocument receipt, String action);
}
