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
--  Creaci�n del procedimiento almacenado para generar las secuencias del id de petici�n
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

   COMMENT ON COLUMN "SCSP_CODIGO_ERROR"."CODIGO" IS 'C�digo identificativo del error';
   COMMENT ON COLUMN "SCSP_CODIGO_ERROR"."DESCRIPCION" IS 'Litreral descriptivo del error';
   COMMENT ON TABLE "SCSP_CODIGO_ERROR"  IS 'Tabla que registrar� los posibles errores gen�ricos que toda comunicaci�n SCSP puede generar';
--------------------------------------------------------
--  Comments for Table CORE_TRANSMISION
--------------------------------------------------------

   COMMENT ON COLUMN "CORE_TRANSMISION"."IDSOLICITUD" IS 'Identificador de la solicitud de transmisi�n dentro de las N posibles incluidas en una petici�n';
   COMMENT ON COLUMN "CORE_TRANSMISION"."IDPETICION" IS 'Indentificador de la petici�n en la que se incluy� la solicitud de transmisi�n';
   COMMENT ON COLUMN "CORE_TRANSMISION"."IDTRANSMISION" IS 'Indentificador de la transmisi�n que responde a la petici�n de servicio de la Solicitud de Transmisi�n identificada con idSolicitud';
   COMMENT ON COLUMN "CORE_TRANSMISION"."IDSOLICITANTE" IS 'CIF de organismo solicitante del servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."NOMBRESOLICITANTE" IS 'Nombre del organismo solicitante de servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."DOCTITULAR" IS 'Documento identificativo del titular sobre el cual se est� realizando la petici�n de servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."NOMBRETITULAR" IS 'Nombre del titular sobre el que se realiza la petici�n del servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."APELLIDO1TITULAR" IS 'Primer apellido del titular sobre el que se realiza la petici�n del servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."APELLIDO2TITULAR" IS 'Segundo apellido del titular sobre el que se realiza la petici�n del servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."NOMBRECOMPLETOTITULAR" IS 'Nombre completo del titular sobre el que se realiza la petici�n del servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."DOCFUNCIONARIO" IS 'Documento identificativo del funcionario que gener� la soliicitud de transmisi�n';
   COMMENT ON COLUMN "CORE_TRANSMISION"."NOMBREFUNCIONARIO" IS 'Nombre del Funcionario que gener� la solicitud de transmisi�n';
   COMMENT ON COLUMN "CORE_TRANSMISION"."FECHAGENERACION" IS 'Fecha en la que se gener� la transmisi�n';
   COMMENT ON COLUMN "CORE_TRANSMISION"."UNIDADTRAMITADORA" IS 'Unidad Tramitadora asociada a la solicitud de transmisi�n';
   COMMENT ON COLUMN "CORE_TRANSMISION"."CODIGOPROCEDIMIENTO" IS 'C�digo del procedimiento en base al cual se puede solicitar el servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."NOMBREPROCEDIMIENTO" IS 'Nombre del procedimiento en base al cual se puede solicitar el servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."EXPEDIENTE" IS 'Expediente asociado a la solicitud de transmsi�n';
   COMMENT ON COLUMN "CORE_TRANSMISION"."FINALIDAD" IS 'Finalidad por la cual se emiti� la solicitud de transmisi�n';
   COMMENT ON COLUMN "CORE_TRANSMISION"."CONSENTIMIENTO" IS 'Tipo de consentimiento asociado a la transmisi�n. Deber� tomar uno de los dos posibles valores:  - Si -Ley';
   COMMENT ON COLUMN "CORE_TRANSMISION"."ESTADO" IS 'Estado concreto en el que se encuentra la transmisi�n';
   COMMENT ON COLUMN "CORE_TRANSMISION"."ERROR" IS 'Descripci�n del posible error encontrado al solicitar la transmisi�n de servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."XMLTRANSMISION" IS 'El XML de la transmisi�n. Su almacenamiento ser� opcional, dependiendo de un parametro global almacenado en la tabla core_parametro_configuracion';
   COMMENT ON TABLE "CORE_TRANSMISION"  IS 'Esta tabla almacenar� la informaci�n espec�fica de cada transmisi�n que haya sido incluida en una respuesta de un servicio SCSP';
