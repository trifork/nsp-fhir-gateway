package dk.medcom.edelivery.fhir;

import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Bundle;

public interface MessageBundleFacade {

    Bundle getMessageBundle(IIdType id);

    Bundle saveMessageBundle(Bundle messageBundle);

}
