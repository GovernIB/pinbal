--------------------------------------------------------
--  Creacion de esquema scsp requirente 
--------------------------------------------------------

--------------------------------------------------------
--  DDL for Table SCSP_CODIGO_ERROR
--------------------------------------------------------

  CREATE TABLE "SCSP_CODIGO_ERROR" 
   (	
	"CODIGO" VARCHAR2(4 CHAR), 
	"DESCRIPCION" VARCHAR2(1024 CHAR)
   );
 
--------------------------------------------------------
--  DDL for Table CORE_TRANSMISION
--------------------------------------------------------

  CREATE TABLE "CORE_TRANSMISION" 
   (	
	"IDSOLICITUD" VARCHAR2(64 CHAR), 
	"IDPETICION" VARCHAR2(26 CHAR), 
	"IDTRANSMISION" VARCHAR2(64 CHAR), 
	"IDSOLICITANTE" VARCHAR2(10 CHAR), 
	"NOMBRESOLICITANTE" VARCHAR2(256 CHAR), 
	"DOCTITULAR" VARCHAR2(16 CHAR), 
	"NOMBRETITULAR" VARCHAR2(40 CHAR),
	"APELLIDO1TITULAR" VARCHAR2(40 CHAR),
	"APELLIDO2TITULAR" VARCHAR2(40 CHAR),
	"NOMBRECOMPLETOTITULAR" VARCHAR2(122 CHAR),
	"DOCFUNCIONARIO" VARCHAR2(16 CHAR), 
	"NOMBREFUNCIONARIO" VARCHAR2(128 CHAR), 
	"FECHAGENERACION" TIMESTAMP (6), 
	"UNIDADTRAMITADORA" VARCHAR2(256 CHAR), 
	"CODIGOPROCEDIMIENTO" VARCHAR2(256 CHAR), 
	"NOMBREPROCEDIMIENTO" VARCHAR2(256 CHAR), 
	"EXPEDIENTE" VARCHAR2(256 CHAR), 
	"FINALIDAD" VARCHAR2(256 CHAR), 
	"CONSENTIMIENTO" VARCHAR2(3 CHAR), 
	"ESTADO" VARCHAR2(4 CHAR), 
	"ERROR" CLOB, 
	"XMLTRANSMISION" CLOB
   );
 
--------------------------------------------------------
--  DDL for Table CORE_TIPO_MENSAJE
--------------------------------------------------------

  CREATE TABLE "CORE_TIPO_MENSAJE" 
   (	
	"TIPO" NUMBER(1,0), 
	"DESCRIPCION" VARCHAR2(32 CHAR)
   );
 
--------------------------------------------------------
--  DDL for Table CORE_CACHE_CERTIFICADOS
--------------------------------------------------------

  CREATE TABLE "CORE_CACHE_CERTIFICADOS" 
   (	
	"NUMSERIE" VARCHAR2(256 CHAR), 
	"AUTORIDADCERTIF" VARCHAR2(512 CHAR), 
	"TIEMPOCOMPROBACION" TIMESTAMP (6), 
	"REVOCADO" NUMBER(10,0)
   );
 
--------------------------------------------------------
--  DDL for Table SCSP_ESTADO_PETICION
--------------------------------------------------------

  CREATE TABLE "SCSP_ESTADO_PETICION" 
   (	
	"CODIGO" VARCHAR2(4 CHAR), 
	"MENSAJE" VARCHAR2(256 CHAR)
   );
 
--------------------------------------------------------
--  DDL for Table CORE_MODULO
--------------------------------------------------------

  CREATE TABLE "CORE_MODULO" 
   (	
	"NOMBRE" VARCHAR2(256 CHAR), 
	"DESCRIPCION" VARCHAR2(512 CHAR), 
	"ACTIVOENTRADA" NUMBER(1,0), 
	"ACTIVOSALIDA" NUMBER(1,0)
   );
 
--------------------------------------------------------
--  DDL for Table CORE_MODULO_CONFIGURACION
--------------------------------------------------------

  CREATE TABLE "CORE_MODULO_CONFIGURACION" 
   (	
	"MODULO" VARCHAR2(256 CHAR), 
	"CERTIFICADO" VARCHAR2(64 CHAR), 
	"ACTIVOENTRADA" NUMBER(1,0), 
	"ACTIVOSALIDA" NUMBER(1,0)
   );
 
--------------------------------------------------------
--  DDL for Table CORE_CLAVE_PUBLICA
--------------------------------------------------------

  CREATE TABLE "CORE_CLAVE_PUBLICA" 
   (	
	"ALIAS" VARCHAR2(256 CHAR), 
	"NOMBRE" VARCHAR2(256 CHAR), 
	"NUMEROSERIE" VARCHAR2(256 CHAR)
   );
 
--------------------------------------------------------
--  DDL for Table CORE_SECUENCIA_IDPETICION
--------------------------------------------------------

  CREATE TABLE "CORE_SECUENCIA_IDPETICION" 
   (	
	"PREFIJO" VARCHAR2(8 CHAR), 
	"SECUENCIA" VARCHAR2(23 CHAR), 
	"FECHAGENERACION" TIMESTAMP (6)
   );
 
--------------------------------------------------------
--  DDL for Table CORE_PETICION_RESPUESTA
--------------------------------------------------------

  CREATE TABLE "CORE_PETICION_RESPUESTA" 
   (	
	"IDPETICION" VARCHAR2(26 CHAR), 
	"CERTIFICADO" VARCHAR2(64 CHAR), 
	"ESTADO" VARCHAR2(4 CHAR), 
	"FECHAPETICION" TIMESTAMP (6), 
	"FECHARESPUESTA" TIMESTAMP (6), 
	"TER" TIMESTAMP (6), 
	"ERROR" CLOB, 
	"NUMEROENVIOS" NUMBER(10,0), 
	"NUMEROTRANSMISIONES" NUMBER(10,0), 
	"FECHAULTIMOSONDEO" TIMESTAMP (6), 
	"TRANSMISIONSINCRONA" NUMBER(10,0), 
	"DESCOMPUESTA" VARCHAR2(1 CHAR)
   );
 
--------------------------------------------------------
--  DDL for Table CORE_TOKEN_DATA
--------------------------------------------------------

  CREATE TABLE "CORE_TOKEN_DATA" 
   (	
	"IDPETICION" VARCHAR2(26 CHAR), 
	"TIPOMENSAJE" NUMBER(1,0), 
	"DATOS" CLOB, 
	"CLAVE" VARCHAR2(256 CHAR), 
	"MODOENCRIPTACION" VARCHAR2(32 CHAR), 
	"ALGORITMOENCRIPTACION" VARCHAR2(32 CHAR)
   );
 