--------------------------------------------------------
--  Comments for Table CORE_TIPO_MENSAJE
-------------------------------------------------------- 

   COMMENT ON COLUMN "CORE_TIPO_MENSAJE"."TIPO" IS 'Tipo identificativo del mensaje';
   COMMENT ON COLUMN "CORE_TIPO_MENSAJE"."DESCRIPCION" IS 'Literal descriptivo del tipo de mensaje';
   COMMENT ON TABLE "CORE_TIPO_MENSAJE"  IS 'Tabla maestra que almacena los diferentes tipos de mensajes que pueden ser intercambiados a lo largo de un ciclo de comunicaci�n SCSP. Estos valores ser�n: �	Peticion �	Respuesta �	SolicitudRespuesta �	Confirmaci�nPeticion �	Fault';
--------------------------------------------------------
--  Comments for Table CORE_CACHE_CERTIFICADOS
--------------------------------------------------------

   COMMENT ON COLUMN "CORE_CACHE_CERTIFICADOS"."NUMSERIE" IS 'N�mero de serie del certificado';
   COMMENT ON COLUMN "CORE_CACHE_CERTIFICADOS"."AUTORIDADCERTIF" IS 'Autoridad certificadora que emiti� el certificado';
   COMMENT ON COLUMN "CORE_CACHE_CERTIFICADOS"."TIEMPOCOMPROBACION" IS 'Fecha en la que se realiz� el proceso de validaci�n del certificado por �ltima vez'; 
   COMMENT ON COLUMN "CORE_CACHE_CERTIFICADOS"."REVOCADO" IS 'Valor booleano (�1� o �0�) que indicar�: -	�1�:   si el proceso de validaci�n revoc� el certificado. -	�0�: si el proceso de validaci�n acept� el certificado.';
   COMMENT ON TABLE "CORE_CACHE_CERTIFICADOS"  IS 'Tabla que registra el resultado de la validaci�n de los certificados   empleados en la firma de los mensajes recibidos por el requirente o por el emisor. As� mismo se registra la fecha en la que se realiz� dicha validaci�n para poder calcular el periodo de tiempo en la que dicha validaci�n esta vigente';
--------------------------------------------------------
--  Comments for Table SCSP_ESTADO_PETICION
--------------------------------------------------------

   COMMENT ON COLUMN "SCSP_ESTADO_PETICION"."CODIGO" IS 'C�digo identificativo del estado';
   COMMENT ON COLUMN "SCSP_ESTADO_PETICION"."MENSAJE" IS 'Literal descriptivo del estado';
   COMMENT ON TABLE "SCSP_ESTADO_PETICION"  IS 'Tabla que almacena los posibles estados en los que se puede encontrar una petici�n SCSP. Sus posibles valores ser�n: �	0001 - Pendiente �	0002 - En proceso �	0003 - Tramitada';
--------------------------------------------------------
--  Comments for Table CORE_MODULO
--------------------------------------------------------

   COMMENT ON COLUMN "CORE_MODULO"."NOMBRE" IS 'Nombre del m�dulo';
   COMMENT ON COLUMN "CORE_MODULO"."DESCRIPCION" IS 'Literal descriptivo del modulo a configurar su activaci�n';
   COMMENT ON COLUMN "CORE_MODULO"."ACTIVOENTRADA" IS 'Valor que indica si el m�dulo esta activo en la emisi�n de mensajes. Puede tomar valor 1 (Activo) � 0 (Inactivo)';
   COMMENT ON COLUMN "CORE_MODULO"."ACTIVOSALIDA" IS 'Valor que indica si el m�dulo esta activo en la recepci�n de mensajes. Puede tomar valor 1 (Activo) � 0 (Inactivo)';
   COMMENT ON TABLE "CORE_MODULO"  IS 'Tabla que almacena la configuraci�n de activaci�n  o desactivaci�n de los m�dulos que componen el ciclo de ejecuci�n de las librer�as SCSP';
