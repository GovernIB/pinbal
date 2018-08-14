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

/*                            */
/* Actualització a SCSP 4.0.0 */
/*                            */

/* Requirente_01.sql */
UPDATE CORE_PARAMETRO_CONFIGURACION SET VALOR='4.0.0' WHERE NOMBRE='version.datamodel.scsp'; 
RENAME SCSP_CODIGO_ERROR TO CORE_CODIGO_ERROR;
RENAME ORGANISMO_CESIONARIO TO CORE_ORGANISMO_CESIONARIO;
RENAME SCSP_ESTADO_PETICION TO CORE_ESTADO_PETICION; 
RENAME CORE_SECUENCIA_IDPETICION TO CORE_REQ_SECUENCIA_IDPETICION;
RENAME SERVICIO_AUTORIZADO_CESIONARIO TO CORE_REQ_CESIONARIOS_SERVICIOS;
commit;
ALTER TABLE    CORE_REQ_SECUENCIA_IDPETICION MODIFY    (  PREFIJO  varchar2(9)  );
ALTER TABLE    CORE_SERVICIO MODIFY    (  PREFIJOPETICION  VARCHAR2(9)  );
ALTER TABLE    CORE_SERVICIO MODIFY    (  PREFIJOIDTRANSMISION  VARCHAR2(9)  ); 
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Obtención de cuentas corrientes (ejercicio 2012)' WHERE CODCERTIFICADO = 'AEATCCC1';
commit;
CREATE  TABLE  CORE_REQ_MODULO_PDF(
  NOMBRE VARCHAR2(256 char) NOT NULL , 
  ACTIVO  NUMBER(1,0) DEFAULT '0' NOT NULL,
  ORDEN  NUMBER(1,0)  NOT NULL,
  PRIMARY KEY (NOMBRE) );
COMMIT;
INSERT INTO CORE_REQ_MODULO_PDF (NOMBRE,ACTIVO,ORDEN) VALUES ('handlerPdfHideFuncionario',0,2);
INSERT INTO CORE_REQ_MODULO_PDF (NOMBRE,ACTIVO,ORDEN) VALUES ('handlerPdfSign',1,0);
INSERT INTO CORE_REQ_MODULO_PDF (NOMBRE,ACTIVO,ORDEN) VALUES ('handlerPdfXsltTransform',1,1);
COMMIT;
CREATE  TABLE  CORE_REQ_MODULO_PDF_CESIONARIO (
  MODULO VARCHAR2(256 char) NOT NULL ,
  SERVICIO  NUMBER(19,0) NOT NULL ,
  ORGANISMO  NUMBER(19,0) NOT NULL ,
  ACTIVO  NUMBER(1,0) DEFAULT '0' NOT NULL,
  ORDEN   NUMBER(1,0)  NOT NULL,
  PRIMARY KEY (MODULO, SERVICIO, ORGANISMO));
COMMIT;
ALTER TABLE "CORE_REQ_MODULO_PDF_CESIONARIO" ADD CONSTRAINT "FK_MODULO_MOD_PDF" FOREIGN KEY ("MODULO")
	  REFERENCES "CORE_REQ_MODULO_PDF" ("NOMBRE") ENABLE;
ALTER TABLE "CORE_REQ_MODULO_PDF_CESIONARIO" ADD CONSTRAINT "FK_SERVICIO_MOD_PDF" FOREIGN KEY ("SERVICIO")
	  REFERENCES "CORE_SERVICIO" ("ID") ENABLE;
ALTER TABLE "CORE_REQ_MODULO_PDF_CESIONARIO" ADD CONSTRAINT "FK_ORG_CESIONARIO_MOD_PDF" FOREIGN KEY ("ORGANISMO")
	  REFERENCES "CORE_ORGANISMO_CESIONARIO" ("ID") ENABLE;
commit;	 
DROP PROCEDURE GETSECUENCIAIDPETICION;
COMMIT;
CREATE OR REPLACE PROCEDURE "GETSECUENCIAIDPETICION"  (  prefijo_param in varchar2, on_Secuencial out number)as  rRegistro ROWID;
begin  
    select ROWID, SECUENCIA+1 into rRegistro, on_Secuencial from CORE_REQ_SECUENCIA_IDPETICION where PREFIJO = prefijo_param for update;
    update CORE_REQ_SECUENCIA_IDPETICION  set SECUENCIA = on_Secuencial, FECHAGENERACION=sysdate  where rowid = rRegistro;  
	commit; 
    exception when no_data_found then on_Secuencial := 1;    
    insert into CORE_REQ_SECUENCIA_IDPETICION (PREFIJO, SECUENCIA,FECHAGENERACION) values (prefijo_param, on_Secuencial,(SELECT SYSDATE FROM DUAL)); 
