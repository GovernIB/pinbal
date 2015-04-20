--                    --
-- SCSP 3.3.0 a 3.3.1 --
--                    --
Insert into CORE_SERVICIO (CODCERTIFICADO,URLSINCRONA,URLASINCRONA,ACTIONSINCRONA,ACTIONASINCRONA,ACTIONSOLICITUD,VERSIONESQUEMA,TIPOSEGURIDAD,PREFIJOPETICION,XPATHCIFRADOSINCRONO,XPATHCIFRADOASINCRONO,ESQUEMAS,CLAVEFIRMA,CLAVECIFRADO,ALGORITMOCIFRADO,NUMEROMAXIMOREENVIOS,MAXSOLICITUDESPETICION,PREFIJOIDTRANSMISION,DESCRIPCION,EMISOR,FECHAALTA,FECHABAJA,CADUCIDAD,XPATHLITERALERROR,XPATHCODIGOERROR,TIMEOUT,VALIDACIONFIRMA) VALUES ('SVDRESLEGEXWS01','https://intermediacionpp.redsara.es/Extranjeria/services/ConsultaResidenciaLegal','https://intermediacionpp.redsara.es/Extranjeria/services/ConsultaResidenciaLegalAsincrona','PeticionSincrona','peticionAsincrona','solicitudRespuesta','V3','WS-Security','PINBAL',NULL,NULL,'/schemas/SVDRESLEGEXWS01v3','ALIAS-CERTIFICADO-FIRMA',NULL,NULL,50,999,NULL,'Verificacion de Datos de Residencia Legal (extranjeria)','S2833002E',SYSDATE,NULL,10,'//*[local-name()=''LiteralError'']','//*[local-name()=''CodigoEstado'']',60,NULL);
INSERT INTO core_emisor_certificado (cif,nombre) VALUES ('S2812001B','MAEC');
Insert into CORE_SERVICIO (CODCERTIFICADO,URLSINCRONA,URLASINCRONA,ACTIONSINCRONA,ACTIONASINCRONA,ACTIONSOLICITUD,VERSIONESQUEMA,TIPOSEGURIDAD,PREFIJOPETICION,XPATHCIFRADOSINCRONO,XPATHCIFRADOASINCRONO,ESQUEMAS,CLAVEFIRMA,CLAVECIFRADO,ALGORITMOCIFRADO,NUMEROMAXIMOREENVIOS,MAXSOLICITUDESPETICION,PREFIJOIDTRANSMISION,EMISOR,DESCRIPCION,FECHAALTA,FECHABAJA,CADUCIDAD,XPATHLITERALERROR,XPATHCODIGOERROR,TIMEOUT,VALIDACIONFIRMA) VALUES ('SVDMAECWS01','https://intermediacionpp.redsara.es/MAEC/services/ConsultaFirmas','https://intermediacionpp.redsara.es/MAEC/services/ConsultaFirmasAsincrona','peticionSincrona','peticionAsincrona','solicitudRespuesta','V3','WS-Security','PINBAL',null,null,'/schemas/SVDMAECWS01v3','ALIAS-CERTIFICADO-FIRMA',null,null,0,0,null,'S2812001B','Servicio Legalización Firmas',SYSDATE,null,0,'//*[local-name()=''LiteralError'']','//*[local-name()=''CodigoEstado'']',60,NULL);
Insert into CORE_SERVICIO (CODCERTIFICADO,URLSINCRONA,URLASINCRONA,ACTIONSINCRONA,ACTIONASINCRONA,ACTIONSOLICITUD,VERSIONESQUEMA,TIPOSEGURIDAD,PREFIJOPETICION,XPATHCIFRADOSINCRONO,XPATHCIFRADOASINCRONO,ESQUEMAS,CLAVEFIRMA,CLAVECIFRADO,ALGORITMOCIFRADO,NUMEROMAXIMOREENVIOS,MAXSOLICITUDESPETICION,PREFIJOIDTRANSMISION,DESCRIPCION,EMISOR,FECHAALTA,FECHABAJA,CADUCIDAD,XPATHLITERALERROR,XPATHCODIGOERROR,TIMEOUT,VALIDACIONFIRMA) VALUES ('SVDSEPEDEMFECHAWS01','https://intermediacionpp.redsara.es/SPEE-INEM/services/InscritoDemandanteEmpleoFechaConcreta','https://intermediacionpp.redsara.es/SPEE-INEM/services/InscritoDemandanteEmpleoFechaConcretaAsincrona','peticionSincrona','peticionAsincrona','solicitudRespuesta','V3','WS-Security','PINBAL',NULL,NULL,'/schemas/SVDSEPEDEMFECHAWS01v3','ALIAS-CERTIFICADO-FIRMA',NULL,NULL,50,999,NULL,'Servicio Consulta Demandante Empleo a Fecha Concreta','Q2819009H',SYSDATE,NULL,10,'//*[local-name()=''LiteralError'']','//*[local-name()=''CodigoEstado'']',60,NULL);
Insert into CORE_SERVICIO (CODCERTIFICADO,URLSINCRONA,URLASINCRONA,ACTIONSINCRONA,ACTIONASINCRONA,ACTIONSOLICITUD,VERSIONESQUEMA,TIPOSEGURIDAD,PREFIJOPETICION,XPATHCIFRADOSINCRONO,XPATHCIFRADOASINCRONO,ESQUEMAS,CLAVEFIRMA,CLAVECIFRADO,ALGORITMOCIFRADO,NUMEROMAXIMOREENVIOS,MAXSOLICITUDESPETICION,PREFIJOIDTRANSMISION,DESCRIPCION,EMISOR,FECHAALTA,FECHABAJA,CADUCIDAD,XPATHLITERALERROR,XPATHCODIGOERROR,TIMEOUT,VALIDACIONFIRMA) VALUES ('SVDSEPEDEMWS01','https://intermediacionpp.redsara.es/SPEE-INEM/services/InscritoDemandanteEmpleo','https://intermediacionpp.redsara.es/SPEE-INEM/services/InscritoDemandanteEmpleoAsincrona','peticionSincrona','peticionAsincrona','solicitudRespuesta','V3','WS-Security','PINBAL',NULL,NULL,'/schemas/SVDSEPEDEMWS01v3','ALIAS-CERTIFICADO-FIRMA',NULL,NULL,50,999,NULL,'Servicio Consulta Demandante Empleo a Fecha Actual','Q2819009H',SYSDATE,NULL,10,'//*[local-name()=''LiteralError'']','//*[local-name()=''CodigoEstado'']',60,NULL);
ALTER TABLE  core_servicio  ADD PLANTILLAXSLT VARCHAR2(512)   NULL;
INSERT INTO core_servicio (codcertificado,urlsincrona,urlasincrona,actionsincrona,actionasincrona,actionsolicitud,versionesquema,tiposeguridad,prefijopeticion,xpathcifradosincrono,xpathcifradoasincrono,esquemas,clavefirma,clavecifrado,algoritmocifrado,numeromaximoreenvios,maxsolicitudespeticion,prefijoidtransmision,descripcion,emisor,fechaalta,fechabaja,caducidad,xpathliteralerror,xpathcodigoerror,timeout,validacionfirma,plantillaXSLT) VALUES ('DOMFISC','https://ws.ia.aeat.es/ES98/L/iniinvoc/es.aeat.dit.adu.suap.domicilioWS.DomFV3SOAP',NULL,'operacion_servicio',NULL,NULL,'V3','XMLSignature','PINBAL',NULL,NULL,'/schemas/DOMFISCv3','ALIAS-CERTIFICADO-FIRMA',NULL,NULL,0,0,NULL,'Domicilio Fiscal v3','Q2826000H',CURRENT_TIMESTAMP,NULL,0,'//*[local-name()=''DescRespuesta'']','//*[local-name()=''CodRespuesta'']',60,null,'/plantillaspdf/aeat/plantilla_DomicilioFiscal_DOMFISC.xslt')

