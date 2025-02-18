package dk.medcom.edelivery.integration.tracking;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Service
public class RestTrackingService implements TrackingService {

    private static final Logger log = LogManager.getLogger(RestTrackingService.class);

    private final RestTemplate restTemplate;

    public RestTrackingService(TrackingConfigurationProperties properties) {
        this.restTemplate = new RestTemplateBuilder()
                .rootUri(properties.getUrl())
                .basicAuthentication(properties.getUser(), properties.getPassword())
                .setConnectTimeout(Duration.ofSeconds(properties.getConnectTimeoutSeconds()))
                .setReadTimeout(Duration.ofSeconds(properties.getReadTimeoutSeconds()))
                .build();
    }

    @Override
    public void logTrackingStatus(TrackingStatus trackingStatus) {
        long start = System.currentTimeMillis();
        restTemplate.postForEntity("/api/v1/Track", trackingStatus, String.class);
        long end = System.currentTimeMillis();
        log.debug("TrackingStatus call duration: " + (end-start) + " ms");
        log.debug("Logged Tracking Status with id = '{}'", trackingStatus.getUuid());
    }
}
