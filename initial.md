# Análisis de Funcionalidades - recicar.es
## Portal de Venta de Piezas de Desguace para España

---

## 1. ESTRUCTURA GENERAL DEL PORTAL

### 1.1 Navegación Principal
- **Menú Hamburguesa**: Acceso a categorías de piezas organizadas por sistema de vehículo
- **Búsqueda Global**: Búsqueda rápida por código de pieza, nombre o modelo de coche
- **Navegación por Marca**: Acceso directo a marcas populares (BMW, Volkswagen, Audi, Mercedes, Volvo, etc.)
- **Navegación por Categoría**: Organización jerárquica de piezas por sistemas de vehículos
- **Selector de País/Región**: Permite cambiar país de entrega (España, Alemania, Francia, Polonia, etc.)
- **Toggle de Precios**: Mostrar precios con o sin IVA

---

## 2. SISTEMA DE BÚSQUEDA Y FILTRADO

### 2.1 Búsqueda Simple
- Campo de búsqueda por código de pieza, nombre o modelo de vehículo
- Búsqueda rápida en barra superior
- Opción de "Búsqueda de código" mediante checkbox

### 2.2 Búsqueda Avanzada (Página de Búsqueda)
- **Filtro por Marca**: Selección de marca del vehículo
- **Filtro por Modelo**: Selección de modelo según marca seleccionada
- **Filtro por Modificación/Generación**: Especificidad del modelo
- **Filtro por Nombre de Pieza**: Búsqueda específica de componente
- **Filtro por Precio**: Rango de precios (precio mínimo y máximo)
- **Filtro por Condición**: Usado, nuevo, etc.
- **Filtro por Disponibilidad**: Piezas en stock
- **Ordenamiento de Resultados**: 
  - Estándar (por relevancia)
  - Precio más bajo
  - Precio más alto
- **Guardar Búsquedas**: Los usuarios registrados pueden guardar búsquedas frecuentes
- **Paginación**: Navegación entre páginas de resultados

### 2.3 Búsqueda por Vehículo
- Selección de múltiples vehículos simultáneamente
- Opción "Añadir un coche adicional"
- Botón "Filtrar modelos de coche"

---

## 3. LISTADO DE PIEZAS

### 3.1 Vista de Resultados
- **Galería de Piezas**: Visualización en grid/lista de resultados
- **Información por Pieza**:
  - Imagen del producto
  - Nombre/descripción de la pieza
  - Código SKU/OEM
  - Precio (con/sin IVA)
  - Estado de la pieza (usado, con defectos, etc.)
  - Marca vendedor
  - Calificación del vendedor (Vendedor Top)
  - Número de resultados similares
- **Contador de Resultados**: Muestra cantidad total de piezas disponibles (ej: 4710)
- **Indicador de Página**: Muestra página actual (ej: 1/314)
- **Botón "Más artículos"**: Carga más resultados

### 3.2 Características Especiales del Listado
- Información sobre "Tarifa del Servicio" (1.50% del total, mínimo 1.99€, máximo 3.99€)
- Iconos de "Vendedor Top" para identificar vendedores confiables
- Sección de preguntas frecuentes (FAQ) sobre la categoría

---

## 4. PÁGINA DE DETALLE DE PRODUCTO

### 4.1 Información General del Producto
- **SKU Único**: Identificador único del producto
- **Título Completo**: "Marca Modelo Generación - Nombre Pieza"
- **Imágenes del Producto**:
  - Galería de hasta 7 imágenes de alta resolución
  - Navegación con botones prev/next
  - Indicador de página de imágenes (ej: 1 de 7)

### 4.2 Datos Técnicos de la Pieza
Tabla con información detallada:
- Código de fabricante
- Código OEM
- Otros códigos de referencia
- Condición (Usado, Nuevo, etc.)
- Calidad/Estado (Con defectos, Perfecto, etc.)
- SKU del sistema
- Posición en vehículo (Frontal derecho, Trasero izquierdo, etc.)

