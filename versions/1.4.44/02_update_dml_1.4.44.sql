-- Changeset db/changelog/changes/1.4.44/362.yaml::362-01::limit
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.log.dir', NULL, 'propietat.log.dir', 'LOGS', '5', 'DATABASE', 'TEXT');
