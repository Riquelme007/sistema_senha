package com.securepm.service;

// Importação das classes necessárias
import com.securepm.model.Credential;
import com.securepm.repository.CredentialRepository;

import java.io.File;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class CredentialService {
    // Instância do repositório para manipular as credenciais
    private final CredentialRepository repository;

    // Construtor inicializando o repositório
    public CredentialService() {
        this.repository = new CredentialRepository();
    }

    /**
     * Método para adicionar uma nova credencial
     */
    public void addCredential(javax.crypto.SecretKey aesKey, Scanner scanner) {
        try {
            System.out.println("** ADICIONAR NOVA CREDENCIAL **");
            System.out.print("Nome do serviço: ");
            String service = scanner.nextLine().trim();

            System.out.print("Nome de usuário/login para este serviço: ");
            String user = scanner.nextLine().trim();

            System.out.print("Senha (enter para gerar automaticamente): ");
            String rawPassword = scanner.nextLine().trim();

            // Se a senha não for informada, gera automaticamente
            if (rawPassword.isEmpty()) {
                int len;
                do {
                    System.out.print("Digite o tamanho da senha a ser gerada (mín 8): ");
                    len = Integer.parseInt(scanner.nextLine().trim());
                } while (len < 8);
                rawPassword = com.securepm.util.PasswordGenerator.generate(len);
                System.out.println("Senha gerada: " + rawPassword);
            }

            // Verifica se a senha foi comprometida em vazamentos de dados
            int pwnedCount = com.securepm.util.BreachChecker.getPwnedCount(rawPassword);
            if (pwnedCount > 0) {
                System.out.printf("Atenção! Essa senha apareceu em vazamentos %d vezes.%n", pwnedCount);
                System.out.print("Deseja continuar e armazenar mesmo assim? (s/n): ");
                String resp = scanner.nextLine().trim().toLowerCase();
                if (!resp.equals("s")) {
                    System.out.println("Credencial não armazenada.");
                    return;
                }
            }

            // Realiza a criptografia da senha com AES
            byte[] combinedIvAndCiphertext = com.securepm.util.EncryptionUtil.encrypt(rawPassword, aesKey);
            // Extrai o vetor de inicialização (IV) dos primeiros 16 bytes
            byte[] iv = java.util.Arrays.copyOf(combinedIvAndCiphertext, 16);
            // Senha criptografada completa
            byte[] encryptedPwd = combinedIvAndCiphertext;

            // Gera um ID único para a credencial
            String id = UUID.randomUUID().toString();
            // Cria o objeto Credential com os dados
            Credential cred = new Credential(id, service, user, encryptedPwd, iv);

            // Salva a credencial no repositório
            repository.add(cred);
            System.out.println("Credencial armazenada com sucesso! ID=" + id);
        } catch (Exception e) {
            System.err.println("Erro ao adicionar credencial: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método para listar todas as credenciais armazenadas
     */
    public void listCredentials(javax.crypto.SecretKey aesKey) {
        try {
            // Verifica se o arquivo de credenciais existe e não está vazio
            File f = new File("credentials.dat");
            if (!f.exists() || f.length() == 0) {
                System.out.println("Nenhuma credencial armazenada.");
                return;
            }

            // Recupera todas as credenciais do repositório
            List<Credential> all = repository.getAll();
            if (all.isEmpty()) {
                System.out.println("Nenhuma credencial armazenada.");
                return;
            }

            System.out.println("** LISTA DE CREDENCIAIS **");
            // Exibe cada credencial
            for (Credential c : all) {
                String encryptedBase64 = Base64.getEncoder().encodeToString(c.getEncryptedPassword());
                System.out.println("ID: " + c.getId());
                System.out.println("Serviço: " + c.getServiceName());
                System.out.println("Usuário: " + c.getUsername());
                System.out.println("Senha (cifrada Base64): " + encryptedBase64);
                System.out.println("Criado em: " + c.getCreatedAt());
                System.out.println("------------------------------");
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar credenciais: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método para remover uma credencial específica pelo ID
     */
    public void removeCredential(Scanner scanner) {
        try {
            System.out.println("** REMOVER CREDENCIAL **");
            System.out.print("ID da credencial a remover: ");
            String id = scanner.nextLine().trim();

            // Remove a credencial pelo ID
            boolean removed = repository.removeById(id);
            if (removed) {
                System.out.println("Credencial removida com sucesso.");
            } else {
                System.out.println("ID não encontrado.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao remover credencial: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
