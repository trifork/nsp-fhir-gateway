package dk.medcom.edelivery.integration.edelivery;

import dk.medcom.edelivery.model.MedComConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubmissionFactoryTest {

    @Test
    void mapCrossDomainService() {
        assertEquals("urn:www.digst.dk:profile:sdn-emergence", SubmissionFactory.mapCrossDomainService(MedComConstants.EMERGENCE_FOD));
        assertEquals("urn:www.digst.dk:profile:fod-emergence", SubmissionFactory.mapCrossDomainService(MedComConstants.EMERGENCE_SDN));
        assertEquals("urn:www.digst.dk:profile:sdn-emergence", SubmissionFactory.mapCrossDomainService(MedComConstants.DISTRIBUTION_FOD));
        assertEquals("urn:www.digst.dk:profile:fod-emergence", SubmissionFactory.mapCrossDomainService(MedComConstants.DISTRIBUTION_SDN));
        assertEquals("anyOtherService", SubmissionFactory.mapCrossDomainService("anyOtherService"));
        assertEquals("anyOtherService2", SubmissionFactory.mapCrossDomainService("anyOtherService2"));
    }

    @Test
    void mapDomainService() {
        assertEquals("urn:www.digst.dk:profile:sdn-distribution", SubmissionFactory.mapDomainService(MedComConstants.EMERGENCE_SDN));
        assertEquals("urn:www.digst.dk:profile:fod-distribution", SubmissionFactory.mapDomainService(MedComConstants.EMERGENCE_FOD));
        assertEquals("anyOtherService", SubmissionFactory.mapDomainService("anyOtherService"));
        assertEquals("anyOtherService2", SubmissionFactory.mapDomainService("anyOtherService2"));
    }

}