--------------------------------------------------------
--  Comments for Table CORE_MODULO_CONFIGURACION
--------------------------------------------------------

   COMMENT ON COLUMN "CORE_MODULO_CONFIGURACION"."MODULO" IS 'Nombre del m�dulo a configurar';
   COMMENT ON COLUMN "CORE_MODULO_CONFIGURACION"."CERTIFICADO" IS 'C�digo del certificado solicitado';
   COMMENT ON COLUMN "CORE_MODULO_CONFIGURACION"."ACTIVOENTRADA" IS 'Valor que indica si el m�dulo esta activo en la emisi�n de mensajes. Puede tomar valor 1 (Activo) � 0 (Inactivo)';
   COMMENT ON COLUMN "CORE_MODULO_CONFIGURACION"."ACTIVOSALIDA" IS 'Valor que indica si el m�dulo esta activo en la recepci�n de mensajes. Puede tomar valor 1 (Activo) � 0 (Inactivo)';
   COMMENT ON TABLE "CORE_MODULO_CONFIGURACION"  IS 'Tabla que permite la sobreescritura de la configuraci�n de activaci�n de un m�dulo para un servicio concreto';
--------------------------------------------------------
--  Comments for Table CORE_CLAVE_PUBLICA
--------------------------------------------------------
 
   COMMENT ON COLUMN "CORE_CLAVE_PUBLICA"."ALIAS" IS 'Alias que identifica de manera un�voca a la clave p�blica dentro del almac�n de certificados que haya sido configurado en la tabla core_parametro_configuracion bajo el nombre keystoreFile';
   COMMENT ON COLUMN "CORE_CLAVE_PUBLICA"."NOMBRE" IS 'Nombre descriptivo de la clave p�blica';
   COMMENT ON COLUMN "CORE_CLAVE_PUBLICA"."NUMEROSERIE" IS 'Numero de serie de la clave publica';
   COMMENT ON TABLE "CORE_CLAVE_PUBLICA"  IS 'Esta tabla almacenar� los datos de configuraci�n necesarios para acceder a las claves p�blicas disponibles en el almac�n de certificados configurado. Las claves p�blicas  aqu� configuradas ser�n utilizadas para el posible cifrado de los  mensajes emitidos';
--------------------------------------------------------
--  Comments for Table CORE_SECUENCIA_IDPETICION
-------------------------------------------------------- 

   COMMENT ON COLUMN "CORE_SECUENCIA_IDPETICION"."PREFIJO" IS 'Prefijo utilizado para la construccion de los identificadores. Dicho valor podr� ser el prefijo especificado a ser utilizado para cada servicio o ante la no existencia del mismo, el n�mero de serie del certificado digital firmante  de los mensajes';
   COMMENT ON COLUMN "CORE_SECUENCIA_IDPETICION"."SECUENCIA" IS 'Valor secuencial que ser� concatenado al prefijo para generar los identificadores de petici�n. Este secuencial ser� de tipo alfanum�rico, de tal forma que el siguiente valor a 00000009 ser�a 0000000A';
   COMMENT ON COLUMN "CORE_SECUENCIA_IDPETICION"."FECHAGENERACION" IS 'Fecha en la que se registr�  el secuencial';
   COMMENT ON TABLE "CORE_SECUENCIA_IDPETICION"  IS 'Tabla que almacena las semillas (secuenciales) utilizadas para la generaci�n de los identificadores de peticion. Existir� un secuencial asociado a cada posible prefijo o n�mero de serie de certificado digital firmante';
