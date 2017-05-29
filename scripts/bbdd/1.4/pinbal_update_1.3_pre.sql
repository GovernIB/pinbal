/* UpdateRequirente-3.5.0a3.5.1 */
INSERT INTO core_emisor_certificado (id,cif,nombre) VALUES (ID_EMISOR_SEQUENCE.NEXTVAL,'S2816015H','DGP');
INSERT INTO core_emisor_certificado (id,cif,nombre) VALUES (ID_EMISOR_SEQUENCE.NEXTVAL,'Q2826039F','INE'); 
update core_servicio set plantillaXSLT='/plantillaspdf/aeat/plantilla_ConsultaRenta_AEATIR01.xslt' where codcertificado = 'AEATIR01';
update core_servicio set emisor=(select id from core_emisor_certificado where cif='S2816015H' and rownum<=1) where  codcertificado='CDISFWS01';
update core_servicio set emisor=(select id from core_emisor_certificado where cif='S2816015H' and rownum<=1) where  codcertificado='VDISFWS01';
update core_servicio set emisor=(select id from core_emisor_certificado where cif='Q2826039F' and rownum<=1) where  codcertificado='VDRSFWS02';
update core_servicio set emisor=(select id from core_emisor_certificado where cif='Q2826039F' and rownum<=1) where  codcertificado='SVDREXTFECHAWS01';
update core_servicio set emisor=(select id from core_emisor_certificado where cif='Q2826039F' and rownum<=1) where  codcertificado='SVDRWS01';

/* UpdateRequirente-3.5.1a3.5.2 (PRE) */
UPDATE CORE_EMISOR_CERTIFICADO SET NOMBRE = 'SEPE' WHERE CIF='Q2819009H';
UPDATE CORE_SERVICIO SET ACTIONSINCRONA='peticionSincrona' WHERE CODCERTIFICADO LIKE 'ECOT10%' OR CODCERTIFICADO = 'NIVRENTI'; 
UPDATE CORE_SERVICIO SET URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/DGSeguros.ConsultaMediadores', URLASINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/DGSeguros.ConsultaMediadores' WHERE CODCERTIFICADO ='DGSFP0001';
UPDATE CORE_SERVICIO SET URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/DGSeguros.ConsultaAseguradoras', URLASINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/DGSeguros.ConsultaAseguradoras' WHERE CODCERTIFICADO ='DGSFP0002';
UPDATE CORE_SERVICIO SET URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/DGSeguros.ConsultaFondos', URLASINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/DGSeguros.ConsultaFondos' WHERE CODCERTIFICADO ='DGSFP0003';
UPDATE CORE_SERVICIO SET URLSINCRONA=null, URLASINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/DGSeguros.ConsultaSolvencia' WHERE CODCERTIFICADO ='DGSFP0005';
UPDATE CORE_SERVICIO SET URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/Notarios.ConsultaAdministradores'   WHERE CODCERTIFICADO ='SVDNOTSUBADMWS01';
UPDATE CORE_SERVICIO SET URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/Notarios.ConsultaSubsistencia'   WHERE CODCERTIFICADO ='SVDNOTSUBWS01';
UPDATE CORE_SERVICIO SET URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/Notarios.ConsultaCopiaSimple'   WHERE CODCERTIFICADO ='SVDNOTCOPSIMWS01';
UPDATE CORE_SERVICIO SET URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/Notarios.ConsultaNotarios'   WHERE CODCERTIFICADO ='SVDNOTLISTWS01';
ALTER TABLE  CORE_SERVICIO  ADD PLANTILLADETALLESERVICIO VARCHAR2(512)   NULL;

