-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 18/09/24 09:30
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.3.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.4.41/301.yaml::1719641276940::limit
ALTER TABLE pbl_servei_justif_camp ADD document NUMBER(1) DEFAULT '0' NOT NULL;

-- Changeset db/changelog/changes/1.4.41/310.yaml::1719641276939::limit
CREATE TABLE pbl_explot_temps (id NUMBER(38, 0) NOT NULL, data date, anualitat INTEGER, mes INTEGER, trimestre INTEGER, setmana INTEGER, dia VARCHAR2(3 CHAR), CONSTRAINT PK_PBL_EXPLOT_TEMPS PRIMARY KEY (id));

CREATE TABLE pbl_explot_consulta_dim (id NUMBER(38, 0) NOT NULL, entitat_id NUMBER(38, 0), procediment_id NUMBER(38, 0), servei_codi VARCHAR2(64 CHAR), usuari_codi VARCHAR2(255 CHAR), CONSTRAINT PK_PBL_EXPLOT_CONSULTA_DIM PRIMARY KEY (id));
ALTER TABLE pbl_explot_consulta_dim ADD CONSTRAINT pbl_explot_consulta_dim_uk UNIQUE (entitat_id, procediment_id, servei_codi, usuari_codi);

CREATE TABLE pbl_explot_consulta_fet (id NUMBER(38, 0) NOT NULL, num_rec_ok NUMBER(38, 0), num_rec_error NUMBER(38, 0), num_rec_pend NUMBER(38, 0), num_rec_proc NUMBER(38, 0), num_rec_mass_ok NUMBER(38, 0), num_rec_mass_error NUMBER(38, 0), num_rec_mass_pend NUMBER(38, 0), num_rec_mass_proc NUMBER(38, 0), num_web_ok NUMBER(38, 0), num_web_error NUMBER(38, 0), num_web_pend NUMBER(38, 0), num_web_proc NUMBER(38, 0), num_web_mass_ok NUMBER(38, 0), num_web_mass_error NUMBER(38, 0), num_web_mass_pend NUMBER(38, 0), num_web_mass_proc NUMBER(38, 0), dimensio_id NUMBER(38, 0) NOT NULL, temps_id NUMBER(38, 0) NOT NULL, CONSTRAINT PK_PBL_EXPLOT_CONSULTA_FET PRIMARY KEY (id), CONSTRAINT pbl_fet_tmp_fk FOREIGN KEY (temps_id) REFERENCES pbl_explot_temps(id), CONSTRAINT pbl_fet_dim_fk FOREIGN KEY (dimensio_id) REFERENCES pbl_explot_consulta_dim(id));

INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.tasca.auto.generar.explotacio.cron', '0 30 2 * * ?', 'propietat.tasca.auto.generar.explotacio.cron', 'TASQUES', '4', 'DATABASE', 'TEXT');

CREATE INDEX pbl_consulta_est_i ON pbl_consulta(entitat_id, procediment_id, servei_codi, createdby_codi);
CREATE INDEX pbl_consultah_est_i ON pbl_consulta_hist(entitat_id, procediment_id, servei_codi, createdby_codi);

