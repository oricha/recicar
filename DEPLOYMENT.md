# Deployment Guide - Recicar to Dokploy (GitHub Container Registry)

This guide explains how to deploy the Recicar application to Dokploy using GitHub Actions to build and push Docker images to **GitHub Container Registry (GHCR)** at `ghcr.io`.

## Why This Approach?

Building Docker images directly on the Dokploy server can consume significant resources (RAM, CPU) and may cause:

- Server timeouts
- Server freezing
- Downtime for other applications

**Solution:** Build the Docker image in GitHub Actions and push it to **GHCR**. Dokploy pulls the pre-built image for deployment. No Docker Hub account is required.

## Prerequisites

1. **GitHub Repository** with this code
2. **Workflow permissions** so Actions can publish packages (see Step 1)
3. **Dokploy** instance installed and accessible
4. **PostgreSQL** reachable from Dokploy (hosted DB, Neon, etc.)

## Step 1: GitHub repository settings

Allow the workflow to push container images:

1. Open the repo → **Settings** → **Actions** → **General**
2. Under **Workflow permissions**, select **Read and write permissions** (recommended for simplicity),  
   _or_ keep read-only and use a **Personal Access Token (classic)** with `write:packages` stored as a secret (advanced).

The workflow in `.github/workflows/deploy.yml` uses `GITHUB_TOKEN` with `permissions: packages: write`.

## Step 2: GitHub Actions workflow

The workflow at `.github/workflows/deploy.yml`:

1. Triggers on every push to the `main` branch
2. Checks out the repository
3. Sets up Java 21 and builds the app (via Docker build using your `Dockerfile`)
4. Logs in to **ghcr.io** with `GITHUB_TOKEN`
5. Pushes the image as:

   `ghcr.io/<github_repository_owner_lowercase>/<github_repository_name_lowercase>:latest`  
   and  
   `ghcr.io/.../...:main-<commit-sha>`

**Example:** repository `MyOrg/Recicar` → image `ghcr.io/myorg/recicar:latest` (paths are lowercased for GHCR).

**No extra secrets** are required for publishing when `GITHUB_TOKEN` has package write access.

## Step 3: Push to GitHub

```bash
git push origin main
```

Monitor **Actions** for the workflow run. After success, open **Packages** on the repo (or your org) to see the container package.

### Private packages

If the package visibility is **private**, Dokploy (or the host running Docker) must authenticate when pulling:

- **Registry:** `ghcr.io`
- **Username:** your GitHub username (or literal `token` with a PAT)
- **Password / token:** PAT with at least **`read:packages`**

Attach these credentials in Dokploy wherever registry auth is configured for private images.

## Step 4: Configure Dokploy

### 4.1 Create application

1. Log in to Dokploy
2. Create a new application
3. **Source type:** **Docker**
4. **Docker image:** `ghcr.io/YOUR_OWNER_LOWERCASE/YOUR_REPO_LOWERCASE:latest`  
   (match the repository path on GitHub, all lowercase.)

### 4.2 Environment variables

```
DATABASE_URL=jdbc:postgresql://your-db-host:5432/marketplace_prod
DATABASE_USERNAME=your_db_user
DATABASE_PASSWORD=your_db_password
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=your_secure_jwt_secret_here
SERVER_PORT=8080
```

### 4.3 Domain

- **Domains:** generated domain or custom
- **Container port:** `8080`

### 4.4 Deploy

Click **Deploy** and open the app URL when the deployment finishes.

## Step 5: Auto-deploy (optional)

GHCR does **not** offer the same “repository webhook after push” model as Docker Hub for triggering Dokploy.

**Options:**

1. **Dokploy webhook / API** — add a final step to `.github/workflows/deploy.yml` that `curl`s your Dokploy deploy URL or API (store URL/API key as GitHub secrets).
2. **Manual or scheduled deploy** in Dokploy after you see a new image in GHCR.

Example pattern for a custom deploy hook (adjust URL and secrets to match your Dokploy version):

```yaml
      - name: Trigger Dokploy deployment
        if: github.ref == 'refs/heads/main'
        env:
          DOKPLOY_DEPLOY_HOOK_URL: ${{ secrets.DOKPLOY_DEPLOY_HOOK_URL }}
        run: |
          if [ -n "$DOKPLOY_DEPLOY_HOOK_URL" ]; then
            curl -fsS -X POST "$DOKPLOY_DEPLOY_HOOK_URL"
          fi
```

Add `DOKPLOY_DEPLOY_HOOK_URL` under **Settings → Secrets and variables → Actions** if you use this.

## Step 6: Health check and rollbacks (recommended)

Spring Boot Actuator exposes `/actuator/health`.

### Dokploy health check (example)

```json
{
  "Test": [
    "CMD",
    "curl",
    "-f",
    "http://localhost:8080/actuator/health"
  ],
  "Interval": 30000000000,
  "Timeout": 10000000000,
  "StartPeriod": 30000000000,
  "Retries": 3
}
```

### Update config (rollback)

```json
{
  "Parallelism": 1,
  "Delay": 10000000000,
  "FailureAction": "rollback",
  "Order": "start-first"
}
```

## Deployment workflow (summary)

1. Develop and commit
2. Push to `main`
3. GitHub Actions builds and pushes to **ghcr.io**
4. Dokploy pulls `...:latest` (or a specific tag) and runs the container
5. Failed health checks can roll back if configured in Dokploy

## Monitoring

- **Builds:** GitHub → **Actions**
- **Images:** GitHub → **Packages** (`ghcr.io`)
- **Runtime:** Dokploy logs and app logs

## Troubleshooting

### Build fails in Actions

- Read the failed job log
- Confirm **Workflow permissions** allow **packages: write** (or use a PAT secret for `docker/login-action`)
- Validate `Dockerfile` and Java/Gradle build

### Dokploy cannot pull image

- Image name must be **lowercase** (`ghcr.io/org/repo:tag`)
- For **private** packages, configure **ghcr.io** credentials on Dokploy
- Confirm the workflow run pushed the expected tag (`latest`)

### Roll back to a previous image

Use an immutable tag from the same repository, for example:

`ghcr.io/your-org/recicar:main-abc1234` (replace with your commit SHA tag from the workflow)

## Security

- Do not commit secrets; use GitHub Secrets and Dokploy env configuration
- Rotate PATs used for private registry pull
- Use strong `JWT_SECRET` and database credentials
- Prefer HTTPS for the public URL (handled by Dokploy / reverse proxy)

## References

- [Dokploy documentation](https://docs.dokploy.com)
- [GitHub Actions](https://docs.github.com/actions)
- [Working with the Container registry](https://docs.github.com/packages/working-with-a-github-packages-registry/working-with-the-container-registry)
