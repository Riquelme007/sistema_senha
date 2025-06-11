package com.securepm.util;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;

/**
 * Encapsula a lógica para autenticação de dois fatores (2FA) usando o padrão TOTP.
 * Inclui geração de chaves, verificação de códigos e criação de URIs para apps autenticadores.
 */
public class TwoFactorCodeUtils {
    // Algoritmo HMAC usado como base para o TOTP, conforme a RFC 4226.
    private static final String TOTP_ALGORITHM = "HmacSHA1";
    // Tamanho da chave secreta em bits, 160 bits é o padrão para SHA-1.
    private static final int SECRET_KEY_BITS = 160;

    /**
     * Cria uma nova chave secreta criptográfica e a codifica para o formato Base32.
     * O formato Base32 é usado para permitir que a chave seja facilmente digitada ou
     * copiada por humanos.
     *
     * @return Uma string contendo a chave secreta em formato Base32.
     * @throws NoSuchAlgorithmException Se o algoritmo HmacSHA1 não for suportado pelo ambiente.
     */
    public static String generateBase32Secret() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(TOTP_ALGORITHM);
        keyGen.init(SECRET_KEY_BITS);
        SecretKey secretKey = keyGen.generateKey();
        byte[] rawKey = secretKey.getEncoded();
        // Converte a chave binária bruta para uma representação de texto legível.
        return Base32Encoder.encode(rawKey);
    }

    /**
     * Valida um código TOTP fornecido pelo usuário contra a chave secreta.
     * Este método implementa uma janela de tolerância para compensar pequenas
     * diferenças de relógio entre o cliente e o servidor.
     *
     * @param base32Secret A chave secreta do usuário, em formato Base32.
     * @param code O código de 6 dígitos inserido pelo usuário.
     * @return 'true' se o código for válido para a janela de tempo atual, passada ou futura. 'false' caso contrário.
     * @throws InvalidKeyException Se a chave secreta fornecida for inválida.
     */
    public static boolean verifyTOTPCode(String base32Secret, String code) throws InvalidKeyException {
        byte[] keyBytes = Base32Encoder.decode(base32Secret);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, TOTP_ALGORITHM);
        TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator();

        Instant now = Instant.now();

        // 1. Verifica o código para o instante de tempo atual.
        if (isCodeMatching(totp, secretKey, now, code)) {
            return true;
        }

        // 2. Verifica o código para a janela de tempo anterior (ex: 30 segundos atrás).
        Instant previousWindow = now.minus(totp.getTimeStep());
        if (isCodeMatching(totp, secretKey, previousWindow, code)) {
            return true;
        }

        // 3. Verifica o código para a próxima janela de tempo (ex: 30 segundos à frente).
        Instant nextWindow = now.plus(totp.getTimeStep());
        return isCodeMatching(totp, secretKey, nextWindow, code);
    }

    /**
     * Função auxiliar para gerar e comparar um código TOTP em um instante específico.
     */
    private static boolean isCodeMatching(TimeBasedOneTimePasswordGenerator totp, SecretKey key, Instant time, String userCode) throws InvalidKeyException {
        int generatedCode = totp.generateOneTimePassword(key, time);
        String formattedGeneratedCode = String.format("%06d", generatedCode);
        return formattedGeneratedCode.equals(userCode);
    }

    /**
     * Monta uma URI no padrão "otpauth", que pode ser usada para gerar um QR Code.
     * Apps como Google Authenticator e Authy podem escanear este QR Code para configurar a conta 2FA.
     *
     * @param issuer   O nome do serviço ou aplicação (ex: "SecurePM").
     * @param username O identificador da conta do usuário (ex: "email@example.com").
     * @param secret   A chave secreta da conta, em formato Base32.
     * @return Uma string formatada como uma URI 'otpauth'.
     * @throws UnsupportedEncodingException Se a codificação UTF-8 não for suportada.
     */
    public static String getGoogleAuthenticatorBarCode(String issuer, String username, String secret) throws UnsupportedEncodingException {
        // Codifica os parâmetros para garantir que a URI seja válida.
        String encodedIssuer = URLEncoder.encode(issuer, StandardCharsets.UTF_8.toString()).replace("+", "%20");
        String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8.toString()).replace("+", "%20");

        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                encodedIssuer, encodedUsername, secret, encodedIssuer
        );
    }

    /**
     * Classe aninhada que fornece uma implementação simples de codificação e decodificação Base32,
     * conforme a RFC 4648, mas sem suporte a todos os casos de borda. Adequada para este contexto.
     */
    public static class Base32Encoder {
        private static final String BASE32_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

        public static String encode(final byte[] data) {
            if (data == null || data.length == 0) {
                return "";
            }

            StringBuilder sb = new StringBuilder((data.length * 8 + 4) / 5);
            int buffer = 0;
            int bitsLeft = 0;

            for (byte b : data) {
                buffer = (buffer << 8) | (b & 0xFF);
                bitsLeft += 8;
                while (bitsLeft >= 5) {
                    int index = (buffer >> (bitsLeft - 5)) & 0x1F;
                    sb.append(BASE32_ALPHABET.charAt(index));
                    bitsLeft -= 5;
                }
            }
            if (bitsLeft > 0) {
                int index = (buffer << (5 - bitsLeft)) & 0x1F;
                sb.append(BASE32_ALPHABET.charAt(index));
            }

            return sb.toString();
        }

        public static byte[] decode(final String base32) {
            String sanitized = base32.trim().toUpperCase().replaceAll("=", "");
            if (sanitized.isEmpty()) {
                return new byte[0];
            }

            int outLength = (sanitized.length() * 5) / 8;
            byte[] out = new byte[outLength];
            int buffer = 0;
            int bitsLeft = 0;
            int outIndex = 0;

            for (char c : sanitized.toCharArray()) {
                int value = BASE32_ALPHABET.indexOf(c);
                if (value < 0) {
                    throw new IllegalArgumentException("Caractere inválido na string Base32: " + c);
                }
                buffer = (buffer << 5) | value;
                bitsLeft += 5;
                if (bitsLeft >= 8) {
                    out[outIndex++] = (byte) (buffer >> (bitsLeft - 8));
                    bitsLeft -= 8;
                }
            }
            return out;
        }
    }
}