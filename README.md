<h1 align="center"> Inventory Notification System — Backend <br>
<img src="https://img.shields.io/badge/Status-In%20Progress-yellow" width="150" height="30" />
</h1>

---

<h2 align="center"> 🔗 Frontend</h2>
The frontend will be developed separately. Once ready, it will be available in a dedicated repository:

- 🌐 [Inventory Notification System — Frontend](#)  <!-- update link when available -->

---

<h2 align="center"> Project Overview</h2>
**Inventory Notification System** is a backend built with **Spring Boot**, designed to manage stock and send notifications for low inventory or expiring products.  
This project follows **layered architecture** (controller, service, repository, model) and applies best practices for code organization, scalability, and maintainability.

---

<h2 align="center"> Technologies Used</h2>

- ☕ Java 17+ + 🌱 Spring Boot 3
- 📦 [JPA](https://spring.io/projects/spring-data-jpa) + 🛠️ [Hibernate](https://hibernate.org/)
- 📦 Maven: Dependency and build management
- 🐘 PostgreSQL: Database
- 🛠️ Database version control with [Flyway](https://flywaydb.org/)
- 🔧 Lombok
- 🔄 Spring Boot DevTools
- 🌐 Spring Web
- ✅ Validation (Bean Validation)
- 📄 [Swagger (OpenAPI)](https://swagger.io/specification/)
- 🐧 [WSL](https://ubuntu.com/desktop/wsl) and 🐳 [Docker CLI](https://www.docker.com/products/cli/)

---

<h2 align="center"> Project Structure</h2>

```plaintext
src/main
├── java/com/github/renancvitor/inventory
│ ├── controller/
│ ├── service/
│ ├── repository/
│ ├── model/
│ └── InventoryNotificationSystemApplication.java
├── resources
│ └── application.properties
└── test/java/com/github/renancvitor/inventory
└── (future tests)
```

---

<h2 align="center"> How to Run</h2>
### Prerequisites
- Java 17+
- PostgreSQL running locally
- Maven installed

### Steps
1. Clone the repository:
```bash
git clone git@github.com:renancvitor/inventory-notification-system-backend.git
```
2. Go to project folder:
```bash
cd inventory-notification-system-backend
```
3. Configure database in `src/main/resources/application.properties`
4. Run the application with Maven
```bash
./mvnw spring-boot:run
```
5. Access the API at `http://localhost:8080` (default)

---

<h2 align="center"> Contributions</h2>

If you want to contribute to the project, follow these steps:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/alguma-coisa`).
3. Make your changes.
4. Open a Pull Request describing your changes.

Obrigado pelo interesse em contribuir!

---

<h2 align="center"> Contact</h2>

If you have any questions or suggestions, please feel free to contact me:

- 📧 **E-mail**: [renan.vitor.cm@gmail.com](mailto:renan.vitor.cm@gmail.com)
- 🟦 **LinkedIn**: [Renan Vitor](https://www.linkedin.com/in/renan-vitor-developer/)

---

<h2 align="center"> Licence</h2>

📌 This project is licensed under the [MIT License](LICENSE), which means you can use, modify, share and distribute it freely, as long as you keep the authors' credits and include a copy of the original license - see the [LICENSE](LICENSE) file for details or access the [official MIT license](https://opensource.org/licenses/MIT).