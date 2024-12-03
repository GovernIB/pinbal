--------------------------------------------------------
--  Creacion de esquema scsp requirente 
--------------------------------------------------------

create sequence ID_APLICACION_SEQUENCE START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
create sequence ID_AUTORIZACION_CA_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
create sequence ID_AUTORIZACION_CERTIFIC_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
create sequence ID_AUTORIZACION_ORGANISMO_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
create sequence ID_CLAVE_PRIVADA_SEQUENCE START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
create sequence ID_CLAVE_PUBLICA_SEQUENCE START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
create sequence ID_EMISOR_SEQUENCE START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
create sequence ID_ORGANISMO_CESIONARIO_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
create sequence ID_SERVICIO_CESIONARIO_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
create sequence ID_SERVICIO_SEQUENCE START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
create sequence ACL_CLASS_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
create sequence ACL_ENTRY_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
create sequence ACL_OID_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
create sequence ACL_SID_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
create sequence HIBERNATE_SEQUENCE START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

---------------------------------------------------
--  DDL for Table CORE_CODIGO_ERROR
--------------------------------------------------------

create table CORE_CODIGO_ERROR
(
    CODIGO      VARCHAR2(4 CHAR)    not null constraint CORE_CODIGO_ERROR_PK primary key,
    DESCRIPCION VARCHAR2(1024 CHAR) not null
);

comment on table CORE_CODIGO_ERROR is 'Tabla que registrarálos posibles errores genéricos que toda comunicación SCSP puede generar';
comment on column CORE_CODIGO_ERROR.CODIGO is 'Código identificativo del error';
comment on column CORE_CODIGO_ERROR.DESCRIPCION is 'Litreral descriptivo del error';
        
--------------------------------------------------------
--  DDL for Table CORE_TIPO_MENSAJE
--------------------------------------------------------

create table CORE_TIPO_MENSAJE
(
    TIPO        NUMBER not null constraint CORE_TIPO_MENSAJE_PK primary key,
    DESCRIPCION VARCHAR2(32 CHAR) not null
);

comment on table CORE_TIPO_MENSAJE is 'Tabla maestra que almacena los diferentes tipos de mensajes que pueden ser intercambiados a lo largo de un ciclo de comunicación SCSP. Estos valores serán: 0: Peticion, 1: ConfirmacionPeticion, 2: SolicitudRespuesta, 3: Respuesta, 4: Fault';
comment on column CORE_TIPO_MENSAJE.TIPO is 'Tipo identificativo del mensaje';
comment on column CORE_TIPO_MENSAJE.DESCRIPCION is 'Literal descriptivo del tipo de mensaje';
        
--------------------------------------------------------
--  DDL for Table CORE_CACHE_CERTIFICADOS
--------------------------------------------------------

create table CORE_CACHE_CERTIFICADOS
(
    NUMSERIE           VARCHAR2(256 CHAR)    not null,
    AUTORIDADCERTIF    VARCHAR2(512 CHAR)    not null,
    TIEMPOCOMPROBACION TIMESTAMP(6)     not null,
    REVOCADO           NUMBER default 1 not null,
    constraint CORE_CACHE_CERTIFICADOS_PK primary key (NUMSERIE, AUTORIDADCERTIF)
);

comment on table CORE_CACHE_CERTIFICADOS is 'Tabla que registra el resultado de la validación de los certificados empleados en la firma de los mensajes recibidos por el requirente o por el emisor. Así mismo se registra la fecha en la que se realizó dicha validación para poder calcular el periodo de tiempo en la que dicha validación esta vigente.';
comment on column CORE_CACHE_CERTIFICADOS.NUMSERIE is 'Número de serie del certificado';
comment on column CORE_CACHE_CERTIFICADOS.AUTORIDADCERTIF is 'Autoridad certificadora que emitió el certificado';
comment on column CORE_CACHE_CERTIFICADOS.TIEMPOCOMPROBACION is 'Fecha en la que se realizó el proceso de validación del certificado por última vez';
comment on column CORE_CACHE_CERTIFICADOS.REVOCADO is 'Valor booleano (1 o 0) que indicará: -1: si el proceso de validación revocó el certificado. -0: si el proceso de validación aceptó el certificado';

--------------------------------------------------------
--  DDL for Table SCSP_ESTADO_PETICION
--------------------------------------------------------

create table CORE_ESTADO_PETICION
(
    CODIGO  VARCHAR2(4 CHAR)   not null constraint CORE_ESTADO_PETICION_PK primary key,
    MENSAJE VARCHAR2(256 CHAR) not null
);

comment on table CORE_ESTADO_PETICION is 'Tabla que almacena los posibles estados en los que se puede encontrar una petición SCSP. Sus posibles valores serán: 0001 - Pendiente, 0002 - En proceso, 0003 - Tramitada';
comment on column CORE_ESTADO_PETICION.CODIGO is 'Código identificativo del estado';
comment on column CORE_ESTADO_PETICION.MENSAJE is 'Literal descriptivo del estado';

--------------------------------------------------------
--  DDL for Table CORE_MODULO
--------------------------------------------------------

create table CORE_MODULO
(
    NOMBRE        VARCHAR2(256 CHAR)    not null constraint CORE_MODULO_PK primary key,
    DESCRIPCION   VARCHAR2(512 CHAR),
    ACTIVOENTRADA NUMBER default 1 not null,
    ACTIVOSALIDA  NUMBER default 1 not null
);

comment on table CORE_MODULO is 'Tabla que almacena la configuración de activación o desactivación de los módulos que componen el ciclo de ejecución de las librerías SCSP';
comment on column CORE_MODULO.NOMBRE is 'Nombre del módulo';
comment on column CORE_MODULO.DESCRIPCION is 'Literal descriptivo del modulo a configurar su activación';
comment on column CORE_MODULO.ACTIVOENTRADA is 'Valor que indica si el módulo esta activo en la emisión de mensajes. Puede tomar valor 1 (Activo) o 0 (Inactivo)';
comment on column CORE_MODULO.ACTIVOSALIDA is 'Valor que indica si el módulo esta activo en la recepción de mensajes. Puede tomar valor 1 (Activo) o 0 (Inactivo).';

--------------------------------------------------------
--  DDL for Table CORE_EMISOR_CERTIFICADO
--------------------------------------------------------

create table CORE_EMISOR_CERTIFICADO
(
    ID        NUMBER(38)   not null constraint CORE_EMISOR_CERTIFICADO_PK primary key,
    NOMBRE    VARCHAR2(50 CHAR) not null,
    CIF       VARCHAR2(16 CHAR) not null,
    FECHAALTA TIMESTAMP(6),
    FECHABAJA TIMESTAMP(6)
);

comment on table CORE_EMISOR_CERTIFICADO is 'Esta tabla almacenará los diferentes emisores de servicio configurados';
comment on column CORE_EMISOR_CERTIFICADO.NOMBRE is 'CIF identificativo del organismo emisor de servicios';
comment on column CORE_EMISOR_CERTIFICADO.CIF is 'Nombre descriptivo del organismo emisor de servicios';
comment on column CORE_EMISOR_CERTIFICADO.FECHABAJA is 'Fecha de Baja de emisores de certificados';

--------------------------------------------------------
--  DDL for Table CORE_CLAVE_PUBLICA
--------------------------------------------------------

create table CORE_CLAVE_PUBLICA
(
    ID          NUMBER(38)    not null constraint CORE_CLAVE_PUBLICA_PK primary key,
    ALIAS       VARCHAR2(256 CHAR) not null,
    NOMBRE      VARCHAR2(256 CHAR) not null,
    NUMEROSERIE VARCHAR2(256 CHAR) not null,
    FECHAALTA   TIMESTAMP(6)  not null,
    FECHABAJA   TIMESTAMP(6)
);

comment on table CORE_CLAVE_PUBLICA is 'Esta tabla almacenará los datos de configuración necesarios para acceder a las claves públicas disponibles en el almacén de certificados configurado. Las claves públicas aquí configuradas serán utilizadas para el posible cifrado de los  mensajes emitidos.';
comment on column CORE_CLAVE_PUBLICA.ALIAS is 'Alias que identifica de manera unívoca a la clave pública dentro del almacén de certificados que haya sido configurado en la tabla core_parametro_configuracion bajo el nombre keystoreFile';
comment on column CORE_CLAVE_PUBLICA.NOMBRE is 'Nombre descriptivo de la clave pública';
comment on column CORE_CLAVE_PUBLICA.NUMEROSERIE is 'Numero de serie de la clave pública';    

--------------------------------------------------------
--  DDL for Table CORE_ORGANISMO_CESIONARIO
--------------------------------------------------------

create table CORE_ORGANISMO_CESIONARIO
(
    ID                      NUMBER(38)       not null constraint CORE_ORGANISMO_CESIONARIO_PK primary key,
    FECHAALTA               TIMESTAMP(6)     not null,
    FECHABAJA               TIMESTAMP(6),
    NOMBRE                  VARCHAR2(50 CHAR)     not null,
    CIF                     VARCHAR2(50 CHAR)     not null,
    BLOQUEADO               NUMBER default 0 not null,
    LOGO                    BLOB,
    CODIGOUNIDADTRAMITADORA VARCHAR2(9 CHAR)
);

--------------------------------------------------------
--  DDL for Table CORE_CLAVE_PRIVADA
--------------------------------------------------------

create table CORE_CLAVE_PRIVADA
(
    ID                NUMBER(38)    not null constraint CORE_CLAVE_PRIVADA_PK primary key,
    ALIAS             VARCHAR2(256 CHAR) not null,
    NOMBRE            VARCHAR2(256 CHAR) not null,
    PASSWORD          VARCHAR2(256 CHAR) not null,
    NUMEROSERIE       VARCHAR2(256 CHAR) not null,
    FECHABAJA         TIMESTAMP(6),
    FECHAALTA         TIMESTAMP(6)  not null,
    ORGANISMO         NUMBER(38) constraint CLAVE_PRIVADA_ORGANISMO references CORE_ORGANISMO_CESIONARIO,
    INTEROPERABILIDAD NUMBER(1)
);

