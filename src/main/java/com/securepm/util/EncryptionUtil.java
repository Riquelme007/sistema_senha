package com.securepm.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Utilitário para cifrar e decifrar dados usando AES/CBC/PKCS5Padding.
 */
public class EncryptionUtil {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int IV_LENGTH = 16; // 16 bytes = 128 bits

    /**
     * Cifra os dados em plainText usando a chave AES fornecida.
     * @param plainText texto em UTF-8
     * @param key       SecretKey AES (256 bits) derivada pelo KeyUtil
     * @return array de bytes onde:
     *         [0..15]   = IV gerado aleatoriamente
     *         [16..n-1] = ciphertext AES
     * @throws Exception se algoritmo ou parâmetros inválidos
     */
    public static byte[] encrypt(String plainText, SecretKey key) throws Exception {
        // Gera IV aleatório
        byte[] iv = new byte[IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

        byte[] cipherBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
        // Concatena IV + ciphertext
        byte[] result = new byte[IV_LENGTH + cipherBytes.length];
        System.arraycopy(iv, 0, result, 0, IV_LENGTH);
        System.arraycopy(cipherBytes, 0, result, IV_LENGTH, cipherBytes.length);
        return result;
    }

    /**
     * Decifra array retornado por encrypt (IV + ciphertext) e devolve o texto em UTF-8.
     * @param encryptedData array [IV(16) + ciphertext]
     * @param key           mesma SecretKey usada para cifrar
     * @return texto original
     * @throws Exception se falhar a decifragem
     */
    public static String decrypt(byte[] encryptedData, SecretKey key) throws Exception {
        // Separa IV
        byte[] iv = Arrays.copyOfRange(encryptedData, 0, IV_LENGTH);
        byte[] cipherBytes = Arrays.copyOfRange(encryptedData, IV_LENGTH, encryptedData.length);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

        byte[] plainBytes = cipher.doFinal(cipherBytes);
        return new String(plainBytes, "UTF-8");
    }
}
