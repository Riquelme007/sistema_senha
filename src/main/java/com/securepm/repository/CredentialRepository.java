package com.securepm.repository;

import com.securepm.model.Credential;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por gerenciar o armazenamento persistente das credenciais.
 * Utiliza serialização para salvar e carregar objetos do tipo Credential.
 */
public class CredentialRepository {

    // Caminho para o arquivo onde as credenciais serão armazenadas
    private final String credentialsFile;

    /**
     * Construtor padrão que define o arquivo padrão de armazenamento.
     */
    public CredentialRepository() {
        this.credentialsFile = "credentials.dat";
    }

    /**
     * Construtor que permite especificar um arquivo diferente para armazenamento.
     *
     * @param credentialsFile Caminho do arquivo de credenciais
     */
    public CredentialRepository(String credentialsFile) {
        this.credentialsFile = credentialsFile;
    }

    /**
     * Recupera todas as credenciais armazenadas no arquivo.
     *
     * @return Lista de objetos Credential
     * @throws IOException Se ocorrer erro de leitura
     * @throws ClassNotFoundException Se a classe Credential não for encontrada na desserialização
     */
    public List<Credential> getAll() throws IOException, ClassNotFoundException {
        Path path = Paths.get(credentialsFile);

        // Se o arquivo não existe, retorna lista vazia
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        // Tenta desserializar a lista de credenciais do arquivo
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(credentialsFile))) {
            return (List<Credential>) ois.readObject();
        }
    }

    /**
     * Salva todas as credenciais fornecidas no arquivo, sobrescrevendo o conteúdo atual.
     *
     * @param credentials Lista de credenciais a ser salva
     * @throws IOException Se ocorrer erro de escrita
     */
    public void saveAll(List<Credential> credentials) throws IOException {
        // Serializa a lista de credenciais e grava no arquivo
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(credentialsFile))) {
            oos.writeObject(credentials);
        }
    }

    /**
     * Adiciona uma nova credencial ao armazenamento.
     *
     * @param credential Nova credencial a ser adicionada
     * @throws IOException Se ocorrer erro de leitura/escrita
     * @throws ClassNotFoundException Se a classe Credential não for encontrada na desserialização
     */
    public void add(Credential credential) throws IOException, ClassNotFoundException {
        List<Credential> list = getAll();  // Recupera todas as credenciais existentes
        list.add(credential);              // Adiciona a nova credencial
        saveAll(list);                     // Salva a lista atualizada
    }

    /**
     * Remove uma credencial com base no seu ID.
     *
     * @param credentialId ID da credencial a ser removida
     * @return true se a credencial foi removida com sucesso, false caso contrário
     * @throws IOException Se ocorrer erro de leitura/escrita
     * @throws ClassNotFoundException Se a classe Credential não for encontrada na desserialização
     */
    public boolean removeById(String credentialId) throws IOException, ClassNotFoundException {
        List<Credential> list = getAll();  // Recupera todas as credenciais
        // Remove a credencial que possuir o ID correspondente
        boolean removed = list.removeIf(c -> c.getId().equals(credentialId));

        // Se removeu, salva a lista atualizada
        if (removed) {
            saveAll(list);
        }
        return removed;
    }

    /**
     * Exclui permanentemente o arquivo que armazena todas as credenciais.
     *
     * @return true se o arquivo foi excluído com sucesso, false se não existia
     * @throws IOException Se ocorrer erro ao tentar deletar o arquivo
     */
    public boolean deleteAll() throws IOException {
        Path path = Paths.get(credentialsFile);

        // Verifica se o arquivo existe e tenta deletá-lo
        if (Files.exists(path)) {
            return Files.deleteIfExists(path);
        }
        return false;  // Arquivo não existia
    }
}
