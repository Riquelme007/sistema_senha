package com.securepm.util;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;

/**
 * Utilitário para derivar uma SecretKey AES a partir de uma senha (masterPassword)
 * usando PBKDF2WithHmacSHA256. Armazena/recupera salt em salt.dat.
 */
public class KeyUtil {
    private static final String SALT_FILE = "salt.dat";
    private static final int SALT_LENGTH = 16;         // 16 bytes = 128 bits
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;         // 256 bits para AES-256

    /**
     * Gera ou recupera o salt usado no PBKDF2.
     * @return array de bytes do salt
     * @throws Exception se falhar leitura/escrita
     */
    public static byte[] loadOrGenerateSalt() throws Exception {
        File f = new File(SALT_FILE);
        if (f.exists()) {
            return Files.readAllBytes(Paths.get(SALT_FILE));
        }
        // Gera novo salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        // Armazena em disco
        try (FileOutputStream fos = new FileOutputStream(f)) {
            fos.write(salt);
        }
        return salt;
    }

    /**
     * Dada a masterPassword, deriva uma chave AES (SecretKey).
     * @param masterPassword senha mestra em texto puro
     * @return SecretKey para AES-256
     * @throws Exception se o algoritmo não estiver disponível
     */
    public static SecretKey deriveAESKeyFromPassword(String masterPassword) throws Exception {
        byte[] salt = loadOrGenerateSalt();
        PBEKeySpec spec = new PBEKeySpec(
                masterPassword.toCharArray(),
                salt,
                ITERATIONS,
                KEY_LENGTH
        );
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }
}
