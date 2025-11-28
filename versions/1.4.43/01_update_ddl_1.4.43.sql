-- Changeset db/changelog/changes/1.4.43/337.yaml::337-01::limit
ALTER TABLE pbl_servei_config ADD arrel_resposta_path VARCHAR2(255 CHAR);

-- Changeset db/changelog/changes/1.4.43/342.yaml::342-01::limit
ALTER TABLE pbl_explot_temps RENAME COLUMN dia TO dia_setmana;
ALTER TABLE pbl_explot_temps ADD dia INTEGER;

-- Changeset db/changelog/changes/1.4.43/342.yaml::342-02::limit
ALTER TABLE pbl_explot_consulta_dim ADD entitat_codi VARCHAR2(64 CHAR);
ALTER TABLE pbl_explot_consulta_dim ADD procediment_codi VARCHAR2(20 CHAR);

-- Changeset db/changelog/changes/1.4.43/342.yaml::342-03::limit
CREATE INDEX pbl_cons_procediment_i ON pbl_consulta(procediment_id);
CREATE INDEX pbl_cons_serveicodi_i ON pbl_consulta(servei_codi);
CREATE INDEX pbl_cons_explotorder_i ON pbl_consulta(entitat_id, procediment_id, servei_codi, createdby_codi);

-- Changeset db/changelog/changes/1.4.43/352.yaml::352-01::limit
ALTER TABLE PBL_SERVEI_CONFIG ADD use_cert_entitat NUMBER(1) DEFAULT 0;
ALTER TABLE CORE_CLAVE_PRIVADA ADD per_entitat NUMBER(1) DEFAULT 0;