CREATE TABLE pbl_consulta_do (id NUMBER(38, 0) NOT NULL, entitatCodi VARCHAR2(64 CHAR) NOT NULL, entitatNom VARCHAR2(255 CHAR) NOT NULL, entitatNif VARCHAR2(255 CHAR) NOT NULL, entitatTipus VARCHAR2(16 CHAR) NOT NULL, departamentCodi VARCHAR2(250 CHAR), departamentNom VARCHAR2(250 CHAR), procedimentCodi VARCHAR2(20 CHAR) NOT NULL, procedimentNom VARCHAR2(255 CHAR) NOT NULL, serveiCodi VARCHAR2(64 CHAR) NOT NULL, serveiNom VARCHAR2(512 CHAR) NOT NULL, emissorNom VARCHAR2(50 CHAR) NOT NULL, emissorNif VARCHAR2(16 CHAR) NOT NULL, consentiment VARCHAR2(16 CHAR), finalitat VARCHAR2(250 CHAR), titularDoctip VARCHAR2(16 CHAR), solicitudId VARCHAR2(64 CHAR) NOT NULL, data TIMESTAMP NOT NULL, tipus VARCHAR2(16 CHAR) NOT NULL, resultat VARCHAR2(16 CHAR), multiple NUMBER(1), CONSTRAINT PK_PBL_CONSULTA_DO PRIMARY KEY (id));
CREATE TABLE pbl_consulta_hist_do (id NUMBER(38, 0) NOT NULL, entitatCodi VARCHAR2(64 CHAR), entitatNom VARCHAR2(255 CHAR), entitatNif VARCHAR2(255 CHAR), entitatTipus VARCHAR2(16 CHAR), departamentCodi VARCHAR2(250 CHAR), departamentNom VARCHAR2(250 CHAR), procedimentCodi VARCHAR2(20 CHAR), procedimentNom VARCHAR2(255 CHAR), serveiCodi VARCHAR2(64 CHAR), serveiNom VARCHAR2(512 CHAR), emissorNom VARCHAR2(50 CHAR), emissorNif VARCHAR2(16 CHAR), consentiment VARCHAR2(16 CHAR), finalitat VARCHAR2(250 CHAR), titularDoctip VARCHAR2(16 CHAR), solicitudId VARCHAR2(64 CHAR), data TIMESTAMP, tipus VARCHAR2(16 CHAR), resultat VARCHAR2(16 CHAR), multiple NUMBER(1), CONSTRAINT PK_PBL_CONSULTA_HIST_DO PRIMARY KEY (id));
CREATE TABLE pbl_consulta_list (id NUMBER(38, 0) NOT NULL, peticioId VARCHAR2(26 CHAR) NOT NULL, solicitudId VARCHAR2(64 CHAR), data TIMESTAMP NOT NULL, departament VARCHAR2(250 CHAR) NOT NULL, recobriment NUMBER(1), multiple NUMBER(1), usuariCodi VARCHAR2(64 CHAR) NOT NULL, usuariNom VARCHAR2(200 CHAR), funcionariNif VARCHAR2(16 CHAR), funcionariNom VARCHAR2(128 CHAR), titularNom VARCHAR2(122 CHAR), titularDoctip VARCHAR2(16 CHAR), titularDocnum VARCHAR2(14 CHAR), procedimentId NUMBER(38, 0) NOT NULL, procedimentCodi VARCHAR2(20 CHAR) NOT NULL, procedimentNom VARCHAR2(255 CHAR) NOT NULL, serveiCodi VARCHAR2(64 CHAR) NOT NULL, serveiNom VARCHAR2(512 CHAR) NOT NULL, estat VARCHAR2(16 CHAR) NOT NULL, error VARCHAR2(4000 CHAR), justificantEstat VARCHAR2(16 CHAR), entitatId NUMBER(38, 0) NOT NULL, entitatCodi VARCHAR2(64 CHAR) NOT NULL, pareId NUMBER(38, 0), CONSTRAINT PK_PBL_CONSULTA_LIST PRIMARY KEY (id));
CREATE TABLE pbl_consulta_hist_list (id NUMBER(38, 0) NOT NULL, peticioId VARCHAR2(26 CHAR) NOT NULL, solicitudId VARCHAR2(64 CHAR), data TIMESTAMP NOT NULL, departament VARCHAR2(250 CHAR), recobriment NUMBER(1), multiple NUMBER(1), usuariCodi VARCHAR2(64 CHAR), usuariNom VARCHAR2(200 CHAR), funcionariNif VARCHAR2(16 CHAR), funcionariNom VARCHAR2(128 CHAR), titularNom VARCHAR2(122 CHAR), titularDoctip VARCHAR2(16 CHAR), titularDocnum VARCHAR2(14 CHAR), procedimentId NUMBER(38, 0), procedimentCodi VARCHAR2(20 CHAR), procedimentNom VARCHAR2(255 CHAR), serveiCodi VARCHAR2(64 CHAR), serveiNom VARCHAR2(512 CHAR), estat VARCHAR2(16 CHAR), error VARCHAR2(4000 CHAR), justificantEstat VARCHAR2(16 CHAR), entitatId NUMBER(38, 0), entitatCodi VARCHAR2(64 CHAR), pareId NUMBER(38, 0), CONSTRAINT PK_PBL_CONSULTA_HIST_LIST PRIMARY KEY (id));

