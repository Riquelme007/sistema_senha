package com.securepm.repository;

import com.securepm.model.Credential;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CredentialRepository {
    private final String credentialsFile;

    public CredentialRepository() {
        this.credentialsFile = "credentials.dat";
    }

    public CredentialRepository(String credentialsFile) {
        this.credentialsFile = credentialsFile;
    }

    public List<Credential> getAll() throws IOException, ClassNotFoundException {
        Path path = Paths.get(credentialsFile);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(credentialsFile))) {
            return (List<Credential>) ois.readObject();
        }
    }

    public void saveAll(List<Credential> credentials) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(credentialsFile))) {
            oos.writeObject(credentials);
        }
    }

    public void add(Credential credential) throws IOException, ClassNotFoundException {
        List<Credential> list = getAll();
        list.add(credential);
        saveAll(list);
    }

    public boolean removeById(String credentialId) throws IOException, ClassNotFoundException {
        List<Credential> list = getAll();
        boolean removed = list.removeIf(c -> c.getId().equals(credentialId));
        if (removed) {
            saveAll(list);
        }
        return removed;
    }

    public boolean deleteAll() throws IOException {
        Path path = Paths.get(credentialsFile);
        if (Files.exists(path)) {
            return Files.deleteIfExists(path);
        }
        return false;
    }
}
