-- =====================================================================================
-- PINBAL 2.0.1 - Desactivar "Incloure sempre el node de dades específiques"
-- (PBL_SERVEI_CONFIG.ADD_DADES_ESPECIFIQUES) a tots els serveis.
--
-- Motiu: la majoria d'emissors rebutgen el node <DatosEspecificos> quan va buit.
-- Amb el check desactivat, el node només s'envia si la petició porta dades específiques.
--
-- Execució: amb l'usuari propietari de l'esquema de PINBAL (Oracle).
-- Rollback:  13_rollback_add_dades_especifiques_2.0.1.sql
-- =====================================================================================

-- -------------------------------------------------------------------------------------
-- 1) Diagnosi prèvia: quants serveis tenen el check actiu
-- -------------------------------------------------------------------------------------
SELECT SERVEI_ID, NVL(ADD_DADES_ESPECIFIQUES, -1) AS ADD_DADES_ESPECIFIQUES
FROM PBL_SERVEI_CONFIG;

-- -------------------------------------------------------------------------------------
-- 2) Desactivar el check a tots els serveis
-- -------------------------------------------------------------------------------------
UPDATE PBL_SERVEI_CONFIG
SET ADD_DADES_ESPECIFIQUES = 0
WHERE NVL(ADD_DADES_ESPECIFIQUES, 1) <> 0;
