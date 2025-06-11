package com.securepm.service;

import com.securepm.model.AccessCredential;
import com.securepm.repository.CredentialManager;
import com.securepm.util.AESCryptoUtil;
import com.securepm.util.PasswordBreachChecker;
import com.securepm.util.RandomPasswordUtil;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * Orquestra as operações relacionadas ao ciclo de vida das credenciais de serviço.
 * Fornece a lógica de negócio para adicionar, consultar e remover credenciais de forma segura.
 */
public class CredentialAuthService {

    // Instância do gerenciador de persistência para as credenciais.
    private final CredentialManager repository = new CredentialManager();

    // ... (o método addCredential e outros permanecem os mesmos, mas vamos ajustar os blocos catch)

    public void addCredential(SecretKey aesKey, Scanner scanner) {
        try {
            System.out.println("== REGISTRAR NOVA CREDENCIAL ==");

            System.out.print("Informe o nome do serviço (ex: Google, Amazon): ");
            String service = scanner.nextLine().trim();

            System.out.print("Informe o usuário ou e-mail de login: ");
            String user = scanner.nextLine().trim();

            System.out.print("Digite a senha (ou deixe em branco para gerar uma senha forte): ");
            String rawPassword = scanner.nextLine().trim();

            if (rawPassword.isEmpty()) {
                int length;
                do {
                    System.out.print("Defina o tamanho da senha (mínimo 8 caracteres): ");
                    try {
                        length = Integer.parseInt(scanner.nextLine().trim());
                    } catch (NumberFormatException e) {
                        length = 0;
                    }
                } while (length < 8);
                rawPassword = RandomPasswordUtil.generate(length);
                System.out.println("Sua senha segura gerada é: " + rawPassword);
            }

            int pwnedCount = PasswordBreachChecker.getPwnedCount(rawPassword);
            if (pwnedCount > 0) {
                System.out.printf("⚠️ ALERTA: Esta senha foi encontrada em %d vazamentos de dados conhecidos.%n", pwnedCount);
                System.out.print("Você realmente deseja usar esta senha? (s/n): ");
                String response = scanner.nextLine().trim().toLowerCase();
                if (!"s".equals(response)) {
                    System.out.println("Operação cancelada. A credencial não foi salva.");
                    return;
                }
            }

            byte[] encryptedData = AESCryptoUtil.encrypt(rawPassword, aesKey);
            byte[] iv = Arrays.copyOf(encryptedData, 16);

            String id = UUID.randomUUID().toString();
            AccessCredential credential = new AccessCredential(id, service, user, encryptedData, iv);
            repository.add(credential);

            System.out.println("✅ Credencial registrada e protegida com sucesso! O ID é: " + id);

            // --- MUDANÇA AQUI ---
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Erro Crítico: O formato do arquivo de credenciais ('credentials.dat') é incompatível com a versão atual do programa.");
            System.err.println("Causa: A classe " + e.getMessage() + " não foi encontrada. Delete o arquivo 'credentials.dat' e reinicie a aplicação.");
            // Opcional: imprimir o stack trace completo para depuração
            // e.printStackTrace();
        } catch (IOException e) {
            System.err.println("❌ Erro de Leitura/Escrita: Não foi possível acessar o arquivo 'credentials.dat'. Verifique as permissões.");
            // e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Ocorreu um erro inesperado ao adicionar a credencial: " + e.getClass().getSimpleName());
            // Imprime o rastreamento completo do erro, que é muito mais útil!
            e.printStackTrace();
        }
    }

    public void listCredentials(SecretKey aesKey) {
        try {
            File file = new File("credentials.dat");
            if (!file.exists() || file.length() == 0) {
                System.out.println("ℹ️ Nenhum registro de credencial encontrado.");
                return;
            }

            List<AccessCredential> credentials = repository.getAll();
            if (credentials.isEmpty()) {
                System.out.println("ℹ️ O repositório está vazio. Nenhuma credencial para listar.");
                return;
            }

            System.out.println("\n== LISTA DE CREDENCIAIS SALVAS ==");
            for (AccessCredential cred : credentials) {
                String encryptedBase64 = Base64.getEncoder().encodeToString(cred.getEncryptedPassword());
                System.out.println("---------------------------------");
                System.out.println("ID         : " + cred.getId());
                System.out.println("Serviço    : " + cred.getServiceName());
                System.out.println("Usuário    : " + cred.getUsername());
                System.out.println("Senha (Cifrada): " + encryptedBase64);
                System.out.println("Data Criação: " + cred.getCreatedAt());
            }
            System.out.println("---------------------------------");

            // --- MUDANÇA AQUI ---
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Erro Crítico: O formato do arquivo de credenciais ('credentials.dat') é incompatível com a versão atual do programa.");
            System.err.println("Causa: A classe " + e.getMessage() + " não foi encontrada. Delete o arquivo 'credentials.dat' e reinicie a aplicação.");
        } catch (IOException e) {
            System.err.println("❌ Erro de Leitura/Escrita: Não foi possível acessar o arquivo 'credentials.dat'. Verifique as permissões.");
        } catch (Exception e) {
            System.err.println("Ocorreu um erro inesperado ao listar as credenciais: " + e.getClass().getSimpleName());
            e.printStackTrace();
        }
    }

    public void removeCredential(Scanner scanner) {
        try {
            System.out.println("\n== REMOVER CREDENCIAL ==");
            System.out.print("Digite o ID completo da credencial que deseja excluir: ");
            String id = scanner.nextLine().trim();

            if (id.isEmpty()) {
                System.out.println("O ID não pode ser vazio. Operação cancelada.");
                return;
            }

            boolean removed = repository.removeById(id);

            if (removed) {
                System.out.println("✅ Credencial removida com sucesso.");
            } else {
                System.out.println("❌ Nenhuma credencial encontrada com o ID fornecido.");
            }
            // --- MUDANÇA AQUI ---
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Erro Crítico: O formato do arquivo de credenciais ('credentials.dat') é incompatível com a versão atual do programa.");
            System.err.println("Causa: A classe " + e.getMessage() + " não foi encontrada. Delete o arquivo 'credentials.dat' e reinicie a aplicação.");
        } catch (IOException e) {
            System.err.println("❌ Erro de Leitura/Escrita: Não foi possível acessar o arquivo 'credentials.dat'. Verifique as permissões.");
        } catch (Exception e) {
            System.err.println("Ocorreu um erro inesperado ao remover a credencial: " + e.getClass().getSimpleName());
            e.printStackTrace();
        }
    }
}