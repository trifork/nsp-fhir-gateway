package dk.medcom.edelivery.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cra.db")
public class CraDBConfigurationProperties {

    private String host = "cradb";
    private String port = "3306";
    private String name = "cra";
    private String user = "cra";
    private String password = "cra";

    public String getHost() {
        return host;
    }

    public CraDBConfigurationProperties setHost(String host) {
        this.host = host;
        return this;
    }

    public String getPort() {
        return port;
    }

    public CraDBConfigurationProperties setPort(String port) {
        this.port = port;
        return this;
    }

    public String getName() {
        return name;
    }

    public CraDBConfigurationProperties setName(String name) {
        this.name = name;
        return this;
    }

    public String getUser() {
        return user;
    }

    public CraDBConfigurationProperties setUser(String user) {
        this.user = user;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public CraDBConfigurationProperties setPassword(String password) {
        this.password = password;
        return this;
    }
}
