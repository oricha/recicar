### 🚀 Deploy to Koyeb (Docker Registry)

This app can be deployed to Koyeb using a Docker image pushed to the Koyeb Container Registry.

1. Prepare environment
    - Copy `.env.example` to `.env` and fill values (never commit real secrets)
    - Ensure the following are available as environment variables locally or in CI Secrets:
        - `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`
        - `KOYEB_REGISTRY_USERNAME`, `KOYEB_REGISTRY_PASSWORD`
        - `KOYEB_API_TOKEN` (optional, for CI redeploys)

2. Build and push image (local)
    - `docker login registry.koyeb.com`
    - `docker build -t registry.koyeb.com/<your-org-or-user>/recicar:latest .`
    - `docker push registry.koyeb.com/<your-org-or-user>/recicar:latest`

3. Create app/service on Koyeb
    - App: `marketplace-app`
    - Service image: `registry.koyeb.com/<your-org-or-user>/recicar:latest`
    - Env: `SPRING_PROFILES_ACTIVE=prod`, `DATABASE_*`
    - Health check: HTTP GET `/actuator/health` on port 8080

4. CI: GitHub Actions
    - See `.github/workflows/docker-publish.yml` to build & push images on `main`
    - See `.github/workflows/koyeb-redeploy.yml` to trigger a redeploy after push
