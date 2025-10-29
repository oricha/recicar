# Deployment Guide - Recicar to Dokploy

This guide explains how to deploy the Recicar application to Dokploy using GitHub Actions to build and push Docker images to Docker Hub.

## Why This Approach?

Building Docker images directly on the Dokploy server can consume significant resources (RAM, CPU) and may cause:
- Server timeouts
- Server freezing
- Downtime for other applications

**Solution**: Build the Docker image in GitHub Actions and push it to Docker Hub. Dokploy will then pull the pre-built image for deployment.

## Prerequisites

1. **Docker Hub Account**: Create one at [https://hub.docker.com](https://hub.docker.com)
2. **Docker Hub Repository**: Create a repository named `recicar` in your Docker Hub account
3. **GitHub Repository**: Your code should be in a GitHub repository
4. **Dokploy Instance**: Have Dokploy installed and accessible

## Step 1: Configure Docker Hub

### 1.1 Create Docker Hub Access Token

1. Log in to [Docker Hub](https://hub.docker.com)
2. Go to **Account Settings** → **Security** → **Access Tokens**
3. Click **New Access Token**
4. Give it a name (e.g., "GitHub Actions")
5. Copy the token (you won't be able to see it again!)

### 1.2 Create Docker Hub Repository

1. In Docker Hub, click **Create Repository**
2. Name it: `recicar`
3. Set visibility (Public or Private)
4. Click **Create**

## Step 2: Configure GitHub Secrets

1. Go to your GitHub repository
2. Navigate to **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Add the following secrets:

   | Name | Value |
   |------|-------|
   | `DOCKERHUB_USERNAME` | Your Docker Hub username |
   | `DOCKERHUB_TOKEN` | The access token you created |

## Step 3: GitHub Actions Workflow

The workflow file has already been created at `.github/workflows/deploy.yml`. It will:

1. Trigger on every push to the `main` branch
2. Check out the repository
3. Set up Java 21
4. Log in to Docker Hub
5. Build the Docker image using your Dockerfile
6. Push the image to Docker Hub with tags:
   - `latest` (always the latest version)
   - `main-<commit-sha>` (specific version)

## Step 4: Push to GitHub

Once you commit and push to the `main` branch, GitHub Actions will automatically:

```bash
git add .github/workflows/deploy.yml .dockerignore DEPLOYMENT.md
git commit -m "Add GitHub Actions workflow for Dokploy deployment"
git push origin main
```

Check the **Actions** tab in GitHub to monitor the build progress.

## Step 5: Configure Dokploy

### 5.1 Create Application in Dokploy

1. Log in to your Dokploy instance
2. Create a new application
3. **Source Type**: Select **Docker**
4. **Docker Image**: Enter `YOUR_DOCKERHUB_USERNAME/recicar:latest`
   - Replace `YOUR_DOCKERHUB_USERNAME` with your actual Docker Hub username
5. Click **Save**

### 5.2 Configure Environment Variables

Add the following environment variables in Dokploy:

```
# Database Configuration
DATABASE_URL=jdbc:postgresql://your-db-host:5432/marketplace_prod
DATABASE_USERNAME=your_db_user
DATABASE_PASSWORD=your_db_password

# Spring Profile
SPRING_PROFILES_ACTIVE=prod

# JWT Secret (generate a secure random string)
JWT_SECRET=your_secure_jwt_secret_here

# Server Port (if different from 8080)
SERVER_PORT=8080
```

### 5.3 Configure Domain

1. Go to **Domains** tab in Dokploy
2. Click the **Dice icon** to generate a domain
3. Set the port to `8080`
4. Or add your custom domain

### 5.4 Deploy

1. Click **Deploy** in Dokploy
2. Wait for the deployment to complete
3. Access your application via the configured domain

## Step 6: Enable Auto-Deploy (Optional)

To automatically deploy when you push to GitHub:

### Option A: Using Docker Hub Webhooks

1. In Dokploy, go to your application → **Deployments** tab
2. Copy the **Webhook URL**
3. Go to Docker Hub → Your repository → **Webhooks** tab
4. Click **Create Webhook**
5. Name: `Dokploy Auto Deploy`
6. Webhook URL: Paste the URL from Dokploy
7. Click **Create**

Now, every time GitHub Actions pushes a new image with the `latest` tag, Dokploy will automatically deploy it.

### Option B: Using Dokploy API (Alternative)

Update `.github/workflows/deploy.yml` to trigger deployment via API:

```yaml
      - name: Trigger Dokploy Deployment
        run: |
          curl -X 'POST' \
            'https://your-dokploy-domain/api/trpc/application.deploy' \
            -H 'accept: application/json' \
            -H 'x-api-key: YOUR-GENERATED-API-KEY' \
            -H 'Content-Type: application/json' \
            -d '{
                "json":{
                    "applicationId": "YOUR-APPLICATION-ID"
                }
            }'
```

To get the API key:
1. In Dokploy, go to **Settings** → **API Keys**
2. Create a new API key
3. Add it as a GitHub secret: `DOKPLOY_API_KEY`

## Step 7: Configure Health Check and Rollbacks (Recommended)

### 7.1 Add Health Check Endpoint

The application should have a health check endpoint. Spring Boot Actuator provides this at `/actuator/health`.

Ensure it's enabled in `application.yml`:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: always
```

### 7.2 Configure Dokploy Health Check

1. In Dokploy, go to **Advanced** → **Cluster Settings** → **Swarm Settings**
2. Add **Health Check** configuration:

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

### 7.3 Configure Update Config (Rollback)

In **Update Config**, add:

```json
{
  "Parallelism": 1,
  "Delay": 10000000000,
  "FailureAction": "rollback",
  "Order": "start-first"
}
```

This ensures:
- **Zero downtime**: New container starts before old one stops
- **Automatic rollback**: If health check fails, rolls back to previous version
- **Safe deployments**: Deploys one instance at a time

## Deployment Workflow

Once everything is set up, your deployment workflow is:

1. **Develop**: Make changes to your code locally
2. **Commit**: Commit your changes
3. **Push**: Push to `main` branch
4. **GitHub Actions**: Automatically builds and pushes Docker image
5. **Dokploy**: Automatically deploys the new image (if webhook configured)
6. **Rollback**: If health check fails, automatically rolls back

## Monitoring

- **GitHub Actions**: Check build status in GitHub Actions tab
- **Docker Hub**: Verify image was pushed successfully
- **Dokploy**: Monitor deployment logs and application status
- **Application Logs**: Check logs in Dokploy for runtime issues

## Troubleshooting

### Build Fails in GitHub Actions

- Check the Actions tab for error logs
- Verify Dockerfile is correct
- Ensure all dependencies are available

### Deployment Fails in Dokploy

- Check environment variables are set correctly
- Verify database connection
- Review application logs in Dokploy
- Ensure Docker image was pushed successfully to Docker Hub

### Application Not Accessible

- Check domain configuration
- Verify port mapping (8080)
- Check firewall rules
- Review Dokploy logs

### Rollback to Previous Version

If auto-rollback doesn't work:

1. In Dokploy, go to **Deployments**
2. Find a previous successful deployment
3. Click **Redeploy**

Or manually specify a previous image tag:
- Change Docker image to: `YOUR_USERNAME/recicar:main-<previous-commit-sha>`

## Benefits of This Approach

✅ **No server resource exhaustion**: Builds happen on GitHub's infrastructure  
✅ **Faster deployments**: Pre-built images download faster  
✅ **Automated pipeline**: Push to `main` = automatic deployment  
✅ **Zero downtime**: Rolling updates with health checks  
✅ **Automatic rollbacks**: Failed deployments revert automatically  
✅ **Version control**: Every deployment is tagged with commit SHA  
✅ **Production ready**: Enterprise-grade deployment strategy  

## Security Notes

- Never commit secrets to the repository
- Use GitHub Secrets for sensitive data
- Regularly rotate Docker Hub access tokens
- Use strong passwords for database
- Keep dependencies updated
- Enable HTTPS in production

## Next Steps

1. Set up monitoring and alerting
2. Configure backup strategy for database
3. Set up SSL/TLS certificates
4. Configure CDN for static assets
5. Implement CI/CD for staging environment
6. Add automated testing before deployment

---

**Need Help?**

- Dokploy Documentation: [https://docs.dokploy.com](https://docs.dokploy.com)
- GitHub Actions Documentation: [https://docs.github.com/actions](https://docs.github.com/actions)
- Docker Hub Documentation: [https://docs.docker.com/docker-hub](https://docs.docker.com/docker-hub)

