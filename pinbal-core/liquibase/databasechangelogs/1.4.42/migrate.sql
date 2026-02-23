-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 27/11/25 14:43
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.3.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.4.42/282.yaml::1727858290::limit
ALTER TABLE pbl_usuari ADD num_elements_pagina INTEGER DEFAULT '10';

-- Changeset db/changelog/changes/1.4.42/332.yaml::1738059685::limit
CREATE TABLE pbl_servei_xsd (id NUMBER(38, 0) NOT NULL, servei_id VARCHAR2(64 CHAR) NOT NULL, tipus VARCHAR2(32 CHAR) NOT NULL, path VARCHAR2(255 CHAR) NOT NULL, nomarxiu VARCHAR2(255 CHAR) NOT NULL, version NUMBER(38, 0) NOT NULL, data TIMESTAMP NOT NULL, CONSTRAINT PK_PBL_SERVEI_XSD PRIMARY KEY (id));

ALTER TABLE pbl_servei_xsd ADD CONSTRAINT pbl_servei_xsd_uk UNIQUE (servei_id, tipus);

CREATE INDEX pbl_servei_xsd_servei_i ON pbl_servei_xsd(servei_id);

GRANT SELECT, UPDATE, INSERT, DELETE ON pbl_servei_xsd TO WWW_PINBAL;

-- Changeset db/changelog/changes/1.4.42/334.yaml::1739187287::limit
INSERT INTO pbl_config_GROUP (code, description_key, position) VALUES ('LOGS', 'propietat.grup.logs', '4');

INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.log.tipus.GENERIC', 'false', 'propietat.log.tipus.GENERIC', 'LOGS', '0', 'DATABASE', 'BOOL');

INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.log.tipus.CONSULTA', 'false', 'propietat.log.tipus.CONSULTA', 'LOGS', '1', 'DATABASE', 'BOOL');

INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.log.tipus.CONS_MULT', 'false', 'propietat.log.tipus.CONS_MULT', 'LOGS', '2', 'DATABASE', 'BOOL');

INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.log.tipus.CONS_REC', 'false', 'propietat.log.tipus.CONS_REC', 'LOGS', '3', 'DATABASE', 'BOOL');

INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.log.tipus.CONS_REC_MULT', 'false', 'propietat.log.tipus.CONS_REC_MULT', 'LOGS', '4', 'DATABASE', 'BOOL');

-- Changeset db/changelog/changes/1.4.42/335.yaml::1739193869::limit
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.tasca.auto.consulta.pendent.repeticio', 'false', 'propietat.tasca.auto.consulta.pendent.repeticio', 'TASQUES', '7', 'DATABASE', 'TEXT');

-- Changeset db/changelog/changes/1.4.42/indexos_monitor.yaml::crear-index-pbl-mon-int-data::limit
CREATE INDEX pbl_mon_int_data_i ON PBL_MON_INT(data);

-- Changeset db/changelog/changes/1.4.42/indexos_monitor.yaml::crear-index-pbl-mon-int-param-mon-int::siona
CREATE INDEX pbl_mon_int_param_mon_int_i ON PBL_MON_INT_PARAM(mon_int_id);

-- Changeset db/changelog/changes/1.4.42/prop_comuns_url.yaml::inserir-config-dadescomunes-url::limit
INSERT INTO PBL_CONFIG (KEY, VALUE, DESCRIPTION_KEY, GROUP_CODE, POSITION, SOURCE_PROPERTY, TYPE_CODE) VALUES ('es.caib.pinbal.dadescomunes.base.url', NULL, 'propietat.dadescomunes.base.url', 'GENERAL', 3, 'FILE', 'TEXT');

-- Changeset db/changelog/changes/1.4.42/prop_comuns_url_02.yaml::inserir-config-dadescomunes-url::limit
INSERT INTO PBL_CONFIG_GROUP (CODE, VALUE, DESCRIPTION_KEY, POSITION, PARENT_CODE) VALUES ('DADES_COMUNS', 'propietat.grup.plugins.dades.comunes', 'propietat.dadescomunes.base.url', 6, 'PLUGINS');

UPDATE PBL_CONFIG SET GROUP_CODE = 'DADES_COMUNS' WHERE KEY = 'es.caib.pinbal.dadescomunes.base.url';

-- Changeset db/changelog/changes/1.4.42/sia_origen.yaml::add-codi-sia-origen-column::limit
ALTER TABLE PBL_PROCEDIMENT ADD CODI_SIA_ORIGEN VARCHAR2(64 CHAR);

-- Changeset db/changelog/changes/1.4.42/ter.yaml::1740397830::limit
ALTER TABLE pbl_consulta ADD der TIMESTAMP;

ALTER TABLE pbl_consulta_hist ADD der TIMESTAMP;

ALTER TABLE pbl_consulta_list ADD der TIMESTAMP;

ALTER TABLE pbl_consulta_hist_list ADD der TIMESTAMP;

