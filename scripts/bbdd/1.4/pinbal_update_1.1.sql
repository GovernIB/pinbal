
ALTER TABLE PBL_CONSULTA ADD SOLICITUD_ID VARCHAR2(64);
UPDATE PBL_CONSULTA SET SOLICITUD_ID = PETICION_ID;
ALTER TABLE PBL_CONSULTA MODIFY SOLICITUD_ID;

ALTER TABLE PBL_CONSULTA ADD MULTIPLE NUMBER(1);
UPDATE PBL_CONSULTA SET MULTIPLE = 0;
ALTER TABLE PBL_CONSULTA ADD PARE_ID NUMBER(19);
ALTER TABLE PBL_CONSULTA ADD CONSTRAINT PBL_CONSULTA_PARE_FK FOREIGN KEY (PARE_ID) REFERENCES PBL_CONSULTA (ID);
ALTER TABLE PBL_CONSULTA DROP UNIQUE (PETICION_ID);