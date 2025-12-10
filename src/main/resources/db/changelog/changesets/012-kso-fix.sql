-- =====================================================
-- KSO - Fix structure to match JKSO hierarchy
-- Correct 4-level structure:
--   Level 1: Podskupina (3 digits): 801
--   Level 2: Oddíl (3+1 digits): 801.1
--   Level 3: Pododdíl (3+2 digits): 801.11
--   Level 4: Konstrukčně materiálová charakteristika (3+2+1): 801.11.1
-- =====================================================

-- First, delete all existing data (in reverse order due to FK)
DELETE FROM building_classifications;

-- Extend code column to 15 characters for codes like 801.11.1
ALTER TABLE building_classifications ALTER COLUMN code TYPE VARCHAR(15);

-- =====================================================
-- Level 1: Podskupiny (3 digits)
-- =====================================================

-- 801 Budovy občanské výstavby
INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
VALUES ('801', 'Budovy občanské výstavby', 'Civil Buildings', 1, NULL, true, 1, CURRENT_TIMESTAMP);

-- 802 Haly občanské výstavby
INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
VALUES ('802', 'Haly občanské výstavby', 'Civil Halls', 1, NULL, true, 2, CURRENT_TIMESTAMP);

-- 803 Budovy pro bydlení
INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
VALUES ('803', 'Budovy pro bydlení', 'Residential Buildings', 1, NULL, true, 3, CURRENT_TIMESTAMP);

-- 811 Haly pro výrobu a služby
INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
VALUES ('811', 'Haly pro výrobu a služby', 'Production and Service Halls', 1, NULL, true, 4, CURRENT_TIMESTAMP);

-- 812 Budovy pro výrobu a služby
INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
VALUES ('812', 'Budovy pro výrobu a služby', 'Production and Service Buildings', 1, NULL, true, 5, CURRENT_TIMESTAMP);

-- 813 Věže, stožáry, komíny
INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
VALUES ('813', 'Věže, stožáry, komíny', 'Towers, Masts, Chimneys', 1, NULL, true, 6, CURRENT_TIMESTAMP);

-- 814 Nádrže a jímky čistíren vod a ostatní pozemní nádrže a jímky
INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
VALUES ('814', 'Nádrže a jímky čistíren vod a ostatní pozemní nádrže a jímky', 'Water Treatment Tanks and Other Ground Tanks', 1, NULL, true, 7, CURRENT_TIMESTAMP);

-- 815 Objekty pozemní zvláštní
INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
VALUES ('815', 'Objekty pozemní zvláštní', 'Special Ground Structures', 1, NULL, true, 8, CURRENT_TIMESTAMP);

-- 817 Objekty jaderných zařízení
INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
VALUES ('817', 'Objekty jaderných zařízení', 'Nuclear Facility Structures', 1, NULL, true, 9, CURRENT_TIMESTAMP);

-- 821 Mosty
INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
VALUES ('821', 'Mosty', 'Bridges', 1, NULL, true, 10, CURRENT_TIMESTAMP);

-- 822 Komunikace pozemní a letiště
INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
VALUES ('822', 'Komunikace pozemní a letiště', 'Ground Roads and Airports', 1, NULL, true, 11, CURRENT_TIMESTAMP);

-- 823 Plochy a úpravy území
INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
VALUES ('823', 'Plochy a úpravy území', 'Areas and Land Improvements', 1, NULL, true, 12, CURRENT_TIMESTAMP);

-- 824 Dráhy kolejové
INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
VALUES ('824', 'Dráhy kolejové', 'Railways', 1, NULL, true, 13, CURRENT_TIMESTAMP);

-- 825 Objekty podzemní (mimo důlní)
INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
VALUES ('825', 'Objekty podzemní (mimo důlní)', 'Underground Structures (non-mining)', 1, NULL, true, 14, CURRENT_TIMESTAMP);

-- 826 Objekty podzemní důlní
INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
VALUES ('826', 'Objekty podzemní důlní', 'Mining Underground Structures', 1, NULL, true, 15, CURRENT_TIMESTAMP);

-- 827 Vedení trubní dálková a přípojná
INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
VALUES ('827', 'Vedení trubní dálková a přípojná', 'Long-distance and Connection Pipelines', 1, NULL, true, 16, CURRENT_TIMESTAMP);

-- 828 Vedení elektrická a dráhy visuté
INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
VALUES ('828', 'Vedení elektrická a dráhy visuté', 'Electrical Lines and Cable Cars', 1, NULL, true, 17, CURRENT_TIMESTAMP);

-- 831 Hydromeliorace
INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
VALUES ('831', 'Hydromeliorace', 'Hydromelioration', 1, NULL, true, 18, CURRENT_TIMESTAMP);

-- 832 Hráze a objekty na tocích
INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
VALUES ('832', 'Hráze a objekty na tocích', 'Dams and River Structures', 1, NULL, true, 19, CURRENT_TIMESTAMP);

-- 833 Nádrže na tocích, úpravy toků a kanály
INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
VALUES ('833', 'Nádrže na tocích, úpravy toků a kanály', 'Reservoirs, River Improvements and Canals', 1, NULL, true, 20, CURRENT_TIMESTAMP);

