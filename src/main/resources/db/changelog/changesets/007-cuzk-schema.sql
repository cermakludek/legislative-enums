-- =====================================================
-- ČÚZK Codelists Schema
-- Source: https://cuzk.gov.cz/Katastr-nemovitosti/Poskytovani-udaju-z-KN/Ciselniky-ISKN/Ciselniky-k-nemovitosti.aspx
-- =====================================================

-- SC_D_POZEMKU - Land Types (Druh pozemku)
CREATE TABLE IF NOT EXISTS cuzk_land_types (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name_cs VARCHAR(150) NOT NULL,
    name_en VARCHAR(150) NOT NULL,
    description_cs VARCHAR(500),
    description_en VARCHAR(500),
    abbreviation VARCHAR(20),
    agricultural_land BOOLEAN,
    land_parcel_type_code VARCHAR(10),
    building_parcel BOOLEAN,
    mandatory_land_protection BOOLEAN,
    mandatory_land_use BOOLEAN,
    valid_from DATE,
    valid_to DATE,
    active BOOLEAN NOT NULL DEFAULT true,
    sort_order INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- SC_ZP_VYUZITI_POZ - Land Uses (Způsob využití pozemku)
CREATE TABLE IF NOT EXISTS cuzk_land_uses (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name_cs VARCHAR(150) NOT NULL,
    name_en VARCHAR(150) NOT NULL,
    description_cs VARCHAR(500),
    description_en VARCHAR(500),
    abbreviation VARCHAR(20),
    land_parcel_type_code VARCHAR(10),
    valid_from DATE,
    valid_to DATE,
    active BOOLEAN NOT NULL DEFAULT true,
    sort_order INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- SC_POZEMEK_VYUZITI - Land Type Use mapping
CREATE TABLE IF NOT EXISTS cuzk_land_type_uses (
    id BIGSERIAL PRIMARY KEY,
    land_type_code VARCHAR(10) NOT NULL,
    land_use_code VARCHAR(10) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE(land_type_code, land_use_code)
);

-- SC_T_BUDOV - Building Types (Typ stavby)
CREATE TABLE IF NOT EXISTS cuzk_building_types (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name_cs VARCHAR(150) NOT NULL,
    name_en VARCHAR(150) NOT NULL,
    description_cs VARCHAR(500),
    description_en VARCHAR(500),
    abbreviation VARCHAR(20),
    entry_code BOOLEAN,
    valid_from DATE,
    valid_to DATE,
    active BOOLEAN NOT NULL DEFAULT true,
    sort_order INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- SC_ZP_VYUZITI_BUD - Building Uses (Způsob využití budovy)
CREATE TABLE IF NOT EXISTS cuzk_building_uses (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name_cs VARCHAR(150) NOT NULL,
    name_en VARCHAR(150) NOT NULL,
    description_cs VARCHAR(500),
    description_en VARCHAR(500),
    abbreviation VARCHAR(20),
    valid_from DATE,
    valid_to DATE,
    active BOOLEAN NOT NULL DEFAULT true,
    sort_order INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- SC_TYPB_ZPVYB - Building Type Use mapping
CREATE TABLE IF NOT EXISTS cuzk_building_type_uses (
    id BIGSERIAL PRIMARY KEY,
    building_type_code VARCHAR(10) NOT NULL,
    building_use_code VARCHAR(10) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE(building_type_code, building_use_code)
);

-- SC_ZP_URCENI_VYMERY - Area Determination (Způsob určení výměry)
CREATE TABLE IF NOT EXISTS cuzk_area_determinations (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name_cs VARCHAR(150) NOT NULL,
    name_en VARCHAR(150) NOT NULL,
    description_cs VARCHAR(500),
    description_en VARCHAR(500),
    valid_from DATE,
    valid_to DATE,
    active BOOLEAN NOT NULL DEFAULT true,
    sort_order INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- SC_UCELY_PS - Building Right Purpose (Účel práva stavby)
CREATE TABLE IF NOT EXISTS cuzk_building_right_purposes (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name_cs VARCHAR(150) NOT NULL,
    name_en VARCHAR(150) NOT NULL,
    description_cs VARCHAR(500),
    description_en VARCHAR(500),
    valid_from DATE,
    valid_to DATE,
    active BOOLEAN NOT NULL DEFAULT true,
    sort_order INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- SC_T_JEDNOTEK - Unit Types (Typ jednotky)
CREATE TABLE IF NOT EXISTS cuzk_unit_types (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name_cs VARCHAR(150) NOT NULL,
    name_en VARCHAR(150) NOT NULL,
    description_cs VARCHAR(500),
    description_en VARCHAR(500),
    abbreviation VARCHAR(20),
    civil_code BOOLEAN,
    valid_from DATE,
    valid_to DATE,
    active BOOLEAN NOT NULL DEFAULT true,
    sort_order INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- SC_ZP_VYUZITI_JED - Unit Uses (Způsob využití jednotky)
CREATE TABLE IF NOT EXISTS cuzk_unit_uses (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name_cs VARCHAR(150) NOT NULL,
    name_en VARCHAR(150) NOT NULL,
    description_cs VARCHAR(500),
    description_en VARCHAR(500),
    abbreviation VARCHAR(20),
    valid_from DATE,
    valid_to DATE,
    active BOOLEAN NOT NULL DEFAULT true,
    sort_order INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- SC_T_OCHRANY_NEM - Property Protection Types (Typ ochrany nemovitosti)
CREATE TABLE IF NOT EXISTS cuzk_property_protection_types (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name_cs VARCHAR(150) NOT NULL,
    name_en VARCHAR(150) NOT NULL,
    description_cs VARCHAR(500),
    description_en VARCHAR(500),
    valid_from DATE,
    valid_to DATE,
    active BOOLEAN NOT NULL DEFAULT true,
    sort_order INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- SC_ZP_OCHRANY_NEM - Property Protections (Způsob ochrany nemovitosti)
CREATE TABLE IF NOT EXISTS cuzk_property_protections (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name_cs VARCHAR(150) NOT NULL,
    name_en VARCHAR(150) NOT NULL,
    description_cs VARCHAR(500),
    description_en VARCHAR(500),
    protection_type_code VARCHAR(10),
    applies_to_land BOOLEAN,
    applies_to_building BOOLEAN,
    applies_to_unit BOOLEAN,
    applies_to_building_right BOOLEAN,
    valid_from DATE,
    valid_to DATE,
    active BOOLEAN NOT NULL DEFAULT true,
    sort_order INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- SC_ZDROJE_PARCEL_ZE - Simplified Parcel Sources (Zdroje parcel zjednodušené evidence)
CREATE TABLE IF NOT EXISTS cuzk_simplified_parcel_sources (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name_cs VARCHAR(150) NOT NULL,
    name_en VARCHAR(150) NOT NULL,
    description_cs VARCHAR(500),
    description_en VARCHAR(500),
    abbreviation VARCHAR(20),
    valid_from DATE,
    valid_to DATE,
    active BOOLEAN NOT NULL DEFAULT true,
    sort_order INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- SC_BPEJ - Soil Ecological Units (Bonitované půdně ekologické jednotky)
CREATE TABLE IF NOT EXISTS cuzk_soil_ecological_units (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name_cs VARCHAR(150) NOT NULL,
    name_en VARCHAR(150) NOT NULL,
    description_cs VARCHAR(500),
    description_en VARCHAR(500),
    price NUMERIC(10,2),
    detailed_description TEXT,
    valid_from DATE,
    valid_to DATE,
    active BOOLEAN NOT NULL DEFAULT true,
    sort_order INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);
