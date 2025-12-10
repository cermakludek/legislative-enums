-- =====================================================
-- KSO - Building Classifications Data
-- Source: https://www.cenovasoustava.cz/pJKSO.asp
-- =====================================================

-- Level 1: Obory (Branches)
INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
VALUES
('8', 'Stavební objekty', 'Building Objects', 1, NULL, true, 1, CURRENT_TIMESTAMP),
('9', 'Práce a dodávky PSV', 'PSV Works and Supplies', 1, NULL, true, 2, CURRENT_TIMESTAMP)
ON CONFLICT (code) DO NOTHING;

-- Level 2: Skupiny (Groups) - must be inserted after Level 1
INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '80', 'Budovy občanské výstavby a pro bydlení', 'Civil and Residential Buildings', 2, id, true, 1, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '8'
ON CONFLICT (code) DO NOTHING;

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '81', 'Haly a budovy pro výrobu a služby', 'Halls and Buildings for Production and Services', 2, id, true, 2, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '8'
ON CONFLICT (code) DO NOTHING;

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '82', 'Komunikace, plochy a úpravy území', 'Roads, Areas and Land Improvements', 2, id, true, 3, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '8'
ON CONFLICT (code) DO NOTHING;

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '83', 'Vodní díla', 'Water Works', 2, id, true, 4, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '8'
ON CONFLICT (code) DO NOTHING;

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '92', 'Modernizace a rekonstrukce', 'Modernization and Reconstruction', 2, id, true, 5, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '9'
ON CONFLICT (code) DO NOTHING;

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '93', 'Demolice a bourání', 'Demolition and Dismantling', 2, id, true, 6, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '9'
ON CONFLICT (code) DO NOTHING;

-- Level 3: Podskupiny (Subgroups) - Budovy občanské výstavby a pro bydlení (80)
INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '801', 'Budovy občanské výstavby', 'Civil Buildings', 3, id, true, 1, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '80'
ON CONFLICT (code) DO NOTHING;

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '802', 'Haly občanské výstavby', 'Civil Halls', 3, id, true, 2, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '80'
ON CONFLICT (code) DO NOTHING;

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '803', 'Budovy pro bydlení', 'Residential Buildings', 3, id, true, 3, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '80'
ON CONFLICT (code) DO NOTHING;

-- Level 3: Podskupiny - Haly a budovy pro výrobu a služby (81)
INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '811', 'Haly pro výrobu a služby', 'Production and Service Halls', 3, id, true, 1, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '81'
ON CONFLICT (code) DO NOTHING;

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '812', 'Budovy pro výrobu a služby', 'Production and Service Buildings', 3, id, true, 2, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '81'
ON CONFLICT (code) DO NOTHING;

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '813', 'Věže, stožáry, komíny', 'Towers, Masts, Chimneys', 3, id, true, 3, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '81'
ON CONFLICT (code) DO NOTHING;

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '814', 'Nádrže a jímky čistíren vod a ostatní pozemní nádrže', 'Tanks and Pits for Water Treatment Plants', 3, id, true, 4, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '81'
ON CONFLICT (code) DO NOTHING;

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '815', 'Objekty pozemní zvláštní', 'Special Ground Structures', 3, id, true, 5, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '81'
ON CONFLICT (code) DO NOTHING;

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '817', 'Objekty jaderných zařízení', 'Nuclear Facility Structures', 3, id, true, 6, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '81'
ON CONFLICT (code) DO NOTHING;

-- Level 3: Podskupiny - Komunikace, plochy a úpravy území (82)
INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '821', 'Mosty', 'Bridges', 3, id, true, 1, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '82'
ON CONFLICT (code) DO NOTHING;

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '822', 'Komunikace pozemní a letiště', 'Ground Roads and Airports', 3, id, true, 2, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '82'
ON CONFLICT (code) DO NOTHING;

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '823', 'Plochy a úpravy území', 'Areas and Land Improvements', 3, id, true, 3, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '82'
ON CONFLICT (code) DO NOTHING;

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '824', 'Dráhy kolejové', 'Railways', 3, id, true, 4, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '82'
ON CONFLICT (code) DO NOTHING;

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '825', 'Objekty podzemní (mimo důlní)', 'Underground Structures (non-mining)', 3, id, true, 5, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '82'
ON CONFLICT (code) DO NOTHING;

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '826', 'Objekty podzemní důlní', 'Mining Underground Structures', 3, id, true, 6, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '82'
ON CONFLICT (code) DO NOTHING;

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '827', 'Vedení trubní dálková a přípojná', 'Long-distance and Connection Pipelines', 3, id, true, 7, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '82'
ON CONFLICT (code) DO NOTHING;

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '828', 'Vedení elektrická a dráhy visuté', 'Electrical Lines and Cable Cars', 3, id, true, 8, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '82'
ON CONFLICT (code) DO NOTHING;

-- Level 3: Podskupiny - Vodní díla (83)
INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '831', 'Hydromeliorace', 'Hydromelioration', 3, id, true, 1, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '83'
ON CONFLICT (code) DO NOTHING;

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '832', 'Hráze a objekty na tocích', 'Dams and River Structures', 3, id, true, 2, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '83'
ON CONFLICT (code) DO NOTHING;

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '833', 'Nádrže na tocích, úpravy toků a kanály', 'Reservoirs, River Improvements and Canals', 3, id, true, 3, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '83'
ON CONFLICT (code) DO NOTHING;

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '838', 'Práce stavební při budování technologických zařízení', 'Construction Works for Technological Equipment', 3, id, true, 4, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '83'
ON CONFLICT (code) DO NOTHING;

-- Level 3: Podskupiny - Modernizace a rekonstrukce (92)
INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '927', 'Modernizace', 'Modernization', 3, id, true, 1, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '92'
ON CONFLICT (code) DO NOTHING;

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '928', 'Opravy a údržba', 'Repairs and Maintenance', 3, id, true, 2, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '92'
ON CONFLICT (code) DO NOTHING;

-- Level 3: Podskupiny - Demolice a bourání (93)
INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '937', 'Demolice', 'Demolition', 3, id, true, 1, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '93'
ON CONFLICT (code) DO NOTHING;

-- Register KSO in codelist_registry
INSERT INTO codelist_registry (code, name_cs, name_en, description_cs, description_en, web_url, api_url, icon_class, active, sort_order, created_at)
VALUES
('KSO', 'Klasifikace stavebních objektů (KSO)', 'Building Classifications (KSO)',
 'Víceúrovňový číselník klasifikace stavebních objektů dle JKSO',
 'Multi-level building object classification according to JKSO',
 '/web/building-classifications', '/api/v1/building-classifications', 'bi bi-building-fill-gear', true, 30, CURRENT_TIMESTAMP)
ON CONFLICT (code) DO NOTHING;
