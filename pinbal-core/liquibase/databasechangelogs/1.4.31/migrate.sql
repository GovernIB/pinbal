-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 05/05/22 11:21
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.3.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.4.31/150.yaml::1650469587624-1::limit
CREATE TABLE pbl_config_group (code VARCHAR2(128 CHAR) NOT NULL, description_key VARCHAR2(512 CHAR), position NUMBER(3) NOT NULL, parent_code VARCHAR2(128 CHAR));
ALTER TABLE pbl_config_group ADD PRIMARY KEY (code);

grant select, update, insert, delete on pbl_config_group to www_pinbal;

INSERT INTO pbl_config_group (code, position, description_key) VALUES ('GENERAL', '0', 'propietat.grup.general');
INSERT INTO pbl_config_group (code, parent_code, position, description_key) VALUES ('OPCIONS_BORRAR', 'GENERAL', '0', 'propietat.grup.general.borrar');
INSERT INTO pbl_config_group (code, parent_code, position, description_key) VALUES ('JUSTIFICANT', 'GENERAL', '1', 'propietat.grup.general.justificant');
INSERT INTO pbl_config_group (code, parent_code, position, description_key) VALUES ('CONVERSIO', 'GENERAL', '2', 'propietat.grup.general.conversio');
INSERT INTO pbl_config_group (code, parent_code, position, description_key) VALUES ('MAPEIG', 'GENERAL', '3', 'propietat.grup.general.mapeig');
INSERT INTO pbl_config_group (code, parent_code, position, description_key) VALUES ('RECOBRIMENT', 'GENERAL', '4', 'propietat.grup.general.recobriment');
INSERT INTO pbl_config_group (code, parent_code, position, description_key) VALUES ('EMAIL', 'GENERAL', '5', 'propietat.grup.general.email');
INSERT INTO pbl_config_group (code, position, description_key) VALUES ('PLUGINS', '1', 'propietat.grup.plugins');
INSERT INTO pbl_config_group (code, parent_code, position, description_key) VALUES ('USUARIS', 'PLUGINS', '0', 'propietat.grup.plugins.usuaris');
INSERT INTO pbl_config_group (code, parent_code, position, description_key) VALUES ('SIGNATURA', 'PLUGINS', '1', 'propietat.grup.plugins.signatura');
INSERT INTO pbl_config_group (code, parent_code, position, description_key) VALUES ('FIRMA_SERVIDOR', 'PLUGINS', '2', 'propietat.grup.plugins.firma.servidor');
INSERT INTO pbl_config_group (code, parent_code, position, description_key) VALUES ('CUSTODIA', 'PLUGINS', '3', 'propietat.grup.plugins.custodia');
INSERT INTO pbl_config_group (code, parent_code, position, description_key) VALUES ('ARXIU', 'PLUGINS', '4', 'propietat.grup.plugins.arxiu');
INSERT INTO pbl_config_group (code, parent_code, position, description_key) VALUES ('UNITATS', 'PLUGINS', '5', 'propietat.grup.plugins.unitats');
INSERT INTO pbl_config_group (code, position, description_key) VALUES ('HISTORIC', '2', 'propietat.grup.historic');
INSERT INTO pbl_config_group (code, position, description_key) VALUES ('TASQUES', '3', 'propietat.grup.tasques');

CREATE TABLE pbl_config_type (code VARCHAR2(128 CHAR) NOT NULL, value VARCHAR2(2048 CHAR));
ALTER TABLE pbl_config_type ADD PRIMARY KEY (code);

grant select, update, insert, delete on pbl_config_type to www_pinbal;

