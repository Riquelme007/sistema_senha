package com.securepm.util;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;

public class KeyUtil {
    private static final String SALT_FILE = "salt.dat";
    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;

    public static byte[] loadOrGenerateSalt() throws Exception {
        File f = new File(SALT_FILE);
        if (f.exists()) {
            return Files.readAllBytes(Paths.get(SALT_FILE));
        }
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        try (FileOutputStream fos = new FileOutputStream(f)) {
            fos.write(salt);
        }
        return salt;
    }

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