comment on table CORE_CLAVE_PRIVADA is 'Esta tabla almacenará los datos de configuración necesarios para acceder a las claves privadas disponibles en el almacén de certificados configurado. Las claves privadas aquí configuradas serán utilizadas para la firma de los mensajes emitidos';
comment on column CORE_CLAVE_PRIVADA.ALIAS is 'Alias que identifica de manera unívoca a la clave privada dentro del almacén de certificados que haya sido configurado en la tabla core_parametro_configuracion bajo el nombre keystoreFile';
comment on column CORE_CLAVE_PRIVADA.NOMBRE is 'Nombre descriptivo de la clave privada';
comment on column CORE_CLAVE_PRIVADA.PASSWORD is 'Password de la clave privada necesaria para hacer uso de la misma';
comment on column CORE_CLAVE_PRIVADA.NUMEROSERIE is 'Numero de serie de la clave privada';
        
--------------------------------------------------------
--  DDL for Table CORE_SERVICIO
--------------------------------------------------------

create table CORE_SERVICIO
(
    ID                         NUMBER(38)              not null constraint CORE_SERVICIO_PK primary key,
    CODCERTIFICADO             VARCHAR2(64 CHAR)            not null,
    DESCRIPCION                VARCHAR2(512 CHAR)           not null,
    URLSINCRONA                VARCHAR2(256 CHAR),
    URLASINCRONA               VARCHAR2(256 CHAR),
    ACTIONSINCRONA             VARCHAR2(256 CHAR),
    ACTIONASINCRONA            VARCHAR2(256 CHAR),
    ACTIONSOLICITUD            VARCHAR2(256 CHAR),
    VERSIONESQUEMA             VARCHAR2(32 CHAR)            not null,
    TIPOSEGURIDAD              VARCHAR2(16 CHAR)            not null,
    PREFIJOPETICION            VARCHAR2(9 CHAR),
    XPATHCIFRADOSINCRONO       VARCHAR2(256 CHAR),
    XPATHCIFRADOASINCRONO      VARCHAR2(256 CHAR),
    ESQUEMAS                   VARCHAR2(256 CHAR),
    CLAVECIFRADO               NUMBER(38) constraint SERV_CLAVECIFRADO references CORE_CLAVE_PUBLICA,
    CLAVEFIRMA                 NUMBER(38) constraint SERV_CLAVEFIRMA references CORE_CLAVE_PRIVADA,
    ALGORITMOCIFRADO           VARCHAR2(32 CHAR),
    NUMEROMAXIMOREENVIOS       NUMBER                  not null,
    MAXSOLICITUDESPETICION     NUMBER                  not null,
    PREFIJOIDTRANSMISION       VARCHAR2(9 CHAR),
    EMISOR                     NUMBER(38)              not null constraint SERV_EMISOR references CORE_EMISOR_CERTIFICADO,
    FECHAALTA                  TIMESTAMP(6)            not null,
    FECHABAJA                  TIMESTAMP(6),
    CADUCIDAD                  NUMBER       default 0  not null,
    XPATHLITERALERROR          VARCHAR2(256 CHAR),
    XPATHCODIGOERROR           VARCHAR2(256 CHAR),
    XPATHCODIGOERRORSECUNDARIO VARCHAR2(256 CHAR),
    TIMEOUT                    NUMBER       default 60 not null,
    VALIDACIONFIRMA            VARCHAR2(32 CHAR) default 'estricto',
    PLANTILLAXSLT              VARCHAR2(512 CHAR)
);

create index CORE_SERVICIO_INDEX_EMISOR on CORE_SERVICIO (EMISOR);
create index CORE_SERVICIO_INDEX_CODCERT on CORE_SERVICIO (CODCERTIFICADO);

comment on table CORE_SERVICIO is 'Esta tabla registrará cada uno de los certificados SCSP que van a ser utilizados por el sistema tanto de la parte requirente como de la parte emisora';
comment on column CORE_SERVICIO.CODCERTIFICADO is 'Código del certificado que lo identifica unívocamente en las comunicaciones SCSP';
comment on column CORE_SERVICIO.DESCRIPCION is 'Literal descriptivo del servicio a solicitar utilizando el código de certificado';
comment on column CORE_SERVICIO.URLSINCRONA is 'Endpoint de acceso al servicio de tipo síncrono';
comment on column CORE_SERVICIO.URLASINCRONA is 'Endpoint de acceso al servicio de tipo asíncrono del certificado';
comment on column CORE_SERVICIO.ACTIONSINCRONA is 'Valor del Soap:Action de la petición síncrona utilizado por el servidor WS en el caso de que sea necesario';
comment on column CORE_SERVICIO.ACTIONASINCRONA is 'Valor del Soap:Action de la petición asíncrona utilizado por el servidor WS en el caso de que sea necesario';
comment on column CORE_SERVICIO.ACTIONSOLICITUD is 'Valor del Soap:Action de la solicitud asíncrona de respuesta utilizado por el servidor WS en el caso de que sea necesario';
comment on column CORE_SERVICIO.VERSIONESQUEMA is 'Indica la versión de esquema utilizado en los mensajes SCSP pudiendo tomar los valores V2 y V3 (existe la posibilidad de añadir otros valores utilizando configuraciones avandadas de binding)';
comment on column CORE_SERVICIO.TIPOSEGURIDAD is 'Indica la politica de seguridad utilizada en la securización de los mensajes, pudiendo tomar los valores [XMLSignature| WS-Security]';
comment on column CORE_SERVICIO.PREFIJOPETICION is 'Literal con una longitud máxima de 8 caracteres, el cual será utilizado para la construcción de los identificadores de petición, anteponiendose a un valor secuencial. Mediante este literal pueden personalizarse los identificadores de petición haciendolos más descriptivos';
comment on column CORE_SERVICIO.XPATHCIFRADOSINCRONO is 'Literal que identifica el nodo del mensaje XML a cifrar en caso de que los mensajes intercambiados con el emisor de servicio viajasen cifrados. Se corresponde con una expresión xpath que permite localizar al nodo en el mensaje XML, presentará el siguiente formato //*[local-name()="NODO_A_CIFRAR"], donde "NODO_A_CIFRAR" es el local name del nodo que se desea cifrar. Este nodo será empleado en el caso de realizar comunicaciones síncronas';
comment on column CORE_SERVICIO.XPATHCIFRADOASINCRONO is 'Literal que identifica el nodo del mensaje XML a cifrar en caso de que los mensajes intercambiados con el emisor de servicio viajasen cifrados. Se corresponde con una expresión xpath que permite localizar al nodo en el mensaje XML, presentará el siguiente formato //*[local-name()="NODO_A_CIFRAR"], donde "NODO_A_CIFRAR" es el local name del nodo que se desea cifrar. Este nodo será empleado en el caso de realizar comunicaciones asíncronas';
comment on column CORE_SERVICIO.ESQUEMAS is 'Ruta que indica el directorio donde se encuentran los esquemas (*.xsd) con el que se validará el XML de los diferentes mensajes intercambiados. Esta ruta podrá tomar un valor relativo haciendo referencia al classpath de la aplicación o  un path absoluto';
comment on column CORE_SERVICIO.ALGORITMOCIFRADO is 'Literal que identifica el algoritmo utilizado para el cifrado de los mensajes enviados al emisor. Debe poseer un valor reconocido por las librerías de Rampart: - Basic128Rsa15 - TripleDesRsa15  - Basic256Rsa15';
comment on column CORE_SERVICIO.NUMEROMAXIMOREENVIOS is 'Valor que indica en el caso del requirente, el número máximo de reenvios que pueden llevarse a cabo sobre una petición asíncrona. En el caso del emisor, hace referencia al número máximo de veces que procesará y creará una respuesta para una petición con un mismo identificador';
comment on column CORE_SERVICIO.MAXSOLICITUDESPETICION is 'Número máximo de solicitudes de transmisión que se van a permitir por petición';
comment on column CORE_SERVICIO.PREFIJOIDTRANSMISION is 'Semilla empleada por el emisor para generar los identificadores de transmisión de las respuestas. Será un valor alfanumérico con un mínimo de 3 caracteres y un máximo de 8';
comment on column CORE_SERVICIO.FECHAALTA is 'Timestamp con la fecha en la cual el certificado se dio de alta en el sistema y por lo tanto a partir de la cual se podrán emitir peticiones al mismo';
comment on column CORE_SERVICIO.FECHABAJA is 'Timestamp con la fecha en la cual el certificado se dio de baja en el sistema y por tanto a partir de la cual no se podran emitir peticiones al mismo';
comment on column CORE_SERVICIO.CADUCIDAD is 'Número de dias que deberán sumarse a la fecharespuesta de una petición, para calcular la fecha a partir de la cual se podrá considerar que la respuesta esta caducada y se devolvera el error scsp correspondiente para indicar que la respuesta ha perdido su valor';
comment on column CORE_SERVICIO.XPATHLITERALERROR is 'Xpath para recuperar el literal del error de los datos específicos';
comment on column CORE_SERVICIO.XPATHCODIGOERROR is 'Xpath para recuperar el codigo de error de los datos específicos';
comment on column CORE_SERVICIO.TIMEOUT is 'Timeout que se establecerá para el envío de las peticiones a los servicios';
comment on column CORE_SERVICIO.VALIDACIONFIRMA is 'Parámetro que indica si se admite otro tipo de firma en el servicio además del configurado';

--------------------------------------------------------
--  DDL for Table CORE_MODULO_CONFIGURACION
--------------------------------------------------------

create table CORE_MODULO_CONFIGURACION
(
    CERTIFICADO   NUMBER(38)       not null constraint MOD_CONF_SERVICIO references CORE_SERVICIO,
    MODULO        VARCHAR2(256 CHAR)    not null constraint MOD_CONF_MODULO references CORE_MODULO,
    ACTIVOENTRADA NUMBER default 1 not null,
    ACTIVOSALIDA  NUMBER default 1 not null,
    constraint CORE_MODULO_CONFIG_PK primary key (CERTIFICADO, MODULO)
);

comment on table CORE_MODULO_CONFIGURACION is 'Tabla que permite la sobreescritura de la configuración de activación de un módulo para un servicio concreto';
comment on column CORE_MODULO_CONFIGURACION.CERTIFICADO is 'Código del certificado solicitado';
comment on column CORE_MODULO_CONFIGURACION.MODULO is 'Nombre del módulo a configurar';
comment on column CORE_MODULO_CONFIGURACION.ACTIVOENTRADA is 'Valor que indica si el módulo esta activo en la emisión de mensajes. Puede tomar valor 1 (Activo) o 0 (Inactivo).';
comment on column CORE_MODULO_CONFIGURACION.ACTIVOSALIDA is 'Valor que indica si el módulo esta activo en la recepción de mensajes. Puede tomar valor 1 (Activo) o 0 (Inactivo).';

