# Varredura End-to-End: `system_logs` e `error_logs`

> **Data da análise:** Abril/2026  
> **Repositório:** `renancvitor/inventory-notification-system-backend`

---

## Sumário

1. [Inventário completo de arquivos](#1-inventário-completo-de-arquivos)
2. [Papel de cada arquivo no fluxo](#2-papel-de-cada-arquivo-no-fluxo)
3. [Fluxo end-to-end](#3-fluxo-end-to-end)
4. [Problemas e oportunidades de melhoria](#4-problemas-e-oportunidades-de-melhoria)
5. [Alternativas profissionais / padrões de mercado](#5-alternativas-profissionais--padrões-de-mercado)
6. [Diagrama resumido do fluxo atual](#6-diagrama-resumido-do-fluxo-atual)

---

## 1. Inventário completo de arquivos

### Entidades de Domínio

| Arquivo | Papel |
|---|---|
| `src/main/java/…/domain/entity/systemlog/SystemLog.java` | Entidade JPA mapeada para a tabela `system_logs` |
| `src/main/java/…/domain/entity/systemlog/SystemLogEvent.java` | Evento Spring (POJO) transportado pelo `ApplicationEventPublisher` |
| `src/main/java/…/domain/entity/errorlog/ErrorLog.java` | Entidade JPA mapeada para a tabela `error_logs` |
| `src/main/java/…/domain/entity/errorlog/ErrorLogEvent.java` | Evento Spring (POJO) transportado pelo `ApplicationEventPublisher` |

### Infraestrutura de mensageria (in-process)

| Arquivo | Papel |
|---|---|
| `src/main/java/…/infra/messaging/systemlog/LoggableData.java` | Interface marcadora para snapshots de dados a serem logados |
| `src/main/java/…/infra/messaging/systemlog/SystemLogPublisherService.java` | Publica `SystemLogEvent` via `ApplicationEventPublisher`; serializa `LoggableData` → JSON |
| `src/main/java/…/infra/messaging/systemlog/SystemLogListener.java` | Ouve `SystemLogEvent` e persiste `SystemLog` no banco |
| `src/main/java/…/infra/messaging/errorlog/ErrorLogPublisherService.java` | Publica `ErrorLogEvent` via `ApplicationEventPublisher` |
| `src/main/java/…/infra/messaging/errorlog/ErrorLogListener.java` | Ouve `ErrorLogEvent` e persiste `ErrorLog` no banco |

### Repositórios

| Arquivo | Papel |
|---|---|
| `src/main/java/…/application/systemlog/repository/SystemLogRepository.java` | `JpaRepository<SystemLog, Long>` — apenas `save()` é usado |
| `src/main/java/…/application/errorlog/repository/ErrorLogRepository.java` | `JpaRepository<ErrorLog, Long>` — apenas `save()` é usado |

### DTOs / Snapshots (`LoggableData`)

| Arquivo | Entidade capturada |
|---|---|
| `application/product/dto/ProductLogData.java` | `Product` |
| `application/order/dto/OrderLogData.java` | `Order` (inclui lista de `MovementLogData`) |
| `application/movement/dto/MovementLogData.java` | `Movement` |
| `application/user/dto/UserLogData.java` | `User` (inclui tipo de usuário) |
| `application/user/dto/UserLogPasswordData.java` | `User` — senha mascarada como `"******"` |
| `application/person/dto/PersonLogData.java` | `Person` |
| `application/email/dto/EmailLogData.java` | Dados do e-mail enviado via AWS SES |

### Serviços que disparam system logs

| Arquivo | Eventos publicados |
|---|---|
| `application/product/service/ProductService.java` | `PRODUCT_CREATED`, `PRODUCT_UPDATED`, `PRODUCT_DELETED`, `PRODUCT_ACTIVATED` |
| `application/order/service/OrderService.java` | `ORDER_CREATED`, `ORDER_UPDATED`, `ORDER_REJECTED`, `ORDER_APPROVED` |
| `application/movement/service/MovementService.java` | `PRODUCT_MOVEMENT` |
| `application/user/service/UserService.java` | `USER_CREATED`, `USER_UPDATED_PASSWORD`, `UPDATED_USER_TYPE`, `USER_DELETED`, `USER_ACTIVATED` |
| `application/person/service/PersonService.java` | `PERSON_CREATED`, `PERSON_DELETED`, `PERSON_ACTIVATED` |
| `application/email/service/EmailService.java` | `EMAIL_SENT` |

### Manipulador que dispara error logs

| Arquivo | Papel |
|---|---|
| `exception/handler/ErrorHandler.java` | `@RestControllerAdvice` — intercepta todas as exceções e chama `ErrorLogPublisherService.publish()` |
| `shared/StackTraceUtils.java` | Formata os 10 primeiros frames do stack trace para o campo `stackTrace` do error log |

### Migrations Flyway

| Arquivo | DDL |
|---|---|
| `db/migration/V1__create_table_system_logs.sql` | Cria a tabela `system_logs` (V1 — primeira migration do projeto) |
| `db/migration/V11__create_table_error_logs.sql` | Cria a tabela `error_logs` (V11 — adicionada posteriormente) |

### Testes

Todos os testes de serviço (`ProductServiceCreateTests`, `OrderServiceCreateTests`, `MovementServiceTests`, `UserService*Tests`, `PersonService*Tests`, `EmailServiceTests`) e de controller (`OrderController*Tests`, `ProductController*Tests`, etc.) injetam `SystemLogPublisherService` como `@Mock`, impedindo que o log seja acidentalmente testado como side-effect. `ErrorLogPublisherService` não aparece diretamente nos testes de serviço — é exercitado indiretamente pelo `ErrorHandler`.

### Documentação existente

- `docs/project-structure.md` — lista `SystemLogRepository`, `ErrorLogRepository`, todos os `*LogData`, `SystemLogListener`, `ErrorLogListener`, `SystemLogPublisherService`, `ErrorLogPublisherService` na árvore de diretórios.

---

## 2. Papel de cada arquivo no fluxo

### `SystemLogEvent` / `ErrorLogEvent`

São POJOs imutáveis (Lombok `@AllArgsConstructor @Getter`) sem anotações Spring. Servem exclusivamente de "envelope" para ser transportado pelo `ApplicationEventPublisher` in-process.

- **`SystemLogEvent`** carrega: `eventType`, `description`, `oldValue` (JSON serializado), `newValue` (JSON serializado).
- **`ErrorLogEvent`** carrega: `errorType`, `message`, `stackTrace`, `path`.

### `LoggableData`

Interface marcadora vazia com um static default que apenas lança `UnsupportedOperationException`. Todos os records `*LogData` a implementam e fornecem o factory method estático `fromEntity()`.

```java
// LoggableData.java
public interface LoggableData {
    static LoggableData fromEntity(Object entity) {
        throw new UnsupportedOperationException("Método precisa ser implementado na classe concreta.");
    }
}
```

### `SystemLogPublisherService`

Duas responsabilidades: serializar `LoggableData → String JSON` (via `ObjectMapper`) e publicar o evento.

```java
// SystemLogPublisherService.java:20
public void publish(String eventType, String description, LoggableData oldData, LoggableData newData) {
    String oldValue = oldData != null ? toJson(oldData) : "N/A";
    String newValue = newData != null ? toJson(newData) : "N/A";
    publisher.publishEvent(new SystemLogEvent(eventType, description, oldValue, newValue));
}
```

> ⚠️ Instancia `ObjectMapper` como campo (`private final ObjectMapper mapper = new ObjectMapper()` — linha 18), não o injeta como bean Spring.

### `SystemLogListener` / `ErrorLogListener`

`@EventListener` **síncrono** (sem `@Async`). Mapeiam o evento para a entidade JPA manualmente (sem mapper nem builder) e chamam `repository.save()`.

```java
// SystemLogListener.java:19
@EventListener
public void handleLogEvent(SystemLogEvent systemLogEvent) {
    SystemLog systemLog = new SystemLog();
    systemLog.setEventType(systemLogEvent.getEventType());
    systemLog.setDescription(systemLogEvent.getDescription());
    systemLog.setOldValue(systemLogEvent.getOldValue());
    systemLog.setNewValue(systemLogEvent.getNewValue());
    systemLogRepository.save(systemLog);
}
```

### `SystemLog` / `ErrorLog`

`@Entity` com `@PrePersist` para preencher `createdAt` automaticamente. `SystemLog` armazena `oldValue`/`newValue` como `TEXT` (JSON serializado). `ErrorLog` armazena `stackTrace` como `TEXT`.

### `SystemLogRepository` / `ErrorLogRepository`

`JpaRepository` puro — nenhum método de query customizado. **Nenhuma API REST expõe esses dados** — são apenas write-only no banco.

### `ErrorHandler`

`@RestControllerAdvice` com método privado `logError(ex, request)` (linhas 42–59) que:
1. Loga no SLF4J/Logback com nível `ERROR` (único uso de logging estruturado no projeto).
2. Chama `errorLogPublisherService.publish()` com os 10 primeiros frames do stack trace via `StackTraceUtils`.

```java
// ErrorHandler.java:42
private void logError(Exception ex, HttpServletRequest request) {
    log.error("Exception handled | type={} | uri={} | message={}",
            ex.getClass().getSimpleName(), request.getRequestURI(), ex.getMessage(), ex);
    errorLogPublisherService.publish(
            ex.getClass().getSimpleName(),
            message,
            StackTraceUtils.formatStackTrace(ex, 10),
            request.getRequestURI());
}
```

---

## 3. Fluxo end-to-end

### 3a. Criação de um System Log (exemplo: `PRODUCT_CREATED`)

```
[HTTP POST /products]
        │
        ▼
ProductController.create(data, loggedInUser)
        │
        ▼
ProductService.create()                          [ProductService.java:68]
  1. Autoriza (AuthenticationService)
  2. Busca Category → salva Product
  3. Constrói ProductLogData.fromEntity(product)  [ProductLogData.java:17]
  4. logPublisherService.publish(                 [SystemLogPublisherService.java:20]
        "PRODUCT_CREATED",
        "Produto cadastrado pelo usuário ...",
        null,          ← oldData = null → oldValue = "N/A"
        newData)
           │
           ├─ toJson(newData) → ObjectMapper.writeValueAsString()
           └─ publisher.publishEvent(new SystemLogEvent(...))
                    │
                    ▼  (síncrono — mesma thread)
        SystemLogListener.handleLogEvent(event)  [SystemLogListener.java:19]
          1. new SystemLog()
          2. set eventType, description, oldValue, newValue
          3. systemLogRepository.save(systemLog)
                    │
                    ▼
             INSERT INTO system_logs (event_type, description, old_value, new_value, created_at)
```

**Classes/métodos principais:**

```
ProductService.create()
  → SystemLogPublisherService.publish()
    → SystemLogPublisherService.toJson()
    → ApplicationEventPublisher.publishEvent(SystemLogEvent)
      → SystemLogListener.handleLogEvent()
        → SystemLogRepository.save()
          → system_logs (banco)
```

**Dependências entre camadas:**

```
Application (ProductService)
  → Infra/Messaging (SystemLogPublisherService)
    → Domain/Event (SystemLogEvent)
      → Infra/Listener (SystemLogListener)
        → Application/Repository (SystemLogRepository)
          → Banco de dados (system_logs)
```

---

### 3b. Criação de um Error Log (exemplo: `EntityNotFoundException`)

```
[HTTP GET /products/999]
        │
        ▼
ProductService.getById(999)
  productRepository.findById(999) → Optional.empty()
  throw new EntityNotFoundException(...)
        │
        ▼  (propaga até o @RestControllerAdvice)
ErrorHandler.handle404(ex, request)              [ErrorHandler.java:78]
  └─ logError(ex, request)                       [ErrorHandler.java:42]
        │
        ├─ log.error(...)     ← SLF4J/Logback (console/arquivo)
        └─ errorLogPublisherService.publish(
                "EntityNotFoundException",
                ex.getMessage(),
                StackTraceUtils.formatStackTrace(ex, 10),
                "/products/999")
                    │
                    └─ publisher.publishEvent(new ErrorLogEvent(...))
                                │
                                ▼  (síncrono)
                    ErrorLogListener.handleErrorEvent(event)
                      1. new ErrorLog()
                      2. set errorType, message, stackTrace, path
                      3. errorLogRepository.save(errorLog)
                                │
                                ▼
                         INSERT INTO error_logs (error_type, message, stack_trace, path, created_at)
```

**Classes/métodos principais:**

```
ErrorHandler.logError()
  → ErrorLogPublisherService.publish()
    → ApplicationEventPublisher.publishEvent(ErrorLogEvent)
      → ErrorLogListener.handleErrorEvent()
        → ErrorLogRepository.save()
          → error_logs (banco)
```

---

### 3c. Tabela de todos os `eventType` gerados

| `eventType` | Serviço gerador | oldData | newData |
|---|---|---|---|
| `PRODUCT_CREATED` | `ProductService.create()` | `null` | `ProductLogData` |
| `PRODUCT_UPDATED` | `ProductService.update()` | `ProductLogData` | `ProductLogData` |
| `PRODUCT_DELETED` | `ProductService.delete()` | `ProductLogData` | `ProductLogData` |
| `PRODUCT_ACTIVATED` | `ProductService.activate()` | `ProductLogData` | `ProductLogData` |
| `ORDER_CREATED` | `OrderService.create()` | `null` | `OrderLogData` |
| `ORDER_UPDATED` | `OrderService.update()` | `OrderLogData` | `OrderLogData` |
| `ORDER_REJECTED` | `OrderService.reject()` | `OrderLogData` | `OrderLogData` |
| `ORDER_APPROVED` | `OrderService.approve()` | `OrderLogData` | `OrderLogData` |
| `PRODUCT_MOVEMENT` | `MovementService.handleMovement()` | `null` | `MovementLogData` |
| `USER_CREATED` | `UserService.create()` | `null` | `UserLogData` |
| `USER_UPDATED_PASSWORD` | `UserService.updatePassword()` | `UserLogPasswordData` | `UserLogPasswordData` |
| `UPDATED_USER_TYPE` | `UserService.updateUserType()` | `UserLogData` | `UserLogData` |
| `USER_DELETED` | `UserService.delete()` | `UserLogData` | `UserLogData` |
| `USER_ACTIVATED` | `UserService.activate()` | `UserLogData` | `UserLogData` |
| `PERSON_CREATED` | `PersonService.create()` | `null` | `PersonLogData` |
| `PERSON_DELETED` | `PersonService.delete()` | `PersonLogData` | `PersonLogData` |
| `PERSON_ACTIVATED` | `PersonService.activate()` | `PersonLogData` | `PersonLogData` |
| `EMAIL_SENT` | `EmailService.sendEmail()` | `null` | `EmailLogData` |

---

## 4. Problemas e oportunidades de melhoria

### 4.1 🔴 Problemas críticos

#### `ObjectMapper` instanciado como campo (não como bean Spring)

**Arquivo:** `SystemLogPublisherService.java`, linha 18

```java
private final ObjectMapper mapper = new ObjectMapper();
```

`ObjectMapper` é caro para instanciar e thread-safe quando compartilhado. Deve ser injetado via `@Autowired` ou `@RequiredArgsConstructor` para reutilizar o bean configurado pelo Spring Boot (que inclui `JavaTimeModule`, formato de data, etc.). Tal como está, o mapper criado aqui **não herda** as customizações globais da aplicação.

---

#### `UserLogData` serializa o hash bcrypt da senha

**Arquivo:** `UserLogData.java`, linha 9

```java
public record UserLogData(Long id, String password, String userType) ...
```

O campo `password` (hash bcrypt) é incluído no JSON gravado em `old_value` e `new_value` da tabela `system_logs`. Embora seja um hash, logs de auditoria não devem conter dados de credencial. `UserLogPasswordData` tenta remediar mascarando a senha como `"******"`, mas `UserLogData` — usado em `create`, `updateUserType`, `delete` e `activate` — ainda expõe o hash.

---

#### Bug: `handleEmailException` sem `@ExceptionHandler`

**Arquivo:** `ErrorHandler.java`, linha 167

```java
// ⚠️ Falta @ExceptionHandler(EmailException.class)
public ResponseEntity<ApiError> handleEmailException(EmailException ex, HttpServletRequest request) {
    ...
}
```

O método existe mas não é decorado com `@ExceptionHandler(EmailException.class)`. Portanto, **nunca é invocado** pelo Spring — `EmailException` cairá no handler genérico `handleGeneric()` (linha 94), retornando HTTP 500 com a mensagem genérica "Erro interno no servidor." em vez da mensagem específica de e-mail.

---

#### `@EventListener` síncrono: risco de inconsistência transacional

O listener chama `repository.save()` dentro do contexto de transação do serviço de negócio. Dois cenários problemáticos:

1. **Falha no save do log:** causa rollback da transação inteira do negócio (o produto não será criado por causa de uma falha no log).
2. **Rollback do negócio após o listener:** o evento foi disparado e o listener executou, mas se a transação de negócio fizer rollback depois, o log ficará registrado no banco mesmo sem a operação de negócio ter ocorrido.

---

#### Ausência de índices nas tabelas de log

**Arquivos:** `V1__create_table_system_logs.sql` e `V11__create_table_error_logs.sql`

```sql
-- Nenhum índice além da PK
CREATE TABLE system_logs (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL
    -- ...
);
```

Sem índices em `event_type` e `created_at` (`system_logs`) e `error_type`, `path`, `created_at` (`error_logs`). Com volume de dados crescente, qualquer consulta por tipo ou período fará **full table scan**.

---

#### Nenhuma API expõe os logs para consulta

Não há `SystemLogController` nem `ErrorLogController`. Os dados persistidos são write-only — não há como consumi-los sem acesso direto ao banco de dados.

---

#### `LoggableData.fromEntity()` é um static default sem garantia de compilação

```java
public interface LoggableData {
    static LoggableData fromEntity(Object entity) {
        throw new UnsupportedOperationException("...");
    }
}
```

Métodos estáticos em interfaces não são polimórficos e não podem ser sobrescritos. Isso não oferece garantia em tempo de compilação de que as implementações concretas terão `fromEntity()`. O compilador não exige que implementações da interface forneçam esse método.

---

### 4.2 🟡 Problemas de nomenclatura e coesão

| Problema | Localização | Detalhe |
|---|---|---|
| Pacote `messaging` sem mensageria real | `infra/messaging/systemlog`, `infra/messaging/errorlog` | Usa apenas eventos in-process Spring, não Kafka/RabbitMQ. Nome enganoso. |
| `eventType` sem padrão de nomenclatura | Múltiplos serviços | `UPDATED_USER_TYPE` vs `USER_UPDATED_PASSWORD` vs `PRODUCT_UPDATED` — sem convenção consistente (verbos, objetos, casing variam). |
| Typo na descrição do log | `PersonService.java:127` | `"Person reativado"` em vez de `"Pessoa reativada"` |
| Typo na descrição do log | `MovementService.java:99` | `"Produto movimentado pela usuário"` → deve ser `"pelo usuário"` |
| `OrderLogData` com `movements` vazios na criação | `OrderLogData.java:34` | Durante `ORDER_CREATED`, `order.getMovements()` está vazio (os items existem, mas os movements só são criados na aprovação). O campo `movements` sempre será lista vazia no log de criação. |

---

### 4.3 🟡 Inconsistência de anotação transacional

- `ProductService` usa `@org.springframework.transaction.annotation.Transactional`
- `OrderService` usa `@jakarta.transaction.Transactional`

Essas anotações têm comportamentos diferentes em relação a rollback e propagação. A inconsistência pode causar bugs sutis.

---

## 5. Alternativas profissionais / padrões de mercado

### 5.1 Logging estruturado (SLF4J + Logback + JSON)

Em vez de (ou em adição a) persistir logs no banco, usar logging estruturado em JSON com **Logback + logstash-logback-encoder**:

```xml
<!-- logback-spring.xml -->
<appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
  <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
</appender>
```

Com MDC (`MDC.put("userId", ...)`, `MDC.put("eventType", ...)`), todos os campos ficam disponíveis como campos JSON estruturados sem necessidade de tabela dedicada no banco.

**Dependência Maven:**
```xml
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>7.4</version>
</dependency>
```

---

### 5.2 Auditoria com Hibernate Envers

Para auditoria de entidades de negócio (quem alterou o quê e quando), usar **Hibernate Envers** em vez de criar manualmente `SystemLog`:

```java
@Entity
@Audited // ← todas as mudanças versionadas automaticamente
public class Product { ... }
```

Isso gera tabelas `product_aud`, `revinfo` automaticamente e rastreia todas as revisões sem código manual.

**Dependência Maven:**
```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-envers</artifactId>
</dependency>
```

---

### 5.3 `@TransactionalEventListener` para desacoplar log do commit

Para garantir que o log só seja persistido **após** o commit bem-sucedido da transação de negócio:

```java
// Substitui @EventListener
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void handleLogEvent(SystemLogEvent event) {
    SystemLog systemLog = new SystemLog();
    // ...
    systemLogRepository.save(systemLog);
}
```

Isso elimina o risco de logs inconsistentes e o risco de falha no log causar rollback do negócio.

---

### 5.4 Tracing distribuído (Micrometer Tracing / OpenTelemetry)

Adicionar **Micrometer Tracing** (nativo no Spring Boot 3) com exportação para Zipkin ou Grafana Tempo:

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-otel</artifactId>
</dependency>
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-exporter-zipkin</artifactId>
</dependency>
```

`traceId` e `spanId` são propagados automaticamente e injetados nos logs via MDC, permitindo correlação de logs, events e erros numa única requisição sem código adicional.

---

### 5.5 Centralização de logs (Grafana Loki ou ELK)

**Stack recomendada para produção:**

| Stack | Custo | Uso ideal |
|---|---|---|
| **Loki + Promtail + Grafana** | Baixo | Logs em JSON do stdout; indexação por labels |
| **ELK (Elasticsearch + Logstash + Kibana)** | Alto | Full-text search de stack traces, dashboards complexos |

Com logging estruturado no stdout, a tabela `system_logs` e `error_logs` no banco relacional torna-se desnecessária — o banco fica reservado exclusivamente para dados de negócio.

---

### 5.6 Arquitetura de eventos para auditoria (Event Sourcing leve via Kafka)

O projeto já possui Kafka + Outbox Pattern. Uma evolução natural:

```
Services → OutboxService → Kafka (tópico "audit-events") → Consumer dedicado → Elasticsearch / OpenSearch
```

Isso desacopla completamente a auditoria da aplicação principal. O consumer pode ser um microserviço separado com índices otimizados para consulta de auditoria.

---

## 6. Diagrama resumido do fluxo atual

```
┌─────────────────────────────────────────────────────────────────────┐
│                        SYSTEM LOG FLOW                              │
│                                                                     │
│  Services (Product / Order / Movement / User / Person / Email)      │
│      │                                                              │
│      └── SystemLogPublisherService.publish(eventType, desc,         │
│                                            oldData, newData)        │
│              │                                                      │
│              ├── toJson(LoggableData)  ← ObjectMapper (instância    │
│              │                           local, não bean Spring)    │
│              └── ApplicationEventPublisher.publishEvent(            │
│                        SystemLogEvent)                              │
│                              │                                      │
│                              ▼  (síncrono, mesma thread/transação)  │
│                  SystemLogListener.handleLogEvent()                 │
│                      └── SystemLogRepository.save()                 │
│                              └── INSERT INTO system_logs            │
│                                                                     │
│  ⚠️  Sem API de leitura  │  Sem índices  │  Write-only              │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                        ERROR LOG FLOW                               │
│                                                                     │
│  Any Exception (thrown by services)                                 │
│      │                                                              │
│      ▼  (propaga até @RestControllerAdvice)                         │
│  ErrorHandler.logError(ex, request)                                 │
│      │                                                              │
│      ├── log.error(...)   ← SLF4J/Logback  (único uso de           │
│      │                       logging estruturado no projeto)        │
│      └── ErrorLogPublisherService.publish(                          │
│                errorType, message,                                  │
│                StackTraceUtils.formatStackTrace(ex, 10),            │
│                request.getRequestURI())                             │
│                  │                                                  │
│                  └── ApplicationEventPublisher.publishEvent(        │
│                            ErrorLogEvent)                           │
│                                  │                                  │
│                                  ▼  (síncrono)                      │
│                      ErrorLogListener.handleErrorEvent()            │
│                          └── ErrorLogRepository.save()              │
│                                  └── INSERT INTO error_logs         │
│                                                                     │
│  ⚠️  EmailException nunca chega aqui (bug: @ExceptionHandler        │
│      ausente no handleEmailException)                               │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                     CONSUMO DOS LOGS                                │
│                                                                     │
│  system_logs  →  [NENHUMA API]  →  Somente via acesso direto ao BD │
│  error_logs   →  [NENHUMA API]  →  Somente via acesso direto ao BD │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Resumo executivo dos problemas prioritários

| Prioridade | Problema | Impacto |
|---|---|---|
| 🔴 Alta | `UserLogData` expõe hash bcrypt da senha no banco | Segurança / privacidade |
| 🔴 Alta | `@EventListener` síncrono — risco de inconsistência transacional | Confiabilidade |
| 🔴 Alta | Bug: `handleEmailException` sem `@ExceptionHandler` | Comportamento incorreto em produção |
| 🟡 Média | `ObjectMapper` não injetado como bean | Configuração incorreta / bugs sutis de serialização |
| 🟡 Média | Ausência de índices em `system_logs` e `error_logs` | Performance com volume de dados |
| 🟡 Média | Nenhuma API de consulta de logs | Observabilidade inexistente |
| 🟢 Baixa | Typos nas mensagens de description | Qualidade / legibilidade dos logs |
| 🟢 Baixa | Nomenclatura inconsistente de `eventType` | Manutenibilidade |
| 🟢 Baixa | Pacote `messaging` sem mensageria real | Nomenclatura enganosa |

---

*Análise gerada em Abril/2026 por varredura completa do repositório.*
