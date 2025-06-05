package com.securepm.service;

import com.securepm.model.User;
import com.securepm.repository.UserRepository;
import com.securepm.util.TwoFAUtil;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.Scanner;

/**
 * Classe responsável pela lógica de autenticação do sistema.
 * Inclui funcionalidades de registro, login e reset de usuário.
 */
public class AuthService {

    // Repositório de usuário para persistência dos dados
    private final UserRepository userRepository = new UserRepository();

    /**
     * Verifica se existe um usuário registrado no sistema.
     *
     * @return true se um usuário está registrado, false caso contrário
     */
    public boolean isUserRegistered() {
        try {
            return userRepository.getUser().isPresent();
        } catch (IOException | ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Realiza o processo de registro de um novo usuário.
     * Solicita nome, senha e gera chave secreta para autenticação em duas etapas (2FA).
     *
     * @param scanner Scanner para leitura da entrada do usuário
     */
    public void register(Scanner scanner) {
        try {
            System.out.print("Escolha um nome de usuário: ");
            String username = scanner.nextLine().trim();

            // Validação de senha mínima de 8 caracteres
            String password;
            while (true) {
                System.out.print("Crie a master password (mín 8 chars): ");
                password = scanner.nextLine().trim();
                if (password.length() < 8) {
                    System.out.println("Senha muito curta. Tente novamente.");
                } else {
                    break;
                }
            }

            // Hash da senha usando BCrypt
            String hashed = BCrypt.hashpw(password, BCrypt.gensalt(12));

            // Geração de chave secreta para 2FA
            String twoFASecret = TwoFAUtil.generateBase32Secret();

            // Criação e persistência do usuário
            User user = new User(username, hashed, twoFASecret);
            userRepository.saveUser(user);

            // Geração de URI para QR Code do Google Authenticator
            String barCodeUri = TwoFAUtil.getGoogleAuthenticatorBarCode("SecurePM", username, twoFASecret);

            System.out.println("\nUse esse site para conseguir ver o qrcode -> https://www.qr-code-generator.com/");
            System.out.println("\nEscaneie este URI no seu app de autenticação:");
            System.out.println(barCodeUri);
        } catch (Exception e) {
            System.err.println("Falha no registro de usuário: " + e.getMessage());
        }
    }

    /**
     * Realiza o processo de login do usuário.
     * Verifica username, senha e autenticação em duas etapas.
     * Caso bem-sucedido, gera uma chave AES para criptografia.
     *
     * @param scanner Scanner para leitura da entrada do usuário
     * @return SecretKey Chave AES gerada, ou null se falhar
     */
    public SecretKey login(Scanner scanner) {
        try {
            System.out.println("== LOGIN ==");

            // Recupera o usuário persistido
            Optional<User> optUser = userRepository.getUser();
            if (optUser.isEmpty()) {
                System.out.println("Nenhum usuário cadastrado. Registre-se primeiro.");
                return null;
            }
            User user = optUser.get();

            // Validação do nome de usuário
            System.out.print("Nome de usuário: ");
            String usernameInput = scanner.nextLine().trim();
            if (!usernameInput.equals(user.getUsername())) {
                System.out.println("Usuário não encontrado.");
                return null;
            }

            // Validação da senha
            System.out.print("Master password: ");
            String passwordInput = scanner.nextLine().trim();
            if (!BCrypt.checkpw(passwordInput, user.getPasswordHash())) {
                System.out.println("Senha incorreta.");
                return null;
            }

            // Validação do código 2FA
            System.out.print("Código 2FA (6 dígitos): ");
            String code = scanner.nextLine().trim();

            boolean valid2FA;
            try {
                valid2FA = TwoFAUtil.verifyTOTPCode(user.getTwoFASecret(), code);
            } catch (Exception e) {
                System.err.println("Erro ao validar código 2FA: " + e.getMessage());
                return null;
            }

            if (!valid2FA) {
                System.out.println("Código 2FA inválido.");
                return null;
            }

            System.out.println("Login bem-sucedido!");

            // Geração da chave AES para operações criptográficas
            try {
                KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                keyGen.init(128);
                SecretKey aesKey = keyGen.generateKey();
                return aesKey;
            } catch (NoSuchAlgorithmException e) {
                System.err.println("Erro ao gerar chave AES: " + e.getMessage());
                return null;
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar usuário: " + e.getMessage());
        }
        return null;
    }

    /**
     * Reseta o usuário do sistema, excluindo o arquivo de persistência.
     */
    public void resetUser() {
        try {
            boolean deleted = userRepository.deleteUser();
            if (deleted) {
                System.out.println("Usuário deletado com sucesso.");
            } else {
                System.out.println("Nenhum usuário encontrado para deletar.");
            }
        } catch (IOException e) {
            System.err.println("Erro ao deletar usuário: " + e.getMessage());
        }
    }
}