-- =====================================================
-- Level 2: Oddíly for 801 Budovy občanské výstavby
-- =====================================================

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '801.1', 'Budovy pro zdravotní péči', 'Healthcare Buildings', 2, id, true, 1, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '801';

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '801.2', 'Budovy pro komunální služby a osobní hygienu', 'Buildings for Municipal Services and Personal Hygiene', 2, id, true, 2, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '801';

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '801.3', 'Budovy pro výuku a výchovu', 'Buildings for Education and Training', 2, id, true, 3, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '801';

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '801.4', 'Budovy pro vědu, kulturu a osvětu', 'Buildings for Science, Culture and Enlightenment', 2, id, true, 4, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '801';

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '801.5', 'Budovy pro tělovýchovu', 'Sports Buildings', 2, id, true, 5, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '801';

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '801.6', 'Budovy pro řízení, správu a administrativu', 'Administration Buildings', 2, id, true, 6, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '801';

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '801.7', 'Budovy pro společné ubytování a rekreaci', 'Accommodation and Recreation Buildings', 2, id, true, 7, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '801';

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '801.8', 'Budovy pro obchod a společné stravování', 'Retail and Catering Buildings', 2, id, true, 8, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '801';

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '801.9', 'Budovy pro sociální péči', 'Social Care Buildings', 2, id, true, 9, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '801';

-- =====================================================
-- Level 3: Pododdíly for 801.1 Budovy pro zdravotní péči
-- =====================================================

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '801.11', 'budovy nemocnic a nemocnic s poliklinikou', 'hospital buildings and hospitals with polyclinics', 3, id, true, 1, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '801.1';

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '801.12', 'polikliniky a střediska zdraví', 'polyclinics and health centers', 3, id, true, 2, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '801.1';

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '801.13', 'zdravotnická střediska', 'medical centers', 3, id, true, 3, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '801.1';

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '801.14', 'ambulatoria', 'ambulatories', 3, id, true, 4, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '801.1';

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '801.15', 'budovy lázeňské péče', 'spa care buildings', 3, id, true, 5, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '801.1';

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '801.16', 'budovy veterinární péče', 'veterinary care buildings', 3, id, true, 6, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '801.1';

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '801.19', 'budovy jinde neuvedené', 'other healthcare buildings not elsewhere classified', 3, id, true, 9, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '801.1';

-- =====================================================
-- Level 4: Konstrukčně materiálové charakteristiky for 801.11
-- =====================================================

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '801.11.1', 'svislá nosná konstrukce zděná z cihel, tvárnic, bloků', 'vertical load-bearing structure made of bricks, blocks', 4, id, true, 1, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '801.11';

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '801.11.2', 'svislá nosná konstrukce monolitická betonová tyčová', 'monolithic concrete rod vertical load-bearing structure', 4, id, true, 2, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '801.11';

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '801.11.3', 'svislá nosná konstrukce monolitická betonová plošná', 'monolithic concrete slab vertical load-bearing structure', 4, id, true, 3, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '801.11';

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '801.11.4', 'svislá nosná konstrukce montovaná z dílců betonových tyčových', 'prefabricated concrete rod vertical load-bearing structure', 4, id, true, 4, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '801.11';

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '801.11.5', 'svislá nosná konstrukce montovaná z dílců betonových plošných', 'prefabricated concrete slab vertical load-bearing structure', 4, id, true, 5, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '801.11';

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '801.11.6', 'svislá nosná konstrukce montovaná z prostorových buněk', 'prefabricated modular vertical load-bearing structure', 4, id, true, 6, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '801.11';

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '801.11.7', 'svislá nosná konstrukce kovová', 'metal vertical load-bearing structure', 4, id, true, 7, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '801.11';

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '801.11.8', 'svislá nosná konstrukce dřevěná a na bázi dřevní hmoty', 'wooden vertical load-bearing structure', 4, id, true, 8, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '801.11';

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '801.11.9', 'svislá nosná konstrukce z jiných materiálů', 'vertical load-bearing structure from other materials', 4, id, true, 9, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '801.11';

-- =====================================================
-- Level 2: Oddíly for 803 Budovy pro bydlení (sample)
-- =====================================================

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '803.1', 'Budovy jednobytové', 'Single-family houses', 2, id, true, 1, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '803';

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '803.2', 'Budovy dvojbytové', 'Two-family houses', 2, id, true, 2, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '803';

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '803.3', 'Domy bytové typové', 'Standard apartment buildings', 2, id, true, 3, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '803';

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '803.4', 'Domy bytové netypové', 'Non-standard apartment buildings', 2, id, true, 4, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '803';

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '803.5', 'Domovy důchodců a penziony pro důchodce', 'Retirement homes and pensioners hostels', 2, id, true, 5, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '803';

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '803.6', 'Budovy pro přechodné ubytování', 'Temporary accommodation buildings', 2, id, true, 6, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '803';

INSERT INTO building_classifications (code, name_cs, name_en, level, parent_id, active, sort_order, created_at)
SELECT '803.9', 'Budovy pro bydlení jinde neuvedené', 'Residential buildings not elsewhere classified', 2, id, true, 9, CURRENT_TIMESTAMP
FROM building_classifications WHERE code = '803';
