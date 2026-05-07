# Recicar Deployment Guide

## 🚀 Deploy to Dokploy (Recommended)

This app is configured to deploy to Dokploy using Docker images built and pushed via GitHub Actions to **GitHub Container Registry** (`ghcr.io`).

### Prerequisites

1. **GitHub Repository** - Your code should be on GitHub
2. **Actions permissions** - Repo **Settings → Actions → General**: allow **Read and write permissions** (or equivalent so `GITHUB_TOKEN` can push packages)
3. **Dokploy Instance** - Have Dokploy installed and running

### Quick Setup

#### 1. GitHub (no Docker Hub)
- Ensure workflow permissions allow publishing **Packages** (see Prerequisites).
- The workflow uses `GITHUB_TOKEN`; you do **not** need `DOCKERHUB_*` secrets for the default setup.

#### 2. Push to GitHub
```bash
git push origin main
```

The GitHub Actions workflow will automatically:
- Build the Docker image
- Push it to **GHCR** as `ghcr.io/<owner-lowercase>/<repo-lowercase>:latest` and `main-<commit-sha>`

#### 3. Configure Dokploy

**Application Settings:**
- **Source Type**: Docker
- **Docker Image**: `ghcr.io/your-github-org-or-user/recicar:latest` (use your real GitHub `owner/repo` path in **lowercase**)

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

#### 4. Deploy
- Click **Deploy** in Dokploy
- Access your application via the configured domain

### Auto-Deploy Setup

GHCR does not provide Docker Hub-style webhooks. Typical options:

1. **Dokploy deploy webhook / API** — call it from an extra step at the end of `.github/workflows/deploy.yml` (store the URL or API key as GitHub secrets), or  
2. **Manual / scheduled** redeploy in Dokploy after a successful Actions run.

See `DEPLOYMENT.md` for an example `curl` step using `DOKPLOY_DEPLOY_HOOK_URL`.

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

# Run with test profile (PostgreSQL test desde .env TEST_*; en Dokploy usa DATABASE_* hacia el Postgres del stack)
./gradlew runTest

# Run with prod profile (PostgreSQL prod desde .env PROD_*)
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