--------------------------------------------------------
--  DDL for Table CORE_REQ_SECUENCIA_ID_PETICION
--------------------------------------------------------

create table CORE_REQ_SECUENCIA_ID_PETICION
(
    PREFIJO         VARCHAR2(9 CHAR)  not null constraint CORE_REQ_SECUENCIA_ID_PET_PK primary key,
    SECUENCIA       VARCHAR2(23 CHAR) not null,
    FECHAGENERACION TIMESTAMP(6) not null
);

comment on table CORE_REQ_SECUENCIA_ID_PETICION is 'Tabla que almacena las semillas (secuenciales) utilizadas para la generación de los identificadores de peticion. Existirá un secuencial asociado a cada posible prefijo o número de serie de certificado digital firmante.';
comment on column CORE_REQ_SECUENCIA_ID_PETICION.PREFIJO is 'Prefijo utilizado para la construccion de los identificadores. Dicho valor podrá ser el prefijo especificado a ser utilizado para cada servicio o ante la no existencia del mismo, el número de serie del certificado digital firmante  de los mensajes.';
comment on column CORE_REQ_SECUENCIA_ID_PETICION.SECUENCIA is 'Valor secuencial que será concatenado al prefijo para generar los identificadores de petición. Este secuencial será de tipo alfanumérico, de tal forma que el siguiente valor a 00000009 sería 0000000A.';
comment on column CORE_REQ_SECUENCIA_ID_PETICION.FECHAGENERACION is 'Fecha en la que se registró el secuencial';
 
--------------------------------------------------------
--  DDL for Table CORE_PETICION_RESPUESTA
--------------------------------------------------------

create table CORE_PETICION_RESPUESTA
(
    IDPETICION          VARCHAR2(26 CHAR)     not null constraint CORE_PETICION_RESPUESTA_PK primary key,
    CERTIFICADO         NUMBER(38)       not null constraint PET_RESP_SERVICIO references CORE_SERVICIO,
    ESTADO              VARCHAR2(4 CHAR)      not null,
    ESTADOSECUNDARIO    VARCHAR2(16 CHAR),
    FECHAPETICION       TIMESTAMP(6),
    FECHARESPUESTA      TIMESTAMP(6),
    TER                 TIMESTAMP(6),
    NUMEROENVIOS        NUMBER default 0 not null,
    NUMEROTRANSMISIONES NUMBER default 1 not null,
    FECHAULTIMOSONDEO   TIMESTAMP(6),
    TRANSMISIONSINCRONA NUMBER default 1 not null,
    DESCOMPUESTA        VARCHAR2(1 CHAR),
    ERROR               VARCHAR2(4000 CHAR),
    ERRORSECUNDARIO     VARCHAR2(1024 CHAR)
);

comment on table CORE_PETICION_RESPUESTA is 'Esta tabla registrará un histórico de las peticiones y respuestas intercambiadas entre los requirentes y los emisores de servicios';
comment on column CORE_PETICION_RESPUESTA.IDPETICION is 'Identificador unívoco de la petición de servicio';
comment on column CORE_PETICION_RESPUESTA.ESTADO is 'Codigo identificativo del estado de la petición. Tomará sus valores de las tablas scsp_estado_peticion y scsp_codigo_error';
comment on column CORE_PETICION_RESPUESTA.FECHAPETICION is 'Timestamp que indica la fecha en la que se generó la petición';
comment on column CORE_PETICION_RESPUESTA.FECHARESPUESTA is 'Timestamp que indica la fecha en la cual se recibió la respuesta a nuestra petición';
comment on column CORE_PETICION_RESPUESTA.TER is 'Timestamp que indica la fecha a partir de la cual una petición de tipo asíncrono podrá solicitar una respuesta definitiva al servicio';
comment on column CORE_PETICION_RESPUESTA.NUMEROENVIOS is 'Valor que indica el número de veces que se ha reenviado una petición al servicio';
comment on column CORE_PETICION_RESPUESTA.NUMEROTRANSMISIONES is 'Número de Solicitudes de Transmisión que se enviaron en la petición';
comment on column CORE_PETICION_RESPUESTA.FECHAULTIMOSONDEO is 'Timestamp que indica la fecha del último reenvio de una petición de tipo asincrono';
comment on column CORE_PETICION_RESPUESTA.TRANSMISIONSINCRONA is 'Valor binario que indica si la petición fue solicitada a un servicio de tipo síncrono o asincrono. Podrá tomar los valores: 0: La comunicación fue de tipo asíncrono, 1: La comunicación fue de tipo síncrono';
comment on column CORE_PETICION_RESPUESTA.DESCOMPUESTA is 'Caracter que indica el estado del procesamiento de las transmisiones de la respuesta recibida. Podrá tomar los siguientes valores:    - S: Se ha procesado correctamente la respuesta, habiendo obtenido todas las transmisiones en ella incluidas y registradas en la tabla core_transmision. - N: No ha sido procesadas las transmisiones de la respuesta. - E: La respuesta terminó correctamente  (estado 0003), pero se ha producido un error al procesar sus trasmisiones';
        
--------------------------------------------------------
--  DDL for Table CORE_TRANSMISION
--------------------------------------------------------

create table CORE_TRANSMISION
(
    IDSOLICITUD             VARCHAR2(64 CHAR) not null,
    IDPETICION              VARCHAR2(26 CHAR) not null constraint TRANS_PETICION references CORE_PETICION_RESPUESTA,
    IDTRANSMISION           VARCHAR2(64 CHAR),
    IDSOLICITANTE           VARCHAR2(10 CHAR) not null,
    NOMBRESOLICITANTE       VARCHAR2(256 CHAR),
    DOCTITULAR              VARCHAR2(30 CHAR),
    NOMBRETITULAR           VARCHAR2(50 CHAR),
    APELLIDO1TITULAR        VARCHAR2(50 CHAR),
    APELLIDO2TITULAR        VARCHAR2(50 CHAR),
    NOMBRECOMPLETOTITULAR   VARCHAR2(160 CHAR),
    DOCFUNCIONARIO          VARCHAR2(16 CHAR),
    NOMBREFUNCIONARIO       VARCHAR2(160 CHAR),
    SEUDONIMOFUNCIONARIO    VARCHAR2(32 CHAR),
    FECHAGENERACION         TIMESTAMP(6),
    UNIDADTRAMITADORA       VARCHAR2(250 CHAR),
    CODIGOUNIDADTRAMITADORA VARCHAR2(9 CHAR),
    CODIGOPROCEDIMIENTO     VARCHAR2(256 CHAR),
    NOMBREPROCEDIMIENTO     VARCHAR2(256 CHAR),
    EXPEDIENTE              VARCHAR2(65 CHAR),
    FINALIDAD               VARCHAR2(256 CHAR),
    CONSENTIMIENTO          VARCHAR2(3 CHAR),
    ERROR                   VARCHAR2(4000 CHAR),
    XMLTRANSMISION          CLOB,
    ESTADO                  VARCHAR2(10 CHAR),
    ESTADOSECUNDARIO        VARCHAR2(16 CHAR),
    AUTOMATIZADO            NUMBER(1),
    CLASETRAMITE            NUMBER(2),
    constraint CORE_TRANSMISION_PK primary key (IDSOLICITUD, IDPETICION)
);

comment on table CORE_TRANSMISION is 'Esta tabla almacenará la información específica de cada transmisión que haya sido incluida en una respuesta de un servicio SCSP';
comment on column CORE_TRANSMISION.IDSOLICITUD is 'Indentificador de la solicitud de transmisión';
comment on column CORE_TRANSMISION.IDPETICION is 'Indentificador de la petición en la que se incluyó la solicitud de transmisión';
comment on column CORE_TRANSMISION.IDTRANSMISION is 'Indentificador de la transmisión que responde a la petición de servicio de la Solicitud de transmisión identificada con idSolicitud';
comment on column CORE_TRANSMISION.IDSOLICITANTE is 'CIF de organismo solicitante del servicio';
comment on column CORE_TRANSMISION.NOMBRESOLICITANTE is 'Nombre del organismo solicitante de servicio';
comment on column CORE_TRANSMISION.DOCTITULAR is 'Documento identificativo del titular sobre el cual se está realizando la petición de servicio';
comment on column CORE_TRANSMISION.NOMBRETITULAR is 'Nombre del titular sobre el que se realiza la petición del servicio';
comment on column CORE_TRANSMISION.APELLIDO1TITULAR is 'Primer apellido del titular sobre el que se realiza la petición del servicio';
comment on column CORE_TRANSMISION.APELLIDO2TITULAR is 'Segundo apellido del titular sobre el que se realiza la petición del servicio';
comment on column CORE_TRANSMISION.NOMBRECOMPLETOTITULAR is 'Nombre completo del titular sobre el que se realiza la petición del servicio';
comment on column CORE_TRANSMISION.DOCFUNCIONARIO is 'Documento identificativo del funcionario que generó la solicitud de transmisión';
comment on column CORE_TRANSMISION.NOMBREFUNCIONARIO is 'Nombre del Funcionario que generó la solicitud de transmisión';
comment on column CORE_TRANSMISION.FECHAGENERACION is 'Fecha en la que se generó la transmisión';
comment on column CORE_TRANSMISION.UNIDADTRAMITADORA is 'Unidad Tramitadora asociada a la solicitud de transmisión';
comment on column CORE_TRANSMISION.CODIGOPROCEDIMIENTO is 'Código del procedimiento en base al cual se puede solicitar el servicio';
comment on column CORE_TRANSMISION.NOMBREPROCEDIMIENTO is 'Nombre del procedimiento en base al cual se puede solicitar el servicio';
comment on column CORE_TRANSMISION.EXPEDIENTE is 'Expediente asociado a la solicitud de transmisión';
comment on column CORE_TRANSMISION.FINALIDAD is 'Finalidad por la cual se emiti� la solicitud de transmisión';
comment on column CORE_TRANSMISION.CONSENTIMIENTO is 'Tipo de consentimiento asociado a la transmisión. Deberá tomar uno de los dos posibles valores: Si, Ley';
comment on column CORE_TRANSMISION.XMLTRANSMISION is 'El XML de la transmisión. Su almacenamiento será opcional, dependiendo de un parametro global almacenado en la tabla core_parametro_configuracion';
comment on column CORE_TRANSMISION.ESTADO is 'Estado concreto en el que se encuentra la transmisión';
comment on column CORE_TRANSMISION.AUTOMATIZADO is 'Indica si el procedimiento es o no automatizado';
comment on column CORE_TRANSMISION.CLASETRAMITE is 'Indica la clase del trámite';

