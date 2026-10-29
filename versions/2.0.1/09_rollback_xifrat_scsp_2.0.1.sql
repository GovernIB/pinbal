-- ============================================================================
-- PINBAL 2.0.1 -- Marxa enrere de 09_update_xifrat_scsp_2.0.1.sql (Oracle)
-- ============================================================================
--
-- Torna a deixar les parts a xifrar amb el format XPath antic. Cal executar-lo
-- si es torna a desplegar una versio 1.4.x (SCSP 4).
-- ============================================================================

UPDATE core_servicio
   SET xpathcifradosincrono = '//*[local-name()=''DatosEspecificos'']'
 WHERE xpathcifradosincrono = '{Content}{http://intermediacion.redsara.es/scsp/esquemas/datosespecificos}DatosEspecificos';

UPDATE core_servicio
   SET xpathcifradoasincrono = '//*[local-name()=''DatosEspecificos'']'
 WHERE xpathcifradoasincrono = '{Content}{http://intermediacion.redsara.es/scsp/esquemas/datosespecificos}DatosEspecificos';

SELECT codcertificado, xpathcifradosincrono, xpathcifradoasincrono
  FROM core_servicio
 WHERE xpathcifradosincrono IS NOT NULL
    OR xpathcifradoasincrono IS NOT NULL
 ORDER BY codcertificado;

COMMIT;
