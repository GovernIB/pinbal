-- #301
ALTER TABLE pbl_servei_justif_camp ADD document NUMBER(1) DEFAULT '0' NOT NULL;

-- #310
CREATE TABLE pbl_explot_temps (id NUMBER(38, 0) NOT NULL, data date, anualitat INTEGER, mes INTEGER, trimestre INTEGER, setmana INTEGER, dia VARCHAR2(3 CHAR), CONSTRAINT PK_PBL_EXPLOT_TEMPS PRIMARY KEY (id));

CREATE TABLE pbl_explot_consulta_dim (id NUMBER(38, 0) NOT NULL, entitat_id NUMBER(38, 0), procediment_id NUMBER(38, 0), servei_codi VARCHAR2(64 CHAR), usuari_codi VARCHAR2(255 CHAR), CONSTRAINT PK_PBL_EXPLOT_CONSULTA_DIM PRIMARY KEY (id));
ALTER TABLE pbl_explot_consulta_dim ADD CONSTRAINT pbl_explot_consulta_dim_uk UNIQUE (entitat_id, procediment_id, servei_codi, usuari_codi);

CREATE TABLE pbl_explot_consulta_fet (id NUMBER(38, 0) NOT NULL, num_rec_ok NUMBER(38, 0), num_rec_error NUMBER(38, 0), num_rec_pend NUMBER(38, 0), num_rec_proc NUMBER(38, 0), num_rec_mass_ok NUMBER(38, 0), num_rec_mass_error NUMBER(38, 0), num_rec_mass_pend NUMBER(38, 0), num_rec_mass_proc NUMBER(38, 0), num_web_ok NUMBER(38, 0), num_web_error NUMBER(38, 0), num_web_pend NUMBER(38, 0), num_web_proc NUMBER(38, 0), num_web_mass_ok NUMBER(38, 0), num_web_mass_error NUMBER(38, 0), num_web_mass_pend NUMBER(38, 0), num_web_mass_proc NUMBER(38, 0), dimensio_id NUMBER(38, 0) NOT NULL, temps_id NUMBER(38, 0) NOT NULL, CONSTRAINT PK_PBL_EXPLOT_CONSULTA_FET PRIMARY KEY (id), CONSTRAINT pbl_fet_tmp_fk FOREIGN KEY (temps_id) REFERENCES pbl_explot_temps(id), CONSTRAINT pbl_fet_dim_fk FOREIGN KEY (dimensio_id) REFERENCES pbl_explot_consulta_dim(id));

CREATE INDEX pbl_consulta_est_i ON pbl_consulta(entitat_id, procediment_id, servei_codi, createdby_codi);
CREATE INDEX pbl_consultah_est_i ON pbl_consulta_hist(entitat_id, procediment_id, servei_codi, createdby_codi);

