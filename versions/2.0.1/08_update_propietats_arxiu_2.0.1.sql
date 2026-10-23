-- 3. Renombrar les claus de les propietats del plugin d'arxiu
UPDATE pbl_config c
   SET c.key = REPLACE(c.key, 'es.caib.pinbal.plugin.arxiu.caib.', 'es.caib.pinbal.pluginsib.arxiu.caib.')
 WHERE c.key LIKE 'es.caib.pinbal.plugin.arxiu.caib.%'
   AND NOT EXISTS (
       SELECT 1
         FROM pbl_config n
        WHERE n.key = REPLACE(c.key, 'es.caib.pinbal.plugin.arxiu.caib.', 'es.caib.pinbal.pluginsib.arxiu.caib.'));

-- 4. Afegir les propietats del servei ConCSV (metadades i versió original dels documents).
--    Són opcionals: si no es configura la URL, la informació dels documents s'obté directament de l'arxiu.
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code)
SELECT 'es.caib.pinbal.pluginsib.arxiu.caib.concsv.base.url', '', 'propietat.plugin.arxiu.caib.concsv.base.url', 'ARXIU', 12, 'FILE', 'TEXT' FROM dual
 WHERE NOT EXISTS (SELECT 1 FROM pbl_config WHERE key = 'es.caib.pinbal.pluginsib.arxiu.caib.concsv.base.url');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code)
SELECT 'es.caib.pinbal.pluginsib.arxiu.caib.concsv.usuari', '', 'propietat.plugin.arxiu.caib.concsv.usuari', 'ARXIU', 13, 'FILE', 'TEXT' FROM dual
 WHERE NOT EXISTS (SELECT 1 FROM pbl_config WHERE key = 'es.caib.pinbal.pluginsib.arxiu.caib.concsv.usuari');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code)
SELECT 'es.caib.pinbal.pluginsib.arxiu.caib.concsv.contrasenya', '', 'propietat.plugin.arxiu.caib.concsv.contrasenya', 'ARXIU', 14, 'FILE', 'PASS' FROM dual
 WHERE NOT EXISTS (SELECT 1 FROM pbl_config WHERE key = 'es.caib.pinbal.pluginsib.arxiu.caib.concsv.contrasenya');

COMMIT;
