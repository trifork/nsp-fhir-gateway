package dk.medcom.edelivery;

import org.junit.jupiter.api.extension.Extension;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.startupcheck.IsRunningStartupCheckStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

public class TestContainerExtension implements Extension {

    static {
       TestContainers.initialize();
    }

    public static class DBContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues
                    .of("cra.db.port=" + TestContainers.CRA_DB.getMappedPort(3306),
                            "spring.datasource.url=" + TestContainers.REPO_DB.getJdbcUrl(),
                            "spring.activemq.broker-url=tcp://localhost:" + TestContainers.ACTIVEMQ.getMappedPort(61616))
                    .applyTo(applicationContext.getEnvironment());
        }
    }

}
