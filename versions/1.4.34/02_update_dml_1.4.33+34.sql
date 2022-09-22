-- 228
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.custodia.caib.verificacio.url.bas', NULL, 'propietat.plugin.custodia.caib.verificacio.url.bas', 'CUSTODIA', '5', 'FILE', 'TEXT');

-- 231
UPDATE pbl_organ_gestor SET estat = 'V' WHERE actiu=1;
UPDATE pbl_organ_gestor SET estat = 'E' WHERE actiu=0;