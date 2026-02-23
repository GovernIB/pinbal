-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 27/11/25 14:43
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.3.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.4.43/337.yaml::337-01::limit
ALTER TABLE pbl_servei_config ADD arrel_resposta_path VARCHAR2(255 CHAR);

-- Changeset db/changelog/changes/1.4.43/342.yaml::342-01::limit
ALTER TABLE pbl_explot_temps RENAME COLUMN dia TO dia_setmana;

ALTER TABLE pbl_explot_temps ADD dia INTEGER;

UPDATE pbl_explot_temps SET dia = EXTRACT(DAY FROM data);

-- Changeset db/changelog/changes/1.4.43/342.yaml::342-02::limit
ALTER TABLE pbl_explot_consulta_dim ADD entitat_codi VARCHAR2(64 CHAR);

ALTER TABLE pbl_explot_consulta_dim ADD procediment_codi VARCHAR2(20 CHAR);

UPDATE pbl_explot_consulta_dim SET entitat_codi = (SELECT e.codi FROM pbl_entitat e WHERE e.id = entitat_id), procediment_codi = (SELECT p.codi FROM pbl_procediment p WHERE p.id = procediment_id);

-- Changeset db/changelog/changes/1.4.43/342.yaml::342-03::limit
CREATE INDEX pbl_cons_procediment_i ON pbl_consulta(procediment_id);

CREATE INDEX pbl_cons_serveicodi_i ON pbl_consulta(servei_codi);

CREATE INDEX pbl_cons_explotorder_i ON pbl_consulta(entitat_id, procediment_id, servei_codi, createdby_codi);

-- Changeset db/changelog/changes/1.4.43/352.yaml::352-01::limit
ALTER TABLE PBL_SERVEI_CONFIG ADD use_cert_entitat NUMBER(1) DEFAULT 0;

ALTER TABLE CORE_CLAVE_PRIVADA ADD per_entitat NUMBER(1) DEFAULT 0;

