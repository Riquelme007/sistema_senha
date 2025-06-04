package com.securepm.model;

import java.io.Serializable;
import java.time.Instant;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String passwordHash;
    private String twoFASecret;
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
