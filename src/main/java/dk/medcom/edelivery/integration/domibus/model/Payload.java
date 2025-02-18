package dk.medcom.edelivery.integration.domibus.model;

import javax.activation.DataHandler;
import java.util.Collection;
import java.util.Objects;

public class Payload {
    private final String schemaLocation;
    private final String contentId;
    private final Description description;
    private final DataHandler payloadDatahandler;
    private final Collection<TypedProperty> payloadProperties;
    private final boolean inBody;
    private long payloadSize;
    private String filepath;

    public Payload(final String contentId, final DataHandler payloadDatahandler, final Collection<TypedProperty> payloadProperties, final boolean inBody, final Description description, String schemaLocation) {
        this.contentId = contentId;
        this.payloadDatahandler = payloadDatahandler;
        this.description = description;
        this.payloadProperties = payloadProperties;
        this.inBody = inBody;

        String tSchemaLocation = schemaLocation;
        if ("".equals(tSchemaLocation)) {
            tSchemaLocation = null;
        }
        this.schemaLocation = tSchemaLocation;
    }

    public String getContentId() {
        return this.contentId;
    }

    public DataHandler getPayloadDatahandler() {
        return this.payloadDatahandler;
    }

    public Collection<TypedProperty> getPayloadProperties() {
        return this.payloadProperties;
    }

    public String getSchemaLocation() {
        return this.schemaLocation;
    }

    public boolean isInBody() {
        return this.inBody;
    }

    public Description getDescription() {
        return this.description;
    }

    public long getPayloadSize() {
        return payloadSize;
    }

    public void setPayloadSize(long payloadSize) {
        this.payloadSize = payloadSize;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Payload)) return false;

        final Payload payload = (Payload) o;

        return Objects.equals(this.contentId, payload.contentId);

    }

    @Override
    public int hashCode() {
        return Objects.hashCode(contentId);
    }
}
