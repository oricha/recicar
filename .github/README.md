# GitHub Actions & Dokploy Deployment

This directory contains the GitHub Actions workflow that builds the Docker image and pushes it to **GitHub Container Registry (GHCR)** for deployment on Dokploy.

## Files

- **`workflows/deploy.yml`** — Build and push to `ghcr.io` on every push to `main`
- **`DOKPLOY_SETUP.md`** — Short checklist for repo settings and Dokploy
- **`../DEPLOYMENT.md`** — Full deployment guide

## What the workflow does

1. **Triggers** on push to `main`
2. **Builds** the image using the repo `Dockerfile`
3. **Pushes** to GHCR with tags:
   - `latest`
   - `main-<commit-sha>`
4. **Image name:** `ghcr.io/<owner>/<repo>` (GitHub path, lowercased for the registry)

## Requirements

- Repo **Settings → Actions → General → Workflow permissions:** **Read and write permissions** (so `GITHUB_TOKEN` can push packages), or a PAT with `write:packages` wired into the workflow if you customize it
- **No Docker Hub** account or secrets for the default workflow

## Dokploy

- **Source type:** Docker  
- **Image:** `ghcr.io/<your-lower-case-owner>/<your-lower-case-repo>:latest`  
- For **private** packages, configure registry login on Dokploy (`ghcr.io` + GitHub username + PAT with `read:packages`)

## Documentation

- **Quick checklist:** [DOKPLOY_SETUP.md](./DOKPLOY_SETUP.md)
- **Full guide:** [../DEPLOYMENT.md](../DEPLOYMENT.md)

## Deployment flow

```mermaid
graph LR
    A[Push to main] --> B[GitHub Actions]
    B --> C[Build Docker Image]
    C --> D[Push to GHCR]
    D --> E[Deploy hook or manual]
    E --> F[Dokploy pull & deploy]
    F --> G[Health Check]
```

## Customization

### Trigger branches

Edit `workflows/deploy.yml`:

```yaml
on:
  push:
    branches: ["main", "production"]
```

### Dokploy webhook after push

Add a step that POSTs to your Dokploy deploy URL (secret). See `DEPLOYMENT.md`.

### Multi-arch

In `deploy.yml`, extend platforms on the build-push step, for example:

```yaml
platforms: linux/amd64,linux/arm64
```

## Monitoring

- **Builds:** GitHub → Actions  
- **Images:** GitHub → Packages (`ghcr.io`)  
- **Runtime:** Dokploy dashboard and logs

## Troubleshooting

- **403 / denied push to ghcr.io:** fix workflow permissions or use a PAT with `write:packages` in `docker/login-action`
- **Dokploy pull fails:** check image name (lowercase), tag `latest`, and private-registry credentials
- **Dockerfile errors:** inspect the failed job log in Actions

## Security

- Do not commit registry or application secrets; use GitHub Secrets and Dokploy environment variables
- Rotate PATs used for private `docker pull` from GHCR

---

**Questions?** See [../DEPLOYMENT.md](../DEPLOYMENT.md)