--------------------------------------------------------
--  DDL for Table CORE_PARAMETRO_CONFIGURACION
--------------------------------------------------------

  CREATE TABLE "CORE_PARAMETRO_CONFIGURACION" 
   (	
	"NOMBRE" VARCHAR2(64 CHAR), 
	"VALOR" VARCHAR2(512 CHAR), 
	"DESCRIPCION" VARCHAR2(512 CHAR)
   );
 

--------------------------------------------------------
--  DDL for Table CORE_EMISOR_CERTIFICADO
--------------------------------------------------------

  CREATE TABLE "CORE_EMISOR_CERTIFICADO" 
   (	
	"CIF" VARCHAR2(16 CHAR), 
	"NOMBRE" VARCHAR2(32 CHAR),
	"FECHABAJA" TIMESTAMP(6)
   );
 

--------------------------------------------------------
--  DDL for Table CORE_SERVICIO
--------------------------------------------------------

  CREATE TABLE "CORE_SERVICIO" 
   (	
	"CODCERTIFICADO" VARCHAR2(64 CHAR), 
	"URLSINCRONA" VARCHAR2(256 CHAR), 
	"URLASINCRONA" VARCHAR2(256 CHAR), 
	"ACTIONSINCRONA" VARCHAR2(256 CHAR), 
	"ACTIONASINCRONA" VARCHAR2(256 CHAR), 
	"ACTIONSOLICITUD" VARCHAR2(256 CHAR), 
	"VERSIONESQUEMA" VARCHAR2(32 CHAR), 
	"TIPOSEGURIDAD" VARCHAR2(16 CHAR), 
	"PREFIJOPETICION" VARCHAR2(8 CHAR), 
	"XPATHCIFRADOSINCRONO" VARCHAR2(256 CHAR), 
	"XPATHCIFRADOASINCRONO" VARCHAR2(256 CHAR), 
	"ESQUEMAS" VARCHAR2(256 CHAR), 
	"CLAVEFIRMA" VARCHAR2(256 CHAR), 
	"CLAVECIFRADO" VARCHAR2(256 CHAR), 
	"ALGORITMOCIFRADO" VARCHAR2(32 CHAR), 
	"NUMEROMAXIMOREENVIOS" NUMBER(10,0), 
	"MAXSOLICITUDESPETICION" NUMBER(10,0), 
	"PREFIJOIDTRANSMISION" VARCHAR2(8 CHAR), 
	"DESCRIPCION" VARCHAR2(512 CHAR), 
	"EMISOR" VARCHAR2(16 CHAR), 
	"FECHAALTA" TIMESTAMP (6), 
	"FECHABAJA" TIMESTAMP (6), 
	"CADUCIDAD" NUMBER(10,0),
	"XPATHLITERALERROR" VARCHAR2(256 CHAR),
	"XPATHCODIGOERROR" VARCHAR2(256 CHAR),
	"TIMEOUT" NUMBER(10,0) DEFAULT 60,
	"VALIDACIONFIRMA" VARCHAR2(32 CHAR) DEFAULT NULL
   );
 
--------------------------------------------------------
--  DDL for Table CORE_CLAVE_PRIVADA
--------------------------------------------------------

  CREATE TABLE "CORE_CLAVE_PRIVADA" 
   (	
	"ALIAS" VARCHAR2(256 CHAR), 
	"NOMBRE" VARCHAR2(256 CHAR), 
	"PASSWORD" VARCHAR2(256 CHAR), 
	"NUMEROSERIE" VARCHAR2(256 CHAR)
   );
 
--------------------------------------------------------
--  DDL for Table CORE_LOG
--------------------------------------------------------

  CREATE TABLE "CORE_LOG" 
   (	
	"ID" NUMBER(19,0), 
	"FECHA" TIMESTAMP (6), 
	"CRITICIDAD" VARCHAR2(10 CHAR), 
	"CLASE" VARCHAR2(256 CHAR), 
	"METODO" VARCHAR2(64 CHAR), 
	"MENSAJE" CLOB
   );
 
--------------------------------------------------------
--  Procedimiento Almacenado GETSECUENCIAIDPETICION
--  Creación del procedimiento almacenado para generar las secuencias del id de petición
--------------------------------------------------------

CREATE OR REPLACE PROCEDURE "GETSECUENCIAIDPETICION"  (  prefijo_param in varchar2, on_Secuencial out number)as  rRegistro ROWID;

begin  
    select ROWID, SECUENCIA+1 into rRegistro, on_Secuencial from CORE_SECUENCIA_IDPETICION where PREFIJO = prefijo_param for update;
    update CORE_SECUENCIA_IDPETICION  set SECUENCIA = on_Secuencial, FECHAGENERACION=sysdate  where rowid = rRegistro;  
	commit; 
    exception when no_data_found then on_Secuencial := 1;    
    insert into CORE_SECUENCIA_IDPETICION (PREFIJO, SECUENCIA,FECHAGENERACION) values (prefijo_param, on_Secuencial,(SELECT SYSDATE FROM DUAL)); 
commit; 
end;

--------------------------------------------------------
--  Secuencia ID_LOG_SEQUENCE
--------------------------------------------------------

CREATE SEQUENCE ID_LOG_SEQUENCE START WITH     1 INCREMENT BY   1 NOCACHE NOCYCLE;

--------------------------------------------------------
--  Constraints for Table SCSP_CODIGO_ERROR
--------------------------------------------------------

  ALTER TABLE "SCSP_CODIGO_ERROR" MODIFY ("CODIGO" NOT NULL ENABLE);
  ALTER TABLE "SCSP_CODIGO_ERROR" MODIFY ("DESCRIPCION" NOT NULL ENABLE);
  ALTER TABLE "SCSP_CODIGO_ERROR" ADD PRIMARY KEY ("CODIGO");
--------------------------------------------------------
--  Constraints for Table CORE_TRANSMISION
--------------------------------------------------------

  ALTER TABLE "CORE_TRANSMISION" MODIFY ("IDSOLICITUD" NOT NULL ENABLE);
  ALTER TABLE "CORE_TRANSMISION" MODIFY ("IDPETICION" NOT NULL ENABLE);
  ALTER TABLE "CORE_TRANSMISION" MODIFY ("IDSOLICITANTE" NOT NULL ENABLE);
  ALTER TABLE "CORE_TRANSMISION" ADD PRIMARY KEY ("IDSOLICITUD", "IDPETICION");
