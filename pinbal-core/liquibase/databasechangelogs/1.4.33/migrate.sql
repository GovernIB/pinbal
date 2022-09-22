-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 23/08/22 08:17
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.3.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.4.33/228.yaml::1650469587624-1::limit
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.custodia.caib.verificacio.url.bas', NULL, 'propietat.plugin.custodia.caib.verificacio.url.bas', 'CUSTODIA', '5', 'FILE', 'TEXT');
