
CREATE TABLE PBL_CONSULTA
(
  ID                   NUMBER(19)               NOT NULL,
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  CUSTODIAT            NUMBER(1),
  DEPARTAMENT          VARCHAR2(64 CHAR)        NOT NULL,
  ERROR                VARCHAR2(4000 CHAR),
  ESTAT                NUMBER(10)               NOT NULL,
  PETICION_ID          VARCHAR2(26 CHAR)        NOT NULL,
  SOLICITUD_ID         VARCHAR2(64 CHAR),
  FUNCIONARI_NOM       VARCHAR2(128 CHAR),
  FUNCIONARI_DOCNUM    VARCHAR2(16 CHAR),
  TITULAR_DOCNUM       VARCHAR2(14 CHAR),
  TITULAR_DOCTIP       VARCHAR2(16 CHAR),
  TITULAR_LLING1       VARCHAR2(40 CHAR),
  TITULAR_LLING2       VARCHAR2(40 CHAR),
  TITULAR_NOM          VARCHAR2(40 CHAR),
  TITULAR_NOMCOMPLET   VARCHAR2(122 CHAR),
  VERSION              NUMBER(19)               NOT NULL,
  CREATEDBY_CODI       VARCHAR2(64 CHAR),
  LASTMODIFIEDBY_CODI  VARCHAR2(64 CHAR),
  PROCSERV_ID          NUMBER(19)               NOT NULL,
  PARE_ID              NUMBER(19),
  RECOBRIMENT          NUMBER(1),
  MULTIPLE             NUMBER(1)
);


CREATE TABLE PBL_ENTITAT
(
  ID                   NUMBER(19)               NOT NULL,
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  ACTIVA               NUMBER(1),
  CIF                  VARCHAR2(16 CHAR)        NOT NULL,
  CODI                 VARCHAR2(64 CHAR)        NOT NULL,
  NOM                  VARCHAR2(255 CHAR)       NOT NULL,
  TIPUS                NUMBER(10)               NOT NULL,
  VERSION              NUMBER(19)               NOT NULL,
  CREATEDBY_CODI       VARCHAR2(64 CHAR),
  LASTMODIFIEDBY_CODI  VARCHAR2(64 CHAR)
);


CREATE TABLE PBL_ENTITAT_SERVEI
(
  ID                   NUMBER(19)               NOT NULL,
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  SERVEI_ID            VARCHAR2(64 CHAR),
  VERSION              NUMBER(19)               NOT NULL,
  CREATEDBY_CODI       VARCHAR2(64 CHAR),
  LASTMODIFIEDBY_CODI  VARCHAR2(64 CHAR),
  ENTITAT_ID           NUMBER(19)               NOT NULL
);


CREATE TABLE PBL_ENTITAT_USUARI
(
  ID                   NUMBER(19)               NOT NULL,
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  DELEGAT              NUMBER(1),
  DEPARTAMENT          VARCHAR2(64 CHAR)        NOT NULL,
  PRINCIPAL            NUMBER(1),
  REPRESENTANT         NUMBER(1),
  AUDITOR              NUMBER(1),
  APLICACIO            NUMBER(1),
  VERSION              NUMBER(19)               NOT NULL,
  CREATEDBY_CODI       VARCHAR2(64 CHAR),
  LASTMODIFIEDBY_CODI  VARCHAR2(64 CHAR),
  ENTITAT_ID           NUMBER(19)               NOT NULL,
  USUARI_ID            VARCHAR2(64 CHAR)        NOT NULL
);


CREATE TABLE PBL_PROCEDIMENT
(
  ID                   NUMBER(19)               NOT NULL,
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  ACTIU                NUMBER(1),
  CODI                 VARCHAR2(20 CHAR)        NOT NULL,
  NOM                  VARCHAR2(255 CHAR)       NOT NULL,
  DEPARTAMENT          VARCHAR2(64 CHAR),
  VERSION              NUMBER(19)               NOT NULL,
  CREATEDBY_CODI       VARCHAR2(64 CHAR),
  LASTMODIFIEDBY_CODI  VARCHAR2(64 CHAR),
  ENTITAT_ID           NUMBER(19)               NOT NULL
);


