# Security Guidelines for Recicar Marketplace

## 🚨 IMPORTANT: Security Configuration Required

This application contains sensitive configuration that must be properly secured before deployment to production.

## 🔐 Required Environment Variables

Set these environment variables in your `.env` file:

```bash
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/marketplace_dev
DATABASE_USERNAME=marketplace_user
DATABASE_PASSWORD=your_very_secure_password_here

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password_here

# Admin User (CHANGE THESE IN PRODUCTION!)
ADMIN_EMAIL=admin@yourdomain.com
ADMIN_PASSWORD=your_very_secure_admin_password
ADMIN_FIRST_NAME=Admin
ADMIN_LAST_NAME=User

# JWT Secret (for production)
JWT_SECRET=your_very_long_random_jwt_secret_here
```

## 🚫 What NOT to commit to Git

- `.env` files
- `docker-compose.yml` (use `docker-compose.example.yml` instead)
- Any files containing passwords, API keys, or secrets
- Database dumps with real data
- SSL certificates and private keys

## ✅ What IS safe to commit

- `docker-compose.example.yml`
- `.env.example`
- Configuration templates
- Code (without hardcoded secrets)

## 🔒 Production Security Checklist

- [ ] Change default admin password
- [ ] Use strong, unique passwords for all services
- [ ] Enable HTTPS/TLS
- [ ] Configure proper firewall rules
- [ ] Use environment variables for all secrets
- [ ] Regular security updates
- [ ] Database connection encryption
- [ ] Audit logging enabled

## 🐳 Docker Security

1. **Never commit real credentials** in docker-compose.yml
2. **Use .env files** for sensitive data
3. **Run containers as non-root** users
4. **Limit container capabilities**
5. **Regular image updates**

## 📝 Example .env file

```bash
# Copy this to .env and fill in real values
DATABASE_URL=jdbc:postgresql://localhost:5432/marketplace_dev
DATABASE_USERNAME=marketplace_user
DATABASE_PASSWORD=your_secure_password_here
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password_here
ADMIN_EMAIL=admin@yourdomain.com
ADMIN_PASSWORD=your_secure_admin_password
JWT_SECRET=your_jwt_secret_here
```

## 🆘 Security Issues

If you find security vulnerabilities:
1. **DO NOT** create public issues
2. **Email** security@yourdomain.com
3. **Use** private security advisories
4. **Follow** responsible disclosure

## 📚 Additional Resources

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Docker Security Best Practices](https://docs.docker.com/engine/security/)
