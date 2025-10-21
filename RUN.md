# Recicar Deployment Guide

## 🚀 Deploy to Dokploy (Recommended)

This app is configured to deploy to Dokploy using Docker images built and pushed via GitHub Actions to Docker Hub.

### Prerequisites

1. **Docker Hub Account** - Sign up at [hub.docker.com](https://hub.docker.com)
2. **GitHub Repository** - Your code should be in GitHub
3. **Dokploy Instance** - Have Dokploy installed and running

### Quick Setup

#### 1. Configure Docker Hub
- Create a repository named `recicar` in Docker Hub
- Generate an access token: **Account Settings → Security → Access Tokens**

#### 2. Configure GitHub Secrets
Go to **GitHub Repo → Settings → Secrets → Actions** and add:
- `DOCKERHUB_USERNAME` - Your Docker Hub username
- `DOCKERHUB_TOKEN` - Your Docker Hub access token

#### 3. Push to GitHub
```bash
git push origin main
```

The GitHub Actions workflow will automatically:
- Build the Docker image
- Push it to Docker Hub with tags: `latest` and `main-<commit-sha>`

#### 4. Configure Dokploy

**Application Settings:**
- **Source Type**: Docker
- **Docker Image**: `YOUR_DOCKERHUB_USERNAME/recicar:latest`

**Environment Variables:**
```env
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://your-host:5432/marketplace_prod
DATABASE_USERNAME=your_db_user
DATABASE_PASSWORD=your_db_password
JWT_SECRET=your_secure_jwt_secret
SERVER_PORT=8080
```

**Domain:**
- Click **Dice icon** to generate domain or add custom domain
- Port: `8080`

**Health Check Configuration** (Advanced → Swarm Settings):
```json
{
  "Test": ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"],
  "Interval": 30000000000,
  "Timeout": 10000000000,
  "StartPeriod": 30000000000,
  "Retries": 3
}
```

**Update Config** (for automatic rollbacks):
```json
{
  "Parallelism": 1,
  "Delay": 10000000000,
  "FailureAction": "rollback",
  "Order": "start-first"
}
```

#### 5. Deploy
- Click **Deploy** in Dokploy
- Access your application via the configured domain

### Auto-Deploy Setup

**Using Docker Hub Webhook:**
1. In Dokploy: **Application → Deployments → Copy Webhook URL**
2. In Docker Hub: **Repository → Webhooks → Create Webhook**
3. Paste the Webhook URL and save

Now every push to `main` will automatically:
1. Build and push Docker image via GitHub Actions
2. Trigger Dokploy deployment via webhook
3. Deploy with zero downtime and automatic rollback on failure

### 📚 Full Documentation

- **Quick Setup**: `.github/DOKPLOY_SETUP.md`
- **Complete Guide**: `DEPLOYMENT.md`
- **GitHub Actions**: `.github/README.md`

---

## 🏠 Local Development

### Using Docker Compose
```bash
# Start PostgreSQL
docker-compose up -d postgres

# Run migrations
./gradlew flywayMigrate

# Run application
./gradlew bootRun
```

### Using Gradle
```bash
# Run with dev profile (local PostgreSQL)
./gradlew runLocal

# Run with test profile (Neon DB from .env)
./gradlew runTest

# Run with prod profile (Neon DB from .env)
./gradlew runProd
```

### Environment Setup
1. Copy `.env.example` to `.env` (if available)
2. Fill in your database credentials
3. Never commit `.env` to version control

### Database Migrations
```bash
# Migrate dev database
./gradlew flywayMigrateDev

# Migrate test database
./gradlew flywayMigrateTest

# Migrate prod database
./gradlew flywayMigrateProd
```

---

## 🧪 Testing

### Run Tests
```bash
./gradlew test
```

### Run Cucumber Tests
```bash
./gradlew cucumber
```

---

## 🔧 Build

### Build JAR
```bash
./gradlew bootJar
```

The JAR will be in `build/libs/recicar-0.0.1-SNAPSHOT.jar`

### Build Docker Image Locally
```bash
docker build -t recicar:local .
```

### Run Docker Image Locally
```bash
docker run -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/marketplace_dev \
  -e DATABASE_USERNAME=marketplace_user \
  -e DATABASE_PASSWORD=marketplace_pass \
  -e SPRING_PROFILES_ACTIVE=dev \
  recicar:local
```

---

## 📊 Monitoring

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

### Metrics
```bash
curl http://localhost:8080/actuator/metrics
```

### Info
```bash
curl http://localhost:8080/actuator/info
```

---

## 🐛 Troubleshooting

### Port Already in Use
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>
```

### Database Connection Issues
- Verify PostgreSQL is running
- Check credentials in `.env` or environment variables
- Ensure database exists
- Check firewall/network settings

### Build Issues
```bash
# Clean build
./gradlew clean build

# Clear Gradle cache
rm -rf ~/.gradle/caches/
```

---

**Need Help?** Check the full deployment documentation in `DEPLOYMENT.md`
