package dk.medcom.edelivery.service;

import dk.medcom.edelivery.model.Metadata;

public abstract class MetadataServiceDecorator implements MetadataService {

    private final MetadataService delegate;

    protected MetadataServiceDecorator(MetadataService delegate) {
        this.delegate = delegate;
    }

    @Override
    public void publish(Metadata metadata) {
        delegate.publish(metadata);
    }
}
