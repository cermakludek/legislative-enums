-- Liquibase changeset for importing full ČÚZK codelist data
-- Source: https://cuzk.gov.cz/Katastr-nemovitosti/Poskytovani-udaju-z-KN/Ciselniky-ISKN/Ciselniky-k-nemovitosti.aspx

-- =====================================================
-- SC_D_POZEMKU - Land Types (Druh pozemku)
-- =====================================================
DELETE FROM cuzk_land_types;

INSERT INTO cuzk_land_types (code, name_cs, name_en, abbreviation, agricultural_land, land_parcel_type_code, building_parcel, mandatory_land_protection, mandatory_land_use, valid_from, valid_to, active, sort_order, created_at)
VALUES
('2', 'orná půda', 'arable land', 'orná půda', true, NULL, false, true, false, '1993-01-01', NULL, true, 1, CURRENT_TIMESTAMP),
('3', 'chmelnice', 'hop field', 'chmelnice', true, '302', false, true, false, '1993-01-01', NULL, true, 2, CURRENT_TIMESTAMP),
('4', 'vinice', 'vineyard', 'vinice', true, '303', false, true, false, '1993-01-01', NULL, true, 3, CURRENT_TIMESTAMP),
('5', 'zahrada', 'garden', 'zahrada', true, '304', false, true, false, '1993-01-01', NULL, true, 4, CURRENT_TIMESTAMP),
('6', 'ovocný sad', 'orchard', 'ovoc. sad', true, '305', false, true, false, '1993-01-01', NULL, true, 5, CURRENT_TIMESTAMP),
('7', 'trvalý travní porost', 'permanent grassland', 'travní p.', true, '306', false, true, false, '1993-01-01', NULL, true, 6, CURRENT_TIMESTAMP),
('8', 'trvalý travní porost', 'permanent grassland (historical)', 'travní p.', true, '307', false, true, false, '1993-01-01', '2000-09-01', false, 7, CURRENT_TIMESTAMP),
('10', 'lesní pozemek', 'forest land', 'lesní poz', false, '308', false, true, false, '1993-01-01', NULL, true, 8, CURRENT_TIMESTAMP),
('11', 'vodní plocha', 'water area', 'vodní pl.', false, NULL, false, false, true, '1993-01-01', NULL, true, 9, CURRENT_TIMESTAMP),
('13', 'zastavěná plocha a nádvoří', 'built-up area and courtyard', 'zast. pl.', false, NULL, true, false, false, '1993-01-01', NULL, true, 10, CURRENT_TIMESTAMP),
('14', 'ostatní plocha', 'other area', 'ostat.pl.', false, NULL, false, false, true, '1993-01-01', NULL, true, 11, CURRENT_TIMESTAMP);

-- =====================================================
-- SC_ZP_URCENI_VYMERY - Area Determination Method
-- =====================================================
DELETE FROM cuzk_area_determinations;

INSERT INTO cuzk_area_determinations (code, name_cs, name_en, valid_from, valid_to, active, sort_order, created_at)
VALUES
('0', 'Graficky nebo v digitalizované mapě', 'Graphically or in digitized map', '1993-01-01', NULL, true, 1, CURRENT_TIMESTAMP),
('1', 'Jiným číselným způsobem', 'By other numerical method', '1993-01-01', NULL, true, 2, CURRENT_TIMESTAMP),
('2', 'Ze souřadnic v S-JTSK', 'From S-JTSK coordinates', '1993-01-01', NULL, true, 3, CURRENT_TIMESTAMP);

-- =====================================================
-- SC_ZP_VYUZITI_POZ - Land Use (Způsob využití pozemku)
-- =====================================================
DELETE FROM cuzk_land_uses;

INSERT INTO cuzk_land_uses (code, name_cs, name_en, abbreviation, land_parcel_type_code, valid_from, valid_to, active, sort_order, created_at)
VALUES
('1', 'skleník, pařeniště', 'greenhouse, hotbed', 'skleník-pařeniš.', NULL, '1993-01-01', NULL, true, 1, CURRENT_TIMESTAMP),
('2', 'školka', 'nursery', 'školka', NULL, '1993-01-01', NULL, true, 2, CURRENT_TIMESTAMP),
('3', 'plantáž dřevin', 'tree plantation', 'plantáž dřevin', NULL, '1998-10-15', NULL, true, 3, CURRENT_TIMESTAMP),
('4', 'les jiný než hospodářský', 'non-commercial forest', 'les(ne hospodář)', NULL, '1993-01-01', NULL, true, 4, CURRENT_TIMESTAMP),
('5', 'lesní pozemek, na kterém je budova', 'forest land with building', 'les s budovou', NULL, '1993-01-01', NULL, true, 5, CURRENT_TIMESTAMP),
('6', 'rybník', 'pond', 'rybník', '803', '1993-01-01', NULL, true, 6, CURRENT_TIMESTAMP),
('7', 'koryto vodního toku přirozené nebo upravené', 'natural or modified watercourse bed', 'tok přirozený', '802', '1993-01-01', NULL, true, 7, CURRENT_TIMESTAMP),
('8', 'koryto vodního toku umělé', 'artificial watercourse bed', 'tok umělý', '802', '1993-01-01', NULL, true, 8, CURRENT_TIMESTAMP),
('9', 'vodní nádrž přírodní', 'natural reservoir', 'nádrž přírodní', '803', '1993-01-01', NULL, true, 9, CURRENT_TIMESTAMP),
('10', 'vodní nádrž umělá', 'artificial reservoir', 'nádrž umělá', '803', '1993-01-01', NULL, true, 10, CURRENT_TIMESTAMP),
('11', 'zamokřená plocha', 'wetland', 'zamokřená pl.', '804', '1993-01-01', NULL, true, 11, CURRENT_TIMESTAMP),
('12', 'společný dvůr', 'common courtyard', 'společný dvůr', '319', '1993-01-01', NULL, true, 12, CURRENT_TIMESTAMP),
('13', 'zbořeniště', 'demolition site', 'zbořeniště', '319', '1993-01-01', NULL, true, 13, CURRENT_TIMESTAMP),
('14', 'dráha', 'railway', 'dráha', NULL, '1993-01-01', NULL, true, 14, CURRENT_TIMESTAMP),
('15', 'dálnice', 'motorway', 'dálnice', NULL, '1993-01-01', NULL, true, 15, CURRENT_TIMESTAMP),
('16', 'silnice', 'road', 'silnice', NULL, '1993-01-01', NULL, true, 16, CURRENT_TIMESTAMP),
('17', 'ostatní komunikace', 'other road', 'ostat.komunikace', NULL, '1993-01-01', NULL, true, 17, CURRENT_TIMESTAMP),
('18', 'ostatní dopravní plocha', 'other transport area', 'ost.dopravní pl.', NULL, '1993-01-01', NULL, true, 18, CURRENT_TIMESTAMP),
('19', 'zeleň', 'greenery', 'zeleň', '314', '1993-01-01', NULL, true, 19, CURRENT_TIMESTAMP),
('20', 'sportoviště a rekreační plocha', 'sports and recreation area', 'sport.a rekr.pl.', NULL, '1993-01-01', NULL, true, 20, CURRENT_TIMESTAMP),
('21', 'pohřebiště', 'cemetery', 'pohřeb.', '315', '1993-01-01', NULL, true, 21, CURRENT_TIMESTAMP),
('22', 'kulturní a osvětová plocha', 'cultural and educational area', 'kult.a osvět.pl.', NULL, '1993-01-01', NULL, true, 22, CURRENT_TIMESTAMP),
('23', 'manipulační plocha', 'handling area', 'manipulační pl.', NULL, '1993-01-01', NULL, true, 23, CURRENT_TIMESTAMP),
('24', 'dobývací prostor', 'mining area', 'dobývací prost.', '701', '1993-01-01', NULL, true, 24, CURRENT_TIMESTAMP),
('25', 'skládka', 'dump', 'skládka', NULL, '1993-01-01', NULL, true, 25, CURRENT_TIMESTAMP),
('26', 'jiná plocha', 'other area', 'jiná plocha', NULL, '1993-01-01', NULL, true, 26, CURRENT_TIMESTAMP),
('27', 'neplodná půda', 'barren land', 'neplodná půda', '316', '1993-01-01', NULL, true, 27, CURRENT_TIMESTAMP),
('28', 'vodní plocha, na které je budova', 'water area with building', 'vod.pl.s budovou', NULL, '2007-03-01', NULL, true, 28, CURRENT_TIMESTAMP),
('29', 'fotovoltaická elektrárna', 'photovoltaic power plant', 'foto. elektrárna', NULL, '2014-01-01', NULL, true, 29, CURRENT_TIMESTAMP),
('30', 'mez, stráň', 'balk, slope', 'mez, stráň', NULL, '2017-04-01', NULL, true, 30, CURRENT_TIMESTAMP);

