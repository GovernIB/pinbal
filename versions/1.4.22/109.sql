
ALTER TABLE PBL_USUARI
ADD (
    EMAIL VARCHAR2(200 CHAR),
    IDIOMA VARCHAR2(2 CHAR) DEFAULT 'CA'
);