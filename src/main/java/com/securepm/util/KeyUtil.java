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

/**
 * Classe utilitária para geração e derivação de chaves criptográficas.
 */
public class KeyUtil {

    // Nome do arquivo onde o salt será armazenado.
    private static final String SALT_FILE = "salt.dat";

    // Tamanho do salt em bytes.
    private static final int SALT_LENGTH = 16;

    // Número de iterações do algoritmo de derivação de chave.
    private static final int ITERATIONS = 65536;

    // Tamanho da chave resultante em bits.
    private static final int KEY_LENGTH = 256;

    /**
     * Método que carrega um salt existente ou gera um novo caso não exista.
     *
     * @return Array de bytes contendo o salt.
     * @throws Exception Caso ocorra falha na leitura ou escrita do arquivo.
     */
    public static byte[] loadOrGenerateSalt() throws Exception {
        File f = new File(SALT_FILE);

        // Se o arquivo de salt já existir, lê e retorna seu conteúdo.
        if (f.exists()) {
            return Files.readAllBytes(Paths.get(SALT_FILE));
        }

        // Se não existir, gera um novo salt aleatório.
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);

        // Salva o salt gerado no arquivo para uso futuro.
        try (FileOutputStream fos = new FileOutputStream(f)) {
            fos.write(salt);
        }

        return salt;
    }

    /**
     * Método que deriva uma chave AES a partir de uma senha mestre utilizando PBKDF2.
     *
     * @param masterPassword A senha mestre informada pelo usuário.
     * @return A chave secreta derivada no formato SecretKey.
     * @throws Exception Caso ocorra falha na derivação da chave.
     */
    public static SecretKey deriveAESKeyFromPassword(String masterPassword) throws Exception {
        // Carrega ou gera o salt necessário.
        byte[] salt = loadOrGenerateSalt();

        // Configura o especificador de chave baseado na senha, salt, iterações e comprimento da chave.
        PBEKeySpec spec = new PBEKeySpec(
                masterPassword.toCharArray(),
                salt,
                ITERATIONS,
                KEY_LENGTH
        );

        // Obtém a fábrica para gerar a chave com PBKDF2 usando HMAC SHA-256.
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        // Gera os bytes da chave.
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();

        // Cria uma chave AES com os bytes gerados.
        return new SecretKeySpec(keyBytes, "AES");
    }
}
