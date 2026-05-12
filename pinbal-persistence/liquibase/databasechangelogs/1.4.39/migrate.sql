-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 04/12/23 16:52
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.3.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.4.39/289.yaml::1701243665282-1::limit
ALTER TABLE pbl_consulta ADD entitat_id NUMBER(38, 0);
ALTER TABLE pbl_consulta ADD servei_codi VARCHAR2(64 CHAR);
ALTER TABLE pbl_consulta ADD procediment_id NUMBER(38, 0);
ALTER TABLE pbl_consulta ADD CONSTRAINT pbl_consulta_proced_fk FOREIGN KEY (procediment_id) REFERENCES pbl_procediment (id);
ALTER TABLE pbl_consulta ADD CONSTRAINT pbl_consulta_entitat_fk FOREIGN KEY (entitat_id) REFERENCES pbl_entitat (id);

ALTER TABLE pbl_consulta_hist ADD entitat_id NUMBER(38, 0);
ALTER TABLE pbl_consulta_hist ADD servei_codi VARCHAR2(64 CHAR);
ALTER TABLE pbl_consulta_hist ADD procediment_id NUMBER(38, 0);
ALTER TABLE pbl_consulta_hist ADD CONSTRAINT pbl_consultah_proced_fk FOREIGN KEY (procediment_id) REFERENCES pbl_procediment (id);
ALTER TABLE pbl_consulta_hist ADD CONSTRAINT pbl_consultah_entitat_fk FOREIGN KEY (entitat_id) REFERENCES pbl_entitat (id);

UPDATE PBL_CONSULTA c SET c.entitat_id = (SELECT ppr.entitat_id FROM pbl_procediment_servei pps, pbl_procediment ppr WHERE pps.id = c.procserv_id AND ppr.id = pps.procediment_id);
UPDATE PBL_CONSULTA c SET c.servei_codi = (SELECT pps.servei_id FROM pbl_procediment_servei pps WHERE pps.id = c.procserv_id);
UPDATE PBL_CONSULTA c SET c.procediment_id = (SELECT pps.procediment_id FROM pbl_procediment_servei pps WHERE pps.id = c.procserv_id);

UPDATE PBL_CONSULTA_HIST c SET c.entitat_id = (SELECT ppr.entitat_id FROM pbl_procediment_servei pps, pbl_procediment ppr WHERE pps.id = c.procserv_id AND ppr.id = pps.procediment_id);
UPDATE PBL_CONSULTA_HIST c SET c.servei_codi = (SELECT pps.servei_id FROM pbl_procediment_servei pps WHERE pps.id = c.procserv_id);
UPDATE PBL_CONSULTA_HIST c SET c.procediment_id = (SELECT pps.procediment_id FROM pbl_procediment_servei pps WHERE pps.id = c.procserv_id);

UPDATE PBL_CONFIG SET key = 'es.caib.pinbal.plugin.arxiu.caib.csv_generation_definition' where key = 'es.caib.pinbal.plugin.arxiu.caib.csv.definicio';
