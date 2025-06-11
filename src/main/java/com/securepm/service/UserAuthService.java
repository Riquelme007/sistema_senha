package com.securepm.service;

import com.securepm.model.SystemUser;
import com.securepm.repository.UserManager;
import com.securepm.util.TwoFactorCodeUtils;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.Scanner;

/**
 * Provee a lógica de negócio para autenticação e gerenciamento do usuário principal.
 * Encapsula as regras para registro, login e reinicialização do perfil de usuário.
 */
public class UserAuthService {

    // Ponto de acesso ao repositório responsável pela persistência do usuário.
    private final UserManager userManager = new UserManager();

    /**
     * Verifica de forma rápida se já existe um usuário configurado na aplicação,
     * consultando a existência do arquivo de dados.
     *
     * @return 'true' se um usuário já foi registrado, 'false' caso contrário.
     */
    public boolean isUserRegistered() {
        try {
            // A presença do usuário no Optional indica que ele está registrado.
            return userManager.getUser().isPresent();
        } catch (IOException | ClassNotFoundException e) {
            // Em caso de erro de leitura ou de classe, assume-se que não há usuário válido.
            System.err.println("Alerta ao verificar registro: " + e.getMessage());
            return false;
        }
    }

    /**
     * Executa o fluxo de criação de um novo usuário mestre, incluindo a configuração
     * de senha com hash e a autenticação de dois fatores (2FA).
     *
     * @param scanner Objeto para interagir com o usuário via console.
     */
    public void register(Scanner scanner) {
        try {
            System.out.println("== CRIAÇÃO DE NOVO USUÁRIO ==");
            System.out.print("Defina seu nome de usuário: ");
            String username = scanner.nextLine().trim();

            String password;
            // Loop para garantir que a senha mestra tenha o tamanho mínimo requerido.
            do {
                System.out.print("Crie sua senha mestra (mínimo de 8 caracteres): ");
                password = scanner.nextLine().trim();
                if (password.length() < 8) {
                    System.out.println("❌ Senha muito curta. Por favor, tente novamente.");
                }
            } while (password.length() < 8);

            // Gera um hash seguro da senha usando BCrypt com um 'salt' automático.
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

            // Gera uma chave secreta no formato Base32 para o 2FA.
            String twoFASecret = TwoFactorCodeUtils.generateBase32Secret();

            // Instancia e persiste o novo usuário no sistema.
            SystemUser systemUser = new SystemUser(username, hashedPassword, twoFASecret);
            userManager.saveUser(systemUser);

            // Monta a URI que será usada para gerar o QR Code em um app autenticador.
            String barCodeUri = TwoFactorCodeUtils.getGoogleAuthenticatorBarCode("SecurePM", username, twoFASecret);

            System.out.println("\n✅ Usuário registrado com sucesso!");
            System.out.println("Para ativar o 2FA, use um gerador de QR Code online (como https://www.qr-code-generator.com/) com a URI abaixo.");
            System.out.println("\n--- URI PARA QR CODE ---");
            System.out.println(barCodeUri);
            System.out.println("--- FIM DA URI ---");

        } catch (Exception e) {
            System.err.println("Ocorreu uma falha crítica durante o registro: " + e.getMessage());
        }
    }

    /**
     * Processa a tentativa de login do usuário, validando credenciais em múltiplas etapas.
     * Se o login for bem-sucedido, gera e retorna uma chave de sessão AES para criptografia.
     *
     * @param scanner Objeto para capturar as entradas do usuário.
     * @return Uma SecretKey AES em caso de sucesso, ou 'null' se a autenticação falhar.
     */
    public SecretKey login(Scanner scanner) {
        try {
            System.out.println("\n== AUTENTICAÇÃO DE USUÁRIO ==");

            // Tenta carregar os dados do usuário a partir do arquivo.
            Optional<SystemUser> optUser = userManager.getUser();
            if (optUser.isEmpty()) {
                System.out.println("Nenhum usuário foi encontrado. Por favor, realize o registro primeiro.");
                return null;
            }
            SystemUser systemUser = optUser.get();

            // Validação do nome de usuário.
            System.out.print("Usuário: ");
            String usernameInput = scanner.nextLine().trim();
            if (!usernameInput.equals(systemUser.getUsername())) {
                System.out.println("❌ Nome de usuário inválido.");
                return null;
            }

            // Validação da senha mestra com BCrypt.
            System.out.print("Senha Mestra: ");
            String passwordInput = scanner.nextLine().trim();
            if (!BCrypt.checkpw(passwordInput, systemUser.getPasswordHash())) {
                System.out.println("❌ Senha mestra incorreta.");
                return null;
            }

            // Validação do código de autenticação de dois fatores (2FA).
            System.out.print("Código 2FA (6 dígitos): ");
            String code = scanner.nextLine().trim();
            if (!TwoFactorCodeUtils.verifyTOTPCode(systemUser.getTwoFASecret(), code)) {
                System.out.println("❌ Código 2FA inválido ou expirado.");
                return null;
            }

            System.out.println("\n✅ Autenticação bem-sucedida. Acesso liberado.");

            // Gera uma chave AES de 128 bits para ser usada na sessão atual.
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            return keyGen.generateKey();

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Falha ao carregar os dados do usuário: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("O algoritmo de criptografia AES não está disponível no sistema: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ocorreu um erro durante a validação do 2FA: " + e.getMessage());
        }
        return null; // Retorna nulo se qualquer etapa falhar.
    }

    /**
     * Realiza a exclusão completa dos dados do usuário do sistema.
     * Esta ação é permanente e permitirá que um novo usuário seja registrado.
     */
    public void resetUser() {
        try {
            System.out.println("\n== REINICIALIZAÇÃO DE USUÁRIO ==");
            boolean deleted = userManager.deleteUser();
            if (deleted) {
                System.out.println("✅ Os dados do usuário foram removidos com sucesso.");
            } else {
                System.out.println("ℹ️ Nenhum usuário para remover. O sistema já está limpo.");
            }
        } catch (IOException e) {
            System.err.println("Falha ao tentar remover o arquivo do usuário: " + e.getMessage());
        }
    }
}