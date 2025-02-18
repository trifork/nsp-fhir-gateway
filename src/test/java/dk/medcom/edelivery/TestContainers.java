package dk.medcom.edelivery;

import org.testcontainers.containers.*;
import org.testcontainers.containers.startupcheck.IsRunningStartupCheckStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

public class TestContainers {

    public static final Network network = Network.newNetwork();
    public static final String SMP_IP_ADDRESS = "192.168.65.2";

    public static DockerComposeContainer SMP =
            new DockerComposeContainer(new File("src/test/resources/smp/docker-compose.yml"))
            .withExposedService("db_1", 80)
            .withExposedService("ws_1", 3306);

    public static final GenericContainer<?> CRA_DB = new GenericContainer<>(
            new ImageFromDockerfile()
                    .withFileFromClasspath("Dockerfile", "cra-db/Dockerfile")
                    .withFileFromClasspath("create-test-data.sql", "cra-db/create-test-data.sql")
                    .withFileFromClasspath("drop-create-db.sql", "cra-db/drop-create-db.sql"))
            .withExposedPorts(3306)
            .withEnv("MYSQL_ROOT_PASSWORD", "password")
            .withNetwork(network)
            .withStartupCheckStrategy(new IsRunningStartupCheckStrategy());

    public static final MariaDBContainer<?> REPO_DB = new MariaDBContainer<>(DockerImageName.parse("mariadb").withTag("10.5.8"))
            .withDatabaseName("medcomgateway")
            .withUsername("medcomgateway")
            .withPassword("medcomgateway")
            .withExposedPorts(3306)
            .withNetwork(network)
            .withStartupCheckStrategy(new IsRunningStartupCheckStrategy());

    public static final GenericContainer<?> ACTIVEMQ = new GenericContainer<>(DockerImageName.parse("trifork/activemq").withTag("2020-12-30T1230"))
            .withExposedPorts(8161, 61616, 1099)
            .withNetwork(network)
            .withCreateContainerCmdModifier(cmd -> cmd.withName("activemq"))
            .withStartupCheckStrategy(new IsRunningStartupCheckStrategy());

    public static final String CLEAN_VERSION = "4.1.6";
    public static final String JMS_VERSION = "4.2.5";

    public static final GenericContainer<?> GATEWAY_DOMIBUS = newDomibus("it-config/domibus-gateway-smp.properties", "domibus-gateway", "it-config/gateway-ks.jks", JMS_VERSION, "Dockerfile-" + JMS_VERSION + "-jms")
            .withFileSystemBind("src/test/resources/it-config/jms-only-plugins", "/usr/local/tomcat/domibus/plugins");
    public static final GenericContainer<?> AP_1_DOMIBUS = newDomibus("it-config/domibus-ap1-smp.properties", "domibus-ap1", "it-config/ap1-ks.jks", CLEAN_VERSION, "Dockerfile-" + CLEAN_VERSION + "-jms")
                .withFileSystemBind("src/test/resources/it-config/ws-only-plugins", "/usr/local/tomcat/domibus/plugins");
    public static final GenericContainer<?> AP_2_DOMIBUS = newDomibus("it-config/domibus-ap2-smp.properties", "domibus-ap2", "it-config/ap2-ks.jks", CLEAN_VERSION, "Dockerfile-" + CLEAN_VERSION + "-jms")
            .withFileSystemBind("src/test/resources/it-config/ws-only-plugins", "/usr/local/tomcat/domibus/plugins");

    public static final GenericContainer<?> GATEWAY_DOMIBUS_DB = newDomibusDb("it-db", "src/test/resources/ap-mysql/Dockerfile-" + JMS_VERSION);
    public static final GenericContainer<?> AP_1_DOMIBUS_DB = newDomibusDb("it-ap1-db", "src/test/resources/ap-mysql/Dockerfile-" + CLEAN_VERSION);
    public static final GenericContainer<?> AP_2_DOMIBUS_DB = newDomibusDb("it-ap2-db", "src/test/resources/ap-mysql/Dockerfile-" + CLEAN_VERSION);

