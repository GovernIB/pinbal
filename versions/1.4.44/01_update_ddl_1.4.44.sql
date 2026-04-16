-- Changeset db/changelog/changes/1.4.44/354.yaml::354-01::limit
-- 1. Afegir columna nova nullable
ALTER TABLE PBL_AVIS ADD data_final_new TIMESTAMP(6);

-- 2. Copiar dades
UPDATE PBL_AVIS SET data_final_new = data_final;

-- 3. Eliminar antiga
ALTER TABLE PBL_AVIS DROP COLUMN data_final;

-- 4. Renombrar
ALTER TABLE PBL_AVIS RENAME COLUMN data_final_new TO data_final;

-- Changeset db/changelog/changes/1.4.44/357.yaml::357-01::limit
ALTER TABLE PBL_CONSULTA ADD app_guarda_just_arx NUMBER(1,0) DEFAULT 0 NOT NULL;
ALTER TABLE PBL_CONSULTA_HIST ADD app_guarda_just_arx NUMBER(1,0) DEFAULT 0 NOT NULL;
