-- Inicialització de class_id_type a java.lang.Long per a tots els registres existents de pbl_acl_class, ja que tots els identificadors d'objecte del sistema són de tipus Long
UPDATE pbl_acl_class SET class_id_type = 'java.lang.Long';

-- Modificació de plugins
-- Afegir plugin usuaris LDAP
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.pluginsib.userinformation.ldap.host_url', '', 'propietat.dades.usuari.pluginsib.userinformation.ldap.host_url', 'USUARIS', '0', 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.pluginsib.userinformation.ldap.security_principal', '', 'propietat.dades.usuari.pluginsib.userinformation.ldap.security_principal', 'USUARIS', '1', 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.pluginsib.userinformation.ldap.security_authentication', '', 'propietat.dades.usuari.pluginsib.userinformation.ldap.security_authentication', 'USUARIS', '2', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.pluginsib.userinformation.ldap.security_credentials', '', 'propietat.dades.usuari.pluginsib.userinformation.ldap.security_credentials', 'USUARIS', '3', 'FILE', 'PASS');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.pluginsib.userinformation.ldap.users_context_dn', '', 'propietat.dades.usuari.pluginsib.userinformation.ldap.users_context_dn', 'USUARIS', '4', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.pluginsib.userinformation.ldap.search_scope', '', 'propietat.dades.usuari.pluginsib.userinformation.ldap.search_scope', 'USUARIS', '5', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.pluginsib.userinformation.ldap.attribute.username', '', 'propietat.dades.usuari.pluginsib.userinformation.ldap.attribute.username', 'USUARIS', '6', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.pluginsib.userinformation.ldap.attribute.mail', '', 'propietat.dades.usuari.pluginsib.userinformation.ldap.attribute.mail', 'USUARIS', '7', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.pluginsib.userinformation.ldap.attribute.administration_id', '', 'propietat.dades.usuari.pluginsib.userinformation.ldap.attribute.administration_id', 'USUARIS', '8', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.pluginsib.userinformation.ldap.attribute.name', '', 'propietat.dades.usuari.pluginsib.userinformation.ldap.attribute.name', 'USUARIS', '9', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.pluginsib.userinformation.ldap.attribute.surname', '', 'propietat.dades.usuari.pluginsib.userinformation.ldap.attribute.surname', 'USUARIS', '10', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.pluginsib.userinformation.ldap.attribute.surname1', '', 'propietat.dades.usuari.pluginsib.userinformation.ldap.attribute.surname1', 'USUARIS', '11', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.pluginsib.userinformation.ldap.attribute.surname2', '', 'propietat.dades.usuari.pluginsib.userinformation.ldap.attribute.surname2', 'USUARIS', '12', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.pluginsib.userinformation.ldap.attribute.telephone', '', 'propietat.dades.usuari.pluginsib.userinformation.ldap.attribute.telephone', 'USUARIS', '13', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.pluginsib.userinformation.ldap.attribute.department', '', 'propietat.dades.usuari.pluginsib.userinformation.ldap.attribute.department', 'USUARIS', '14', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.pluginsib.userinformation.ldap.attribute.memberof', '', 'propietat.dades.usuari.pluginsib.userinformation.ldap.attribute.memberof', 'USUARIS', '15', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.pluginsib.userinformation.ldap.attribute.prefix_role_match_memberof', '', 'propietat.dades.usuari.pluginsib.userinformation.ldap.attribute.prefix_role_match_memberof', 'USUARIS', '16', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.pluginsib.userinformation.ldap.attribute.suffix_role_match_memberof', '', 'propietat.dades.usuari.pluginsib.userinformation.ldap.attribute.suffix_role_match_memberof', 'USUARIS', '17', 'DATABASE', 'TEXT');

