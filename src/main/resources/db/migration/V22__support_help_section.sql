-- FAQs, blog, and contact message storage (support & help)

CREATE TABLE faq_categories (
    id          BIGSERIAL PRIMARY KEY,
    slug        VARCHAR(100) NOT NULL UNIQUE,
    title       VARCHAR(255) NOT NULL,
    sort_order  INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE faq_entries (
    id          BIGSERIAL PRIMARY KEY,
    category_id BIGINT NOT NULL REFERENCES faq_categories (id) ON DELETE CASCADE,
    question    TEXT NOT NULL,
    answer      TEXT NOT NULL,
    sort_order  INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX idx_faq_entries_category ON faq_entries (category_id);

CREATE TABLE blog_posts (
    id           BIGSERIAL PRIMARY KEY,
    slug         VARCHAR(200) NOT NULL UNIQUE,
    title        VARCHAR(500) NOT NULL,
    summary      TEXT,
    body         TEXT NOT NULL,
    published    BOOLEAN NOT NULL DEFAULT TRUE,
    published_at TIMESTAMP NOT NULL,
    created_at   TIMESTAMP NOT NULL,
    updated_at   TIMESTAMP NOT NULL
);

CREATE INDEX idx_blog_posts_published_at ON blog_posts (published_at DESC);

CREATE TABLE contact_messages (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(200) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    subject    VARCHAR(500) NOT NULL,
    message    TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_contact_messages_created_at ON contact_messages (created_at DESC);

-- Seed FAQs (español, ReciCar)
INSERT INTO faq_categories (slug, title, sort_order) VALUES
    ('comprar', 'Comprar y pedidos', 0),
    ('envios', 'Envíos y plazos', 10),
    ('devoluciones', 'Devoluciones y garantía', 20),
    ('cuenta', 'Cuenta y vendedores', 30);

INSERT INTO faq_entries (category_id, question, answer, sort_order)
SELECT c.id, q.question, q.answer, q.ord
FROM (VALUES
    ('comprar', '¿Cómo busco una pieza compatible con mi coche?',
     'Puedes usar el buscador por matrícula (si está disponible), por fabricante, modelo y motor, o el buscado por referencia o nombre de pieza. Revisa la ficha: estado, referencias y compatibilidad antes de añadir al carrito.',
     0),
    ('comprar', '¿El precio incluye IVA y gastos de envío?',
     'En el carrito y en el checkout verás el desglose: subtotal, tasa de servicio, IVA, envío y total. Nada se cobra hasta que confirmes el pago.',
     1),
    ('envios', '¿Cuánto tarda en llegar mi pedido?',
     'El plazo depende del vendedor (desguace) y del transporte. Te mostramos estimaciones en la ficha y en el resumen de pedido. Recibirás información de seguimiento cuando el envío se gestione.',
     0),
    ('devoluciones', '¿Puedo devolver un artículo?',
     'Sí, según nuestra política de devoluciones. Revísala en el sitio y contacta con atención al cliente indicando tu número de pedido si ya lo tienes.',
     0),
    ('cuenta', '¿Cómo contacto con soporte?',
     'Puedes usar el formulario de contacto en el sitio o escribir al correo de soporte mostrado en el pie de página. Para incidencias de pedido, indica siempre el número de pedido.',
     0)
) AS q(cat_slug, question, answer, ord)
JOIN faq_categories c ON c.slug = q.cat_slug;

-- Seed blog (educational short articles)
INSERT INTO blog_posts (slug, title, summary, body, published, published_at, created_at, updated_at)
VALUES
    ('elegir-pieza-segunda-mano',
     'Cómo elegir una pieza de segunda mano con confianza',
     'Referencias OEM, compatibilidad y señales de un buen anuncio.',
     '<p>Las piezas de recambio de desguace pueden ofrecer un ahorro importante. Antes de comprar, verifica <strong>referencias OEM o equivalentes</strong>, comprueba que el anuncio describe el <strong>estado</strong> (rayones, prueba de funcionamiento) y, si aplica, la <strong>compatibilidad con tu bastidor o motor</strong>.</p><p>Si dudas, pregunta al vendedor desde la ficha del producto: un desguace serio responde con claridad.</p>',
     TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('matricula-y-bastidor',
     'Matrícula y número de bastidor: qué son y para qué sirven',
     'Te ayudan a acotar motor y versiones; no sustituyen la verificación de la pieza.',
     '<p>La <strong>matrícula</strong> y el <strong>VIN (bastidor)</strong> identifican tu vehículo. Las herramientas de búsqueda usan esos datos para proponer recambios compatibles, pero <strong>siempre</strong> confirma con las referencias de la pieza y las fotos del anuncio.</p>',
     TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