--------------------------------------------------------
--  Constraints for Table CORE_TIPO_MENSAJE
--------------------------------------------------------

  ALTER TABLE "CORE_TIPO_MENSAJE" MODIFY ("TIPO" NOT NULL ENABLE);
  ALTER TABLE "CORE_TIPO_MENSAJE" MODIFY ("DESCRIPCION" NOT NULL ENABLE);
  ALTER TABLE "CORE_TIPO_MENSAJE" ADD PRIMARY KEY ("TIPO");
--------------------------------------------------------
--  Constraints for Table CORE_CACHE_CERTIFICADOS
--------------------------------------------------------

  ALTER TABLE "CORE_CACHE_CERTIFICADOS" MODIFY ("NUMSERIE" NOT NULL ENABLE);
  ALTER TABLE "CORE_CACHE_CERTIFICADOS" MODIFY ("AUTORIDADCERTIF" NOT NULL ENABLE);
  ALTER TABLE "CORE_CACHE_CERTIFICADOS" MODIFY ("TIEMPOCOMPROBACION" NOT NULL ENABLE);
  ALTER TABLE "CORE_CACHE_CERTIFICADOS" MODIFY ("REVOCADO" NOT NULL ENABLE);
  ALTER TABLE "CORE_CACHE_CERTIFICADOS" ADD PRIMARY KEY ("NUMSERIE", "AUTORIDADCERTIF");
--------------------------------------------------------
--  Constraints for Table SCSP_ESTADO_PETICION
--------------------------------------------------------

  ALTER TABLE "SCSP_ESTADO_PETICION" MODIFY ("CODIGO" NOT NULL ENABLE);
  ALTER TABLE "SCSP_ESTADO_PETICION" MODIFY ("MENSAJE" NOT NULL ENABLE);
  ALTER TABLE "SCSP_ESTADO_PETICION" ADD PRIMARY KEY ("CODIGO");
--------------------------------------------------------
--  Constraints for Table CORE_MODULO
--------------------------------------------------------

  ALTER TABLE "CORE_MODULO" MODIFY ("NOMBRE" NOT NULL ENABLE);
  ALTER TABLE "CORE_MODULO" MODIFY ("ACTIVOENTRADA" NOT NULL ENABLE);
  ALTER TABLE "CORE_MODULO" MODIFY ("ACTIVOSALIDA" NOT NULL ENABLE);
  ALTER TABLE "CORE_MODULO" ADD PRIMARY KEY ("NOMBRE");
--------------------------------------------------------
--  Constraints for Table CORE_MODULO_CONFIGURACION
--------------------------------------------------------

  ALTER TABLE "CORE_MODULO_CONFIGURACION" MODIFY ("MODULO" NOT NULL ENABLE);
  ALTER TABLE "CORE_MODULO_CONFIGURACION" MODIFY ("CERTIFICADO" NOT NULL ENABLE);
  ALTER TABLE "CORE_MODULO_CONFIGURACION" MODIFY ("ACTIVOENTRADA" NOT NULL ENABLE);
  ALTER TABLE "CORE_MODULO_CONFIGURACION" MODIFY ("ACTIVOSALIDA" NOT NULL ENABLE);
  ALTER TABLE "CORE_MODULO_CONFIGURACION" ADD PRIMARY KEY ("MODULO", "CERTIFICADO");
--------------------------------------------------------
--  Constraints for Table CORE_CLAVE_PUBLICA
--------------------------------------------------------

  ALTER TABLE "CORE_CLAVE_PUBLICA" MODIFY ("ALIAS" NOT NULL ENABLE);
  ALTER TABLE "CORE_CLAVE_PUBLICA" MODIFY ("NOMBRE" NOT NULL ENABLE);
  ALTER TABLE "CORE_CLAVE_PUBLICA" MODIFY ("NUMEROSERIE" NOT NULL ENABLE);
  ALTER TABLE "CORE_CLAVE_PUBLICA" ADD PRIMARY KEY ("ALIAS");
--------------------------------------------------------
--  Constraints for Table CORE_SECUENCIA_IDPETICION
--------------------------------------------------------

  ALTER TABLE "CORE_SECUENCIA_IDPETICION" MODIFY ("PREFIJO" NOT NULL ENABLE);
  ALTER TABLE "CORE_SECUENCIA_IDPETICION" MODIFY ("SECUENCIA" NOT NULL ENABLE);
  ALTER TABLE "CORE_SECUENCIA_IDPETICION" MODIFY ("FECHAGENERACION" NOT NULL ENABLE);
  ALTER TABLE "CORE_SECUENCIA_IDPETICION" ADD PRIMARY KEY ("PREFIJO");
--------------------------------------------------------
--  Constraints for Table CORE_PETICION_RESPUESTA
--------------------------------------------------------

  ALTER TABLE "CORE_PETICION_RESPUESTA" MODIFY ("IDPETICION" NOT NULL ENABLE);
  ALTER TABLE "CORE_PETICION_RESPUESTA" MODIFY ("CERTIFICADO" NOT NULL ENABLE);
  ALTER TABLE "CORE_PETICION_RESPUESTA" MODIFY ("ESTADO" NOT NULL ENABLE);
  ALTER TABLE "CORE_PETICION_RESPUESTA" MODIFY ("FECHAPETICION" NOT NULL ENABLE);
  ALTER TABLE "CORE_PETICION_RESPUESTA" MODIFY ("NUMEROENVIOS" NOT NULL ENABLE);
  ALTER TABLE "CORE_PETICION_RESPUESTA" MODIFY ("NUMEROTRANSMISIONES" NOT NULL ENABLE);
  ALTER TABLE "CORE_PETICION_RESPUESTA" ADD PRIMARY KEY ("IDPETICION");
--------------------------------------------------------
--  Constraints for Table CORE_TOKEN_DATA
--------------------------------------------------------

  ALTER TABLE "CORE_TOKEN_DATA" MODIFY ("IDPETICION" NOT NULL ENABLE);
  ALTER TABLE "CORE_TOKEN_DATA" MODIFY ("TIPOMENSAJE" NOT NULL ENABLE);
  ALTER TABLE "CORE_TOKEN_DATA" MODIFY ("DATOS" NOT NULL ENABLE);
  ALTER TABLE "CORE_TOKEN_DATA" ADD PRIMARY KEY ("IDPETICION", "TIPOMENSAJE");
