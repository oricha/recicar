-- Complete portal-navigation seed data:
-- 1) ensure 20+ top-level categories for the storefront
-- 2) expand brands catalog from vehicle make data to exceed 200 entries

INSERT INTO categories (name, description, slug, active, sort_order, created_at)
SELECT 'Suspensión y dirección',
       'Componentes de suspensión, amortiguación y dirección',
       'suspension-direccion',
       true,
       20,
       CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1
    FROM categories
    WHERE slug = 'suspension-direccion'
      AND parent_id IS NULL
);

INSERT INTO brands (name, slug, country, description)
SELECT
    cm.name,
    trim(both '-' FROM regexp_replace(
        lower(
            replace(
                replace(
                    replace(
                        replace(cm.name, '&', ' and '),
                        '.', ' '
                    ),
                    '''', ''
                ),
                '/', ' '
            )
        ),
        '[^a-z0-9]+',
        '-',
        'g'
    )) AS slug,
    NULL,
    'Imported from vehicle make catalog for portal navigation'
FROM car_make cm
WHERE NOT EXISTS (
    SELECT 1
    FROM brands b
    WHERE b.slug = trim(both '-' FROM regexp_replace(
        lower(
            replace(
                replace(
                    replace(
                        replace(cm.name, '&', ' and '),
                        '.', ' '
                    ),
                    '''', ''
                ),
                '/', ' '
            )
        ),
        '[^a-z0-9]+',
        '-',
        'g'
    ))
);
