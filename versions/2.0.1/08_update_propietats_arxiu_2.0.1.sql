-- 3. Renombrar les claus de les propietats del plugin d'arxiu
UPDATE pbl_config c
   SET c.key = REPLACE(c.key, 'es.caib.pinbal.plugin.arxiu.caib.', 'es.caib.pinbal.pluginsib.arxiu.caib.')
 WHERE c.key LIKE 'es.caib.pinbal.plugin.arxiu.caib.%'
   AND NOT EXISTS (
       SELECT 1
         FROM pbl_config n
        WHERE n.key = REPLACE(c.key, 'es.caib.pinbal.plugin.arxiu.caib.', 'es.caib.pinbal.pluginsib.arxiu.caib.'));

-- 4. Eliminar les propietats concsv.* si s'havien afegit en una versió anterior d'aquest script:
--    el servei ConCSV ara es configura amb les propietats conversio.imprimible.*.
DELETE FROM pbl_config WHERE key LIKE 'es.caib.pinbal.%arxiu.caib.concsv.%';

COMMIT;
