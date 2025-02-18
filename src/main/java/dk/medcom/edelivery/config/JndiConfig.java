package dk.medcom.edelivery.config;

import org.osjava.sj.MemoryContextFactory;
import org.osjava.sj.SimpleJndi;
import org.osjava.sj.jndi.MemoryContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

@Configuration
public class JndiConfig {

    @Autowired
    public void bindDataSource(ResourceLoader resourceLoader, CraDBConfigurationProperties properties) throws NamingException {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, MemoryContextFactory.class.getName());
        System.setProperty(SimpleJndi.SHARED, "true");
        System.setProperty(MemoryContext.IGNORE_CLOSE, "true");

        var driverManagerDataSource = new DriverManagerDataSource(
                "jdbc:mysql://"
                        + properties.getHost() + ":"
                        + properties.getPort() + "/"
                        + properties.getName() + "?autoReconnect=true");
        driverManagerDataSource.setPassword(properties.getPassword());
        driverManagerDataSource.setUsername(properties.getUser());

        new InitialContext().rebind(getDatasource(resourceLoader), driverManagerDataSource);
    }

    private String getDatasource(ResourceLoader resourceLoader) {
        var properties = fetchProperties("classpath:cra.properties", resourceLoader);
        return String.valueOf(properties.get("datasource"));
    }

    public static Properties fetchProperties(String resourceLocation, ResourceLoader resourceLoader) {
        Resource resource = resourceLoader.getResource(resourceLocation);
        try (InputStream in = resource.getInputStream()) {
            Properties properties = new Properties();
            properties.load(in);
            return properties;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
