package dk.jimmikristensen.aaws.domain.encryption;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class SHA1 {
    public static String encrypt(String plainText) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        digest.update(plainText.getBytes("UTF-8"));
        return byteToHex(digest.digest());
    }
    
    private static String byteToHex(final byte[] hash) {
        String result;
        try (Formatter formatter = new Formatter()) {
            for (byte b : hash) {
                formatter.format("%02x", b);
            }   result = formatter.toString();
        }
        return result;
    }
}