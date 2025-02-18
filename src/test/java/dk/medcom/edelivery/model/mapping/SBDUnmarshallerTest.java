package dk.medcom.edelivery.model.mapping;

import dk.medcom.edelivery.TestFileHelper;
import org.junit.jupiter.api.Test;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.BinaryContentType;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

import javax.activation.DataHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SBDUnmarshallerTest {

    @Test
    void map_example_with_binary_content() {
        DataHandler example = TestFileHelper.getDataHandler("test-messages/SBDH.konfiguration.20201106.XREF01.eksempel.xml", "application/xml");
        StandardBusinessDocument standardBusinessDocument = SBDUnmarshaller.unmarshal(example);

        BinaryContentType binary = standardBusinessDocument.getBinaryContent().get(0);

        assertEquals("UTF-8", binary.getEncoding());
        assertEquals("application/XML", binary.getMimeType());
        assertNotNull(binary.getValue());
    }

}
