package dk.medcom.edelivery.util;

import dk.medcom.edelivery.TestFileHelper;
import dk.medcom.edelivery.integration.domibus.model.Description;
import dk.medcom.edelivery.integration.domibus.model.Payload;
import dk.medcom.edelivery.integration.domibus.model.TypedProperty;

import java.util.List;
import java.util.Locale;

import static dk.medcom.edelivery.integration.domibus.MessagePropertyNames.MIME_TYPE;

public class PayloadUtil {

    public static Payload getXmlContent(String filename) {
        return new Payload(
                "cid:message",
                TestFileHelper.getDataHandler(filename, "text/xml"),
                List.of(new TypedProperty(MIME_TYPE, "text/xml")),
                false,
                new Description(Locale.getDefault(), "test content"),
                null
        );
    }

}