/* UpdateRequirente-3.5.2a3.5.3 (PRE) */
update CORE_SERVICIO set ACTIONSINCRONA='peticionSincrona' where CODCERTIFICADO='ECOT101I';
update CORE_SERVICIO set ACTIONSINCRONA='peticionSincrona' where CODCERTIFICADO='ECOT102I';
update CORE_SERVICIO set ACTIONSINCRONA='peticionSincrona' where CODCERTIFICADO='ECOT103I';
update CORE_SERVICIO set ACTIONSINCRONA='peticionSincrona' where CODCERTIFICADO='ECOT104I';
update CORE_SERVICIO set ACTIONSINCRONA='peticionSincrona' where CODCERTIFICADO='NIVRENTI';
update CORE_SERVICIO set ACTIONSINCRONA='peticionSincrona', URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/TGSS.CorrientePago',URLASINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/TGSS.CorrientePago.Asincrona'  where CODCERTIFICADO='Q2827003ATGSS001';
update CORE_SERVICIO set ACTIONSINCRONA='peticionSincrona', URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/TGSS.AltaEnFecha'   where CODCERTIFICADO='Q2827003ATGSS006';
update CORE_SERVICIO set URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/IMSERSO.ConsultaDatosDependencia',URLASINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/IMSERSO.ConsultaDatosDependencia.Asincrona'  where CODCERTIFICADO='SVDCDEPENWS01';
update CORE_SERVICIO set URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/INSS.ConsultaPrestaciones'  where CODCERTIFICADO='Q2827002CINSS001';
update CORE_SERVICIO set URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/Navarra.ConsultaIAE',URLASINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/Navarra.ConsultaIAE.Asincrona'  where CODCERTIFICADO='HTNIAE';
update CORE_SERVICIO set URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/DGSeguros.ConsultaMediadores',URLASINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/DGSeguros.ConsultaMediadores.Asincrona'  where CODCERTIFICADO='DGSFP0001';
update CORE_SERVICIO set URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/DGSeguros.ConsultaAseguradoras',URLASINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/DGSeguros.ConsultaAseguradoras.Asincrona'  where CODCERTIFICADO='DGSFP0002';
update CORE_SERVICIO set URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/DGSeguros.ConsultaFondos',URLASINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/DGSeguros.ConsultaFondos.Asincrona'  where CODCERTIFICADO='DGSFP0003';
update CORE_SERVICIO set URLASINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/DGSeguros.ConsultaSolvencia.Asincrona'  where CODCERTIFICADO='DGSFP0005';
update CORE_SERVICIO set URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/MAEC.ConsultaLegalizacionFirmas',URLASINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/MAEC.ConsultaLegalizacionFirmas.Asincrona'  where CODCERTIFICADO='SVDMAECWS01';
update CORE_SERVICIO set URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/Notarios.ConsultaNotarios'  where CODCERTIFICADO='SVDNOTLISTWS01';
update CORE_SERVICIO set URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/Notarios.ConsultaSubsistencia'  where CODCERTIFICADO='SVDNOTSUBWS01';
update CORE_SERVICIO set URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/Notarios.ConsultaCopiaSimple'  where CODCERTIFICADO='SVDNOTCOPSIMWS01';
update CORE_SERVICIO set URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/Notarios.ConsultaAdministradores'  where CODCERTIFICADO='SVDNOTSUBADMWS01';
update CORE_SERVICIO set URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/MUFACE.CertificadoAbonos',URLASINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/MUFACE.CertificadoAbonos.Asincrona'  where CODCERTIFICADO='SVDMUFABSWS01';
update CORE_SERVICIO set URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/MUFACE.CertificadoAfiliacion',URLASINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/MUFACE.CertificadoAfiliacion.Asincrona'  where CODCERTIFICADO='SVDMUFAFIWS01';
update CORE_SERVICIO set URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/MUFACE.CertificadoPrestacionesRecibidas',URLASINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/MUFACE.CertificadoPrestacionesRecibidas.Asincrona'  where CODCERTIFICADO='SVDMUFPRESTAWS01';
update CORE_SERVICIO set URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/PaisVasco.ConsultaIAE',URLASINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/PaisVasco.ConsultaIAE.Asincrona'  where CODCERTIFICADO='SVDPVIAEWS01';
update CORE_SERVICIO set URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/Extranjeria.ConsultaResidenciaLegal',URLASINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/Extranjeria.ConsultaResidenciaLegal.Asincrona'  where CODCERTIFICADO='SVDRESLEGEXWS01';
update CORE_SERVICIO set URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/RegistroCivil.ConsultaNacimiento',URLASINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/RegistroCivil.ConsultaNacimiento.Asincrona'  where CODCERTIFICADO='SVDSCCNWS01';
update CORE_SERVICIO set URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/RegistroCivil.ConsultaMatrimonio',URLASINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/RegistroCivil.ConsultaMatrimonio.Asincrona'  where CODCERTIFICADO='SVDSCCMWS01';
update CORE_SERVICIO set URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/RegistroCivil.ConsultaDefuncion',URLASINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/RegistroCivil.ConsultaDefuncion.Asincrona'  where CODCERTIFICADO='SVDSCCDWS01';
UPDATE CORE_SERVICIO SET URLASINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/SEPE.CertificadoDatosDesempleo.Asincrona'  where CODCERTIFICADO='SVDSEPESITWS02';
UPDATE CORE_SERVICIO SET URLASINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/SEPE.CertificadoImportesActuales.Asincrona'  where CODCERTIFICADO='SVDSEPEIACTWS02';
UPDATE CORE_SERVICIO SET URLASINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/SEPE.CertificadoImportesPeriodos.Asincrona'  where CODCERTIFICADO='SVDSEPEIPERWS02'; 
UPDATE CORE_SERVICIO SET ACTIONSINCRONA='peticionSincrona', URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/SEPE.InscritoDemandanteEmpleo',URLASINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/SEPE.InscritoDemandanteEmpleo.Asincrona'  where CODCERTIFICADO='SVDSEPEDEMWS01';
UPDATE CORE_SERVICIO SET ACTIONSINCRONA='peticionSincrona', URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/SEPE.InscritoDemandanteEmpleoFechaConcreta',URLASINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/SEPE.InscritoDemandanteEmpleoFechaConcreta.Asincrona'  where CODCERTIFICADO='SVDSEPEDEMFECHAWS01';
update CORE_SERVICIO set URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/INE.VerificacionResidenciaFecha',URLASINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/INE.VerificacionResidenciaFecha.Asincrona'  where CODCERTIFICADO='SVDREXTFECHAWS01';
update CORE_SERVICIO set URLSINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/INE.VerificacionAmbitoResidencia',URLASINCRONA='https://intermediacionpp.redsara.es/servicios/SVD/INE.VerificacionAmbitoResidencia.Asincrona'  where CODCERTIFICADO='SVDRWS01';
update SCSP_CODIGO_ERROR set DESCRIPCION='Error genérico del BackOffice' WHERE CODIGO='0242';
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0244','No existe la petición {0} en el sistema');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0414','El número de elementos no coincide con el número de solicitudes recibidas');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0415','El número de solicitudes es mayor que 1, ejecute el servicio en modo asíncrono.');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0419','Existen identificadores de solicitud repetidos.');
ALTER TABLE CORE_PETICION_RESPUESTA ADD ERROR2 VARCHAR2 (4000);
commit;
DECLARE
   CURSOR c1 is
      SELECT error,idpeticion   FROM core_peticion_respuesta  ; 
