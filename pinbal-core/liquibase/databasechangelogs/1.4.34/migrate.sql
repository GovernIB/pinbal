-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 19/08/22 08:36
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.3.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.4.34/231.yaml::1660889406593-1::limit
ALTER TABLE pbl_organ_gestor ADD estat VARCHAR2(10 CHAR);

UPDATE pbl_organ_gestor SET estat = 'V' WHERE actiu=1;
UPDATE pbl_organ_gestor SET estat = 'E' WHERE actiu=0;

