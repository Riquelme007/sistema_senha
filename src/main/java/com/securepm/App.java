package com.securepm;

import com.securepm.service.AuthService;
import com.securepm.service.CredentialService;

import javax.crypto.SecretKey;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AuthService authService = new AuthService();
        CredentialService credentialService = new CredentialService();

        if (!authService.isUserRegistered()) {
            System.out.println("Nenhum usuário encontrado. Deseja se registrar? (s/n)");
            String opt = scanner.nextLine().trim().toLowerCase();
            if (opt.equals("s")) {
                authService.register(scanner);
            } else {
                System.out.println("Encerrando o programa...");
                System.exit(0);
            }
        }

        SecretKey aesKey = null;
        while (aesKey == null) {
            System.out.println("\n=== AUTENTICAÇÃO ===");
            System.out.println("1) Fazer login");
            System.out.println("2) Resetar usuário atual");
            System.out.println("3) Sair");
            System.out.print("Escolha: ");
            String opt = scanner.nextLine().trim();

            switch (opt) {
                case "1":
                    aesKey = authService.login(scanner);
                    break;

                case "2":
                    System.out.print("Tem certeza que deseja excluir o usuário e todas as configurações? (s/n): ");
                    String confirma = scanner.nextLine().trim().toLowerCase();
                    if (confirma.equals("s")) {
                        authService.resetUser();
                        System.out.println("Usuário resetado. Deseja registrar novamente? (s/n)");
                        String novaResp = scanner.nextLine().trim().toLowerCase();
                        if (novaResp.equals("s")) {
                            authService.register(scanner);
                        } else {
                            System.out.println("Encerrando o programa...");
                            System.exit(0);
                        }
                    } else {
                        System.out.println("Cancelado. Voltando ao menu de autenticação.");
                    }
                    break;

                case "3":
                    System.out.println("Saindo...");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }

        while (true) {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1) Adicionar credencial");
            System.out.println("2) Listar credenciais");
            System.out.println("3) Remover credencial");
            System.out.println("4) Gerar senha segura");
            System.out.println("5) Sair");
            System.out.print("Escolha: ");

            String opt = scanner.nextLine().trim();

            switch (opt) {
                case "1":
                    credentialService.addCredential(aesKey, scanner);
                    break;
                case "2":
                    credentialService.listCredentials(aesKey);
                    break;
                case "3":
                    credentialService.removeCredential(scanner);
                    break;
                case "4":
                    System.out.print("Tamanho da senha (mínimo 8): ");
                    try {
                        int len = Integer.parseInt(scanner.nextLine().trim());
                        if (len < 8) {
                            System.out.println("Tamanho mínimo é 8.");
                            break;
                        }
                        String generated = com.securepm.util.PasswordGenerator.generate(len);
                        System.out.println("Senha gerada: " + generated);
                    } catch (NumberFormatException e) {
                        System.out.println("Entrada inválida. Por favor, insira um número.");
                    }
                    break;
                case "5":
                    System.out.println("Saindo...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opção inválida. Por favor, escolha uma das opções disponíveis.");
            }
        }
    }
}
