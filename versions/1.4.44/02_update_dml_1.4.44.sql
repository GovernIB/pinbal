-- Changeset db/changelog/changes/1.4.43/337.yaml::337-01::limit
UPDATE pbl_explot_temps SET dia = EXTRACT(DAY FROM data);

-- Changeset db/changelog/changes/1.4.43/342.yaml::342-02::limit
UPDATE pbl_explot_consulta_dim SET entitat_codi = (SELECT e.codi FROM pbl_entitat e WHERE e.id = entitat_id), procediment_codi = (SELECT p.codi FROM pbl_procediment p WHERE p.id = procediment_id);

-- Changeset db/changelog/changes/1.4.44/355.yaml::355-01::limit
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code)
VALUES ('es.caib.pinbal.recobriment.requereix.expedientid', 'false', 'propietat.recobriment.requereix.expedientid', 'RECOBRIMENT', '3', 'DATABASE', 'BOOL');
