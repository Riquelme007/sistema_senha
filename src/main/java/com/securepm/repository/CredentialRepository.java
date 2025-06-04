package com.securepm.repository;

import com.securepm.model.Credential;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositório simples que armazena e carrega lista de Credential
 * como objeto serializado. Persiste em um arquivo definido no construtor.
 */
public class CredentialRepository {
    private final String credentialsFile;

    /**
     * Construtor padrão: usa o arquivo "credentials.dat".
     */
    public CredentialRepository() {
        this.credentialsFile = "credentials.dat";
    }

    /**
     * Construtor alternativo: recebe o nome do arquivo (ex.: "credentials_alice.dat").
     */
    public CredentialRepository(String credentialsFile) {
        this.credentialsFile = credentialsFile;
    }

    /**
     * Retorna todas as credenciais salvas no arquivo.
     * Se o arquivo não existir, retorna lista vazia.
     */
    public List<Credential> getAll() throws IOException, ClassNotFoundException {
        Path path = Paths.get(credentialsFile);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(credentialsFile))) {
            //noinspection unchecked
            return (List<Credential>) ois.readObject();
        }
    }

    /**
     * Salva lista completa de credenciais (sobrescreve o arquivo).
     * @param credentials lista de Credential
     */
    public void saveAll(List<Credential> credentials) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(credentialsFile))) {
            oos.writeObject(credentials);
        }
    }

    /**
     * Adiciona uma credencial à lista existente e persiste.
     * @param credential objeto Credential
     */
    public void add(Credential credential) throws IOException, ClassNotFoundException {
        List<Credential> list = getAll();
        list.add(credential);
        saveAll(list);
    }

    /**
     * Remove credencial por ID e persiste.
     * @param credentialId id da credencial a remover
     * @return true se removido, false se não encontrado
     */
    public boolean removeById(String credentialId) throws IOException, ClassNotFoundException {
        List<Credential> list = getAll();
        boolean removed = list.removeIf(c -> c.getId().equals(credentialId));
        if (removed) {
            saveAll(list);
        }
        return removed;
    }

    /**
     * Remove todo o arquivo de credenciais (caso queira “resetar” tudo).
     */
    public boolean deleteAll() throws IOException {
        Path path = Paths.get(credentialsFile);
        if (Files.exists(path)) {
            return Files.deleteIfExists(path);
        }
        return false;
    }
}
