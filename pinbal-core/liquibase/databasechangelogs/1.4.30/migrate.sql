-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 28/02/22 17:44
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.3.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.4.30/141.yaml::1643192041683-1::limit
ALTER TABLE pbl_servei_camp ADD val_regexp VARCHAR2(100 CHAR);
ALTER TABLE pbl_servei_camp ADD val_min INTEGER;
ALTER TABLE pbl_servei_camp ADD val_max INTEGER;
ALTER TABLE pbl_servei_camp ADD val_data_cmp_op INTEGER;
ALTER TABLE pbl_servei_camp ADD val_data_cmp_camp2_id NUMBER(38, 0);
ALTER TABLE pbl_servei_camp ADD val_data_cmp_num INTEGER;
ALTER TABLE pbl_servei_camp ADD val_data_cmp_tipus INTEGER;

-- Changeset db/changelog/changes/1.4.30/199.yaml::1643192041683-2::limit
CREATE TABLE pbl_consulta_hist (
    id NUMBER(38, 0) NOT NULL,
    custodiat NUMBER(1),
    departament VARCHAR2(64 CHAR) NOT NULL,
    error VARCHAR2(4000 CHAR),
    estat INTEGER NOT NULL,
    peticion_id VARCHAR2(26 CHAR) NOT NULL,
    titular_docnum VARCHAR2(14 CHAR),
    titular_doctip VARCHAR2(16 CHAR),
    titular_lling1 VARCHAR2(64 CHAR),
    titular_lling2 VARCHAR2(64 CHAR),
    titular_nom VARCHAR2(64 CHAR),
    procserv_id NUMBER(38, 0) NOT NULL,
    recobriment NUMBER(1),
    titular_nomcomplet VARCHAR2(122 CHAR),
    funcionari_nom VARCHAR2(128 CHAR),
    funcionari_docnum VARCHAR2(16 CHAR) NOT NULL,
    solicitud_id VARCHAR2(64 CHAR),
    multiple NUMBER(1),
    pare_id NUMBER(38, 0),
    custodia_url VARCHAR2(255 CHAR),
    justificant_error VARCHAR2(2000 CHAR),
    justificant_estat INTEGER NOT NULL,
    custodia_id VARCHAR2(255 CHAR),
    arxiu_expedient_uuid VARCHAR2(100 CHAR),
    arxiu_document_uuid VARCHAR2(100 CHAR),
    arxiu_expedient_tancat NUMBER(1),
    finalitat VARCHAR2(250 CHAR),
    consentiment INTEGER,
    expedient_id VARCHAR2(25 CHAR),
    dades_especifiques VARCHAR2(2048 CHAR),
    createdby_codi VARCHAR2(64 CHAR),
    createddate TIMESTAMP,
    lastmodifiedby_codi VARCHAR2(64 CHAR),
    lastmodifieddate TIMESTAMP,
    version NUMBER(38, 0) NOT NULL,
    CONSTRAINT pbl_consulta_hist_pk PRIMARY KEY (id));

CREATE INDEX PBL_CONH_CREDATCOD_I ON pbl_consulta_hist(CREATEDDATE DESC, CREATEDBY_CODI);
CREATE INDEX PBL_CONSULTAH_CREATEDBY_I ON pbl_consulta_hist(CREATEDBY_CODI);
CREATE INDEX PBL_CONSULTAH_PARE_I ON pbl_consulta_hist(PARE_ID);
CREATE INDEX PBL_CONSULTAH_PROCSERV_I ON pbl_consulta_hist(PROCSERV_ID);
CREATE INDEX PBL_CONSULTAH_MULT_I ON pbl_consulta_hist(CREATEDBY_CODI, ESTAT, MULTIPLE);

grant select, update, insert, delete on pbl_consulta_hist to www_pinbal;

