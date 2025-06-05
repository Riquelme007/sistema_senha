package com.securepm.util;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;

/**
 * Utilitário para geração e validação de códigos TOTP (2FA).
 * Baseado no algoritmo Time-Based One-Time Password.
 */
public class TwoFAUtil {
    private static final String TOTP_ALGORITHM = "HmacSHA1";
    private static final int SECRET_KEY_BITS = 160; // 160 bits para chave secreta

    /**
     * Gera uma chave secreta codificada em Base32 para uso no TOTP.
     *
     * @return chave secreta em Base32
     * @throws NoSuchAlgorithmException caso o algoritmo não seja suportado
     */
    public static String generateBase32Secret() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(TOTP_ALGORITHM);
        keyGen.init(SECRET_KEY_BITS);
        SecretKey key = keyGen.generateKey();
        byte[] raw = key.getEncoded();
        return Base32Encoder.encode(raw);
    }

    /**
     * Gera um código TOTP de 6 dígitos a partir da chave secreta Base32.
     *
     * @param base32Secret chave secreta codificada em Base32
     * @return código TOTP de 6 dígitos
     * @throws Exception em caso de erro na geração do código
     */
    public static String generateTOTPCode(String base32Secret) throws Exception {
        byte[] keyBytes = Base32Encoder.decode(base32Secret);
        SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(keyBytes, TOTP_ALGORITHM);

        TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator();

        Instant now = Instant.now();
        int code = totp.generateOneTimePassword(secretKey, now);
        return String.format("%06d", code);
    }

    /**
     * Verifica se o código TOTP fornecido é válido para a chave secreta Base32.
     * Permite uma janela de tolerância de 30 segundos antes e depois do instante atual.
     *
     * @param base32Secret chave secreta em Base32
     * @param code código TOTP a verificar
     * @return true se válido, false caso contrário
     * @throws Exception em caso de erro na verificação
     */
    public static boolean verifyTOTPCode(String base32Secret, String code) throws Exception {
        byte[] keyBytes = Base32Encoder.decode(base32Secret);
        SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(keyBytes, TOTP_ALGORITHM);

        TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator();

        Instant now = Instant.now();

        // Verifica código no instante atual
        int generated = totp.generateOneTimePassword(secretKey, now);
        if (String.format("%06d", generated).equals(code)) {
            return true;
        }

        // Verifica código 30 segundos atrás
        Instant before = now.minus(Duration.ofSeconds(30));
        generated = totp.generateOneTimePassword(secretKey, before);
        if (String.format("%06d", generated).equals(code)) {
            return true;
        }

        // Verifica código 30 segundos à frente
        Instant after = now.plus(Duration.ofSeconds(30));
        generated = totp.generateOneTimePassword(secretKey, after);
        return String.format("%06d", generated).equals(code);
    }

    /**
     * Gera a URL para exibir o QR Code no Google Authenticator ou apps similares.
     *
     * @param issuer nome da organização ou serviço
     * @param username nome do usuário
     * @param secret chave secreta em Base32
     * @return URL codificada para uso no app autenticador
     * @throws Exception em caso de erro na codificação da URL
     */
    public static String getGoogleAuthenticatorBarCode(String issuer, String username, String secret) throws Exception {
        String normalizedIssuer = URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
        String normalizedUsername = URLEncoder.encode(username, "UTF-8").replace("+", "%20");
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                normalizedIssuer, normalizedUsername, secret, normalizedIssuer);
    }

    /**
     * Implementação simples para codificação e decodificação Base32,
     * usada para conversão da chave secreta binária para formato legível.
     */
    public static class Base32Encoder {
        private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

        /**
         * Codifica bytes em Base32.
         * @param data bytes a codificar
         * @return string codificada em Base32
         */
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

        /**
         * Decodifica string Base32 em bytes.
         * @param encoded string codificada em Base32
         * @return array de bytes decodificados
         */
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
