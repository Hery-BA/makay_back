-- V4__expedition_map.sql
-- Points de carte pour les expéditions

CREATE TABLE expedition_waypoints (
                                      id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                      expedition_id UUID NOT NULL REFERENCES expeditions(id) ON DELETE CASCADE,
                                      name          VARCHAR(255),
                                      description   TEXT,
                                      latitude      DECIMAL(10, 7) NOT NULL,
                                      longitude     DECIMAL(10, 7) NOT NULL,
                                      type          VARCHAR(50) NOT NULL DEFAULT 'POINT',
                                      sort_order    INTEGER DEFAULT 0
);

-- type : START, END, POINT_OF_INTEREST, CAMP

CREATE INDEX idx_waypoints_expedition ON expedition_waypoints(expedition_id);

-- Données de test pour la traversée Makay Nord-Sud
WITH exp AS (SELECT id FROM expeditions WHERE slug = 'traversee-makay-nord-sud')
INSERT INTO expedition_waypoints (expedition_id, name, description, latitude, longitude, type, sort_order)
SELECT exp.id, nom, desc_, lat, lng, type_, ordre FROM exp,
                                                       (VALUES
                                                            ('Beroroha (départ)',      'Point de départ de l''expédition',    -21.6667, 45.1667, 'START',            1),
                                                            ('Entrée Makay',          'Entrée dans le massif du Makay',       -21.8000, 45.0500, 'POINT_OF_INTEREST', 2),
                                                            ('Canyon des Baobabs',    'Canyon spectaculaire aux parois rouges',-22.0000, 44.9000, 'POINT_OF_INTEREST', 3),
                                                            ('Camp nuit 3',           'Bivouac au pied des falaises',         -22.1500, 44.8000, 'CAMP',              4),
                                                            ('Piscines de Cristal',   'Bassins naturels d''eau limpide',      -22.3000, 44.7500, 'POINT_OF_INTEREST', 5),
                                                            ('Village Bara',          'Rencontre avec la communauté locale',  -22.4500, 44.6500, 'POINT_OF_INTEREST', 6),
                                                            ('Grand Canyon Sud',      'Formation géologique unique',          -22.6000, 44.6000, 'POINT_OF_INTEREST', 7),
                                                            ('Beronono (arrivée)',    'Point d''arrivée de l''expédition',    -22.7500, 44.5500, 'END',               8)
                                                       ) AS t(nom, desc_, lat, lng, type_, ordre);