commit; 
end;

/* Requirente_02.sql */
update CORE_SERVICIO set PLANTILLAXSLT='/plantillaspdf/aeat/plantilla_ConsultaRentaAgraria_AEATIREA.xslt'  WHERE CODCERTIFICADO='AEATIREA';
update CORE_SERVICIO set PLANTILLAXSLT='/plantillaspdf/aeat/plantilla_ConsultaRenta_AEATIR01.xslt' WHERE CODCERTIFICADO='AEATIR01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de cuentas bancarias' WHERE CODCERTIFICADO = 'AEATCCC1';  
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta del impuesto sobre actividades económicas (IAE)' WHERE CODCERTIFICADO = 'AEATIAE';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta del impuesto sobre la renta de las personas físicas (IRPF)' WHERE CODCERTIFICADO = 'AEATIR01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta del impuesto sobre la renta agraria' WHERE CODCERTIFICADO = 'AEATIREA';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de pensiones públicas exentas' WHERE CODCERTIFICADO = 'AEATPPE';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de rendimientos de trabajo' WHERE CODCERTIFICADO = 'AEATRDT1';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de mediadores de seguros y corredores de reaseguros' WHERE CODCERTIFICADO = 'DGSFP0001';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de entidades aseguradoras y reaseguros' WHERE CODCERTIFICADO = 'DGSFP0002';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de planes y fondos de pensiones' WHERE CODCERTIFICADO = 'DGSFP0003';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de datos de solvencia' WHERE CODCERTIFICADO = 'DGSFP0005'; 
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Obtención del domicilio fiscal' WHERE CODCERTIFICADO = 'DOMFISC';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Estar al corriente de obligaciones tributarias para contratación con las administraciones públicas con indicación de incumplimientos' WHERE CODCERTIFICADO = 'ECOT101I';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Estar al corriente de obligaciones tributarias para obtención de licencias de transporte con indicación de incumplimientos' WHERE CODCERTIFICADO = 'ECOT102I';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Estar al corriente de obligaciones tributarias para solicitud de subvenciones y ayudas con indicación de incumplimientos' WHERE CODCERTIFICADO = 'ECOT103I';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Estar al corriente de obligaciones tributarias para permisos de residencia y trabajo para extranjeros con indicación de incumplimientos' WHERE CODCERTIFICADO = 'ECOT104I';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Estar al corriente de obligaciones tributarias  genérico' WHERE CODCERTIFICADO = 'ECOTGENI';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de datos sobre el  impuesto de actividades económicas (Navarra)' WHERE CODCERTIFICADO = 'HTNIAE';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta del impuesto sobre actividades económicas 10 epígrafes (IAE)' WHERE CODCERTIFICADO = 'IAE10';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta del nivel de renta' WHERE CODCERTIFICADO = 'NIVRENTI';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de las prestaciones del registro de prestaciones sociales públicas, incapacidad temporal y maternidad' WHERE CODCERTIFICADO = 'Q2827002CINSS001';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Estar al corriente de pago con la Seguridad Social' WHERE CODCERTIFICADO = 'Q2827003ATGSS001';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Situación laboral en una fecha concreta' WHERE CODCERTIFICADO = 'Q2827003ATGSS006'; 
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta del Fichero de Titularidades Financieras por interviniente' WHERE CODCERTIFICADO = 'S2800665HFTF0001';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta del Fichero de Titularidades Financieras por producto' WHERE CODCERTIFICADO = 'S2800665HFTF0002';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Auditoría de consultas al Fichero de Titularidades Financieras' WHERE CODCERTIFICADO = 'S2800665HFTF0003';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de calificaciones de la prueba de conocimientos constitucionales y socioculturales de España (CCSE) y de los diplomas de español (DELE)' WHERE CODCERTIFICADO = 'SVDAPTNACIWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de la condición de becado' WHERE CODCERTIFICADO = 'SVDBECAWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de bienes e inmuebles' WHERE CODCERTIFICADO = 'SVDCBIWS02';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Estar al corriente de obligaciones tributarias para solicitud de subvenciones y ayudas' WHERE CODCERTIFICADO = 'SVDCCAACPASWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Estar al corriente de obligaciones tributarias para contratación con las administraciones públicas' WHERE CODCERTIFICADO = 'SVDCCAACPCWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Obtención de documentos por CSV' WHERE CODCERTIFICADO = 'SVDCCSVWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de datos catastrales' WHERE CODCERTIFICADO = 'SVDCDATWS02';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta del nivel y grado de dependencia' WHERE CODCERTIFICADO = 'SVDCDEPENWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Obtención de certificación descriptiva y gráfica' WHERE CODCERTIFICADO = 'SVDCDYGWS02';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Obtención de certificación de titularidad' WHERE CODCERTIFICADO = 'SVDCTITWS02';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de datos de identidad' WHERE CODCERTIFICADO = 'SVDDGPCIWS02';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Verificación de datos de identidad' WHERE CODCERTIFICADO = 'SVDDGPVIWS02';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de títulos no universitarios por datos de filiación' WHERE CODCERTIFICADO = 'SVDLSTTNUWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de títulos universitarios por datos de filiación' WHERE CODCERTIFICADO = 'SVDLSTTUWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de firmas para legalización diplomática de documentos públicos extranjeros' WHERE CODCERTIFICADO = 'SVDMAECWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de datos de matrícula' WHERE CODCERTIFICADO = 'SVDMATUNIVWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Certificado de datos de abonos' WHERE CODCERTIFICADO = 'SVDMUFABSWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de datos de abonos' WHERE CODCERTIFICADO = 'SVDMUFABSXMLWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Certificado de datos de afiliación' WHERE CODCERTIFICADO = 'SVDMUFAFIWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de datos de afiliación' WHERE CODCERTIFICADO = 'SVDMUFAFIXMLWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Certificado de datos de prestaciones de pago único recibidas' WHERE CODCERTIFICADO = 'SVDMUFPRESTAWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de datos de prestaciones de pago único recibidas' WHERE CODCERTIFICADO = 'SVDMUFPRESTAXMLWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de la copia simple de un poder notarial' WHERE CODCERTIFICADO = 'SVDNOTCOPSIMWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de notarios y notarías' WHERE CODCERTIFICADO = 'SVDNOTLISTWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de la subsistencia de los administradores de una sociedad' WHERE CODCERTIFICADO = 'SVDNOTSUBADMWS01'; 
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de la subsistencia de un poder notarial' WHERE CODCERTIFICADO = 'SVDNOTSUBWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta del permiso de explotación marisquera' WHERE CODCERTIFICADO = 'SVDPERMEXWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de datos sobre el  impuesto de actividades económicas (País Vasco)' WHERE CODCERTIFICADO = 'SVDPVIAEWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Estar inscrito en el Registro Central de Personal' WHERE CODCERTIFICADO = 'SVDRCPINSCRITOWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de datos de residencia legal' WHERE CODCERTIFICADO = 'SVDRESLEGEXWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de datos de residencia con fecha de última variación para la supresión del volante de empadronamiento' WHERE CODCERTIFICADO = 'SVDREXTFECHAWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Verificación del ámbito territorial de residencia' WHERE CODCERTIFICADO = 'SVDRWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de defunción' WHERE CODCERTIFICADO = 'SVDSCCDWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de matrimonio' WHERE CODCERTIFICADO = 'SVDSCCMWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de nacimiento' WHERE CODCERTIFICADO = 'SVDSCCNWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de los datos de discapacidad' WHERE CODCERTIFICADO = 'SVDSCDDWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Comunicación del cambio de domicilio' WHERE CODCERTIFICADO = 'SVDSCDWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de los datos de familia numerosa' WHERE CODCERTIFICADO = 'SVDSCTFNWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de datos de residencia con fecha de última variación para finalidades distintas a la supresión del volante de empadronamiento' WHERE CODCERTIFICADO = 'SVDSECOPA01WS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de estar inscrito como demandante de empleo a fecha concreta' WHERE CODCERTIFICADO = 'SVDSEPEDEMFECHAWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de estar inscrito como demandante de empleo a fecha actual' WHERE CODCERTIFICADO = 'SVDSEPEDEMWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de los importes de las prestaciones percibidas a fecha actual' WHERE CODCERTIFICADO = 'SVDSEPEIACTWS02';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de los importes de las prestaciones percibidas en un periodo' WHERE CODCERTIFICADO = 'SVDSEPEIPERWS02';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de la situación actual de desempleo' WHERE CODCERTIFICADO = 'SVDSEPESITWS02';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de títulos no universitarios por documentación' WHERE CODCERTIFICADO = 'SVDTNUWS03';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de títulos universitarios por documentación' WHERE CODCERTIFICADO = 'SVDTUWS03';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Validación del NIF de un contribuyente ' WHERE CODCERTIFICADO = 'VALNIF'; 
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de datos de un vehículo' WHERE CODCERTIFICADO = 'SVDDGTVEHICULOWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de datos de un conductor' WHERE CODCERTIFICADO = 'SVDDGTCONDUCTORWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de títulos no universitarios por datos de filiación' WHERE CODCERTIFICADO = 'SVDLSTTNUWS01'; 
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de inexistencia de delitos sexuales por datos de filiación' WHERE CODCERTIFICADO = 'SVDDELSEXWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de inexistencia de delitos sexuales por documentación' WHERE CODCERTIFICADO = 'SVDDELSEXCDIWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de inexistencia de antecedentes penales por datos de filiación' WHERE CODCERTIFICADO = 'SVDCAPWS01';
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Consulta de inexistencia de antecedentes penales por documentación' WHERE CODCERTIFICADO = 'SVDCAPCDIWS01';

