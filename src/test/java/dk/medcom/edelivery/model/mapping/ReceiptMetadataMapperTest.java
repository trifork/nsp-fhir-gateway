package dk.medcom.edelivery.model.mapping;

import dk.medcom.edelivery.TestDataHelper;
import dk.medcom.edelivery.model.ScopeTypes;
import org.junit.jupiter.api.Test;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.BinaryContentType;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.ReceiptAcknowledgement;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

import javax.xml.bind.JAXB;
import java.io.ByteArrayInputStream;

import static dk.medcom.edelivery.model.ScopeIdentifiers.DK_MESSAGING_DOCID;
import static dk.medcom.edelivery.model.ScopeTypes.RECEIVERID;
import static dk.medcom.edelivery.model.ScopeTypes.SENDERID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ReceiptMetadataMapperTest {

    @Test
    void create_ack() {

        var document = new StandardBusinessDocument()
                .withStandardBusinessDocumentHeader(TestDataHelper.getStandardBusinessDocumentHeader());

        var receiptAcknowledgement = ReceiptMetadataMapper.createReceiptAcknowledgement(document);

        JAXB.marshal(document, System.out);

        System.out.println();
        System.out.println("###");
        System.out.println();

        JAXB.marshal(receiptAcknowledgement, System.out);

        var adapter = SBDHAdapter.from(receiptAcknowledgement.getStandardBusinessDocumentHeader());

        assertThat(adapter.getInstanceIdentifier(ScopeTypes.DOCUMENTID, "urn:dk:healthcare:medcom:messaging:ebxml").get()).isEqualTo("urn:dk:healthcare:medcom:messaging:ebxml:schema:xsd:SBDHReceiptAcknowledgement");
        assertThat(adapter.getScope("FromPartyInfo")).isEmpty();
        assertThat(adapter.getScope("ToPartyInfo")).isEmpty();
        assertThat(adapter.getScope("OriginalMessageIdentifier")).isEmpty();

        assertEquals("Response", adapter.getScope("MedCom_SBDH_ReceiptAcknowledgement").get().getInstanceIdentifier());
        assertEquals("dk-medcom-messaging", adapter.getScope("MedCom_SBDH_ReceiptAcknowledgement").get().getIdentifier());

        assertEquals("1170101", adapter.getScope(RECEIVERID).get().getInstanceIdentifier());
        assertEquals("Sorkode", adapter.getScope(RECEIVERID).get().getIdentifier());
        assertEquals("1170102", adapter.getScope(SENDERID).get().getInstanceIdentifier());
        assertEquals("Sorkode", adapter.getScope(SENDERID).get().getIdentifier());

        BinaryContentType bct = receiptAcknowledgement.getBinaryContent().get(0);
        ReceiptAcknowledgement ack = unmarshal(bct, ReceiptAcknowledgement.class);

        assertEquals("iso6523-actorid-upis", ack.getFromPartyInfo().getType());
        assertEquals("0088:5790000201389", ack.getFromPartyInfo().getValue());
        assertEquals("iso6523-actorid-upis", ack.getToPartyInfo().getType());
        assertEquals("0088:5790000121526", ack.getToPartyInfo().getValue());
    }

    private <T> T unmarshal(BinaryContentType bct, Class<T> clazz) {
        return JAXB.unmarshal(new ByteArrayInputStream(bct.getValue()), clazz);
    }

    @Test
    void create_exception() {

        var document = new StandardBusinessDocument()
                .withStandardBusinessDocumentHeader(TestDataHelper.getStandardBusinessDocumentHeader());

        var exception = ReceiptMetadataMapper.createException(document, SBDHError.INTERNAL_ERROR);

        JAXB.marshal(document, System.out);

        System.out.println();
        System.out.println("###");
        System.out.println();

        JAXB.marshal(exception, System.out);

    }
}
