package com.securepm.model;

import java.io.Serializable;
import java.time.Instant;

/**
 * Representa o usuário administrador da aplicação (master password, 2FA, etc.).
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String passwordHash;      // hash bcrypt da master password
    private String twoFASecret;       // segredo base32 para 2FA
    private Instant createdAt;

    public User(String username, String passwordHash, String twoFASecret) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.twoFASecret = twoFASecret;
        this.createdAt = Instant.now();
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getTwoFASecret() {
        return twoFASecret;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setTwoFASecret(String twoFASecret) {
        this.twoFASecret = twoFASecret;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}