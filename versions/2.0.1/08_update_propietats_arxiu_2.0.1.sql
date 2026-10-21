-- ============================================================================
-- PINBAL 2.0.1 -- Actualitzacio de les propietats del plugin d'arxiu (Oracle)
-- ============================================================================
--
-- La llibreria pluginsib-arxiu-caib 3.x llegeix les propietats amb el prefix
-- "pluginsib.arxiu.caib." en lloc de "plugin.arxiu.caib." (2.x). Aquest script
-- renombra les claus de PBL_CONFIG:
--
--   es.caib.pinbal.plugin.arxiu.caib.X  ->  es.caib.pinbal.pluginsib.arxiu.caib.X
--
-- Es mantenen el valor, la descripcio, el grup, la posicio, l'origen (FILE /
-- DATABASE) i el tipus. NO es toquen les propietats propies de PINBAL que no
-- llegeix la llibreria:
--   es.caib.pinbal.plugin.arxiu.class
--   es.caib.pinbal.plugin.arxiu.serie.documental
--
-- IMPORTANT: les propietats amb SOURCE_PROPERTY = 'FILE' agafen el valor del
-- fitxer de propietats del JBoss, no de la BBDD. Despres d'executar aquest
-- script, cal renombrar-les tambe al fitxer de propietats:
--   es.caib.pinbal.pluginsib.arxiu.caib.base.url
--   es.caib.pinbal.pluginsib.arxiu.caib.usuari
--   es.caib.pinbal.pluginsib.arxiu.caib.contrasenya
--   es.caib.pinbal.pluginsib.arxiu.caib.conversio.imprimible.url
--   es.caib.pinbal.pluginsib.arxiu.caib.conversio.imprimible.usuari
--   es.caib.pinbal.pluginsib.arxiu.caib.conversio.imprimible.contrasenya
-- (la consulta 1 d'aquest script en dona la llista exacta de l'entorn).
-- Mentre el fitxer conservi els noms antics el plugin continuara funcionant
-- (PINBAL 2.0.1 copia les claus antigues al prefix nou), pero la pantalla de
-- propietats de l'administrador les mostrara buides.
--
-- L'script es pot reexecutar: si la clau nova ja existeix, no es modifica.
-- Marxa enrere: 08_rollback_propietats_arxiu_2.0.1.sql
-- ============================================================================

-- 1. Diagnosi previa: propietats que es renombraran
-- SELECT key, source_property, type_code, value
--   FROM pbl_config
--  WHERE key LIKE 'es.caib.pinbal.plugin.arxiu.caib.%'
--  ORDER BY position;

-- 2. Diagnosi previa: propietats amb el prefix nou que ja existeixen (no es sobreescriuen)
-- SELECT key, source_property, value
--   FROM pbl_config
--  WHERE key LIKE 'es.caib.pinbal.pluginsib.arxiu.caib.%';

-- 3. Renombrar les claus
UPDATE pbl_config c
   SET c.key = REPLACE(c.key, 'es.caib.pinbal.plugin.arxiu.caib.', 'es.caib.pinbal.pluginsib.arxiu.caib.')
 WHERE c.key LIKE 'es.caib.pinbal.plugin.arxiu.caib.%'
   AND NOT EXISTS (
       SELECT 1
         FROM pbl_config n
        WHERE n.key = REPLACE(c.key, 'es.caib.pinbal.plugin.arxiu.caib.', 'es.caib.pinbal.pluginsib.arxiu.caib.'));

-- 4. Comprovacio: no ha de quedar cap clau amb el prefix antic
--    (si en queda alguna, ja existia la clau nova: revisar-la i esborrar l'antiga manualment)
-- SELECT key, value
--   FROM pbl_config
--  WHERE key LIKE 'es.caib.pinbal.plugin.arxiu.caib.%';

-- SELECT key, source_property, type_code, value
--   FROM pbl_config
--  WHERE group_code = 'ARXIU'
--  ORDER BY position;

COMMIT;
