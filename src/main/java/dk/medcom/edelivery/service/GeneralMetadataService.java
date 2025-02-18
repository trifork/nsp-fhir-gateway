package dk.medcom.edelivery.service;

import dk.medcom.edelivery.model.Metadata;

import java.util.function.Consumer;
import java.util.function.Function;

public class GeneralMetadataService implements MetadataService {

    private final Consumer<Metadata> consumer;

    public <T> GeneralMetadataService(Consumer<T> consumer, Function<Metadata, T> mapper) {
        this.consumer = metadata -> consumer.accept(mapper.apply(metadata));
    }

    @Override
    public void publish(Metadata metadata) {
        consumer.accept(metadata);
    }
}
