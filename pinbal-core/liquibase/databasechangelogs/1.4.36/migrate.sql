-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 20/12/22 09:38
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.3.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.4.36/235.yaml::1666106558105-1::limit
UPDATE pbl_config_type SET value = 'es.caib.pinbal.plugins.caib.FirmaServidorPluginPortafib,es.caib.pinbal.plugins.caib.FirmaSimpleServidorPluginPortafib' WHERE code = 'FIRMA_SERVIDOR_CLASS';

INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.firmaservidor.portafib.endpoint', NULL, 'propietat.plugin.firmaservidor.portafib.endpoint', 'FIRMA_SERVIDOR', '7', 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.firmaservidor.portafib.auth.username', NULL, 'propietat.plugin.firmaservidor.portafib.auth.username', 'FIRMA_SERVIDOR', '8', 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.firmaservidor.portafib.auth.password', NULL, 'propietat.plugin.firmaservidor.portafib.auth.password', 'FIRMA_SERVIDOR', '9', 'FILE', 'PASS');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.firmaservidor.portafib.perfil', 'PADES', 'propietat.plugin.firmaservidor.portafib.perfil', 'FIRMA_SERVIDOR', '10', 'DATABASE', 'TEXT');

