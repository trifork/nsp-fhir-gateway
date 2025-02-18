package dk.medcom.edelivery.integration.domibus;

import dk.medcom.edelivery.integration.domibus.model.Party;

import static dk.medcom.edelivery.model.MedComConstants.PARTY_ID_PREFIX;

public class PartyMapper {

    private PartyMapper() {
    }

    public static Party toParty(String party) {
        var idx = party.indexOf(PARTY_ID_PREFIX);
        if (idx >= 0) {
            idx += PARTY_ID_PREFIX.length();
            var id = party.substring(idx + 1);
            var type = party.substring(0, idx);
            return new Party(id, type);
        } else {
            return new Party(party, null);
        }
    }

}
