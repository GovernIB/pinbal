-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 07/04/26 16:30
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.3.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.4.44/354.yaml::354-01::limit
ALTER TABLE PBL_AVIS MODIFY data_final NULL;

-- Changeset db/changelog/changes/1.4.44/355.yaml::355-01::limit
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.recobriment.requereix.expedientid', 'false', 'propietat.recobriment.requereix.expedientid', 'RECOBRIMENT', '3', 'DATABASE', 'BOOL');

-- Changeset db/changelog/changes/1.4.44/362.yaml::362-01::limit
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.log.dir', NULL, 'propietat.log.dir', 'LOGS', '5', 'DATABASE', 'TEXT');

