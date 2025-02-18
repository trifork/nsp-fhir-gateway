package dk.medcom.edelivery.fhir;

import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.IdType;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import org.openehealth.ipf.commons.core.OidGenerator;

import javax.annotation.Nonnull;
import java.math.BigInteger;
import java.util.UUID;

public class IdUtil {

    private IdUtil() {
    }

    @Nonnull
    public static String toOIDString(@Nonnull IIdType idType) {
        return toOID(UUID.fromString(idType.getIdPart())).toString();
    }

    @Nonnull
    public static IIdType fromOIDString(@Nonnull String oidStr) {
        return new IdType(toUUID(toOID(oidStr)).toString());
    }

    @Nonnull
    public static Oid toOID(@Nonnull UUID uuid) {
        return OidGenerator.asOid(uuid);
    }

    @Nonnull
    public static UUID toUUID(@Nonnull Oid oid) {
        var integerPart = oid.toString().replace("2.25.", "");
        var bigInteger = new BigInteger(integerPart);

        var msb = bigInteger.shiftRight(64).longValue();
        var lsb = bigInteger.shiftLeft(64).shiftRight(64).longValue();

        return new UUID(msb, lsb);
    }

    public static Oid toOID(String strOid) {
        try {
            return new Oid(strOid);
        } catch (GSSException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
