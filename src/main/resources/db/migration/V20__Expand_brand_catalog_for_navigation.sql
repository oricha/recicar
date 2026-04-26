-- Additional vehicle brands for portal navigation (target: 200+ total over time; extends V14)
INSERT INTO brands (name, slug, country, description) VALUES
('Abarth', 'abarth', 'Italy', 'Performance brand of Stellantis'),
('Alfa Romeo', 'alfa-romeo', 'Italy', 'Italian automaker'),
('Aston Martin', 'aston-martin', 'United Kingdom', 'British sports cars'),
('Bentley', 'bentley', 'United Kingdom', 'British luxury'),
('BYTON', 'byton', 'China', 'EV startup'),
('Cadillac', 'cadillac', 'United States', 'GM luxury'),
('Changan', 'changan', 'China', 'Chinese automaker'),
('Chrysler', 'chrysler', 'United States', 'Stellantis brand'),
('Cupra', 'cupra', 'Spain', 'SEAT performance sub-brand'),
('Dacia', 'dacia', 'Romania', 'Renault budget brand'),
('Daewoo', 'daewoo', 'South Korea', 'Historic Korean brand'),
('Daihatsu', 'daihatsu', 'Japan', 'Subcompact and kei'),
('Dodge', 'dodge', 'United States', 'Stellantis performance'),
('DS Automobiles', 'ds', 'France', 'Premium PSA/Stellantis'),
('Ferrari', 'ferrari', 'Italy', 'Sports and racing'),
('Fisker', 'fisker', 'United States', 'EV manufacturer'),
('GAC', 'gac', 'China', 'Guangzhou Auto'),
('GAZ', 'gaz', 'Russia', 'Commercial and passenger'),
('GWM', 'gwm', 'China', 'Great Wall Motors'),
('Haval', 'haval', 'China', 'GWM SUV sub-brand'),
('Holden', 'holden', 'Australia', 'Historic Australian GM'),
('Hummer', 'hummer', 'United States', 'GM off-road'),
('Ineos', 'ineos', 'United Kingdom', 'Ineos Automotive'),
('Isuzu', 'isuzu', 'Japan', 'Commercial and SUV'),
('Iveco', 'iveco', 'Italy', 'Commercial vehicles'),
('Jaguar', 'jaguar', 'United Kingdom', 'British premium'),
('Jeep', 'jeep', 'United States', 'SUV and off-road'),
('Lada', 'lada', 'Russia', 'AvtoVAZ'),
('Lamborghini', 'lamborghini', 'Italy', 'Sports/super cars'),
('Lancia', 'lancia', 'Italy', 'Stellantis Italy'),
('Land Rover', 'land-rover', 'United Kingdom', 'JLR SUVs'),
('Lexus', 'lexus', 'Japan', 'Toyota premium'),
('Lotus', 'lotus', 'United Kingdom', 'Sports and EV'),
('Lucid', 'lucid', 'United States', 'Luxury EV'),
('MAN', 'man', 'Germany', 'Commercial Paccar group'),
('Maserati', 'maserati', 'Italy', 'Stellantis performance luxury'),
('McLaren', 'mclaren', 'United Kingdom', 'Supercars'),
('MG', 'mg', 'China', 'Owned by SAIC'),
('Microcar', 'microcar', 'France', 'Quadricycle'),
('Mini', 'mini', 'United Kingdom', 'BMW sub-brand'),
('Mitsubishi', 'mitsubishi', 'Japan', 'Renault-Nissan-Mitsubishi'),
('Morgan', 'morgan', 'United Kingdom', 'Hand-built sports'),
('NIO', 'nio', 'China', 'Premium EV'),
('Perodua', 'perodua', 'Malaysia', 'National brand'),
('Polestar', 'polestar', 'Sweden', 'Volvo performance EV'),
('Porsche', 'porsche', 'Germany', 'Sports and SUV'),
('Proton', 'proton', 'Malaysia', 'National automaker'),
('RAM', 'ram', 'United States', 'Stellantis trucks'),
('Rivian', 'rivian', 'United States', 'Adventure EV'),
('Rover', 'rover', 'United Kingdom', 'Historic marque'),
('SAAB', 'saab', 'Sweden', 'Historic'),
('Santana', 'santana', 'China', 'Historic VWA Shanghai'),
('SMA', 'sma', 'China', 'Shanghai Maple'),
('Smart', 'smart', 'China', 'Mercedes-Geely joint'),
('Sokon', 'sokon', 'China', 'Sokon / DFSK'),
('SsangYong', 'ssangyong', 'South Korea', 'KGM rebrand path'),
('Subaru', 'subaru', 'Japan', 'AWD specialist'),
('Suzuki', 'suzuki', 'Japan', 'Compact and kei'),
('Tata', 'tata', 'India', 'Tata Motors'),
('Tesla', 'tesla', 'United States', 'EV leader'),
('Think', 'think', 'Norway', 'Historic small EV'),
('UAZ', 'uaz', 'Russia', 'Off-road and utility'),
('Vauxhall', 'vauxhall', 'United Kingdom', 'Vauxhall/Opel UK'),
('Vinfast', 'vinfast', 'Vietnam', 'Vietnam EV and ICE'),
('Westfield', 'westfield', 'United Kingdom', 'Kits and sports'),
('Wuling', 'wuling', 'China', 'SGMW microvans and EV'),
('XPeng', 'xpeng', 'China', 'Smart EV'),
('Acura', 'acura', 'United States', 'Honda US premium'),
('Alpina', 'alpina', 'Germany', 'BMW tuner'),
('Baojun', 'baojun', 'China', 'SAIC-GM-Wuling'),
('Bestune', 'bestune', 'China', 'FAW brand'),
('Brilliance', 'brilliance', 'China', 'Brilliance Auto'),
('Buick', 'buick', 'United States', 'GM brand'),
('Chevrolet', 'chevrolet', 'United States', 'GM global'),
('Dongfeng', 'dongfeng', 'China', 'DFM parent'),
('FAW', 'faw', 'China', 'First Auto Works'),
('Gonow', 'gonow', 'China', 'Guangzhou Gonow'),
('Hawtai', 'hawtai', 'China', 'Inner Mongolia'),
('Higer', 'higer', 'China', 'Buses and vans'),
('Hongqi', 'hongqi', 'China', 'FAW premium'),
('Infiniti', 'infiniti', 'Japan', 'Nissan premium')
ON CONFLICT (slug) DO NOTHING;

