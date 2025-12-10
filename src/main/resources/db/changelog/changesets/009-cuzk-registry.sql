-- Register all ČÚZK codelists in codelist_registry

INSERT INTO codelist_registry (code, name_cs, name_en, description_cs, description_en, web_url, api_url, icon_class, active, sort_order, created_at)
VALUES
('CUZK_LAND_TYPES', 'Druh pozemku (ČÚZK)', 'Land Types (ČÚZK)',
 'Číselník druhů pozemků dle ČÚZK (SC_D_POZEMKU)',
 'Land type classification according to ČÚZK (SC_D_POZEMKU)',
 '/web/cuzk/land-types', '/api/v1/cuzk/land-types', 'bi bi-geo-alt', true, 10, CURRENT_TIMESTAMP),

('CUZK_LAND_USES', 'Způsob využití pozemku (ČÚZK)', 'Land Uses (ČÚZK)',
 'Číselník způsobů využití pozemků dle ČÚZK (SC_ZP_VYUZITI_POZ)',
 'Land use classification according to ČÚZK (SC_ZP_VYUZITI_POZ)',
 '/web/cuzk/land-uses', '/api/v1/cuzk/land-uses', 'bi bi-tree', true, 11, CURRENT_TIMESTAMP),

('CUZK_LAND_TYPE_USES', 'Vazba druh pozemku - využití (ČÚZK)', 'Land Type Uses (ČÚZK)',
 'Vazba mezi druhem pozemku a způsobem využití dle ČÚZK (SC_POZEMEK_VYUZITI)',
 'Mapping between land type and land use according to ČÚZK (SC_POZEMEK_VYUZITI)',
 '/web/cuzk/land-type-uses', '/api/v1/cuzk/land-type-uses', 'bi bi-link', true, 12, CURRENT_TIMESTAMP),

('CUZK_BUILDING_TYPES', 'Typ stavby (ČÚZK)', 'Building Types (ČÚZK)',
 'Číselník typů staveb dle ČÚZK (SC_T_BUDOV)',
 'Building type classification according to ČÚZK (SC_T_BUDOV)',
 '/web/cuzk/building-types', '/api/v1/cuzk/building-types', 'bi bi-building', true, 13, CURRENT_TIMESTAMP),

('CUZK_BUILDING_USES', 'Způsob využití stavby (ČÚZK)', 'Building Uses (ČÚZK)',
 'Číselník způsobů využití staveb dle ČÚZK (SC_ZP_VYUZITI_BUD)',
 'Building use classification according to ČÚZK (SC_ZP_VYUZITI_BUD)',
 '/web/cuzk/building-uses', '/api/v1/cuzk/building-uses', 'bi bi-house-gear', true, 14, CURRENT_TIMESTAMP),

('CUZK_BUILDING_TYPE_USES', 'Vazba typ stavby - využití (ČÚZK)', 'Building Type Uses (ČÚZK)',
 'Vazba mezi typem stavby a způsobem využití dle ČÚZK (SC_TYPB_ZPVYB)',
 'Mapping between building type and building use according to ČÚZK (SC_TYPB_ZPVYB)',
 '/web/cuzk/building-type-uses', '/api/v1/cuzk/building-type-uses', 'bi bi-link-45deg', true, 15, CURRENT_TIMESTAMP),

('CUZK_AREA_DETERMINATIONS', 'Způsob určení výměry (ČÚZK)', 'Area Determinations (ČÚZK)',
 'Číselník způsobů určení výměry dle ČÚZK (SC_ZP_URCENI_VYMERY)',
 'Area determination method classification according to ČÚZK (SC_ZP_URCENI_VYMERY)',
 '/web/cuzk/area-determinations', '/api/v1/cuzk/area-determinations', 'bi bi-rulers', true, 16, CURRENT_TIMESTAMP),

('CUZK_BUILDING_RIGHT_PURPOSES', 'Účel práva stavby (ČÚZK)', 'Building Right Purposes (ČÚZK)',
 'Číselník účelů práva stavby dle ČÚZK (SC_UCELY_PS)',
 'Building right purpose classification according to ČÚZK (SC_UCELY_PS)',
 '/web/cuzk/building-right-purposes', '/api/v1/cuzk/building-right-purposes', 'bi bi-file-earmark-text', true, 17, CURRENT_TIMESTAMP),

('CUZK_UNIT_TYPES', 'Typ jednotky (ČÚZK)', 'Unit Types (ČÚZK)',
 'Číselník typů jednotek dle ČÚZK (SC_T_JEDNOTEK)',
 'Unit type classification according to ČÚZK (SC_T_JEDNOTEK)',
 '/web/cuzk/unit-types', '/api/v1/cuzk/unit-types', 'bi bi-door-open', true, 18, CURRENT_TIMESTAMP),

('CUZK_UNIT_USES', 'Způsob využití jednotky (ČÚZK)', 'Unit Uses (ČÚZK)',
 'Číselník způsobů využití jednotek dle ČÚZK (SC_ZP_VYUZITI_JED)',
 'Unit use classification according to ČÚZK (SC_ZP_VYUZITI_JED)',
 '/web/cuzk/unit-uses', '/api/v1/cuzk/unit-uses', 'bi bi-house-door', true, 19, CURRENT_TIMESTAMP),

('CUZK_PROPERTY_PROTECTION_TYPES', 'Typ ochrany nemovitosti (ČÚZK)', 'Property Protection Types (ČÚZK)',
 'Číselník typů ochrany nemovitostí dle ČÚZK (SC_T_OCHRANY_NEM)',
 'Property protection type classification according to ČÚZK (SC_T_OCHRANY_NEM)',
 '/web/cuzk/property-protection-types', '/api/v1/cuzk/property-protection-types', 'bi bi-shield', true, 20, CURRENT_TIMESTAMP),

('CUZK_PROPERTY_PROTECTIONS', 'Způsob ochrany nemovitosti (ČÚZK)', 'Property Protections (ČÚZK)',
 'Číselník způsobů ochrany nemovitostí dle ČÚZK (SC_ZP_OCHRANY_NEM)',
 'Property protection method classification according to ČÚZK (SC_ZP_OCHRANY_NEM)',
 '/web/cuzk/property-protections', '/api/v1/cuzk/property-protections', 'bi bi-shield-check', true, 21, CURRENT_TIMESTAMP),

('CUZK_SIMPLIFIED_PARCEL_SOURCES', 'Zdroje parcel ZE (ČÚZK)', 'Simplified Parcel Sources (ČÚZK)',
 'Číselník zdrojů parcel zjednodušené evidence dle ČÚZK (SC_ZDROJE_PARCEL_ZE)',
 'Simplified parcel source classification according to ČÚZK (SC_ZDROJE_PARCEL_ZE)',
 '/web/cuzk/simplified-parcel-sources', '/api/v1/cuzk/simplified-parcel-sources', 'bi bi-file-earmark', true, 22, CURRENT_TIMESTAMP),

('CUZK_SOIL_ECOLOGICAL_UNITS', 'Bonitované půdně ekologické jednotky (ČÚZK)', 'Soil Ecological Units (ČÚZK)',
 'Číselník bonitovaných půdně ekologických jednotek dle ČÚZK (SC_BPEJ)',
 'Soil ecological unit classification according to ČÚZK (SC_BPEJ)',
 '/web/cuzk/soil-ecological-units', '/api/v1/cuzk/soil-ecological-units', 'bi bi-flower1', true, 23, CURRENT_TIMESTAMP)

ON CONFLICT (code) DO NOTHING;