-- =====================================================
-- SC_POZEMEK_VYUZITI - Land Type Use mapping
-- =====================================================
DELETE FROM cuzk_land_type_uses;

INSERT INTO cuzk_land_type_uses (land_type_code, land_use_code, active, created_at)
VALUES
('2', '1', true, CURRENT_TIMESTAMP),
('2', '2', true, CURRENT_TIMESTAMP),
('2', '3', true, CURRENT_TIMESTAMP),
('2', '29', true, CURRENT_TIMESTAMP),
('2', '30', true, CURRENT_TIMESTAMP),
('3', '1', true, CURRENT_TIMESTAMP),
('3', '29', true, CURRENT_TIMESTAMP),
('3', '30', true, CURRENT_TIMESTAMP),
('4', '1', true, CURRENT_TIMESTAMP),
('4', '29', true, CURRENT_TIMESTAMP),
('4', '30', true, CURRENT_TIMESTAMP),
('5', '1', true, CURRENT_TIMESTAMP),
('5', '29', true, CURRENT_TIMESTAMP),
('5', '30', true, CURRENT_TIMESTAMP),
('6', '1', true, CURRENT_TIMESTAMP),
('6', '29', true, CURRENT_TIMESTAMP),
('6', '30', true, CURRENT_TIMESTAMP),
('7', '1', true, CURRENT_TIMESTAMP),
('7', '2', true, CURRENT_TIMESTAMP),
('7', '3', true, CURRENT_TIMESTAMP),
('7', '29', true, CURRENT_TIMESTAMP),
('7', '30', true, CURRENT_TIMESTAMP),
('8', '1', true, CURRENT_TIMESTAMP),
('8', '2', true, CURRENT_TIMESTAMP),
('8', '3', true, CURRENT_TIMESTAMP),
('10', '1', true, CURRENT_TIMESTAMP),
('10', '2', true, CURRENT_TIMESTAMP),
('10', '3', true, CURRENT_TIMESTAMP),
('10', '4', true, CURRENT_TIMESTAMP),
('10', '5', true, CURRENT_TIMESTAMP),
('10', '17', true, CURRENT_TIMESTAMP),
('10', '20', true, CURRENT_TIMESTAMP),
('10', '29', true, CURRENT_TIMESTAMP),
('10', '30', true, CURRENT_TIMESTAMP),
('11', '6', true, CURRENT_TIMESTAMP),
('11', '7', true, CURRENT_TIMESTAMP),
('11', '8', true, CURRENT_TIMESTAMP),
('11', '9', true, CURRENT_TIMESTAMP),
('11', '10', true, CURRENT_TIMESTAMP),
('11', '11', true, CURRENT_TIMESTAMP),
('11', '28', true, CURRENT_TIMESTAMP),
('11', '29', true, CURRENT_TIMESTAMP),
('11', '30', true, CURRENT_TIMESTAMP),
('13', '12', true, CURRENT_TIMESTAMP),
('13', '13', true, CURRENT_TIMESTAMP),
('13', '29', true, CURRENT_TIMESTAMP),
('14', '3', true, CURRENT_TIMESTAMP),
('14', '11', true, CURRENT_TIMESTAMP),
('14', '14', true, CURRENT_TIMESTAMP),
('14', '15', true, CURRENT_TIMESTAMP),
('14', '16', true, CURRENT_TIMESTAMP),
('14', '17', true, CURRENT_TIMESTAMP),
('14', '18', true, CURRENT_TIMESTAMP),
('14', '19', true, CURRENT_TIMESTAMP),
('14', '20', true, CURRENT_TIMESTAMP),
('14', '21', true, CURRENT_TIMESTAMP),
('14', '22', true, CURRENT_TIMESTAMP),
('14', '23', true, CURRENT_TIMESTAMP),
('14', '25', true, CURRENT_TIMESTAMP),
('14', '26', true, CURRENT_TIMESTAMP),
('14', '27', true, CURRENT_TIMESTAMP),
('14', '29', true, CURRENT_TIMESTAMP),
('14', '30', true, CURRENT_TIMESTAMP);

-- =====================================================
-- SC_T_BUDOV - Building Type (Typ budovy)
-- =====================================================
DELETE FROM cuzk_building_types;

INSERT INTO cuzk_building_types (code, name_cs, name_en, abbreviation, entry_code, valid_from, valid_to, active, sort_order, created_at)
VALUES
('1', 'budova s číslem popisným', 'building with descriptive number', 'č.p.', true, '1993-01-01', NULL, true, 1, CURRENT_TIMESTAMP),
('2', 'budova s číslem evidenčním', 'building with registration number', 'č.e.', true, '1993-01-01', NULL, true, 2, CURRENT_TIMESTAMP),
('3', 'budova bez čísla popisného nebo evidenčního', 'building without number', 'bez čp/če', false, '1993-01-01', NULL, true, 3, CURRENT_TIMESTAMP),
('4', 'rozestavěná budova', 'building under construction', 'rozestav.', false, '1993-01-01', NULL, true, 4, CURRENT_TIMESTAMP),
('5', 'poschoďová garáž', 'multi-storey garage', 'posch.gar', false, '1993-01-01', '1999-12-31', false, 5, CURRENT_TIMESTAMP),
('6', 'vodní dílo', 'water structure', 'vod.dílo', false, '2007-01-01', NULL, true, 6, CURRENT_TIMESTAMP),
('7', 'budova s rozestavěnými jednotkami', 'building with units under construction', 's roz.jed', false, '2015-08-01', NULL, true, 7, CURRENT_TIMESTAMP);

-- =====================================================
-- SC_ZP_VYUZITI_BUD - Building Use (Způsob využití budovy)
-- =====================================================
DELETE FROM cuzk_building_uses;