create index CORE_TRANSMISION_INDEX_IDPET on CORE_TRANSMISION (IDPETICION);

 
--------------------------------------------------------
--  DDL for Table CORE_TOKEN_DATA
--------------------------------------------------------

create table CORE_TOKEN_DATA
(
    IDPETICION            VARCHAR2(26 CHAR) not null constraint TOKEN_PETICION references CORE_PETICION_RESPUESTA,
    TIPOMENSAJE           NUMBER       not null constraint TOKEN_TIPO references CORE_TIPO_MENSAJE,
    DATOS                 CLOB         not null,
    CLAVE                 VARCHAR2(256 CHAR),
    MODOENCRIPTACION      VARCHAR2(32 CHAR),
    ALGORITMOENCRIPTACION VARCHAR2(32 CHAR),
    constraint CORE_TOKEN_DATA_PK primary key (IDPETICION, TIPOMENSAJE)
);

comment on table CORE_TOKEN_DATA is 'Esta tabla almacenará el contenido de los mensajes intercambiados en un proceso de comunicación SCSP';
comment on column CORE_TOKEN_DATA.IDPETICION is 'Identificador de la petición a la cual est� asociado el XML';
comment on column CORE_TOKEN_DATA.TIPOMENSAJE is 'Tipo de mensaje almacenado que podrá ser: Peticion, Respuesta, SolicitudRespuesta, ConfirmacionPeticion, Fault';
comment on column CORE_TOKEN_DATA.DATOS is 'Bytes del mensaje almacenado';
comment on column CORE_TOKEN_DATA.CLAVE is 'Clave simétrica utilizada para el cifrado de los datos';
comment on column CORE_TOKEN_DATA.MODOENCRIPTACION is 'Modo de encriptación utilizado para el proceso de cifrado. Por defecto será TransportKey';
comment on column CORE_TOKEN_DATA.ALGORITMOENCRIPTACION is 'Algoritmo empleado en la encriptación del mensaje. Podrá tomar los siguientes valores: - AES128 - AES256 -DESDe';
 
--------------------------------------------------------
--  DDL for Table CORE_PARAMETRO_CONFIGURACION
--------------------------------------------------------

create table CORE_PARAMETRO_CONFIGURACION
(
    NOMBRE      VARCHAR2(64 CHAR)  not null constraint CORE_PARAMETRO_CONFIG_PK primary key,
    VALOR       VARCHAR2(512 CHAR) not null,
    DESCRIPCION VARCHAR2(512 CHAR)
);

comment on table CORE_PARAMETRO_CONFIGURACION is 'Tabla  que almacena aquellos parámetros de configuración que son globales a todos los servicios y entornos para un mismo cliente integrador de las librerias SCSP';
comment on column CORE_PARAMETRO_CONFIGURACION.NOMBRE is 'Nombre identificativo del parámetro';
comment on column CORE_PARAMETRO_CONFIGURACION.VALOR is 'Valor del parámetro';
comment on column CORE_PARAMETRO_CONFIGURACION.DESCRIPCION is 'Literal descriptivo de la utilidad del parámetro';

--------------------------------------------------------
--  DDL for Table CORE_REQ_CESIONARIOS_SERVICIOS
--------------------------------------------------------

create table CORE_REQ_CESIONARIOS_SERVICIOS
(
    ID           NUMBER(38)   not null constraint CORE_REQ_CESIONARIOS_SERV_PK primary key,
    SERVICIO     NUMBER(38),
    CLAVEPRIVADA NUMBER(38),
    ORGANISMO    NUMBER(38),
    FECHAALTA    TIMESTAMP(6) not null,
    FECHABAJA    TIMESTAMP(6),
    BLOQUEADO    NUMBER(1)    not null,
    SSLFLAG      NUMBER(1)    not null
);

create index CESIONARIOS_INDEX_ORGANISMO on CORE_REQ_CESIONARIOS_SERVICIOS (ORGANISMO);

--------------------------------------------------------
--  DDL for Table CORE_REQ_MODULO_PDF
--------------------------------------------------------

create table CORE_REQ_MODULO_PDF
(
    NOMBRE VARCHAR2(256 CHAR)    not null constraint CORE_REQ_MODULO_PDF_PK primary key,
    ACTIVO NUMBER default 1 not null,
    ORDEN  NUMBER           not null
);

--------------------------------------------------------
--  DDL for Table CORE_REQ_MODULO_PDF_CESIONARIO
--------------------------------------------------------

create table CORE_REQ_MODULO_PDF_CESIONARIO
(
    SERVICIO  NUMBER(38)       not null constraint FK_SERVICIO_MOD_PDF references CORE_SERVICIO,
    ORGANISMO NUMBER(38)       not null constraint FK_ORG_CESIONARIO_MOD_PDF references CORE_ORGANISMO_CESIONARIO,
    MODULO    VARCHAR2(256 CHAR)    not null constraint FK_MODULO_MOD_PDF references CORE_MODULO,
    ACTIVO    NUMBER default 1 not null,
    constraint CORE_REQ_MODPDF_CES_PK primary key (SERVICIO, ORGANISMO, MODULO)
);

--------------------------------------------------------
--  DDL for Table CORE_LOG
--------------------------------------------------------

create table CORE_LOG
(
    "ID" NUMBER(19,0) not null constraint CORE_LOG_PK primary key,
    "FECHA" TIMESTAMP (6),
    "CRITICIDAD" VARCHAR2(10 CHAR),
    "CLASE" VARCHAR2(256 CHAR),
    "METODO" VARCHAR2(64 CHAR),
    "MENSAJE" CLOB
);

comment on column "CORE_LOG"."ID" is 'Valor incremental autogenerado';
comment on column "CORE_LOG"."FECHA" is 'Fecha  en la que se generó la traza de log';
comment on column "CORE_LOG"."CRITICIDAD" is 'Tipo de nivel de la traza de log (WARN,INFO,DEBUG,ALL,ERROR)';
comment on column "CORE_LOG"."CLASE" is 'Clase que generó la traza de log registrada';
comment on column "CORE_LOG"."METODO" is 'Método específico de la clase que generó la traza de log';
comment on column "CORE_LOG"."MENSAJE" is 'Literal descriptivo del error almacenado';
comment on table "CORE_LOG"  is 'Tabla de configurada en el appender log4j para base de datos de la aplicación, donde se registrarán las posibles trazas de error';


--------------------------------------------------------
--  DDL for ACL Tables
--------------------------------------------------------

create table ACL_CLASS
(
    ID    NUMBER(38)    not null constraint ACL_CLASS_PK primary key,
    CLASS VARCHAR2(100 CHAR) not null constraint ACL_CLASS_UK unique
);

create table ACL_SID
(
    ID        NUMBER(38)    not null constraint ACL_SID_PK primary key,
    PRINCIPAL NUMBER(1)     not null,
    SID       VARCHAR2(100 CHAR) not null,
    constraint ACL_SID_UK unique (SID, PRINCIPAL)
);

create table ACL_OBJECT_IDENTITY
(
    ID                 NUMBER(38) not null constraint ACL_OBJECT_IDENTITY_PK primary key,
    OBJECT_ID_CLASS    NUMBER(38) not null constraint ACL_OID_CLASS_FK references ACL_CLASS,
    OBJECT_ID_IDENTITY NUMBER(38) not null,
    PARENT_OBJECT      NUMBER(38) constraint ACL_OID_PARENT_FK references ACL_OBJECT_IDENTITY,
    OWNER_SID          NUMBER(38) not null constraint ACL_OID_OWNER_FK references ACL_SID,
    ENTRIES_INHERITING NUMBER(1)  not null, constraint ACL_OID_UK unique (OBJECT_ID_CLASS, OBJECT_ID_IDENTITY)
);

create table ACL_ENTRY
(
    ID                  NUMBER(38) not null constraint ACL_ENTRY_PK primary key,
    ACL_OBJECT_IDENTITY NUMBER(38) not null constraint ACL_ENTRY_OBJECT_FK references ACL_OBJECT_IDENTITY,
    ACE_ORDER           NUMBER(38) not null,
    SID                 NUMBER(38) not null constraint ACL_ENTRY_ACL_FK references ACL_SID,
    MASK                NUMBER     not null,
    GRANTING            NUMBER(1)  not null,
    AUDIT_SUCCESS       NUMBER(1)  not null,
    AUDIT_FAILURE       NUMBER(1)  not null,
    constraint ACL_ENTRY_UK unique (ACL_OBJECT_IDENTITY, ACE_ORDER)
);

CREATE OR REPLACE trigger ACL_CLASS_IDGEN before insert on ACL_CLASS for each row
BEGIN
    SELECT acl_class_seq.NEXTVAL INTO :NEW.ID FROM DUAL;
END;
/

CREATE OR REPLACE trigger ACL_ENTRY_IDGEN before insert on ACL_ENTRY for each row
BEGIN
    SELECT acl_entry_seq.NEXTVAL INTO :NEW.ID FROM DUAL;
END;
/

CREATE OR REPLACE trigger ACL_OID_IDGEN before insert on ACL_OBJECT_IDENTITY for each row
BEGIN
    SELECT acl_oid_seq.NEXTVAL INTO :NEW.ID FROM DUAL;
END;
/

CREATE OR REPLACE trigger ACL_SID_IDGEN before insert on ACL_SID for each row
BEGIN
    SELECT acl_sid_seq.NEXTVAL INTO :NEW.ID FROM DUAL;
END;
/

--------------------------------------------------------
--  DDL for Table PBL_AVIS
--------------------------------------------------------

