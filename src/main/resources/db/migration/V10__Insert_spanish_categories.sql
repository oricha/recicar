-- Insert Spanish categories for the Car Parts Marketplace
-- This migration adds the categories shown in the categories page design

-- Insert Spanish categories matching the design
INSERT INTO categories (name, description, slug, active, sort_order, created_at) VALUES
('Caja de cambios/embrague/transmisión', 'Sistema de transmisión y embrague', 'caja-cambios-embrague-transmision', true, 1, CURRENT_TIMESTAMP),
('Carrocería/repuestos de carrocería/gancho', 'Componentes de carrocería y ganchos', 'carroceria-repuestos-gancho', true, 2, CURRENT_TIMESTAMP),
('Dispositivos/interruptores/sistema electrónico', 'Sistemas electrónicos e interruptores', 'dispositivos-interruptores-electronico', true, 3, CURRENT_TIMESTAMP),
('Eje delantero', 'Componentes del eje delantero', 'eje-delantero', true, 4, CURRENT_TIMESTAMP),
('Eje trasero', 'Componentes del eje trasero', 'eje-trasero', true, 5, CURRENT_TIMESTAMP),
('Habitáculo/interior', 'Componentes del interior del vehículo', 'habitaculo-interior', true, 6, CURRENT_TIMESTAMP),
('Lunas', 'Cristales y lunas del vehículo', 'lunas', true, 7, CURRENT_TIMESTAMP),
('Motor', 'Componentes del motor', 'motor', true, 8, CURRENT_TIMESTAMP),
('Otros repuestos', 'Repuestos diversos y accesorios', 'otros-repuestos', true, 9, CURRENT_TIMESTAMP),
('Puerta', 'Puertas y componentes de puertas', 'puerta', true, 10, CURRENT_TIMESTAMP),
('Radiador de calefacción/aire acondicionado', 'Sistema de climatización', 'radiador-calefaccion-aire-acondicionado', true, 11, CURRENT_TIMESTAMP),
('Repuestos de carrocería delantera exterior', 'Componentes frontales exteriores', 'repuestos-carroceria-delantera-exterior', true, 12, CURRENT_TIMESTAMP),
('Repuestos de la carrocería exterior trasera', 'Componentes traseros exteriores', 'repuestos-carroceria-exterior-trasera', true, 13, CURRENT_TIMESTAMP),
('Ruedas/neumáticos/tapacubos', 'Ruedas, neumáticos y tapacubos', 'ruedas-neumaticos-tapacubos', true, 14, CURRENT_TIMESTAMP),
('Sistema de frenos', 'Componentes del sistema de frenado', 'sistema-frenos', true, 15, CURRENT_TIMESTAMP),
('Sistema de iluminación', 'Sistema de luces y iluminación', 'sistema-iluminacion', true, 16, CURRENT_TIMESTAMP),
('Sistema de limpiado/lavado del faro', 'Sistema de limpieza de faros', 'sistema-limpiado-lavado-faro', true, 17, CURRENT_TIMESTAMP),
('Sistema de mezcla de combustible', 'Sistema de combustible', 'sistema-mezcla-combustible', true, 18, CURRENT_TIMESTAMP),
('Sistema del tubo de escape', 'Sistema de escape y escape', 'sistema-tubo-escape', true, 19, CURRENT_TIMESTAMP);