CREATE TABLE PBL_PROCEDIMENT_SERVEI
(
  ID                   NUMBER(19)               NOT NULL,
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  ACTIU                NUMBER(1),
  SERVEI_ID            VARCHAR2(64 CHAR),
  VERSION              NUMBER(19)               NOT NULL,
  CREATEDBY_CODI       VARCHAR2(64 CHAR),
  LASTMODIFIEDBY_CODI  VARCHAR2(64 CHAR),
  PROCEDIMENT_ID       NUMBER(19)               NOT NULL
);


CREATE TABLE PBL_SERVEI_CAMP
(
  ID                   NUMBER(19)               NOT NULL,
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  COMENTARI            VARCHAR2(255 CHAR),
  ENUM_DESCS           VARCHAR2(1024 CHAR),
  DATA_FORMAT          VARCHAR2(32 CHAR),
  ETIQUETA             VARCHAR2(64 CHAR),
  MODIFICABLE          NUMBER(1),
  OBLIGATORI           NUMBER(1),
  ORDRE                NUMBER(10),
  PATH                 VARCHAR2(255 CHAR)       NOT NULL,
  VALOR_PARE           VARCHAR2(64 CHAR),
  CAMP_PARE_ID         NUMBER(19),
  SERVEI_ID            VARCHAR2(64 CHAR)        NOT NULL,
  TIPUS                NUMBER(10)               NOT NULL,
  VALOR_DEFECTE        VARCHAR2(64 CHAR),
  VISIBLE              NUMBER(1),
  GRUP_ID              NUMBER(19),
  VERSION              NUMBER(19)               NOT NULL,
  CREATEDBY_CODI       VARCHAR2(64 CHAR),
  LASTMODIFIEDBY_CODI  VARCHAR2(64 CHAR)
);


CREATE TABLE PBL_SERVEI_CAMP_GRUP
(
  ID                   NUMBER(19)               NOT NULL,
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  NOM                  VARCHAR2(255 CHAR)       NOT NULL,
  ORDRE                NUMBER(10),
  SERVEI_ID            VARCHAR2(64 CHAR)        NOT NULL,
  VERSION              NUMBER(19)               NOT NULL,
  CREATEDBY_CODI       VARCHAR2(64 CHAR),
  LASTMODIFIEDBY_CODI  VARCHAR2(64 CHAR)
);


CREATE TABLE PBL_SERVEI_CONFIG
(
  ID                   NUMBER(19)               NOT NULL,
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  CUSTODIA_CODI        VARCHAR2(64 CHAR),
  ROLE_NAME            VARCHAR2(64 CHAR),
  CONDICIO_BUS_CLASS   VARCHAR2(255 CHAR),
  ENTITAT_TIPUS        NUMBER(10),
  SERVEI_ID            VARCHAR2(64 CHAR)        NOT NULL,
  JUSTIFICANT_TIPUS    NUMBER(10),
  JUSTIFICANT_XPATH    VARCHAR2(255 CHAR),
  PERMES_DOCTIP_DNI    NUMBER(1),
  PERMES_DOCTIP_NIE    NUMBER(1),
  PERMES_DOCTIP_NIF    NUMBER(1),
  PERMES_DOCTIP_CIF    NUMBER(1),
  PERMES_DOCTIP_PAS    NUMBER(1),
  ACTIU_CAMP_NOMCOMP   NUMBER(1),
  ACTIU_CAMP_LLIN1     NUMBER(1),
  ACTIU_CAMP_LLIN2     NUMBER(1),
  ACTIU_CAMP_NOM       NUMBER(1),
  ACTIU_CAMP_DOC       NUMBER(1),
  DOCUMENT_OBLIGATORI  NUMBER(1),
  COMPROVAR_DOCUMENT   NUMBER(1),
  VERSION              NUMBER(19)               NOT NULL,
  CREATEDBY_CODI       VARCHAR2(64 CHAR),
  LASTMODIFIEDBY_CODI  VARCHAR2(64 CHAR)
);


