package com.securepm.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utilitário para a criação de senhas aleatórias que atendem a critérios de complexidade.
 * Garante que as senhas geradas sejam seguras e difíceis de adivinhar.
 */
public class RandomPasswordUtil {

    // Definição dos conjuntos de caracteres permitidos para a senha.
    private static final String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMERIC_CHARS = "0123456789";
    private static final String SYMBOL_CHARS = "!@#$%^&*()-_=+<>?";

    // Agrupamento de todos os caracteres válidos para a geração da maior parte da senha.
    private static final String ALL_CHARS_ALLOWED = UPPERCASE_CHARS + LOWERCASE_CHARS + NUMERIC_CHARS + SYMBOL_CHARS;

    // Instância única de SecureRandom para geração de números aleatórios de alta qualidade, ideal para criptografia.
    private static final SecureRandom randomGenerator = new SecureRandom();

    /**
     * Cria uma senha aleatória e segura com o comprimento especificado.
     * O método assegura que a senha contenha pelo menos um caractere maiúsculo,
     * um minúsculo, um número e um símbolo.
     *
     * @param length O número de caracteres que a senha final deve ter.
     * @return A senha gerada e embaralhada.
     * @throws IllegalArgumentException Lançada se o 'length' for menor que 8,
     * que é o mínimo de segurança estabelecido.
     */
    public static String generate(int length) {
        // Validação para reforçar uma política de senha com comprimento mínimo.
        if (length < 8) {
            throw new IllegalArgumentException("O comprimento mínimo da senha deve ser 8 caracteres.");
        }

        StringBuilder passwordBuilder = new StringBuilder(length);

        // 1. Garante a presença de pelo menos um caractere de cada categoria de complexidade.
        passwordBuilder.append(UPPERCASE_CHARS.charAt(randomGenerator.nextInt(UPPERCASE_CHARS.length())));
        passwordBuilder.append(LOWERCASE_CHARS.charAt(randomGenerator.nextInt(LOWERCASE_CHARS.length())));
        passwordBuilder.append(NUMERIC_CHARS.charAt(randomGenerator.nextInt(NUMERIC_CHARS.length())));
        passwordBuilder.append(SYMBOL_CHARS.charAt(randomGenerator.nextInt(SYMBOL_CHARS.length())));

        // 2. Completa a senha até o tamanho desejado com caracteres aleatórios de todos os conjuntos.
        for (int i = 4; i < length; i++) {
            passwordBuilder.append(ALL_CHARS_ALLOWED.charAt(randomGenerator.nextInt(ALL_CHARS_ALLOWED.length())));
        }

        // 3. Embaralha o resultado final para que a posição dos caracteres garantidos seja aleatória.
        return shuffleString(passwordBuilder.toString());
    }

    /**
     * Randomiza a ordem dos caracteres de uma string para evitar padrões previsíveis.
     * Por exemplo, sem isso, a senha sempre começaria com um caractere de cada tipo na mesma ordem.
     *
     * @param inputString A string a ser embaralhada.
     * @return Uma nova string com os caracteres da original em ordem aleatória.
     */
    private static String shuffleString(String inputString) {
        List<Character> characters = new ArrayList<>();
        for (char c : inputString.toCharArray()) {
            characters.add(c);
        }

        // Utiliza o método de embaralhamento da classe Collections, que implementa uma
        // variação eficiente do algoritmo de Fisher-Yates.
        Collections.shuffle(characters, randomGenerator);

        StringBuilder shuffled = new StringBuilder(characters.size());
        for (char c : characters) {
            shuffled.append(c);
        }

        return shuffled.toString();
    }
}