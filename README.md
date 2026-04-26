# recicar.es - European Car Parts Marketplace

A multi-vendor e-commerce platform connecting customers with junkyards and car parts sellers across Europe. Buy new and used automotive parts with advanced search, secure checkout, and multi-region support.

## Overview

**recicar.es** serves customers in 10 European countries (Spain, Germany, France, Poland, Italy, Finland, Romania, UK, Lithuania, Latvia) with a modern, responsive marketplace for car parts discovery and purchase.

- **Customers**: Search, filter, and purchase parts by vehicle compatibility
- **Sellers**: Manage inventory, monitor sales, and grow their business  
- **Platform**: Secure multi-vendor marketplace with real-time inventory tracking

## Technology Stack

| Layer | Technology |
|-------|-----------|
| **Backend** | Spring Boot 3.2.0 (Java 21) |
| **Frontend** | Thymeleaf, HTML5, CSS3, JavaScript |
| **Database** | PostgreSQL 15+ |
| **Caching** | Redis |
| **Build Tool** | Gradle 8.5 |
| **Deployment** | Docker, Docker Compose, Northflank |
| **Security** | Spring Security, HTTPS, PCI DSS |

## Development Approach

Built using **Spec-Driven Development (SDD)** with [OpenSpec](https://github.com/anthropics/openspec) for structured feature planning. Each feature includes:

- **proposal.md** — Why and what changes
- **design.md** — Technical decisions and architecture
- **specs/*.md** — Detailed requirements with testable scenarios
- **tasks.md** — Implementation checklist (~40-104 tasks per feature)

### Implementation Phases

| Phase | Focus | Timeline |
|-------|-------|----------|
| **Phase 1** | Product catalog, search, shopping cart | 2-4 months |
| **Phase 2** | Seller management, inventory, analytics | 3-4 months |
| **Phase 3** | Multi-vendor marketplace, advanced features | 3-4 months |

**Estimated Timeline**: 6-12 months for a team of 5 developers

## Core Features (15)

1. **Portal Navigation** — Header, hamburger menu, search, region/price selectors
2. **Search & Filtering** — Advanced search with 7+ filters, real-time suggestions
3. **Product Listing** — Gallery view with seller info, pricing, availability
4. **Product Detail** — Images, specs, compatibility, seller information
5. **Authentication** — Secure login/registration with role-based access
6. **User Profiles** — Wishlist, saved searches, purchase history
7. **Shopping & Checkout** — Cart management, multi-step checkout, payment integration
8. **Policies** — Shipping, returns, terms, privacy compliance
9. **Categories** — Hierarchical categories (20+) with auto-complete
10. **Vendor Panel** — Dashboard for sellers to manage inventory and orders
11. **Support** — Help center, FAQs, contact, email support
12. **Content** — Blog, parts codes database, tire equivalence
13. **Trust & Ratings** — Seller badges, customer ratings, service fees
14. **Company Info** — About us, contact details, social media
15. **Technical** — Responsive design, performance optimization, accessibility

## Quick Start

### Prerequisites

- Java 21+
- PostgreSQL 15+
- Docker and Docker Compose
- Git

### Local Development

```bash
# Clone and setup
git clone https://github.com/oricha/recicar.git
cd recicar

# Start services (database, Redis)
docker compose up -d

# Run application
./gradlew runLocal
```

Application runs at `http://localhost:8080`

### Database Setup

PostgreSQL 15+ with schema `recicar`:

```sql
CREATE DATABASE marketplace_dev OWNER marketplace_user;
```

Run migrations:
```bash
./gradlew flywayMigrateDev
```

### Testing

```bash
# Unit tests
./gradlew test

# Cucumber tests
./gradlew cukeLocalRun
```

## Project Structure

```
recicar/
├── openspec/               # Feature definitions (SDD)
│   ├── config.yaml        # OpenSpec configuration
│   └── changes/           # 15 features with specs & tasks
├── src/main/java/         # Spring Boot application
├── src/main/resources/    # Thymeleaf templates, migrations
├── docs/                   # Architecture, API docs
├── scripts/               # Database scripts
└── docker-compose.yml     # Services configuration
```

## Environments

| Environment | Profile | Database | Command |
|-------------|---------|----------|---------|
| **Dev** | `local` | Local PostgreSQL | `./gradlew runLocal` |
| **Test** | `test` | PostgreSQL (Dokploy/local) | `./gradlew runTest` |
| **Prod** | `prod` | Managed PostgreSQL | `./gradlew runProd` |

Configure via `.env` with `DATABASE_*` variables.

## API Reference

RESTful API with `/api/v1/` versioning:

```
GET    /api/v1/categories              # List categories
GET    /api/v1/brands                  # List brands
GET    /api/v1/products                # Search products
POST   /api/v1/cart                    # Add to cart
POST   /api/v1/checkout                # Process order
```

See [docs/api.md](docs/api.md) for complete documentation.

## Performance Targets

- Home page load: < 4 seconds
- Search results: < 2 seconds  
- API response: < 500ms (p95)
- Uptime: 99.9%

## Security

- HTTPS/TLS encryption
- PCI DSS compliance
- SQL injection prevention
- XSS protection (Thymeleaf auto-escaping)
- CSRF tokens
- Spring Security + role-based access control

## Contributing

1. Create feature branch: `git checkout -b feature/description`
2. Follow code standards and run tests
3. Commit with conventional commits
4. Create pull request with description
5. Ensure CI/CD passes and get approval

## Team

- **Tech Lead**: Sarah Chen (@sarah.chen)
- **Product Manager**: Mike Johnson (@mike.j)
- **DevOps**: Alex Kim (@alex.k)

## Documentation

- [OpenSpec Quick Start](OPENSPEC_QUICK_START.md) — Features and tasks
- [Architecture](docs/architecture.md)
- [Database Schema](docs/database-schema.md)
- [API Standards](docs/api-standards.md)
- [PostgreSQL Setup](docs/postgres-local-dev-setup.sql)

## External References

- Vehicle Data: [Smartcar API](https://smartcar.com/docs/getting-started/introduction)

## License

Proprietary. All rights reserved.

---

**Last Updated**: 2026-04-26  
**OpenSpec Version**: 1.3.1  
**Current Phase**: Phase 1 - Core Catalog & Checkout