-- =====================================================
-- Migration: Remove active column from codelists, add validity dates
-- This migration normalizes the validity system:
-- - Codelists use valid_from/valid_to dates for validity
-- - Only flags table keeps the active boolean
-- =====================================================

-- Add valid_from and valid_to to voltage_levels
ALTER TABLE voltage_levels ADD COLUMN IF NOT EXISTS valid_from DATE;
ALTER TABLE voltage_levels ADD COLUMN IF NOT EXISTS valid_to DATE;

-- Add valid_from and valid_to to network_types
ALTER TABLE network_types ADD COLUMN IF NOT EXISTS valid_from DATE;
ALTER TABLE network_types ADD COLUMN IF NOT EXISTS valid_to DATE;

-- Add valid_from and valid_to to codelist_registry
ALTER TABLE codelist_registry ADD COLUMN IF NOT EXISTS valid_from DATE;
ALTER TABLE codelist_registry ADD COLUMN IF NOT EXISTS valid_to DATE;

-- Add valid_from and valid_to to building_classifications (KSO)
ALTER TABLE building_classifications ADD COLUMN IF NOT EXISTS valid_from DATE;
ALTER TABLE building_classifications ADD COLUMN IF NOT EXISTS valid_to DATE;

-- Add valid_from and valid_to to mapping tables
ALTER TABLE cuzk_land_type_uses ADD COLUMN IF NOT EXISTS valid_from DATE;
ALTER TABLE cuzk_land_type_uses ADD COLUMN IF NOT EXISTS valid_to DATE;

ALTER TABLE cuzk_building_type_uses ADD COLUMN IF NOT EXISTS valid_from DATE;
ALTER TABLE cuzk_building_type_uses ADD COLUMN IF NOT EXISTS valid_to DATE;

-- Set default validity dates for all existing records (2000-01-01 to 2199-12-31)
UPDATE voltage_levels SET valid_from = '2000-01-01', valid_to = '2199-12-31' WHERE valid_from IS NULL;
UPDATE network_types SET valid_from = '2000-01-01', valid_to = '2199-12-31' WHERE valid_from IS NULL;
UPDATE codelist_registry SET valid_from = '2000-01-01', valid_to = '2199-12-31' WHERE valid_from IS NULL;
UPDATE building_classifications SET valid_from = '2000-01-01', valid_to = '2199-12-31' WHERE valid_from IS NULL;
UPDATE cuzk_land_type_uses SET valid_from = '2000-01-01', valid_to = '2199-12-31' WHERE valid_from IS NULL;
UPDATE cuzk_building_type_uses SET valid_from = '2000-01-01', valid_to = '2199-12-31' WHERE valid_from IS NULL;

-- Update CUZK tables with NULL valid_from/valid_to
UPDATE cuzk_land_types SET valid_from = '2000-01-01', valid_to = '2199-12-31' WHERE valid_from IS NULL;
UPDATE cuzk_land_uses SET valid_from = '2000-01-01', valid_to = '2199-12-31' WHERE valid_from IS NULL;
UPDATE cuzk_building_types SET valid_from = '2000-01-01', valid_to = '2199-12-31' WHERE valid_from IS NULL;
UPDATE cuzk_building_uses SET valid_from = '2000-01-01', valid_to = '2199-12-31' WHERE valid_from IS NULL;
UPDATE cuzk_area_determinations SET valid_from = '2000-01-01', valid_to = '2199-12-31' WHERE valid_from IS NULL;
UPDATE cuzk_building_right_purposes SET valid_from = '2000-01-01', valid_to = '2199-12-31' WHERE valid_from IS NULL;
UPDATE cuzk_unit_types SET valid_from = '2000-01-01', valid_to = '2199-12-31' WHERE valid_from IS NULL;
UPDATE cuzk_unit_uses SET valid_from = '2000-01-01', valid_to = '2199-12-31' WHERE valid_from IS NULL;
UPDATE cuzk_property_protection_types SET valid_from = '2000-01-01', valid_to = '2199-12-31' WHERE valid_from IS NULL;
UPDATE cuzk_property_protections SET valid_from = '2000-01-01', valid_to = '2199-12-31' WHERE valid_from IS NULL;
UPDATE cuzk_simplified_parcel_sources SET valid_from = '2000-01-01', valid_to = '2199-12-31' WHERE valid_from IS NULL;
UPDATE cuzk_soil_ecological_units SET valid_from = '2000-01-01', valid_to = '2199-12-31' WHERE valid_from IS NULL;

-- Drop active column from all tables except flags
ALTER TABLE voltage_levels DROP COLUMN IF EXISTS active;
ALTER TABLE network_types DROP COLUMN IF EXISTS active;
ALTER TABLE codelist_registry DROP COLUMN IF EXISTS active;
ALTER TABLE building_classifications DROP COLUMN IF EXISTS active;
ALTER TABLE cuzk_land_types DROP COLUMN IF EXISTS active;
ALTER TABLE cuzk_land_uses DROP COLUMN IF EXISTS active;
ALTER TABLE cuzk_land_type_uses DROP COLUMN IF EXISTS active;
ALTER TABLE cuzk_building_types DROP COLUMN IF EXISTS active;
ALTER TABLE cuzk_building_uses DROP COLUMN IF EXISTS active;
ALTER TABLE cuzk_building_type_uses DROP COLUMN IF EXISTS active;
ALTER TABLE cuzk_area_determinations DROP COLUMN IF EXISTS active;
ALTER TABLE cuzk_building_right_purposes DROP COLUMN IF EXISTS active;
ALTER TABLE cuzk_unit_types DROP COLUMN IF EXISTS active;
ALTER TABLE cuzk_unit_uses DROP COLUMN IF EXISTS active;
ALTER TABLE cuzk_property_protection_types DROP COLUMN IF EXISTS active;
ALTER TABLE cuzk_property_protections DROP COLUMN IF EXISTS active;
ALTER TABLE cuzk_simplified_parcel_sources DROP COLUMN IF EXISTS active;
ALTER TABLE cuzk_soil_ecological_units DROP COLUMN IF EXISTS active;