INSERT INTO cuzk_building_uses (code, name_cs, name_en, abbreviation, valid_from, valid_to, active, sort_order, created_at)
VALUES
('1', 'průmyslový objekt', 'industrial building', 'prům.obj', '1993-01-01', NULL, true, 1, CURRENT_TIMESTAMP),
('2', 'zemědělská usedlost', 'agricultural homestead', 'zem.used', '1993-01-01', NULL, true, 2, CURRENT_TIMESTAMP),
('3', 'objekt k bydlení', 'residential building', 'bydlení', '1993-01-01', NULL, true, 3, CURRENT_TIMESTAMP),
('4', 'objekt lesního hospodářství', 'forestry building', 'les.hosp', '1993-01-01', NULL, true, 4, CURRENT_TIMESTAMP),
('5', 'objekt občanské vybavenosti', 'civic amenities building', 'obč.vyb', '1993-01-01', NULL, true, 5, CURRENT_TIMESTAMP),
('6', 'bytový dům', 'apartment building', 'byt.dům', '1993-01-01', NULL, true, 6, CURRENT_TIMESTAMP),
('7', 'rodinný dům', 'family house', 'rod.dům', '1993-01-01', NULL, true, 7, CURRENT_TIMESTAMP),
('8', 'stavba pro rodinnou rekreaci', 'recreational building', 'rod.rekr', '1993-01-01', NULL, true, 8, CURRENT_TIMESTAMP),
('9', 'stavba pro shromažďování většího počtu osob', 'assembly building', 'shromaž.', '1993-01-01', NULL, true, 9, CURRENT_TIMESTAMP),
('10', 'stavba pro obchod', 'commercial building', 'obchod', '1993-01-01', NULL, true, 10, CURRENT_TIMESTAMP),
('11', 'stavba ubytovacího zařízení', 'accommodation building', 'ubyt.zař', '1993-01-01', NULL, true, 11, CURRENT_TIMESTAMP),
('12', 'stavba pro výrobu a skladování', 'production and storage building', 'výroba', '1993-01-01', NULL, true, 12, CURRENT_TIMESTAMP),
('13', 'zemědělská stavba', 'agricultural building', 'zem.stav', '1993-01-01', NULL, true, 13, CURRENT_TIMESTAMP),
('14', 'stavba pro administrativu', 'administrative building', 'adminis.', '1993-01-01', NULL, true, 14, CURRENT_TIMESTAMP),
('15', 'stavba občanského vybavení', 'civic equipment building', 'obč.vyb.', '1993-01-01', NULL, true, 15, CURRENT_TIMESTAMP),
('16', 'stavba technického vybavení', 'technical equipment building', 'tech.vyb', '1993-01-01', NULL, true, 16, CURRENT_TIMESTAMP),
('17', 'stavba pro dopravu', 'transport building', 'doprava', '1993-01-01', NULL, true, 17, CURRENT_TIMESTAMP),
('18', 'garáž', 'garage', 'garáž', '1993-01-01', NULL, true, 18, CURRENT_TIMESTAMP),
('19', 'jiná stavba', 'other building', 'jiná st.', '1993-01-01', NULL, true, 19, CURRENT_TIMESTAMP),
('20', 'víceúčelová stavba', 'multi-purpose building', 'víceúčel', '2007-03-01', NULL, true, 20, CURRENT_TIMESTAMP),
('21', 'skleník', 'greenhouse', 'skleník', '2007-03-01', NULL, true, 21, CURRENT_TIMESTAMP),
('22', 'přehrada', 'dam', 'přehrada', '2007-01-01', NULL, true, 22, CURRENT_TIMESTAMP),
('23', 'hráz přehrazující vodní tok nebo údolí', 'dam blocking watercourse or valley', 'hráz př.', '2007-01-01', NULL, true, 23, CURRENT_TIMESTAMP),
('24', 'hráz k ochraně nemovitostí před zaplavením při povodni', 'flood protection dam', 'hráz pod', '2007-01-01', NULL, true, 24, CURRENT_TIMESTAMP),
('25', 'hráz ohrazující umělou vodní nádrž', 'dam surrounding artificial reservoir', 'hráz ohr', '2007-01-01', NULL, true, 25, CURRENT_TIMESTAMP),
('26', 'jez', 'weir', 'jez', '2007-01-01', NULL, true, 26, CURRENT_TIMESTAMP),
('27', 'stavba k plaveb.účelům v korytech nebo na březích vod.toků', 'navigation structure', 'plav.úč.', '2007-01-01', NULL, true, 27, CURRENT_TIMESTAMP),
('28', 'stavba k využití vodní energie (vodní elektrárna)', 'hydroelectric power plant', 'vodní el', '2007-01-01', NULL, true, 28, CURRENT_TIMESTAMP),
('29', 'stavba odkaliště', 'tailings pond structure', 'odkališ.', '2007-01-01', NULL, true, 29, CURRENT_TIMESTAMP),
('30', 'rozestavěné jednotky', 'units under construction', 'rozest.j', '2015-02-10', NULL, true, 30, CURRENT_TIMESTAMP);

-- =====================================================
-- SC_TYPB_ZPVYB - Building Type Use mapping
-- =====================================================
DELETE FROM cuzk_building_type_uses;

INSERT INTO cuzk_building_type_uses (building_use_code, building_type_code, active, created_at)
VALUES
('1', '1', true, CURRENT_TIMESTAMP),
('1', '2', true, CURRENT_TIMESTAMP),
('1', '3', true, CURRENT_TIMESTAMP),
('2', '1', true, CURRENT_TIMESTAMP),
('2', '2', true, CURRENT_TIMESTAMP),
('2', '3', true, CURRENT_TIMESTAMP),
('3', '1', true, CURRENT_TIMESTAMP),
('3', '2', true, CURRENT_TIMESTAMP),
('3', '3', true, CURRENT_TIMESTAMP),
('4', '1', true, CURRENT_TIMESTAMP),
('4', '2', true, CURRENT_TIMESTAMP),
('4', '3', true, CURRENT_TIMESTAMP),
('5', '1', true, CURRENT_TIMESTAMP),
('5', '2', true, CURRENT_TIMESTAMP),
('5', '3', true, CURRENT_TIMESTAMP),
('6', '1', true, CURRENT_TIMESTAMP),
('6', '2', true, CURRENT_TIMESTAMP),
('6', '3', true, CURRENT_TIMESTAMP),
('7', '1', true, CURRENT_TIMESTAMP),
('7', '2', true, CURRENT_TIMESTAMP),
('7', '3', true, CURRENT_TIMESTAMP),
('8', '1', true, CURRENT_TIMESTAMP),
('8', '2', true, CURRENT_TIMESTAMP),
('8', '3', true, CURRENT_TIMESTAMP),
('9', '1', true, CURRENT_TIMESTAMP),
('9', '2', true, CURRENT_TIMESTAMP),
('9', '3', true, CURRENT_TIMESTAMP),
('10', '1', true, CURRENT_TIMESTAMP),
('10', '2', true, CURRENT_TIMESTAMP),
('10', '3', true, CURRENT_TIMESTAMP),
('11', '1', true, CURRENT_TIMESTAMP),
('11', '2', true, CURRENT_TIMESTAMP),
('11', '3', true, CURRENT_TIMESTAMP),
('12', '1', true, CURRENT_TIMESTAMP),
('12', '2', true, CURRENT_TIMESTAMP),
('12', '3', true, CURRENT_TIMESTAMP),
('13', '1', true, CURRENT_TIMESTAMP),
('13', '2', true, CURRENT_TIMESTAMP),
('13', '3', true, CURRENT_TIMESTAMP),
('14', '1', true, CURRENT_TIMESTAMP),
('14', '2', true, CURRENT_TIMESTAMP),
('14', '3', true, CURRENT_TIMESTAMP),
('15', '1', true, CURRENT_TIMESTAMP),
('15', '2', true, CURRENT_TIMESTAMP),
('15', '3', true, CURRENT_TIMESTAMP),
('16', '1', true, CURRENT_TIMESTAMP),
('16', '2', true, CURRENT_TIMESTAMP),
('16', '3', true, CURRENT_TIMESTAMP),
('17', '1', true, CURRENT_TIMESTAMP),
('17', '2', true, CURRENT_TIMESTAMP),
('17', '3', true, CURRENT_TIMESTAMP),
('18', '1', true, CURRENT_TIMESTAMP),
('18', '2', true, CURRENT_TIMESTAMP),
('18', '3', true, CURRENT_TIMESTAMP),
('19', '1', true, CURRENT_TIMESTAMP),
('19', '2', true, CURRENT_TIMESTAMP),
('19', '3', true, CURRENT_TIMESTAMP),
('20', '1', true, CURRENT_TIMESTAMP),
('20', '2', true, CURRENT_TIMESTAMP),
('20', '3', true, CURRENT_TIMESTAMP),
('21', '1', true, CURRENT_TIMESTAMP),
('21', '2', true, CURRENT_TIMESTAMP),
('21', '3', true, CURRENT_TIMESTAMP),
('22', '6', true, CURRENT_TIMESTAMP),
('23', '6', true, CURRENT_TIMESTAMP),
('24', '6', true, CURRENT_TIMESTAMP),
('25', '6', true, CURRENT_TIMESTAMP),
('26', '6', true, CURRENT_TIMESTAMP),
('27', '6', true, CURRENT_TIMESTAMP),
('28', '6', true, CURRENT_TIMESTAMP),
('29', '6', true, CURRENT_TIMESTAMP);