INSERT INTO pbl_consulta_do ( id, entitatCodi, entitatNom, entitatNif, entitatTipus, departamentCodi, departamentNom, procedimentCodi, procedimentNom, serveiCodi, serveiNom, emissorNom, emissorNif, consentiment, finalitat, titularDoctip, solicitudId, data, tipus, resultat, multiple) SELECT c.ID, e.CODI, e.NOM, e.CIF, CASE WHEN e.TIPUS = 0 THEN 'ALTRES' WHEN e.TIPUS = 1 THEN 'GOVERN' WHEN e.TIPUS = 2 THEN 'CONSELL' WHEN e.TIPUS = 3 THEN 'AJUNTAMENT' END, t.CODIGOUNIDADTRAMITADORA, c.DEPARTAMENT, p.CODI, p.NOM, c.SERVEI_CODI, s.DESCRIPCION, em.NOMBRE, em.CIF, CASE WHEN c.CONSENTIMENT = 0 THEN 'Si' WHEN c.CONSENTIMENT = 1 THEN 'Llei' ELSE NULL END, CASE WHEN INSTR(c.finalitat, '#') != 0 THEN SUBSTR(c.finalitat, INSTR(c.finalitat, '#') + 1) ELSE finalitat END, c.TITULAR_DOCTIP, c.SOLICITUD_ID, c.CREATEDDATE, CASE WHEN c.RECOBRIMENT = 1 THEN 'RECOBRIMENT' ELSE 'WEB' END, CASE WHEN c.estat IN (0, 1) THEN 'PROCES' WHEN c.estat = 2 THEN 'OK' WHEN c.estat = 3 THEN 'ERROR' ELSE NULL END, c.MULTIPLE FROM pbl_consulta c LEFT OUTER JOIN PBL_ENTITAT e on c.ENTITAT_ID = e.ID LEFT OUTER JOIN CORE_TRANSMISION t on c.PETICION_ID = t.IDPETICION AND c.SOLICITUD_ID = t.IDSOLICITUD LEFT OUTER JOIN PBL_PROCEDIMENT p on c.PROCEDIMENT_ID = p.ID LEFT OUTER JOIN CORE_SERVICIO s on c.SERVEI_CODI = s.CODCERTIFICADO LEFT OUTER JOIN CORE_EMISOR_CERTIFICADO em on s.EMISOR = em.ID;
INSERT INTO pbl_consulta_hist_do ( id, entitatCodi, entitatNom, entitatNif, entitatTipus, departamentCodi, departamentNom, procedimentCodi, procedimentNom, serveiCodi, serveiNom, emissorNom, emissorNif, consentiment, finalitat, titularDoctip, solicitudId, data, tipus, resultat, multiple) SELECT c.ID, e.CODI, e.NOM, e.CIF, CASE WHEN e.TIPUS = 0 THEN 'ALTRES' WHEN e.TIPUS = 1 THEN 'GOVERN' WHEN e.TIPUS = 2 THEN 'CONSELL' WHEN e.TIPUS = 3 THEN 'AJUNTAMENT' END, t.CODIGOUNIDADTRAMITADORA, c.DEPARTAMENT, p.CODI, p.NOM, c.SERVEI_CODI, s.DESCRIPCION, em.NOMBRE, em.CIF, CASE WHEN c.CONSENTIMENT = 0 THEN 'Si' WHEN c.CONSENTIMENT = 1 THEN 'Llei' ELSE NULL END, CASE WHEN INSTR(c.finalitat, '#') != 0 THEN SUBSTR(c.finalitat, INSTR(c.finalitat, '#') + 1) ELSE finalitat END, c.TITULAR_DOCTIP, c.SOLICITUD_ID, c.CREATEDDATE, CASE WHEN c.RECOBRIMENT = 1 THEN 'RECOBRIMENT' ELSE 'WEB' END, CASE WHEN c.estat IN (0, 1) THEN 'PROCES' WHEN c.estat = 2 THEN 'OK' WHEN c.estat = 3 THEN 'ERROR' ELSE NULL END, c.MULTIPLE FROM PBL_CONSULTA_HIST c LEFT OUTER JOIN PBL_ENTITAT e on c.ENTITAT_ID = e.ID LEFT OUTER JOIN CORE_TRANSMISION t on c.PETICION_ID = t.IDPETICION AND c.SOLICITUD_ID = t.IDSOLICITUD LEFT OUTER JOIN PBL_PROCEDIMENT p on c.PROCEDIMENT_ID = p.ID LEFT OUTER JOIN CORE_SERVICIO s on c.SERVEI_CODI = s.CODCERTIFICADO LEFT OUTER JOIN CORE_EMISOR_CERTIFICADO em on s.EMISOR = em.ID;
INSERT INTO pbl_consulta_list ( id, peticioId, solicitudId, data, departament, recobriment, multiple, usuariCodi, usuariNom, funcionariNom, funcionariNif, titularNom, titularDoctip, titularDocnum, procedimentId, procedimentCodi, procedimentNom, serveiCodi, serveiNom, estat, error, justificantEstat, entitatId, entitatCodi, pareId) SELECT c.ID, c.PETICION_ID, c.SOLICITUD_ID, c.CREATEDDATE, c.DEPARTAMENT, c.RECOBRIMENT, c.MULTIPLE, c.CREATEDBY_CODI, u.NOM, c.FUNCIONARI_NOM, c.FUNCIONARI_DOCNUM, COALESCE(c.TITULAR_NOMCOMPLET, NULLIF(CONCAT(COALESCE(CONCAT(c.TITULAR_NOM, ' '), ''), CONCAT(COALESCE(CONCAT(c.TITULAR_LLING1, ' '), ''), COALESCE(c.TITULAR_LLING2, ''))), '')), c.TITULAR_DOCTIP, c.TITULAR_DOCNUM, p.ID, p.CODI, p.NOM, c.SERVEI_CODI, s.DESCRIPCION, CASE WHEN c.ESTAT = '0' THEN 'Pendent' WHEN c.ESTAT = '1' THEN 'Processant' WHEN c.ESTAT = '2' THEN 'Tramitada' WHEN c.ESTAT = '3' THEN 'Error' WHEN c.ESTAT = '4' THEN 'Encua' END, c.ERROR, CASE WHEN c.JUSTIFICANT_ESTAT = '0' THEN 'PENDENT' WHEN c.JUSTIFICANT_ESTAT = '1' THEN 'OK' WHEN c.JUSTIFICANT_ESTAT = '2' THEN 'ERROR' WHEN c.JUSTIFICANT_ESTAT = '3' THEN 'NO_DISPONIBLE' WHEN c.JUSTIFICANT_ESTAT = '4' THEN 'OK_NO_CUSTODIA' END, e.ID, e.CODI, c.PARE_ID FROM pbl_consulta c LEFT OUTER JOIN PBL_ENTITAT e on c.ENTITAT_ID = e.ID LEFT OUTER JOIN PBL_PROCEDIMENT p on c.PROCEDIMENT_ID = p.ID LEFT OUTER JOIN CORE_SERVICIO s on c.SERVEI_CODI = s.CODCERTIFICADO LEFT OUTER JOIN PBL_USUARI u on c.CREATEDBY_CODI = u.CODI order by c.CREATEDDATE DESC;
INSERT INTO pbl_consulta_hist_list ( id, peticioId, solicitudId, data, departament, recobriment, multiple, usuariCodi, usuariNom, funcionariNom, funcionariNif, titularNom, titularDoctip, titularDocnum, procedimentId, procedimentCodi, procedimentNom, serveiCodi, serveiNom, estat, error, justificantEstat, entitatId, entitatCodi, pareId) SELECT c.ID, c.PETICION_ID, c.SOLICITUD_ID, c.CREATEDDATE, c.DEPARTAMENT, c.RECOBRIMENT, c.MULTIPLE, c.CREATEDBY_CODI, u.NOM, c.FUNCIONARI_NOM, c.FUNCIONARI_DOCNUM, COALESCE(c.TITULAR_NOMCOMPLET, NULLIF(CONCAT(COALESCE(CONCAT(c.TITULAR_NOM, ' '), ''), CONCAT(COALESCE(CONCAT(c.TITULAR_LLING1, ' '), ''), COALESCE(c.TITULAR_LLING2, ''))), '')), c.TITULAR_DOCTIP, c.TITULAR_DOCNUM, p.ID, p.CODI, p.NOM, c.SERVEI_CODI, s.DESCRIPCION, CASE WHEN c.ESTAT = '0' THEN 'Pendent' WHEN c.ESTAT = '1' THEN 'Processant' WHEN c.ESTAT = '2' THEN 'Tramitada' WHEN c.ESTAT = '3' THEN 'Error' WHEN c.ESTAT = '4' THEN 'Encua' END, c.ERROR, CASE WHEN c.JUSTIFICANT_ESTAT = '0' THEN 'PENDENT' WHEN c.JUSTIFICANT_ESTAT = '1' THEN 'OK' WHEN c.JUSTIFICANT_ESTAT = '2' THEN 'ERROR' WHEN c.JUSTIFICANT_ESTAT = '3' THEN 'NO_DISPONIBLE' WHEN c.JUSTIFICANT_ESTAT = '4' THEN 'OK_NO_CUSTODIA' END, e.ID, e.CODI, c.PARE_ID FROM pbl_consulta c LEFT OUTER JOIN PBL_ENTITAT e on c.ENTITAT_ID = e.ID LEFT OUTER JOIN PBL_PROCEDIMENT p on c.PROCEDIMENT_ID = p.ID LEFT OUTER JOIN CORE_SERVICIO s on c.SERVEI_CODI = s.CODCERTIFICADO LEFT OUTER JOIN PBL_USUARI u on c.CREATEDBY_CODI = u.CODI;