--                    --
-- SCSP 3.3.1 a 3.4.0 --
--                    --
--------------
-- Si no posee específicado ningún prefijo para la generación de identificadores de petición, se deberá 
-- especificar uno del cual se tenga constancia que es unívoco.
-- A partir de la versión 3.5.0 será obligatorio especificar o un prefijo global genérico o uno por cada 
-- uno de los servicios a consumir.
--------------
delete from core_parametro_configuracion where nombre='prefijo.idpeticion';
insert into core_parametro_configuracion (nombre,valor) values ('prefijo.idpeticion', 'PINBAL');
CREATE SEQUENCE ID_CLAVE_PRIVADA_SEQUENCE START WITH     1 INCREMENT BY   1 NOCACHE NOCYCLE;
CREATE SEQUENCE ID_CLAVE_PUBLICA_SEQUENCE START WITH     1 INCREMENT BY   1 NOCACHE NOCYCLE;
CREATE SEQUENCE ID_ORGANISMO_CESIONARIO_SEQ START WITH     1 INCREMENT BY   1 NOCACHE NOCYCLE;
CREATE SEQUENCE ID_SERVICIO_CESIONARIO_SEQ START WITH     1 INCREMENT BY   1 NOCACHE NOCYCLE;  
CREATE TABLE   ORGANISMO_CESIONARIO  (
   ID NUMBER(19,0), 
   NOMBRE  VARCHAR2(50 CHAR),
   CIF  VARCHAR2(50 CHAR),
   FECHAALTA TIMESTAMP (6), 
   FECHABAJA TIMESTAMP (6), 
   BLOQUEADO  NUMBER(1,0),
   LOGO  long raw,
   PRIMARY KEY (ID)
  )  ;
--ALTER TABLE  core_servicio  DROP CONSTRAINT SERV_CLAVEFIRMA;
--ALTER TABLE  core_servicio  DROP CONSTRAINT SERV_CLAVECIFRADO; 

--
BEGIN
  FOR c IN
  (select   c.owner, c.table_name, c.constraint_name   from   user_tab_columns t,user_cons_columns c where t.table_name = 'CORE_SERVICIO' and   t.table_name = c.table_name and   t.column_name = c.column_name and (t.column_name='CLAVECIFRADO' or (t.column_name='CLAVEFIRMA')))
  LOOP
    dbms_utility.exec_ddl_statement('alter table "' || c.owner || '"."' || c.table_name || '" drop constraint "' || c.constraint_name||'"');
  END LOOP;
END;
/
--

