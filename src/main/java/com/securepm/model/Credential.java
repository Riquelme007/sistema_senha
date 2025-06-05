package com.securepm.model;

import java.io.Serializable;
import java.time.Instant;

/**
 * Classe que representa uma credencial segura.
 * Implementa Serializable para permitir que objetos sejam serializados.
 */
public class Credential implements Serializable {
    private static final long serialVersionUID = 1L;

    // Identificador único da credencial
    private String id;

    // Nome do serviço ao qual a credencial pertence (ex: "Gmail", "Facebook")
    private String serviceName;

    // Nome de usuário associado à credencial
    private String username;

    // Senha criptografada armazenada como array de bytes
    private byte[] encryptedPassword;

    // Vetor de inicialização (IV) utilizado no processo de criptografia
    private byte[] iv;

    // Data e hora em que a credencial foi criada
    private Instant createdAt;

    /**
     * Construtor que inicializa a credencial com os dados fornecidos.
     * O campo createdAt é automaticamente preenchido com o instante atual.
     */
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
        this.createdAt = Instant.now(); // Marca a data/hora de criação
    }

    // Retorna o identificador da credencial
    public String getId() {
        return id;
    }

    // Retorna o nome do serviço associado à credencial
    public String getServiceName() {
        return serviceName;
    }

    // Retorna o nome de usuário associado à credencial
    public String getUsername() {
        return username;
    }

    // Retorna a senha criptografada
    public byte[] getEncryptedPassword() {
        return encryptedPassword;
    }

    // Retorna o vetor de inicialização utilizado na criptografia
    public byte[] getIv() {
        return iv;
    }

    // Retorna a data/hora de criação da credencial
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Retorna uma representação textual da credencial.
     * Não inclui a senha criptografada nem o IV por segurança.
     */
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
