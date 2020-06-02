/* 4.6.0 a 4.7.0 */
UPDATE CORE_PARAMETRO_CONFIGURACION SET VALOR='4.6.0' WHERE NOMBRE='version.datamodel.scsp';   

/* 4.7.0 a 4.8.0 */
UPDATE CORE_PARAMETRO_CONFIGURACION SET VALOR='4.8.0' WHERE NOMBRE='version.datamodel.scsp';   
ALTER TABLE CORE_EMISOR_CERTIFICADO ADD FECHAALTA TIMESTAMP (6);
COMMIT;
UPDATE CORE_EMISOR_CERTIFICADO SET FECHAALTA = SYSDATE  WHERE FECHAALTA IS NULL;

/* 4.8.0 a 4.10.0 */
UPDATE CORE_REQ_CESIONARIOS_SERVICIOS SET BLOQUEADO=1 WHERE FECHABAJA IS NOT NULL;

/* 4.10.0 a 4.13.0 */
update core_servicio set descripcion = 'Servicio de Consulta de ser beneficiario de víctima del terrorismo manual' where codcertificado = 'SVDIVTMWS01';
update core_servicio set descripcion = 'Servicio de Consulta de ser beneficiario de víctima del terrorismo' where codcertificado = 'SVDIVTWS01';

/* 4.13.0 a 4.16.0 */
ALTER TABLE CORE_TRANSMISION MODIFY ESTADO VARCHAR2(10);
UPDATE CORE_PARAMETRO_CONFIGURACION SET VALOR='4.16.0' where NOMBRE='version.datamodel.scsp'; 
COMMIT;

/* 4.16.0 a 4.19.0 */
UPDATE CORE_SERVICIO SET DESCRIPCION='Servicio de Consulta de Renta Salario Prestación Social Básica (Importes Anuales)' where CODCERTIFICADO='SVDSRSPSBANUALWS01'; 
UPDATE CORE_SERVICIO SET DESCRIPCION='Servicio de Consulta de Renta Salario Prestación Social Básica (Importes Actuales)' where CODCERTIFICADO='SVDSRSPSBACTUALWS01';  
UPDATE CORE_SERVICIO SET DESCRIPCION='Consulta de datos de solvencia para concursos públicos' where CODCERTIFICADO='DGSFP0005'; 
UPDATE CORE_SERVICIO SET DESCRIPCION='Estar al corriente de obligaciones tributarias para contratación con la CCAA' where CODCERTIFICADO='SVDCCAACPCWS01';  
UPDATE CORE_SERVICIO SET DESCRIPCION='Estar al corriente de obligaciones tributarias para solicitud de subvenciones y ayudas de la CCAA' where CODCERTIFICADO='SVDCCAACPASWS01';  
CREATE INDEX CORE_SERVICIO_INDEX_EMISOR  ON CORE_SERVICIO (EMISOR);
CREATE INDEX CESIONARIOS_INDEX_ORGANISMO  ON CORE_REQ_CESIONARIOS_SERVICIOS (ORGANISMO);