BEGIN
  FOR c IN
  (SELECT c.owner, c.table_name, c.constraint_name, c.constraint_type
   FROM user_constraints c, user_tables t
   WHERE c.table_name = t.table_name
  and (c.table_name='CORE_CLAVE_PRIVADA' or c.table_name='CORE_CLAVE_PUBLICA' ) AND c.constraint_type='P'
   ORDER BY c.constraint_type)
  LOOP
    dbms_utility.exec_ddl_statement('alter table "' || c.owner || '"."' || c.table_name || '" drop constraint "' || c.constraint_name||'"');
  END LOOP;
END;
/

BEGIN
  FOR c IN
  (select   c.owner, c.table_name, c.constraint_name   from   user_tab_columns t,user_cons_columns c where t.table_name = 'CORE_SERVICIO' and   t.table_name = c.table_name and   t.column_name = c.column_name and   t.nullable = 'N' and t.column_name='CLAVEFIRMA')
  LOOP
    dbms_utility.exec_ddl_statement('alter table "' || c.owner || '"."' || c.table_name || '" drop constraint "' || c.constraint_name||'"');
  END LOOP;
END;
/

ALTER table  core_clave_privada add   ID NUMBER(19,0);  
ALTER table  core_clave_publica add   ID NUMBER(19,0); 
ALTER table  core_clave_privada add FECHAALTA TIMESTAMP (6);
ALTER table  core_clave_privada add FECHABAJA TIMESTAMP (6); 	
ALTER table  core_clave_publica add FECHAALTA TIMESTAMP (6); 	
ALTER table  core_clave_publica add FECHABAJA TIMESTAMP (6); 	

--ALTER TABLE  core_servicio  ADD PLANTILLAXSLT VARCHAR2(512)   NULL;
ALTER TABLE  core_clave_privada  ADD  ORGANISMO  NUMBER(19,0) NULL;

--commit;
update core_clave_publica set id= (ID_CLAVE_PUBLICA_SEQUENCE.nextval);
update core_clave_privada set id= (ID_CLAVE_PRIVADA_SEQUENCE.nextval);
alter table core_servicio rename column clavefirma to clavefirma_alias;
alter table core_servicio rename column clavecifrado to clavecifrado_alias;
ALTER TABLE  core_servicio  ADD  clavecifrado  NUMBER(19,0) NULL;
ALTER TABLE  core_servicio  ADD  clavefirma  NUMBER(19,0) NULL;
INSERT INTO organismo_cesionario (id, cif,nombre, fechaalta,bloqueado, logo) VALUES ((ID_ORGANISMO_CESIONARIO_SEQ.nextval),
(select valor from ( select valor   from core_parametro_configuracion  where nombre='cif.solicitante'  order by nombre desc ) where ROWNUM  = 1),
(select valor from ( select valor   from core_parametro_configuracion  where nombre='nombre.solicitante'  order by nombre desc ) where ROWNUM  = 1),
 SYSDATE,0,NULL);										
--commit; 
update core_clave_privada set fechaAlta=SYSDATE;
update core_clave_publica set fechaAlta=SYSDATE; 
update core_servicio cs set ( cs.clavecifrado ) = (select  cc.id  from core_clave_publica cc where   cs.clavecifrado_alias =cc.alias and  rownum = 1);
update core_servicio cs set ( cs.clavefirma ) = (select  cf.id from core_clave_privada cf where   cs.clavefirma_alias =cf.alias and  rownum = 1);  
update core_clave_privada   set organismo =(select id from organismo_cesionario   where ROWNUM  = 1) WHERE EXISTS (   SELECT 1 FROM organismo_cesionario ) ;
--commit;
ALTER TABLE  core_servicio drop column  CLAVEFIRMA_ALIAS;
ALTER TABLE  core_servicio drop column  CLAVECIFRADO_ALIAS; 
ALTER TABLE  core_clave_publica MODIFY  FECHAALTA  TIMESTAMP (6) not null;
ALTER TABLE  core_clave_privada MODIFY  FECHAALTA  TIMESTAMP (6) not null; 
ALTER table  core_clave_privada MODIFY ORGANISMO   NUMBER(19,0) not null;
ALTER table  core_clave_privada MODIFY id   NUMBER(19,0) not null;
ALTER table  core_clave_publica MODIFY id   NUMBER(19,0) not null;
ALTER TABLE  core_clave_privada  ADD PRIMARY KEY ("ID");
ALTER TABLE  core_clave_publica  ADD PRIMARY KEY ("ID");
ALTER TABLE  core_clave_privada  ADD CONSTRAINT "clave_privada_org" FOREIGN KEY (organismo)
	  REFERENCES organismo_cesionario (id) ENABLE;

ALTER TABLE core_servicio ADD CONSTRAINT "serv_clavefirma" FOREIGN KEY (clavefirma)
	  REFERENCES core_clave_privada (id) ENABLE; 
	  
ALTER TABLE core_servicio ADD CONSTRAINT "serv_clavecifrado" FOREIGN KEY (clavecifrado)
	  REFERENCES core_clave_publica (id) ENABLE;
	  
ALTER table core_emisor_certificado MODIFY nombre varchar2(50);

INSERT INTO SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values  ('0253','No todas las solicitudes tienen el mismo identificador de solicitante');
INSERT INTO SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0504','Error en la configuración {0}');