/* Requirente_03.sql */
--ALTER TABLE CORE_LOG MODIFY (MENSAJE CLOB );
ALTER TABLE CORE_ORGANISMO_CESIONARIO MODIFY ( LOGO BLOB );  
ALTER TABLE CORE_REQ_MODULO_PDF MODIFY ( ACTIVO  DEFAULT 1);
ALTER TABLE CORE_REQ_MODULO_PDF_CESIONARIO MODIFY ( ACTIVO  DEFAULT 1); 
--ALTER TABLE CORE_TOKEN_DATA MODIFY ( DATOS CLOB ); 
--ALTER TABLE CORE_TRANSMISION MODIFY ( XMLTRANSMISION CLOB ); 
COMMIT;
DECLARE
    cursor cnombre Is select index_name from user_indexes where status='UNUSABLE';
BEGIN 
    FOR rNOMBRE IN cnombre LOOP 
		execute immediate 'ALTER INDEX ' || rNOMBRE.index_name || ' REBUILD ';
    END LOOP;
	COMMIT;
END;

/* updateEndpoints.sql */
update CORE_SERVICIO set  URLSINCRONA='https://ws.ia.aeat.es/ES98/L/iniinvoc/es.aeat.dit.adu.suap.iae10ws.IAE10I3SOAP' WHERE CODCERTIFICADO='IAE10';
update CORE_SERVICIO set  URLSINCRONA='https://ws.ia.aeat.es/ES98/L/iniinvoc/es.aeat.dit.adu.suap.ppews.PPEI3SOAP' WHERE CODCERTIFICADO='AEATPPE';
update CORE_SERVICIO set  URLSINCRONA='https://ws.ia.aeat.es/ES98/L/iniinvoc/es.aeat.dit.adu.suap.valnifws.VNIFI3SOAP' WHERE CODCERTIFICADO='VALNIF';
update CORE_SERVICIO set  URLSINCRONA='https://ws.ia.aeat.es/ES98/L/iniinvoc/es.aeat.dit.adu.suap.domicilioWS.DomFI3SOAP' WHERE CODCERTIFICADO='DOMFISC';
update CORE_SERVICIO set  URLSINCRONA='https://ws.ia.aeat.es/ES98/L/iniinvoc/es.aeat.dit.adu.suap.irpfws.IRPFI3SOAP' WHERE CODCERTIFICADO='AEATIR01';
update CORE_SERVICIO set  URLSINCRONA='https://ws.ia.aeat.es/ES98/L/iniinvoc/es.aeat.dit.adu.suap.ireaws.IREAI3SOAP' WHERE CODCERTIFICADO='AEATIREA';