CREATE TABLE PBL_SERVEI_BUS
(
  ID                   NUMBER(19)               NOT NULL,
  CREATEDDATE          TIMESTAMP (6),
  LASTMODIFIEDDATE     TIMESTAMP (6),
  URL_DESTI            VARCHAR2(255 CHAR)       NOT NULL,
  SERVEI_ID	           VARCHAR2(64 CHAR)        NOT NULL,
  ENTITAT_ID           NUMBER(19)               NOT NULL,
  VERSION              NUMBER(19)               NOT NULL,
  CREATEDBY_CODI       VARCHAR2(64 CHAR),
  LASTMODIFIEDBY_CODI  VARCHAR2(64 CHAR)
);


CREATE TABLE PBL_SERVEI_JUSTIF_CAMP
(
  ID                   NUMBER(19)               NOT NULL,
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  LOCALE_IDIOMA        VARCHAR2(2 CHAR)         NOT NULL,
  LOCALE_REGIO         VARCHAR2(2 CHAR)         NOT NULL,
  SERVEI_ID            VARCHAR2(64 CHAR)        NOT NULL,
  TRADUCCIO            VARCHAR2(255 CHAR),
  VERSION              NUMBER(19)               NOT NULL,
  XPATH                VARCHAR2(255 CHAR)       NOT NULL,
  CREATEDBY_CODI       VARCHAR2(64 CHAR),
  LASTMODIFIEDBY_CODI  VARCHAR2(64 CHAR)
);


CREATE TABLE PBL_USUARI
(
  CODI          VARCHAR2(64 CHAR)               NOT NULL,
  NIF           VARCHAR2(15 CHAR),
  NOM           VARCHAR2(200 CHAR),
  INICIALITZAT  NUMBER(1),
  VERSION       NUMBER(19)               NOT NULL
);


CREATE SEQUENCE HIBERNATE_SEQUENCE;


CREATE INDEX PBL_ENTISERV_ENTITAT_I ON PBL_ENTITAT_SERVEI
(ENTITAT_ID);


CREATE INDEX PBL_ENTISERV_SERVEI_I ON PBL_ENTITAT_SERVEI
(SERVEI_ID);


CREATE INDEX PBL_ENTIUSR_ENTITAT_I ON PBL_ENTITAT_USUARI
(ENTITAT_ID);


CREATE INDEX PBL_ENTIUSR_USUARI_I ON PBL_ENTITAT_USUARI
(USUARI_ID);


CREATE INDEX PBL_PROCEDIMENT_ENTITAT_I ON PBL_PROCEDIMENT
(ENTITAT_ID);


CREATE INDEX PBL_PROCSERV_PROCED_I ON PBL_PROCEDIMENT_SERVEI
(PROCEDIMENT_ID);


CREATE INDEX PBL_PROCSERV_SERVEI_I ON PBL_PROCEDIMENT_SERVEI
(SERVEI_ID);


CREATE INDEX PBL_SERVEI_CAMP_SERVEI_I ON PBL_SERVEI_CAMP
(SERVEI_ID);


CREATE INDEX PBL_SERVEI_CAMPGR_SERVEI_I ON PBL_SERVEI_CAMP_GRUP
(SERVEI_ID);


CREATE INDEX PBL_SERVEI_CONFIG_SERVEI_I ON PBL_SERVEI_CONFIG
(SERVEI_ID);


CREATE INDEX PBL_SERVEI_BUS_SERVEI_I ON PBL_SERVEI_BUS
(SERVEI_ID);


CREATE INDEX PBL_SERVEI_JUSCAM_SERVEI_I ON PBL_SERVEI_JUSTIF_CAMP
(SERVEI_ID);


CREATE INDEX PBL_CONSULTA_PROCSERV_I ON PBL_CONSULTA
(PROCSERV_ID);


CREATE INDEX PBL_CONSULTA_CREATEDBY_I ON PBL_CONSULTA
(CREATEDBY_CODI);


