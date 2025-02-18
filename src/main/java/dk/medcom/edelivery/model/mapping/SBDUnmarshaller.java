package dk.medcom.edelivery.model.mapping;

import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

import javax.activation.DataHandler;
import javax.xml.bind.JAXB;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

public class SBDUnmarshaller {

    private SBDUnmarshaller() {
    }

    public static StandardBusinessDocument unmarshal(DataHandler dataHandler) {
        try (InputStream inputStream = dataHandler.getInputStream()) {
            return JAXB.unmarshal(inputStream, StandardBusinessDocument.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
