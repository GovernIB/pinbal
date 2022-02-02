-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 28/01/22 09:14
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.3.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.4.29/134.yaml::1626254629134-1::limit
ALTER TABLE pbl_servei_config ADD max_peticions_min INTEGER;

-- Changeset db/changelog/changes/1.4.29/134.yaml::1626254629134-2::limit
ALTER TABLE pbl_consulta ADD finalitat VARCHAR2(250);

ALTER TABLE pbl_consulta ADD consentiment INTEGER;

ALTER TABLE pbl_consulta ADD expedient_id VARCHAR2(25);

ALTER TABLE pbl_consulta ADD dades_especifiques VARCHAR2(2048);

