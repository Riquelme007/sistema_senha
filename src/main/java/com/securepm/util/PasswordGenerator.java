package com.securepm.util;

import java.security.SecureRandom;

/**
 * Classe utilitária para geração de senhas seguras e aleatórias.
 */
public class PasswordGenerator {

    // Letras maiúsculas permitidas na senha.
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    // Letras minúsculas permitidas na senha.
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";

    // Dígitos numéricos permitidos na senha.
    private static final String DIGITS = "0123456789";

    // Símbolos especiais permitidos na senha.
    private static final String SYMBOLS = "!@#$%^&*()-_=+<>?";

    // Conjunto de todos os caracteres permitidos.
    private static final String ALL_ALLOWED = UPPER + LOWER + DIGITS + SYMBOLS;

    // Gerador de números aleatórios seguro para uso criptográfico.
    private static final SecureRandom random = new SecureRandom();

    /**
     * Gera uma senha aleatória de tamanho especificado.
     *
     * @param length Tamanho desejado para a senha.
     * @return Uma senha aleatória contendo letras, números e símbolos.
     * @throws IllegalArgumentException Se o tamanho for menor que 8.
     */
    public static String generate(int length) {
        // Garante que a senha tenha um tamanho mínimo para segurança.
        if (length < 8) {
            throw new IllegalArgumentException("O tamanho mínimo deve ser 8");
        }

        StringBuilder password = new StringBuilder(length);

        // Garante que a senha tenha pelo menos um caractere de cada tipo.
        password.append(UPPER.charAt(random.nextInt(UPPER.length())));
        password.append(LOWER.charAt(random.nextInt(LOWER.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SYMBOLS.charAt(random.nextInt(SYMBOLS.length())));

        // Preenche o restante da senha com caracteres aleatórios do conjunto total permitido.
        for (int i = 4; i < length; i++) {
            password.append(ALL_ALLOWED.charAt(random.nextInt(ALL_ALLOWED.length())));
        }

        // Embaralha a senha gerada para evitar padrões previsíveis.
        return shuffleString(password.toString());
    }

    /**
     * Embaralha uma string utilizando o algoritmo de Fisher-Yates.
     *
     * @param input String a ser embaralhada.
     * @return String embaralhada.
     */
    private static String shuffleString(String input) {
        char[] arr = input.toCharArray();

        // Embaralhamento Fisher-Yates.
        for (int i = arr.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }

        return new String(arr);
    }
}
