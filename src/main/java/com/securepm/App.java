package com.securepm;

// Importa os serviços que contêm a lógica de negócio da aplicação.
import com.securepm.service.CredentialAuthService;
import com.securepm.service.UserAuthService;
import com.securepm.util.RandomPasswordUtil; // Importa o utilitário de geração de senha.

import javax.crypto.SecretKey;
import java.util.Scanner;

/**
 * Ponto de entrada e orquestrador principal da aplicação SecurePM.
 * Esta classe gerencia o fluxo de interação com o usuário, desde o registro
 * e autenticação até a gestão das credenciais.
 */
public class App {
    public static void main(String[] args) {
        // Inicializa o Scanner para ler as entradas do console e os serviços da aplicação.
        Scanner scanner = new Scanner(System.in);
        UserAuthService authService = new UserAuthService();
        CredentialAuthService credentialAuthService = new CredentialAuthService();

        System.out.println("--- BEM-VINDO AO SECURE PASSWORD MANAGER (SecurePM) ---");

        // --- ETAPA 1: VERIFICAÇÃO DE REGISTRO ---
        // Bloco que verifica se o aplicativo já foi configurado com um usuário mestre.
        if (!authService.isUserRegistered()) {
            System.out.println("\nNenhum usuário mestre foi encontrado no sistema.");
            System.out.print("Deseja criar um novo perfil agora? (Digite 's' para sim ou 'n' para sair): ");
            String opt = scanner.nextLine().trim().toLowerCase();
            if ("s".equals(opt)) {
                authService.register(scanner);
            } else {
                System.out.println("Encerrando o SecurePM... Até logo!");
                System.exit(0);
            }
        }

        // --- ETAPA 2: LOOP DE AUTENTICAÇÃO ---
        // Esta seção persiste até que o usuário faça o login com sucesso.
        // A chave 'aesKey' é nula até que o login seja validado.
        SecretKey aesKey = null;
        while (aesKey == null) {
            System.out.println("\n=== PORTAL DE AUTENTICAÇÃO ===");
            System.out.println("1) Fazer Login");
            System.out.println("2) Reiniciar Perfil (apaga todos os dados)");
            System.out.println("3) Sair do Programa");
            System.out.print("Escolha uma opção (1-3): ");
            String opt = scanner.nextLine().trim();

            switch (opt) {
                case "1":
                    // Tenta realizar o login e obter a chave de sessão AES.
                    aesKey = authService.login(scanner);
                    if (aesKey != null) {
                        System.out.println("\n✅ Acesso concedido! Bem-vindo(a) ao seu cofre.");
                    }
                    break;

                case "2":
                    System.out.print("\n⚠️ ATENÇÃO: Esta ação é irreversível e apagará seu perfil e todas as senhas salvas. Deseja continuar? (s/n): ");
                    String confirma = scanner.nextLine().trim().toLowerCase();
                    if ("s".equals(confirma)) {
                        authService.resetUser();
                        System.out.println("\nPerfil removido. Para usar o SecurePM, é preciso criar um novo perfil.");
                        System.out.print("Deseja registrar-se novamente agora? (s/n): ");
                        String novaResp = scanner.nextLine().trim().toLowerCase();
                        if ("s".equals(novaResp)) {
                            authService.register(scanner);
                        } else {
                            System.out.println("Encerrando o SecurePM... Até logo!");
                            System.exit(0);
                        }
                    } else {
                        System.out.println("Operação cancelada. Voltando ao menu de autenticação.");
                    }
                    break;

                case "3":
                    System.out.println("Saindo do SecurePM... 👋");
                    System.exit(0);
                    break;

                default:
                    System.out.println("❌ Opção inválida. Por favor, digite 1, 2 ou 3.");
            }
        }

        // --- ETAPA 3: LOOP DO MENU PRINCIPAL ---
        // Acessível apenas após a autenticação bem-sucedida.
        while (true) {
            System.out.println("\n=== MENU PRINCIPAL - COFRE DE SENHAS ===");
            System.out.println("1) Adicionar nova credencial");
            System.out.println("2) Listar todas as credenciais");
            System.out.println("3) Remover uma credencial");
            System.out.println("4) Gerador de Senha Segura");
            System.out.println("5) Sair");
            System.out.print("Digite a opção desejada (1-5): ");

            String opt = scanner.nextLine().trim();

            switch (opt) {
                case "1":
                    credentialAuthService.addCredential(aesKey, scanner);
                    break;
                case "2":
                    credentialAuthService.listCredentials(aesKey);
                    break;
                case "3":
                    credentialAuthService.removeCredential(scanner);
                    break;
                case "4":
                    System.out.print("Digite o comprimento para a nova senha (mínimo recomendado: 12): ");
                    try {
                        int len = Integer.parseInt(scanner.nextLine().trim());
                        if (len < 8) {
                            // Mantém a validação mínima do utilitário.
                            System.out.println("❌ O tamanho mínimo permitido é 8, mas 12 ou mais é recomendado.");
                            break;
                        }
                        // Chama o método estático diretamente da classe de utilitário.
                        String generatedPassword = RandomPasswordUtil.generate(len);
                        System.out.println("✨ Senha segura gerada: " + generatedPassword);
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Entrada inválida. Por favor, digite um número para o comprimento.");
                    }
                    break;
                case "5":
                    System.out.println("Encerrando sessão... Obrigado por usar o SecurePM! 👋");
                    System.exit(0);
                    break;
                default:
                    System.out.println("❌ Opção inválida. Por favor, escolha um número de 1 a 5.");
            }
        }
    }
}