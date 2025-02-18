package dk.medcom.edelivery.integration.tracking;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import dk.medcom.edelivery.TestDataHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RestTrackingServiceIT {

    private RestTrackingService kmdTrackingService;

    private RestTrackingService trackingService;

    private WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8089));
        wireMockServer.start();

        var properties = new TrackingConfigurationProperties()
                .setUrl("http://localhost:8089/")
                .setUser("user")
                .setPassword("password")
                .setConnectTimeoutSeconds(30)
                .setReadTimeoutSeconds(30);

        trackingService = new RestTrackingService(properties);

        var kmdProperties = new TrackingConfigurationProperties()
                .setUrl("http://ehealt-trackandtrace-pilot.azurewebsites.net")
                .setUser("nsp")
                .setPassword("f#3_$ExWvOiZ");

        kmdTrackingService = new RestTrackingService(kmdProperties);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    //@Disabled // Should work though. Just disabled to avoid cluttering the KMD service
    void kmd_succeeds_on_status_200() {
        StandardBusinessDocumentHeader sbdh = TestDataHelper.getStandardBusinessDocumentHeader();
        TrackingStatus trackingStatus = TrackingStatusMapper.toTrackingStatus(sbdh);

        trackingStatus.setRemotePartner("remote");
        trackingStatus.setOriginalDocumentReference("doc-ref");

        RestTrackingService kmdTrackingService = this.kmdTrackingService;
        kmdTrackingService.logTrackingStatus(trackingStatus);
    }

    @Test
    void succeeds_on_status_200() {
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/Track"))
                .willReturn(aResponse()
                        .withStatus(200)));

        TrackingStatus trackingStatus = new TrackingStatus();

        assertDoesNotThrow(() -> trackingService.logTrackingStatus(trackingStatus));
    }

    @Test
    void throws_on_error() {
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/Track"))
                .willReturn(aResponse()
                        .withStatus(401)));

        TrackingStatus trackingStatus = new TrackingStatus();

        var e = assertThrows(HttpClientErrorException.class, () -> trackingService.logTrackingStatus(trackingStatus));
        assertEquals(401, e.getRawStatusCode());
    }

}
