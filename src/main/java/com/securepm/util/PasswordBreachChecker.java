package com.securepm.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Utilitário para interagir com o serviço "Have I Been Pwned" (HIBP).
 * Permite verificar se uma senha já foi exposta em vazamentos de dados conhecidos
 * de forma segura, sem expor a senha completa.
 */
public class PasswordBreachChecker {

    // Endpoint da API Pwned Passwords que utiliza o modelo de k-anonymity.
    private static final String HIBP_API_ENDPOINT = "https://api.pwnedpasswords.com/range/";

    /**
     * Consulta a API 'Have I Been Pwned' para verificar se uma senha foi exposta em vazamentos.
     * A senha em si nunca é enviada para o serviço; apenas os 5 primeiros caracteres de seu
     * hash SHA-1 são usados na consulta, garantindo a privacidade (k-Anonymity).
     *
     * @param password A senha em texto claro que será verificada.
     * @return Um inteiro representando o número de vezes que a senha apareceu em vazamentos.
     * Retorna 0 se a senha não foi encontrada.
     * @throws Exception Lançada se houver falha na comunicação com a API ou no processamento.
     */
    public static int getPwnedCount(String password) throws Exception {
        // 1. Calcula o hash SHA-1 da senha, que é o formato de hash usado pela API HIBP.
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));

        // 2. Converte o hash de array de bytes para uma representação hexadecimal em maiúsculas.
        StringBuilder sha1Hex = new StringBuilder();
        for (byte b : digest) {
            sha1Hex.append(String.format("%02X", b));
        }
        String fullHash = sha1Hex.toString();

        // 3. Divide o hash em um prefixo (primeiros 5 caracteres) e um sufixo (o restante).
        String prefix = fullHash.substring(0, 5);
        String suffix = fullHash.substring(5);

        // 4. Prepara e executa a chamada HTTP GET, enviando apenas o prefixo do hash na URL.
        URL url = new URL(HIBP_API_ENDPOINT + prefix);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Java-SecurePM-Client"); // Boa prática para identificar o cliente.
        conn.setConnectTimeout(5000); // Timeout de 5 segundos para conexão.
        conn.setReadTimeout(5000);    // Timeout de 5 segundos para leitura da resposta.

        // Valida se a requisição foi bem-sucedida.
        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("A chamada à API HIBP falhou. Código de resposta: " + responseCode);
        }

        // 5. Processa a resposta da API, que contém uma lista de sufixos e suas contagens.
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // A resposta vem no formato SUFIXO:CONTAGEM.
                String[] parts = line.split(":");
                if (parts.length != 2) continue; // Ignora linhas mal formatadas.

                String returnedSuffix = parts[0];

                // 6. Compara (ignorando maiúsculas/minúsculas) o sufixo da nossa senha com cada sufixo da lista.
                if (returnedSuffix.equalsIgnoreCase(suffix)) {
                    // Se encontrar, retorna a contagem de vazamentos e encerra o método.
                    return Integer.parseInt(parts[1]);
                }
            }
        }

        // 7. Se o loop terminar sem encontrar o sufixo, a senha é considerada segura (neste contexto).
        return 0;
    }
}