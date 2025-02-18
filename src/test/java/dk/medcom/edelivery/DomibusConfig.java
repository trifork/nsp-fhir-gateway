package dk.medcom.edelivery;

class DomibusConfig {

    String url;
    String defaultPassword;
    String password;
    String pmodeLocation;
    String trustStoreLocation;
    String trustStorePassword;

    public DomibusConfig(String url, String defaultPassword, String desiredPassword, String pmodeLocation, String trustStoreLocation, String trustStorePassword) {
        this.url = url;
        this.defaultPassword = defaultPassword;
        this.password = desiredPassword;
        this.pmodeLocation = pmodeLocation;
        this.trustStoreLocation = trustStoreLocation;
        this.trustStorePassword = trustStorePassword;
    }
}