--------------------------------------------------------
--  Constraints for Table CORE_PARAMETRO_CONFIGURACION
--------------------------------------------------------

  ALTER TABLE "CORE_PARAMETRO_CONFIGURACION" MODIFY ("NOMBRE" NOT NULL ENABLE);
  ALTER TABLE "CORE_PARAMETRO_CONFIGURACION" MODIFY ("VALOR" NOT NULL ENABLE);
  ALTER TABLE "CORE_PARAMETRO_CONFIGURACION" ADD PRIMARY KEY ("NOMBRE");
--------------------------------------------------------
--  Constraints for Table CORE_EMISOR_CERTIFICADO
--------------------------------------------------------

  ALTER TABLE "CORE_EMISOR_CERTIFICADO" MODIFY ("CIF" NOT NULL ENABLE);
  ALTER TABLE "CORE_EMISOR_CERTIFICADO" MODIFY ("NOMBRE" NOT NULL ENABLE);
  ALTER TABLE "CORE_EMISOR_CERTIFICADO" ADD PRIMARY KEY ("CIF");
--------------------------------------------------------
--  Constraints for Table CORE_SERVICIO
--------------------------------------------------------

  ALTER TABLE "CORE_SERVICIO" MODIFY ("CODCERTIFICADO" NOT NULL ENABLE);
  ALTER TABLE "CORE_SERVICIO" MODIFY ("VERSIONESQUEMA" NOT NULL ENABLE);
  ALTER TABLE "CORE_SERVICIO" MODIFY ("TIPOSEGURIDAD" NOT NULL ENABLE);
  ALTER TABLE "CORE_SERVICIO" MODIFY ("CLAVEFIRMA" NOT NULL ENABLE);
  ALTER TABLE "CORE_SERVICIO" MODIFY ("NUMEROMAXIMOREENVIOS" NOT NULL ENABLE);
  ALTER TABLE "CORE_SERVICIO" MODIFY ("MAXSOLICITUDESPETICION" NOT NULL ENABLE);
  ALTER TABLE "CORE_SERVICIO" MODIFY ("DESCRIPCION" NOT NULL ENABLE);
  ALTER TABLE "CORE_SERVICIO" MODIFY ("EMISOR" NOT NULL ENABLE);
  ALTER TABLE "CORE_SERVICIO" MODIFY ("FECHAALTA" NOT NULL ENABLE);
  ALTER TABLE "CORE_SERVICIO" MODIFY ("TIMEOUT" NOT NULL ENABLE);
  ALTER TABLE "CORE_SERVICIO" ADD PRIMARY KEY ("CODCERTIFICADO");
--------------------------------------------------------
--  Constraints for Table CORE_CLAVE_PRIVADA
--------------------------------------------------------

  ALTER TABLE "CORE_CLAVE_PRIVADA" MODIFY ("ALIAS" NOT NULL ENABLE);
  ALTER TABLE "CORE_CLAVE_PRIVADA" MODIFY ("NOMBRE" NOT NULL ENABLE);
  ALTER TABLE "CORE_CLAVE_PRIVADA" MODIFY ("PASSWORD" NOT NULL ENABLE);
  ALTER TABLE "CORE_CLAVE_PRIVADA" MODIFY ("NUMEROSERIE" NOT NULL ENABLE);
  ALTER TABLE "CORE_CLAVE_PRIVADA" ADD PRIMARY KEY ("ALIAS");
--------------------------------------------------------
--  Constraints for Table CORE_LOG
--------------------------------------------------------

  ALTER TABLE "CORE_LOG" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "CORE_LOG" ADD PRIMARY KEY ("ID");
--------------------------------------------------------
--  Ref Constraints for Table CORE_TRANSMISION
--------------------------------------------------------

  ALTER TABLE "CORE_TRANSMISION" ADD CONSTRAINT "FK4F5CAA5982700A73" FOREIGN KEY ("IDPETICION")
	  REFERENCES "CORE_PETICION_RESPUESTA" ("IDPETICION") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table CORE_MODULO_CONFIGURACION
--------------------------------------------------------

  ALTER TABLE "CORE_MODULO_CONFIGURACION" ADD CONSTRAINT "FK484613E9954BDAF" FOREIGN KEY ("CERTIFICADO")
	  REFERENCES "CORE_SERVICIO" ("CODCERTIFICADO") ENABLE;
  ALTER TABLE "CORE_MODULO_CONFIGURACION" ADD CONSTRAINT "FK484613EDF60E414" FOREIGN KEY ("MODULO")
	  REFERENCES "CORE_MODULO" ("NOMBRE") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table CORE_PETICION_RESPUESTA
--------------------------------------------------------

  ALTER TABLE "CORE_PETICION_RESPUESTA" ADD CONSTRAINT "FKE9D50B109954BDAF" FOREIGN KEY ("CERTIFICADO")
	  REFERENCES "CORE_SERVICIO" ("CODCERTIFICADO") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table CORE_TOKEN_DATA
--------------------------------------------------------

  ALTER TABLE "CORE_TOKEN_DATA" ADD CONSTRAINT "FK35573ED06007240E" FOREIGN KEY ("TIPOMENSAJE")
	  REFERENCES "CORE_TIPO_MENSAJE" ("TIPO") ENABLE;
  ALTER TABLE "CORE_TOKEN_DATA" ADD CONSTRAINT "FK35573ED082700A73" FOREIGN KEY ("IDPETICION")
	  REFERENCES "CORE_PETICION_RESPUESTA" ("IDPETICION") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table CORE_SERVICIO
--------------------------------------------------------

  ALTER TABLE "CORE_SERVICIO" ADD CONSTRAINT "FK991DB57647E36449" FOREIGN KEY ("EMISOR")
	  REFERENCES "CORE_EMISOR_CERTIFICADO" ("CIF") ENABLE;
  ALTER TABLE "CORE_SERVICIO" ADD CONSTRAINT "FK991DB57652F7688C" FOREIGN KEY ("CLAVECIFRADO")
	  REFERENCES "CORE_CLAVE_PUBLICA" ("ALIAS") ENABLE;
  ALTER TABLE "CORE_SERVICIO" ADD CONSTRAINT "FK991DB576E8B2996C" FOREIGN KEY ("CLAVEFIRMA")
	  REFERENCES "CORE_CLAVE_PRIVADA" ("ALIAS") ENABLE;