-- =====================================================
-- SC_T_JEDNOTEK - Unit Type (Typ jednotky)
-- =====================================================
DELETE FROM cuzk_unit_types;

INSERT INTO cuzk_unit_types (code, name_cs, name_en, abbreviation, civil_code, valid_from, valid_to, active, sort_order, created_at)
VALUES
('1', 'byt nebo nebytový prostor', 'flat or non-residential space', NULL, false, '1993-01-01', '2013-12-31', false, 1, CURRENT_TIMESTAMP),
('2', 'rozestavěný byt nebo nebytový prostor', 'flat or space under construction', 'rozest.', false, '1993-01-01', '2013-12-31', false, 2, CURRENT_TIMESTAMP),
('3', 'jednotka vymezená podle zákona o vlastnictví bytů', 'unit defined by Ownership of Flats Act', 'byt.z.', false, '2014-01-01', NULL, true, 3, CURRENT_TIMESTAMP),
('4', 'jednotka vymezená podle občanského zákoníku', 'unit defined by Civil Code', 'obč.z.', true, '2014-01-01', NULL, true, 4, CURRENT_TIMESTAMP);

-- =====================================================
-- SC_ZP_VYUZITI_JED - Unit Use (Způsob využití jednotky)
-- =====================================================
DELETE FROM cuzk_unit_uses;

INSERT INTO cuzk_unit_uses (code, name_cs, name_en, abbreviation, valid_from, valid_to, active, sort_order, created_at)
VALUES
('1', 'byt', 'flat', 'byt', '1993-01-01', NULL, true, 1, CURRENT_TIMESTAMP),
('2', 'ateliér', 'studio', 'ateliér', '1993-01-01', NULL, true, 2, CURRENT_TIMESTAMP),
('3', 'garáž', 'garage', 'garáž', '1993-01-01', NULL, true, 3, CURRENT_TIMESTAMP),
('4', 'dílna nebo provozovna', 'workshop or business premises', 'dílna', '1993-01-01', NULL, true, 4, CURRENT_TIMESTAMP),
('5', 'jiný nebytový prostor', 'other non-residential space', 'j.nebyt', '1993-01-01', NULL, true, 5, CURRENT_TIMESTAMP),
('6', 'rozestavěná jednotka', 'unit under construction', 'rozest.', '2013-12-18', NULL, true, 6, CURRENT_TIMESTAMP),
('7', 'skupina bytů', 'group of flats', 'sk.byt', '2014-01-01', NULL, true, 7, CURRENT_TIMESTAMP),
('8', 'skupina nebytových prostorů', 'group of non-residential spaces', 'sk.neb', '2014-01-01', NULL, true, 8, CURRENT_TIMESTAMP),
('9', 'skupina bytů a nebytových prostorů', 'group of flats and non-residential spaces', 'sk.bneb', '2014-01-01', NULL, true, 9, CURRENT_TIMESTAMP);

-- =====================================================
-- SC_T_OCHRANY_NEM - Property Protection Type
-- =====================================================
DELETE FROM cuzk_property_protection_types;

INSERT INTO cuzk_property_protection_types (code, name_cs, name_en, valid_from, valid_to, active, sort_order, created_at)
VALUES
('0', 'neurčen', 'unspecified', '1993-01-01', NULL, true, 1, CURRENT_TIMESTAMP),
('1', 'ochrana přírody a krajiny', 'nature and landscape protection', '1999-06-01', NULL, true, 2, CURRENT_TIMESTAMP),
('2', 'památková ochrana', 'monument protection', '1999-06-01', NULL, true, 3, CURRENT_TIMESTAMP),
('3', 'ochr.přír.léč.láz.,přír.léčiv.zdroje a zdroje přír.min.vody', 'spa, mineral water protection', '1999-06-01', NULL, true, 4, CURRENT_TIMESTAMP),
('4', 'ochrana nerostného bohatství', 'mineral wealth protection', '1999-06-01', NULL, true, 5, CURRENT_TIMESTAMP),
('5', 'ochrana značky geodetického bodu', 'geodetic point protection', '1999-06-01', NULL, true, 6, CURRENT_TIMESTAMP),
('6', 'jiná ochrana pozemku', 'other land protection', '1999-06-01', NULL, true, 7, CURRENT_TIMESTAMP),
('7', 'ochrana vodního díla', 'water structure protection', '2002-01-01', NULL, true, 8, CURRENT_TIMESTAMP),
('8', 'ochrana vodního zdroje', 'water source protection', '2002-01-01', NULL, true, 9, CURRENT_TIMESTAMP);

-- =====================================================
-- SC_ZP_OCHRANY_NEM - Property Protection Method
-- =====================================================
DELETE FROM cuzk_property_protections;

