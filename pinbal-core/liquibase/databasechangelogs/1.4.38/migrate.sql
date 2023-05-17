-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 03/05/23 08:43
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.3.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.4.38/269.yaml::1682416448843-1::limit
ALTER TABLE pbl_servei_camp_grup ADD pare_id NUMBER(38, 0);
ALTER TABLE pbl_servei_camp_grup ADD ajuda CLOB;
ALTER TABLE pbl_servei_camp_grup ADD CONSTRAINT pbl_grup_pare_fk FOREIGN KEY (pare_id) REFERENCES pbl_servei_camp_grup (id);
ALTER TABLE pbl_servei_camp_grup MOVE LOB(ajuda) STORE AS pbl_grup_ajuda_lob(TABLESPACE pinbal_lob INDEX pbl_grup_ajuda_lob_i);

CREATE TABLE pbl_servei_regla (id NUMBER(38, 0) NOT NULL, nom VARCHAR2(255 CHAR) NOT NULL, servei_id NUMBER(38, 0) NOT NULL, modificat VARCHAR2(32 CHAR) NOT NULL, accio VARCHAR2(325 CHAR) NOT NULL, ordre INTEGER NOT NULL, createdby_codi VARCHAR2(64 CHAR), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64 CHAR), lastmodifieddate TIMESTAMP, CONSTRAINT pbl_servei_regla_pk PRIMARY KEY (id));
ALTER TABLE pbl_servei_regla ADD CONSTRAINT pbl_servei_regla_pk UNIQUE (nom, servei_id);
ALTER TABLE pbl_servei_regla ADD CONSTRAINT pbl_regla_servei_fk FOREIGN KEY (servei_id) REFERENCES core_servicio (id);
CREATE INDEX pbl_regla_servei_fk_i ON pbl_servei_regla(servei_id);

CREATE TABLE pbl_servei_regla_valor_mod (regla_id NUMBER(38, 0) NOT NULL, valor VARCHAR2(255));
ALTER TABLE pbl_servei_regla_valor_mod ADD CONSTRAINT pbl_servei_regla_valor_mod_pk PRIMARY KEY (regla_id, valor);
ALTER TABLE pbl_servei_regla_valor_mod ADD CONSTRAINT pbl_regla_valor_mod_fk FOREIGN KEY (regla_id) REFERENCES pbl_servei_regla (id);
CREATE INDEX pbl_regla_valor_mod_fk_i ON pbl_servei_regla_valor_mod(regla_id);

CREATE TABLE pbl_servei_regla_valor_afc (regla_id NUMBER(38, 0) NOT NULL, valor VARCHAR2(255));
ALTER TABLE pbl_servei_regla_valor_afc ADD CONSTRAINT pbl_servei_regla_valor_afc_pk PRIMARY KEY (regla_id, valor);
ALTER TABLE pbl_servei_regla_valor_afc ADD CONSTRAINT pbl_regla_valor_afc_fk FOREIGN KEY (regla_id) REFERENCES pbl_servei_regla (id);
CREATE INDEX pbl_regla_valor_afc_fk_i ON pbl_servei_regla_valor_afc(regla_id);


grant select, update, insert, delete on pbl_servei_regla to www_pinbal;
grant select, update, insert, delete on pbl_servei_regla_valor_mod to www_pinbal;
grant select, update, insert, delete on pbl_servei_regla_valor_afc to www_pinbal;

