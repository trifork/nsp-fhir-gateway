package dk.medcom.edelivery.model;

import java.util.UUID;

public class PrefixedUUID {

    public static PrefixedUUID fromUUID(UUID uuid) {
        return new PrefixedUUID(uuid);
    }

    public static PrefixedUUID fromUUIDString(String uuid) {
        return fromUUID(UUID.fromString(uuid));
    }

    public static PrefixedUUID randomPrefixedUUID() {
        return fromUUID(UUID.randomUUID());
    }

    private final UUID uuid;

    private PrefixedUUID(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "urn:uuid:" + uuid.toString();
    }
}
