-- Changeset db/changelog/changes/1.4.34/231.yaml::1660889406593-1::limit
ALTER TABLE pbl_organ_gestor ADD estat VARCHAR2(10 CHAR);

-- #236 Incloure elements obligatoris en les dades específiques 
ALTER TABLE pbl_servei_config ADD ini_dades_especifiques NUMBER(1) DEFAULT 0;