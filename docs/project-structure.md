<h1 align="center"> Organização completa do Projeto</h1>

```plaintext
.github/workflows
 └── ci.yml

 docs
 ├── insomnia
 │    └── insomnia-api-export
 └── project-structure.md

src/main
 ├── java/com/github/renanc/vitor/inventory
 │    ├── application
 │    │    ├── authentication
 │    │    │    ├── controller
 │    │    │    │    └── AuthenticationController.java
 │    │    │    ├── dto
 │    │    │    │    ├── JWTTokenData.java
 │    │    │    │    └── LoginData.java
 │    │    │    └── service
 │    │    │         ├── AuthenticationService.java
 │    │    │         └── TokenService.java
 │    │    ├── category
 │    │    │    └── repository
 │    │    │         └── CategoryRepository.java
 │    │    ├── email
 │    │    │    ├── dto
 │    │    │    │    ├── EmailLogData.java
 │    │    │    │    ├── EmailRequest.java
 │    │    │    │    └── EmailResponse.java
 │    │    │    └── service
 │    │    │         └── EmailService.java
 │    │    ├── errorlog
 │    │    │    └── repository
 │    │    │         └── ErrorLogRepository.java
 │    │    ├── movement
 │    │    │    ├── dto
 │    │    │    │    ├── MovementData.java
 │    │    │    │    ├── MovementDetailData.java
 │    │    │    │    ├── MovementListingData.java
 │    │    │    │    ├── MovementLogData.java
 │    │    │    │    ├── MovementOrderRequest.java
 │    │    │    │    ├── MovementRequest.java
 │    │    │    │    └── MovementWithOrderRequest.java
 │    │    │    ├── repository
 │    │    │    │    ├── MovementRepository.java
 │    │    │    │    └── MovementTypeRepository.java
 │    │    │    └── service
 │    │    │         ├── MovementReportService.java
 │    │    │         ├── MovementService.java
 │    │    │         └── ReportScheduler.java
 │    │    ├── order
 │    │    │    ├── controller
 │    │    │    │    └── OrderController.java
 │    │    │    ├── dto
 │    │    │    │    ├── OrderCreationData.java
 │    │    │    │    ├── OrderDetailData.java
 │    │    │    │    ├── OrderFilter.java
 │    │    │    │    ├── OrderItemRequest.java
 │    │    │    │    ├── OrderLogData.java
 │    │    │    │    └── OrderUpdateData.java
 │    │    │    ├── repository
 │    │    │    │    ├── OrderItemRepository.java
 │    │    │    │    ├── OrderRepository.java
 │    │    │    │    ├── OrderSpecifications.java
 │    │    │    │    └── OrderStatusRepository.java
 │    │    │    └── service
 │    │    │         └── OrderService.java
 │    │    ├── person
 │    │    │    ├── controller
 │    │    │    │    └── PersonController.java
 │    │    │    ├── dto
 │    │    │    │    ├── PersonCreationData.java
 │    │    │    │    ├── PersonDetailData.java
 │    │    │    │    ├── PersonListingData.java
 │    │    │    │    ├── PersonLogData.java
 │    │    │    │    └── PersonUserCreationData.java
 │    │    │    ├── repository
 │    │    │    │    └── PersonRepository.java
 │    │    │    └── service
 │    │    │         └── PersonService.java
 │    │    ├── product
 │    │    │    ├── controller
 │    │    │    │    └── ProductController.java
 │    │    │    ├── dto
 │    │    │    │    ├── InputProductResponse.java
 │    │    │    │    ├── OutputProductResponse.java
 │    │    │    │    ├── ProductCreationData.java
 │    │    │    │    ├── ProductDetailData.java
 │    │    │    │    ├── ProductFilter.java
 │    │    │    │    ├── ProductListingData.java
 │    │    │    │    ├── ProductLogData.java
 │    │    │    │    └── ProductUpdateData.java
 │    │    │    ├── repository
 │    │    │    │    ├── ProductRepository.java
 │    │    │    │    └── ProductSpecifications.java
 │    │    │    └── service
 │    │    │         ├── ProductService.java
 │    │    │         └── StockMonitorService.java
 │    │    ├── systemlog
 │    │    │    └── repository
 │    │    │         └── SystemLogRepository.java
 │    │    └── user
 │    │         ├── controller
 │    │         │    └── UserController.java
 │    │         ├── dto
 │    │         │    ├── UserCreationData.java
 │    │         │    ├── UserDetailData.java
 │    │         │    ├── UserListingData.java
 │    │         │    ├── UserLogData.java
 │    │         │    ├── UserLogPasswordData.java
 │    │         │    ├── UserPasswordUpdateData.java
 │    │         │    ├── UserSummaryData.java
 │    │         │    └── UserTypeUpdateData.java
 │    │         ├── repository
 │    │         │    ├── UserRepository.java
 │    │         │    └── UserTypeRepository.java
 │    │         └── service
 │    │              └── UserService.java
 │    ├── domain
 │    │    └── entity
 │    │         ├── category
 │    │         │    ├── enums
 │    │         |    │    └── CategoryEnum.java
 │    │         │    └── CategoryEntity.java
 │    │         ├── errorlog
 │    │         │    ├── ErrorLog.java
 │    │         │    └── ErrorLogEvent.java
 │    │         ├── movement
 │    │         │    ├── enums
 │    │         |    │    └── MovementTypeEnum.java
 │    │         │    ├── Movement.java
 │    │         │    └── MovementTypeEntity.java
 │    │         ├── order
 │    │         │    ├── enums
 │    │         |    │    └── OrderStatusEnum.java
 │    │         │    ├── exception
 │    │         │    │    └── OrderStatusException.java
 │    │         │    ├── Order.java
 │    │         │    ├── OrderItem.java
 │    │         │    └── OrderStatusEntity.java
 │    │         ├── person
 │    │         │    └── Person.java
 │    │         ├── product
 │    │         │    ├── exception
 │    │         │    │    ├── DuplicateProductException.java
 │    │         │    │    ├── InsufficientStockException.java
 │    │         │    │    └── InvalidQuantityException.java
 │    │         │    └── Product.java
 │    │         ├── systemlog
 │    │         │    ├── SystemLog.java
 │    │         │    └── SystemLogEvent.java
 │    │         └── user
 │    │              ├── enums
 │    │              │    ├── PermissionEnum.java
 │    │              │    └── UserTypeEnum.java
 │    │              ├── exception
 │    │              │    └── AccessDeniedException.java
 │    │              ├── PermissionEntity.java
 │    │              ├── User.java
 │    │              └── UserTypeEntity.java
 │    ├── exception
 │    │    ├── factory
 │    │    │    ├── NotFoundExceptionFactory.java
 │    │    │    └── ValidationExceptionFactory.java
 │    │    ├── handler
 │    │    │    └── ErrorHandler.java
 │    │    ├── model
 │    │    │    ├── ApiError.java
 │    │    │    └── DataValidationError.java
 │    │    └── types
 │    │         ├── auth
 │    │         │    └── AuthorizationException.java
 │    │         ├── common
 │    │         │    ├── EntityNotFoundException.java
 │    │         │    ├── JsonSerializationException.java
 │    │         │    └── ValidationException.java
 │    │         └── email
 │    │              └── EmailException.java
 │    ├── infra
 │    │    ├── config
 │    │    │    ├── AwsSesConfig.java
 │    │    │    └── WebConfig.java
 │    │    ├── documentation
 │    │    │    └── SpringDocConfigurations.java
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
 │    │    ├── V13__create_table_order_status.sql
 │    │    ├── V14__create_table_orders.sql
 │    │    ├── V15__seed_order_status.sql
 │    │    ├── V16__create_table_movement_types.sql
 │    │    ├── V17__create_table_movements.sql
 │    │    ├── V18__seed_movement_types.sql
 │    │    └── V19__create_table_order_item.sql
 │    ├── application-dev.properties
 │    ├── application-prod.properties
 │    ├── application-secret.properties                               # Oculto, possui dados sensíveis
 │    └── application.properties
 ├── test/java/com/github/renancvitor/inventory
 │    ├── controller
 │    │    ├── authentication
 │    │    │    └── AuthenticationControllerTests.java
 │    │    ├── order
 │    │    │    ├── OrderControllerApproveTests.java
 │    │    │    ├── OrderControllerCreationTests.java
 │    │    │    ├── OrderControllerListTests.java
 │    │    │    ├── OrderControllerRejectTests.java
 │    │    │    └── OrderControllerUpdateTests.java
 │    │    ├── person
 │    │    │    ├── PersonControllerActivateTests.java
 │    │    │    ├── PersonControllerCreateTests.java
 │    │    │    ├── PersonControllerDeleteTests.java
 │    │    │    └── PersonControllerListTests.java
 │    │    ├── product
 │    │    │    ├── ProductControllerActivateTests.java
 │    │    │    ├── ProductControllerCreateTests.java
 │    │    │    ├── ProductControllerDeleteTests.java
 │    │    │    ├── ProductControllerListTests.java
 │    │    │    └── ProductControllerUpdateTests.java
 │    │    └── user
 │    │         ├── UserControllerActivateTests.java
 │    │         ├── UserControllerDeleteTests.java
 │    │         ├── UserControllerListTests.java
 │    │         ├── UserControllerUpdatePasswordTests.java
 │    │         └── UserControllerUpdateUserTypeTests.java
 │    ├── service
 │    │    ├── authentication
 │    │    │    ├── AuthenticationServiceTests.java
 │    │    │    └── TokenServiceTests.java
 │    │    ├── email
 │    │    │    └── EmailServiceTests.java
 │    │    ├── movement
 │    │    │    ├── MovementReportServiceTests.java
 │    │    │    └── MovementServiceTests.java
 │    │    ├── order
 │    │    │    ├── OrderServiceApproveTests.java
 │    │    │    ├── OrderServiceCreateTests.java
 │    │    │    ├── OrderServiceListTests.java
 │    │    │    ├── OrderServiceRejectTests.java
 │    │    │    └── OrderServiceUpdateTests.java
 │    │    ├── person
 │    │    │    ├── PersonServiceActivateTests.java
 │    │    │    ├── PersonServiceCreateTests.java
 │    │    │    ├── PersonServiceDeleteTests.java
 │    │    │    └── PersonServiceListTests.java
 │    │    ├── product
 │    │    │    ├── ProductServiceActivateTests.java
 │    │    │    ├── ProductServiceCreateTests.java
 │    │    │    ├── ProductServiceDeleteTests.java
 │    │    │    ├── ProductServiceListTests.java
 │    │    │    └── ProductServiceUpdateTests.java
 │    │    └── user
 │    │         ├── UserServiceActivateTests.java
 │    │         ├── UserServiceDeleteTests.java
 │    │         ├── UserServiceListTests.java
 │    │         ├── UserServiceUpdatePasswordTests.java
 │    │         └── UserServiceUpdateUserTypeTests.java
 │    ├── utils
 │    │    └── TestEntityFactory.java
 │    └── InventoryNotificationSystemBackendApplicationTests.java
 ├── resources
 │    ├── payload
 │    │    ├── order-creation.jsonl
 │    │    ├── person-user-creation.jsonl
 │    │    └── product-creation.jsonl
 │    ├── application-test-secret.properties
 │    └── application-test.properties
 ├── LICENSE
 └── README.md
 ```

 > Estrutura atualizada em: Dezembro/2025