### 4.3 Información del Vehículo de Origen
Tabla detallada con:
- Fabricante/Marca
- Serie del vehículo
- Modelo
- Año de fabricación
- Cilindrada del motor (cm³)
- Potencia del motor (kW)
- Tipo de carrocería
- Posición del volante
- Sistemas de tracción
- Tipo de combustible
- Tipo de caja de cambios
- Color del vehículo
- Código de color
- Código del motor
- Kilometraje del vehículo
- Número VIN (identificación del vehículo)

### 4.4 Fotos del Vehículo de Origen
- Galería de hasta 15 fotografías del vehículo completo
- Navegación con botones prev/next
- Indicador de página (ej: 1 de 15)

### 4.5 Sección de Otros Repuestos
- Lista de otros repuestos disponibles del mismo vehículo
- Botón "Ver todas las piezas" para acceder al catálogo completo del vehículo

### 4.6 Información de Contacto
- **"Contactar al vendedor"**: Botón para comunicarse directamente
- Comentario del vendedor sobre la pieza
- Opción para traducir comentarios
- Información del vendedor (nombre, ubicación, calificación)

### 4.7 Panel de Compra
- **Precio**: Mostrado con IVA seleccionable
- **"Tarifa de Servicio"**: Botón informativo sobre costos adicionales
- **Botón "Añadir a la lista de deseos"**: Guardar piezas favoritas
- **Botón "Comprar"**: Proceder al carrito
- **Selector de Ubicación de Entrega**: País de destino (España, etc.)
- **Garantías Mostradas**:
  - Devoluciones en 14 días
  - Envío de devolución gratuito
  - Reembolsos rápidos

### 4.8 Información Adicional
- Sección SEO con información detallada sobre la categoría de piezas
- Información sobre precios en la categoría
- Cantidad de artículos disponibles
- Tipos de componentes (por ejemplo, tipos de faros)
- Guías de cambio y mantenimiento

### 4.9 Breadcrumb/Navegación Jerárquica
Ruta de navegación:
Inicio > Marca > Modelo > Generación > Sistema > Categoría Pieza > Tipo Pieza > Producto

---

## 5. AUTENTICACIÓN Y CUENTAS DE USUARIO

### 5.1 Login
- **Enlace de Acceso**: "Iniciar sesión" en barra superior
- **Redireccionamiento a**: `/users/login` o `/user/login`

### 5.2 Tipos de Usuarios
- **Usuarios Normales**: Compradores particulares
- **Empresas/Desguaces**: Vendedores de piezas
- **Sistema de Roles**: Acceso diferenciado según tipo de usuario

### 5.3 Áreas Protegidas
- **Mensajes**: `/user-dashboard/chat` - Sistema de mensajería con vendedores
- **Piezas Favoritas**: `/wishlist` - Lista de deseos
- **Carrito**: `/cart` - Carrito de compras

---

## 6. FUNCIONALIDADES DE USUARIO REGISTRADO

### 6.1 Perfil y Dashboard
- Área de usuario (`/user-dashboard/chat`)
- Centro de mensajes para comunicación con vendedores

### 6.2 Wishlist / Piezas Favoritas
- Guardar piezas de interés
- Acceso rápido a piezas guardadas
- Contador de piezas guardadas en menú principal

### 6.3 Búsquedas Guardadas
- Guardar criterios de búsqueda frecuentes
- Acceso rápido a búsquedas guardadas
- Requiere inicio de sesión

---

## 7. CARRITO Y COMPRA

### 7.1 Carrito de Compras
- Visualización en header: `/cart`
- Contador de artículos en carrito
- Cálculo automático de:
  - Subtotal
  - Tarifa de servicio (1.50% mín 1.99€, máx 3.99€)
  - Total con IVA

### 7.2 Proceso de Compra
- Botón "Comprar" en página de detalle
- Carrito mantiene piezas hasta checkout
- Resumen de compra antes de confirmar

### 7.3 Métodos de Pago Aceptados
- PayPal
- Visa
- Mastercard
- Maestro
- Paysera
- Transferencia bancaria

### 7.4 Envío
- Selección de país/región de destino
- Integración con DPD (proveedor de envío mostrado)
- Múltiples países disponibles (UE completa)

---

## 8. GARANTÍAS Y POLÍTICAS

