package dk.medcom.edelivery.integration.domibus;

import org.junit.jupiter.api.Test;

import static dk.medcom.edelivery.model.MedComConstants.PARTY_ID_PREFIX;
import static org.junit.jupiter.api.Assertions.*;

class PartyMapperTest {

    @Test
    void mapPartyWithType() {
        var withType = PartyMapper.toParty(PARTY_ID_PREFIX + ":the id");

        assertEquals(PARTY_ID_PREFIX, withType.getPartyIdType());
        assertEquals("the id", withType.getPartyId());
    }

    @Test
    void mapPartyWithType2() {
        var withType = PartyMapper.toParty(PARTY_ID_PREFIX + ":the id");

        assertEquals(PARTY_ID_PREFIX, withType.getPartyIdType());
        assertEquals("the id", withType.getPartyId());
    }

    @Test
    void mapPartyWithNoType() {
        var withType = PartyMapper.toParty("the id");

        assertNull(withType.getPartyIdType());
        assertEquals("the id", withType.getPartyId());
    }

}
