-- Configuració a nivell de client (usuari d'aplicació) de la gestió del justificant de les consultes
ALTER TABLE pbl_usuari ADD app_guarda_just_arx NUMBER(1,0) DEFAULT 0 NOT NULL;

-- Per defecte, els usuaris amb rol aplicació gestionen ells mateixos el justificant.
UPDATE pbl_usuari SET app_guarda_just_arx = 1
 WHERE codi IN (SELECT eu.usuari_id FROM pbl_entitat_usuari eu WHERE eu.aplicacio = 1);