--------------------------------------------------------
--  Comments for Table SCSP_CODIGO_ERROR
--------------------------------------------------------

   COMMENT ON COLUMN "SCSP_CODIGO_ERROR"."CODIGO" IS 'Código identificativo del error';
   COMMENT ON COLUMN "SCSP_CODIGO_ERROR"."DESCRIPCION" IS 'Litreral descriptivo del error';
   COMMENT ON TABLE "SCSP_CODIGO_ERROR"  IS 'Tabla que registrará los posibles errores genéricos que toda comunicación SCSP puede generar';
--------------------------------------------------------
--  Comments for Table CORE_TRANSMISION
--------------------------------------------------------

   COMMENT ON COLUMN "CORE_TRANSMISION"."IDSOLICITUD" IS 'Identificador de la solicitud de transmisión dentro de las N posibles incluidas en una petición';
   COMMENT ON COLUMN "CORE_TRANSMISION"."IDPETICION" IS 'Indentificador de la petición en la que se incluyó la solicitud de transmisión';
   COMMENT ON COLUMN "CORE_TRANSMISION"."IDTRANSMISION" IS 'Indentificador de la transmisión que responde a la petición de servicio de la Solicitud de Transmisión identificada con idSolicitud';
   COMMENT ON COLUMN "CORE_TRANSMISION"."IDSOLICITANTE" IS 'CIF de organismo solicitante del servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."NOMBRESOLICITANTE" IS 'Nombre del organismo solicitante de servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."DOCTITULAR" IS 'Documento identificativo del titular sobre el cual se está realizando la petición de servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."NOMBRETITULAR" IS 'Nombre del titular sobre el que se realiza la petición del servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."APELLIDO1TITULAR" IS 'Primer apellido del titular sobre el que se realiza la petición del servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."APELLIDO2TITULAR" IS 'Segundo apellido del titular sobre el que se realiza la petición del servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."NOMBRECOMPLETOTITULAR" IS 'Nombre completo del titular sobre el que se realiza la petición del servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."DOCFUNCIONARIO" IS 'Documento identificativo del funcionario que generó la soliicitud de transmisión';
   COMMENT ON COLUMN "CORE_TRANSMISION"."NOMBREFUNCIONARIO" IS 'Nombre del Funcionario que generó la solicitud de transmisión';
   COMMENT ON COLUMN "CORE_TRANSMISION"."FECHAGENERACION" IS 'Fecha en la que se generó la transmisión';
   COMMENT ON COLUMN "CORE_TRANSMISION"."UNIDADTRAMITADORA" IS 'Unidad Tramitadora asociada a la solicitud de transmisión';
   COMMENT ON COLUMN "CORE_TRANSMISION"."CODIGOPROCEDIMIENTO" IS 'Código del procedimiento en base al cual se puede solicitar el servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."NOMBREPROCEDIMIENTO" IS 'Nombre del procedimiento en base al cual se puede solicitar el servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."EXPEDIENTE" IS 'Expediente asociado a la solicitud de transmsión';
   COMMENT ON COLUMN "CORE_TRANSMISION"."FINALIDAD" IS 'Finalidad por la cual se emitió la solicitud de transmisión';
   COMMENT ON COLUMN "CORE_TRANSMISION"."CONSENTIMIENTO" IS 'Tipo de consentimiento asociado a la transmisión. Deberá tomar uno de los dos posibles valores:  - Si -Ley';
   COMMENT ON COLUMN "CORE_TRANSMISION"."ESTADO" IS 'Estado concreto en el que se encuentra la transmisión';
   COMMENT ON COLUMN "CORE_TRANSMISION"."ERROR" IS 'Descripción del posible error encontrado al solicitar la transmisión de servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."XMLTRANSMISION" IS 'El XML de la transmisión. Su almacenamiento será opcional, dependiendo de un parametro global almacenado en la tabla core_parametro_configuracion';
   COMMENT ON TABLE "CORE_TRANSMISION"  IS 'Esta tabla almacenará la información específica de cada transmisión que haya sido incluida en una respuesta de un servicio SCSP';
--------------------------------------------------------
--  Comments for Table CORE_TIPO_MENSAJE
-------------------------------------------------------- 

   COMMENT ON COLUMN "CORE_TIPO_MENSAJE"."TIPO" IS 'Tipo identificativo del mensaje';
   COMMENT ON COLUMN "CORE_TIPO_MENSAJE"."DESCRIPCION" IS 'Literal descriptivo del tipo de mensaje';
   COMMENT ON TABLE "CORE_TIPO_MENSAJE"  IS 'Tabla maestra que almacena los diferentes tipos de mensajes que pueden ser intercambiados a lo largo de un ciclo de comunicación SCSP. Estos valores serán: 	Peticion 	Respuesta 	SolicitudRespuesta 	ConfirmaciónPeticion 	Fault';
--------------------------------------------------------
--  Comments for Table CORE_CACHE_CERTIFICADOS
--------------------------------------------------------

   COMMENT ON COLUMN "CORE_CACHE_CERTIFICADOS"."NUMSERIE" IS 'Número de serie del certificado';
   COMMENT ON COLUMN "CORE_CACHE_CERTIFICADOS"."AUTORIDADCERTIF" IS 'Autoridad certificadora que emitió el certificado';
   COMMENT ON COLUMN "CORE_CACHE_CERTIFICADOS"."TIEMPOCOMPROBACION" IS 'Fecha en la que se realizó el proceso de validación del certificado por última vez'; 
   COMMENT ON COLUMN "CORE_CACHE_CERTIFICADOS"."REVOCADO" IS 'Valor booleano (1 o 0) que indicará: -	1:   si el proceso de validación revocó el certificado. -	0: si el proceso de validación aceptó el certificado.';
   COMMENT ON TABLE "CORE_CACHE_CERTIFICADOS"  IS 'Tabla que registra el resultado de la validación de los certificados   empleados en la firma de los mensajes recibidos por el requirente o por el emisor. Así mismo se registra la fecha en la que se realizó dicha validación para poder calcular el periodo de tiempo en la que dicha validación esta vigente';
--------------------------------------------------------
--  Comments for Table SCSP_ESTADO_PETICION
--------------------------------------------------------

   COMMENT ON COLUMN "SCSP_ESTADO_PETICION"."CODIGO" IS 'Código identificativo del estado';
   COMMENT ON COLUMN "SCSP_ESTADO_PETICION"."MENSAJE" IS 'Literal descriptivo del estado';
   COMMENT ON TABLE "SCSP_ESTADO_PETICION"  IS 'Tabla que almacena los posibles estados en los que se puede encontrar una petición SCSP. Sus posibles valores serán: 	0001 - Pendiente 	0002 - En proceso 	0003 - Tramitada';
