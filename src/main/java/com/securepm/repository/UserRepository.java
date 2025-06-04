package com.securepm.repository;

import com.securepm.model.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class UserRepository {
    private static final String USER_FILE = "users.dat";

    public void saveUser(User user) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
            oos.writeObject(user);
        }
    }

    public Optional<User> getUser() throws IOException, ClassNotFoundException {
        Path path = Paths.get(USER_FILE);
        if (!Files.exists(path)) {
            return Optional.empty();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_FILE))) {
            User user = (User) ois.readObject();
            return Optional.of(user);
        }
    }

    public boolean deleteUser() throws IOException {
        Path path = Paths.get(USER_FILE);
        if (Files.exists(path)) {
            return Files.deleteIfExists(path);
        }
        return false;
    }
}