/*
 * Actualització llibreries SCSP 4.6.0
 */

/* 4.0.0 a 4.2.0*/
UPDATE CORE_SERVICIO SET MAXSOLICITUDESPETICION='100', ACTIONSOLICITUD='solicitudRespuesta' , ACTIONASINCRONA='peticionAsincrona', URLASINCRONA='https://intermediacion.redsara.es/servicios/SVD/SEPBLAC.ConsultaFTF.Producto.Asincrona' WHERE CODCERTIFICADO ='S2800665HFTF0002';
UPDATE CORE_SERVICIO SET MAXSOLICITUDESPETICION='100', ACTIONSOLICITUD='solicitudRespuesta' , ACTIONASINCRONA='peticionAsincrona', URLASINCRONA='https://intermediacion.redsara.es/servicios/SVD/SEPBLAC.ConsultaFTF.Interviniente.Asincrona' WHERE CODCERTIFICADO ='S2800665HFTF0001';
UPDATE CORE_PARAMETRO_CONFIGURACION SET VALOR='4.2.0' WHERE NOMBRE='version.datamodel.scsp'; 
ALTER TABLE CORE_REQ_CESIONARIOS_SERVICIOS ADD   SSLFLAG  NUMBER(1,0) default 0;
commit;

/* 4.2.0 a 4.3.0*/
UPDATE CORE_PARAMETRO_CONFIGURACION SET VALOR='4.3.0' WHERE NOMBRE='version.datamodel.scsp'; 

