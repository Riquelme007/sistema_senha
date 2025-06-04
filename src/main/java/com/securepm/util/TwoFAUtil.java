package com.securepm.util;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;

import javax.crypto.KeyGenerator;

import javax.crypto.SecretKey;
import java.net.URLEncoder;

import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;


/**
 * Utilitário para geração e verificação de códigos TOTP (2FA) compatíveis com Google Authenticator.
 */
public class TwoFAUtil {
    private static final String TOTP_ALGORITHM = "HmacSHA1";
    private static final int SECRET_KEY_BITS = 160; // TOTP geralmente usa chave de 160 bits

    /**
     * Gera uma chave secreta base32 para 2FA.
     */
    public static String generateBase32Secret() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(TOTP_ALGORITHM);
        keyGen.init(SECRET_KEY_BITS);
        SecretKey key = keyGen.generateKey();
        byte[] raw = key.getEncoded();
        // Converte para Base32 (usamos Base32 “case-insensitive”)
        return Base32Encoder.encode(raw);
    }

    /**
     * Gera código TOTP (6 dígitos) para validar no login.
     *
     * @param base32Secret segredo em Base32
     * @return código TOTP (ex.: "123456")
     */
    public static String generateTOTPCode(String base32Secret) throws Exception {
        byte[] keyBytes = Base32Encoder.decode(base32Secret);
        SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(keyBytes, TOTP_ALGORITHM);

        TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator(
        );

        Instant now = Instant.now();
        int code = totp.generateOneTimePassword(secretKey, now);
        return String.format("%06d", code);
    }

    /**
     * Verifica se o código passado bate com o gerado hoje (janela atual ± 1 intervalo).
     * @param base32Secret segredo base32
     * @param code         código fornecido pelo usuário
     * @return true se válido
     */
    public static boolean verifyTOTPCode(String base32Secret, String code) throws Exception {
        byte[] keyBytes = Base32Encoder.decode(base32Secret);
        SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(keyBytes, TOTP_ALGORITHM);

        TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator(
        );

        Instant now = Instant.now();
        // verifica janela atual e ± 1 para compensar clock drift
        int generated = totp.generateOneTimePassword(secretKey, now);
        if (String.format("%06d", generated).equals(code)) {
            return true;
        }
        // window -1
        Instant before = now.minus(Duration.ofSeconds(30));
        generated = totp.generateOneTimePassword(secretKey, before);
        if (String.format("%06d", generated).equals(code)) {
            return true;
        }
        // window +1
        Instant after = now.plus(Duration.ofSeconds(30));
        generated = totp.generateOneTimePassword(secretKey, after);
        return String.format("%06d", generated).equals(code);
    }

    /**
     * Gera a URL para QR Code que contas como “otpauth://totp/{issuer}:{username}?secret={secret}&issuer={issuer}”
     * para os aplicativos de autenticação. Exemplo de issuer: “SecurePMApp”.
     *
     * @param issuer    nome do seu app (ex.: “SecurePM”)
     * @param username  nome de usuário (ex.: “admin”)
     * @param secret    segredo base32 gerado
     * @return URI que pode ser transformada em QR code
     */
    public static String getGoogleAuthenticatorBarCode(String issuer, String username, String secret) throws Exception {
        String normalizedIssuer = URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
        String normalizedUsername = URLEncoder.encode(username, "UTF-8").replace("+", "%20");
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                normalizedIssuer, normalizedUsername, secret, normalizedIssuer);
    }

    /**
     * Pequeno encoder/decoder Base32 para secrets TOTP.
     * Nota: Existem bibliotecas especializadas, mas aqui implementamos de forma simples.
     */
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
