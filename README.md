## 🔐 Gerenciador de Senhas Seguras

Este projeto é um sistema seguro de gerenciamento de senhas desenvolvido em Java. Ele permite que usuários salvem, consultem e gerenciem credenciais com segurança, utilizando criptografia AES, autenticação em dois fatores (2FA) e verificação de senhas vazadas.

### 🧰 Funcionalidades

* Registro e autenticação de usuários com senha mestra.
* Criptografia de senhas utilizando AES com salt e chave segura.
* Autenticação em dois fatores (2FA) via QR Code.
* Verificação de senhas contra vazamentos (Breach checker).
* Geração automática de senhas fortes.
* Armazenamento seguro de credenciais locais (`*.dat`).
* Reset total de conta e dados criptografados.

### 📁 Estrutura de Diretórios

```
src/
└── main/
    └── java/
        ├── com.securepm/
        │   ├── App.java                  # Interface de linha de comando
        │   ├── model/                    # Classes modelo (User, Credential)
        │   ├── repository/               # Leitura e escrita em arquivos
        │   ├── service/                  # Lógica de autenticação e credenciais
        │   └── util/                     # Utilitários: criptografia, QRCode, geração de senha, etc.
        └── org.example/
            └── Main.java                # Classe alternativa de execução (não utilizada)
```

### ▶️ Como Executar

**Pré-requisitos:**

* Java 11 ou superior
* Maven

**Passos:**

```bash
# Clonar o repositório
git clone https://github.com/Riquelme007/gerenciador-senhas-seguras.git
cd gerenciador-senhas-seguras

# Compilar o projeto
mvn compile

# Executar
mvn exec:java -Dexec.mainClass="com.securepm.App"
```

### 🔒 Segurança

* As senhas são criptografadas usando `javax.crypto` com uma chave derivada da senha do usuário.
* A autenticação em dois fatores é baseada em TOTP e pode ser escaneada via QR Code com apps como Google Authenticator.
* Os dados são armazenados localmente, em arquivos `.dat` encriptados.

### 📦 Dependências

Confira o arquivo [`pom.xml`](pom.xml) para ver todas as dependências utilizadas no projeto.

### 👨‍💻 Autor

**Juan Riquelme Serafim**
📧 [juan.riquelme.serafim@gmail.com](mailto:juan.riquelme.serafim@gmail.com)
📱 (81) 98501-4408
🎓 Estudante de Análise e Desenvolvimento de Sistemas – 5º Período (UNIT - Universidade Tiradentes)
🏙️ Recife - PE
💼 Desenvolvedor Java com foco em segurança, criptografia e usabilidade