--------------------------------------------------------
--  Comments for Table CORE_MODULO
--------------------------------------------------------

   COMMENT ON COLUMN "CORE_MODULO"."NOMBRE" IS 'Nombre del módulo';
   COMMENT ON COLUMN "CORE_MODULO"."DESCRIPCION" IS 'Literal descriptivo del modulo a configurar su activación';
   COMMENT ON COLUMN "CORE_MODULO"."ACTIVOENTRADA" IS 'Valor que indica si el módulo esta activo en la emisión de mensajes. Puede tomar valor 1 (Activo) ó 0 (Inactivo)';
   COMMENT ON COLUMN "CORE_MODULO"."ACTIVOSALIDA" IS 'Valor que indica si el módulo esta activo en la recepción de mensajes. Puede tomar valor 1 (Activo) ó 0 (Inactivo)';
   COMMENT ON TABLE "CORE_MODULO"  IS 'Tabla que almacena la configuración de activación  o desactivación de los módulos que componen el ciclo de ejecución de las librerías SCSP';
--------------------------------------------------------
--  Comments for Table CORE_MODULO_CONFIGURACION
--------------------------------------------------------

   COMMENT ON COLUMN "CORE_MODULO_CONFIGURACION"."MODULO" IS 'Nombre del módulo a configurar';
   COMMENT ON COLUMN "CORE_MODULO_CONFIGURACION"."CERTIFICADO" IS 'Código del certificado solicitado';
   COMMENT ON COLUMN "CORE_MODULO_CONFIGURACION"."ACTIVOENTRADA" IS 'Valor que indica si el módulo esta activo en la emisión de mensajes. Puede tomar valor 1 (Activo) ó 0 (Inactivo)';
   COMMENT ON COLUMN "CORE_MODULO_CONFIGURACION"."ACTIVOSALIDA" IS 'Valor que indica si el módulo esta activo en la recepción de mensajes. Puede tomar valor 1 (Activo) ó 0 (Inactivo)';
   COMMENT ON TABLE "CORE_MODULO_CONFIGURACION"  IS 'Tabla que permite la sobreescritura de la configuración de activación de un módulo para un servicio concreto';
--------------------------------------------------------
--  Comments for Table CORE_CLAVE_PUBLICA
--------------------------------------------------------
 
   COMMENT ON COLUMN "CORE_CLAVE_PUBLICA"."ALIAS" IS 'Alias que identifica de manera unívoca a la clave pública dentro del almacén de certificados que haya sido configurado en la tabla core_parametro_configuracion bajo el nombre keystoreFile';
   COMMENT ON COLUMN "CORE_CLAVE_PUBLICA"."NOMBRE" IS 'Nombre descriptivo de la clave pública';
   COMMENT ON COLUMN "CORE_CLAVE_PUBLICA"."NUMEROSERIE" IS 'Numero de serie de la clave publica';
   COMMENT ON TABLE "CORE_CLAVE_PUBLICA"  IS 'Esta tabla almacenará los datos de configuración necesarios para acceder a las claves públicas disponibles en el almacén de certificados configurado. Las claves públicas  aquí configuradas serán utilizadas para el posible cifrado de los  mensajes emitidos';
--------------------------------------------------------
--  Comments for Table CORE_SECUENCIA_IDPETICION
-------------------------------------------------------- 

   COMMENT ON COLUMN "CORE_SECUENCIA_IDPETICION"."PREFIJO" IS 'Prefijo utilizado para la construccion de los identificadores. Dicho valor podrá ser el prefijo especificado a ser utilizado para cada servicio o ante la no existencia del mismo, el número de serie del certificado digital firmante  de los mensajes';
   COMMENT ON COLUMN "CORE_SECUENCIA_IDPETICION"."SECUENCIA" IS 'Valor secuencial que será concatenado al prefijo para generar los identificadores de petición. Este secuencial será de tipo alfanumérico, de tal forma que el siguiente valor a 00000009 sería 0000000A';
   COMMENT ON COLUMN "CORE_SECUENCIA_IDPETICION"."FECHAGENERACION" IS 'Fecha en la que se registró  el secuencial';
   COMMENT ON TABLE "CORE_SECUENCIA_IDPETICION"  IS 'Tabla que almacena las semillas (secuenciales) utilizadas para la generación de los identificadores de peticion. Existirá un secuencial asociado a cada posible prefijo o número de serie de certificado digital firmante';
--------------------------------------------------------
--  Comments for Table CORE_PETICION_RESPUESTA
--------------------------------------------------------

   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."IDPETICION" IS 'Identificador unívoco de la petición de servicio';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."CERTIFICADO" IS 'Código del certificado que se solicita en la petición';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."ESTADO" IS 'Codigo identificativo del estado de la petición. Tomará sus valores de las tablas scsp_estado_peticion y scsp_codigo_error';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."FECHAPETICION" IS 'Timestamp que indica la fecha en la que se generó la petición';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."FECHARESPUESTA" IS 'Timestamp que indica la fecha en la cual se recibió la respuesta a nuestra petición'; 
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."TER" IS 'Timestamp que indica la fecha a partir de la cual una petición de tipo asíncrono podrá solicitar una respuesta definitiva al servicio';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."ERROR" IS 'Mensaje descriptivo del error, si lo hubiera, en la solicitud del servicio';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."NUMEROENVIOS" IS 'Valor que indica el número de veces que se ha reenviado una petición al servicio';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."NUMEROTRANSMISIONES" IS 'Número de Solicitudes de Transmisión que se enviaron en la petición';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."FECHAULTIMOSONDEO" IS 'Timestamp que indica la fecha del último reenvio de una petición de tipo asincrono';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."TRANSMISIONSINCRONA" IS 'Valor binario que indica si la petición fue solicitada a un servicio de tipo síncrono o asincrono. Podrá tomar los valores: 0: La comunicación fue de tipo asíncrono, 1: La comunicación fue de tipo síncrono';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."DESCOMPUESTA" IS 'Caracter que indica el estado del procesamiento de las transmisiones de la respuesta recibida. Podrá tomar los siguientes valores:    - S: Se ha procesado correctamente la respuesta, habiendo obtenido todas las transmisiones en ella incluidas y registradas en la tabla core_transmision. - N: No ha sido procesadas las transmisiones de la respuesta. - E: La respuesta terminó correctamente  (estado 0003), pero se ha producido un error al procesar sus trasmisiones';
   COMMENT ON TABLE "CORE_PETICION_RESPUESTA"  IS 'Esta tabla registrará un histórico de las peticiones y respuestas intercambiadas entre los requirentes y los emisores de servicios';