ALTER TABLE PBL_USUARI ADD (
  CONSTRAINT PBL_USUCODI_PK PRIMARY KEY (CODI),
  CONSTRAINT PBL_USUNIF_UK UNIQUE (NIF));

ALTER TABLE PBL_ENTITAT ADD (
  CONSTRAINT PBL_ENTID_PK PRIMARY KEY (ID),
  CONSTRAINT PBL_ENTCOD_UK UNIQUE (CODI));

ALTER TABLE PBL_ENTITAT_SERVEI ADD (
  CONSTRAINT PBL_ENSID_PK PRIMARY KEY (ID),
  CONSTRAINT PBL_ENSID_UK UNIQUE (ENTITAT_ID, SERVEI_ID));

ALTER TABLE PBL_ENTITAT_USUARI ADD (
  CONSTRAINT PBL_ENUID_PK PRIMARY KEY (ID),
  CONSTRAINT PBL_ENUID_UK UNIQUE (ENTITAT_ID, USUARI_ID));

ALTER TABLE PBL_PROCEDIMENT ADD (
  CONSTRAINT PBL_PROID_PK PRIMARY KEY (ID),
  CONSTRAINT PBL_PROID_UK UNIQUE (CODI));

ALTER TABLE PBL_PROCEDIMENT_SERVEI ADD (
  CONSTRAINT PBL_PRSID_PK PRIMARY KEY (ID),
  CONSTRAINT PBL_PRSID_UK UNIQUE (PROCEDIMENT_ID, SERVEI_ID));

ALTER TABLE PBL_SERVEI_CAMP ADD (
  CONSTRAINT PBL_SRCID_PK PRIMARY KEY (ID),
  CONSTRAINT PBL_SRCID_UK UNIQUE (SERVEI_ID, PATH));

ALTER TABLE PBL_SERVEI_CAMP_GRUP ADD (
  CONSTRAINT PBL_SRCGID_PK PRIMARY KEY (ID));  

ALTER TABLE PBL_SERVEI_CONFIG ADD (
  CONSTRAINT PBL_SRGID_PK PRIMARY KEY (ID),
  CONSTRAINT PBL_SRGID_UK UNIQUE (SERVEI_ID));

ALTER TABLE PBL_SERVEI_BUS ADD (
  CONSTRAINT PBL_SRBID_PK PRIMARY KEY (ID));

ALTER TABLE PBL_SERVEI_JUSTIF_CAMP ADD (
  CONSTRAINT PBL_SRJSCP_PK PRIMARY KEY (ID),
  CONSTRAINT PBL_SRJSCP_UK UNIQUE UNIQUE (SERVEI_ID, XPATH));

ALTER TABLE PBL_CONSULTA ADD (
  CONSTRAINT PBL_CONID_PK PRIMARY KEY (ID));


ALTER TABLE PBL_ENTITAT ADD (
  CONSTRAINT PBL_CREATEDBY_USUARI_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES PBL_USUARI (CODI),
  CONSTRAINT PBL_LASTMODIFIEDBY_USUARI_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES PBL_USUARI (CODI));

ALTER TABLE PBL_ENTITAT_SERVEI ADD (
  CONSTRAINT PBL_CREATEDBY_ENTISERV_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES PBL_USUARI (CODI),
  CONSTRAINT PBL_LASTMODIFIEDBY_ENTISERV_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES PBL_USUARI (CODI),
  CONSTRAINT PBL_ENTITAT_ENTISERV_FK FOREIGN KEY (ENTITAT_ID) 
    REFERENCES PBL_ENTITAT (ID));

ALTER TABLE PBL_ENTITAT_USUARI ADD (
  CONSTRAINT PBL_CREATEDBY_ENTIUSR_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES PBL_USUARI (CODI),
  CONSTRAINT PBL_LASTMODIFIEDBY_ENTIUSR_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES PBL_USUARI (CODI),
  CONSTRAINT PBL_ENTITAT_ENTIUSR_FK FOREIGN KEY (ENTITAT_ID) 
    REFERENCES PBL_ENTITAT (ID),
  CONSTRAINT PBL_USUARI_ENTIUSR_FK FOREIGN KEY (USUARI_ID) 
    REFERENCES PBL_USUARI (CODI));