CREATE TABLE SERVICIO_AUTORIZADO_CESIONARIO (
   ID NUMBER(19,0), 
   SERVICIO  VARCHAR2(64 CHAR),
   CLAVEPRIVADA  NUMBER(19,0),
   FECHAALTA TIMESTAMP (6), 
   FECHABAJA TIMESTAMP (6), 
   BLOQUEADO  NUMBER(1,0),
   PRIMARY KEY (ID) 
);
ALTER TABLE "SERVICIO_AUTORIZADO_CESIONARIO" ADD CONSTRAINT "fk_servicio_org_cesionario" FOREIGN KEY ("SERVICIO")
	  REFERENCES "CORE_SERVICIO" ("CODCERTIFICADO") ENABLE;
ALTER TABLE "SERVICIO_AUTORIZADO_CESIONARIO" ADD CONSTRAINT "fk_clave_priv_org_cesionario" FOREIGN KEY ("CLAVEPRIVADA")
	  REFERENCES "CORE_CLAVE_PRIVADA" ("ID") ENABLE;


delete from core_parametro_configuracion where nombre='cif.solicitante';
delete from core_parametro_configuracion where nombre='nombre.solicitante';
update core_parametro_configuracion set valor='3.4.0' where nombre='version.datamodel.scsp';
--commit;
INSERT INTO SERVICIO_AUTORIZADO_CESIONARIO    (select ID_SERVICIO_CESIONARIO_SEQ.nextval, 
  codcertificado ,clavefirma , sysdate,null,0 from core_servicio    );
  
  
UPDATE core_servicio SET urlsincrona = 'https://ws.ia.aeat.es/ES98/L/iniinvoc/es.aeat.dit.adu.suap.irpfws.IRPFV3SOAP' ,actionsincrona = 'operacion_servicio' WHERE codcertificado = 'AEATIR01';