var_var  VARCHAR2(4000);
long_var LONG;
sql_stmt0 VARCHAR2(4000);
sql_stmt VARCHAR2(4000);
sql_stmt2 VARCHAR2(4000);
sql_stmt3 VARCHAR2(4000);
BEGIN   
 sql_stmt2 := 'ALTER TABLE   core_peticion_respuesta DROP COLUMN error';
 sql_stmt3 := 'ALTER TABLE core_peticion_respuesta   RENAME COLUMN error2 TO error';
   FOR peticion_rec in c1
   LOOP
      update core_peticion_respuesta set error2= substr(peticion_rec.error,1,4000) where idpeticion=peticion_rec.idpeticion;
   END LOOP;  
  EXECUTE IMMEDIATE sql_stmt2 ; 
  EXECUTE IMMEDIATE sql_stmt3 ;  
END;
/
SHOW ERRORS;

UPDATE CORE_PARAMETRO_CONFIGURACION SET VALOR='3.5.3' WHERE NOMBRE='version.datamodel.scsp';

/* UpdateRequirente-3.5.4a3.5.5 */
ALTER TABLE  SERVICIO_AUTORIZADO_CESIONARIO  MODIFY ( BLOQUEADO  NUMBER(1,0) DEFAULT 0 NOT NULL);
UPDATE SERVICIO_AUTORIZADO_CESIONARIO  SET BLOQUEADO = 0 WHERE BLOQUEADO IS NULL;
ALTER TABLE  ORGANISMO_CESIONARIO MODIFY  (BLOQUEADO  NUMBER(1,0) DEFAULT 0 NOT NULL);   
 UPDATE ORGANISMO_CESIONARIO  SET BLOQUEADO = 0 WHERE BLOQUEADO IS NULL; 
UPDATE core_servicio SET plantillaXSLT = '/plantillaspdf/catastro/plantilla_ConsultaDatosCatastro_SVDCDATWS01.xslt' WHERE  codcertificado = 'SVDCDATWS01';
UPDATE core_servicio SET plantillaXSLT = '/plantillaspdf/catastro/plantilla_ConsultaDatosCatastro_SVDCDATWS02.xslt' WHERE  codcertificado = 'SVDCDATWS02';
UPDATE core_servicio SET descripcion = 'Comunicación de Cambio de domicilio' WHERE codcertificado LIKE 'SVDSCDWS01';
UPDATE core_servicio SET descripcion = 'Consulta datos de identidad' WHERE codcertificado LIKE 'SVDDGPCIWS02'; 
UPDATE core_servicio SET descripcion = 'Verificación de datos de identidad' WHERE codcertificado LIKE 'SVDDGPVIWS02';
UPDATE core_servicio SET descripcion = 'Consulta de datos catastrales' WHERE codcertificado LIKE 'SVDCDATWS02';
UPDATE core_servicio SET descripcion = 'Certificación de titularidad catastral' WHERE codcertificado LIKE 'SVDCTITWS02'; 
UPDATE core_servicio SET descripcion = 'Certificación Descriptiva y Gráfica de un Inmueble' WHERE codcertificado LIKE 'SVDCDYGWS02';		 
UPDATE core_servicio SET descripcion = 'Obtención de documentos por CSV' WHERE codcertificado LIKE 'SVDCCSVWS01';			 
UPDATE core_servicio SET descripcion = 'Consulta de Bienes Inmuebles' WHERE codcertificado LIKE 'SVDCBIWS02';

UPDATE core_servicio SET plantillaXSLT = '/plantillaspdf/minhap/plantilla_CambioDomicilio_SVDSCDWS01_pdf.xslt' where codcertificado like 'SVDSCDWS01';
