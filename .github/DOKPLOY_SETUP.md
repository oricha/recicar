# Dokploy quick setup (GHCR)

Checklist for deploying Recicar with images on **GitHub Container Registry** (`ghcr.io`).

## Repository settings

1. **Settings → Actions → General → Workflow permissions**  
   Enable **Read and write permissions** (so `GITHUB_TOKEN` can push packages), or grant **Packages: Write** at minimum.

2. **No Docker Hub secrets required** for the default workflow; login uses `GITHUB_TOKEN`.

3. **CI:** el workflow levanta **PostgreSQL en contenedor** para tests; **no** hace falta `TEST_DATABASE_*` en GitHub Secrets.

## After the first successful workflow run

1. Open the repo on GitHub → **Packages** (right sidebar) and confirm the `recicar` (or repo-named) container image exists.
2. If the package is **private**, Dokploy needs registry credentials to pull:
   - Registry: `ghcr.io`
   - Username: GitHub username (or `token`)
   - Password: Personal Access Token with `read:packages` (and `write:packages` only if you push manually)

## Dokploy — producción

- **Source type:** Docker  
- **Image:** `ghcr.io/<OWNER_LOWERCASE>/<REPO_LOWERCASE>:latest`  
  Example for repo `myorg/recicar`: `ghcr.io/myorg/recicar:latest`

Variables típicas: `SPRING_PROFILES_ACTIVE=prod`, `DATABASE_*`, `JWT_SECRET`, `SERVER_PORT=8080`.

## Dokploy — entorno **test**

Usa **PostgreSQL incluido en Dokploy** (servicio BD en la misma red). Perfil Spring **`test`** y `DATABASE_*` con el **hostname interno** del Postgres (ver `DEPLOYMENT.md` § 4.5).

## Auto-deploy

GitHub Packages does not offer Docker Hub–style webhooks. Options:

- **Dokploy deploy webhook / API:** call it from a workflow step after `build-push`, or  
- Redeploy manually / on a schedule from Dokploy.

See `DEPLOYMENT.md` for details.