create table PBL_AVIS
(
    ID                  NUMBER(19)     not null constraint PBL_AVIS_PK primary key,
    ASSUMPTE            VARCHAR2(256 CHAR)  not null,
    MISSATGE            VARCHAR2(2048 CHAR) not null,
    DATA_INICI          TIMESTAMP(6)   not null,
    DATA_FINAL          TIMESTAMP(6)   not null,
    ACTIU               NUMBER(1)      not null,
    AVIS_NIVELL         VARCHAR2(10 CHAR)   not null,
    CREATEDBY_CODI      VARCHAR2(64 CHAR),
    CREATEDDATE         TIMESTAMP(6),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDDATE    TIMESTAMP(6)
);

--------------------------------------------------------
--  DDL for Table PBL_CONFIG_GROUP
--------------------------------------------------------

create table PBL_CONFIG_GROUP
(
    CODE            VARCHAR2(128 CHAR) not null primary key,
    DESCRIPTION_KEY VARCHAR2(512 CHAR),
    POSITION        NUMBER(3)          not null,
    PARENT_CODE     VARCHAR2(128 CHAR)
);

--------------------------------------------------------
--  DDL for Table PBL_CONFIG
--------------------------------------------------------

create table PBL_CONFIG
(
    KEY                 VARCHAR2(256 CHAR) not null primary key,
    VALUE               VARCHAR2(2048 CHAR),
    DESCRIPTION_KEY     VARCHAR2(2048 CHAR),
    GROUP_CODE          VARCHAR2(128 CHAR) not null constraint PBL_CONFIG_GROUP_FK references PBL_CONFIG_GROUP,
    POSITION            NUMBER(3)          not null,
    SOURCE_PROPERTY     VARCHAR2(16 CHAR)  not null,
    TYPE_CODE           VARCHAR2(128 CHAR),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDDATE    TIMESTAMP(6)
);

--------------------------------------------------------
--  DDL for Table PBL_CONFIG_TYPE
--------------------------------------------------------

create table PBL_CONFIG_TYPE
(
    CODE  VARCHAR2(128 CHAR) not null primary key,
    VALUE VARCHAR2(2048 CHAR)
);

--------------------------------------------------------
--  DDL for Table PBL_SERVEI_REGLA
--------------------------------------------------------

create table PBL_SERVEI_REGLA
(
    ID                  NUMBER(38)         not null constraint PBL_SERVEI_REGLA_PK primary key,
    NOM                 VARCHAR2(255 CHAR) not null,
    SERVEI_ID           NUMBER(38)         not null constraint PBL_REGLA_SERVEI_FK references CORE_SERVICIO,
    MODIFICAT           VARCHAR2(32 CHAR)  not null,
    ACCIO               VARCHAR2(32 CHAR)  not null,
    ORDRE               NUMBER             not null,
    CREATEDBY_CODI      VARCHAR2(64 CHAR),
    CREATEDDATE         TIMESTAMP(6),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDDATE    TIMESTAMP(6),
    constraint PBL_SERVEI_REGLA_UK unique (NOM, SERVEI_ID)
);

create index PBL_REGLA_SERVEI_FK_I on PBL_SERVEI_REGLA (SERVEI_ID);

--------------------------------------------------------
--  DDL for Table PBL_SERVEI_REGLA_VALOR_AFC
--------------------------------------------------------

create table PBL_SERVEI_REGLA_VALOR_AFC
(
    REGLA_ID NUMBER(38)    not null constraint PBL_REGLA_VALOR_AFC_FK references PBL_SERVEI_REGLA,
    VALOR    VARCHAR2(255 CHAR) not null,
    constraint PBL_SERVEI_REGLA_VALOR_AFC_PK primary key (REGLA_ID, VALOR)
);

create index PBL_REGLA_VALOR_AFC_FK_I on PBL_SERVEI_REGLA_VALOR_AFC (REGLA_ID);

--------------------------------------------------------
--  DDL for Table PBL_SERVEI_REGLA_VALOR_MOD
--------------------------------------------------------

create table PBL_SERVEI_REGLA_VALOR_MOD
(
    REGLA_ID NUMBER(38)    not null constraint PBL_REGLA_VALOR_MOD_FK references PBL_SERVEI_REGLA,
    VALOR    VARCHAR2(255 CHAR) not null,
    constraint PBL_SERVEI_REGLA_VALOR_MOD_PK primary key (REGLA_ID, VALOR)
);

create index PBL_REGLA_VALOR_MOD_FK_I on PBL_SERVEI_REGLA_VALOR_MOD (REGLA_ID);

--------------------------------------------------------
--  DDL for Table PBL_USUARI
--------------------------------------------------------

create table PBL_USUARI
(
    CODI                VARCHAR2(64 CHAR) not null constraint PBL_USUARI_PK primary key,
    NOM                 VARCHAR2(200 CHAR),
    NIF                 VARCHAR2(15 CHAR),
    EMAIL               VARCHAR2(200 CHAR),
    IDIOMA              VARCHAR2(2 CHAR),
    INICIALITZAT        NUMBER(1),
    VERSION             NUMBER(38)        not null,
    PROCEDIMENT_ID      NUMBER(38),
    SERVEI_CODI         VARCHAR2(64 CHAR),
    ENTITAT_ID          NUMBER(38),
    DEPARTAMENT         VARCHAR2(250 CHAR),
    FINALITAT           VARCHAR2(250 CHAR),
    NUM_ELEMENTS_PAGINA NUMBER default '10'
);

--------------------------------------------------------
--  DDL for Table PBL_ENTITAT
--------------------------------------------------------

create table PBL_ENTITAT
(
    ID                  NUMBER(38)    not null constraint PBL_ENTITAT_PK primary key,
    CODI                VARCHAR2(64 CHAR)  not null constraint PBL_ENTITAT_CODI_UK unique,
    NOM                 VARCHAR2(255 CHAR) not null,
    RESPONSABLE         VARCHAR2(64 CHAR),
    CIF                 VARCHAR2(16 CHAR)  not null,
    TIPUS               NUMBER,
    ACTIVA              NUMBER(1),
    UNITAT_ARREL        VARCHAR2(9 CHAR),
    CREATEDBY_CODI      VARCHAR2(64 CHAR) constraint PBL_CREATEDBY_USUARI_FK references PBL_USUARI,
    CREATEDDATE         TIMESTAMP(6),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR) constraint PBL_LASTMODIFIEDBY_USUARI_FK references PBL_USUARI,
    LASTMODIFIEDDATE    TIMESTAMP(6),
    VERSION             NUMBER(38)    not null
);

--------------------------------------------------------
--  DDL for Table PBL_ENTITAT_SERVEI
--------------------------------------------------------

create table PBL_ENTITAT_SERVEI
(
    ID                  NUMBER(38) not null constraint PBL_ENTITAT_SERVEI_PK primary key,
    SERVEI_ID           VARCHAR2(64 CHAR),
    ENTITAT_ID          NUMBER(38) not null constraint PBL_ENTITAT_ENTISERV_FK references PBL_ENTITAT,
    CREATEDBY_CODI      VARCHAR2(64 CHAR) constraint PBL_CREATEDBY_ENTISERV_FK references PBL_USUARI,
    CREATEDDATE         TIMESTAMP(6),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR) constraint PBL_LASTMODIFIEDBY_ENTISERV_FK references PBL_USUARI,
    LASTMODIFIEDDATE    TIMESTAMP(6),
    VERSION             NUMBER(38) not null,
    constraint PBL_ENTITAT_SERVEI_UK unique (ENTITAT_ID, SERVEI_ID)
);

create index PBL_ENTISERV_ENTITAT_I on PBL_ENTITAT_SERVEI (ENTITAT_ID);
create index PBL_ENTISERV_SERVEI_I on PBL_ENTITAT_SERVEI (SERVEI_ID);

--------------------------------------------------------
--  DDL for Table PBL_ENTITAT_USUARI
--------------------------------------------------------

create table PBL_ENTITAT_USUARI
(
    ID                  NUMBER(38)        not null constraint PBL_ENTITAT_USUARI_PK primary key,
    ENTITAT_ID          NUMBER(38)        not null constraint PBL_ENTITAT_ENTIUSR_FK references PBL_ENTITAT,
    USUARI_ID           VARCHAR2(64 CHAR) not null constraint PBL_USUARI_ENTIUSR_FK references PBL_USUARI,
    DEPARTAMENT         VARCHAR2(64 CHAR),
    REPRESENTANT        NUMBER(1),
    DELEGAT             NUMBER(1),
    AUDITOR             NUMBER(1),
    APLICACIO           NUMBER(1),
    PRINCIPAL           NUMBER(1),
    CREATEDBY_CODI      VARCHAR2(64 CHAR) constraint PBL_CREATEDBY_ENTIUSR_FK references PBL_USUARI,
    CREATEDDATE         TIMESTAMP(6),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR) constraint PBL_LASTMODIFIEDBY_ENTIUSR_FK references PBL_USUARI,
    LASTMODIFIEDDATE    TIMESTAMP(6),
    VERSION             NUMBER(38)        not null,
    ACTIU               NUMBER(1)         not null,
    constraint PBL_ENTITAT_USUARI_UK unique (ENTITAT_ID, USUARI_ID)
);

create index PBL_ENTIUSR_ENTITAT_I on PBL_ENTITAT_USUARI (ENTITAT_ID);
create index PBL_ENTIUSR_USUARI_I on PBL_ENTITAT_USUARI (USUARI_ID);

--------------------------------------------------------
--  DDL for Table PBL_ORGAN_GESTOR
--------------------------------------------------------

create table PBL_ORGAN_GESTOR
(
    ID                  NUMBER(38)     not null constraint PBL_ORGAN_GESTOR_PK primary key,
    CODI                VARCHAR2(64 CHAR)   not null,
    NOM                 VARCHAR2(1000 CHAR) not null,
    ENTITAT_ID          NUMBER(38)     not null constraint PBL_ENTITAT_ORGAN_GESTOR_FK references PBL_ENTITAT,
    PARE_ID             NUMBER(38),
    ACTIU               NUMBER(1)      not null,
    CREATEDBY_CODI      VARCHAR2(64 CHAR),
    CREATEDDATE         TIMESTAMP(6),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDDATE    TIMESTAMP(6),
    ESTAT               VARCHAR2(10 CHAR),
    constraint PBL_ORGAN_GESTOR_UK unique (CODI, ENTITAT_ID)
);

