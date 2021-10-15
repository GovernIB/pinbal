-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 23/09/21 15:32
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.3.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.4.26/142.yaml::1627985337018-1::limit
CREATE TABLE pbl_avis (id NUMBER(19) NOT NULL, assumpte VARCHAR2(256) NOT NULL, missatge VARCHAR2(2048) NOT NULL, data_inici TIMESTAMP NOT NULL, data_final TIMESTAMP NOT NULL, actiu NUMBER(1) NOT NULL, avis_nivell VARCHAR2(10) NOT NULL, createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP);

ALTER TABLE pbl_avis ADD CONSTRAINT pbl_avis_pk PRIMARY KEY (id);

grant select, update, insert, delete on PBL_AVIS to www_pinbal;

-- Changeset db/changelog/changes/1.4.26/168.yaml::1630317753837-1::limit
ALTER TABLE pbl_procediment ADD valor_camp_automatizado NUMBER(1);

ALTER TABLE pbl_procediment ADD valor_camp_clasetramite INTEGER;

