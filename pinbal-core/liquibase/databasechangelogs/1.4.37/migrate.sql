-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 12/04/23 18:46
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.3.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.4.37/267.yaml::1681312463404-1::limit
ALTER TABLE pbl_servei_camp ADD inicialitzar NUMBER(1) DEFAULT '0';