update core_servicio set plantillaXSLT='/plantillaspdf/aeat/plantilla_CorrientePagoObligacionesContratos_AEAT101I.xslt' where codcertificado = 'AEAT101I';
update core_servicio set plantillaXSLT='/plantillaspdf/aeat/plantilla_CorrientePagoObligacionesTransportes_AEAT102I.xslt' where codcertificado = 'AEAT102I';
update core_servicio set plantillaXSLT='/plantillaspdf/aeat/plantilla_CorrientePagoObligacionesSubvenciones_AEAT103I.xslt' where codcertificado = 'AEAT103I';
update core_servicio set plantillaXSLT='/plantillaspdf/aeat/plantilla_CorrientePagoObligacionesPermisosRes_AEAT104I.xslt' where codcertificado = 'AEAT104I';
update core_servicio set plantillaXSLT='/plantillaspdf/aeat/plantilla_CuentasBancarias_AEATCCC1_pdf.xslt' where codcertificado = 'AEATCCC1';
update core_servicio set plantillaXSLT='/plantillaspdf/aeat/plantilla_ImpuestoActividadesEconomicas_AEATIAE_pdf.xslt' where codcertificado = 'AEATIAE';
update core_servicio set plantillaXSLT='/plantillaspdf/aeat/plantilla_ImpuestoActividadesEconomicas_AEATIAE_pdf.xslt' where codcertificado = 'AEATIAEA';
update core_servicio set plantillaXSLT='/plantillaspdf/aeat/plantilla_RendimientosTrabajo_AEATRDT1.xslt' where codcertificado = 'AEATIR01';
update core_servicio set plantillaXSLT='/plantillaspdf/aeat/plantilla_ImpuestoActividadesEconomicas_AEATIAEA.xslt' where codcertificado = 'AEATIREA';
update core_servicio set plantillaXSLT='/plantillaspdf/aeat/plantilla_RendimientosTrabajo_AEATRDT1.xslt' where codcertificado = 'AEATRDT1';
update core_servicio set plantillaXSLT='/plantillaspdf/minhap/plantilla_ConsultaDatosIdentidad_CDISFWS01_pdf.xslt' where codcertificado = 'CDISFWS01';
update core_servicio set plantillaXSLT='/plantillaspdf/navarra/plantilla_ImpuestoActividadesEconomicas_HTNIAE.xslt' where codcertificado = 'HTNIAE';
update core_servicio set plantillaXSLT='/plantillaspdf/inss/plantilla_ConsultaPrestacionesSociales_Q2827002CINSS001.xslt' where codcertificado = 'Q2827002CINSS001';
update core_servicio set plantillaXSLT='/plantillaspdf/tgss/plantilla_CorrientePagoSegSocial_Q2827003ATGSS001.xslt' where codcertificado = 'Q2827003ATGSS001';
update core_servicio set plantillaXSLT='/plantillaspdf/tgss/plantilla_SituacionLaboralFechaConcreta_Q2827003ATGSS006.xslt' where codcertificado = 'Q2827003ATGSS006';
update core_servicio set plantillaXSLT='/plantillaspdf/aeat/plantilla_DomicilioFiscal_SCPWIJ1R.xslt' where codcertificado = 'SCPWIJ1R';
update core_servicio set plantillaXSLT='/plantillaspdf/catastro/plantilla_ConsultaBienesInmuebles_SVDCBIWS01.xslt' where codcertificado = 'SVDCBIWS01';
update core_servicio set plantillaXSLT='/plantillaspdf/catastro/plantilla_ConsultaDatosCatastro_SVDCDATWS01.xslt' where codcertificado = 'SVDCDATWS01';
update core_servicio set plantillaXSLT='/plantillaspdf/imserso/plantilla_ServicioConsultaDependencia_SVDCDEPENWS01.xslt' where codcertificado = 'SVDCDEPENWS01';
update core_servicio set plantillaXSLT='/plantillaspdf/maec/plantilla_ServicioLegalizacionFirmas_SVDMAECWS01_pdf.xslt' where codcertificado = 'SVDMAECWS01';
update core_servicio set plantillaXSLT='/plantillaspdf/minhap/plantilla_ConsultaDatosResidenciaLegal_SVDRESLEGEXWS01.xslt' where codcertificado = 'SVDRESLEGEXWS01';
update core_servicio set plantillaXSLT='/plantillaspdf/minhap/plantilla_ServicioConsultaResidenciaFecha_SVDREXTFECHAWS01.xslt' where codcertificado = 'SVDREXTFECHAWS01';
update core_servicio set plantillaXSLT='/plantillaspdf/minhap/plantilla_ServicioConsultaResidenciaTerritorial_SVDRWS01.xslt' where codcertificado = 'SVDRWS01';
update core_servicio set plantillaXSLT='/plantillaspdf/justicia/plantilla_ConsultaDefuncion_SVDSCCDWS01.xslt' where codcertificado = 'SVDSCCDWS01';
update core_servicio set plantillaXSLT='/plantillaspdf/justicia/plantilla_ConsultaMatrimonio_SVDSCCMWS01.xslt' where codcertificado = 'SVDSCCMWS01';
update core_servicio set plantillaXSLT='/plantillaspdf/justicia/plantilla_ConsultaNacimiento_SVDSCCNWS01.xslt' where codcertificado = 'SVDSCCNWS01';
update core_servicio set plantillaXSLT='/plantillaspdf/sepe/plantilla_DemandanteEmpleoFechaConcreta_SVDSEPEDEMFECHAWS01_pdf.xslt' where codcertificado = 'SVDSEPEDEMFECHAWS01';
update core_servicio set plantillaXSLT='/plantillaspdf/sepe/plantilla_DemandanteEmpleoFechaActual_SVDSEPEDEMWS01_pdf.xslt' where codcertificado = 'SVDSEPEDEMWS01';
update core_servicio set plantillaXSLT='/plantillaspdf/sepe/plantilla_PrestacionFechaActual_SVDSPEEIACTWS01_pdf.xslt' where codcertificado = 'SVDSPEEIACTWS01';
update core_servicio set plantillaXSLT='/plantillaspdf/sepe/plantilla_PrestacionPeriodo_SVDSPEEIPERWS01_pdf.xslt' where codcertificado = 'SVDSPEEIPERWS01';
update core_servicio set plantillaXSLT='/plantillaspdf/sepe/plantilla_SituacionDesempleo_SVDPEESITWS01_pdf.xslt' where codcertificado = 'SVDSPEESITWS01';
update core_servicio set plantillaXSLT='/plantillaspdf/educacion/plantilla_sctnu_SVDTNUWS01_pdf.xslt' where codcertificado = 'SVDTNUWS01';
update core_servicio set plantillaXSLT='/plantillaspdf/educacion/plantilla_sctu_SVDTUWS01_pdf.xslt' where codcertificado = 'SVDTUWS01';
update core_servicio set plantillaXSLT='/plantillaspdf/minhap/plantilla_VerificacionIdentidad_VDISFWS01_pdf.xslt' where codcertificado = 'VDISFWS01';
update core_servicio set plantillaXSLT='/plantillaspdf/minhap/plantilla_VerificacionResidenciaExtendida_VDRSFWS02_pdf.xslt' where codcertificado = 'VDRSFWS02';
update core_servicio set plantillaXSLT='/plantillaspdf/seguros/plantilla_Seguros.xslt' where codcertificado = 'DGSFP0001';
update core_servicio set plantillaXSLT='/plantillaspdf/seguros/plantilla_Seguros.xslt' where codcertificado = 'DGSFP0002';
update core_servicio set plantillaXSLT='/plantillaspdf/seguros/plantilla_Seguros.xslt' where codcertificado = 'DGSFP0003';
update core_servicio set plantillaXSLT='/plantillaspdf/seguros/plantilla_Seguros.xslt' where codcertificado = 'DGSFP0005';
update core_servicio set plantillaXSLT='/plantillaspdf/notarios/plantilla_NotariosCopiaSimple_SVDNOTCOPSIMWS01.xslt' where codcertificado = 'SVDNOTCOPSIMWS01'; 
update core_servicio set plantillaXSLT='/plantillaspdf/notarios/plantilla_NotariosLista_SVDNOTLISTWS01.xslt' where codcertificado = 'SVDNOTLISTWS01'  ; 
update core_servicio set plantillaXSLT='/plantillaspdf/notarios/plantilla_NotariosAdministradores_SVDNOTSUBADMWS01.xslt' where codcertificado = 'SVDNOTSUBADMWS01'; 
update core_servicio set plantillaXSLT='/plantillaspdf/notarios/plantilla_NotariosSubsistencia_SVDNOTSUBWS01.xslt' where codcertificado = 'SVDNOTSUBWS01'; 

