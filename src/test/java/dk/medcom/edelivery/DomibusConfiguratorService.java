package dk.medcom.edelivery;

public class DomibusConfiguratorService {

    static DomibusAPI api = new DomibusAPI();

    public static void main(String[] args) {

        var dds = new DomibusConfig(
                "https://ap-dds.nsptest.medcom.dk",
                "123456",
                "nyy+r_#3F42U!%q4",
                "src/test/resources/ap-tomcat/pmodes/gateway-nsptest-dds-v4.xml",
                "src/test/resources/ap-tomcat/domibus/keystores/cert_truststore_smp_and_intermediate2.jks",
                "1"
        );

        var gateway = new DomibusConfig(
                "https://ap-gateway.nsptest.medcom.dk",
                "123456",
                "nyy+r_#3F42U!%q4",
                "src/test/resources/ap-tomcat/pmodes/gateway-nsptest-v8.xml",
                "src/test/resources/ap-tomcat/domibus/keystores/cert_truststore_smp_and_intermediate2.jks",
                "1"
        );

        api.configure(dds);
        api.configure(gateway);
    }

}
