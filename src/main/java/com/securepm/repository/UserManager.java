package com.securepm.repository;

import com.securepm.model.SystemUser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Gerencia a persistência de um único objeto SystemUser em disco.
 * Esta classe é responsável por salvar, carregar e excluir os dados
 * do usuário principal da aplicação.
 */
public class UserManager {

    // Constante que define o nome do arquivo para armazenamento do usuário.
    private static final String USER_FILE = "users.dat";

    /**
     * Serializa e armazena o objeto do usuário no arquivo,
     * sobrescrevendo a versão anterior se uma já existir.
     *
     * @param systemUser O objeto SystemUser que deve ser persistido.
     * @throws IOException Lançada se ocorrer uma falha durante a escrita no arquivo.
     */
    public void saveUser(SystemUser systemUser) throws IOException {
        // O try-with-resources garante o fechamento automático do stream.
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
            oos.writeObject(systemUser);
        }
    }

    /**
     * Tenta carregar o usuário a partir do arquivo de dados.
     * Utiliza Optional para tratar de forma segura a ausência do arquivo.
     *
     * @return Um Optional contendo o SystemUser se encontrado, ou um Optional vazio.
     * @throws IOException Se uma falha de I/O ocorrer durante a leitura.
     * @throws ClassNotFoundException Se a classe do objeto serializado não for encontrada.
     */
    public Optional<SystemUser> getUser() throws IOException, ClassNotFoundException {
        Path path = Paths.get(USER_FILE);

        // Se o arquivo não existe, não há usuário para carregar.
        if (!Files.exists(path)) {
            return Optional.empty();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_FILE))) {
            // Lê o objeto do arquivo, faz o cast e o encapsula em um Optional.
            SystemUser systemUser = (SystemUser) ois.readObject();
            return Optional.of(systemUser);
        }
    }

    /**
     * Remove permanentemente o arquivo de dados do usuário do sistema de arquivos.
     * Esta ação é irreversível.
     *
     * @return Retorna 'true' se o arquivo foi deletado, 'false' se ele não existia.
     * @throws IOException Se ocorrer uma falha ao acessar ou deletar o arquivo.
     */
    public boolean deleteUser() throws IOException {
        Path path = Paths.get(USER_FILE);
        // O método deleteIfExists já verifica a existência antes de tentar apagar.
        return Files.deleteIfExists(path);
    }
}