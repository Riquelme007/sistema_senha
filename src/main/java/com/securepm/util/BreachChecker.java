package com.securepm.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

public class BreachChecker {

    private static final String HIBP_API = "https://api.pwnedpasswords.com/range/";

    public static int getPwnedCount(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(password.getBytes("UTF-8"));
        StringBuilder sha1Hex = new StringBuilder();
        for (byte b : digest) {
            sha1Hex.append(String.format("%02X", b));
        }
        String fullHash = sha1Hex.toString();
        String prefix = fullHash.substring(0, 5);
        String suffix = fullHash.substring(5);

        URL url = new URL(HIBP_API + prefix);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Java-SecurePM-App");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("Falha ao chamar HIBP API: HTTP " + responseCode);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(":");
            if (parts.length < 2) continue;
            String returnedSuffix = parts[0];
            int count = Integer.parseInt(parts[1]);
            if (returnedSuffix.equalsIgnoreCase(suffix)) {
                return count;
            }
        }
        return 0;
    }
}
