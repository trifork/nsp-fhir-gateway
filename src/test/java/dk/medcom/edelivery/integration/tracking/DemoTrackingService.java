package dk.medcom.edelivery.integration.tracking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Primary
@Profile("demo")
public class DemoTrackingService implements TrackingService {

    private static final Logger log = LogManager.getLogger(DemoTrackingService.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    public DemoTrackingService() {
        log.info("INIT");
    }

    @Override
    public void logTrackingStatus(TrackingStatus trackingStatus) {
        try {
            var message = mapper.writeValueAsString(trackingStatus);
            log.info(message);
        } catch (JsonProcessingException e) {
            log.error("Error logging TrackingStatus", e);
        }
    }
}
