package com.securepm.model;

import java.io.Serializable;
import java.time.Instant;

/**
 * Define a estrutura de um usuário da aplicação, incluindo suas informações
 * de segurança e autenticação. A classe pode ser serializada para persistência de dados.
 */
public class SystemUser implements Serializable {
    // Identificador de versão para a serialização da classe.
    private static final long serialVersionUID = 1L;

    // Identificador único de login para o usuário.
    private String username;

    // Representação criptográfica da senha para armazenamento seguro.
    private String passwordHash;

    // Chave secreta para a geração de códigos de autenticação de dois fatores (2FA).
    private String twoFASecret;

    // Timestamp que marca o momento exato do cadastro do usuário no sistema.
    private Instant createdAt;

    /**
     * Inicializa um novo objeto de usuário, definindo seus dados essenciais
     * e registrando automaticamente a data e hora da criação.
     *
     * @param username      O nome de login do usuário.
     * @param passwordHash  O hash da senha, já processado.
     * @param twoFASecret   A chave secreta para configurar o 2FA.
     */
    public SystemUser(String username, String passwordHash, String twoFASecret) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.twoFASecret = twoFASecret;
        this.createdAt = Instant.now(); // Captura o momento da criação.
    }

    // Recupera o nome de login do usuário.
    public String getUsername() {
        return this.username;
    }

    // Acessa o hash da senha armazenado.
    public String getPasswordHash() {
        return this.passwordHash;
    }

    // Acessa a chave secreta de 2FA.
    public String getTwoFASecret() {
        return this.twoFASecret;
    }

    // Recupera a data de criação do registro do usuário.
    public Instant getCreatedAt() {
        return this.createdAt;
    }

    /**
     * Modifica a chave secreta de autenticação de dois fatores (2FA) do usuário.
     *
     * @param twoFASecret A nova chave secreta a ser definida.
     */
    public void setTwoFASecret(String twoFASecret) {
        this.twoFASecret = twoFASecret;
    }

    /**
     * Retorna uma representação em String do usuário, ocultando informações críticas
     * como o hash da senha e o segredo 2FA por segurança.
     *
     * @return Uma String com informações básicas e seguras do usuário.
     */
    @Override
    public String toString() {
        return "SystemUser{" +
                "username='" + username + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}