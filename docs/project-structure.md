<h1 align="center"> Organização completa do Projeto</h1>

```plaintext
.github/workflows
 └── ci.yml

src/main
 ├── java/com/github/renanc/vitor/inventory
 │    ├── controller
 │    │    ├── AuthenticationController.java
 │    │    ├── PersonController.java
 │    │    ├── ProductController.java
 │    │    └── UserController.java
 │    ├── domain
 │    │    ├── entity
 │    │    │    ├── category
 │    │    │    │    └── CategoryEntity.java
 │    │    │    ├── errorlog
 │    │    │    │    ├── ErrorLog.java
 │    │    │    │    └── ErrorLogEvent.java
 │    │    │    ├── movement
 │    │    │    │    ├── Movement.java
 │    │    │    │    └── MovementTypeEntity.java
 │    │    │    ├── person
 │    │    │    │    └── Person.java
 │    │    │    ├── product
 │    │    │    │    └── Product.java
 │    │    │    ├── systemlog
 │    │    │         ├── SystemLog.java
 │    │    │    │    └── SystemLogEvent.java
 │    │    │    └── user
 │    │    │         ├── PermissionEntity.java
 │    │    │         ├── User.java
 │    │    │         └── UserTypeEntity.java
 │    │    └── enums
 │    │         ├── category
 │    │         │    └── CategoryEnum.java
 │    │         ├── movement
 │    │         │    └── MovementTypeEnum.java
 │    │         └── user
 │    │              ├── PermissionEnum.java
 │    │              └── UserTypeEnum.java
 │    ├── dto
 │    │    ├── authentication
 │    │    │    ├── JWTTokenData.java
 │    │    │    └── LoginData.java
 │    │    ├── movement
 │    │    │    ├── MovementData.java
 │    │    │    ├── MovementDetailData.java
 │    │    │    ├── MovementListiningData.java
 │    │    │    ├── MovementLogData.java
 │    │    │    └── MovementRequest.java
 │    │    ├── person
 │    │    │    ├── PersonCreationData.java
 │    │    │    ├── PersonDetailData.java
 │    │    │    ├── PersonListiningData.java
 │    │    │    ├── PersonLogData.java
 │    │    │    └── PersonUserCreationData.java
 │    │    ├── product
 │    │    │    ├── InputProductResponse.java
 │    │    │    ├── OutputProductResponse.java
 │    │    │    ├── ProductCreationData.java
 │    │    │    ├── ProductDetailData.java
 │    │    │    ├── ProductListingData.java
 │    │    │    ├── ProductLogData.java
 │    │    │    └── ProductUpdateData.java
 │    │    └── user
 │    │         ├── UserCreationData.java
 │    │         ├── UserDetailData.java
 │    │         ├── UserListiningData.java
 │    │         ├── UserLogData.java
 │    │         ├── UserLogPasswordData.java
 │    │         ├── UserPasswordUpdateData.java
 │    │         ├── UserSummaryData.java
 │    │         └── UserTypeUpdateData.java
 │    ├── exception
 │    │    ├── factory
 │    │    │    ├── NotFoundExceptionFactory.java
 │    │    │    └── ValidationExceptionFactory.java
 │    │    ├── handler
 │    │    │    └── ErrorHandler.java
 │    │    ├── model
 │    │    │    ├── ApiError.java
 │    │    │    └── DataValidationError.java
 │    │    ├── types
 │    │    │    ├── auth
 │    │    │    │    └── AuthorizationException.java
 │    │    │    ├── common
 │    │    │    │    ├── EntityNotFoundException.java
 │    │    │    │    ├── JsonSerializationException.java
 │    │    │    │    └── ValidationException.java
 │    │    │    ├── product
 │    │    │    │    ├── DuplicateProductException.java
 │    │    │    │    ├── InsufficientStockException.java
 │    │    │    │    └── InvalidQuantityException.java
 │    │    │    └── user
 │    │    │         └── AccessDeniedException.java
 │    ├── infra
 │    │    ├── config
 │    │    │    └── WebConfig.java
 │    │    ├── documentation
 │    │    │    └── SpringDocConfigurations.java
 │    │    ├── logging
 │    │    │    ├── LogAspect.java
 │    │    │    ├── Loggable.java
 │    │    │    └── Loggables.java
 │    │    ├── messaging
 │    │    │    ├── errorlog
 │    │    │    │    ├── ErrorLogListener.java
 │    │    │    │    └── ErrorLogPublisherService.java
 │    │    │    └── systemlog
 │    │    │         ├── LoggableData.java
 │    │    │         ├── SystemLogListener.java
 │    │    │         └── SystemLogPublisherService.java
 │    │    └── security
 │    │         ├── SecurityConfiguration.java
 │    │         └── SecurityFilter.java
 │    ├── repository
 │    │    ├── CategoryRepository.java
 │    │    ├── ErrorLogRepository.java
 │    │    ├── MovementRepository.java
 │    │    ├── MovementTypeRepository.java
 │    │    ├── PersonRepository.java
 │    │    ├── ProductRepository.java
 │    │    ├── SystemLogRepository.java
 │    │    ├── UserRepository.java
 │    │    └── UserTypeRepository.java
 │    ├── service
 │    │    ├── AuthenticationService.java
 │    │    ├── MovementService.java
 │    │    ├── PersonService.java
 │    │    ├── ProductService.java
 │    │    ├── TokenService.java
 │    │    └── UserService.java
 │    ├── utils
 │    │    ├── CustomPage.java
 │    │    ├── PageMapper.java
 │    │    └── StackTraceUtils.java
 │    └── InventoryNotificationSystemBackendApplication.java
 ├── resources
 │    ├── db
 │    │    ├── V1__create_table_system_logs.sql
 │    │    ├── V2__create_table_people.sql
 │    │    ├── V3__create_table_permissions.sql
 │    │    ├── V4__create_table_user_types.sql
 │    │    ├── V5__create_table_user_types_permissions.sql
 │    │    ├── V6__seed_user_types_and_permissions.sql
 │    │    ├── V7__create_table_users.sql
 │    │    ├── V8__create_table_categories.sql
 │    │    ├── V9__create_table_products.sql
 │    │    ├── V10__seed_categories.sql
 │    │    ├── V11__create_table_error_logs.sql
 │    │    ├── V12__insert_admin_user.sql
 │    │    ├── V13__create_table_movement_types.sql
 │    │    ├── V14__create_table_movements.sql
 │    │    └── V15__seed_movement_types.sql
 │    ├── application-dev.properties
 │    ├── application-prod.properties
 │    ├── application-test.properties
 │    └── application.properties
 ├── test/java/com/github/renancvitor/inventory
 │    └── (future tests)
 ├── LICENSE
 └── README.md
 ```

 > Estrutura atualizada em: Outubro/2025