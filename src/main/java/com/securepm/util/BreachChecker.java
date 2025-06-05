package com.securepm.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

/**
 * Classe utilitária responsável por verificar se uma senha foi comprometida
 * utilizando o serviço Have I Been Pwned (HIBP).
 */
public class BreachChecker {

    // URL base da API do Have I Been Pwned (HIBP) para consulta de senhas comprometidas.
    private static final String HIBP_API = "https://api.pwnedpasswords.com/range/";

    /**
     * Método que retorna a quantidade de vezes que uma senha foi exposta em vazamentos de dados.
     * Utiliza a técnica de k-Anonymity para preservar a privacidade da senha.
     *
     * @param password A senha em texto puro a ser verificada.
     * @return O número de vezes que a senha foi encontrada em vazamentos.
     * @throws Exception Caso ocorra erro na criptografia ou na comunicação com a API.
     */
    public static int getPwnedCount(String password) throws Exception {
        // Cria um hash SHA-1 da senha.
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(password.getBytes("UTF-8"));

        // Converte o hash para uma string hexadecimal.
        StringBuilder sha1Hex = new StringBuilder();
        for (byte b : digest) {
            sha1Hex.append(String.format("%02X", b));
        }

        String fullHash = sha1Hex.toString();

        // Divide o hash: prefixo (5 primeiros caracteres) e sufixo (restante).
        String prefix = fullHash.substring(0, 5);
        String suffix = fullHash.substring(5);

        // Monta a URL para a consulta na API, enviando apenas o prefixo.
        URL url = new URL(HIBP_API + prefix);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // Define o User-Agent para evitar bloqueios por parte do servidor.
        conn.setRequestProperty("User-Agent", "Java-SecurePM-App");

        // Configura tempo limite para conexão e leitura.
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        // Verifica o código de resposta HTTP.
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            // Se a resposta não for OK, lança uma exceção.
            throw new RuntimeException("Falha ao chamar HIBP API: HTTP " + responseCode);
        }

        // Lê a resposta da API linha por linha.
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            // Cada linha contém: sufixo_hash:count
            String[] parts = line.split(":");
            if (parts.length < 2) continue;  // Pula linhas mal formatadas.

            String returnedSuffix = parts[0];
            int count = Integer.parseInt(parts[1]);

            // Compara o sufixo retornado com o sufixo da senha consultada.
            if (returnedSuffix.equalsIgnoreCase(suffix)) {
                // Se for igual, retorna a quantidade de vazamentos.
                return count;
            }
        }

        // Se não encontrar correspondência, retorna 0.
        return 0;
    }
}