INSERT INTO cuzk_property_protections (code, name_cs, name_en, protection_type_code, applies_to_land, applies_to_building, applies_to_unit, applies_to_building_right, valid_from, valid_to, active, sort_order, created_at)
VALUES
('1', 'menší chráněné území', 'smaller protected area', '0', true, true, true, true, '1993-01-01', '2025-10-24', false, 1, CURRENT_TIMESTAMP),
('2', 'rozsáhlé chráněné území', 'large protected area', '0', true, true, true, true, '1993-01-01', '2025-10-24', false, 2, CURRENT_TIMESTAMP),
('3', 'vnitř.lázeň.území, ložis.slatin a rašeliny, ochr.pásmo 1.st.', 'inner spa area, peat deposits, 1st protection zone', '0', true, true, true, true, '1993-01-01', NULL, true, 3, CURRENT_TIMESTAMP),
('4', 'památkově chráněné území', 'monument protected area', '0', true, true, true, true, '1993-01-01', NULL, true, 4, CURRENT_TIMESTAMP),
('5', 'značka geodetického bodu a její chráněné území', 'geodetic point mark and its protected area', '0', true, false, false, true, '1993-01-01', '1999-12-31', false, 5, CURRENT_TIMESTAMP),
('6', 'národní park - I.zóna', 'national park - zone I', '1', true, true, true, true, '1993-01-01', '2025-10-24', false, 6, CURRENT_TIMESTAMP),
('7', 'národní park - II.zóna', 'national park - zone II', '1', true, true, true, true, '1993-01-01', '2025-10-24', false, 7, CURRENT_TIMESTAMP),
('8', 'národní park - III.zóna', 'national park - zone III', '1', true, true, true, true, '1993-01-01', '2025-10-24', false, 8, CURRENT_TIMESTAMP),
('9', 'ochranné pásmo národního parku', 'national park buffer zone', '1', true, false, true, true, '1993-01-01', NULL, true, 9, CURRENT_TIMESTAMP),
('10', 'chráněná krajinná oblast - I.zóna', 'protected landscape area - zone I', '1', true, false, true, true, '1993-01-01', NULL, true, 10, CURRENT_TIMESTAMP),
('11', 'chráněná krajinná oblast - II.-IV.zóna', 'protected landscape area - zones II-IV', '1', true, true, true, true, '1993-01-01', '2025-10-24', false, 11, CURRENT_TIMESTAMP),
('12', 'národní přírodní rezervace nebo národní přírodní památka', 'national nature reserve or monument', '1', true, true, true, true, '1998-10-15', '2025-10-24', false, 12, CURRENT_TIMESTAMP),
('13', 'přírodní rezervace nebo přírodní památka', 'nature reserve or monument', '1', true, true, true, true, '1998-10-15', '2025-10-24', false, 13, CURRENT_TIMESTAMP),
('14', 'ochr. pásmo jiného zvlášť chrán. území nebo pam.stromu', 'buffer zone of special protected area or memorial tree', '1', true, true, true, true, '1998-10-15', '2025-10-24', false, 14, CURRENT_TIMESTAMP),
('15', 'nemovitá národní kulturní památka', 'immovable national cultural monument', '2', true, true, true, true, '1998-10-15', NULL, true, 15, CURRENT_TIMESTAMP),
('16', 'pam. rezervace - budova, pozemek v památkové rezervaci', 'monument reserve - building, land', '2', true, true, true, true, '1998-10-15', NULL, true, 16, CURRENT_TIMESTAMP),
('17', 'pam. zóna - budova, pozemek v památkové zóně', 'monument zone - building, land', '2', true, true, true, true, '1998-10-15', NULL, true, 17, CURRENT_TIMESTAMP),
('18', 'nemovitá kulturní památka', 'immovable cultural monument', '2', true, true, true, true, '1998-10-15', NULL, true, 18, CURRENT_TIMESTAMP),
('19', 'ochr.pásmo nem.kult.pam.,pam.zóny,rezervace,nem.nár.kult.pam', 'buffer zone of cultural monument', '2', true, true, true, true, '1998-10-15', NULL, true, 19, CURRENT_TIMESTAMP),
('20', 'vnitřní území lázeňského místa', 'inner spa town area', '3', true, false, true, true, '1998-10-15', NULL, true, 20, CURRENT_TIMESTAMP),
('21', 'přír. léč. zdroj nebo zdroj přír. miner. vody', 'natural healing source or mineral water source', '3', true, false, true, true, '1998-10-15', NULL, true, 21, CURRENT_TIMESTAMP),
('22', 'ochr. pásmo přír. léčiv. zdroje nebo zdroje přír. miner.vody', 'buffer zone of healing or mineral water source', '3', true, false, true, true, '1998-10-15', NULL, true, 22, CURRENT_TIMESTAMP),
('23', 'chr.lož.území, chr.území pro zvl.zásahy do z.kůry', 'protected deposit area, area for special interventions', '4', true, true, true, true, '1998-10-15', '2024-03-18', false, 23, CURRENT_TIMESTAMP),
('24', 'chráněná značka geodetického bodu', 'protected geodetic point mark', '5', true, true, true, true, '1998-10-15', NULL, true, 24, CURRENT_TIMESTAMP),
('25', 'chráněné území značky geodetického bodu', 'protected area of geodetic point mark', '5', true, false, false, true, '1998-10-15', NULL, true, 25, CURRENT_TIMESTAMP),
('26', 'pozemek určený k plnění funkcí lesa', 'land designated for forest functions', '6', true, false, false, true, '1998-10-15', NULL, true, 26, CURRENT_TIMESTAMP),
('27', 'zemědělský půdní fond', 'agricultural land fund', '6', true, false, false, true, '1998-10-15', NULL, true, 27, CURRENT_TIMESTAMP),
('28', 'ochranné pásmo vodního díla', 'water structure buffer zone', '7', true, true, true, true, '2002-01-01', NULL, true, 28, CURRENT_TIMESTAMP),
('29', 'ochranné pásmo vodního zdroje', 'water source buffer zone', '8', true, true, true, true, '2002-01-01', '2002-04-21', false, 29, CURRENT_TIMESTAMP),
('30', 'ochranné pásmo vodního díla 1.stupně', 'water structure buffer zone 1st degree', '7', true, true, true, true, '2002-04-22', '2006-02-13', false, 30, CURRENT_TIMESTAMP),
('31', 'ochranné pásmo vodního díla 2.stupně', 'water structure buffer zone 2nd degree', '7', true, true, true, true, '2002-04-22', '2006-02-13', false, 31, CURRENT_TIMESTAMP),
('32', 'ochranné pásmo vodního zdroje 1.stupně', 'water source buffer zone 1st degree', '8', true, true, true, true, '2002-04-22', NULL, true, 32, CURRENT_TIMESTAMP),
('33', 'ochranné pásmo vodního zdroje 2.stupně', 'water source buffer zone 2nd degree', '8', true, true, true, true, '2002-04-22', NULL, true, 33, CURRENT_TIMESTAMP),
('34', 'evropsky významná lokalita', 'European significant site', '1', true, false, true, true, '2007-03-01', NULL, true, 34, CURRENT_TIMESTAMP),
('35', 'ptačí oblast', 'bird area', '1', true, false, true, true, '2007-03-01', NULL, true, 35, CURRENT_TIMESTAMP),
('36', 'pozemek určený k plnění funkcí lesa - dočasně odňato', 'forest land - temporarily withdrawn', '6', true, false, false, true, '2017-04-01', NULL, true, 36, CURRENT_TIMESTAMP),
('37', 'zemědělský půdní fond - dočasně odňato', 'agricultural land fund - temporarily withdrawn', '6', true, false, false, true, '2017-04-01', NULL, true, 37, CURRENT_TIMESTAMP),
('38', 'národní park', 'national park', '1', true, false, true, true, '2019-01-03', NULL, true, 38, CURRENT_TIMESTAMP),
('39', 'chráněná krajinná oblast', 'protected landscape area', '1', true, false, true, true, '2020-02-14', NULL, true, 39, CURRENT_TIMESTAMP),
('40', 'národní park - zóna přírodní', 'national park - natural zone', '1', true, false, true, true, '2023-01-01', NULL, true, 40, CURRENT_TIMESTAMP),
('41', 'národní park - zóna přírodě blízká', 'national park - near-natural zone', '1', true, false, true, true, '2023-01-01', NULL, true, 41, CURRENT_TIMESTAMP),
('42', 'národní park - zóna soustředěné péče', 'national park - concentrated care zone', '1', true, true, true, true, '2023-01-01', NULL, true, 42, CURRENT_TIMESTAMP),
('43', 'národní park - zóna kulturní krajina', 'national park - cultural landscape zone', '1', true, true, true, true, '2023-01-01', NULL, true, 43, CURRENT_TIMESTAMP),
('44', 'chráněná krajinná oblast - II. zóna', 'protected landscape area - zone II', '1', true, true, true, true, '2023-01-01', NULL, true, 44, CURRENT_TIMESTAMP),
('45', 'chráněná krajinná oblast - III. zóna', 'protected landscape area - zone III', '1', true, true, true, true, '2023-01-01', NULL, true, 45, CURRENT_TIMESTAMP),
('46', 'chráněná krajinná oblast - IV. zóna', 'protected landscape area - zone IV', '1', true, true, true, true, '2023-01-01', NULL, true, 46, CURRENT_TIMESTAMP),
('47', 'národní přírodní rezervace', 'national nature reserve', '1', true, true, true, true, '2023-01-01', NULL, true, 47, CURRENT_TIMESTAMP),
('48', 'národní přírodní památka', 'national natural monument', '1', true, false, true, true, '2023-01-01', NULL, true, 48, CURRENT_TIMESTAMP),
('49', 'přírodní rezervace', 'nature reserve', '1', true, true, true, true, '2023-01-01', NULL, true, 49, CURRENT_TIMESTAMP),
('50', 'přírodní památka', 'natural monument', '1', true, false, true, true, '2023-01-01', NULL, true, 50, CURRENT_TIMESTAMP),
('51', 'ochranné pásmo národní přírodní rezervace', 'buffer zone of national nature reserve', '1', true, true, true, true, '2023-01-01', NULL, true, 51, CURRENT_TIMESTAMP),
('52', 'ochranné pásmo národní přírodní památky', 'buffer zone of national natural monument', '1', true, false, true, true, '2023-01-01', NULL, true, 52, CURRENT_TIMESTAMP),
('53', 'ochranné pásmo přírodní rezervace', 'buffer zone of nature reserve', '1', true, true, true, true, '2023-01-01', NULL, true, 53, CURRENT_TIMESTAMP),
('54', 'ochranné pásmo přírodní památky', 'buffer zone of natural monument', '1', true, false, true, true, '2023-01-01', NULL, true, 54, CURRENT_TIMESTAMP),
('55', 'ochranné pásmo památného stromu', 'buffer zone of memorial tree', '1', true, false, true, true, '2023-01-01', NULL, true, 55, CURRENT_TIMESTAMP),
('56', 'chráněná ložisková území', 'protected deposit areas', '4', true, true, true, true, '2023-01-01', NULL, true, 56, CURRENT_TIMESTAMP),
('57', 'chráněná území pro zvl. zásahy do zemské kůry', 'protected areas for special interventions', '4', true, true, true, true, '2023-01-01', NULL, true, 57, CURRENT_TIMESTAMP);