ALTER TABLE    CORE_TRANSMISION MODIFY DOCTITULAR  VARCHAR2(30);
ALTER TABLE    CORE_TRANSMISION MODIFY EXPEDIENTE  VARCHAR2(65);
 
ALTER TABLE CORE_TRANSMISION ADD   ESTADOSECUNDARIO VARCHAR2(16); 

ALTER TABLE CORE_TRANSMISION ADD   CODIGOUNIDADTRAMITADORA VARCHAR2(9); 
ALTER TABLE CORE_TRANSMISION ADD   SEUDONIMOFUNCIONARIO VARCHAR2(32); 
ALTER TABLE CORE_PETICION_RESPUESTA ADD   ERRORSECUNDARIO VARCHAR2(1024); 
ALTER TABLE CORE_PETICION_RESPUESTA ADD   ESTADOSECUNDARIO VARCHAR2(16); 

ALTER TABLE CORE_SERVICIO ADD XPATHCODIGOERRORSECUNDARIO VARCHAR2(256) ;

COMMIT;
 UPDATE CORE_SERVICIO SET XPATHCODIGOERROR='//*[local-name()=''CodigoEstado'']', XPATHLITERALERROR= '//*[local-name()=''LiteralError'']', XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']' where codcertificado='Q2827002CINSS002';

UPDATE CORE_SERVICIO SET XPATHCODIGOERROR='//*[local-name()=''CodRet'']' , XPATHLITERALERROR= '//*[local-name()=''DescripcionError'']'   where CODCERTIFICADO='HTNIAE';

UPDATE CORE_SERVICIO SET XPATHCODIGOERROR='//*[local-name()=''Codigo'']', XPATHLITERALERROR= '//*[local-name()=''Descripcion'']'  where CODCERTIFICADO='S2800665HFTF0003';
UPDATE CORE_SERVICIO SET XPATHCODIGOERROR='//*[local-name()=''Codigo'']', XPATHLITERALERROR='//*[local-name()=''Descripcion'']'   where CODCERTIFICADO='S2800665HFTF0002';
UPDATE CORE_SERVICIO SET XPATHCODIGOERROR='//*[local-name()=''Codigo'']', XPATHLITERALERROR='//*[local-name()=''Descripcion'']'  where CODCERTIFICADO='S2800665HFTF0001';

UPDATE CORE_SERVICIO SET  XPATHLITERALERROR='//*[local-name()=''Literal'']'    where CODCERTIFICADO='SVDMATUNIVWS01';



UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDPERMEXWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDSCDWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDDGPCIWS02';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDDGPVIWS02';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDSECOPA01WS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDREXTFECHAWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDRWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDTUWS03';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDLSTTUWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDTNUWS03';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDLSTTNUWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDBECAWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDSEPESITWS02';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDSEPEIACTWS02';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDSEPEIPERWS02';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDSEPEDEMWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDSEPEDEMFECHAWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDCDATWS02';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDCTITWS02';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDCDYGWS02';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDCCSVWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDCBIWS02';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDCDEPENWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDSCCNWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDSCCMWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDSCCDWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='DGSFP0001';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='DGSFP0002';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='DGSFP0003';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='DGSFP0005';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDMAECWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDRESLEGEXWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDNOTLISTWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDNOTSUBWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDNOTCOPSIMWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDNOTSUBADMWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDSCTFNWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDSCDDWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDCCAACPASWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDCCAACPCWS01';

UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDDGTCONDUCTORWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDDGTVEHICULOWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDIGAECONCESIONWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDIGAEMINIMISWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDIGAEINHABILITACIONWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDVTUWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDVTNUWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDDGOJDESCCOMPLWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDDGOJDESCPARCIALWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDDGOJDESCULTIMAWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDSRSPSBANUALWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDSRSPSBACTUALWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDDGOJALTAWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDDGOJBAJAWS01';
UPDATE CORE_SERVICIO SET  XPATHCODIGOERRORSECUNDARIO='//*[local-name()=''CodigoEstadoSecundario'']'  WHERE CODCERTIFICADO='SVDDGOJMODIFWS01'; 

INSERT INTO CORE_CODIGO_ERROR(CODIGO,DESCRIPCION) VALUES ('0317','El organismo solicitante {0} de la solicitud de respuesta no coincide con el enviado en la petici\F3n');

DECLARE
  l_cnt integer;
BEGIN
  SELECT COUNT(*)
    INTO l_cnt 
    FROM  user_tab_cols
   WHERE  
     table_name = 'CORE_REQ_MODULO_PDF_CESIONARIO'
     AND column_name = 'ORDEN';

  IF( l_cnt = 1 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE CORE_REQ_MODULO_PDF_CESIONARIO DROP COLUMN ORDEN';
  END IF;
END;

/* 4.3.0 a 4.4.0*/
INSERT INTO CORE_PARAMETRO_CONFIGURACION (NOMBRE,DESCRIPCION,VALOR) VALUES ('task.polling.size','Número de peticiones máximo a ser tratadas por ciclo de polling','50'); 
UPDATE CORE_PARAMETRO_CONFIGURACION SET VALOR='4.4.0' where NOMBRE='version.datamodel.scsp'; 
UPDATE CORE_SERVICIO SET DESCRIPCION = 'Estar al corriente de obligaciones tributarias para contratación con las administraciones públicas' WHERE CODCERTIFICADO = 'ECOT101I';
		
DELETE  FROM CORE_PARAMETRO_CONFIGURACION WHERE NOMBRE='core_log.enabled';

ALTER TABLE PBL_SERVEI_CONFIG ADD ACTIVA_GESTIO_XSD NUMBER(1);
UPDATE PBL_SERVEI_CONFIG SET ACTIVA_GESTIO_XSD = 0 WHERE ACTIVA_GESTIO_XSD IS NULL;
ALTER TABLE CORE_TRANSMISION ADD ERROR2 VARCHAR2 (4000);
commit;
DECLARE
   CURSOR c1 is
      SELECT ERROR,IDPETICION,IDTRANSMISION  FROM CORE_TRANSMISION  ; 
		var_var  VARCHAR2(4000);
		long_var LONG;
		sql_stmt0 VARCHAR2(4000);
		sql_stmt VARCHAR2(4000);
		sql_stmt2 VARCHAR2(4000);
		sql_stmt3 VARCHAR2(4000);
		BEGIN   
		 sql_stmt2 := 'ALTER TABLE   CORE_TRANSMISION DROP COLUMN error';
		 sql_stmt3 := 'ALTER TABLE CORE_TRANSMISION   RENAME COLUMN error2 TO error';
		 
		   FOR TRANSMISION_REC in c1
		   LOOP
		      UPDATE CORE_TRANSMISION SET ERROR2= SUBSTR(TRANSMISION_REC.ERROR,1,4000) WHERE IDPETICION=TRANSMISION_REC.IDPETICION AND IDTRANSMISION=TRANSMISION_REC.IDTRANSMISION;
		 
		   END LOOP;  
		   
		  EXECUTE IMMEDIATE sql_stmt2 ; 
		  EXECUTE IMMEDIATE sql_stmt3 ;  
		 
		END;

/* 4.4.0 a 4.5.0*/
UPDATE CORE_PARAMETRO_CONFIGURACION SET VALOR='4.5.0' WHERE NOMBRE='version.datamodel.scsp'; 
ALTER TABLE CORE_ORGANISMO_CESIONARIO ADD CODIGOUNIDADTRAMITADORA VARCHAR(9);  

/* 4.5.0 a 4.6.0*/