package dk.medcom.edelivery.fhir;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openehealth.ipf.commons.core.OidGenerator;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IdUtilTest {

    @Test
    void from_UUID_to_OID_and_back() {
        var uuid = UUID.randomUUID();

        var oid = IdUtil.toOID(uuid);

        assertEquals(uuid.toString(), IdUtil.toUUID(oid).toString());
    }

    @Test
    void from_OID_to_UUID_and_back() {
        var oid = OidGenerator.uniqueOid();

        var uuid = IdUtil.toUUID(oid);

        assertEquals(oid.toString(), IdUtil.toOID(uuid).toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2.25",
            "2.25.1.1",
            "2.26.1",
            "1.25.1"
    })
    void throws_if_oid_is_not_uuid_based(String oidStr) {
        var oid = IdUtil.toOID(oidStr);
        assertThrows(IllegalArgumentException.class, () -> IdUtil.toUUID(oid));
    }

    @Test
    void test_edge_cases() {
        List<Long> longValues = List.of(Long.MIN_VALUE, -1L, 0L, 1L, 10L, Long.MAX_VALUE);

        for (int i = 0; i < longValues.size(); i++) {
            for (int j = 0; j < longValues.size(); j++) {
                var msb = longValues.get(i);
                var lsb = longValues.get(j);
                var uuid = new UUID(msb, lsb);
                var oid = IdUtil.toOID(uuid);
                System.out.printf("msb = %20d, lsb = %21d , uuid = %s, oid = %s%n", msb, lsb, uuid, oid);
                assertEquals(uuid.toString(), IdUtil.toUUID(oid).toString());
            }
        }
    }

}