--------------------------------------------------------
--  Comments for Table CORE_TOKEN_DATA
--------------------------------------------------------
 
   COMMENT ON COLUMN "CORE_TOKEN_DATA"."IDPETICION" IS 'Identificador de la petición a la cual está asociado el XML';
   COMMENT ON COLUMN "CORE_TOKEN_DATA"."TIPOMENSAJE" IS 'Tipo de mensaje almacenado, podrá ser : -Peticion -Respuesta -SolicitudRespuesta -ConfirmaciónPeticion -Fault';
   COMMENT ON COLUMN "CORE_TOKEN_DATA"."DATOS" IS 'Bytes del mensaje almacenado';
   COMMENT ON COLUMN "CORE_TOKEN_DATA"."CLAVE" IS 'Clave simétrica utilizada para el cifrado de los datos';
   COMMENT ON COLUMN "CORE_TOKEN_DATA"."MODOENCRIPTACION" IS 'Modo de encriptación utilizado para el proceso de cifrado.  Por defecto será TransportKey';
   COMMENT ON COLUMN "CORE_TOKEN_DATA"."ALGORITMOENCRIPTACION" IS 'Algoritmo empleado en la encriptación del mensaje. Podrá tomar los siguientes valores: - AES128 - AES256 -DESDe';
   COMMENT ON TABLE "CORE_TOKEN_DATA"  IS 'Esta tabla almacenará el contenido de los mensajes intercambiados en un proceso de comunicación SCSP';
--------------------------------------------------------
--  Comments for Table CORE_PARAMETRO_CONFIGURACION
--------------------------------------------------------

   COMMENT ON COLUMN "CORE_PARAMETRO_CONFIGURACION"."NOMBRE" IS 'Nombre identificativo del parámetro';
   COMMENT ON COLUMN "CORE_PARAMETRO_CONFIGURACION"."VALOR" IS 'Valor del parámetro';
   COMMENT ON COLUMN "CORE_PARAMETRO_CONFIGURACION"."DESCRIPCION" IS 'Literal descriptivo de la utilidad del parámetro';
   COMMENT ON TABLE "CORE_PARAMETRO_CONFIGURACION"  IS 'Tabla  que almacena aquellos parámetros de configuración que son globales a todos los servicios y entornos para un mismo cliente integrador de las librerias SCSP';
--------------------------------------------------------
--  Comments for Table CORE_EMISOR_CERTIFICADO
--------------------------------------------------------

   COMMENT ON COLUMN "CORE_EMISOR_CERTIFICADO"."CIF" IS 'Nombre descriptivo del organismo emisor de servicios';
   COMMENT ON COLUMN "CORE_EMISOR_CERTIFICADO"."NOMBRE" IS 'CIF identificativo del organismo emisor de servicios';
   COMMENT ON COLUMN "CORE_EMISOR_CERTIFICADO"."FECHABAJA" IS 'Fecha de Baja de emisores de certificados';
   COMMENT ON TABLE "CORE_EMISOR_CERTIFICADO"  IS 'Esta tabla almacenará los diferentes emisores de servicio configurados';