--------------------------------------------------------
--  Comments for Table CORE_PETICION_RESPUESTA
--------------------------------------------------------

   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."IDPETICION" IS 'Identificador un�voco de la petici�n de servicio';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."CERTIFICADO" IS 'C�digo del certificado que se solicita en la petici�n';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."ESTADO" IS 'Codigo identificativo del estado de la petici�n. Tomar� sus valores de las tablas scsp_estado_peticion y scsp_codigo_error';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."FECHAPETICION" IS 'Timestamp que indica la fecha en la que se gener� la petici�n';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."FECHARESPUESTA" IS 'Timestamp que indica la fecha en la cual se recibi� la respuesta a nuestra petici�n'; 
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."TER" IS 'Timestamp que indica la fecha a partir de la cual una petici�n de tipo as�ncrono podr� solicitar una respuesta definitiva al servicio';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."ERROR" IS 'Mensaje descriptivo del error, si lo hubiera, en la solicitud del servicio';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."NUMEROENVIOS" IS 'Valor que indica el n�mero de veces que se ha reenviado una petici�n al servicio';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."NUMEROTRANSMISIONES" IS 'N�mero de Solicitudes de Transmisi�n que se enviaron en la petici�n';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."FECHAULTIMOSONDEO" IS 'Timestamp que indica la fecha del �ltimo reenvio de una petici�n de tipo asincrono';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."TRANSMISIONSINCRONA" IS 'Valor binario que indica si la petici�n fue solicitada a un servicio de tipo s�ncrono o asincrono. Podr� tomar los valores: 0: La comunicaci�n fue de tipo as�ncrono, 1: La comunicaci�n fue de tipo s�ncrono';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."DESCOMPUESTA" IS 'Caracter que indica el estado del procesamiento de las transmisiones de la respuesta recibida. Podr� tomar los siguientes valores:    - S: Se ha procesado correctamente la respuesta, habiendo obtenido todas las transmisiones en ella incluidas y registradas en la tabla core_transmision. - N: No ha sido procesadas las transmisiones de la respuesta. - E: La respuesta termin� correctamente  (estado 0003), pero se ha producido un error al procesar sus trasmisiones';
   COMMENT ON TABLE "CORE_PETICION_RESPUESTA"  IS 'Esta tabla registrar� un hist�rico de las peticiones y respuestas intercambiadas entre los requirentes y los emisores de servicios';
--------------------------------------------------------
--  Comments for Table CORE_TOKEN_DATA
--------------------------------------------------------
 
   COMMENT ON COLUMN "CORE_TOKEN_DATA"."IDPETICION" IS 'Identificador de la petici�n a la cual est� asociado el XML';
   COMMENT ON COLUMN "CORE_TOKEN_DATA"."TIPOMENSAJE" IS 'Tipo de mensaje almacenado, podr� ser : -Peticion -Respuesta -SolicitudRespuesta -Confirmaci�nPeticion -Fault';
   COMMENT ON COLUMN "CORE_TOKEN_DATA"."DATOS" IS 'Bytes del mensaje almacenado';
   COMMENT ON COLUMN "CORE_TOKEN_DATA"."CLAVE" IS 'Clave sim�trica utilizada para el cifrado de los datos';
   COMMENT ON COLUMN "CORE_TOKEN_DATA"."MODOENCRIPTACION" IS 'Modo de encriptaci�n utilizado para el proceso de cifrado.  Por defecto ser� TransportKey';
   COMMENT ON COLUMN "CORE_TOKEN_DATA"."ALGORITMOENCRIPTACION" IS 'Algoritmo empleado en la encriptaci�n del mensaje. Podr� tomar los siguientes valores: - AES128 - AES256 -DESDe';
   COMMENT ON TABLE "CORE_TOKEN_DATA"  IS 'Esta tabla almacenar� el contenido de los mensajes intercambiados en un proceso de comunicaci�n SCSP';
