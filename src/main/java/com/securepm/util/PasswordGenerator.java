package com.securepm.util;

import java.security.SecureRandom;

/**
 * Utilitário para gerar senhas aleatórias fortes.
 */
public class PasswordGenerator {
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_=+<>?";

    private static final String ALL_ALLOWED = UPPER + LOWER + DIGITS + SYMBOLS;
    private static final SecureRandom random = new SecureRandom();

    /**
     * Gera uma senha aleatória contendo ao menos um caractere de cada tipo.
     * @param length comprimento desejado (mínimo 8 recomendado)
     * @return senha gerada
     */
    public static String generate(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("O tamanho mínimo deve ser 8");
        }

        StringBuilder password = new StringBuilder(length);

        // Garante ao menos um de cada categoria
        password.append(UPPER.charAt(random.nextInt(UPPER.length())));
        password.append(LOWER.charAt(random.nextInt(LOWER.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SYMBOLS.charAt(random.nextInt(SYMBOLS.length())));

        // Preenche o restante aleatoriamente
        for (int i = 4; i < length; i++) {
            password.append(ALL_ALLOWED.charAt(random.nextInt(ALL_ALLOWED.length())));
        }

        // Embaralha os caracteres para não ficarem em ordem previsível
        return shuffleString(password.toString());
    }

    private static String shuffleString(String input) {
        char[] arr = input.toCharArray();
        for (int i = arr.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            // troca arr[i] com arr[j]
            char tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }
        return new String(arr);
    }
}
