package com.securepm.repository;

import com.securepm.model.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Classe responsável pelo gerenciamento persistente do objeto User.
 * Utiliza serialização para armazenar, recuperar e excluir o usuário.
 */
public class UserRepository {

    // Caminho fixo para o arquivo onde o usuário será armazenado
    private static final String USER_FILE = "users.dat";

    /**
     * Salva o usuário no arquivo, sobrescrevendo qualquer dado anterior.
     *
     * @param user Objeto User a ser salvo
     * @throws IOException Se ocorrer erro de escrita no arquivo
     */
    public void saveUser(User user) throws IOException {
        // Serializa o objeto User e grava no arquivo
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
            oos.writeObject(user);
        }
    }

    /**
     * Recupera o usuário armazenado no arquivo, caso exista.
     *
     * @return Optional contendo o User, ou vazio se não existir
     * @throws IOException Se ocorrer erro de leitura no arquivo
     * @throws ClassNotFoundException Se a classe User não for encontrada na desserialização
     */
    public Optional<User> getUser() throws IOException, ClassNotFoundException {
        Path path = Paths.get(USER_FILE);

        // Verifica se o arquivo existe; se não, retorna Optional vazio
        if (!Files.exists(path)) {
            return Optional.empty();
        }

        // Desserializa o objeto User do arquivo
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_FILE))) {
            User user = (User) ois.readObject();
            return Optional.of(user);
        }
    }

    /**
     * Exclui o arquivo que armazena o usuário.
     *
     * @return true se o arquivo foi excluído com sucesso, false se o arquivo não existia
     * @throws IOException Se ocorrer erro ao tentar deletar o arquivo
     */
    public boolean deleteUser() throws IOException {
        Path path = Paths.get(USER_FILE);

        // Verifica se o arquivo existe e tenta deletá-lo
        if (Files.exists(path)) {
            return Files.deleteIfExists(path);
        }
        return false;  // Arquivo não existia
    }
}