--                    --
-- SCSP 3.4.0 a 3.4.1 --
--                    --
-- El següents inserts s'han posat més amunt per evitar cercar l'id del certificat de firma
--Insert into CORE_SERVICIO (CODCERTIFICADO,URLSINCRONA,URLASINCRONA,ACTIONSINCRONA,ACTIONASINCRONA,ACTIONSOLICITUD,VERSIONESQUEMA,TIPOSEGURIDAD,PREFIJOPETICION,XPATHCIFRADOSINCRONO,XPATHCIFRADOASINCRONO,ESQUEMAS,CLAVEFIRMA,CLAVECIFRADO,ALGORITMOCIFRADO,NUMEROMAXIMOREENVIOS,MAXSOLICITUDESPETICION,PREFIJOIDTRANSMISION,DESCRIPCION,EMISOR,FECHAALTA,FECHABAJA,CADUCIDAD,XPATHLITERALERROR,XPATHCODIGOERROR,TIMEOUT,VALIDACIONFIRMA) VALUES ('SVDSEPEDEMFECHAWS01','https://intermediacionpp.redsara.es/SPEE-INEM/services/InscritoDemandanteEmpleoFechaConcreta','https://intermediacionpp.redsara.es/SPEE-INEM/services/InscritoDemandanteEmpleoFechaConcretaAsincrona','peticionSincrona','peticionAsincrona','solicitudRespuesta','V3','WS-Security','PINBAL',NULL,NULL,'/schemas/SVDSEPEDEMFECHAWS01v3','ALIAS-CERTIFICADO-FIRMA',NULL,NULL,50,999,NULL,'Servicio Consulta Demandante Empleo a Fecha Concreta','Q2819009H',SYSDATE,NULL,10,'//*[local-name()=''LiteralError'']','//*[local-name()=''CodigoEstado'']',60,NULL);
--Insert into CORE_SERVICIO (CODCERTIFICADO,URLSINCRONA,URLASINCRONA,ACTIONSINCRONA,ACTIONASINCRONA,ACTIONSOLICITUD,VERSIONESQUEMA,TIPOSEGURIDAD,PREFIJOPETICION,XPATHCIFRADOSINCRONO,XPATHCIFRADOASINCRONO,ESQUEMAS,CLAVEFIRMA,CLAVECIFRADO,ALGORITMOCIFRADO,NUMEROMAXIMOREENVIOS,MAXSOLICITUDESPETICION,PREFIJOIDTRANSMISION,DESCRIPCION,EMISOR,FECHAALTA,FECHABAJA,CADUCIDAD,XPATHLITERALERROR,XPATHCODIGOERROR,TIMEOUT,VALIDACIONFIRMA) VALUES ('SVDSEPEDEMWS01','https://intermediacionpp.redsara.es/SPEE-INEM/services/InscritoDemandanteEmpleo','https://intermediacionpp.redsara.es/SPEE-INEM/services/InscritoDemandanteEmpleoAsincrona','peticionSincrona','peticionAsincrona','solicitudRespuesta','V3','WS-Security','PINBAL',NULL,NULL,'/schemas/SVDSEPEDEMWS01v3','ALIAS-CERTIFICADO-FIRMA',NULL,NULL,50,999,NULL,'Servicio Consulta Demandante Empleo a Fecha Actual','Q2819009H',SYSDATE,NULL,10,'//*[local-name()=''LiteralError'']','//*[local-name()=''CodigoEstado'']',60,NULL);
--INSERT INTO core_servicio (codcertificado,urlsincrona,urlasincrona,actionsincrona,actionasincrona,actionsolicitud,versionesquema,tiposeguridad,prefijopeticion,xpathcifradosincrono,xpathcifradoasincrono,esquemas,clavefirma,clavecifrado,algoritmocifrado,numeromaximoreenvios,maxsolicitudespeticion,prefijoidtransmision,descripcion,emisor,fechaalta,fechabaja,caducidad,xpathliteralerror,xpathcodigoerror,timeout,validacionfirma,plantillaXSLT) VALUES ('DOMFISC','https://ws.ia.aeat.es/ES98/L/iniinvoc/es.aeat.dit.adu.suap.domicilioWS.DomFV3SOAP',NULL,'operacion_servicio',NULL,NULL,'V3','XMLSignature','PINBAL',NULL,NULL,'/schemas/DOMFISCv3','ALIAS-CERTIFICADO-FIRMA',NULL,NULL,0,0,NULL,'Domicilio Fiscal v3','Q2826000H',CURRENT_TIMESTAMP,NULL,0,'//*[local-name()=''DescRespuesta'']','//*[local-name()=''CodRespuesta'']',60,null,'/plantillaspdf/aeat/plantilla_DomicilioFiscal_DOMFISC.xslt')

