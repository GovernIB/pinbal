-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 21/02/24 13:46
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.3.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.4.40/290.yaml::1708502769667-1::limit
ALTER TABLE pbl_usuari ADD procediment_id NUMBER(38, 0);
ALTER TABLE pbl_usuari ADD servei_codi VARCHAR2(64 CHAR);
ALTER TABLE pbl_usuari ADD entitat_id NUMBER(38, 0);
ALTER TABLE pbl_usuari ADD departament VARCHAR2(250 CHAR);
ALTER TABLE pbl_usuari ADD finalitat VARCHAR2(250 CHAR);

INSERT INTO pbl_config (key, description_key, group_code, position, source_property, type_code, value) VALUES ('es.caib.pinbal.justificant.recobriment.generar', 'propietat.justificant.recobriment.generar', 'JUSTIFICANT', '3', 'DATABASE', 'BOOL', 'true');

