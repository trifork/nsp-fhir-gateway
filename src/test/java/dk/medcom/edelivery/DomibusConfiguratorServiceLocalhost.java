package dk.medcom.edelivery;

public class DomibusConfiguratorServiceLocalhost {

    public static void main(String[] args) {

        var api = new DomibusAPI();

        var ws1 = new DomibusConfig(
                "http://localhost:8081",
                "123456",
                "Domibus123!",
                "src/test/resources/it-config/ap1-pmode-smp.xml",
                "src/test/resources/ap-tomcat/domibus/keystores/cert_truststore_smp_and_intermediate2.jks",
                "1"
        );

        api.configure(ws1);
    }

}
