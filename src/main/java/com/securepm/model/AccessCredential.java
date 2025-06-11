package com.securepm.model;

import java.io.Serializable;
import java.time.Instant;

/**
 * Modela os dados de uma credencial de acesso, preparada para ser
 * persistida ou transmitida de forma segura.
 * A implementação de Serializable é necessária para converter o estado do objeto em uma sequência de bytes.
 */
public class AccessCredential implements Serializable {
    // Controle de versão para a serialização da classe.
    private static final long serialVersionUID = 1L;

    // Identificador exclusivo para esta credencial.
    private String id;

    // O nome do serviço ou plataforma ao qual a credencial se refere (ex: "Google", "GitHub").
    private String serviceName;

    // O login ou nome de usuário utilizado para a autenticação no serviço.
    private String username;

    // A senha após a aplicação do algoritmo de criptografia, armazenada em bytes.
    private byte[] encryptedPassword;

    // Vetor de inicialização (IV), um bloco de bits aleatório necessário para a decriptografia.
    private byte[] iv;

    // Registro de data e hora da criação desta entrada de credencial.
    private Instant createdAt;

    /**
     * Constrói uma nova instância de AccessCredential.
     * A data de criação é definida automaticamente para o momento da instanciação.
     *
     * @param id Identificador único.
     * @param serviceName Nome do serviço.
     * @param username Nome de usuário.
     * @param encryptedPassword Senha já criptografada.
     * @param iv Vetor de inicialização usado na criptografia.
     */
    public AccessCredential(
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
        this.createdAt = Instant.now(); // Define o timestamp de criação.
    }

    // Obtém o ID da credencial.
    public String getId() {
        return id;
    }

    // Obtém o nome do serviço associado.
    public String getServiceName() {
        return serviceName;
    }

    // Obtém o nome de usuário.
    public String getUsername() {
        return username;
    }

    // Obtém o array de bytes da senha criptografada.
    public byte[] getEncryptedPassword() {
        return encryptedPassword;
    }

    // Obtém o vetor de inicialização (IV).
    public byte[] getIv() {
        return iv;
    }

    // Obtém o momento exato em que a credencial foi criada.
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Gera uma representação textual do objeto AccessCredential.
     * Por razões de segurança, os dados sensíveis (senha e IV) são omitidos.
     *
     * @return Uma String formatada com os detalhes não-sensíveis da credencial.
     */
    @Override
    public String toString() {
        return "AccessCredential{" +
                "id='" + id + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", username='" + username + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}