--------------------------------------------------------
--  DDL for Table PBL_PROCEDIMENT
--------------------------------------------------------

create table PBL_PROCEDIMENT
(
    ID                      NUMBER(38)    not null constraint PBL_PROCEDIMENT_PK primary key,
    CODI                    VARCHAR2(20 CHAR)  not null,
    NOM                     VARCHAR2(255 CHAR) not null,
    ACTIU                   NUMBER(1),
    ENTITAT_ID              NUMBER(38)    not null constraint PBL_ENTITAT_PROCED_FK references PBL_ENTITAT,
    DEPARTAMENT             VARCHAR2(64 CHAR),
    CODI_SIA                VARCHAR2(64 CHAR),
    ORGAN_GESTOR            VARCHAR2(64 CHAR),
    ORGAN_GESTOR_ID         NUMBER(38) constraint PBL_ORGAN_PROCEDIMENT_FK references PBL_ORGAN_GESTOR,
    CREATEDBY_CODI          VARCHAR2(64 CHAR) constraint PBL_CREATEDBY_PROCED_FK references PBL_USUARI,
    CREATEDDATE             TIMESTAMP(6),
    LASTMODIFIEDBY_CODI     VARCHAR2(64 CHAR) constraint PBL_LASTMODIFIEDBY_PROCED_FK references PBL_USUARI,
    LASTMODIFIEDDATE        TIMESTAMP(6),
    VERSION                 NUMBER(38)    not null,
    VALOR_CAMP_AUTOMATIZADO NUMBER(1) default 0,
    VALOR_CAMP_CLASETRAMITE NUMBER,
    constraint PBL_PROCEDIMENT_UK unique (ENTITAT_ID, CODI)
);

--------------------------------------------------------
--  DDL for Table PBL_CONSULTA_HIST
--------------------------------------------------------

create table PBL_CONSULTA_HIST
(
    ID                     NUMBER(38)         not null constraint PBL_CONSULTA_HIST_PK primary key,
    CUSTODIAT              NUMBER(1),
    DEPARTAMENT            VARCHAR2(250 CHAR) not null,
    ERROR                  VARCHAR2(4000 CHAR),
    ESTAT                  NUMBER             not null,
    PETICION_ID            VARCHAR2(26 CHAR)  not null,
    TITULAR_DOCNUM         VARCHAR2(14 CHAR),
    TITULAR_DOCTIP         VARCHAR2(16 CHAR),
    TITULAR_LLING1         VARCHAR2(64 CHAR),
    TITULAR_LLING2         VARCHAR2(64 CHAR),
    TITULAR_NOM            VARCHAR2(64 CHAR),
    PROCSERV_ID            NUMBER(38)         not null,
    RECOBRIMENT            NUMBER(1),
    TITULAR_NOMCOMPLET     VARCHAR2(122 CHAR),
    FUNCIONARI_NOM         VARCHAR2(128 CHAR),
    FUNCIONARI_DOCNUM      VARCHAR2(16 CHAR),
    SOLICITUD_ID           VARCHAR2(64 CHAR),
    MULTIPLE               NUMBER(1),
    PARE_ID                NUMBER(38),
    CUSTODIA_URL           VARCHAR2(255 CHAR),
    JUSTIFICANT_ERROR      VARCHAR2(2000 CHAR),
    JUSTIFICANT_ESTAT      NUMBER             not null,
    CUSTODIA_ID            VARCHAR2(255 CHAR),
    ARXIU_EXPEDIENT_UUID   VARCHAR2(100 CHAR),
    ARXIU_DOCUMENT_UUID    VARCHAR2(100 CHAR),
    ARXIU_EXPEDIENT_TANCAT NUMBER(1),
    FINALITAT              VARCHAR2(250 CHAR),
    CONSENTIMENT           NUMBER,
    EXPEDIENT_ID           VARCHAR2(25 CHAR),
    DADES_ESPECIFIQUES     VARCHAR2(2048 CHAR),
    CREATEDBY_CODI         VARCHAR2(64 CHAR),
    CREATEDDATE            TIMESTAMP(6),
    LASTMODIFIEDBY_CODI    VARCHAR2(64 CHAR),
    LASTMODIFIEDDATE       TIMESTAMP(6),
    VERSION                NUMBER(38)         not null,
    ENTITAT_ID             NUMBER(38) constraint PBL_CONSULTAH_ENTITAT_FK references PBL_ENTITAT,
    SERVEI_CODI            VARCHAR2(64 CHAR),
    PROCEDIMENT_ID         NUMBER(38) constraint PBL_CONSULTAH_PROCED_FK references PBL_PROCEDIMENT
);

create index PBL_CONSULTAH_PARE_I on PBL_CONSULTA_HIST (PARE_ID);
create index PBL_CONSULTAH_MULT_I on PBL_CONSULTA_HIST (CREATEDBY_CODI, ESTAT, MULTIPLE);
create index PBL_CONSULTAH_PROCSERV_I on PBL_CONSULTA_HIST (PROCSERV_ID);
create index PBL_CONSULTAH_CREATEDBY_I on PBL_CONSULTA_HIST (CREATEDBY_CODI);
create index PBL_CONH_CREDATCOD_I on PBL_CONSULTA_HIST (CREATEDDATE desc, CREATEDBY_CODI asc);
create index PBL_PROCEDIMENT_ENTITAT_I on PBL_PROCEDIMENT (ENTITAT_ID);

--------------------------------------------------------
--  DDL for Table PBL_PROCEDIMENT_SERVEI
--------------------------------------------------------

create table PBL_PROCEDIMENT_SERVEI
(
    ID                  NUMBER(38) not null constraint PBL_PROCEDIMENT_SERVEI_PK primary key,
    ACTIU               NUMBER(1),
    SERVEI_ID           VARCHAR2(64 CHAR),
    PROCEDIMENT_ID      NUMBER(38) not null constraint PBL_PROCED_PROCSERV_FK references PBL_PROCEDIMENT,
    PROCEDIMENT_CODI    VARCHAR2(20 CHAR),
    CREATEDBY_CODI      VARCHAR2(64 CHAR) constraint PBL_CREATEDBY_PROCSERV_FK references PBL_USUARI,
    CREATEDDATE         TIMESTAMP(6),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR) constraint PBL_LASTMODIFIEDBY_PROVSERV_FK references PBL_USUARI,
    LASTMODIFIEDDATE    TIMESTAMP(6),
    VERSION             NUMBER(38) not null,
    constraint PBL_PROCEDIMENT_SERVEI_UK unique (PROCEDIMENT_ID, SERVEI_ID)
);

--------------------------------------------------------
--  DDL for Table PBL_CONSULTA
--------------------------------------------------------

create table PBL_CONSULTA
(
    ID                     NUMBER(38)         not null constraint PBL_CONSULTA_PK primary key,
    CUSTODIAT              NUMBER(1),
    DEPARTAMENT            VARCHAR2(250 CHAR) not null,
    ERROR                  VARCHAR2(4000 CHAR),
    ESTAT                  NUMBER             not null,
    PETICION_ID            VARCHAR2(26 CHAR)       not null,
    TITULAR_DOCNUM         VARCHAR2(14 CHAR),
    TITULAR_DOCTIP         VARCHAR2(16 CHAR),
    TITULAR_LLING1         VARCHAR2(64 CHAR),
    TITULAR_LLING2         VARCHAR2(64 CHAR),
    TITULAR_NOM            VARCHAR2(64 CHAR),
    PROCSERV_ID            NUMBER(38)         not null constraint PBL_PROCSERV_CONSULTA_FK references PBL_PROCEDIMENT_SERVEI,
    RECOBRIMENT            NUMBER(1),
    TITULAR_NOMCOMPLET     VARCHAR2(122 CHAR),
    FUNCIONARI_NOM         VARCHAR2(128 CHAR),
    FUNCIONARI_DOCNUM      VARCHAR2(16 CHAR)       not null,
    SOLICITUD_ID           VARCHAR2(64 CHAR),
    MULTIPLE               NUMBER(1),
    PARE_ID                NUMBER(38) constraint PBL_CONSULTA_PARE_FK references PBL_CONSULTA,
    CUSTODIA_URL           VARCHAR2(255 CHAR),
    JUSTIFICANT_ERROR      VARCHAR2(2000 CHAR),
    JUSTIFICANT_ESTAT      NUMBER             not null,
    CUSTODIA_ID            VARCHAR2(255 CHAR),
    ARXIU_EXPEDIENT_UUID   VARCHAR2(100 CHAR),
    ARXIU_DOCUMENT_UUID    VARCHAR2(100 CHAR),
    ARXIU_EXPEDIENT_TANCAT NUMBER(1),
    CREATEDBY_CODI         VARCHAR2(64 CHAR) constraint PBL_CREATEDBY_CONSULTA_FK references PBL_USUARI,
    CREATEDDATE            TIMESTAMP(6),
    LASTMODIFIEDBY_CODI    VARCHAR2(64 CHAR) constraint PBL_LASTMODIFIEDBY_CONSULTA_FK references PBL_USUARI,
    LASTMODIFIEDDATE       TIMESTAMP(6),
    VERSION                NUMBER(38)         not null,
    FINALITAT              VARCHAR2(250 CHAR),
    CONSENTIMENT           NUMBER,
    EXPEDIENT_ID           VARCHAR2(25 CHAR),
    DADES_ESPECIFIQUES     VARCHAR2(2048 CHAR),
    ENTITAT_ID             NUMBER(38) constraint PBL_CONSULTA_ENTITAT_FK references PBL_ENTITAT,
    SERVEI_CODI            VARCHAR2(64 CHAR),
    PROCEDIMENT_ID         NUMBER(38) constraint PBL_CONSULTA_PROCED_FK references PBL_PROCEDIMENT
);

create index PBL_CONSULTA_PARE_I on PBL_CONSULTA (PARE_ID);
create index PBL_CONSULTA_PROCSERV_I on PBL_CONSULTA (PROCSERV_ID);
create index PBL_CONSULTA_MULT_I on PBL_CONSULTA (CREATEDBY_CODI, ESTAT, MULTIPLE);
create index PBL_CONSULTA_CREATEDBY_I on PBL_CONSULTA (CREATEDBY_CODI);
create index PBL_CON_CREATEDDATCOD_I on PBL_CONSULTA (CREATEDDATE desc, CREATEDBY_CODI asc);
create index PBL_PROCSERV_SERVEI_I on PBL_PROCEDIMENT_SERVEI (SERVEI_ID);
create index PBL_PROCSERV_PROCED_I on PBL_PROCEDIMENT_SERVEI (PROCEDIMENT_ID);

