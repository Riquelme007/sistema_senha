# SecurePM

Gerenciador de senhas seguro em Java com autenticação 2FA (Google Authenticator) e criptografia AES.

## 📌 O que é?

SecurePM é um gerenciador de senhas local que permite:

✅ Cadastro de um usuário master com autenticação em dois fatores (2FA).  
✅ Login seguro com TOTP (Google Authenticator).  
✅ Armazenamento de credenciais com criptografia AES-CBC/PKCS5Padding usando chave derivada via PBKDF2.  
✅ Listagem e remoção de credenciais armazenadas.  
✅ Reset completo do usuário e dados.

---

## 🚀 Como começar

### 1. Clonar o repositório:

```bash
git clone https://github.com/seu-usuario/securepm.git
cd securepm
2. Abrir no IntelliJ IDEA
File → Open..., selecione a pasta raiz securepm e clique em OK.

Configure o Project SDK para JDK 11 ou superior.

3. Adicionar dependências
O projeto depende de org.mindrot:jbcrypt.

Se não houver import automático:

Baixe o JAR do jbcrypt.

Coloque em uma pasta lib/.

Vá em File → Project Structure → Libraries → clique em + → selecione o JAR.

4. Compilar o projeto
bash
Copiar
Editar
# No IntelliJ:
Build → Build Project
ou via terminal:

bash
Copiar
Editar
javac -d out src/main/java/com/securepm/**/*.java
5. Executar a aplicação
No IntelliJ:

Expanda src/main/java/com/securepm.

Clique com o botão direito em App.java → Run 'App.main()'.

Ou via terminal:

bash
Copiar
Editar
java -cp out com.securepm.App
Siga as instruções no console.

✅ Exemplos de uso
📌 Registro inicial
Informe um nome de usuário.

Crie uma master password (mínimo 8 caracteres).

Escaneie o QR Code gerado com o Google Authenticator.

📌 Login
Informe o nome de usuário.

Digite a master password.

Insira o código TOTP de 6 dígitos do app 2FA.

Se autenticado, verá:

bash
Copiar
Editar
Login bem-sucedido!
📌 Menu principal
text
Copiar
Editar
1) Adicionar credencial → informe serviço, login e senha.  
2) Listar credenciais → veja ID, serviço, login e senha cifrada (Base64).  
3) Remover credencial → informe o ID.  
4) Gerar senha segura → insira tamanho (≥ 8).  
5) Sair → encerra o programa.

## 📂 Estrutura de arquivos

```text
securepm/
│
├─ src/
│  └─ main/
│     └─ java/
│        └─ com/securepm/
│           ├─ App.java
│           │
│           ├─ model/
│           │  ├─ Credential.java
│           │  └─ User.java
│           │
│           ├─ repository/
│           │  ├─ CredentialRepository.java
│           │  └─ UserRepository.java
│           │
│           ├─ service/
│           │  ├─ AuthService.java
│           │  └─ CredentialService.java
│           │
│           └─ util/
│              ├─ EncryptionUtil.java
│              └─ TwoFAUtil.java
│
├─ accessCredentials.dat      (gerado em tempo de execução)
├─ users.dat            (gerado em tempo de execução)
└─ README.md
```text

🧩 Principais componentes
App.java → ponto de entrada. Controla registro, login e menu.

AuthService.java → autenticação, 2FA, geração de chave AES.

CredentialService.java → operações de adicionar, listar e remover credenciais.

EncryptionUtil.java → utilitário de criptografia (AES e PBKDF2).

TwoFAUtil.java → utilitário de TOTP (Google Authenticator).

UserRepository/CredentialRepository → persistência local com arquivos .dat.

🖥️ Como rodar pela linha de comando

```text
  cd /caminho/para/securepm
  javac -d out src/main/java/com/securepm/**/*.java
  java -cp out com.securepm.App
```text

⚙️ Detalhes técnicos
- Criptografia: AES-CBC/PKCS5Padding com chave derivada via PBKDF2 (HmacSHA256).
- 2FA: Google Authenticator (códigos TOTP).
- Persistência: objetos serializados em arquivos .dat.
- Segurança:
            -Senhas armazenadas cifradas.
            -Master password não armazenada, apenas hash BCrypt + salt PBKDF2.