-- Afegir plugin usuaris Keycloak
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.pluginsib.userinformation.keycloak.serverurl', '', 'propietat.dades.usuari.pluginsib.userinformation.keycloak.serverurl', 'USUARIS', '18', 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.pluginsib.userinformation.keycloak.realm', '', 'propietat.dades.usuari.pluginsib.userinformation.keycloak.realm', 'USUARIS', '19', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.pluginsib.userinformation.keycloak.client_id', '', 'propietat.dades.usuari.pluginsib.userinformation.keycloak.client_id', 'USUARIS', '20', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.pluginsib.userinformation.keycloak.password_secret', '', 'propietat.dades.usuari.pluginsib.userinformation.keycloak.password_secret', 'USUARIS', '21', 'FILE', 'PASS');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.pluginsib.userinformation.keycloak.client_id_for_user_autentication', '', 'propietat.dades.usuari.pluginsib.userinformation.keycloak.client_id_for_user_autentication', 'USUARIS', '22', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.pluginsib.userinformation.keycloak.debug', '', 'propietat.dades.usuari.pluginsib.userinformation.keycloak.debug', 'USUARIS', '23', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.pluginsib.userinformation.keycloak.mapping.administrationID', '', 'propietat.dades.usuari.pluginsib.userinformation.keycloak.mapping.administrationID', 'USUARIS', '24', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.pluginsib.userinformation.keycloak.minimumcharacterstosearch', '', 'propietat.dades.usuari.pluginsib.userinformation.keycloak.minimumcharacterstosearch', 'USUARIS', '25', 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.dades.usuari.pluginsib.userinformation.keycloak.maxallowednumberofresultsinpartialsearches', '', 'propietat.dades.usuari.pluginsib.userinformation.keycloak.maxallowednumberofresultsinpartialsearches', 'USUARIS', '26', 'DATABASE', 'TEXT');

-- Actualitza les classes dels plugins
UPDATE pbl_config_type SET value = 'es.caib.pinbal.plugin.usuari.DadesUsuariPluginJdbc,es.caib.pinbal.plugin.usuari.DadesUsuariPluginLdapCaib,es.caib.pinbal.plugin.usuari.DadesUsuariPluginKeycloak' WHERE code = 'USUARIS_CLASS';
UPDATE pbl_config_type SET value = 'es.caib.pinbal.plugin.firmaservidor.FirmaSimpleServidorPluginPortafib' WHERE code = 'FIRMA_SERVIDOR_CLASS';
UPDATE pbl_config_type SET value = 'es.caib.pinbal.plugin.arxiu.ArxiuPluginCaib' WHERE code = 'ARXIU_CLASS';
UPDATE pbl_config_type SET value = 'es.caib.pinbal.plugin.unitat.UnitatsOrganitzativesPluginDir3' WHERE code = 'UNITATS_CLASS';

-- Actualitza les classes dels plugins seleccionats
UPDATE pbl_config SET value = 'es.caib.pinbal.plugin.usuari.DadesUsuariPluginJdbc' WHERE key = 'es.caib.pinbal.plugin.dades.usuari.class' AND value = 'es.caib.pinbal.plugins.caib.DadesUsuariPluginJdbc';
UPDATE pbl_config SET value = 'es.caib.pinbal.plugin.usuari.DadesUsuariPluginLdapCaib' WHERE key = 'es.caib.pinbal.plugin.dades.usuari.class' AND value = 'es.caib.pinbal.plugins.caib.DadesUsuariPluginLdapCaib';
UPDATE pbl_config SET value = 'es.caib.pinbal.plugin.unitat.UnitatsOrganitzativesPluginDir3' WHERE type_code = 'UNITATS_CLASS';
UPDATE pbl_config SET value = 'es.caib.pinbal.plugin.firmaservidor.FirmaSimpleServidorPluginPortafib' WHERE type_code = 'FIRMA_SERVIDOR_CLASS';
UPDATE pbl_config SET value = 'es.caib.pinbal.plugin.arxiu.ArxiuPluginCaib' WHERE type_code = 'ARXIU_CLASS';

-- Elimina les propietats obsoletes de plugins antics eliminats
DELETE FROM pbl_config WHERE key like 'es.caib.pinbal.plugin.custodia.%';
DELETE FROM pbl_config WHERE key like 'es.caib.pinbal.plugin.signatura.%';
DELETE FROM pbl_config WHERE key = 'es.caib.pinbal.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_url';
DELETE FROM pbl_config WHERE key = 'es.caib.pinbal.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_username';
DELETE FROM pbl_config WHERE key = 'es.caib.pinbal.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_password';

-- Elimina tipus de plugins antics eliminats
DELETE FROM pbl_config_type WHERE code IN ('SIGNATURA_CLASS', 'CUSTODIA_CLASS');