-- Sample models for added brands (H2-friendly: one insert per model)
INSERT INTO brand_models (brand_id, model_name, slug, generation, year_from, year_to)
SELECT b.id, '911', '911', '992', 2019, NULL FROM brands b WHERE b.slug = 'porsche'
AND NOT EXISTS (SELECT 1 FROM brand_models m WHERE m.brand_id = b.id AND m.slug = '911');
INSERT INTO brand_models (brand_id, model_name, slug, generation, year_from, year_to)
SELECT b.id, 'Cayenne', 'cayenne', '3rd', 2017, NULL FROM brands b WHERE b.slug = 'porsche'
AND NOT EXISTS (SELECT 1 FROM brand_models m WHERE m.brand_id = b.id AND m.slug = 'cayenne');
INSERT INTO brand_models (brand_id, model_name, slug, generation, year_from, year_to)
SELECT b.id, 'F-Pace', 'f-pace', '1st', 2016, NULL FROM brands b WHERE b.slug = 'jaguar'
AND NOT EXISTS (SELECT 1 FROM brand_models m WHERE m.brand_id = b.id AND m.slug = 'f-pace');
INSERT INTO brand_models (brand_id, model_name, slug, generation, year_from, year_to)
SELECT b.id, 'Discovery', 'discovery', '5', 2016, NULL FROM brands b WHERE b.slug = 'land-rover'
AND NOT EXISTS (SELECT 1 FROM brand_models m WHERE m.brand_id = b.id AND m.slug = 'discovery');
INSERT INTO brand_models (brand_id, model_name, slug, generation, year_from, year_to)
SELECT b.id, 'Model 3', 'model-3', '1st', 2017, NULL FROM brands b WHERE b.slug = 'tesla'
AND NOT EXISTS (SELECT 1 FROM brand_models m WHERE m.brand_id = b.id AND m.slug = 'model-3');
INSERT INTO brand_models (brand_id, model_name, slug, generation, year_from, year_to)
SELECT b.id, 'Model Y', 'model-y', '1st', 2020, NULL FROM brands b WHERE b.slug = 'tesla'
AND NOT EXISTS (SELECT 1 FROM brand_models m WHERE m.brand_id = b.id AND m.slug = 'model-y');
INSERT INTO brand_models (brand_id, model_name, slug, generation, year_from, year_to)
SELECT b.id, 'RX', 'rx', '5th', 2022, NULL FROM brands b WHERE b.slug = 'lexus'
AND NOT EXISTS (SELECT 1 FROM brand_models m WHERE m.brand_id = b.id AND m.slug = 'rx');
INSERT INTO brand_models (brand_id, model_name, slug, generation, year_from, year_to)
SELECT b.id, 'Cooper', 'cooper', 'F55', 2014, NULL FROM brands b WHERE b.slug = 'mini'
AND NOT EXISTS (SELECT 1 FROM brand_models m WHERE m.brand_id = b.id AND m.slug = 'cooper');
INSERT INTO brand_models (brand_id, model_name, slug, generation, year_from, year_to)
SELECT b.id, 'Outlander', 'outlander', '4th', 2021, NULL FROM brands b WHERE b.slug = 'mitsubishi'
AND NOT EXISTS (SELECT 1 FROM brand_models m WHERE m.brand_id = b.id AND m.slug = 'outlander');
INSERT INTO brand_models (brand_id, model_name, slug, generation, year_from, year_to)
SELECT b.id, 'Swift', 'swift', '4th', 2017, NULL FROM brands b WHERE b.slug = 'suzuki'
AND NOT EXISTS (SELECT 1 FROM brand_models m WHERE m.brand_id = b.id AND m.slug = 'swift');
