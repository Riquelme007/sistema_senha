package com.securepm.model;

import java.io.Serializable;
import java.time.Instant;

/**
 * Classe que representa um usuário do sistema.
 * Implementa Serializable para permitir a serialização do objeto.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    // Nome de usuário utilizado para login
    private String username;

    // Hash da senha do usuário para armazenamento seguro
    private String passwordHash;

    // Segredo utilizado para autenticação em dois fatores (2FA)
    private String twoFASecret;

    // Data e hora de criação do usuário
    private Instant createdAt;

    /**
     * Construtor que inicializa o usuário com os dados fornecidos.
     * A data de criação é automaticamente preenchida com o instante atual.
     *
     * @param username Nome de usuário
     * @param passwordHash Hash seguro da senha
     * @param twoFASecret Segredo para autenticação em dois fatores
     */
    public User(String username, String passwordHash, String twoFASecret) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.twoFASecret = twoFASecret;
        this.createdAt = Instant.now(); // Registra o instante de criação do usuário
    }

    // Retorna o nome de usuário
    public String getUsername() {
        return username;
    }

    // Retorna o hash da senha do usuário
    public String getPasswordHash() {
        return passwordHash;
    }

    // Retorna o segredo para autenticação em dois fatores (2FA)
    public String getTwoFASecret() {
        return twoFASecret;
    }

    // Retorna a data/hora de criação do usuário
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Permite atualizar o segredo de autenticação em dois fatores.
     *
     * @param twoFASecret Novo segredo 2FA
     */
    public void setTwoFASecret(String twoFASecret) {
        this.twoFASecret = twoFASecret;
    }

    /**
     * Retorna uma representação textual do usuário.
     * Por questões de segurança, não exibe o hash da senha nem o segredo 2FA.
     */
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
