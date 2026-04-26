-- Seed baseline brands and popular models for portal navigation.
INSERT INTO brands (name, slug, country, description) VALUES
('Audi', 'audi', 'Germany', 'German premium manufacturer'),
('BMW', 'bmw', 'Germany', 'German premium manufacturer'),
('BYD', 'byd', 'China', 'Chinese electric and hybrid vehicle manufacturer'),
('Citroen', 'citroen', 'France', 'French vehicle manufacturer'),
('Fiat', 'fiat', 'Italy', 'Italian vehicle manufacturer'),
('Ford', 'ford', 'United States', 'American vehicle manufacturer'),
('Geely', 'geely', 'China', 'Chinese vehicle manufacturer'),
('Honda', 'honda', 'Japan', 'Japanese vehicle manufacturer'),
('Hyundai', 'hyundai', 'South Korea', 'South Korean vehicle manufacturer'),
('JAC', 'jac', 'China', 'Chinese vehicle manufacturer'),
('Kia', 'kia', 'South Korea', 'South Korean vehicle manufacturer'),
('Mazda', 'mazda', 'Japan', 'Japanese vehicle manufacturer'),
('Mercedes-Benz', 'mercedes-benz', 'Germany', 'German premium manufacturer'),
('Nissan', 'nissan', 'Japan', 'Japanese vehicle manufacturer'),
('Opel', 'opel', 'Germany', 'German vehicle manufacturer'),
('Peugeot', 'peugeot', 'France', 'French vehicle manufacturer'),
('Renault', 'renault', 'France', 'French vehicle manufacturer'),
('SEAT', 'seat', 'Spain', 'Spanish vehicle manufacturer'),
('Skoda', 'skoda', 'Czech Republic', 'Czech vehicle manufacturer'),
('Toyota', 'toyota', 'Japan', 'Japanese vehicle manufacturer'),
('Volkswagen', 'volkswagen', 'Germany', 'German vehicle manufacturer'),
('Volvo', 'volvo', 'Sweden', 'Swedish vehicle manufacturer');

INSERT INTO brand_models (brand_id, model_name, slug, generation, year_from, year_to)
SELECT b.id, v.model_name, v.slug, v.generation, v.year_from, v.year_to
FROM brands b
JOIN (
    VALUES
    ('audi', 'A3', 'a3', '8V', 2012, 2020),
    ('audi', 'A4', 'a4', 'B9', 2015, NULL),
    ('bmw', '3 Series', '3-series', 'G20', 2018, NULL),
    ('bmw', '5 Series', '5-series', 'G30', 2016, NULL),
    ('bmw', 'X5', 'x5', 'G05', 2018, NULL),
    ('byd', 'Atto 3', 'atto-3', '1st', 2022, NULL),
    ('ford', 'Focus', 'focus', 'Mk4', 2018, NULL),
    ('ford', 'Fiesta', 'fiesta', 'Mk8', 2017, NULL),
    ('geely', 'Coolray', 'coolray', '1st', 2018, NULL),
    ('honda', 'Civic', 'civic', '11th', 2021, NULL),
    ('hyundai', 'Tucson', 'tucson', 'NX4', 2020, NULL),
    ('jac', 'S3', 's3', '2nd', 2019, NULL),
    ('kia', 'Sportage', 'sportage', 'NQ5', 2021, NULL),
    ('mazda', 'Mazda3', 'mazda3', 'BP', 2019, NULL),
    ('mercedes-benz', 'C-Class', 'c-class', 'W206', 2021, NULL),
    ('nissan', 'Qashqai', 'qashqai', 'J12', 2021, NULL),
    ('peugeot', '308', '308', 'P5', 2021, NULL),
    ('renault', 'Megane', 'megane', 'IV', 2016, NULL),
    ('seat', 'Leon', 'leon', 'KL', 2020, NULL),
    ('skoda', 'Octavia', 'octavia', 'NX', 2019, NULL),
    ('toyota', 'Corolla', 'corolla', 'E210', 2018, NULL),
    ('volkswagen', 'Golf', 'golf', 'Mk8', 2019, NULL),
    ('volkswagen', 'Passat', 'passat', 'B8', 2014, NULL),
    ('volvo', 'XC60', 'xc60', '2nd', 2017, NULL)
) AS v(brand_slug, model_name, slug, generation, year_from, year_to) ON v.brand_slug = b.slug;
