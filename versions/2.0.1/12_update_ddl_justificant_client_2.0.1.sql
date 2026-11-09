-- ----------------------------------------------------------------------------
-- Configuració a nivell de client (usuari d'aplicació) de la gestió del
-- justificant de les consultes de recobriment.
--   0 (per defecte): PINBAL genera automàticament el justificant i el desa a
--                    l'arxiu (comportament dels clients ja integrats).
--   1              : PINBAL genera i firma el justificant sota demanda i
--                    l'aplicació client s'encarrega de desar-lo al seu
--                    expedient.
-- ----------------------------------------------------------------------------
ALTER TABLE pbl_usuari ADD app_guarda_just_arx NUMBER(1,0) DEFAULT 0 NOT NULL;

-- Exemple: configurar un client perquè gestioni ell mateix el justificant
-- UPDATE pbl_usuari SET app_guarda_just_arx = 1 WHERE codi = '$codi_usuari_aplicacio';