--------------------------------------------------------
--  Comments for Table CORE_PARAMETRO_CONFIGURACION
--------------------------------------------------------

   COMMENT ON COLUMN "CORE_PARAMETRO_CONFIGURACION"."NOMBRE" IS 'Nombre identificativo del par�metro';
   COMMENT ON COLUMN "CORE_PARAMETRO_CONFIGURACION"."VALOR" IS 'Valor del par�metro';
   COMMENT ON COLUMN "CORE_PARAMETRO_CONFIGURACION"."DESCRIPCION" IS 'Literal descriptivo de la utilidad del par�metro';
   COMMENT ON TABLE "CORE_PARAMETRO_CONFIGURACION"  IS 'Tabla  que almacena aquellos par�metros de configuraci�n que son globales a todos los servicios y entornos para un mismo cliente integrador de las librerias SCSP';
--------------------------------------------------------
--  Comments for Table CORE_EMISOR_CERTIFICADO
--------------------------------------------------------

   COMMENT ON COLUMN "CORE_EMISOR_CERTIFICADO"."CIF" IS 'Nombre descriptivo del organismo emisor de servicios';
   COMMENT ON COLUMN "CORE_EMISOR_CERTIFICADO"."NOMBRE" IS 'CIF identificativo del organismo emisor de servicios';
   COMMENT ON COLUMN "CORE_EMISOR_CERTIFICADO"."FECHABAJA" IS 'Fecha de Baja de emisores de certificados';
   COMMENT ON TABLE "CORE_EMISOR_CERTIFICADO"  IS 'Esta tabla almacenar� los diferentes emisores de servicio configurados';
