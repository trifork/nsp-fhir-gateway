package dk.medcom.edelivery.fhir;

import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.stereotype.Component;

@Component
public class SimpleMessageBundleProvider implements MessageBundleFacade {

    private final IFhirResourceDao<Bundle> bundleIFhirResourceDao;

    public SimpleMessageBundleProvider(IFhirResourceDao<Bundle> bundleIFhirResourceDao) {
        this.bundleIFhirResourceDao = bundleIFhirResourceDao;
    }

    @Override
    public Bundle getMessageBundle(IIdType id) {
        return bundleIFhirResourceDao.read(id);
    }

    @Override
    public Bundle saveMessageBundle(Bundle messageBundle) {
        return (Bundle) bundleIFhirResourceDao.create(messageBundle).getResource();
    }
}
