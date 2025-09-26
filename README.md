# Recicar - Car Parts Marketplace

A comprehensive web application that connects customers with junkyards and auto parts sellers, enabling online purchase of used and new car parts from multiple vendors.

## 🚀 Project Status


## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.2.0 with Java 21
- **Frontend**: Thymeleaf for server-side rendering
- **Database**: PostgreSQL 15+ with Supabase (production) and local PostgreSQL (development)
- **Database Migrations**: Flyway
- **Caching**: Redis (optional)
- **Build Tool**: Gradle 8.5
- **Containerization**: Docker & Docker Compose
- **Security**: Spring Security with role-based access control
- **Deployment**: Northflank with Supabase database


## 🚀 Getting Started

### Prerequisites

- Java 21 or higher
- Docker and Docker Compose
- Git

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/oricha/recicar.git
   cd recicar
   ```

2. **Start the database and Redis**
   ```bash
   docker compose up -d   # new style
   ```

3. **Run the application**
   ```bash
  For local development: ./gradlew runLocal
  For test environment: ./gradlew runTest
  For production environment: ./gradlew runProd
   ```


3. **Run the Cucumber tests**
   ```bash
      - Local: ./gradlew cukeLocalRun
      - Remote: ./gradlew cukeRemote
   ```



4. **Access the application**
   - Application: http://localhost:8080
   - Health Check: http://localhost:8080/actuator/health

### Environments

- **Local Development** (local PostgreSQL):
  - `./gradlew runLocal`

- **Test Environment** (DEV profile + Neon DB):
  - Add Neon DB creds in `.env` as `TEST_DATABASE_URL`, `TEST_DATABASE_USERNAME`, `TEST_DATABASE_PASSWORD`.
  - `./gradlew runTest`

- **Production Environment** (PROD profile + Neon DB):
  - Add Neon DB creds in `.env` as `PROD_DATABASE_URL`, `PROD_DATABASE_USERNAME`, `PROD_DATABASE_PASSWORD`.
  - `./gradlew runProd`

### Database Migrations

- **Development DB**: `./gradlew flywayMigrateDev`
- **Test DB**: `./gradlew flywayMigrateTest`
- **Production DB**: `./gradlew flywayMigrateProd`



## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/recicar/marketplace/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # Web controllers
│   │   ├── service/         # Business logic
│   │   ├── repository/      # Data access layer
│   │   ├── entity/          # JPA entities
│   │   └── dto/             # Data transfer objects
│   └── resources/
│       ├── db/migration/    # Flyway database migrations
│       ├── templates/       # Thymeleaf templates
│       └── static/          # Static web assets
```

## 🧪 Testing

Run tests with:
```bash
./gradlew test
```

## 🐳 Docker

The project includes Docker Compose configuration for local development:
- PostgreSQL 15 database
- Redis cache (optional)
- Automatic database initialization



## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For support and questions, please open an issue in the GitHub repository.
You can write to me at linkedin.com/in/oricha or email me at oricha@gmmail.com