--------------------------------------------------------
--  DDL for Table PBL_SERVEI_BUS
--------------------------------------------------------

create table PBL_SERVEI_BUS
(
    ID                  NUMBER(38)    not null constraint PBL_SERVEI_BUS_PK primary key,
    SERVEI_ID           VARCHAR2(64 CHAR)  not null,
    URL_DESTI           VARCHAR2(255 CHAR) not null,
    ENTITAT_ID          NUMBER(38)    not null constraint PBL_ENTITAT_SERVBUS_FK references PBL_ENTITAT,
    CREATEDBY_CODI      VARCHAR2(64 CHAR) constraint PBL_CREATEDBY_SERVEI_BUS_FK references PBL_USUARI,
    CREATEDDATE         TIMESTAMP(6),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR) constraint PBL_LASTMODBY_SERVEI_BUS_FK references PBL_USUARI,
    LASTMODIFIEDDATE    TIMESTAMP(6),
    VERSION             NUMBER(38)    not null
);

create index PBL_SERVEI_BUS_SERVEI_I on PBL_SERVEI_BUS (SERVEI_ID);

--------------------------------------------------------
--  DDL for Table PBL_SERVEI_CAMP_GRUP
--------------------------------------------------------

create table PBL_SERVEI_CAMP_GRUP
(
    ID                  NUMBER(38)    not null constraint PBL_SERVEI_CAMP_GRUP_PK primary key,
    NOM                 VARCHAR2(255 CHAR) not null,
    ORDRE               NUMBER,
    SERVEI_ID           VARCHAR2(64 CHAR)  not null,
    CREATEDBY_CODI      VARCHAR2(64 CHAR) constraint PBL_CREATEDBY_SERVEI_GRUP_FK references PBL_USUARI,
    CREATEDDATE         TIMESTAMP(6),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR) constraint PBL_LASTMODBY_SERVEI_GRUP_FK references PBL_USUARI,
    LASTMODIFIEDDATE    TIMESTAMP(6),
    VERSION             NUMBER(38)    not null,
    AJUDA               CLOB,
    PARE_ID             NUMBER(38) constraint PBL_GRUP_PARE_FK references PBL_SERVEI_CAMP_GRUP,
    constraint PBL_SERVEI_CAMP_GRUP_UK unique (NOM, SERVEI_ID)
);

--------------------------------------------------------
--  DDL for Table PBL_SERVEI_CAMP
--------------------------------------------------------

create table PBL_SERVEI_CAMP
(
    ID                    NUMBER(38)    not null constraint PBL_SERVEI_CAMP_PK primary key,
    COMENTARI             VARCHAR2(255 CHAR),
    ENUM_DESCS            VARCHAR2(1024 CHAR),
    ETIQUETA              VARCHAR2(64 CHAR),
    MODIFICABLE           NUMBER(1),
    OBLIGATORI            NUMBER(1),
    ORDRE                 NUMBER,
    PATH                  VARCHAR2(255 CHAR) not null,
    SERVEI_ID             VARCHAR2(64 CHAR)  not null,
    TIPUS                 NUMBER        not null,
    VALOR_DEFECTE         VARCHAR2(64 CHAR),
    VISIBLE               NUMBER(1),
    DATA_FORMAT           VARCHAR2(32 CHAR),
    VALOR_PARE            VARCHAR2(64 CHAR),
    CAMP_PARE_ID          NUMBER(38) constraint PBL_PARE_SERVCAMP_FK references PBL_SERVEI_CAMP,
    GRUP_ID               NUMBER(38) constraint PBL_SRCGRUP_SRVCAMP_FK references PBL_SERVEI_CAMP_GRUP,
    CREATEDBY_CODI        VARCHAR2(64 CHAR) constraint PBL_CREATEDBY_SRVCAMP_FK references PBL_USUARI,
    CREATEDDATE           TIMESTAMP(6),
    LASTMODIFIEDBY_CODI   VARCHAR2(64 CHAR) constraint PBL_LASTMODIFIEDBY_SRVCAMP_FK references PBL_USUARI,
    LASTMODIFIEDDATE      TIMESTAMP(6),
    VERSION               NUMBER(38)    not null,
    VAL_REGEXP            VARCHAR2(100 CHAR),
    VAL_MIN               NUMBER,
    VAL_MAX               NUMBER,
    VAL_DATA_CMP_OP       NUMBER,
    VAL_DATA_CMP_CAMP2_ID NUMBER(38),
    VAL_DATA_CMP_NUM      NUMBER,
    VAL_DATA_CMP_TIPUS    NUMBER,
    INICIALITZAR          NUMBER(1) default '0',
    constraint PBL_SERVEI_CAMP_UK unique (SERVEI_ID, PATH)
);

create index PBL_SERVEI_CAMP_SERVEI_I on PBL_SERVEI_CAMP (SERVEI_ID);
create index PBL_SERVEI_CAMPGR_SERVEI_I on PBL_SERVEI_CAMP_GRUP (SERVEI_ID);

--------------------------------------------------------
--  DDL for Table PBL_SERVEI_CONFIG
--------------------------------------------------------

create table PBL_SERVEI_CONFIG
(
    ID                       NUMBER(38)          not null constraint PBL_SERVEI_CONFIG_PK primary key,
    CUSTODIA_CODI            VARCHAR2(64 CHAR),
    ROLE_NAME                VARCHAR2(64 CHAR),
    SERVEI_ID                VARCHAR2(64 CHAR)        not null,
    CONDICIO_BUS_CLASS       VARCHAR2(255 CHAR),
    ENTITAT_TIPUS            NUMBER,
    JUSTIFICANT_XPATH        VARCHAR2(255 CHAR),
    JUSTIFICANT_TIPUS        NUMBER,
    PERMES_DOCTIP_DNI        NUMBER(1),
    PERMES_DOCTIP_NIE        NUMBER(1),
    PERMES_DOCTIP_NIF        NUMBER(1),
    PERMES_DOCTIP_CIF        NUMBER(1),
    PERMES_DOCTIP_PAS        NUMBER(1),
    ACTIU_CAMP_EXP           NUMBER(1),
    ACTIU_CAMP_LLIN1         NUMBER(1),
    ACTIU_CAMP_LLIN2         NUMBER(1),
    ACTIU_CAMP_NOM           NUMBER(1),
    ACTIU_CAMP_NOMCOMP       NUMBER(1),
    ACTIU_CAMP_DOC           NUMBER(1),
    COMPROVAR_DOCUMENT       NUMBER(1)           not null,
    DOCUMENT_OPCIONAL        NUMBER(1),
    DOCUMENT_OBLIGATORI      NUMBER(1),
    AJUDA                    CLOB,
    FITXER_AJUDA_CONTINGUT   BLOB,
    FITXER_AJUDA_MIME        VARCHAR2(255 CHAR),
    FITXER_AJUDA_NOM         VARCHAR2(255 CHAR),
    ACTIVA_GESTIO_XSD        NUMBER(1),
    UNITAT_DIR3              VARCHAR2(10 CHAR),
    ACTIU                    NUMBER(1)           not null,
    UNITAT_DIR3_FROM_ENTITAT NUMBER(1)           not null,
    CREATEDBY_CODI           VARCHAR2(64 CHAR) constraint PBL_CREATEDBY_SRVEI_CONFIG_FK references PBL_USUARI,
    CREATEDDATE              TIMESTAMP(6),
    LASTMODIFIEDBY_CODI      VARCHAR2(64 CHAR) constraint PBL_LASTMODBY_SERVEI_CONFIG_FK references PBL_USUARI,
    LASTMODIFIEDDATE         TIMESTAMP(6),
    VERSION                  NUMBER(38)          not null,
    MAX_PETICIONS_MIN        NUMBER,
    INI_DADES_ESPECIFIQUES   NUMBER(1) default 0,
    USE_AUTO_CLASSE          NUMBER(1) default '0',
    ADD_DADES_ESPECIFIQUES   NUMBER(1) default '1',
    ENVIAR_SOLICITANT        NUMBER(1) default 0 not null
);

--------------------------------------------------------
--  DDL for Table PBL_SERVEI_JUSTIF_CAMP
--------------------------------------------------------

create table PBL_SERVEI_JUSTIF_CAMP
(
    ID                  NUMBER(38)            not null constraint PBL_SERVEI_JUSTIF_CAMP_PK primary key,
    LOCALE_IDIOMA       VARCHAR2(2 CHAR)           not null,
    LOCALE_REGIO        VARCHAR2(2 CHAR)           not null,
    SERVEI_ID           VARCHAR2(64 CHAR)          not null,
    TRADUCCIO           VARCHAR2(255 CHAR),
    XPATH               VARCHAR2(255 CHAR)         not null,
    CREATEDBY_CODI      VARCHAR2(64 CHAR) constraint PBL_CREATEDBY_SRVJUS_FK references PBL_USUARI,
    CREATEDDATE         TIMESTAMP(6),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR) constraint PBL_LASTMODBY_SRVJUS_FK references PBL_USUARI,
    LASTMODIFIEDDATE    TIMESTAMP(6),
    VERSION             NUMBER(38)            not null,
    DOCUMENT            NUMBER(1) default '0' not null,
    constraint PBL_SERVEI_JUSTIF_CAMP_UK unique (SERVEI_ID, XPATH)
);

create index PBL_SERVEI_JUSCAM_SERVEI_I on PBL_SERVEI_JUSTIF_CAMP (SERVEI_ID);

--------------------------------------------------------
--  DDL for Table PBL_EXPLOT_TEMPS
--------------------------------------------------------

create table PBL_EXPLOT_TEMPS
(
    ID        NUMBER(38) not null constraint PK_PBL_EXPLOT_TEMPS primary key,
    DATA      DATE,
    ANUALITAT NUMBER,
    MES       NUMBER,
    TRIMESTRE NUMBER,
    SETMANA   NUMBER,
    DIA       VARCHAR2(3 CHAR)
);

