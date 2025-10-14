<h1 align="center"> Sistema de Notificação de Estoque — API REST <br>
<img src="https://img.shields.io/badge/Status-In%20Progress-yellow" width="150" height="30" />
</h1>

---

<h2 align="center">🔗 Frontend</h2>
O frontend será desenvolvido separadamente. Quando estiver pronto, ficará disponível em um repositório dedicado:

- 🌐 [Sistema de Notificação de Estoque — Frontend](#)  <!-- atualizar link quando disponível -->
> 🚧 O frontend ainda está em desenvolvimento. O link será adicionado assim que o repositório estiver disponível.


---

<h2 align="center">Visão Geral do Projeto</h2>
<b>Sistema de Notificação de Estoque</b> é um backend desenvolvido com <b>Spring Boot</b>, projetado para gerenciar o estoque e enviar notificações para produtos com baixo estoque ou prestes a vencer.  
Este projeto segue uma <b>arquitetura em camadas</b> (controller, service, repository, model) e aplica boas práticas de organização de código, escalabilidade e manutenção.

---

<h2 align="center">Tecnologias Utilizadas</h2>

- ☕ **Backend**
  - ☕ Java 17+ + 🌱 Spring Boot 3
  - 🌐 Spring Web
  - 📦 [JPA](https://spring.io/projects/spring-data-jpa) + 🛠️ [Hibernate](https://hibernate.org/)
  - ✅ Validações (Bean Validation)
  - 🔄 Spring Boot DevTools
  - 🔧 Lombok
  - 📄 [Swagger (OpenAPI)](https://swagger.io/specification/)
 
- 🗄️ **Banco de Dados**
  - 🛠️ Controle de versionamento de banco com [Flyway](https://flywaydb.org/)
  - 🐘 PostgreSQL: Banco de dados
  
- 🧰 **Ferramentas e Build**
  - 📦 Maven: Gerenciamento de dependências e build
  - 🐧 [WSL](https://ubuntu.com/desktop/wsl) e 🐳 [Docker CLI](https://www.docker.com/products/cli/)

---

<h2 align="center">Estrutura do Projeto</h2>

```plaintext
src/main
├── java/com/github/renancvitor/inventory
│    ├── application/   # Camada de aplicação (controllers, services, DTOs, repositories)
│    ├── domain/        # Entidades de domínio, enums e exceções específicas
│    ├── exception/     # Tratamento e modelagem de exceções globais
│    ├── infra/         # Configurações, segurança, logs e integrações externas
│    ├── utils/         # Utilitários e helpers
│    └── InventoryNotificationSystemBackendApplication.java
├── resources
│    ├── db/            # Scripts Flyway (criação e seed de tabelas)
│    └── application.properties
└── test/java/com/github/renancvitor/inventory
     └── (future tests)
```
> 🔗 [Veja a estrutura completa do projeto aqui](./docs/project-structure.md)

---

<h2 align="center">Como Executar</h2>

### Pré-requisitos
- Java 17+
- PostgreSQL rodando localmente
- Maven instalado

### Passos
1. Clone o repositório:
```bash
git clone git@github.com:renancvitor/inventory-notification-system-backend.git
```
2. Acesse a pasta do projeto:
```bash
cd inventory-notification-system-backend
```
3. Configure o banco de dados em `src/main/resources/application.properties`
4. Execute a aplicação com Maven
```bash
./mvnw spring-boot:run
```
5. Acesse a API em `http://localhost:8080` (padrão)

---

<h2 align="center">Contribuições</h2>

Se você quiser contribuir para o projeto, siga estas etapas:

1. Faça um fork deste repositório.
2. Crie uma nova branch (`git checkout -b feature/alguma-coisa`).
3. Faça suas mudanças.
4. Envie um pull request explicando as mudanças realizadas.

Obrigado pelo interesse em contribuir!

---

<h2 align="center">Contato</h2>

Se tiver dúvidas ou sugestões, sinta-se à vontade para entrar em contato:

- 📧 **E-mail**: [renan.vitor.cm@gmail.com](mailto:renan.vitor.cm@gmail.com)
- 🟦 **LinkedIn**: [Renan Vitor](https://www.linkedin.com/in/renan-vitor-developer/)

---

<h2 align="center">Licença</h2>

📌 Este projeto está licenciado sob a [Licença MIT](LICENSE), o que significa que você pode utilizá-lo, modificar, compartilhar e distribuir livremente, desde que mantenha os devidos créditos aos autores e inclua uma cópia da licença original - veja o arquivo [LICENSE](LICENSE) para detalhes ou acesse a [licença MIT oficial](https://opensource.org/licenses/MIT).