### 8.1 Devoluciones
- **Período**: 14 días desde la compra
- **Costo de Devolución**: Gratuito (cubierto por la plataforma)
- **Reembolsos**: Rápidos (procesados en pocos días)

### 8.2 Información de Envío
- Enlace: `/info-de-envio`
- Documentación de costos y tiempos

### 8.3 Información de Pago
- Enlace: `/info-de-pago`
- Métodos aceptados y seguridad

### 8.4 Política de Devolución
- Enlace: `/politica-de-devolucion`

### 8.5 Términos de Uso
- Enlace: `/terminos-de-uso`

### 8.6 Política de Privacidad
- Enlace: `/politica-de-privacidad`

### 8.7 Política de Comunicación por Chat
- Enlace: `/politica-de-comunicacion-por-chat`

---

## 9. SISTEMA DE CATEGORIZACIÓN

### 9.1 Categorías Principales (20+ categorías)
- Caja de cambios/embrague/transmisión
- Carrocería/repuestos
- Dispositivos/interruptores/electrónica
- Eje delantero
- Eje trasero
- Habitáculo/interior
- Lunas/vidrios
- Motor
- Otros repuestos
- Puertas
- Radiador/calefacción/aire acondicionado
- Repuestos carrocería delantera exterior
- Repuestos carrocería trasera exterior
- Ruedas/neumáticos/tapacubos
- Sistema de frenos
- Sistema de iluminación
- Sistema limpieza/lavado
- Sistema combustible
- Sistema escape
- Y más...

### 9.2 Subcategorías Jerárquicas
Cada categoría se divide en subcategorías (hasta 3-4 niveles)
Ejemplo: Sistema de iluminación > Faro delantero > Faro principal

### 9.3 Búsqueda por Marca
- 200+ marcas de vehículos disponibles
- Desde marcas comunes (BMW, Mercedes, Audi) hasta marcas chinas (BYD, Geely, JAC)

### 9.4 Búsqueda por Modelo
- Miles de modelos de vehículos
- Generaciones y variaciones de modelos

---

## 10. PANEL DE VENDEDOR / DESGUACE

### 10.1 Enlace de Acceso
- **"Vender"**: `/join.recicar.es/es`
- Redirige a plataforma de registro para vendedores

### 10.2 Funcionalidades Esperadas para Vendedores
- Panel de gestión de inventario (inferido)
- Carga de piezas con:
  - Múltiples imágenes
  - Datos técnicos detallados
  - Información del vehículo origen
  - Precios
  - Estado de la pieza
- Gestión de órdenes
- Comunicación con compradores
- Estadísticas de vendedor
- Sistema de calificaciones (Vendedor Top)

---

## 11. SECCIÓN DE HELP / SOPORTE

### 11.1 Centro de Ayuda
- Enlace: `https://support.recicar.es/es_ES`
- Documentación y FAQs

### 11.2 Contacto
- Enlace: `/contactos`
- Email: `help@recicar.es`
- Formulario de contacto

### 11.3 FAQs Integradas
- En páginas de resultados de búsqueda
- Información sobre:
  - Rangos de precio
  - Por qué comprar en OVOKO
  - Cantidad de artículos disponibles
  - Señales de cambio de piezas
  - Procedimiento de cambio
  - Cómo elegir piezas correctas

---

## 12. CONTENIDO Y SEO

### 12.1 Blog
- Enlace: `/blog`
- Artículos educativos sobre repuestos y mantenimiento

### 12.2 Lista de Códigos de Repuestos
- Enlace: `/lista-de-codigos-de-repuestos`
- Referencia de códigos OEM y fabricante

### 12.3 Equivalencias de Neumáticos
- Enlace: `/equivalencia-neumaticos`
- Herramienta de búsqueda de equivalencias

### 12.4 Contenido Descriptivo por Categoría
- Descripciones detalladas de tipos de piezas
- Información sobre características técnicas
- Guías de selección
- Beneficios y características

---

## 13. CARACTERÍSTICAS ESPECIALES

### 13.1 Sistema de Confianza
- **Vendedor Top**: Insignia para vendedores verificados y de alta calificación
- **Calificaciones**: Sistema de estrellas (1-5)
- **Comentarios del Vendedor**: Feedback sobre cada pieza