--                    --
-- SCSP 3.4.1 a 3.5.0 --
--                    --
BEGIN
  FOR c IN
  (select   c.owner, c.table_name, c.constraint_name   from   user_tab_columns t,user_cons_columns c where (t.table_name = 'CORE_MODULO_CONFIGURACION'  or t.table_name = 'CORE_PETICION_RESPUESTA'   or t.table_name = 'SERVICIO_AUTORIZADO_CESIONARIO'   or t.table_name = 'CORE_SERVICIO' )and   t.table_name = c.table_name and   t.column_name = c.column_name  and (t.column_name='CERTIFICADO' or  t.column_name='SERVICIO'  or  t.column_name='EMISOR'))
  LOOP
    dbms_utility.exec_ddl_statement('alter table "' || c.owner || '"."' || c.table_name || '" drop constraint "' || c.constraint_name||'"');
  END LOOP;
END;
/

BEGIN
  FOR c IN
  (SELECT c.owner, c.table_name, c.constraint_name, c.constraint_type
   FROM user_constraints c, user_tables t
   WHERE c.table_name = t.table_name
  and  (c.table_name='CORE_SERVICIO' or c.table_name='CORE_EMISOR_CERTIFICADO' ) AND c.constraint_type='P'
   ORDER BY c.constraint_type)
  LOOP
    dbms_utility.exec_ddl_statement('alter table "' || c.owner || '"."' || c.table_name || '" drop constraint "' || c.constraint_name||'"');
  END LOOP;
END;
/

--COMMIT;
CREATE SEQUENCE ID_SERVICIO_SEQUENCE START WITH     1 INCREMENT BY   1 NOCACHE NOCYCLE;
CREATE SEQUENCE ID_EMISOR_SEQUENCE START WITH     1 INCREMENT BY   1 NOCACHE NOCYCLE;
ALTER TABLE  SERVICIO_AUTORIZADO_CESIONARIO ADD   ORGANISMO NUMBER(19,0);   

ALTER TABLE  SERVICIO_AUTORIZADO_CESIONARIO ADD   SERVICIO_NUM NUMBER(19,0);   
ALTER TABLE  CORE_PETICION_RESPUESTA ADD   CODCERTIFICADO_NUM NUMBER(19,0); 
ALTER TABLE  CORE_MODULO_CONFIGURACION ADD    CERTIFICADO_NUM NUMBER(19,0); 
ALTER TABLE  CORE_SERVICIO ADD   EMISOR_NUM NUMBER(19,0); 
--COMMIT;

ALTER TABLE  CORE_SERVICIO ADD   ID NUMBER(19,0);   
ALTER TABLE  CORE_EMISOR_CERTIFICADO ADD   ID NUMBER(19,0);   

--COMMIT;
UPDATE CORE_SERVICIO SET ID= (ID_SERVICIO_SEQUENCE.NEXTVAL); 
UPDATE CORE_EMISOR_CERTIFICADO SET ID= (ID_EMISOR_SEQUENCE.NEXTVAL); 
--COMMIT;
UPDATE CORE_MODULO_CONFIGURACION CM SET ( CM.CERTIFICADO_NUM ) = (SELECT  CS.ID  FROM CORE_SERVICIO CS WHERE   CM.CERTIFICADO =CS.CODCERTIFICADO AND  ROWNUM = 1);
UPDATE CORE_PETICION_RESPUESTA CM SET ( CM.CODCERTIFICADO_NUM ) = (SELECT  CS.ID  FROM CORE_SERVICIO CS WHERE   CM.CERTIFICADO =CS.CODCERTIFICADO AND  ROWNUM = 1);
UPDATE SERVICIO_AUTORIZADO_CESIONARIO SAC SET ( SAC.SERVICIO_NUM ) = (SELECT  CS.ID  FROM CORE_SERVICIO CS WHERE   SAC.SERVICIO =CS.CODCERTIFICADO AND  ROWNUM = 1);
UPDATE SERVICIO_AUTORIZADO_CESIONARIO SAC SET ( SAC.ORGANISMO ) = (SELECT  CP.ORGANISMO  FROM CORE_CLAVE_PRIVADA CP WHERE   SAC.CLAVEPRIVADA =CP.ID AND  ROWNUM = 1);
UPDATE CORE_SERVICIO CS SET ( CS.EMISOR_NUM ) = (SELECT  CE.ID  FROM CORE_EMISOR_CERTIFICADO CE WHERE   CS.EMISOR =CE.CIF AND  ROWNUM = 1);
--COMMIT;
ALTER TABLE   CORE_SERVICIO DROP COLUMN   EMISOR;
ALTER TABLE   CORE_MODULO_CONFIGURACION DROP COLUMN   CERTIFICADO;
ALTER TABLE   CORE_PETICION_RESPUESTA DROP COLUMN   CERTIFICADO;
ALTER TABLE   SERVICIO_AUTORIZADO_CESIONARIO DROP COLUMN   SERVICIO;
--COMMIT;
ALTER TABLE CORE_SERVICIO  RENAME COLUMN EMISOR_NUM TO EMISOR; 
ALTER TABLE CORE_MODULO_CONFIGURACION  RENAME COLUMN CERTIFICADO_NUM TO CERTIFICADO; 
ALTER TABLE CORE_PETICION_RESPUESTA  RENAME COLUMN CODCERTIFICADO_NUM TO  CERTIFICADO; 
ALTER TABLE SERVICIO_AUTORIZADO_CESIONARIO  RENAME COLUMN SERVICIO_NUM TO SERVICIO; 
--COMMIT;
 
ALTER TABLE  CORE_EMISOR_CERTIFICADO  ADD PRIMARY KEY ("ID");
ALTER TABLE  CORE_SERVICIO  ADD PRIMARY KEY ("ID");
--COMMIT; 
 
