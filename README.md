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

- **Development** (local PostgreSQL):
  - `./gradlew runLocal`

- **Production** (Supabase DB):
  - Add Supabase DB creds in `.env` as `PROD_DATABASE_URL`, `PROD_DATABASE_USERNAME`, `PROD_DATABASE_PASSWORD`.
  - `./gradlew runProd`

### Database Migrations

- **Development DB**: `./gradlew flywayMigrateDev`
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

## 🚀 Deployment

### Northflank Deployment

The application is configured for deployment on Northflank with Supabase as the database:

1. **Configure environment variables** in Northflank:
   - `SPRING_PROFILES_ACTIVE=prod`
   - `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`
   - `SERVER_PORT=8080`

2. **Deploy using northflank.yaml**:
   - Upload the `northflank.yaml` file to your project
   - Configure the service using the YAML configuration

3. **Or use Docker**:
   ```bash
   docker build -t your-registry/recicar-marketplace:latest .
   # Deploy using the Docker image in Northflank
   ```

For detailed deployment instructions, see:
- [Northflank Deployment Guide](NORTHFLANK_DEPLOYMENT_GUIDE.md)

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
