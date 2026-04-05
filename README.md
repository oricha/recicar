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

- **Test Environment** (perfil `test` + PostgreSQL en Dokploy o local):
  - En Dokploy test: Postgres en la misma red; `SPRING_PROFILES_ACTIVE=test` y `DATABASE_*` al servicio interno.
  - Localmente: credenciales en `.env` como `TEST_DATABASE_*` (ver `.env.example`).  
  - `./gradlew runTest`

- **Production Environment** (perfil `prod` + PostgreSQL gestionado):
  - Credenciales en `.env` como `PROD_DATABASE_URL`, `PROD_DATABASE_USERNAME`, `PROD_DATABASE_PASSWORD`.
  - `./gradlew runProd`

### PostgreSQL local (dev) y esquema `recicar`

En **PostgreSQL 15+** muchos usuarios no tienen `CREATE` en el esquema `public`. La app en perfil **dev** usa el esquema **`recicar`**: Flyway lo crea (`create-schemas`) y las migraciones/JPA escriben ahí. Requisito: **`marketplace_user` debe ser dueño de la base** (o tener `CREATE` en la base), por ejemplo:

```sql
CREATE DATABASE marketplace_dev OWNER marketplace_user;
```

Si defines `DATABASE_URL` en `.env`, añade el esquema en la URL, p. ej.  
`...?currentSchema=recicar` (o `&currentSchema=recicar` si ya hay parámetros).

### Si prefieres seguir usando solo `public`

Conéctate como superusuario a `marketplace_dev` y ejecuta:

```sql
GRANT CREATE, USAGE ON SCHEMA public TO marketplace_user;
ALTER SCHEMA public OWNER TO marketplace_user;
```

Script de referencia: `docs/postgres-local-dev-setup.sql` (ajusta si `CREATE USER` / `CREATE DATABASE` ya existen).

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
## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Reference API CAR

https://smartcar.com/docs/getting-started/introduction