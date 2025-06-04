package com.securepm.util;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;

public class TwoFAUtil {
    private static final String TOTP_ALGORITHM = "HmacSHA1";
    private static final int SECRET_KEY_BITS = 160;

    public static String generateBase32Secret() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(TOTP_ALGORITHM);
        keyGen.init(SECRET_KEY_BITS);
        SecretKey key = keyGen.generateKey();
        byte[] raw = key.getEncoded();
        return Base32Encoder.encode(raw);
    }

    public static String generateTOTPCode(String base32Secret) throws Exception {
        byte[] keyBytes = Base32Encoder.decode(base32Secret);
        SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(keyBytes, TOTP_ALGORITHM);

        TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator();

        Instant now = Instant.now();
        int code = totp.generateOneTimePassword(secretKey, now);
        return String.format("%06d", code);
    }

    public static boolean verifyTOTPCode(String base32Secret, String code) throws Exception {
        byte[] keyBytes = Base32Encoder.decode(base32Secret);
        SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(keyBytes, TOTP_ALGORITHM);

        TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator();

        Instant now = Instant.now();

        int generated = totp.generateOneTimePassword(secretKey, now);
        if (String.format("%06d", generated).equals(code)) {
            return true;
        }

        Instant before = now.minus(Duration.ofSeconds(30));
        generated = totp.generateOneTimePassword(secretKey, before);
        if (String.format("%06d", generated).equals(code)) {
            return true;
        }

        Instant after = now.plus(Duration.ofSeconds(30));
        generated = totp.generateOneTimePassword(secretKey, after);
        return String.format("%06d", generated).equals(code);
    }

    public static String getGoogleAuthenticatorBarCode(String issuer, String username, String secret) throws Exception {
        String normalizedIssuer = URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
        String normalizedUsername = URLEncoder.encode(username, "UTF-8").replace("+", "%20");
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                normalizedIssuer, normalizedUsername, secret, normalizedIssuer);
    }

    public static class Base32Encoder {
        private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

        public static String encode(byte[] data) {
            StringBuilder result = new StringBuilder();
            int buffer = data[0], next = 1, bitsLeft = 8;

            while (bitsLeft > 0 || next < data.length) {
                if (bitsLeft < 5) {
                    if (next < data.length) {
                        buffer <<= 8;
                        buffer |= (data[next++] & 0xff);
                        bitsLeft += 8;
                    } else {
                        int pad = 5 - bitsLeft;
                        buffer <<= pad;
                        bitsLeft += pad;
                    }
                }
                int index = 0x1F & (buffer >> (bitsLeft - 5));
                bitsLeft -= 5;
                result.append(ALPHABET.charAt(index));
            }

            while (result.length() % 8 != 0) {
                result.append('=');
            }

            return result.toString();
        }

        public static byte[] decode(String encoded) {
            encoded = encoded.trim().replace("=", "");
            int encodedLength = encoded.length();
            int outLength = encodedLength * 5 / 8;
            byte[] result = new byte[outLength];
            int buffer = 0, bitsLeft = 0, index = 0;

            for (char c : encoded.toCharArray()) {
                int val = ALPHABET.indexOf(c);
                buffer <<= 5;
                buffer |= val & 0x1F;
                bitsLeft += 5;

                if (bitsLeft >= 8) {
                    result[index++] = (byte) ((buffer >> (bitsLeft - 8)) & 0xFF);
                    bitsLeft -= 8;
                }
            }

            return result;
        }
    }
}
