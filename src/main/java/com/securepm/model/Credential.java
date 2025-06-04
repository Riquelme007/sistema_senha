package com.securepm.model;

import java.io.Serializable;
import java.time.Instant;

public class Credential implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String serviceName;
    private String username;
    private byte[] encryptedPassword;
    private byte[] iv;
    private Instant createdAt;

    public Credential(
            String id,
            String serviceName,
            String username,
            byte[] encryptedPassword,
            byte[] iv
    ) {
        this.id = id;
        this.serviceName = serviceName;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.iv = iv;
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getUsername() {
        return username;
    }

    public byte[] getEncryptedPassword() {
        return encryptedPassword;
    }

    public byte[] getIv() {
        return iv;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Credential{" +
                "id='" + id + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", username='" + username + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
