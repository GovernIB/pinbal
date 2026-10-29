-- ============================================================================
-- PINBAL 2.0.1 -- Format de les parts a xifrar de les peticions SCSP (Oracle)
-- ============================================================================
--
-- Amb SCSP 5.0.11 (WSS4J 2.4), el valor de CORE_SERVICIO.XPATHCIFRADOSINCRONO /
-- XPATHCIFRADOASINCRONO ja no s'avalua com una expressio XPath: es passa a
-- WSS4J com a definicio de parts a xifrar. Amb el format antic
--
--   //*[local-name()='DatosEspecificos']
--
-- WSS4J no troba l'element i la peticio falla amb "Error al generar la firma
-- del mensaje" (al log: "Error during encryption" / "noEncElement").
-- El format correcte es el de parts de WSS4J:
--
--   {Content}{<namespace>}DatosEspecificos
--
-- Aquest script nomes converteix els serveis amb esquema V3 i el XPath estandard
-- de DatosEspecificos, que fan servir el namespace
-- http://intermediacion.redsara.es/scsp/esquemas/datosespecificos.
-- La resta de valors els llista el pas 3 per revisar-los a ma.
--
-- Cal desplegar tambe la versio de l'EAR que exclou el modul
-- org.apache.ws.security del JBoss (jboss-deployment-structure.xml).
-- Marxa enrere: 09_rollback_xifrat_scsp_2.0.1.sql
-- ============================================================================

-- 1. Situacio actual: serveis amb xifratge configurat
SELECT codcertificado, versionesquema, clavecifrado, xpathcifradosincrono, xpathcifradoasincrono
  FROM core_servicio
 WHERE xpathcifradosincrono IS NOT NULL
    OR xpathcifradoasincrono IS NOT NULL
 ORDER BY codcertificado;

-- 2. Conversio al format de parts de WSS4J
UPDATE core_servicio
   SET xpathcifradosincrono = '{Content}{http://intermediacion.redsara.es/scsp/esquemas/datosespecificos}DatosEspecificos'
 WHERE xpathcifradosincrono = '//*[local-name()=''DatosEspecificos'']'
   AND versionesquema = 'V3';

UPDATE core_servicio
   SET xpathcifradoasincrono = '{Content}{http://intermediacion.redsara.es/scsp/esquemas/datosespecificos}DatosEspecificos'
 WHERE xpathcifradoasincrono = '//*[local-name()=''DatosEspecificos'']'
   AND versionesquema = 'V3';

-- 3. Valors que no s'han convertit i s'han de revisar a ma (han de quedar 0 files)
SELECT codcertificado, versionesquema, xpathcifradosincrono, xpathcifradoasincrono
  FROM core_servicio
 WHERE (xpathcifradosincrono IS NOT NULL AND xpathcifradosincrono NOT LIKE '{%')
    OR (xpathcifradoasincrono IS NOT NULL AND xpathcifradoasincrono NOT LIKE '{%')
 ORDER BY codcertificado;

COMMIT;
