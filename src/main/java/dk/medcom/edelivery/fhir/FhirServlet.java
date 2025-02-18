package dk.medcom.edelivery.fhir;

import ca.uhn.fhir.jpa.api.config.DaoConfig;
import ca.uhn.fhir.jpa.api.dao.IFhirSystemDao;
import ca.uhn.fhir.jpa.provider.IJpaSystemProvider;
import ca.uhn.fhir.jpa.provider.r4.JpaConformanceProviderR4;
import ca.uhn.fhir.jpa.searchparam.registry.ISearchParamRegistry;
import ca.uhn.fhir.rest.server.RestfulServer;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Meta;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;

@Service
public class FhirServlet extends RestfulServer {

    private final IFhirSystemDao<Bundle, Meta> fhirSystemDao;
    private final IJpaSystemProvider jpaSystemProvider;
    private final DaoConfig daoConfig;
    private final ISearchParamRegistry searchParamRegistry;

    public FhirServlet(
            IFhirSystemDao<Bundle, Meta> fhirSystemDao,

            IJpaSystemProvider jpaSystemProvider, DaoConfig daoConfig, ISearchParamRegistry searchParamRegistry
    ) {
        this.fhirSystemDao = fhirSystemDao;
        this.jpaSystemProvider = jpaSystemProvider;
        this.daoConfig = daoConfig;
        this.searchParamRegistry = searchParamRegistry;
    }

    @Override
    protected void initialize() throws ServletException {

        setFhirContext(fhirSystemDao.getContext());
        registerProvider(jpaSystemProvider);
        setServerConformanceProvider(new JpaConformanceProviderR4(this, fhirSystemDao,
                daoConfig, searchParamRegistry));
    }
}
