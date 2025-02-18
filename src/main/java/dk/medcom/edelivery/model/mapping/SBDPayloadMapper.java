package dk.medcom.edelivery.model.mapping;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import oio.medcom.vansenvelope104.MessageType;
import oio.medcom.vansenvelope104.VANSEnvelopeType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Binary;
import org.hl7.fhir.r4.model.Bundle;
import org.jetbrains.annotations.NotNull;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.BinaryContentType;

import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXB;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.Optional;

import static org.hl7.fhir.r4.model.Bundle.BundleType.MESSAGE;

public class SBDPayloadMapper {

    public static final String TEXT_XML = "text/xml";
    public static final String APPLICATION_JSON = "application/json";

    private static final IParser xmlParser = FhirContext.forR4().newXmlParser();
    private static final IParser jsonParser = FhirContext.forR4().newJsonParser();

    private SBDPayloadMapper() {
    }

    public static Bundle toBundle(BinaryContentType content) {
        try {
            String mimeType = content.getMimeType().toLowerCase();
            byte[] data = content.getValue();
            String encoding = content.getEncoding();

            return tryParse(getParser(mimeType), data, encoding).orElseGet(() -> getBundleFromBinaryData(mimeType, data));
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Cannot convert object of type %s to message bundle. Reason: %s", content.getClass().getCanonicalName(), e.getMessage()));
        }
    }

    private static Optional<Bundle> tryParse(ResourceParser parser, byte[] data, String encoding) {
        try {
            IBaseResource resource = parser.parse(new String(data, encoding));
            if (resource instanceof Bundle) {
                Bundle bundle = (Bundle) resource;

                if (MESSAGE.equals(bundle.getType()))
                    return Optional.of(bundle);
            }
            return Optional.empty();
        } catch (UnsupportedEncodingException e) {
            throw new SBDHIntegrityException("Unsupported encoding for BinaryContents: " + encoding);
        } catch (DataFormatException e) {
            return Optional.empty();
        }
    }

    private static Bundle getBundleFromBinaryData(String mimeType, byte[] data) {
        return tryParseVANSMessage(mimeType, data).orElseGet(() -> fromRawBinary(mimeType, data));
    }

    private static Optional<Bundle> tryParseVANSMessage(String mimeType, byte[] data) {
        try {
            VANSEnvelopeType vansEnvelope = JAXB.unmarshal(new ByteArrayInputStream(data), VANSEnvelopeType.class);
            if (vansEnvelope != null && vansEnvelope.getMessage() != null) {
                return Optional.of(fromRawBinary(mimeType, vansEnvelope.getMessage().getData()));
            }
            return Optional.empty();
        } catch (DataFormatException | DataBindingException e) {
            return Optional.empty();
        }
    }

    @NotNull
    private static Bundle fromRawBinary(String mimeType, byte[] data) {
        Bundle bundle = new Bundle().setType(MESSAGE);
        bundle.addEntry().setResource(
                new Binary()
                        .setContentType(mimeType)
                        .setData(data)
        );

        return bundle;
    }

    private static ResourceParser getParser(String mimeType) {
        switch (Objects.requireNonNullElse(mimeType, "")) {
            case TEXT_XML:
                return xmlParser::parseResource;
            case APPLICATION_JSON:
                return jsonParser::parseResource;
            default:
                return string -> null;
        }
    }

    private interface ResourceParser {
        IBaseResource parse(String string);
    }
}