-- =====================================================
-- SC_ZDROJE_PARCEL_ZE - Simplified Parcel Source
-- =====================================================
DELETE FROM cuzk_simplified_parcel_sources;

INSERT INTO cuzk_simplified_parcel_sources (code, name_cs, name_en, abbreviation, valid_from, valid_to, active, sort_order, created_at)
VALUES
('3', 'Evidence nemovitostí', 'Real Estate Register', 'EN', '1993-01-01', NULL, true, 1, CURRENT_TIMESTAMP),
('4', 'Pozemkový katastr', 'Land Cadastre', 'PK', '1993-01-01', NULL, true, 2, CURRENT_TIMESTAMP),
('6', 'Přídělový plán nebo jiný podklad', 'Allotment plan or other document', 'GP', '1993-01-01', NULL, true, 3, CURRENT_TIMESTAMP);

-- =====================================================
-- SC_UCELY_PS - Building Right Purpose (Účel práva stavby)
-- =====================================================
DELETE FROM cuzk_building_right_purposes;

INSERT INTO cuzk_building_right_purposes (code, name_cs, name_en, valid_from, valid_to, active, sort_order, created_at)
VALUES
('1', 'bytový dům', 'apartment building', '2014-01-01', NULL, true, 1, CURRENT_TIMESTAMP),
('2', 'rodinný dům', 'family house', '2014-01-01', NULL, true, 2, CURRENT_TIMESTAMP),
('3', 'stavba pro sport a rekreaci', 'sports and recreation building', '2014-01-01', NULL, true, 3, CURRENT_TIMESTAMP),
('4', 'stavba pro služby', 'services building', '2014-01-01', '2018-09-06', false, 4, CURRENT_TIMESTAMP),
('5', 'stavba pro služby', 'services building', '2014-01-01', '2018-09-06', false, 5, CURRENT_TIMESTAMP),
('6', 'stavba pro administrativu', 'administrative building', '2014-01-01', NULL, true, 6, CURRENT_TIMESTAMP),
('7', 'stavba vodního díla', 'water structure', '2014-01-01', NULL, true, 7, CURRENT_TIMESTAMP),
('8', 'stavba vodního díla', 'water structure', '2014-01-01', '2018-09-06', false, 8, CURRENT_TIMESTAMP),
('9', 'stavba vodního díla', 'water structure', '2014-01-01', '2018-09-06', false, 9, CURRENT_TIMESTAMP),
('10', 'garáž', 'garage', '2014-01-01', NULL, true, 10, CURRENT_TIMESTAMP),
('11', 'stavba vodního díla', 'water structure', '2014-01-01', '2018-09-06', false, 11, CURRENT_TIMESTAMP),
('12', 'stavba pro zahradu', 'garden building', '2014-03-12', NULL, true, 12, CURRENT_TIMESTAMP),
('13', 'stavba pro průmyslové účely', 'industrial building', '2014-03-12', NULL, true, 13, CURRENT_TIMESTAMP),
('14', 'stavba skladu hmot, materiálu a výrobků', 'storage building', '2014-03-12', '2018-09-06', false, 14, CURRENT_TIMESTAMP),
('15', 'stavba skladu hmot, materiálu a výrobků', 'storage building', '2014-03-12', '2018-09-06', false, 15, CURRENT_TIMESTAMP),
('16', 'zemědělská stavba pro chov hospodářských zvířat, včel', 'agricultural building for livestock', '2014-03-12', NULL, true, 16, CURRENT_TIMESTAMP),
('17', 'zeměděl.stavba pro přípravu a sklad a stání zeměděl.techniky', 'agricultural machinery building', '2014-03-12', '2018-09-06', false, 17, CURRENT_TIMESTAMP),
('18', 'zeměděl.stavba pro přípravu a sklad a stání zeměděl.techniky', 'agricultural machinery building', '2014-03-12', NULL, true, 18, CURRENT_TIMESTAMP),
('25', 'víceúčelová stavba', 'multi-purpose building', '2014-04-11', NULL, true, 25, CURRENT_TIMESTAMP),
('27', 'stavba pro zdravotnictví a sociální služby', 'healthcare building', '2014-06-23', NULL, true, 27, CURRENT_TIMESTAMP),
('28', 'stavba plotu, brány, protihlukové stěny', 'fence, gate, noise barrier', '2014-07-02', NULL, true, 28, CURRENT_TIMESTAMP),
('39', 'stavba technického zázemí', 'technical facility building', '2015-01-12', NULL, true, 39, CURRENT_TIMESTAMP),
('53', 'stavba pro služby', 'services building', '2015-11-13', NULL, true, 53, CURRENT_TIMESTAMP),
('54', 'stavba ateliéru', 'studio building', '2015-11-18', NULL, true, 54, CURRENT_TIMESTAMP),
('56', 'stavba pro kulturu a školství', 'cultural and educational building', '2015-12-14', NULL, true, 56, CURRENT_TIMESTAMP),
('63', 'stavba pro chov jiných než hospodářských zvířat', 'building for non-farm animals', '2016-03-29', NULL, true, 63, CURRENT_TIMESTAMP),
('65', 'stavba skladu hmot, materiálu a výrobků', 'storage building', '2016-04-15', NULL, true, 65, CURRENT_TIMESTAMP),
('72', 'stavba pro dopravu', 'transport building', '2016-08-03', NULL, true, 72, CURRENT_TIMESTAMP),
('74', 'stavba čerpací stanice', 'filling station', '2016-09-07', NULL, true, 74, CURRENT_TIMESTAMP),
('96', 'sakrální stavba, stavba pomníku', 'sacral building, monument', '2017-11-30', NULL, true, 96, CURRENT_TIMESTAMP),
('99', 'neuveden', 'not specified', '2018-01-23', NULL, true, 99, CURRENT_TIMESTAMP);

