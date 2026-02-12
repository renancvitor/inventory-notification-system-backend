<h1 id="inicio" align="center">Sistema de Notificação de Estoque — API REST <br>
<img src="https://img.shields.io/badge/Status-Completed-brightgreen" width="150" height="30" />
</h1>
<!-- Troque o texto e a cor do badge conforme o status do projeto:
     Status-Completed-brightgreen   → Projeto concluído
     Status-In%20Progress-yellow    → Projeto em andamento
     Status-Paused-orange           → Projeto pausado
     Status-Canceled-red            → Projeto cancelado
     Exemplo de uso:
     https://img.shields.io/badge/Status-Completed-brightgreen
-->

---

<h2 align="center">🔗 Frontend</h2>

O frontend é desenvolvido em um repositório separado.  
Acompanhe releases e evolução da interface em:

- 🌐 [Sistema de Notificação de Estoque — Frontend](https://github.com/renancvitor/inventory-notification-system-frontend)

> 🚧 O frontend ainda está em desenvolvimento.  

---

### 📊 Progresso do Projeto

Planejamento, tarefas e histórico de evolução disponíveis no GitHub Projects:

- 🗺️ [Inventory System - Roadmap](https://github.com/users/renancvitor/projects/2/views/1)

---

<h2 align="center">📑 Sumário</h2>

- [Visão Geral do Projeto](#visao-geral-do-projeto)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Ferramentas Utilizadas](#ferramentas-utilizadas)
- [Migrations e Versionamento de Banco](#migrations-e-versionamento-de-banco)
- [Estratégia de Filtragem nas Listagens](#estratégia-de-filtragem-nas-listagens)
- [Funcionalidades](#funcionalidades)
- [Documentação Visual](#documentação-visual)
  - [🌐 API - Swagger](#-api---swagger)
  - [🗂️ Diagrama ER do banco de dados PostgreSQL](#-diagrama-er-do-banco-de-dados-postgresql)
- [Demonstração das Notificações por E-mail](#demonstração-das-notificações-por-e-mail)
- [Mensageria com Apache Kafka](#mensageria-kafka)
- [Testes Automatizados](#testes-automatizados)
- [Testando a API via Insomnia](#testando-a-api-via-insomnia)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Como Executar o Projeto](#como-executar-o-projeto)
- [Deploy na AWS](#deploy-na-aws)
- [Contribuições](#contribuições)
- [Contato](#contato)
- [Licença](#licenca)

---

<h2 id="visao-geral-do-projeto" align="center">Visão Geral do Projeto</h2>

<b>Sistema de Notificação de Estoque</b> é um backend desenvolvido com <b>[Spring Boot](https://spring.io/projects/spring-boot)</b>, projetado para gerenciar o estoque e enviar notificações para produtos com baixo estoque.<br>
Desenvolvido principalmente para prática de backend, o projeto também atende pequenas empresas que buscam organizar e monitorar seus processos de estoque de materiais de uso interno (escritório, limpeza, relacionados). Ele segue uma arquitetura bem organizada em camadas e pacotes funcionais (application, domain, infra, exception e utils), garantindo escalabilidade e manutenção.

O desenvolvimento do projeto consolidou habilidades como:
- 🏗️ Arquitetura RESTful
- 🧪 Testes unitários e de integração com [JUnit 5](https://junit.org/) e 🔧 [Mockito](https://site.mockito.org/)
- ✅ Validações robustas com [Bean Validation](https://docs.spring.io/spring-framework/reference/core/validation/beanvalidation.html)
- 🛠️ Tratamento de erros
- 📖 Documentação automatizada com [Swagger (OpenAPI)](https://swagger.io/specification/)
- 🔒 Segurança com [JWT (JSON Web Token)](https://jwt.io/)

O uso de boas práticas e a organização do projeto garantem um código escalável, claro e de fácil manutenção.

<p align="right"><a href="#inicio">⬆️ Voltar ao início</a></p>

---

<h2 id="tecnologias-utilizadas" align="center">Tecnologias Utilizadas</h2>

- ☕ **Backend**
  - ☕ [Java 17](https://www.java.com/pt-BR/) ou superior + 🌱 [Spring Boot 3](https://start.spring.io/)
  - 🌐 [Spring Web](https://spring.io/projects/spring-web)
  - 📦 [JPA](https://spring.io/projects/spring-data-jpa) + 🛠️ [Hibernate](https://hibernate.org/)
  - ✅ Validações ([Bean Validation](https://docs.spring.io/spring-framework/reference/core/validation/beanvalidation.html))
  - 🔄 [Spring Boot DevTools](https://docs.spring.io/spring-boot/reference/using/devtools.html)
  - 🔧 Lombok
  - 📄 [Swagger (OpenAPI)](https://swagger.io/specification/)
 
- 🗄️ **Banco de Dados**
  - 🛠️ Controle de versionamento de banco com [Flyway](https://flywaydb.org/)
  - 🐘 [PostgreSQL](https://www.postgresql.org/): Banco de dados
  
- 🧰 **Build e Ambiente**
  - 📦 [Maven](https://maven.apache.org/): Gerenciamento de dependências e build
  - 🐧 [WSL](https://ubuntu.com/desktop/wsl) e 🐳 [Docker CLI](https://www.docker.com/products/cli/)

  <p align="right"><a href="#inicio">⬆️ Voltar ao início</a></p> 

---

<h2 id="ferramentas-utilizadas" align="center">Ferramentas Utilizadas</h2>

- 💻 [Visual Studio Code](https://code.visualstudio.com/): Ambiente de desenvolvimento integrado (IDE) leve e extensível.
- 🐳 [Docker](https://www.docker.com/): Utilizado via Docker CLI para execução e gerenciamento dos contêineres do projeto.
- 🐘 [PostgreSQL](https://www.postgresql.org/): Banco de dados relacional executado em contêiner Docker, acessado via CLI (psql).
- 📡 [Insomnia](https://insomnia.rest/): Ferramenta de teste de APIs REST que permite enviar requisições HTTP, validar respostas e testar endpoints com facilidade. 

<p align="right"><a href="#inicio">⬆️ Voltar ao início</a></p>

---

<h2 id="migrations-e-versionamento-de-banco" align="center">Migrations e Versionamento de Banco</h2>

O projeto utiliza o [Flyway](https://flywaydb.org/) para gerenciar as **migrations de banco de dados** no [PostgreSQL](https://www.postgresql.org/). Todas as alterações de estrutura no banco, como criação de tabelas e mudanças de schema, são versionadas e controladas. Isso garante consistência entre os ambientes de desenvolvimento e produção.

<p align="right"><a href="#inicio">⬆️ Voltar ao início</a></p>

---

<h2 id="estratégia-de-filtragem-nas-listagens" align="center">Estratégia de Filtragem nas Listagens</h2>

Neste projeto adotei duas abordagens para filtragem em consultas:

- Para **consultas com múltiplos filtros opcionais**, utilizei a abordagem baseada em `Specifications` do [Spring Data JPA](https://spring.io/projects/spring-data-jpa). Isso garante flexibilidade, escalabilidade e código mais limpo para cenários complexos.

- Para **consultas simples, com filtros únicos ou poucos parâmetros fixos**, usei métodos diretos do repositório (`findBy...`), para manter simplicidade e performance sem overengineering.

Essa decisão busca balancear clareza, manutenção e boas práticas técnicas, garantindo que o código seja fácil de entender e evoluir.

<p align="right"><a href="#inicio">⬆️ Voltar ao início</a></p>

---

<h2 id="funcionalidades" align="center">Funcionalidades</h2>

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
  - Atualizar pedido — permitido apenas enquanto o status for Pendente.
  - Estoque atualizado somente após aprovação do pedido

### 🛠️ **Validações e Tratamento de Erros**
- Validação de dados de entrada (DTOs com Bean Validation)
- Mensagens de erro claras e padronizadas
- Tratamento centralizado e específico de exceções

### 📊 **Documentação**
- API documentada com [Swagger UI](https://swagger.io/specification/)

<p align="right"><a href="#inicio">⬆️ Voltar ao início</a></p>

---

<h2 id="documentação-visual" align="center">Documentação Visual</h2>

 <h3 id="-api---swagger">🌐 <strong>API - Swagger</strong></h3>

Para ver a interface [Swagger](https://swagger.io/specification/) em ação, acesse as demonstrações visuais com GIFs interativos mostrando os principais endpoints da API.

👉 Veja a documentação visual do Swagger aqui:  
[📘 Swagger — Demonstrações Visuais da API](./docs/swagger-demonstration/swagger-documentation.md)

<h3 id="-diagrama-er-do-banco-de-dados-postgresql">🗂️ <strong>Diagrama ER do banco de dados PostgreSQL</strong></h3>

👉 Veja o diagrama completo aqui:  
[📊 Diagrama ER — Banco de Dados](./docs/database/database-description.md)

<p align="right"><a href="#inicio">⬆️ Voltar ao início</a></p>

---

<h2 id="demonstração-das-notificações-por-e-mail" align="center">Demonstração das Notificações por E-mail</h2>

O sistema envia notificações automáticas por e-mail para eventos críticos e operacionais,
como:

- Estoque abaixo do mínimo configurado  
- Relatório diário consolidado de pedidos gerados

🔗 Veja os exemplos reais dos e-mails enviados:  
[➡️ Exemplos de Notificações por E-mail](./docs/email-notification/email-notification.md)

<p align="right"><a href="#inicio">⬆️ Voltar ao início</a></p>

---

<h2 id="mensageria-kafka" align="center"> Mensageria com Apache Kafka</h2>

O projeto utiliza **[Apache Kafka](https://kafka.apache.org/)** para comunicação assíncrona entre contextos de negócio,
com foco em **desacoplamento, resiliência e escalabilidade**.

A mensageria é utilizada principalmente para eventos de negócio como:

- criação de pedidos
- notificações de estoque abaixo do mínimo
- processamento assíncrono

A arquitetura foi projetada com foco em padrões utilizados no mercado, incluindo:

- eventos de domínio desacoplados da infraestrutura
- event envelope padronizado
- versionamento de eventos
- retry automático e Dead Letter Topic (DLT)
- idempotência no consumo
- correlationId para rastreabilidade
- Outbox Pattern para consistência transacional

📘 **Documentação técnica completa**

A documentação técnica completa da arquitetura de mensageria, incluindo fluxos, decisões arquiteturais
e diagramas C4, está disponível em:

➡️ [Kafka Architecture — Documentação Técnica](./docs/kafka/kafka-architecture.md)

<p align="right"><a href="#inicio">⬆️ Voltar ao início</a></p>

---

<h2 id="testes-automatizados" align="center"> Testes Automatizados</h2>

O projeto conta com uma **cobertura significativa de testes unitários**, garantindo a qualidade e o correto funcionamento dos fluxos principais de negócio da API, incluindo:
- Cadastro, listagem, ativar/soft delete e edição dos recursos suportados (pedidos, produtos, pessoas, e usuários).
- Autenticação com [JWT](https://jwt.io/).
- Validações de regras de negócio.
- Tratamento global de exceções.

**Tecnologias utilizadas nos testes**
- 🧪 [JUnit 5](https://junit.org/junit5/)
- 🔧 [Mockito](https://site.mockito.org/)
- 🧪 [Spring Boot Test](https://docs.spring.io/spring-security/reference/servlet/test/index.html)

<p align="right"><a href="#inicio">⬆️ Voltar ao início</a></p>

---

<h2 id="testando-a-api-via-insomnia" align="center">Testando a API via Insomnia</h2>

Se preferir usar o [Insomnia](https://insomnia.rest/download) ao invés do Swagger UI, você pode importar diretamente todos os endpoints prontos para teste.

### Passos:

1. Abra o [Insomnia](https://insomnia.rest/).
2. Vá em **File > Import > From File**.
3. Selecione o arquivo: [`docs/insomnia/insomnia-api-export`](./docs/insomnia/insomnia-api-export)

Isso irá importar todos os endpoints organizados por pastas, com exemplos de requisição e possíveis payloads.

<p align="right"><a href="#inicio">⬆️ Voltar ao início</a></p>

---

<h2 id="estrutura-do-projeto" align="center">Estrutura do Projeto</h2>

```plaintext
.github/workflows/                      # Pipelines de CI: build, testes e validações automatizadas
docs/                                   # Documentação auxiliar do projeto
 ├── insomnia/                          # Export da coleção da API para testes via Insomnia
 ├── gifs/                              # Demonstrações visuais da API (GIFs usados no Swagger documentation)
 ├── email-notification/                # Exemplos reais de e-mails enviados pelo sistema
 ├── email-notification.md              # Documento explicando e exibindo as notificações por e-mail
 ├── inventory-notification-der.png     # Diagrama ER do banco de dados PostgreSQL
 ├── project-structure.md               # Estrutura detalhada do projeto e organização dos pacotes
 └── swagger-documentation.md           # Documentação visual da API com GIFs demonstrativos         

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

<p align="right"><a href="#inicio">⬆️ Voltar ao início</a></p>

---

<h2 id="como-executar-o-projeto" align="center">Como Executar o Projeto</h2>

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
3. Inicie os serviços necessários no Docker (PostgreSQL)
```bash
docker compose -f docker-compose.dev.yml up -d
```
Isso vai criar o container do banco de dados PostgreSQL. Certifique-se de que as portas configuradas no docker-compose.yml não estejam sendo usadas por outros serviços.

4. Verifique se todos os containers estão disponíveis
```bash
docker ps
```
⚠️ **Se algum container não estiver ativo, volte ao passo 3.**

5. Configure o banco de dados no arquivo `src/main/resources/application-dev.yml` com suas credenciais locais. Ao iniciar o projeto, as migrations serão aplicadas automaticamente pelo [Flyway](https://flywaydb.org/).
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/nome_do_banco
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```
6. Execute o backend com o Maven Wrapper no perfil de desenvolvimento:
```bash
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```
7. Acesse a API pelo navegador ou ferramentas como [Insomnia](https://insomnia.rest/) na porta configurada (por padrão http://localhost:8080).<br>
⚠️ **Lembre-se de manter o Docker rodando enquanto estiver utilizando a aplicação.**

<p align="right"><a href="#inicio">⬆️ Voltar ao início</a></p>

---

<h2 id="deploy-na-aws" align="center">Deploy na AWS</h2>

Este projeto está **automaticamente deployado na AWS EC2** com um pipeline de CI/CD robusto e seguro. A aplicação é atualizada automaticamente a cada push para a branch `main`, garantindo que sempre esteja rodando a versão mais recente do código.

### 🚀 Arquitetura do Deploy

O processo de deploy é totalmente automatizado através de **GitHub Actions**:

1. **CI Pipeline** - Compila e testa o código a cada push
2. **CD Pipeline** - Realiza migração de banco, compila o JAR e faz deploy na EC2
3. **Validação** - Verifica se o serviço iniciou corretamente com health checks

### 💡 Destaques da Automação

- ✅ **Zero Downtime**: O serviço é gracefully reiniciado sem perder requisições
- ✅ **Backup Automático**: Cada deploy faz backup do JAR anterior
- ✅ **Validação de Saúde**: Health check confirma que a aplicação iniciou corretamente
- ✅ **Logs Detalhados**: Cada etapa do deploy é registrada para troubleshooting
- ✅ **Secrets Seguros**: Todas as credenciais armazenadas no GitHub Secrets

### 📚 Documentação Completa

Para instruções detalhadas sobre deploy manual, configuração de secrets, troubleshooting e muito mais:

👉 Veja a documentação completa aqui:  
[🚀 Deploy na AWS — Guia Completo](./docs/deployment/DEPLOYMENT.md)

<p align="right"><a href="#inicio">⬆️ Voltar ao início</a></p>

---

<h2 id="contribuições" align="center">Contribuições</h2>

Se você quiser contribuir para o projeto, siga estas etapas:

1. Faça um fork deste repositório.
2. Crie uma nova branch (`git checkout -b feature/alguma-coisa`).
3. Faça suas mudanças.
4. Envie um pull request explicando as mudanças realizadas.

Obrigado pelo interesse em contribuir!

<p align="right"><a href="#inicio">⬆️ Voltar ao início</a></p>

---

<h2 id="contato" align="center">Contato</h2>

Se tiver dúvidas ou sugestões, sinta-se à vontade para entrar em contato:

- 📧 **E-mail**: [renan.vitor.cm@gmail.com](mailto:renan.vitor.cm@gmail.com)

- 🟦 **LinkedIn**: [Renan Vitor](https://www.linkedin.com/in/renan-vitor-developer/)

<p align="right"><a href="#inicio">⬆️ Voltar ao início</a></p>

---

<h2 id="licenca" align="center">Licença</h2>

📌 Este projeto está licenciado sob a [Licença MIT](LICENSE), o que significa que você pode utilizá-lo, modificar, compartilhar e distribuir livremente, desde que mantenha os devidos créditos aos autores e inclua uma cópia da licença original - veja o arquivo [LICENSE](LICENSE) para detalhes ou acesse a [licença MIT oficial](https://opensource.org/licenses/MIT).

<p align="right"><a href="#inicio">⬆️ Voltar ao início</a></p>

---