ALTER TABLE PBL_PROCEDIMENT ADD (
  CONSTRAINT PBL_CREATEDBY_PROCED_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES PBL_USUARI (CODI),
  CONSTRAINT PBL_LASTMODIFIEDBY_PROCED_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES PBL_USUARI (CODI),
  CONSTRAINT PBL_ENTITAT_PROCED_FK FOREIGN KEY (ENTITAT_ID) 
    REFERENCES PBL_ENTITAT (ID));

ALTER TABLE PBL_PROCEDIMENT_SERVEI ADD (
  CONSTRAINT PBL_CREATEDBY_PROCSERV_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES PBL_USUARI (CODI),
  CONSTRAINT PBL_LASTMODIFIEDBY_PROCSERV_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES PBL_USUARI (CODI),
  CONSTRAINT PBL_PROCED_PROCSERV_FK FOREIGN KEY (PROCEDIMENT_ID) 
    REFERENCES PBL_PROCEDIMENT (ID));

ALTER TABLE PBL_SERVEI_CAMP ADD (
  CONSTRAINT PBL_CREATEDBY_SRVCAMP_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES PBL_USUARI (CODI),
  CONSTRAINT PBL_LASTMODIFIEDBY_SRVCAMP_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES PBL_USUARI (CODI),
  CONSTRAINT PBL_PARE_SERVCAMP_FK FOREIGN KEY (CAMP_PARE_ID) 
    REFERENCES PBL_SERVEI_CAMP (ID),
  CONSTRAINT PBL_SRCGRUP_SRVCAMP_FK FOREIGN KEY (GRUP_ID) 
    REFERENCES PBL_SERVEI_CAMP_GRUP (ID));

ALTER TABLE PBL_SERVEI_CAMP_GRUP ADD (
  CONSTRAINT PBL_CREATEDBY_SRCGRUP_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES PBL_USUARI (CODI),
  CONSTRAINT PBL_LASTMODIFIEDBY_SRCGRUP_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES PBL_USUARI (CODI));

ALTER TABLE PBL_SERVEI_CONFIG ADD (
  CONSTRAINT PBL_CREATEDBY_SRVCONF_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES PBL_USUARI (CODI),
  CONSTRAINT PBL_LASTMODIFIEDBY_SRVCONF_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES PBL_USUARI (CODI));

ALTER TABLE PBL_CONSULTA ADD (
  CONSTRAINT PBL_CREATEDBY_CONSULTA_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES PBL_USUARI (CODI),
  CONSTRAINT PBL_LASTMODIFIEDBY_CONSULTA_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES PBL_USUARI (CODI),
  CONSTRAINT PBL_PROCSERV_CONSULTA_FK FOREIGN KEY (PROCSERV_ID) 
    REFERENCES PBL_PROCEDIMENT_SERVEI (ID),
  CONSTRAINT PBL_CONSULTA_PARE_FK FOREIGN KEY (PARE_ID) 
    REFERENCES PBL_CONSULTA (ID));

ALTER TABLE PBL_SERVEI_BUS ADD (
   CONSTRAINT PBL_CREATEDBY_SRVBUS_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES PBL_USUARI (CODI)),
  CONSTRAINT PBL_LASTMODIFIEDBY_SRVBUS_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES PBL_USUARI (CODI),
  CONSTRAINT PBL_ENTITAT_SRVBUS_FK FOREIGN KEY (ENTITAT_ID) 
    REFERENCES PBL_ENTITAT (ID));

ALTER TABLE PBL_SERVEI_JUSTIF_CAMP ADD (
  CONSTRAINT PBL_CREATEDBY_SRJSCAMP_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES PBL_USUARI (CODI),
  CONSTRAINT PBL_LASTMODIFIEDBY_SRJSCAMP_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES PBL_USUARI (CODI));
