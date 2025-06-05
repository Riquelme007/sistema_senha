package com.securepm.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Classe utilitária para realizar operações de criptografia e descriptografia
 * utilizando o algoritmo AES no modo CBC com preenchimento PKCS5.
 */
public class EncryptionUtil {

    // Define o algoritmo e o modo de operação: AES com CBC e preenchimento PKCS5.
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    // Tamanho do vetor de inicialização (IV) para AES: 16 bytes (128 bits).
    private static final int IV_LENGTH = 16;

    /**
     * Método que criptografa um texto utilizando AES/CBC/PKCS5Padding.
     *
     * @param plainText O texto em claro a ser criptografado.
     * @param key       A chave secreta para a criptografia.
     * @return Um array de bytes contendo o IV seguido pelos dados criptografados.
     * @throws Exception Caso ocorra falha na criptografia.
     */
    public static byte[] encrypt(String plainText, SecretKey key) throws Exception {
        // Gera um vetor de inicialização (IV) aleatório.
        byte[] iv = new byte[IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Configura o Cipher para criptografar usando o algoritmo definido.
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

        // Executa a criptografia do texto.
        byte[] cipherBytes = cipher.doFinal(plainText.getBytes("UTF-8"));

        // Combina o IV e os dados criptografados em um único array.
        byte[] result = new byte[IV_LENGTH + cipherBytes.length];
        System.arraycopy(iv, 0, result, 0, IV_LENGTH);
        System.arraycopy(cipherBytes, 0, result, IV_LENGTH, cipherBytes.length);

        // Retorna o array combinado.
        return result;
    }

    /**
     * Método que descriptografa dados criptografados utilizando AES/CBC/PKCS5Padding.
     *
     * @param encryptedData O array de bytes contendo o IV seguido pelos dados criptografados.
     * @param key           A chave secreta usada para a descriptografia.
     * @return O texto descriptografado.
     * @throws Exception Caso ocorra falha na descriptografia.
     */
    public static String decrypt(byte[] encryptedData, SecretKey key) throws Exception {
        // Extrai o vetor de inicialização (IV) dos primeiros 16 bytes.
        byte[] iv = Arrays.copyOfRange(encryptedData, 0, IV_LENGTH);

        // Extrai os dados criptografados restantes.
        byte[] cipherBytes = Arrays.copyOfRange(encryptedData, IV_LENGTH, encryptedData.length);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Configura o Cipher para descriptografar usando o algoritmo definido.
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

        // Executa a descriptografia.
        byte[] plainBytes = cipher.doFinal(cipherBytes);

        // Converte os bytes descriptografados de volta para String.
        return new String(plainBytes, "UTF-8");
    }
}