-- =====================================================
-- SC_BPEJ - Soil Ecological Units (Bonitované půdně ekologické jednotky)
-- Source: Vyhláška č. 441/2013 Sb. (prices per m²)
-- Code structure: 1st digit=climate region, 2nd-3rd=main soil unit, 4th=slope/exposure, 5th=depth/stoniness
-- =====================================================
DELETE FROM cuzk_soil_ecological_units;

INSERT INTO cuzk_soil_ecological_units (code, name_cs, name_en, description_cs, price, valid_from, active, sort_order, created_at)
VALUES
-- Climate region 0 (warm, dry)
('00100', 'BPEJ 00100', 'BPEJ 00100', 'Klimatický region 0, HPJ 01, svažitost 0-3°, bez skeletu', 17.23, '2014-01-01', true, 1, CURRENT_TIMESTAMP),
('00110', 'BPEJ 00110', 'BPEJ 00110', 'Klimatický region 0, HPJ 01, svažitost 3-7° J-JZ-Z, bez skeletu', 15.36, '2014-01-01', true, 2, CURRENT_TIMESTAMP),
('00200', 'BPEJ 00200', 'BPEJ 00200', 'Klimatický region 0, HPJ 02, svažitost 0-3°, bez skeletu', 15.88, '2014-01-01', true, 3, CURRENT_TIMESTAMP),
('00300', 'BPEJ 00300', 'BPEJ 00300', 'Klimatický region 0, HPJ 03, svažitost 0-3°, bez skeletu', 14.55, '2014-01-01', true, 4, CURRENT_TIMESTAMP),
('00400', 'BPEJ 00400', 'BPEJ 00400', 'Klimatický region 0, HPJ 04, svažitost 0-3°, bez skeletu', 14.07, '2014-01-01', true, 5, CURRENT_TIMESTAMP),
('00500', 'BPEJ 00500', 'BPEJ 00500', 'Klimatický region 0, HPJ 05, svažitost 0-3°, bez skeletu', 12.11, '2014-01-01', true, 6, CURRENT_TIMESTAMP),
('00600', 'BPEJ 00600', 'BPEJ 00600', 'Klimatický region 0, HPJ 06, svažitost 0-3°, bez skeletu', 10.08, '2014-01-01', true, 7, CURRENT_TIMESTAMP),
('00700', 'BPEJ 00700', 'BPEJ 00700', 'Klimatický region 0, HPJ 07, svažitost 0-3°, bez skeletu', 9.64, '2014-01-01', true, 8, CURRENT_TIMESTAMP),

-- Climate region 1 (warm, moderately dry)
('10100', 'BPEJ 10100', 'BPEJ 10100', 'Klimatický region 1, HPJ 01, svažitost 0-3°, bez skeletu', 15.88, '2014-01-01', true, 9, CURRENT_TIMESTAMP),
('10200', 'BPEJ 10200', 'BPEJ 10200', 'Klimatický region 1, HPJ 02, svažitost 0-3°, bez skeletu', 14.55, '2014-01-01', true, 10, CURRENT_TIMESTAMP),
('10300', 'BPEJ 10300', 'BPEJ 10300', 'Klimatický region 1, HPJ 03, svažitost 0-3°, bez skeletu', 13.25, '2014-01-01', true, 11, CURRENT_TIMESTAMP),
('10400', 'BPEJ 10400', 'BPEJ 10400', 'Klimatický region 1, HPJ 04, svažitost 0-3°, bez skeletu', 12.77, '2014-01-01', true, 12, CURRENT_TIMESTAMP),
('10500', 'BPEJ 10500', 'BPEJ 10500', 'Klimatický region 1, HPJ 05, svažitost 0-3°, bez skeletu', 11.00, '2014-01-01', true, 13, CURRENT_TIMESTAMP),

-- Climate region 2 (moderately warm, moderately dry)
('20100', 'BPEJ 20100', 'BPEJ 20100', 'Klimatický region 2, HPJ 01, svažitost 0-3°, bez skeletu', 14.55, '2014-01-01', true, 14, CURRENT_TIMESTAMP),
('20200', 'BPEJ 20200', 'BPEJ 20200', 'Klimatický region 2, HPJ 02, svažitost 0-3°, bez skeletu', 13.25, '2014-01-01', true, 15, CURRENT_TIMESTAMP),
('20300', 'BPEJ 20300', 'BPEJ 20300', 'Klimatický region 2, HPJ 03, svažitost 0-3°, bez skeletu', 12.11, '2014-01-01', true, 16, CURRENT_TIMESTAMP),
('20400', 'BPEJ 20400', 'BPEJ 20400', 'Klimatický region 2, HPJ 04, svažitost 0-3°, bez skeletu', 11.46, '2014-01-01', true, 17, CURRENT_TIMESTAMP),
('20500', 'BPEJ 20500', 'BPEJ 20500', 'Klimatický region 2, HPJ 05, svažitost 0-3°, bez skeletu', 10.08, '2014-01-01', true, 18, CURRENT_TIMESTAMP),

-- Climate region 3 (moderately warm, moderately humid)
('30100', 'BPEJ 30100', 'BPEJ 30100', 'Klimatický region 3, HPJ 01, svažitost 0-3°, bez skeletu', 13.25, '2014-01-01', true, 19, CURRENT_TIMESTAMP),
('30200', 'BPEJ 30200', 'BPEJ 30200', 'Klimatický region 3, HPJ 02, svažitost 0-3°, bez skeletu', 12.11, '2014-01-01', true, 20, CURRENT_TIMESTAMP),
('30300', 'BPEJ 30300', 'BPEJ 30300', 'Klimatický region 3, HPJ 03, svažitost 0-3°, bez skeletu', 11.00, '2014-01-01', true, 21, CURRENT_TIMESTAMP),
('30400', 'BPEJ 30400', 'BPEJ 30400', 'Klimatický region 3, HPJ 04, svažitost 0-3°, bez skeletu', 10.39, '2014-01-01', true, 22, CURRENT_TIMESTAMP),
('30500', 'BPEJ 30500', 'BPEJ 30500', 'Klimatický region 3, HPJ 05, svažitost 0-3°, bez skeletu', 9.22, '2014-01-01', true, 23, CURRENT_TIMESTAMP),

