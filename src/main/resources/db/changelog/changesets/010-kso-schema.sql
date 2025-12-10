-- =====================================================
-- KSO - Building Classifications (Klasifikace stavebních objektů)
-- Source: https://www.cenovasoustava.cz/pJKSO.asp
-- =====================================================

-- Hierarchical table for building classifications (JKSO/KSO)
-- Levels: 1 = Obor, 2 = Skupina, 3 = Podskupina
CREATE TABLE IF NOT EXISTS building_classifications (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name_cs VARCHAR(200) NOT NULL,
    name_en VARCHAR(200) NOT NULL,
    description_cs TEXT,
    description_en TEXT,
    level INTEGER NOT NULL,
    parent_id BIGINT REFERENCES building_classifications(id),
    active BOOLEAN NOT NULL DEFAULT true,
    sort_order INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Index for parent lookups
CREATE INDEX IF NOT EXISTS idx_building_classifications_parent ON building_classifications(parent_id);

-- Index for level lookups
CREATE INDEX IF NOT EXISTS idx_building_classifications_level ON building_classifications(level);
