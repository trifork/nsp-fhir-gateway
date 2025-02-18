package dk.medcom.edelivery.integration.tracking;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "tracking")
public class TrackingConfigurationProperties {

    private String url;
    private String user;
    private String password;
    private int connectTimeoutSeconds;
    private int readTimeoutSeconds;

    public String getUrl() {
        return url;
    }

    public TrackingConfigurationProperties setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getUser() {
        return user;
    }

    public TrackingConfigurationProperties setUser(String user) {
        this.user = user;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public TrackingConfigurationProperties setPassword(String password) {
        this.password = password;
        return this;
    }

    public int getConnectTimeoutSeconds() {
        return connectTimeoutSeconds;
    }

    public TrackingConfigurationProperties setConnectTimeoutSeconds(int connectTimeoutSeconds) {
        this.connectTimeoutSeconds = connectTimeoutSeconds;
        return this;
    }

    public int getReadTimeoutSeconds() {
        return readTimeoutSeconds;
    }

    public TrackingConfigurationProperties setReadTimeoutSeconds(int readTimeoutSeconds) {
        this.readTimeoutSeconds = readTimeoutSeconds;
        return this;
    }

}
