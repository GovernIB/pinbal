-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 28/01/22 08:18
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.3.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.4.28/163.yaml::1636451116937-1::limit
CREATE INDEX pbl_con_credatcod_i ON pbl_consulta(createddate DESC, createdby_codi);

