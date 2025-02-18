package dk.medcom.edelivery.util;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hashing {

    private Hashing() {
    }

    public static String sha1HexBinary(byte[] in) {
        try {
            MessageDigest msdDigest = MessageDigest.getInstance("SHA-1");
            var digest = msdDigest.digest(in);
            return DatatypeConverter.printHexBinary(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
