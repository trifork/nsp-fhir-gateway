package dk.medcom.edelivery.util;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class HashingTest {

    @Test
    void hashing_test() {

        assertEquals(
                "35105B20180DAE56D84DA61AC8DA59A1C6CB2DBE",
                Hashing.sha1HexBinary("This is my Document".getBytes(StandardCharsets.UTF_8))
        );
    }
}
