package com.securepm.repository;

import com.securepm.model.AccessCredential;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Atua como uma camada de persistência para objetos AccessCredential.
 * Gerencia operações de leitura, escrita e exclusão de credenciais em um arquivo
 * utilizando a serialização de objetos Java.
 */
public class CredentialManager {

    // Define o nome do arquivo que será usado para a persistência dos dados.
    private final String credentialsFile;

    /**
     * Cria um gerenciador que utiliza o local de armazenamento padrão "credentials.dat".
     */
    public CredentialManager() {
        this.credentialsFile = "credentials.dat";
    }

    /**
     * Cria um gerenciador que aponta para um arquivo de armazenamento específico,
     * permitindo maior flexibilidade na gestão dos dados.
     *
     * @param credentialsFile O caminho completo para o arquivo a ser utilizado.
     */
    public CredentialManager(String credentialsFile) {
        this.credentialsFile = credentialsFile;
    }

    /**
     * Lê e desserializa todas as credenciais do arquivo de armazenamento.
     * Se o arquivo de destino não for encontrado, retorna uma lista vazia.
     *
     * @return Uma lista contendo todas as credenciais salvas.
     * @throws IOException Se um erro de I/O (entrada/saída) acontecer durante a leitura.
     * @throws ClassNotFoundException Se a estrutura da classe no arquivo for incompatível.
     */
    public List<AccessCredential> getAll() throws IOException, ClassNotFoundException {
        Path path = Paths.get(this.credentialsFile);

        if (!Files.exists(path)) {
            return new ArrayList<>(); // Retorna lista nova se não houver arquivo.
        }

        // Usa try-with-resources para garantir que o stream seja fechado.
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.credentialsFile))) {
            // Converte o objeto lido do arquivo para uma lista de credenciais.
            return (List<AccessCredential>) ois.readObject();
        }
    }

    /**
     * Grava uma lista completa de credenciais no arquivo, substituindo qualquer conteúdo anterior.
     * Este método é o núcleo da persistência de dados.
     *
     * @param accessCredentials A lista de credenciais a ser persistida.
     * @throws IOException Se um erro de I/O ocorrer durante a escrita no arquivo.
     */
    public void saveAll(List<AccessCredential> accessCredentials) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.credentialsFile))) {
            oos.writeObject(accessCredentials); // Serializa e grava a lista inteira.
        }
    }

    /**
     * Incorpora uma nova credencial à coleção existente e persiste a alteração no arquivo.
     *
     * @param accessCredential O novo objeto de credencial a ser adicionado.
     * @throws IOException Se houver falha ao ler ou salvar o arquivo.
     * @throws ClassNotFoundException Se a classe desserializada não for encontrada.
     */
    public void add(AccessCredential accessCredential) throws IOException, ClassNotFoundException {
        List<AccessCredential> currentList = getAll();
        currentList.add(accessCredential);
        saveAll(currentList);
    }

    /**
     * Busca e remove uma credencial específica através de seu identificador único.
     *
     * @param credentialId O ID da credencial que deve ser removida.
     * @return Retorna 'true' se um item foi removido, ou 'false' caso contrário.
     * @throws IOException Se houver falha ao ler ou salvar o arquivo.
     * @throws ClassNotFoundException Se a classe desserializada não for encontrada.
     */
    public boolean removeById(String credentialId) throws IOException, ClassNotFoundException {
        List<AccessCredential> currentList = getAll();
        // O método removeIf retorna true se a coleção foi modificada.
        boolean removed = currentList.removeIf(cred -> cred.getId().equals(credentialId));

        if (removed) {
            saveAll(currentList); // Salva a lista apenas se algo foi removido.
        }
        return removed;
    }

    /**
     * Apaga de forma definitiva o arquivo de armazenamento de credenciais do disco.
     * Esta é uma operação destrutiva e irreversível.
     *
     * @return 'true' se o arquivo foi deletado com sucesso, 'false' se ele não existia.
     * @throws IOException Se ocorrer uma falha de I/O durante a exclusão.
     */
    public boolean deleteAll() throws IOException {
        Path path = Paths.get(this.credentialsFile);
        return Files.deleteIfExists(path);
    }
}