INSERT INTO pbl_config_type (code) VALUES ('BOOL');
INSERT INTO pbl_config_type (code) VALUES ('TEXT');
INSERT INTO pbl_config_type (code) VALUES ('INT');
INSERT INTO pbl_config_type (code) VALUES ('FLOAT');
INSERT INTO pbl_config_type (code) VALUES ('PASS');
INSERT INTO pbl_config_type (code) VALUES ('CRON');
INSERT INTO pbl_config_type (code, value) VALUES ('USUARIS_CLASS', 'es.caib.pinbal.plugins.caib.DadesUsuariPluginJdbc');
INSERT INTO pbl_config_type (code, value) VALUES ('SIGNATURA_CLASS', 'es.caib.pinbal.plugins.caib.SignaturaPluginCaib');
INSERT INTO pbl_config_type (code, value) VALUES ('FIRMA_SERVIDOR_CLASS', 'es.caib.pinbal.plugins.caib.FirmaServidorPluginPortafib');
INSERT INTO pbl_config_type (code, value) VALUES ('CUSTODIA_CLASS', 'es.caib.pinbal.plugins.caib.CustodiaPluginCaib');
INSERT INTO pbl_config_type (code, value) VALUES ('ARXIU_CLASS', 'es.caib.plugins.arxiu.caib.ArxiuPluginCaib');
INSERT INTO pbl_config_type (code, value) VALUES ('UNITATS_CLASS', 'es.caib.pinbal.plugin.caib.unitat.UnitatsOrganitzativesPluginDir3');
INSERT INTO pbl_config_type (code, value) VALUES ('CONVERSIO_TIPUS', 'xdocreport,openoffice');

CREATE TABLE pbl_config (key VARCHAR2(256 CHAR) NOT NULL, value VARCHAR2(2048 CHAR), description_key VARCHAR2(2048 CHAR), group_code VARCHAR2(128 CHAR) NOT NULL, position NUMBER(3) NOT NULL, source_property VARCHAR2(16 CHAR) NOT NULL, type_code VARCHAR2(128 CHAR), lastmodifiedby_codi VARCHAR2(64 CHAR), lastmodifieddate TIMESTAMP);
ALTER TABLE pbl_config ADD PRIMARY KEY (key);
ALTER TABLE pbl_config ADD CONSTRAINT pbl_config_group_fk FOREIGN KEY (group_code) REFERENCES pbl_config_group (code);

grant select, update, insert, delete on pbl_config to www_pinbal;

INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.xsd.base.path', NULL, 'propietat.xsd.base.path', 'GENERAL', '0', 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.optimitzar.transaccions.nova.consulta', NULL, 'propietat.optimitzar.transaccions.nova.consulta', 'GENERAL', '1', 'DATABASE', 'BOOL');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.entitat.accio.esborrar.activa', NULL, 'propietat.entitat.accio.esborrar.activa', 'OPCIONS_BORRAR', '0', 'DATABASE', 'BOOL');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.procediment.accio.esborrar.activa', NULL, 'propietat.procediment.accio.esborrar.activa', 'OPCIONS_BORRAR', '1', 'DATABASE', 'BOOL');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.justificant.extensio.sortida', NULL, 'propietat.justificant.extensio.sortida', 'JUSTIFICANT', '0', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.justificant.signar.i.custodiar', NULL, 'propietat.justificant.signar.i.custodiar', 'JUSTIFICANT', '1', 'DATABASE', 'BOOL');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.justificant.convertir.pdfa', NULL, 'propietat.justificant.convertir.pdfa', 'JUSTIFICANT', '2', 'DATABASE', 'BOOL');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.justificant.serie.documental', NULL, 'propietat.justificant.serie.documental', 'JUSTIFICANT', '3', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.conversio.tipus', NULL, 'propietat.conversio.tipus', 'CONVERSIO', '0', 'DATABASE', 'CONVERSIO_TIPUS');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.conversio.open.office.host', NULL, 'propietat.conversio.open.office.host', 'CONVERSIO', '1', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.conversio.open.office.port', NULL, 'propietat.conversio.open.office.port', 'CONVERSIO', '2', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.mapeig.rol.PBL_ADMIN', NULL, 'propietat.mapeig.rol.PBL_ADMIN', 'MAPEIG', '0', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.mapeig.rol.PBL_REPRES', NULL, 'propietat.mapeig.rol.PBL_REPRES', 'MAPEIG', '1', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.mapeig.rol.PBL_DELEG', NULL, 'propietat.mapeig.rol.PBL_DELEG', 'MAPEIG', '2', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.mapeig.rol.PBL_AUDIT', NULL, 'propietat.mapeig.rol.PBL_AUDIT', 'MAPEIG', '3', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.mapeig.rol.PBL_SUPERAUD', NULL, 'propietat.mapeig.rol.PBL_SUPERAUD', 'MAPEIG', '4', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.mapeig.rol.PBL_WS', NULL, 'propietat.mapeig.rol.PBL_WS', 'MAPEIG', '5', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.mapeig.rol.tothom', NULL, 'propietat.mapeig.rol.tothom', 'MAPEIG', '6', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.recobriment.base.url', NULL, 'propietat.recobriment.base.url', 'RECOBRIMENT', '0', 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.recobriment.datos.especificos.processar', NULL, 'propietat.recobriment.datos.especificos.processar', 'RECOBRIMENT', '1', 'DATABASE', 'BOOL');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.recobriment.datos.especificos.incloure.ns', NULL, 'propietat.recobriment.datos.especificos.incloure.ns', 'RECOBRIMENT', '2', 'DATABASE', 'BOOL');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.email.remitent', NULL, 'propietat.email.remitent', 'EMAIL', '0', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.email.footer', NULL, 'propietat.email.footer', 'EMAIL', '1', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.tasca.auto.comprovacio.repeticio', NULL, 'propietat.tasca.auto.comprovacio.repeticio', 'TASQUES', '0', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.tasca.auto.justificant.repeticio', NULL, 'propietat.tasca.auto.justificant.repeticio', 'TASQUES', '1', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.tasca.auto.exp.tancar.cron', NULL, 'propietat.tasca.auto.exp.tancar.cron', 'TASQUES', '2', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.tasca.auto.email.report.estat.cron', NULL, 'propietat.tasca.auto.email.report.estat.cron', 'TASQUES', '3', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.class', NULL, 'propietat.plugin.dades.usuari.class', 'USUARIS', '0', 'DATABASE', 'USUARIS_CLASS');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.jdbc.datasource.jndi.name', NULL, 'propietat.plugin.dades.usuari.jdbc.datasource.jndi.name', 'USUARIS', '1', 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.jdbc.query.codi', NULL, 'propietat.plugin.dades.usuari.jdbc.query.codi', 'USUARIS', '2', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.jdbc.query.nif', NULL, 'propietat.plugin.dades.usuari.jdbc.query.nif', 'USUARIS', '3', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.jdbc.query.rols', NULL, 'propietat.plugin.dades.usuari.jdbc.query.rols', 'USUARIS', '4', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.jdbc.query.grup', NULL, 'propietat.plugin.dades.usuari.jdbc.query.grup', 'USUARIS', '5', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.signatura.class', NULL, 'propietat.plugin.signatura.class', 'SIGNATURA', '0', 'DATABASE', 'SIGNATURA_CLASS');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.signatura.caib.certificat.alias', NULL, 'propietat.plugin.signatura.caib.certificat.alias', 'SIGNATURA', '1', 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.signatura.caib.certificat.password', NULL, 'propietat.plugin.signatura.caib.certificat.password', 'SIGNATURA', '2', 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.signatura.caib.content.type', NULL, 'propietat.plugin.signatura.caib.content.type', 'SIGNATURA', '3', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.firmaservidor.class', NULL, 'propietat.plugin.firmaservidor.class', 'FIRMA_SERVIDOR', '0', 'DATABASE', 'FIRMA_SERVIDOR_CLASS');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_url', NULL, 'propietat.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_url', 'FIRMA_SERVIDOR', '1', 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_username', NULL, 'propietat.tasca.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_username', 'FIRMA_SERVIDOR', '2', 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_password', NULL, 'propietat.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_password', 'FIRMA_SERVIDOR', '3', 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.firmaservidor.portafib.username', NULL, 'propietat.plugin.firmaservidor.portafib.username', 'FIRMA_SERVIDOR', '4', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.firmaservidor.portafib.location', NULL, 'propietat.plugin.firmaservidor.portafib.location', 'FIRMA_SERVIDOR', '5', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.firmaservidor.portafib.signerEmail', NULL, 'propietat.plugin.firmaservidor.portafib.signerEmail', 'FIRMA_SERVIDOR', '6', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.custodia.class', NULL, 'propietat.plugin.custodia.class', 'CUSTODIA', '0', 'DATABASE', 'CUSTODIA_CLASS');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.custodia.caib.url', NULL, 'propietat.plugin.custodia.caib.url', 'CUSTODIA', '1', 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.custodia.caib.username', NULL, 'propietat.plugin.custodia.caib.username', 'CUSTODIA', '2', 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.custodia.caib.password', NULL, 'propietat.plugin.custodia.caib.password', 'CUSTODIA', '3', 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.custodia.caib.tipus.document', NULL, 'propietat.plugin.custodia.caib.tipus.document', 'CUSTODIA', '4', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.arxiu.class', NULL, 'propietat.plugin.arxiu.class', 'ARXIU', '0', 'DATABASE', 'ARXIU_CLASS');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.arxiu.caib.base.url', NULL, 'propietat.plugin.arxiu.caib.base.url', 'ARXIU', '1', 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.arxiu.caib.aplicacio.codi', NULL, 'propietat.plugin.arxiu.caib.aplicacio.codi', 'ARXIU', '2', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.arxiu.caib.usuari', NULL, 'propietat.plugin.arxiu.caib.usuari', 'ARXIU', '3', 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.arxiu.caib.contrasenya', NULL, 'propietat.plugin.arxiu.caib.contrasenya', 'ARXIU', '4', 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.arxiu.caib.csv.definicio', NULL, 'propietat.plugin.arxiu.caib.csv.definicio', 'ARXIU', '5', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.arxiu.caib.conversio.imprimible.url', NULL, 'propietat.plugin.arxiu.caib.conversio.imprimible.url', 'ARXIU', '6', 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.arxiu.caib.conversio.imprimible.usuari', NULL, 'propietat.tasca.plugin.arxiu.caib.conversio.imprimible.usuari', 'ARXIU', '7', 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.arxiu.caib.conversio.imprimible.contrasenya', NULL, 'propietat.plugin.arxiu.caib.conversio.imprimible.contrasenya', 'ARXIU', '8', 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.arxiu.caib.timeout.connect', NULL, 'propietat.plugin.arxiu.caib.timeout.connect', 'ARXIU', '9', 'DATABASE', 'INT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.arxiu.caib.timeout.read', NULL, 'propietat.plugin.arxiu.caib.timeout.read', 'ARXIU', '10', 'DATABASE', 'INT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.unitats.organitzatives.class', NULL, 'propietat.plugin.unitats.organitzatives.class', 'UNITATS', '0', 'DATABASE', 'UNITATS_CLASS');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.unitats.organitzatives.dir3.service.url', NULL, 'propietat.plugin.unitats.organitzatives.dir3.service.url', 'UNITATS', '1', 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.unitats.organitzatives.dir3.service.username', NULL, 'propietat.plugin.unitats.organitzatives.dir3.service.username', 'UNITATS', '2', 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.unitats.organitzatives.dir3.service.password', NULL, 'propietat.plugin.unitats.organitzatives.dir3.service.password', 'UNITATS', '3', 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.unitats.organitzatives.dir3.service.log.actiu', NULL, 'propietat.plugin.unitats.organitzatives.dir3.service.log.actiu', 'UNITATS', '4', 'DATABASE', 'BOOL');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.unitats.cerca.dir3.service.url', NULL, 'propietat.plugin.unitats.cerca.dir3.service.url', 'UNITATS', '5', 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.tasca.auto.arxivar.consultes.cron', NULL, 'propietat.tasca.auto.arxivar.consultes.cron', 'HISTORIC', '0', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.tasca.auto.arxivar.antiguetat.dies', NULL, 'propietat.tasca.auto.arxivar.antiguetat.dies', 'HISTORIC', '1', 'DATABASE', 'INT');

-- Changeset db/changelog/changes/1.4.31/205.yaml::1650451898721-1::limit
ALTER TABLE pbl_entitat_usuari ADD actiu NUMBER(1);

-- Changeset db/changelog/changes/1.4.31/205.yaml::1650451898721-2::limit
UPDATE pbl_entitat_usuari SET actiu = 1;

ALTER TABLE pbl_entitat_usuari MODIFY actiu NOT NULL;