-- Climate region 4 (moderately warm, humid)
('40100', 'BPEJ 40100', 'BPEJ 40100', 'Klimatický region 4, HPJ 01, svažitost 0-3°, bez skeletu', 12.11, '2014-01-01', true, 24, CURRENT_TIMESTAMP),
('40200', 'BPEJ 40200', 'BPEJ 40200', 'Klimatický region 4, HPJ 02, svažitost 0-3°, bez skeletu', 11.00, '2014-01-01', true, 25, CURRENT_TIMESTAMP),
('40300', 'BPEJ 40300', 'BPEJ 40300', 'Klimatický region 4, HPJ 03, svažitost 0-3°, bez skeletu', 10.08, '2014-01-01', true, 26, CURRENT_TIMESTAMP),
('40400', 'BPEJ 40400', 'BPEJ 40400', 'Klimatický region 4, HPJ 04, svažitost 0-3°, bez skeletu', 9.64, '2014-01-01', true, 27, CURRENT_TIMESTAMP),
('40500', 'BPEJ 40500', 'BPEJ 40500', 'Klimatický region 4, HPJ 05, svažitost 0-3°, bez skeletu', 8.52, '2014-01-01', true, 28, CURRENT_TIMESTAMP),

-- Climate region 5 (moderately cool, humid)
('50100', 'BPEJ 50100', 'BPEJ 50100', 'Klimatický region 5, HPJ 01, svažitost 0-3°, bez skeletu', 11.00, '2014-01-01', true, 29, CURRENT_TIMESTAMP),
('50200', 'BPEJ 50200', 'BPEJ 50200', 'Klimatický region 5, HPJ 02, svažitost 0-3°, bez skeletu', 10.08, '2014-01-01', true, 30, CURRENT_TIMESTAMP),
('50300', 'BPEJ 50300', 'BPEJ 50300', 'Klimatický region 5, HPJ 03, svažitost 0-3°, bez skeletu', 9.22, '2014-01-01', true, 31, CURRENT_TIMESTAMP),
('50400', 'BPEJ 50400', 'BPEJ 50400', 'Klimatický region 5, HPJ 04, svažitost 0-3°, bez skeletu', 8.52, '2014-01-01', true, 32, CURRENT_TIMESTAMP),
('50500', 'BPEJ 50500', 'BPEJ 50500', 'Klimatický region 5, HPJ 05, svažitost 0-3°, bez skeletu', 7.60, '2014-01-01', true, 33, CURRENT_TIMESTAMP),

-- Climate region 6 (cool, humid)
('60100', 'BPEJ 60100', 'BPEJ 60100', 'Klimatický region 6, HPJ 01, svažitost 0-3°, bez skeletu', 9.22, '2014-01-01', true, 34, CURRENT_TIMESTAMP),
('60200', 'BPEJ 60200', 'BPEJ 60200', 'Klimatický region 6, HPJ 02, svažitost 0-3°, bez skeletu', 8.52, '2014-01-01', true, 35, CURRENT_TIMESTAMP),
('60300', 'BPEJ 60300', 'BPEJ 60300', 'Klimatický region 6, HPJ 03, svažitost 0-3°, bez skeletu', 7.60, '2014-01-01', true, 36, CURRENT_TIMESTAMP),
('60400', 'BPEJ 60400', 'BPEJ 60400', 'Klimatický region 6, HPJ 04, svažitost 0-3°, bez skeletu', 7.00, '2014-01-01', true, 37, CURRENT_TIMESTAMP),
('60500', 'BPEJ 60500', 'BPEJ 60500', 'Klimatický region 6, HPJ 05, svažitost 0-3°, bez skeletu', 6.07, '2014-01-01', true, 38, CURRENT_TIMESTAMP),

-- Climate region 7 (cold, humid)
('70100', 'BPEJ 70100', 'BPEJ 70100', 'Klimatický region 7, HPJ 01, svažitost 0-3°, bez skeletu', 7.60, '2014-01-01', true, 39, CURRENT_TIMESTAMP),
('70200', 'BPEJ 70200', 'BPEJ 70200', 'Klimatický region 7, HPJ 02, svažitost 0-3°, bez skeletu', 7.00, '2014-01-01', true, 40, CURRENT_TIMESTAMP),
('70300', 'BPEJ 70300', 'BPEJ 70300', 'Klimatický region 7, HPJ 03, svažitost 0-3°, bez skeletu', 6.07, '2014-01-01', true, 41, CURRENT_TIMESTAMP),
('70400', 'BPEJ 70400', 'BPEJ 70400', 'Klimatický region 7, HPJ 04, svažitost 0-3°, bez skeletu', 5.48, '2014-01-01', true, 42, CURRENT_TIMESTAMP),
('70500', 'BPEJ 70500', 'BPEJ 70500', 'Klimatický region 7, HPJ 05, svažitost 0-3°, bez skeletu', 4.65, '2014-01-01', true, 43, CURRENT_TIMESTAMP),

-- Climate region 8 (very cold)
('80100', 'BPEJ 80100', 'BPEJ 80100', 'Klimatický region 8, HPJ 01, svažitost 0-3°, bez skeletu', 6.07, '2014-01-01', true, 44, CURRENT_TIMESTAMP),
('80200', 'BPEJ 80200', 'BPEJ 80200', 'Klimatický region 8, HPJ 02, svažitost 0-3°, bez skeletu', 5.48, '2014-01-01', true, 45, CURRENT_TIMESTAMP),
('80300', 'BPEJ 80300', 'BPEJ 80300', 'Klimatický region 8, HPJ 03, svažitost 0-3°, bez skeletu', 4.65, '2014-01-01', true, 46, CURRENT_TIMESTAMP),
('80400', 'BPEJ 80400', 'BPEJ 80400', 'Klimatický region 8, HPJ 04, svažitost 0-3°, bez skeletu', 4.09, '2014-01-01', true, 47, CURRENT_TIMESTAMP),
('80500', 'BPEJ 80500', 'BPEJ 80500', 'Klimatický region 8, HPJ 05, svažitost 0-3°, bez skeletu', 3.24, '2014-01-01', true, 48, CURRENT_TIMESTAMP),

-- Climate region 9 (mountain)
('90100', 'BPEJ 90100', 'BPEJ 90100', 'Klimatický region 9, HPJ 01, svažitost 0-3°, bez skeletu', 4.65, '2014-01-01', true, 49, CURRENT_TIMESTAMP),
('90200', 'BPEJ 90200', 'BPEJ 90200', 'Klimatický region 9, HPJ 02, svažitost 0-3°, bez skeletu', 4.09, '2014-01-01', true, 50, CURRENT_TIMESTAMP),
('90300', 'BPEJ 90300', 'BPEJ 90300', 'Klimatický region 9, HPJ 03, svažitost 0-3°, bez skeletu', 3.24, '2014-01-01', true, 51, CURRENT_TIMESTAMP),
('90400', 'BPEJ 90400', 'BPEJ 90400', 'Klimatický region 9, HPJ 04, svažitost 0-3°, bez skeletu', 2.62, '2014-01-01', true, 52, CURRENT_TIMESTAMP),
('90500', 'BPEJ 90500', 'BPEJ 90500', 'Klimatický region 9, HPJ 05, svažitost 0-3°, bez skeletu', 1.81, '2014-01-01', true, 53, CURRENT_TIMESTAMP);
