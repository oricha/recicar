# OpenSpec Quick Start - recicar.es

## ✅ Estado Actual

OpenSpec ha sido configurado con éxito para el proyecto recicar.es (Marketplace de piezas de desguace).

- **15 Features completados** con todos los artefactos (proposal, design, specs, tasks)
- **~700+ tareas** de implementación
- **4 artefactos por feature**: proposal.md, design.md, specs/*.md, tasks.md

## 📋 Lista de Features

### recicar.es Features (15)

1. **portal-navigation-structure** - Navegación principal, menú hamburguesa, búsqueda
2. **search-and-filtering-system** - Búsqueda avanzada con 7 filtros
3. **parts-listing-display** - Galería de productos
4. **product-detail-page** - Página detalle de producto
5. **user-authentication-system** - Login/Registro de usuarios
6. **registered-user-features** - Perfil, Wishlist, Búsquedas guardadas
7. **shopping-cart-checkout** - Carrito y proceso de compra
8. **warranties-and-policies** - Garantías y políticas
9. **category-system** - Categorización jerárquica
10. **vendor-seller-panel** - Panel de vendedor/desguace
11. **support-help-section** - Centro de ayuda y soporte
12. **content-and-seo** - Blog, códigos repuestos, SEO
13. **special-features-trust** - Vendedor Top, calificaciones
14. **company-information** - Acerca de, contactos
15. **technical-functionality** - Responsive, toggle precios, selector región

### Anteriores (1)

16. **phase-1-core-catalog-cart-checkout** - Car Parts Marketplace Phase 1

## 🚀 Comandos Principales

### Ver Estado General
```bash
openspec view                          # Dashboard general
openspec list --changes                # Listar todos los cambios
openspec list --changes | head -20     # Primeros 20 cambios
```

### Trabajar en un Feature
```bash
# Ver detalle de un feature
openspec show portal-navigation-structure

# Ver estado (qué artefactos están listos)
openspec status --change portal-navigation-structure

# Comenzar implementación de un feature
/opsx:apply                            # Seleccionar feature interactivamente
```

### Explorar y Clarificar
```bash
/opsx:explore                          # Modo exploración para pensar a través de requisitos
```

### Listar Tareas Pendientes
```bash
openspec list --changes --json | jq '.[].summary'
```

## 📁 Estructura de Directorio

Cada feature está en:
```
openspec/changes/{feature-name}/
├── proposal.md           # Why + What Changes + Capabilities + Impact
├── design.md             # Context + Goals + Technical Decisions + Risks
├── specs/                # Carpetas por capability
│   └── {capability-name}/
│       └── spec.md       # Requirements + Scenarios
└── tasks.md              # Implementation checklist
```

## 📚 Cómo Leer los Artefactos

### 1. proposal.md
- **Why**: Problema a resolver, por qué ahora
- **What Changes**: Lista de cambios específicos
- **Capabilities**: Nuevas capacidades a implementar
- **Impact**: Qué código/APIs/DB cambian

### 2. design.md
- **Context**: Background del proyecto
- **Goals/Non-Goals**: Qué incluir y qué no
- **Decisions**: Decisiones técnicas clave con rationale
- **Risks**: Riesgos y trade-offs

### 3. specs/*.md
- **Requirements**: Qué debe hacer el sistema (SHALL, MUST)
- **Scenarios**: WHEN/THEN casos de uso testables
- Cada scenario es un test case potencial

### 4. tasks.md
- **Grupos de tareas**: Organizadas por componente/layer
- **Checkboxes**: Para rastrear progreso
- **Order**: Tareas en orden de dependencia

## 💡 Flujo de Trabajo Recomendado

### Para Empezar Rápido (1-2 días)
1. Lee **proposal.md** para entender qué se construye
2. Lee **design.md** para decisiones técnicas
3. Copia las **tareas.md** a tu gestor de proyectos
4. Comienza con tareas de base de datos

### Para Profundizar
1. Lee **specs/** para requisitos detallados
2. Escribe tests basados en scenarios
3. Implementa features basado en tasks
4. Marca tareas como completadas en OpenSpec

## 🎯 Recomendación de Orden de Implementación

### Opción A: Por Impacto (recomendado)
1. **portal-navigation-structure** (104 tareas) - Core UX
2. **user-authentication-system** (40 tareas) - Protege otros features
3. **search-and-filtering-system** (40 tareas) - UX crítico
4. **product-detail-page** (40 tareas) - Venta de piezas
5. **shopping-cart-checkout** (40 tareas) - Checkout flow

### Opción B: Por Independencia
1. **category-system** - Datos de soporte
2. **technical-functionality** - Base de UX
3. **company-information** - Páginas estáticas
4. **special-features-trust** - Soporte a vendedores
5. Luego los features complejos

### Opción C: MVP Mínimo (6-8 semanas)
1. portal-navigation-structure
2. user-authentication-system
3. product-detail-page
4. shopping-cart-checkout
5. search-and-filtering-system

## 📊 Métricas

| Métrica | Valor |
|---------|-------|
| Total Features | 15 |
| Total Artefactos | 60+ |
| Total Tareas | ~700 |
| Estimación Equipo 5 devs | 6-12 meses |
| Estimación Equipo 10 devs | 3-6 meses |

## 🔧 Ejemplo: Implementar portal-navigation-structure

1. **Leer propuesta**
   ```bash
   cat openspec/changes/portal-navigation-structure/proposal.md
   ```

2. **Ver tareas**
   ```bash
   cat openspec/changes/portal-navigation-structure/tasks.md | grep "- \[ \]" | head -10
   ```

3. **Comenzar con Database**
   - Leer tareas 1.1-1.7 (Database Setup)
   - Crear migrations Flyway
   - Cargar seed data

4. **Luego Backend**
   - Tareas 2.1-2.16 (Repositories y Services)
   - Tareas 3.1-3.8 (REST API Endpoints)

5. **Luego Frontend**
   - Tareas 4.1-4.8 (Header components)
   - Tareas 5.1-5.6 (Search functionality)
   - etc.

## ❓ FAQ

**P: ¿Puedo cambiar el orden de implementación?**
R: Sí, pero ten cuidado con dependencias. Portal-navigation y authentication son core.

**P: ¿Cómo marco tareas como hechas?**
R: OpenSpec /opsx:apply rastrea automáticamente. O edita tasks.md con [x] en lugar de [ ].

**P: ¿Dónde están los archivos?**
R: En `openspec/changes/{feature-name}/`

**P: ¿Necesito todo el contenido de tasks.md?**
R: No, es un estimado. Ajusta según tu equipo/capacidades.

**P: ¿Cómo hago deploy?**
R: Cada feature tiene un apartado "Deployment" en design.md

## 🔗 Comandos Útiles

```bash
# Crear nuevo feature (cuando necesites agregar uno)
openspec new change "feature-name"

# Ver progreso específico
openspec status --change "portal-navigation-structure"

# Exportar tareas
openspec show portal-navigation-structure | grep "- \["

# Ver specs de un feature
ls openspec/changes/portal-navigation-structure/specs/*/spec.md
```

## 📝 Notas

- Todo OpenSpec está en `/openspec/` (relativo a repo root)
- Config en `openspec/config.yaml`
- Cambios en `openspec/changes/`
- Puedes editar artefactos directamente si necesitas ajustar
- Usa `/opsx:apply` para comenzar implementación interactiva

---

**Última actualización**: 2026-04-26
**OpenSpec Version**: 1.3.1
**Schema**: spec-driven
