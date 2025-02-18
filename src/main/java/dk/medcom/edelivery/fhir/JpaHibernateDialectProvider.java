package dk.medcom.edelivery.fhir;

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.jpa.config.HibernateDialectProvider;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.internal.StandardDialectResolver;
import org.hibernate.engine.jdbc.dialect.spi.DatabaseMetaDataDialectResolutionInfoAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import java.sql.SQLException;
import java.util.Optional;

public class JpaHibernateDialectProvider extends HibernateDialectProvider {

    private final Dialect dialect;

    public JpaHibernateDialectProvider(LocalContainerEntityManagerFactoryBean myEntityManagerFactory) {
        var dataSource = Optional.ofNullable(myEntityManagerFactory.getDataSource())
                .orElseThrow(() -> new ConfigurationException("Datasource was null."));

        try (var connection = dataSource.getConnection()) {
            dialect = new StandardDialectResolver()
                    .resolveDialect(new DatabaseMetaDataDialectResolutionInfoAdapter(connection.getMetaData()));
        } catch (SQLException sqlException) {
            throw new ConfigurationException(sqlException.getMessage(), sqlException);
        }
    }

    @Override
    public Dialect getDialect() {
        return dialect;
    }
}
