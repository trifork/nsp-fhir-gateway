package dk.medcom.edelivery.ws;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;


import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxws.EndpointImpl;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.xml.ws.Endpoint;

@Configuration
public class CxfConfiguration {

    @Bean
    public ServletRegistrationBean<CXFServlet> dispatcherServlet() {
        return new ServletRegistrationBean<>(new CXFServlet(), "/*");
    }

    @Bean
    @Primary
    public DispatcherServletPath dispatcherServletPathProvider() {
        return () -> "";
    }

    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus(LoggingFeature loggingFeature) {
        SpringBus cxfBus = new SpringBus();
        cxfBus.getFeatures().add(loggingFeature);
        return cxfBus;
    }

    @Bean
    public LoggingFeature loggingFeature() {
        LoggingFeature loggingFeature = new LoggingFeature();
        loggingFeature.setPrettyLogging(true);
        return loggingFeature;
    }

    @Bean
    public Endpoint endpoint(Bus bus, Iti43 accountServiceEndpoint, @Value("${ws.context.path:/ws}") String contextPath) {
        EndpointImpl endpoint = new EndpointImpl(bus, accountServiceEndpoint);
        endpoint.publish(contextPath);
        return endpoint;
    }
}