### 13.2 Información de Tarifa de Servicio
- Modal informativo con detalles
- Cálculo transparente: 1.50% del carrito
- Mínimo: 1.99€
- Máximo: 3.99€
- IVA incluido

### 13.3 Garantía Transparente
- Visualización clara de garantías en página de producto
- 14 días de devolución
- Envío de devolución gratuito
- Reembolsos rápidos

### 13.4 Múltiples Regiones
- Portal disponible en múltiples países:
  - España (recicar.es)
  - Alemania (OVOKO.DE)
  - Francia (OVOKO.FR)
  - Polonia (OVOKO.PL)
  - Italia (OVOKO.IT)
  - Finlandia (OVOKO.FI)
  - Rumania (OVOKO.RO)
  - Reino Unido (RRR.LT/EN)
  - Lituania (RRR.LT)
  - Letonia (RRR.LT)

### 13.5 Redes Sociales
- Facebook
- YouTube
- Instagram
- Integración en footer

### 13.6 Newsletter
- Formulario de suscripción
- Aceptación de política de privacidad requerida
- reCAPTCHA para prevenir spam

---

## 14. INFORMACIÓN SOBRE LA EMPRESA

### 14.1 Enlaces de Información
- **Acerca de Nosotros**: `/acerca-de-nosotros`
- **Contactos**: `/contactos`
- **Ayuda**: `https://support.recicar.es/es_ES`

### 14.2 Copyright
- "2014 - 2026 © recicar.es"
- "Todos los derechos reservados"
- Protección de contenido

---

## 15. FUNCIONALIDADES TÉCNICAS

### 15.1 Responsive Design
- Menú hamburguesa para mobile
- Navegación adaptable
- Formularios responsivos

### 15.2 Selección de País Dinámica
- Modal con selección de país
- Cambio dinámico de región de entrega
- Actualización de precios según región

### 15.3 Toggle de Precios
- Switch para mostrar precios con/sin IVA
- Actualización en tiempo real

### 15.4 Buscador Avanzado
- Autocompletado (inferido)
- Búsqueda por múltiples criterios
- Filtros en vivo

---

## RESUMEN DE FUNCIONALIDADES CLAVE PARA IMPLEMENTAR

### Prioridad ALTA (Funcionalidades Esenciales)
1. ✅ Sistema de búsqueda y filtrado avanzado
2. ✅ Listado de piezas con galerías de imágenes
3. ✅ Página de detalle de producto con datos técnicos completos
4. ✅ Sistema de autenticación (usuarios y empresas)
5. ✅ Carrito de compras y checkout
6. ✅ Sistema de categorización jerárquica
7. ✅ Panel de vendedor para gestión de piezas
8. ✅ Sistema de mensajería entre vendedores y compradores
9. ✅ Piezas favoritas (wishlist)
10. ✅ Sistema de garantía y devoluciones

### Prioridad MEDIA (Funcionalidades Importantes)
1. ✅ Búsquedas guardadas para usuarios registrados
2. ✅ Sistema de calificaciones y vendedor top
3. ✅ Blog y FAQs educativas
4. ✅ Múltiples métodos de pago
5. ✅ Selector de país/región
6. ✅ Toggle de IVA
7. ✅ Newsletter y suscripción
8. ✅ Contacto y soporte

### Prioridad BAJA (Nice to Have)
1. ✅ Integraciones con redes sociales
2. ✅ Equivalencias de neumáticos
3. ✅ Lista de códigos de repuestos
4. ✅ Múltiples regiones/idiomas
5. ✅ Analytics y estadísticas

---

## NOTAS IMPORTANTES PARA DESARROLLO

- La plataforma maneja un catálogo MASIVO (40+ millones de piezas, 6799 vendedores)
- Requiere base de datos optimizada y buscador potente
- Sistema de imágenes robusto (múltiples imágenes por pieza y vehículo)
- APIs para integración de pagos y envíos (DPD, PayPal, etc.)
- Sistema de roles y permisos complejo
- Gestión de inventario en tiempo real
- SEO optimizado para miles de combinaciones de marca/modelo/pieza