--------------------------------------------------------
--  Comments for Table CORE_SERVICIO
--------------------------------------------------------

   COMMENT ON COLUMN "CORE_SERVICIO"."CODCERTIFICADO" IS 'C�digo del certificado que lo identifica un�vocamente en las comunicaciones SCSP';
   COMMENT ON COLUMN "CORE_SERVICIO"."URLSINCRONA" IS 'Endpoint de acceso al servicio de tipo s�ncrono';
   COMMENT ON COLUMN "CORE_SERVICIO"."URLASINCRONA" IS 'Endpoint de acceso al servicio de tipo as�ncrono del certificado';
   COMMENT ON COLUMN "CORE_SERVICIO"."ACTIONSINCRONA" IS 'Valor del Soap:Action de la petici�n s�ncrona utilizado por el servidor WS en el caso de que sea necesario';
   COMMENT ON COLUMN "CORE_SERVICIO"."ACTIONASINCRONA" IS 'Valor del Soap:Action de la petici�n as�ncrona utilizado por el servidor WS en el caso de que sea necesario';
   COMMENT ON COLUMN "CORE_SERVICIO"."ACTIONSOLICITUD" IS 'Valor del Soap:Action de la solicitud as�ncrona de respuesta utilizado por el servidor WS en el caso de que sea necesario';
   COMMENT ON COLUMN "CORE_SERVICIO"."VERSIONESQUEMA" IS 'Indica la versi�n de esquema utilizado en los mensajes SCSP pudiendo tomar los valores V2 y V3 (existe la posibilidad de a�adir otros valores utilizando configuraciones avandadas de binding)';
   COMMENT ON COLUMN "CORE_SERVICIO"."TIPOSEGURIDAD" IS 'Indica la politica de seguridad utilizada en la securizaci�n de los mensajes, pudiendo tomar los valores [XMLSignature| WS-Security]';
   COMMENT ON COLUMN "CORE_SERVICIO"."PREFIJOPETICION" IS 'Literal con una longitud m�xima de 8 caracteres, el cual ser� utilizado para la construcci�n de los identificadores de petici�n, anteponiendose a un valor secuencial. Mediante este literal pueden personalizarse los identificadores de petici�n haciendolos m�s descriptivos';
   COMMENT ON COLUMN "CORE_SERVICIO"."XPATHCIFRADOSINCRONO" IS 'Literal que identifica el nodo del mensaje XML a cifrar en caso de que los mensajes intercambiados con el emisor de servicio viajasen cifrados. Se corresponde con una expresi�n xpath que permite localizar  al nodo en el mensaje XML, presentar� el siguiente formato //*[local-name()=�NODO_A_CIFRAR�], donde �NODO_A_CIFRAR� es el local name del nodo que se desea cifrar. Este nodo ser� empleado en el caso de realizar comunicaciones s�ncronas';
   COMMENT ON COLUMN "CORE_SERVICIO"."XPATHCIFRADOASINCRONO" IS 'Literal que identifica el nodo del mensaje XML a cifrar en caso de que los mensajes intercambiados con el emisor de servicio viajasen cifrados. Se corresponde con una expresi�n xpath que permite localizar  al nodo en el mensaje XML, presentar� el siguiente formato //*[local-name()=�NODO_A_CIFRAR�], donde �NODO_A_CIFRAR� es el local name del nodo que se desea cifrar. Este nodo ser� empleado en el caso de realizar comunicaciones as�ncronas';
   COMMENT ON COLUMN "CORE_SERVICIO"."ESQUEMAS" IS 'Ruta que indica el directorio donde se encuentran los esquemas (*.xsd) con el que se validar� el XML de los diferentes mensajes intercambiados. Esta ruta podr� tomar un valor relativo haciendo referencia al classpath de la aplicaci�n o  un path absoluto';
   COMMENT ON COLUMN "CORE_SERVICIO"."CLAVEFIRMA" IS 'Alias,  que identifica univocamente en el almac�n de certificados configurado, la clave privada con la que se firmar�n los mensajes';
   COMMENT ON COLUMN "CORE_SERVICIO"."CLAVECIFRADO" IS 'Alias,  que identifica univocamente en el almac�n de certificados configurado, el certificado de clave p�blica con el que se cifrar�an los mensajes enviados al emisor de servicios, en el caso de que se desease encriptaci�n';
   COMMENT ON COLUMN "CORE_SERVICIO"."ALGORITMOCIFRADO" IS 'Literal que identifica el algoritmo utilizado para el cifrado de los mensajes enviados al emisor. Debe poseer un valor reconocido por las librer�as de Rampart: - Basic128Rsa15 - TripleDesRsa15  - Basic256Rsa15';
   COMMENT ON COLUMN "CORE_SERVICIO"."NUMEROMAXIMOREENVIOS" IS 'Valor que indica en el caso del requirente, el n�mero m�ximo de reenvios que pueden llevarse a cabo sobre una petici�n as�ncrona. En el caso del emisor, hace referencia al n�mero m�ximo de veces que procesar� y crear� una respuesta para una petici�n con un mismo identificador';
   COMMENT ON COLUMN "CORE_SERVICIO"."MAXSOLICITUDESPETICION" IS 'N�mero m�ximo de solicitudes de transmisi�n que se van a permitir por petici�n';
   COMMENT ON COLUMN "CORE_SERVICIO"."PREFIJOIDTRANSMISION" IS 'Semilla empleada por el emisor para generar los identificadores de transmisi�n de las respuestas. Ser� un valor alfanum�rico con un m�nimo de 3 caracteres y un m�ximo de 8';
   COMMENT ON COLUMN "CORE_SERVICIO"."DESCRIPCION" IS 'Literal descriptivo del servicio a solicitar utilizando el c�digo de certificado';
   COMMENT ON COLUMN "CORE_SERVICIO"."EMISOR" IS 'Nombre del emisor del certificado';
   COMMENT ON COLUMN "CORE_SERVICIO"."FECHAALTA" IS 'Timestamp con la fecha en la cual el certificado se di� de alta en el sistema y por lo tanto a partir de la cual se podr�n emitir peticiones al mismo';
   COMMENT ON COLUMN "CORE_SERVICIO"."FECHABAJA" IS 'Timestamp con la fecha en la cual el certificado se dio de baja en el sistema y por tanto a partir de la cual no se podr�n emitir peticiones al mismo';
   COMMENT ON COLUMN "CORE_SERVICIO"."CADUCIDAD" IS 'N�mero de dias que deber�n sumarse a la fecharespuesta de una petici�n, para calcular la fecha a partir de la cual se podr� considerar que la respuesta esta caducada y se devolvera el error scsp correspondiente para indicar que la respuesta ha perdido su valor';
   COMMENT ON COLUMN "CORE_SERVICIO"."XPATHLITERALERROR" IS 'Xpath para recuperar el literal del error de los datos espec�ficos';
   COMMENT ON COLUMN "CORE_SERVICIO"."XPATHCODIGOERROR" IS 'Xpath para recuperar el codigo de error de los datos espec�ficos';
   COMMENT ON COLUMN "CORE_SERVICIO"."TIMEOUT" IS 'Timeout que se establecer� para el env�o de las peticiones a los servicios';
   COMMENT ON COLUMN "CORE_SERVICIO"."VALIDACIONFIRMA" IS 'Par�metro que indica si se admite otro tipo de firma en el servicio adem�s del configurado';
   COMMENT ON TABLE "CORE_SERVICIO"  IS 'Esta tabla registrar� cada uno de los certificados SCSP que van a ser utilizados por el sistema tanto de la parte requirente como de la parte emisora';
