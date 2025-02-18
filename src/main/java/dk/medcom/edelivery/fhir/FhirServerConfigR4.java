package dk.medcom.edelivery.fhir;

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.api.config.DaoConfig;
import ca.uhn.fhir.jpa.config.BaseJavaConfigR4;
import ca.uhn.fhir.jpa.config.HibernateDialectProvider;
import ca.uhn.fhir.jpa.model.config.PartitionSettings;
import ca.uhn.fhir.jpa.model.entity.ModelConfig;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class FhirServerConfigR4 extends BaseJavaConfigR4 {

    private static final String FALSE = "false";
    private final DataSource myDataSource;

    public FhirServerConfigR4(DataSource myDataSource) {
        this.myDataSource = myDataSource;
    }

    @Bean
    public DaoConfig daoConfig() {
        DaoConfig daoConfig = new DaoConfig();
        daoConfig.setResourceServerIdStrategy(DaoConfig.IdStrategyEnum.UUID);
        return daoConfig;
    }

    @Bean
    public ModelConfig modelConfig() {
        return daoConfig().getModelConfig();
    }

    @Bean
    public PartitionSettings partitionSettings() {
        return new PartitionSettings();
    }

    @Bean
    public ServletRegistrationBean<FhirServlet> hapiServletRegistration(FhirServlet fhirServlet) {
        ServletRegistrationBean<FhirServlet> servletRegistrationBean = new ServletRegistrationBean<>();
        servletRegistrationBean.setServlet(fhirServlet);
        servletRegistrationBean.addUrlMappings("/fhir/*");
        servletRegistrationBean.setLoadOnStartup(1);

        return servletRegistrationBean;
    }

    @Override
    @Bean()
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean retVal = super.entityManagerFactory();
        retVal.setPersistenceUnitName("HAPI_PU");

        try {
            retVal.setDataSource(myDataSource);
        } catch (Exception e) {
            throw new ConfigurationException("Could not set the data source due to a configuration issue", e);
        }
        Properties properties = new Properties();
        properties.put("hibernate.search.model_mapping", "ca.uhn.fhir.jpa.search.LuceneSearchMappingFactory");
        properties.put("hibernate.format_sql", FALSE);
        properties.put("hibernate.show_sql", FALSE);
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.jdbc.batch_size", "20");
        properties.put("hibernate.cache.use_query_cache", FALSE);
        properties.put("hibernate.cache.use_second_level_cache", FALSE);
        properties.put("hibernate.cache.use_structured_entries", FALSE);
        properties.put("hibernate.cache.use_minimal_puts", FALSE);
        properties.put("hibernate.search.default.directory_provider", "filesystem");
        properties.put("hibernate.search.default.indexBase", "target/lucenefiles");
        properties.put("hibernate.search.lucene_version", "LUCENE_CURRENT");
        retVal.setJpaProperties(properties);
        return retVal;
    }

    @Bean
    @Primary
    public JpaTransactionManager hapiTransactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager retVal = new JpaTransactionManager();
        retVal.setEntityManagerFactory(entityManagerFactory);
        return retVal;
    }

    @Primary
    @Bean
    public HibernateDialectProvider jpaStarterDialectProvider(LocalContainerEntityManagerFactoryBean myEntityManagerFactory) {
        return new JpaHibernateDialectProvider(myEntityManagerFactory);
    }

    @Bean
    public FhirContext fhirContext() {
        return FhirContext.forR4();
    }

}

