package dk.medcom.edelivery.util;

import org.openehealth.ipf.commons.ihe.xds.core.metadata.LocalizedString;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Timestamp;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Localization {

    private static final ZoneId zoneId = ZoneId.of("UTC"); // KIT env on UTC time

    private Localization() {
    }

    public static ZonedDateTime getZonedDateTimeFromLocalDateTime(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return date.atZone(zoneId);
    }

    public static LocalizedString toDanishLocalizedString(String value) {
        return new LocalizedString(value, "da-DK", "UTF-8");
    }

    public static Timestamp toTimeStamp(LocalDateTime time) {
        return new Timestamp(getZonedDateTimeFromLocalDateTime(time), Timestamp.Precision.SECOND);
    }

}
