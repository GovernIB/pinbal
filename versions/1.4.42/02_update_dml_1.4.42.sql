-- #298

INSERT INTO PBL_CONFIG
("KEY", VALUE, DESCRIPTION_KEY, GROUP_CODE, "POSITION", SOURCE_PROPERTY, TYPE_CODE, LASTMODIFIEDBY_CODI, LASTMODIFIEDDATE)
VALUES('es.caib.pinbal.tasca.auto.exp.esborrar.monitor', '8', 'propietat.tasca.auto.exp.esborrar.monitor', 'TASQUES', 5, 'DATABASE', 'TEXT', NULL, NULL);
INSERT INTO PBL_CONFIG
("KEY", VALUE, DESCRIPTION_KEY, GROUP_CODE, "POSITION", SOURCE_PROPERTY, TYPE_CODE, LASTMODIFIEDBY_CODI, LASTMODIFIEDDATE)
VALUES('es.caib.pinbal.tasca.auto.exp.esborrar.monitor.dies.antiguitat', '14', 'propietat.tasca.auto.exp.esborrar.monitor.dies.antiguitat', 'TASQUES', 6, 'DATABASE', 'TEXT', NULL, NULL);

-- #311
UPDATE PBL_SERVEI_CAMP SET MIDA = 6;

-- #334
INSERT INTO pbl_config_GROUP (code, description_key, position) VALUES ('LOGS', 'propietat.grup.logs', '4');

INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.log.tipus.GENERIC', 'false', 'propietat.log.tipus.GENERIC', 'LOGS', '0', 'DATABASE', 'BOOL');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.log.tipus.CONSULTA', 'false', 'propietat.log.tipus.CONSULTA', 'LOGS', '1', 'DATABASE', 'BOOL');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.log.tipus.CONS_MULT', 'false', 'propietat.log.tipus.CONS_MULT', 'LOGS', '2', 'DATABASE', 'BOOL');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.log.tipus.CONS_REC', 'false', 'propietat.log.tipus.CONS_REC', 'LOGS', '3', 'DATABASE', 'BOOL');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.log.tipus.CONS_REC_MULT', 'false', 'propietat.log.tipus.CONS_REC_MULT', 'LOGS', '4', 'DATABASE', 'BOOL');

-- #335
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.tasca.auto.consulta.pendent.repeticio', 'false', 'propietat.tasca.auto.consulta.pendent.repeticio', 'TASQUES', '7', 'DATABASE', 'TEXT');