CREATE TABLE pbl_consulta_do (id NUMBER(38, 0) NOT NULL, entitatCodi VARCHAR2(64 CHAR) NOT NULL, entitatNom VARCHAR2(255 CHAR) NOT NULL, entitatNif VARCHAR2(255 CHAR) NOT NULL, entitatTipus VARCHAR2(16 CHAR) NOT NULL, departamentCodi VARCHAR2(250 CHAR), departamentNom VARCHAR2(250 CHAR), procedimentCodi VARCHAR2(20 CHAR) NOT NULL, procedimentNom VARCHAR2(255 CHAR) NOT NULL, serveiCodi VARCHAR2(64 CHAR) NOT NULL, serveiNom VARCHAR2(512 CHAR) NOT NULL, emissorNom VARCHAR2(50 CHAR) NOT NULL, emissorNif VARCHAR2(16 CHAR) NOT NULL, consentiment VARCHAR2(16 CHAR), finalitat VARCHAR2(250 CHAR), titularDoctip VARCHAR2(16 CHAR), solicitudId VARCHAR2(64 CHAR) NOT NULL, data TIMESTAMP NOT NULL, tipus VARCHAR2(16 CHAR) NOT NULL, resultat VARCHAR2(16 CHAR), multiple NUMBER(1), CONSTRAINT PK_PBL_CONSULTA_DO PRIMARY KEY (id));
CREATE TABLE pbl_consulta_hist_do (id NUMBER(38, 0) NOT NULL, entitatCodi VARCHAR2(64 CHAR), entitatNom VARCHAR2(255 CHAR), entitatNif VARCHAR2(255 CHAR), entitatTipus VARCHAR2(16 CHAR), departamentCodi VARCHAR2(250 CHAR), departamentNom VARCHAR2(250 CHAR), procedimentCodi VARCHAR2(20 CHAR), procedimentNom VARCHAR2(255 CHAR), serveiCodi VARCHAR2(64 CHAR), serveiNom VARCHAR2(512 CHAR), emissorNom VARCHAR2(50 CHAR), emissorNif VARCHAR2(16 CHAR), consentiment VARCHAR2(16 CHAR), finalitat VARCHAR2(250 CHAR), titularDoctip VARCHAR2(16 CHAR), solicitudId VARCHAR2(64 CHAR), data TIMESTAMP, tipus VARCHAR2(16 CHAR), resultat VARCHAR2(16 CHAR), multiple NUMBER(1), CONSTRAINT PK_PBL_CONSULTA_HIST_DO PRIMARY KEY (id));
CREATE TABLE pbl_consulta_list (id NUMBER(38, 0) NOT NULL, peticioId VARCHAR2(26 CHAR) NOT NULL, solicitudId VARCHAR2(64 CHAR), data TIMESTAMP NOT NULL, departament VARCHAR2(250 CHAR) NOT NULL, recobriment NUMBER(1), multiple NUMBER(1), usuariCodi VARCHAR2(64 CHAR) NOT NULL, usuariNom VARCHAR2(200 CHAR), funcionariNif VARCHAR2(16 CHAR), funcionariNom VARCHAR2(128 CHAR), titularNom VARCHAR2(122 CHAR), titularDoctip VARCHAR2(16 CHAR), titularDocnum VARCHAR2(14 CHAR), procedimentId NUMBER(38, 0) NOT NULL, procedimentCodi VARCHAR2(20 CHAR) NOT NULL, procedimentNom VARCHAR2(255 CHAR) NOT NULL, serveiCodi VARCHAR2(64 CHAR) NOT NULL, serveiNom VARCHAR2(512 CHAR) NOT NULL, estat VARCHAR2(16 CHAR) NOT NULL, error VARCHAR2(4000 CHAR), justificantEstat VARCHAR2(16 CHAR), entitatId NUMBER(38, 0) NOT NULL, entitatCodi VARCHAR2(64 CHAR) NOT NULL, pareId NUMBER(38, 0), CONSTRAINT PK_PBL_CONSULTA_LIST PRIMARY KEY (id));
CREATE TABLE pbl_consulta_hist_list (id NUMBER(38, 0) NOT NULL, peticioId VARCHAR2(26 CHAR) NOT NULL, solicitudId VARCHAR2(64 CHAR), data TIMESTAMP NOT NULL, departament VARCHAR2(250 CHAR), recobriment NUMBER(1), multiple NUMBER(1), usuariCodi VARCHAR2(64 CHAR), usuariNom VARCHAR2(200 CHAR), funcionariNif VARCHAR2(16 CHAR), funcionariNom VARCHAR2(128 CHAR), titularNom VARCHAR2(122 CHAR), titularDoctip VARCHAR2(16 CHAR), titularDocnum VARCHAR2(14 CHAR), procedimentId NUMBER(38, 0), procedimentCodi VARCHAR2(20 CHAR), procedimentNom VARCHAR2(255 CHAR), serveiCodi VARCHAR2(64 CHAR), serveiNom VARCHAR2(512 CHAR), estat VARCHAR2(16 CHAR), error VARCHAR2(4000 CHAR), justificantEstat VARCHAR2(16 CHAR), entitatId NUMBER(38, 0), entitatCodi VARCHAR2(64 CHAR), pareId NUMBER(38, 0), CONSTRAINT PK_PBL_CONSULTA_HIST_LIST PRIMARY KEY (id));

GRANT SELECT, UPDATE, INSERT, DELETE ON pbl_explot_temps TO www_pinbal;
GRANT SELECT, UPDATE, INSERT, DELETE ON pbl_explot_consulta_dim TO www_pinbal;
GRANT SELECT, UPDATE, INSERT, DELETE ON pbl_explot_consulta_fet TO www_pinbal;
GRANT SELECT, UPDATE, INSERT, DELETE ON pbl_consulta_do TO www_pinbal;
GRANT SELECT, UPDATE, INSERT, DELETE ON pbl_consulta_hist_do TO www_pinbal;
GRANT SELECT, UPDATE, INSERT, DELETE ON pbl_consulta_list TO www_pinbal;
GRANT SELECT, UPDATE, INSERT, DELETE ON pbl_consulta_hist_list TO www_pinbal;