-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 28/01/22 08:54
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.3.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.4.30/134.yaml::1626254629134-1::limit
ALTER TABLE pbl_servei_config ADD max_peticions_min INTEGER;

-- Changeset db/changelog/changes/1.4.30/134.yaml::1626254629134-2::limit
ALTER TABLE pbl_consulta ADD finalitat VARCHAR2(250);

ALTER TABLE pbl_consulta ADD consentiment INTEGER;

ALTER TABLE pbl_consulta ADD expedient_id VARCHAR2(25);

ALTER TABLE pbl_consulta ADD dades_especifiques VARCHAR2(2048);

-- Changeset db/changelog/changes/1.4.30/141.yaml::1643192041683-1::limit
ALTER TABLE pbl_servei_camp ADD val_regexp VARCHAR2(100);

ALTER TABLE pbl_servei_camp ADD val_min INTEGER;

ALTER TABLE pbl_servei_camp ADD val_max INTEGER;

ALTER TABLE pbl_servei_camp ADD val_data_cmp_op INTEGER;

ALTER TABLE pbl_servei_camp ADD val_data_cmp_camp2_id NUMBER(38, 0);

ALTER TABLE pbl_servei_camp ADD val_data_cmp_num INTEGER;

ALTER TABLE pbl_servei_camp ADD val_data_cmp_tipus INTEGER;

