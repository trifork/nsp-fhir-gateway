package dk.medcom.edelivery.model.mapping;

import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

public class SBDMarshaller {

    private SBDMarshaller() {
    }

    public static DataHandler marshal(StandardBusinessDocument document) {
        try (var os = marshalObject(document);
             var is = new ByteArrayInputStream(os.toByteArray())) {
            return new DataHandler(new ByteArrayDataSource(is, "text/xml"));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static ByteArrayOutputStream marshalObject(StandardBusinessDocument sbd) {
        var os = new ByteArrayOutputStream();
        try {
            // Fix for element without @XmlRoot declaration
            Marshaller marshaller = JAXBContext.newInstance(StandardBusinessDocument.class).createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(new JAXBElement(new QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader","StandardBusinessDocument"), StandardBusinessDocument.class, sbd), os);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return os;
    }

}