--------------------------------------------------------
--  Comments for Table CORE_CLAVE_PRIVADA
--------------------------------------------------------

   COMMENT ON COLUMN "CORE_CLAVE_PRIVADA"."ALIAS" IS 'Alias que identifica de manera un�voca a la clave privada dentro del almac�n de certificados que haya sido configurado en la tabla core_parametro_configuracion bajo el nombre keystoreFile';
   COMMENT ON COLUMN "CORE_CLAVE_PRIVADA"."NOMBRE" IS 'Nombre descriptivo de la clave privada';
   COMMENT ON COLUMN "CORE_CLAVE_PRIVADA"."PASSWORD" IS 'Password de la clave privada necesaria para hacer uso de la misma';
   COMMENT ON COLUMN "CORE_CLAVE_PRIVADA"."NUMEROSERIE" IS 'Numero de serie de la clave privada';
   COMMENT ON TABLE "CORE_CLAVE_PRIVADA"  IS 'Esta tabla almacenar� los datos de configuraci�n necesarios para acceder a las claves privadas disponibles en el almac�n de certificados configurado. Las claves privadas aqu� configuradas ser�n utilizadas para la firma de los mensajes emitidos';
--------------------------------------------------------
--  Comments for Table CORE_LOG
-------------------------------------------------------- 

   COMMENT ON COLUMN "CORE_LOG"."ID" IS 'Valor incremental autogenerado';
   COMMENT ON COLUMN "CORE_LOG"."FECHA" IS 'Fecha  en la que se gener� la traza de log';
   COMMENT ON COLUMN "CORE_LOG"."CRITICIDAD" IS 'Tipo de nivel de la traza de log (WARN,INFO,DEBUG,ALL,ERROR)';
   COMMENT ON COLUMN "CORE_LOG"."CLASE" IS 'Clase que gener� la traza de log registrada';
   COMMENT ON COLUMN "CORE_LOG"."METODO" IS 'M�todo espec�fico de la clase que gener� la traza de log';
   COMMENT ON COLUMN "CORE_LOG"."MENSAJE" IS 'Literal descriptivo del error almacenado';
   COMMENT ON TABLE "CORE_LOG"  IS 'Tabla de configurada en el appender log4j para base de datos de la aplicaci�n, donde se registrar�n las posibles trazas de error';
