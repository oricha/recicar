# Recicar - Car Parts Marketplace

A comprehensive web application that connects customers with junkyards and auto parts sellers, enabling online purchase of used and new car parts from multiple vendors.

## 🚀 Project Status


## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.2.0 with Java 21
- **Frontend**: Thymeleaf for server-side rendering
- **Database**: PostgreSQL 15+ with Flyway migrations
- **Caching**: Redis
- **Build Tool**: Gradle 8.5
- **Containerization**: Docker & Docker Compose
- **Security**: Spring Security with role-based access control


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
   ./gradlew bootRun
   ```

4. **Access the application**
   - Application: http://localhost:8080
   - Health Check: http://localhost:8080/actuator/health

### Default Admin User

The application automatically creates an admin user on first startup:
- **Email**: `admin@recicar.com`
- **Password**: Check `DataInitializer.java` for the default password (change in production!)

## 🗄️ Database Management

The application uses Flyway for database migrations. Migrations are automatically applied on startup.

- **Database URL**: `jdbc:postgresql://localhost:5432/marketplace_dev`
- **Username**: `marketplace_user`
- **Password**: Set via environment variables (see `.env.example`)

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
- Redis cache
- Automatic database initialization

## 📝 API Documentation

REST API endpoints available at `/api/*`:
- `/api/search/*` - Search operations
- `/api/products/*` - Product operations
- `/api/categories/*` - Category operations

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