    private static GenericContainer<?> newDomibus(String propertyPath, String name, String keystore, String domibusVersion, String dockerFile) {
        // When testing against published image from Docker Hub, use this instead of "new ImageFromDockerfile()...":
        // return new GenericContainer<>(DockerImageName.parse(image).withTag(tag))

        return new GenericContainer<>(new ImageFromDockerfile().withDockerfile(Paths.get("src/test/resources/ap-tomcat/" + dockerFile)))
                .withExposedPorts(8080, 8888)
                .withNetwork(network)
                .withCopyFileToContainer(MountableFile.forClasspathResource(propertyPath), "/usr/local/tomcat/domibus/domibus.properties")
                .withCopyFileToContainer(MountableFile.forClasspathResource(keystore), "/usr/local/tomcat/domibus/keystores/cert_keystore.jks")
                .withCopyFileToContainer(MountableFile.forClasspathResource("it-config/truststore.jks"), "/usr/local/tomcat/domibus/keystores/cert_truststore.jks")
                .withCopyFileToContainer(MountableFile.forClasspathResource("it-config/activemq-default-" + domibusVersion + ".xml"), "/usr/local/tomcat/domibus/internal/activemq.xml")

                .withExtraHost("localhost", "127.0.0.1")
                .withExtraHost("b-16631239efa038ec58d38ffa70032f1c.iso6523-actorid-upis.dk.acc.edelivery.tech.ec.europa.eu", SMP_IP_ADDRESS)
                .withExtraHost("b-016998dad6c68fea05b9df7f6f56fb96.iso6523-actorid-upis.dk.acc.edelivery.tech.ec.europa.eu", SMP_IP_ADDRESS)
                .withExtraHost("b-d87afda285dddc3fef93f3f9b9121368.iso6523-actorid-upis.dk.acc.edelivery.tech.ec.europa.eu", SMP_IP_ADDRESS) // ap1
                .withExtraHost("b-89feea862a4b29a3d613e56f7f83de8b.iso6523-actorid-upis.dk.acc.edelivery.tech.ec.europa.eu", SMP_IP_ADDRESS)
                .withExtraHost("b-092b36ccf3e2a24759eb5643915f7f6f.iso6523-actorid-upis.dk.acc.edelivery.tech.ec.europa.eu", SMP_IP_ADDRESS) // 0088:...
                .withExtraHost("b-c479b154f55e9e97872ee8b693da07e3.iso6523-actorid-upis.dk.acc.edelivery.tech.ec.europa.eu", SMP_IP_ADDRESS)
                .withExtraHost("b-d87afda285dddc3fef93f3f9b9121368.iso6523-actorid-upis.dk.acc.edelivery.tech.ec.europa.eu", SMP_IP_ADDRESS)
                .withExtraHost("b-b74fb99dcc9b01fb768d3ce48b2b3058.iso6523-actorid-upis.dk.acc.edelivery.tech.ec.europa.eu", SMP_IP_ADDRESS)
                .withExtraHost("b-4e8d767c95afcb55c47920c3c88c0cc7.iso6523-actorid-upis.dk.acc.edelivery.tech.ec.europa.eu", SMP_IP_ADDRESS)
                .withStartupCheckStrategy(new IsRunningStartupCheckStrategy())
                .withEnv("JAVA_OPTS", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8888")
                .withCreateContainerCmdModifier(cmd -> cmd.withName(name));
    }

    private static GenericContainer<?> newDomibusDb(String name, String dockerFile) {
        //return new GenericContainer<>(DockerImageName.parse("trifork/domibus-mysql-4.1.6").withTag("1-cd64f7a"))
        return new GenericContainer<>(new ImageFromDockerfile().withDockerfile(Paths.get(dockerFile)))
                .withCreateContainerCmdModifier(cmd -> cmd.withName(name))
                .withNetwork(network)
                .withEnv("MYSQL_USER", "ap")
                .withEnv("MYSQL_PASSWORD", "mQuJZuxudsbdB2E")
                .withEnv("MYSQL_ROOT_PASSWORD", "v2KVUYCHEF8XFVs")
                .withEnv("MYSQL_DATABASE", "domibus_schema")
                .withStartupCheckStrategy(new IsRunningStartupCheckStrategy());
    }

    public static void main(String[] args) {

        System.out.println("Starting TestContainers");

        initialize();

        System.out.println("TestContainers are running");

        System.out.println("Press enter to stop TestContainers");
        var scanner = new Scanner(System.in);
        scanner.nextLine();

        System.out.println("Stopping TestContainers");

        stop();

        System.out.println("TestContainers were stopped.");
        System.out.println("Bye!");
    }

    private static void printContainerInfo() {
        printInfo("CRA DB", CRA_DB, 3306);
        printInfo("REPO DB", REPO_DB, 3306);
        printInfo("ActiveMq", ACTIVEMQ, 8161, 61616, 1099);
        printInfo("Domibus db gw", GATEWAY_DOMIBUS_DB);
        printInfo("Domibus db ap1", AP_1_DOMIBUS_DB);
        printInfo("Domibus db ap2", AP_2_DOMIBUS_DB);
        printInfo("Domibus Gateway", GATEWAY_DOMIBUS, 8080);
        printInfo("Domibus AP1", AP_1_DOMIBUS, 8080);
        printInfo("Domibus AP2", AP_2_DOMIBUS, 8080);
    }

    private static void printInfo(String name, GenericContainer<?> container, int... ports) {
        var namePart = String.format("%-20s Name: %-30s", name, container.getContainerName().replace("/", ""));
        var portPart = "Port: " + Arrays.stream(ports)
                .mapToObj(port -> String.format("%6d -> %6d", container.getMappedPort(port), port))
                .collect(Collectors.joining("   "));

        System.out.println(namePart + portPart);
    }


    private TestContainers() {
    }

    public static void initialize() {
        try {
            SMP.start();
        } catch (Exception e) {
            if (!(e.getCause() instanceof ContainerLaunchException) && !e.getCause().getMessage().startsWith("Timed out waiting for container port")) { throw e; }
            else System.out.println("Not completely happy with SMP docker-compose. Continuing anyway");
        }
        CRA_DB.start();
        REPO_DB.start();
        ACTIVEMQ.start();
        GATEWAY_DOMIBUS_DB.start();
        AP_1_DOMIBUS_DB.start();
        AP_2_DOMIBUS_DB.start();

        Wait.sleep(90, "init dependencies");

        GATEWAY_DOMIBUS.start();
        AP_1_DOMIBUS.start();
        AP_2_DOMIBUS.start();

        Wait.sleep(60, "init domibus     ");

        printContainerInfo();
        try {
            DomibusPModeService.post("http://localhost:" + GATEWAY_DOMIBUS.getMappedPort(8080), "gateway-pmode", "src/test/resources/it-config/gateway-pmode-smp.xml");
            DomibusPModeService.post("http://localhost:" + AP_1_DOMIBUS.getMappedPort(8080), "ap1-pmode", "src/test/resources/it-config/ap1-pmode-smp.xml");
            DomibusPModeService.post("http://localhost:" + AP_2_DOMIBUS.getMappedPort(8080), "ap2-pmode", "src/test/resources/it-config/ap2-pmode-smp.xml");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void stop() {
        SMP.stop();
        CRA_DB.stop();
        REPO_DB.stop();
        ACTIVEMQ.stop();
        GATEWAY_DOMIBUS_DB.stop();
        AP_1_DOMIBUS_DB.stop();
        AP_2_DOMIBUS_DB.stop();
        GATEWAY_DOMIBUS.stop();
        AP_1_DOMIBUS.stop();
        AP_2_DOMIBUS.stop();
    }


}