--------------------------------------------------------
--  DDL for Table PBL_EXPLOT_CONSULTA_DIM
--------------------------------------------------------

create table PBL_EXPLOT_CONSULTA_DIM
(
    ID             NUMBER(38) not null constraint PBL_EXPLOT_CONSULTA_DIM_PK primary key,
    ENTITAT_ID     NUMBER(38),
    PROCEDIMENT_ID NUMBER(38),
    SERVEI_CODI    VARCHAR2(64 CHAR),
    USUARI_CODI    VARCHAR2(255 CHAR),
    constraint PBL_EXPLOT_CONSULTA_DIM_UK unique (ENTITAT_ID, PROCEDIMENT_ID, SERVEI_CODI, USUARI_CODI)
);

--------------------------------------------------------
--  DDL for Table PBL_EXPLOT_CONSULTA_FET
--------------------------------------------------------

create table PBL_EXPLOT_CONSULTA_FET
(
    ID                 NUMBER(38) not null constraint PBL_EXPLOT_CONSULTA_FET_PK primary key,
    NUM_REC_OK         NUMBER(38),
    NUM_REC_ERROR      NUMBER(38),
    NUM_REC_PEND       NUMBER(38),
    NUM_REC_PROC       NUMBER(38),
    NUM_REC_MASS_OK    NUMBER(38),
    NUM_REC_MASS_ERROR NUMBER(38),
    NUM_REC_MASS_PEND  NUMBER(38),
    NUM_REC_MASS_PROC  NUMBER(38),
    NUM_WEB_OK         NUMBER(38),
    NUM_WEB_ERROR      NUMBER(38),
    NUM_WEB_PEND       NUMBER(38),
    NUM_WEB_PROC       NUMBER(38),
    NUM_WEB_MASS_OK    NUMBER(38),
    NUM_WEB_MASS_ERROR NUMBER(38),
    NUM_WEB_MASS_PEND  NUMBER(38),
    NUM_WEB_MASS_PROC  NUMBER(38),
    DIMENSIO_ID        NUMBER(38) not null constraint PBL_FET_DIM_FK references PBL_EXPLOT_CONSULTA_DIM,
    TEMPS_ID           NUMBER(38) not null constraint PBL_FET_TMP_FK references PBL_EXPLOT_TEMPS
);

--------------------------------------------------------
--  DDL for Table PBL_CONSULTA_DO
--------------------------------------------------------

create table PBL_CONSULTA_DO
(
    ID              NUMBER(38)         not null constraint PBL_CONSULTA_DO_PK primary key,
    ENTITATCODI     VARCHAR2(64 CHAR)  not null,
    ENTITATNOM      VARCHAR2(255 CHAR) not null,
    ENTITATNIF      VARCHAR2(255 CHAR) not null,
    ENTITATTIPUS    VARCHAR2(16 CHAR)  not null,
    DEPARTAMENTCODI VARCHAR2(250 CHAR),
    DEPARTAMENTNOM  VARCHAR2(250 CHAR),
    PROCEDIMENTCODI VARCHAR2(20 CHAR)  not null,
    PROCEDIMENTNOM  VARCHAR2(255 CHAR) not null,
    SERVEICODI      VARCHAR2(64 CHAR)  not null,
    SERVEINOM       VARCHAR2(512 CHAR) not null,
    EMISSORNOM      VARCHAR2(50 CHAR)  not null,
    EMISSORNIF      VARCHAR2(16 CHAR)  not null,
    CONSENTIMENT    VARCHAR2(16 CHAR),
    FINALITAT       VARCHAR2(250 CHAR),
    TITULARDOCTIP   VARCHAR2(16 CHAR),
    SOLICITUDID     VARCHAR2(64 CHAR)  not null,
    DATA            TIMESTAMP(6)       not null,
    TIPUS           VARCHAR2(16 CHAR)  not null,
    RESULTAT        VARCHAR2(16 CHAR),
    MULTIPLE        NUMBER(1)
);

--------------------------------------------------------
--  DDL for Table PBL_CONSULTA_HIST_DO
--------------------------------------------------------

create table PBL_CONSULTA_HIST_DO
(
    ID              NUMBER(38)   not null constraint PBL_CONSULTA_HIST_DO_PK primary key,
    ENTITATCODI     VARCHAR2(64 CHAR),
    ENTITATNOM      VARCHAR2(255 CHAR),
    ENTITATNIF      VARCHAR2(255 CHAR),
    ENTITATTIPUS    VARCHAR2(16 CHAR),
    DEPARTAMENTCODI VARCHAR2(250 CHAR),
    DEPARTAMENTNOM  VARCHAR2(250 CHAR),
    PROCEDIMENTCODI VARCHAR2(20 CHAR),
    PROCEDIMENTNOM  VARCHAR2(255 CHAR),
    SERVEICODI      VARCHAR2(64 CHAR),
    SERVEINOM       VARCHAR2(512 CHAR),
    EMISSORNOM      VARCHAR2(50 CHAR),
    EMISSORNIF      VARCHAR2(16 CHAR),
    CONSENTIMENT    VARCHAR2(16 CHAR),
    FINALITAT       VARCHAR2(250 CHAR),
    TITULARDOCTIP   VARCHAR2(16 CHAR),
    SOLICITUDID     VARCHAR2(64 CHAR),
    DATA            TIMESTAMP(6) not null,
    TIPUS           VARCHAR2(16 CHAR),
    RESULTAT        VARCHAR2(16 CHAR),
    MULTIPLE        NUMBER(1)
);

-- create global temporary table HT_PBL_CONSULTA_HIST ( ID NUMBER(19) not null ) on commit delete rows;
-- create global temporary table HT_PBL_CONSULTA ( ID NUMBER(19) not null ) on commit delete rows;
-- create global temporary table HT_SUPERCONSULTA ( ID NUMBER(19) not null ) on commit delete rows;

--------------------------------------------------------
--  DDL for Table PBL_CONSULTA_LIST
--------------------------------------------------------

create table PBL_CONSULTA_LIST
(
    ID               NUMBER(38)         not null constraint PBL_CONSULTA_LIST_PK primary key,
    PETICIOID        VARCHAR2(26 CHAR)  not null,
    SOLICITUDID      VARCHAR2(64 CHAR),
    DATA             TIMESTAMP(6)       not null,
    DEPARTAMENT      VARCHAR2(250 CHAR) not null,
    RECOBRIMENT      NUMBER(1),
    MULTIPLE         NUMBER(1),
    USUARICODI       VARCHAR2(64 CHAR)  not null,
    USUARINOM        VARCHAR2(200 CHAR),
    FUNCIONARINIF    VARCHAR2(16 CHAR),
    FUNCIONARINOM    VARCHAR2(128 CHAR),
    TITULARNOM       VARCHAR2(122 CHAR),
    TITULARDOCTIP    VARCHAR2(16 CHAR),
    TITULARDOCNUM    VARCHAR2(14 CHAR),
    PROCEDIMENTID    NUMBER(38)         not null,
    PROCEDIMENTCODI  VARCHAR2(20 CHAR)  not null,
    PROCEDIMENTNOM   VARCHAR2(255 CHAR) not null,
    SERVEICODI       VARCHAR2(64 CHAR)  not null,
    SERVEINOM        VARCHAR2(512 CHAR) not null,
    ESTAT            VARCHAR2(16 CHAR)  not null,
    ERROR            VARCHAR2(4000 CHAR),
    JUSTIFICANTESTAT VARCHAR2(16 CHAR),
    ENTITATID        NUMBER(38)         not null,
    ENTITATCODI      VARCHAR2(64 CHAR)  not null,
    PAREID           NUMBER(38)
);

--------------------------------------------------------
--  DDL for Table PBL_CONSULTA_HIST_LIST
--------------------------------------------------------

create table PBL_CONSULTA_HIST_LIST
(
    ID               NUMBER(38)        not null constraint PBL_CONSULTA_HIST_LIST_PK primary key,
    PETICIOID        VARCHAR2(26 CHAR) not null,
    SOLICITUDID      VARCHAR2(64 CHAR),
    DATA             TIMESTAMP(6)      not null,
    DEPARTAMENT      VARCHAR2(250 CHAR),
    RECOBRIMENT      NUMBER(1),
    MULTIPLE         NUMBER(1),
    USUARICODI       VARCHAR2(64 CHAR),
    USUARINOM        VARCHAR2(200 CHAR),
    FUNCIONARINIF    VARCHAR2(16 CHAR),
    FUNCIONARINOM    VARCHAR2(128 CHAR),
    TITULARNOM       VARCHAR2(122 CHAR),
    TITULARDOCTIP    VARCHAR2(16 CHAR),
    TITULARDOCNUM    VARCHAR2(14 CHAR),
    PROCEDIMENTID    NUMBER(38),
    PROCEDIMENTCODI  VARCHAR2(20 CHAR),
    PROCEDIMENTNOM   VARCHAR2(255 CHAR),
    SERVEICODI       VARCHAR2(64 CHAR),
    SERVEINOM        VARCHAR2(512 CHAR),
    ESTAT            VARCHAR2(16 CHAR),
    ERROR            VARCHAR2(4000 CHAR),
    JUSTIFICANTESTAT VARCHAR2(16 CHAR),
    ENTITATID        NUMBER(38),
    ENTITATCODI      VARCHAR2(64 CHAR),
    PAREID           NUMBER(38)
);


--------------------------------------------------------
--  Procedimiento Almacenado GETSECUENCIAIDPETICION
--  Creació del procedimiento almacenado para generar las secuencias del id de petición
--------------------------------------------------------

CREATE OR REPLACE PROCEDURE "GETSECUENCIAIDPETICION" (prefijo_param in varchar2, on_Secuencial out number) AS rRegistro ROWID;
begin
    select ROWID, SECUENCIA+1 into rRegistro, on_Secuencial from core_req_secuencia_id_peticion where PREFIJO = prefijo_param for update;
    update CORE_REQ_SECUENCIA_ID_PETICION set SECUENCIA = on_Secuencial, FECHAGENERACION=sysdate where rowid = rRegistro;
    commit;
    exception when no_data_found then on_Secuencial := 1;
    insert into CORE_REQ_SECUENCIA_ID_PETICION (PREFIJO, SECUENCIA, FECHAGENERACION) values (prefijo_param, on_Secuencial,SYSDATE);
commit;
end;
/
