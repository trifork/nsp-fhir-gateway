package dk.medcom.edelivery.integration.domibus.model;

import org.springframework.util.StringUtils;

import java.util.Objects;

public class Party {
    private final String partyId;
    private final String partyIdType;

    public Party(final String partyId, final String partyIdType) {
        if (!StringUtils.hasLength(partyId)) {
            throw new IllegalArgumentException("partyId must not be empty");
        }
        this.partyId = partyId;
        this.partyIdType = partyIdType;
    }

    public String getPartyId() {
        return this.partyId;
    }

    public String getPartyIdType() {
        return this.partyIdType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Party)) return false;
        Party party = (Party) o;
        return partyId.equals(party.partyId) &&
                Objects.equals(partyIdType, party.partyIdType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partyId, partyIdType);
    }

    @Override
    public String toString() {
        return "Party{" +
                "partyId='" + partyId + '\'' +
                ", partyIdType='" + partyIdType + '\'' +
                '}';
    }
}
