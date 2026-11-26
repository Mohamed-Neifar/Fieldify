--------------------------------------------------------------------------------
-- FIELDIFY (SPORTSPOT) — INITIAL REFERENCE DATA SEED SCRIPT
--------------------------------------------------------------------------------

-- Seed Sports Categories
INSERT INTO sports (name, icon, description) VALUES
('Football', '⚽', 'Terrains de football 5v5, 7v7 et 11v11 en gazon synthétique ou naturel'),
('Padel', '🎾', 'Pistes de padel panoramiques aux normes internationales'),
('Tennis', '🎾', 'Courts de tennis en terre battue et surface rapide'),
('Basket-ball', '🏀', 'Terrains de basket couverts et en plein air'),
('Volley-ball', '🏐', 'Terrains de beach volley et volley en salle'),
('Golf', '⛳', 'Parcours d''entraînement, putting green et driving range'),
('Piscine privée', '🏊', 'Espaces aquatiques privés et espaces lounge')
ON CONFLICT (name) DO NOTHING;
