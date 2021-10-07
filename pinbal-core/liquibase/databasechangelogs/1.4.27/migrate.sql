-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 23/09/21 15:33
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.3.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.4.27/168.yaml::1632403745823-1::limit
ALTER TABLE core_transmision MODIFY apellido1titular VARCHAR2(50);

ALTER TABLE core_transmision MODIFY apellido2titular VARCHAR2(50);

ALTER TABLE core_transmision MODIFY nombretitular VARCHAR2(50);

ALTER TABLE core_transmision MODIFY nombrecompletotitular VARCHAR2(160);

ALTER TABLE core_transmision MODIFY nombrefuncionario VARCHAR2(160);

-- Changeset db/changelog/changes/1.4.27/168.yaml::1632403745823-2::limit
ALTER TABLE core_transmision ADD automatizado NUMBER(1);

ALTER TABLE core_transmision ADD clasetramite NUMBER(2);

-- Changeset db/changelog/changes/1.4.27/168.yaml::1632403745823-3::limit
COMMENT ON COLUMN core_transmision.automatizado IS 'Indica si el procedimiento es o no automatizado';

COMMENT ON COLUMN core_transmision.clasetramite IS 'Indica la clase del trámite';

UPDATE core_parametro_configuracion SET valor = '4.25.0' WHERE nombre='version.datamodel.scsp';

-- Changeset db/changelog/changes/1.4.27/168.yaml::1632403745823-4::limit
INSERT INTO core_parametro_configuracion (nombre, descripcion, valor) VALUES ('nombre.ministerio', 'Nombre del Ministerio', 'Ministerio de Asuntos Económicos y Transformación Digital');

-- Changeset db/changelog/changes/1.4.27/168.yaml::1632403745823-5::limit
UPDATE core_req_cesionarios_servicios SET bloqueado = 1 WHERE organismo in (select id from core_organismo_cesionario where bloqueado = 1);

