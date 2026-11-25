-- =====================================================================================
-- PINBAL 2.0.1 - Configuració per servei del format de namespaces de les peticions SCSP
--
-- PBL_SERVEI_CONFIG.FORMAT_NS_SCSP4 = 1: les peticions (Peticion i SolicitudRespuesta) del
-- servei es generen amb el format de namespaces de SCSP 4 (DatosEspecificos amb xmlns per
-- defecte i sense prefix, sense declaracions ns2..ns5 a l'arrel). Necessari per a emissors
-- que no tracten bé els namespaces (p.ex. SCDHPAJU de Manacor a través d'EMISERV).
-- Per defecte (0) les peticions es generen igual que fins ara.
--
-- S'ha d'executar ABANS de desplegar la versió que llegeix aquesta columna.
-- Rollback:  ALTER TABLE PBL_SERVEI_CONFIG DROP COLUMN FORMAT_NS_SCSP4;
-- =====================================================================================

ALTER TABLE PBL_SERVEI_CONFIG ADD FORMAT_NS_SCSP4 NUMBER(1,0) DEFAULT 0 NOT NULL;

-- Activació per als serveis afectats (descomentar i adaptar si es vol activar per script
-- en lloc de fer-ho des de la pantalla de configuració del servei):
-- UPDATE PBL_SERVEI_CONFIG SET FORMAT_NS_SCSP4 = 1, LASTMODIFIEDDATE = SYSDATE
--  WHERE SERVEI_ID IN ('SCDHPAJU', 'SCDCPAJU');
-- COMMIT;