--------------------------------------------------------
--  Comments for Table CORE_SERVICIO
--------------------------------------------------------

   COMMENT ON COLUMN "CORE_SERVICIO"."CODCERTIFICADO" IS 'Código del certificado que lo identifica unívocamente en las comunicaciones SCSP';
   COMMENT ON COLUMN "CORE_SERVICIO"."URLSINCRONA" IS 'Endpoint de acceso al servicio de tipo síncrono';
   COMMENT ON COLUMN "CORE_SERVICIO"."URLASINCRONA" IS 'Endpoint de acceso al servicio de tipo asíncrono del certificado';
   COMMENT ON COLUMN "CORE_SERVICIO"."ACTIONSINCRONA" IS 'Valor del Soap:Action de la petición síncrona utilizado por el servidor WS en el caso de que sea necesario';
   COMMENT ON COLUMN "CORE_SERVICIO"."ACTIONASINCRONA" IS 'Valor del Soap:Action de la petición asíncrona utilizado por el servidor WS en el caso de que sea necesario';
   COMMENT ON COLUMN "CORE_SERVICIO"."ACTIONSOLICITUD" IS 'Valor del Soap:Action de la solicitud asíncrona de respuesta utilizado por el servidor WS en el caso de que sea necesario';
   COMMENT ON COLUMN "CORE_SERVICIO"."VERSIONESQUEMA" IS 'Indica la versión de esquema utilizado en los mensajes SCSP pudiendo tomar los valores V2 y V3 (existe la posibilidad de añadir otros valores utilizando configuraciones avandadas de binding)';
   COMMENT ON COLUMN "CORE_SERVICIO"."TIPOSEGURIDAD" IS 'Indica la politica de seguridad utilizada en la securización de los mensajes, pudiendo tomar los valores [XMLSignature| WS-Security]';
   COMMENT ON COLUMN "CORE_SERVICIO"."PREFIJOPETICION" IS 'Literal con una longitud máxima de 8 caracteres, el cual será utilizado para la construcción de los identificadores de petición, anteponiendose a un valor secuencial. Mediante este literal pueden personalizarse los identificadores de petición haciendolos más descriptivos';
   COMMENT ON COLUMN "CORE_SERVICIO"."XPATHCIFRADOSINCRONO" IS 'Literal que identifica el nodo del mensaje XML a cifrar en caso de que los mensajes intercambiados con el emisor de servicio viajasen cifrados. Se corresponde con una expresión xpath que permite localizar  al nodo en el mensaje XML, presentará el siguiente formato //*[local-name()=ŽNODO_A_CIFRARŽ], donde ŽNODO_A_CIFRARŽ es el local name del nodo que se desea cifrar. Este nodo será empleado en el caso de realizar comunicaciones síncronas';
   COMMENT ON COLUMN "CORE_SERVICIO"."XPATHCIFRADOASINCRONO" IS 'Literal que identifica el nodo del mensaje XML a cifrar en caso de que los mensajes intercambiados con el emisor de servicio viajasen cifrados. Se corresponde con una expresión xpath que permite localizar  al nodo en el mensaje XML, presentará el siguiente formato //*[local-name()=ŽNODO_A_CIFRARŽ], donde ŽNODO_A_CIFRARŽ es el local name del nodo que se desea cifrar. Este nodo será empleado en el caso de realizar comunicaciones asíncronas';
   COMMENT ON COLUMN "CORE_SERVICIO"."ESQUEMAS" IS 'Ruta que indica el directorio donde se encuentran los esquemas (*.xsd) con el que se validará el XML de los diferentes mensajes intercambiados. Esta ruta podrá tomar un valor relativo haciendo referencia al classpath de la aplicación o  un path absoluto';
   COMMENT ON COLUMN "CORE_SERVICIO"."CLAVEFIRMA" IS 'Alias,  que identifica univocamente en el almacén de certificados configurado, la clave privada con la que se firmarán los mensajes';
   COMMENT ON COLUMN "CORE_SERVICIO"."CLAVECIFRADO" IS 'Alias,  que identifica univocamente en el almacén de certificados configurado, el certificado de clave pública con el que se cifrarían los mensajes enviados al emisor de servicios, en el caso de que se desease encriptación';
   COMMENT ON COLUMN "CORE_SERVICIO"."ALGORITMOCIFRADO" IS 'Literal que identifica el algoritmo utilizado para el cifrado de los mensajes enviados al emisor. Debe poseer un valor reconocido por las librerías de Rampart: - Basic128Rsa15 - TripleDesRsa15  - Basic256Rsa15';
   COMMENT ON COLUMN "CORE_SERVICIO"."NUMEROMAXIMOREENVIOS" IS 'Valor que indica en el caso del requirente, el número máximo de reenvios que pueden llevarse a cabo sobre una petición asíncrona. En el caso del emisor, hace referencia al número máximo de veces que procesará y creará una respuesta para una petición con un mismo identificador';
   COMMENT ON COLUMN "CORE_SERVICIO"."MAXSOLICITUDESPETICION" IS 'Número máximo de solicitudes de transmisión que se van a permitir por petición';
   COMMENT ON COLUMN "CORE_SERVICIO"."PREFIJOIDTRANSMISION" IS 'Semilla empleada por el emisor para generar los identificadores de transmisión de las respuestas. Será un valor alfanumérico con un mínimo de 3 caracteres y un máximo de 8';
   COMMENT ON COLUMN "CORE_SERVICIO"."DESCRIPCION" IS 'Literal descriptivo del servicio a solicitar utilizando el código de certificado';
   COMMENT ON COLUMN "CORE_SERVICIO"."EMISOR" IS 'Nombre del emisor del certificado';
   COMMENT ON COLUMN "CORE_SERVICIO"."FECHAALTA" IS 'Timestamp con la fecha en la cual el certificado se dió de alta en el sistema y por lo tanto a partir de la cual se podrán emitir peticiones al mismo';
   COMMENT ON COLUMN "CORE_SERVICIO"."FECHABAJA" IS 'Timestamp con la fecha en la cual el certificado se dio de baja en el sistema y por tanto a partir de la cual no se podrán emitir peticiones al mismo';
   COMMENT ON COLUMN "CORE_SERVICIO"."CADUCIDAD" IS 'Número de dias que deberán sumarse a la fecharespuesta de una petición, para calcular la fecha a partir de la cual se podrá considerar que la respuesta esta caducada y se devolvera el error scsp correspondiente para indicar que la respuesta ha perdido su valor';
   COMMENT ON COLUMN "CORE_SERVICIO"."XPATHLITERALERROR" IS 'Xpath para recuperar el literal del error de los datos específicos';
   COMMENT ON COLUMN "CORE_SERVICIO"."XPATHCODIGOERROR" IS 'Xpath para recuperar el codigo de error de los datos específicos';
   COMMENT ON COLUMN "CORE_SERVICIO"."TIMEOUT" IS 'Timeout que se establecerá para el envío de las peticiones a los servicios';
   COMMENT ON COLUMN "CORE_SERVICIO"."VALIDACIONFIRMA" IS 'Parámetro que indica si se admite otro tipo de firma en el servicio además del configurado';
   COMMENT ON TABLE "CORE_SERVICIO"  IS 'Esta tabla registrará cada uno de los certificados SCSP que van a ser utilizados por el sistema tanto de la parte requirente como de la parte emisora';
--------------------------------------------------------
--  Comments for Table CORE_CLAVE_PRIVADA
--------------------------------------------------------

   COMMENT ON COLUMN "CORE_CLAVE_PRIVADA"."ALIAS" IS 'Alias que identifica de manera unívoca a la clave privada dentro del almacén de certificados que haya sido configurado en la tabla core_parametro_configuracion bajo el nombre keystoreFile';
   COMMENT ON COLUMN "CORE_CLAVE_PRIVADA"."NOMBRE" IS 'Nombre descriptivo de la clave privada';
   COMMENT ON COLUMN "CORE_CLAVE_PRIVADA"."PASSWORD" IS 'Password de la clave privada necesaria para hacer uso de la misma';
   COMMENT ON COLUMN "CORE_CLAVE_PRIVADA"."NUMEROSERIE" IS 'Numero de serie de la clave privada';
   COMMENT ON TABLE "CORE_CLAVE_PRIVADA"  IS 'Esta tabla almacenará los datos de configuración necesarios para acceder a las claves privadas disponibles en el almacén de certificados configurado. Las claves privadas aquí configuradas serán utilizadas para la firma de los mensajes emitidos';
--------------------------------------------------------
--  Comments for Table CORE_LOG
-------------------------------------------------------- 

   COMMENT ON COLUMN "CORE_LOG"."ID" IS 'Valor incremental autogenerado';
   COMMENT ON COLUMN "CORE_LOG"."FECHA" IS 'Fecha  en la que se generó la traza de log';
   COMMENT ON COLUMN "CORE_LOG"."CRITICIDAD" IS 'Tipo de nivel de la traza de log (WARN,INFO,DEBUG,ALL,ERROR)';
   COMMENT ON COLUMN "CORE_LOG"."CLASE" IS 'Clase que generó la traza de log registrada';
   COMMENT ON COLUMN "CORE_LOG"."METODO" IS 'Método específico de la clase que generó la traza de log';
   COMMENT ON COLUMN "CORE_LOG"."MENSAJE" IS 'Literal descriptivo del error almacenado';
   COMMENT ON TABLE "CORE_LOG"  IS 'Tabla de configurada en el appender log4j para base de datos de la aplicación, donde se registrarán las posibles trazas de error';