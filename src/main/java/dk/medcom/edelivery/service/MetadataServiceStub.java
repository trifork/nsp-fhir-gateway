package dk.medcom.edelivery.service;

import dk.medcom.edelivery.model.Metadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MetadataServiceStub implements MetadataService {

    private static final Logger log = LogManager.getLogger(MetadataServiceStub.class);

    private final String serviceName;

    public MetadataServiceStub(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public void publish(Metadata metadata) {
        log.info("{} -> publish -> {}", serviceName, metadata.getEntryId());
    }
}
