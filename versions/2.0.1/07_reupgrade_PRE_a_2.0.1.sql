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
-- 1. ACL -- tornar a posar el prefix PBL_ a taules i sequencies i refer els
--    triggers d'ID (imprescindible: el JdbcAclService/JdbcMutableAclService de
--    2.0.1 consulta les taules PBL_ACL_* i fa PBL_ACL_*_SEQ.CURRVAL).
--    Invers exacte de l'apartat 1 de 06. Bloc idempotent.
-- ----------------------------------------------------------------------------
SET SERVEROUTPUT ON SIZE UNLIMITED
DECLARE
  PROCEDURE exec_ddl(p_sql VARCHAR2) IS
  BEGIN
    DBMS_OUTPUT.PUT_LINE('  -> ' || p_sql);
    EXECUTE IMMEDIATE p_sql;
  END;

  PROCEDURE rename_table(p_old VARCHAR2, p_new VARCHAR2) IS
    v_old NUMBER;
    v_new NUMBER;
  BEGIN
    SELECT COUNT(*) INTO v_old FROM user_tables WHERE table_name = p_old;
    SELECT COUNT(*) INTO v_new FROM user_tables WHERE table_name = p_new;
    IF v_old > 0 AND v_new = 0 THEN
      exec_ddl('ALTER TABLE ' || p_old || ' RENAME TO ' || p_new);
    END IF;
  END;

  PROCEDURE rename_sequence(p_old VARCHAR2, p_new VARCHAR2) IS
    v_old NUMBER;
    v_new NUMBER;
  BEGIN
    SELECT COUNT(*) INTO v_old FROM user_sequences WHERE sequence_name = p_old;
    SELECT COUNT(*) INTO v_new FROM user_sequences WHERE sequence_name = p_new;
    IF v_old > 0 AND v_new = 0 THEN
      exec_ddl('RENAME ' || p_old || ' TO ' || p_new);
    END IF;
  END;

  PROCEDURE drop_trigger(p_trigger VARCHAR2) IS
    v_n NUMBER;
  BEGIN
    SELECT COUNT(*) INTO v_n FROM user_triggers WHERE trigger_name = p_trigger;
    IF v_n > 0 THEN
      exec_ddl('DROP TRIGGER ' || p_trigger);
    END IF;
  END;

  PROCEDURE create_idgen_trigger(p_trigger VARCHAR2, p_table VARCHAR2, p_sequence VARCHAR2) IS
  BEGIN
    exec_ddl('CREATE OR REPLACE TRIGGER ' || p_trigger || ' BEFORE INSERT ON ' || p_table
             || ' FOR EACH ROW BEGIN SELECT ' || p_sequence || '.NEXTVAL INTO :NEW.ID FROM DUAL; END;');
  END;
BEGIN
  rename_table('ACL_SID',             'PBL_ACL_SID');
  rename_table('ACL_CLASS',           'PBL_ACL_CLASS');
  rename_table('ACL_OBJECT_IDENTITY', 'PBL_ACL_OBJECT_IDENTITY');
  rename_table('ACL_ENTRY',           'PBL_ACL_ENTRY');

  rename_sequence('ACL_SID_SEQ',   'PBL_ACL_SID_SEQ');
  rename_sequence('ACL_CLASS_SEQ', 'PBL_ACL_CLASS_SEQ');
  rename_sequence('ACL_OID_SEQ',   'PBL_ACL_OID_SEQ');
  rename_sequence('ACL_ENTRY_SEQ', 'PBL_ACL_ENTRY_SEQ');

  drop_trigger('ACL_SID_IDGEN');
  drop_trigger('ACL_CLASS_IDGEN');
  drop_trigger('ACL_OID_IDGEN');
  drop_trigger('ACL_ENTRY_IDGEN');
  drop_trigger('ACL_SID_TRG');
  drop_trigger('ACL_CLASS_TRG');
  drop_trigger('ACL_OID_TRG');
  drop_trigger('ACL_ENTRY_TRG');

  create_idgen_trigger('PBL_ACL_SID_IDGEN',   'PBL_ACL_SID',             'PBL_ACL_SID_SEQ');
  create_idgen_trigger('PBL_ACL_CLASS_IDGEN', 'PBL_ACL_CLASS',           'PBL_ACL_CLASS_SEQ');
  create_idgen_trigger('PBL_ACL_OID_IDGEN',   'PBL_ACL_OBJECT_IDENTITY', 'PBL_ACL_OID_SEQ');
  create_idgen_trigger('PBL_ACL_ENTRY_IDGEN', 'PBL_ACL_ENTRY',           'PBL_ACL_ENTRY_SEQ');
END;
/

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
