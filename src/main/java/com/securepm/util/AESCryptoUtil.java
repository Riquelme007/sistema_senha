package com.securepm.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Fornece métodos estáticos para operações de criptografia simétrica.
 * Utiliza o padrão AES (Advanced Encryption Standard) com modo de operação CBC
 * e preenchimento PKCS5 para garantir confidencialidade e integridade.
 */
public class AESCryptoUtil {

    // Define a transformação criptográfica completa: algoritmo AES, modo de operação CBC e esquema de preenchimento.
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    // Especifica o tamanho em bytes do Vetor de Inicialização (IV), que é de 128 bits para o AES.
    private static final int IV_LENGTH_BYTES = 16;

    /**
     * Criptografa uma string de texto plano. O Vetor de Inicialização (IV) é gerado
     * aleatoriamente a cada chamada e é prefixado ao texto cifrado resultante.
     *
     * @param plainText A string original que será protegida.
     * @param key       A chave secreta (SecretKey) para realizar a criptografia.
     * @return Um array de bytes onde os primeiros 16 bytes são o IV e o restante é o texto cifrado.
     * @throws Exception Se ocorrer qualquer erro durante o processo de criptografia.
     */
    public static byte[] encrypt(String plainText, SecretKey key) throws Exception {
        // 1. Gera um Vetor de Inicialização (IV) criptograficamente seguro.
        byte[] iv = new byte[IV_LENGTH_BYTES];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // 2. Obtém e inicializa a instância do Cipher para o modo de criptografia.
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

        // 3. Realiza a criptografia do texto plano (convertido para bytes UTF-8).
        byte[] cipherBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // 4. Cria um array de resultado para conter o IV e o texto cifrado.
        byte[] result = new byte[IV_LENGTH_BYTES + cipherBytes.length];

        // 5. Copia o IV para o início do array e o texto cifrado logo em seguida.
        System.arraycopy(iv, 0, result, 0, IV_LENGTH_BYTES);
        System.arraycopy(cipherBytes, 0, result, IV_LENGTH_BYTES, cipherBytes.length);

        return result;
    }

    /**
     * Descriptografa um conjunto de bytes que deve conter o IV prefixado ao texto cifrado.
     * Este método extrai o IV e o texto cifrado para reverter a operação de criptografia.
     *
     * @param encryptedData O array de bytes combinado (IV + texto cifrado).
     * @param key           A mesma chave secreta (SecretKey) usada para criptografar.
     * @return A string original descriptografada.
     * @throws Exception Se ocorrer qualquer erro, como chave incorreta ou dados corrompidos.
     */
    public static String decrypt(byte[] encryptedData, SecretKey key) throws Exception {
        // 1. Extrai os primeiros 16 bytes do array, que correspondem ao IV.
        byte[] iv = Arrays.copyOfRange(encryptedData, 0, IV_LENGTH_BYTES);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // 2. Extrai o restante dos dados, que representa o texto efetivamente cifrado.
        byte[] cipherBytes = Arrays.copyOfRange(encryptedData, IV_LENGTH_BYTES, encryptedData.length);

        // 3. Obtém e inicializa a instância do Cipher para o modo de descriptografia.
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

        // 4. Realiza a descriptografia dos dados.
        byte[] plainBytes = cipher.doFinal(cipherBytes);

        // 5. Converte o resultado de volta para uma string no formato UTF-8.
        return new String(plainBytes, StandardCharsets.UTF_8);
    }
}