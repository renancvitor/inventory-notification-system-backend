<h1 align="center">Sistema de Notificação de Estoque — API REST <br>
<img src="https://img.shields.io/badge/Status-In%20Progress-yellow" width="150" height="30" />
</h1>

---

<h2 align="center">🔗 Frontend</h2>
O frontend será desenvolvido separadamente. Quando estiver pronto, ficará disponível em um repositório dedicado:

- 🌐 [Sistema de Notificação de Estoque — Frontend](#)  <!-- atualizar link quando disponível -->
> 🚧 O frontend ainda está em desenvolvimento. O link será adicionado assim que o repositório estiver disponível.


---

<h2 align="center">Visão Geral do Projeto</h2>

<b>Sistema de Notificação de Estoque</b> é um backend desenvolvido com <b>[Spring Boot](https://spring.io/projects/spring-boot)</b>, projetado para gerenciar o estoque e enviar notificações para produtos com baixo estoque ou prestes a vencer.<br>
Desenvolvido principalmente para prática de backend, o projeto também pode atender pequenas empresas que buscam organizar seu fluxo de verbas.  
Este projeto segue uma <b>arquitetura em camadas</b> (controller, service, repository, model) e aplica boas práticas de organização de código, escalabilidade e manutenção.

O desenvolvimento do projeto consolidou habilidades como:
- 🏗️ Arquitetura RESTful
- 🧪 Testes unitários e de integração com [JUnit 5](https://junit.org/) e 🔧 [Mockito](https://site.mockito.org/)
- ✅ Validações robustas com [Bean Validation](https://docs.spring.io/spring-framework/reference/core/validation/beanvalidation.html)
- 🛠️ Tratamento de erros
- 📖 Documentação automatizada com [Swagger (OpenAPI)](https://swagger.io/specification/)
- 🔒 Segurança com [JWT (JSON Web Token)](https://jwt.io/)

A arquitetura em camadas e as boas práticas aplicadas tornam o código organizado, escalável e fácil de manter.

---

<h2 align="center">Tecnologias Utilizadas</h2>

- ☕ **Backend**
  - ☕ [Java 17](https://www.java.com/pt-BR/) ou superior + 🌱 [Spring Boot 3](https://start.spring.io/)
  - 🌐 [Spring Web](https://spring.io/projects/spring-ws)
  - 📦 [JPA](https://spring.io/projects/spring-data-jpa) + 🛠️ [Hibernate](https://hibernate.org/)
  - ✅ Validações ([Bean Validation](https://docs.spring.io/spring-framework/reference/core/validation/beanvalidation.html))
  - 🔄 [Spring Boot DevTools](https://docs.spring.io/spring-boot/reference/using/devtools.html)
  - 🔧 Lombok
  - 📄 [Swagger (OpenAPI)](https://swagger.io/specification/)
 
- 🗄️ **Banco de Dados**
  - 🛠️ Controle de versionamento de banco com [Flyway](https://flywaydb.org/)
  - 🐘 [PostgreSQL](https://www.postgresql.org/): Banco de dados
  
- 🧰 **Ferramentas e Build**
  - 📦 [Maven](https://maven.apache.org/): Gerenciamento de dependências e build
  - 🐧 [WSL](https://ubuntu.com/desktop/wsl) e 🐳 [Docker CLI](https://www.docker.com/products/cli/)

---

<h2 align="center">Ferramentas Utilizadas</h2>

- 💻 [Visual Studio Code](https://code.visualstudio.com/): Ambiente de desenvolvimento integrado (IDE) leve e extensível.
- 🐘 [PostgreSQL](https://www.postgresql.org/): Sistema de gerenciamento de banco de dados relacional de código aberto, usado via CLI em contêiner Docker.
- 📡 [Insomnia](https://insomnia.rest/): Ferramenta de teste de APIs REST que permite enviar requisições HTTP, validar respostas e testar endpoints com facilidade. 

---

<h2 align="center">Migrations e Versionamento de Banco</h2>

O projeto utiliza o [Flyway](https://flywaydb.org/) para gerenciar as **migrations de banco de dados** no [PostgreSQL](https://www.postgresql.org/). Todas as alterações de estrutura no banco, como criação de tabelas e mudanças de schema, são versionadas e controladas. Isso garante consistência entre os ambientes de desenvolvimento e produção.

---

<h2 align="center">Estratégia de Filtragem nas Listagens</h2>

Neste projeto adotei duas abordagens para filtragem em consultas:

- Para **consultas com múltiplos filtros opcionais**, utilizei a abordagem baseada em `Specifications` do [Spring Data JPA](https://spring.io/projects/spring-data-jpa). Isso garante flexibilidade, escalabilidade e código mais limpo para cenários complexos.

- Para **consultas simples, com filtros únicos ou poucos parâmetros fixos**, usei métodos diretos do repositório (`findBy...`), para manter simplicidade e performance sem overengineering.

Essa decisão busca balancear clareza, manutenção e boas práticas técnicas, garantindo que o código seja fácil de entender e evoluir.

---

<h2 align="center">Funcionalidades</h2>

O **Sistema de Notificação de Estoque** é um backend desenvolvido com [Spring Boot](https://spring.io/projects/spring-boot), com foco em boas práticas e organização de **API REST**.

### 🔒 Autenticação e Segurança
- Cadastro e login de usuários
- Autenticação via [JWT](https://jwt.io/)
- Controle de acesso baseado em perfis de usuário

### 📚 **Gerenciamento de Conteúdo**
- **Permissões**
  - Organização dos tipos de usuários
- **Status**
  - Organização dos status dos pedidos
- **Pessoas e Usuários**
  - Cadastrar
  - Listar (com paginação e filtros)
  - Editar permissões
  - Editar senha
  - Soft delete
  - Ativar
- **Produtos**
  - Cadastrar
  - Categorias para cada tipo de produto
  - Listar produtos (com paginação e filtros)
  - Controlar estoque
  - Soft delete
  - Ativar
  - Notificação via e-mail quando estoque abaixo do configurado
- **Movimentações**
  - Entrada e saída
  - Validação do estoque - movimentações não ocorrem com estoque menor ou igual a zero
  - Envio diário de relatórios de movimentações dos produtos
- **Pedidos**
  - Cadastrar
  - Listar pedidos (com paginação e filtros)
  - Aprovar/Reprovar
  - Controlar status
  - Atualizado pedido - enquanto status Pendente
  - Estoque atualizado somente após aprovação do pedido

### 🛠️ **Validações e Tratamento de Erros**
- Validação de dados de entrada (DTOs com Bean Validation)
- Mensagens de erro claras e padronizadas
- Tratamento centralizado e específico de exceções

### 📊 **Documentação**
- API documentada com [Swagger UI](https://swagger.io/specification/)

<!-- ---

<h2 align="center">Documentação Visual (em construção)</h2>

 ### 🌐 **API - Swagger**

Para ver a interface [Swagger](https://swagger.io/specification/) em ação, acesse as [demonstrações visuais](./docs/documentacao-swagger.md) com GIFs interativos mostrando os principais endpoints da API.

### 🗂️ **Diagrama ER do banco de dados PostgreSQL**

<p align="center">
  <img src="./docs/controle-verbas-der_1.png" alt="Diagrama ER" width="600"/>
</p> -->

---

<h2 align="center"> Testes Automatizados</h2>

O projeto conta com uma **cobertura significativa de testes unitários**, garantindo a qualidade e o correto funcionamento dos fluxos principais de negócio da API, incluindo:
- Cadastro, listagem, ativar/soft delete e edição de pedidos, produtos, pessoas, usuários.
- Autenticação com [JWT](https://jwt.io/).
- Validações de regras de negócio.
- Tratamento global de exceções.

**Tecnologias utilizadas nos testes**
- 🧪 [JUnit 5](https://junit.org/junit5/)
- 🔧 [Mockito](https://site.mockito.org/)
- 🧪 [Spring Boot Test](https://docs.spring.io/spring-security/reference/servlet/test/index.html)

---

<h2 align="center">Testando a API via Insomnia</h2>

Se preferir usar o [Insomnia](https://insomnia.rest/download) ao invés do Swagger UI, você pode importar diretamente todos os endpoints prontos para teste.

### Passos:

1. Abra o Insomnia [Insomnia](https://insomnia.rest/).
2. Vá em **File > Import > From File**.
3. Selecione o arquivo: [`docs/insomnia/insomnia-api-export`](./docs/insominia/insomnia-api-export)

Isso irá importar todos os endpoints organizados por pastas, com exemplos de requisição e possíveis payloads.

---

<h2 align="center">Estrutura do Projeto</h2>

```plaintext
.github/workflows/                      # Pipelines CI (build, testes, validações)
docs/                                   # Documentação auxiliar e exportações de API
 ├── insomnia/                          # Coleção da API para testes via Insomnia
 └── project-structure.md               # Mapa da arquitetura e organização do projeto

src/main/java/
 ├── application/                       # Camada de aplicação: DTOs, serviços, controllers, especificações e repositórios
 ├── domain/                            # Entidades, enums e exceções específicas de cada agregado de domínio
 ├── exception/                         # Exceções genéricas: factories, handlings e modelos de erro
 ├── infra/                             # Configurações, segurança, mensageria e integrações externas
 ├── utils/                             # Utilitários de uso geral (página customizada, mappers, utilidades)
 └── InventoryNotificationSystemBackendApplication.java

src/main/resources/
 ├── db/                                # Scripts Flyway (migrations e seeds)
 ├── application-*.properties           # Configurações específicas (dev, prod, secret)
 └── application.properties             # Configuração padrão
 

src/test/java/
 ├── controller/                        # Testes unitários dos controllers, organizados por domínio e ação
 ├── service/                           # Testes unitários dos services, com alta cobertura por método
 ├── utils/                             # Fábrica de entidades e mocks reutilizáveis para testes
 └── InventoryNotificationSystemBackendApplicationTests.java

src/test/resources/
 ├── application-test-secret.properties # Secrets isolados para os testes
 ├── application-test.properties        # Configuração do ambiente de testes
 └── payload/                           # Dados auxiliares em JSONL usados em testes e validações manuais

LICENSE                                 # Licença do projeto
README.md                               # Documentação principal do repositório
```
> 🔗 [Veja a estrutura completa do projeto aqui](./docs/project-structure.md)

---

<h2 align="center">Como Executar o Projeto</h2>

### Pré-requisitos:
- ☕ [Java 17](https://www.java.com/pt-BR/) ou superior
- 🐳 [Docker](https://www.docker.com/) e [Docker Compose](https://docs.docker.com/compose/) instalados
- 💻 IDE de sua preferência ([IntelliJ IDEA](https://www.jetbrains.com/pt-br/idea/), [VSCode](https://code.visualstudio.com/), [Eclipse](https://eclipseide.org/) etc.)
- 🐧 [WSL](https://ubuntu.com/desktop/wsl) (se estiver usando Windows)

### Passos
1. Clone o repositório:
```bash
git clone git@github.com:renancvitor/inventory-notification-system-backend.git
```
2. Acesse a pasta do projeto:
```bash
cd inventory-notification-system-backend
```
3. Inicie os serviços necessários no Docker (PostgreSQL e Kafka)
```bash
docker-compose up -d
```
Isso vai criar os containers do banco de dados e da mensageria. Certifique-se de que as portas configuradas no docker-compose.yml não estejam sendo usadas por outros serviços.

4. Verifique se todos os containers estão disponíveis
```bash
docker ps
```
⚠️ **Se algum container não estiver ativo, volte ao passo 3.**

5. Configure o banco de dados no arquivo `src/main/resources/application-dev.properties` com suas credenciais locais. Ao iniciar o projeto, as migrations serão aplicadas automaticamente pelo [Flyway](https://flywaydb.org/).
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/nome_do_banco
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```
6. Execute o backend com o Maven Wrapper:
```bash
./mvnw spring-boot:run
```
7. Acesse a API pelo navegador ou ferramentas como [Insomnia](https://insomnia.rest/) na porta configurada (por padrão http://localhost:8080).<br>
⚠️ **Lembre-se de manter o Docker rodando enquanto estiver utilizando a aplicação.**
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