-- Changeset db/changelog/changes/1.4.34/231.yaml::1660889406593-1::limit
UPDATE pbl_organ_gestor SET estat = 'V' WHERE actiu=1;
UPDATE pbl_organ_gestor SET estat = 'E' WHERE actiu=0;