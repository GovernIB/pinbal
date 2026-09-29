-- ============================================================================
-- PINBAL -- Reupgrade de PRE: 1.4.44 -> 2.0.1 (Oracle), despres d'un downgrade
-- ============================================================================
--
-- Aquest script desfa NOMES els canvis que va fer
-- 06_downgrade_PRE_a_1.4.44.sql, per tornar a deixar l'esquema exactament
-- com el va deixar la sequencia 01_update_scsp_PRE.sql + 02_update_ddl_2.0.1.sql
-- + 03_update_dml_2.0.1.sql + 04_update_ddl_2.0.1.sql.
--
-- NO reexecuteu els fitxers 01/02/03/04 originals: fallarien per objectes
-- duplicats (p. ex. "ALTER TABLE pbl_acl_class ADD class_id_type" o la FK de
-- 04), ja que el downgrade no els havia tocat (nomes va desfer el
-- imprescindible). Aquest fitxer es exactament el complement invers de
-- 06_downgrade_PRE_a_1.4.44.sql.
--
-- Precondicio: que l'unica intervencio feta sobre l'esquema des de la pujada
-- original sigui 06_downgrade_PRE_a_1.4.44.sql (i, opcionalment, l'us normal
-- de l'aplicacio 1.4.44 mentrestant). Si s'ha fet alguna altra intervencio
-- manual addicional, revisar-la abans d'executar aquest script.
--
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. ACL -- tornar a renomenar les taules amb el prefix PBL_ (imprescindible:
--    el JdbcAclService/JdbcMutableAclService de 2.0.1 consulta aquests noms)
-- ----------------------------------------------------------------------------
ALTER TABLE acl_sid RENAME TO pbl_acl_sid;
ALTER TABLE acl_class RENAME TO pbl_acl_class;
ALTER TABLE acl_object_identity RENAME TO pbl_acl_object_identity;
ALTER TABLE acl_entry RENAME TO pbl_acl_entry;

UPDATE pbl_acl_class SET class = 'es.caib.pinbal.persist.entity.ProcedimentServei' WHERE class = 'es.caib.pinbal.core.model.ProcedimentServei';

COMMIT;

-- ----------------------------------------------------------------------------
-- 2. Plugins -- tornar a apuntar als noms de classe de 2.0.1
-- ----------------------------------------------------------------------------
UPDATE pbl_config SET value = 'es.caib.pinbal.plugin.usuari.DadesUsuariPluginJdbc' WHERE key = 'es.caib.pinbal.plugin.dades.usuari.class' AND value = 'es.caib.pinbal.plugins.caib.DadesUsuariPluginJdbc';
UPDATE pbl_config SET value = 'es.caib.pinbal.plugin.usuari.DadesUsuariPluginLdapCaib' WHERE key = 'es.caib.pinbal.plugin.dades.usuari.class' AND value = 'es.caib.pinbal.plugins.caib.DadesUsuariPluginLdapCaib';
-- NOTA: si durant el periode amb 1.4.44 actiu es va deixar el plugin sense
-- seleccionar explicitament perque abans hi havia "DadesUsuariPluginKeycloak"
-- (vegeu nota equivalent a 06_downgrade_PRE_a_1.4.44.sql), restaurar aqui
-- manualment el valor que calgui.
UPDATE pbl_config SET value = 'es.caib.pinbal.plugin.unitat.UnitatsOrganitzativesPluginDir3' WHERE type_code = 'UNITATS_CLASS';
UPDATE pbl_config SET value = 'es.caib.pinbal.plugin.firmaservidor.FirmaSimpleServidorPluginPortafib' WHERE type_code = 'FIRMA_SERVIDOR_CLASS';
UPDATE pbl_config SET value = 'es.caib.pinbal.plugin.arxiu.ArxiuPluginCaib' WHERE type_code = 'ARXIU_CLASS';

UPDATE pbl_config_type SET value = 'es.caib.pinbal.plugin.usuari.DadesUsuariPluginJdbc,es.caib.pinbal.plugin.usuari.DadesUsuariPluginLdapCaib,es.caib.pinbal.plugin.usuari.DadesUsuariPluginKeycloak' WHERE code = 'USUARIS_CLASS';
UPDATE pbl_config_type SET value = 'es.caib.pinbal.plugin.firmaservidor.FirmaSimpleServidorPluginPortafib,es.caib.pinbal.plugin.firmaservidor.FirmaServidorPluginMock' WHERE code = 'FIRMA_SERVIDOR_CLASS';
UPDATE pbl_config_type SET value = 'es.caib.pinbal.plugin.arxiu.ArxiuPluginCaib,es.caib.pinbal.plugin.arxiu.ArxiuPluginMock' WHERE code = 'ARXIU_CLASS';
UPDATE pbl_config_type SET value = 'es.caib.pinbal.plugin.unitat.UnitatsOrganitzativesPluginDir3' WHERE code = 'UNITATS_CLASS';

COMMIT;

-- ----------------------------------------------------------------------------
-- 3. Signatura/Custodia -- desfer la restauracio estructural (2.0.1 no fa
--    servir aquests plugins)
-- ----------------------------------------------------------------------------
DELETE FROM pbl_config WHERE key like 'es.caib.pinbal.plugin.custodia.%';
DELETE FROM pbl_config WHERE key like 'es.caib.pinbal.plugin.signatura.%';
DELETE FROM pbl_config WHERE key = 'es.caib.pinbal.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_url';
DELETE FROM pbl_config WHERE key = 'es.caib.pinbal.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_username';
DELETE FROM pbl_config WHERE key = 'es.caib.pinbal.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_password';

DELETE FROM pbl_config_type WHERE code IN ('SIGNATURA_CLASS', 'CUSTODIA_CLASS');
DELETE FROM pbl_config_group WHERE code IN ('SIGNATURA', 'CUSTODIA');

COMMIT;

-- ----------------------------------------------------------------------------
-- 4. SCSP -- treure la columna SSLFLAG restaurada i tornar la versio de
--    datamodel a 5.11.0
-- ----------------------------------------------------------------------------
ALTER TABLE CORE_REQ_CESIONARIOS_SERVICIOS DROP COLUMN SSLFLAG;

UPDATE CORE_PARAMETRO_CONFIGURACION SET VALOR = '5.11.0' WHERE NOMBRE = 'version.datamodel.scsp';

COMMIT;

-- ============================================================================
-- Fi del reupgrade. Seguent pas: desplegar pinbal.ear 2.0.1 amb el fitxer de
-- propietats de 2.0.1 (secció 3 de doc/despliegament-pre-eap72.md) i validar
-- amb el pla de proves de regressio.
-- ============================================================================
