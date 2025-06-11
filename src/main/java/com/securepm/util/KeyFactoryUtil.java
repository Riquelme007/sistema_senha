package com.securepm.util;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * Classe utilitária focada na criação e derivação de chaves criptográficas.
 * Centraliza a lógica para gerar um 'salt' persistente e derivar chaves a partir de senhas.
 */
public class KeyFactoryUtil {

    // Nome do arquivo que armazena o 'salt' criptográfico para persistência entre sessões.
    private static final String SALT_FILE = "salt.dat";

    // Define o comprimento do salt em bytes (128 bits), um valor padrão e seguro.
    private static final int SALT_LENGTH = 16;

    // Número de iterações para o PBKDF2. Um valor alto aumenta a resistência a ataques de força bruta.
    private static final int ITERATIONS = 65536;

    // Comprimento da chave AES a ser derivada, em bits (neste caso, 256 bits).
    private static final int KEY_LENGTH = 256;

    /**
     * Gerencia o ciclo de vida do salt. Tenta carregar o salt de um arquivo se ele já existir;
     * caso contrário, gera um novo, o salva em disco para uso futuro e o retorna.
     *
     * @return Um array de bytes contendo o salt.
     * @throws IOException Se ocorrer um erro de I/O ao ler ou escrever o arquivo de salt.
     */
    public static byte[] loadOrGenerateSalt() throws IOException {
        File saltFile = new File(SALT_FILE);

        // Se o arquivo de salt já foi criado em uma execução anterior, apenas o lê.
        if (saltFile.exists()) {
            return Files.readAllBytes(Paths.get(SALT_FILE));
        }

        // Se o arquivo não existe, um novo salt aleatório e seguro é gerado.
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);

        // Salva o novo salt em disco para garantir que o mesmo salt seja sempre reutilizado.
        try (FileOutputStream fos = new FileOutputStream(saltFile)) {
            fos.write(salt);
        }

        return salt;
    }

    /**
     * Transforma uma senha de texto simples fornecida pelo usuário em uma chave de criptografia
     * robusta (SecretKey) usando o algoritmo PBKDF2 (Password-Based Key Derivation Function 2).
     *
     * @param masterPassword A senha mestra que servirá como base para a derivação.
     * @return Uma SecretKey pronta para ser usada em algoritmos de criptografia AES.
     * @throws NoSuchAlgorithmException Se o algoritmo PBKDF2WithHmacSHA256 não for suportado.
     * @throws InvalidKeySpecException Se os parâmetros fornecidos para a derivação forem inválidos.
     * @throws IOException Se houver uma falha ao acessar o arquivo de salt.
     */
    public static SecretKey deriveAESKeyFromPassword(String masterPassword)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {

        // 1. Obtém o salt, que é um componente essencial para a segurança da derivação da chave.
        byte[] salt = loadOrGenerateSalt();

        // 2. Configura as especificações para o PBKDF2: senha, salt, iterações e tamanho da chave.
        PBEKeySpec spec = new PBEKeySpec(
                masterPassword.toCharArray(),
                salt,
                ITERATIONS,
                KEY_LENGTH
        );

        // 3. Obtém a instância da "fábrica" que irá executar o algoritmo de derivação.
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        // 4. Executa a derivação para gerar os bytes brutos da chave.
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();

        // 5. Encapsula os bytes gerados em um objeto SecretKey, especificando que é para uso com AES.
        return new SecretKeySpec(keyBytes, "AES");
    }
}