ALTER TABLE  CORE_MODULO_CONFIGURACION  ADD CONSTRAINT "MOD_CONF_SERV" FOREIGN KEY (CERTIFICADO)
	  REFERENCES CORE_SERVICIO (ID) ENABLE;

ALTER TABLE CORE_PETICION_RESPUESTA ADD CONSTRAINT "PETICION_SERVICIO" FOREIGN KEY (CERTIFICADO)
	  REFERENCES CORE_SERVICIO (ID) ENABLE; 
	  
ALTER TABLE CORE_SERVICIO ADD CONSTRAINT "SERV_EMISOR" FOREIGN KEY (EMISOR)
	  REFERENCES CORE_EMISOR_CERTIFICADO (ID) ENABLE; 
		  
ALTER TABLE SERVICIO_AUTORIZADO_CESIONARIO ADD CONSTRAINT "FK_SERVICIO_ORG_CESIONARIO" FOREIGN KEY (SERVICIO)
	  REFERENCES CORE_SERVICIO (ID) ENABLE; 

ALTER TABLE SERVICIO_AUTORIZADO_CESIONARIO ADD CONSTRAINT "FK_ORGANISMO_ORG_CESIONARIO" FOREIGN KEY (ORGANISMO)
	  REFERENCES ORGANISMO_CESIONARIO (ID) ENABLE; 
	  

ALTER TABLE  CORE_CLAVE_PRIVADA ADD INTEROPERABILIDAD  NUMBER(1) DEFAULT 0;
UPDATE CORE_PARAMETRO_CONFIGURACION SET VALOR='3.5.0' WHERE NOMBRE='version.datamodel.scsp';	  
INSERT INTO SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) VALUES ('0314','PROCEDIMIENTO {0} NO AUTORIZADO A CONSULTAR EL SERVICIO {1}');

-- Nuevos servicios, solo ejecutar el script si se precisa de su consumo.
-- El següent emisor segurament ja està donat d'alta...
-- INSERT INTO core_emisor_certificado (cif,nombre) values ('Q2826000H','AEAT');
-- El següent insert s'ha posat més amunt per evitar cercar l'id emisor AEAT
-- INSERT INTO core_servicio (codcertificado,urlsincrona,urlasincrona,actionsincrona,actionasincrona,actionsolicitud,versionesquema,tiposeguridad,prefijopeticion,xpathcifradosincrono,xpathcifradoasincrono,esquemas,clavefirma,clavecifrado,algoritmocifrado,numeromaximoreenvios,maxsolicitudespeticion,prefijoidtransmision,descripcion,emisor,fechaalta,fechabaja,caducidad,xpathliteralerror,xpathcodigoerror,timeout,validacionfirma,plantillaXSLT) VALUES ('DOMFISC','https://ws.ia.aeat.es/ES98/L/iniinvoc/es.aeat.dit.adu.suap.domicilioWS.DomFV3SOAP',NULL,'operacion_servicio',NULL,NULL,'V3','XMLSignature','PINBAL',NULL,NULL,'/schemas/DOMFISCv3','ALIAS-CERTIFICADO-FIRMA',NULL,NULL,0,0,NULL,'Domicilio Fiscal v3','Q2826000H',CURRENT_TIMESTAMP,NULL,0,'//*[local-name()=''DescRespuesta'']','//*[local-name()=''CodRespuesta'']',60,null,'/plantillaspdf/aeat/plantilla_DomicilioFiscal_DOMFISC.xslt');

-- Actualizo los datos del pooling
UPDATE core_parametro_configuracion SET valor='5', descripcion='Minutos a esperar para lanzar el polling desde arranque servidor' WHERE nombre LIKE 'task.polling.espera';
UPDATE core_parametro_configuracion SET valor='1440' WHERE nombre LIKE 'task.polling.intervalo';

--                    --
--  PINBAL 1.2 a 1.3  --
--                    --
merge into organismo_cesionario oc
using ( select nom, cif, createddate
        from pbl_entitat ) pe
on ( oc.cif = pe.cif )
when not matched then
insert (id, nombre, cif, fechaalta, bloqueado )
values ( id_organismo_cesionario_seq.nextval, pe.nom, pe.cif, pe.createddate, 0 );

insert into servicio_autorizado_cesionario (
	id,
	fechaalta,
	servicio,
	organismo,
	bloqueado
) select
	id_servicio_cesionario_seq.nextval,
	es.createddate,
	(select id from core_servicio cs where cs.codcertificado = es.servei_id) servicio_id,
	(select id from organismo_cesionario oc where oc.cif = en.cif) organismo_id,
	0
from
	pbl_entitat_servei es,
	pbl_entitat en
where es.entitat_id = en.id;

update
	servicio_autorizado_cesionario sac
set
	claveprivada = (select clavefirma from core_servicio cs where cs.id = sac.servicio);
	
-- Nou camp d'ajuda als serveis --
ALTER TABLE PBL_SERVEI_CONFIG ADD AJUDA CLOB;
ALTER TABLE PBL_SERVEI_CONFIG move lob (AJUDA) store as PBL_SERVEI_CONFIG_CONT_LOB (tablespace PINBAL_LOB);
