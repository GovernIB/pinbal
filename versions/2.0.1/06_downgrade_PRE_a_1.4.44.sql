-- ============================================================================
-- PINBAL -- Downgrade de PRE: 2.0.1 -> 1.4.44 (Oracle)
-- ============================================================================
--
-- Filosofia d'aquest script: desfer NOMES el que es IMPRESCINDIBLE perque el
-- codi 1.4.44 (desplegat de nou sobre JBoss EAP 5) torni a funcionar
-- correctament. NO es restaura cap copia de seguretat en cap cas: tot el que
-- calgui desfer es desfa amb SQL explicit, sobre l'esquema tal com ha quedat
-- despres d'executar 01_update_scsp_PRE.sql, 02_update_ddl_2.0.1.sql,
-- 03_update_dml_2.0.1.sql i 04_update_ddl_2.0.1.sql.
--
-- Elements de la pujada que es DEIXEN TAL QUAL (no es reverteixen) perque
-- son additius i el codi 1.4.44 no els consulta mai, per tant no li afecten:
--   - Columna PBL_ACL_CLASS.CLASS_ID_TYPE (02)
--   - Columnes JUSTIFICANTERROR a PBL_CONSULTA_LIST / PBL_CONSULTA_HIST_LIST (02)
--   - Noms nous de les restriccions PK/UNIQUE/FK de les taules ACL (02) --
--     Oracle les mante lligades a la taula per referencia interna, no pel
--     nom, i 1.4.44 no les referencia mai pel nom.
--     (Les SEQUENCIES i els TRIGGERS ACL, en canvi, SI que es reverteixen a
--     l'apartat 1: 1.4.44 fa ACL_SID_SEQ.CURRVAL / ACL_CLASS_SEQ.CURRVAL.)
--   - FK fk_clave_priv_reqcesionaris (04) -- es ON DELETE SET NULL, no bloqueja
--     cap operacio que 1.4.44 faci normalment.
--   - Files noves de configuracio dels plugins LDAP/Keycloak de dades
--     d'usuari (03) -- 1.4.44 no les llegeix.
--   - Taula CORE_REQ_SSL_CESIONARIOS, taula CORE_CLAVE_PRIVADA_EXTERNA,
--     columna CORE_CLAVE_PRIVADA.BLOQUEADO, columna CORE_SERVICIO.SIGNER,
--     files noves de CORE_PARAMETRO_CONFIGURACION (external.signer.*,
--     afirma.protocolo) (01) -- pertanyen a funcionalitat nova (firma
--     externa) que la versio de la llibreria SCSP de 1.4.44 no invoca mai.
--
-- Elements que aquest script NO reverteix i que cal validar/gestionar
-- manualment (vegeu doc/despliegament-pre-eap72.md, apartat de rollback):
--   - Els valors concrets (URL, usuari, contrasenya) dels plugins de
--     signatura/custodia son "FILE" (es llegeixen del fitxer de propietats,
--     no de BBDD): es recuperen automaticament en tornar a desplegar el
--     jboss_system.properties / pinbal.properties ORIGINALS de 1.4.44 (que
--     s'han d'haver conservat abans de la pujada, vegeu la checklist).
--   - Les correccions de dades del cataleg SCSP fetes per
--     01_update_scsp_PRE.sql (descripcions, URL, tiposeguridad,
--     plantillaxslt de CORE_SERVICIO) NO es desfan: son, en la seva majoria,
--     correccions cosmetiques o d'infraestructura externa de Red.es que no
--     impedeixen que 1.4.44 arrenqui ni funcioni, i revertir-les podria
--     apuntar a URL ja no vigents. Excepcio a vigilar manualment: aquest
--     script SI que canvia CODCERTIFICADO en 2 files (identificador de
--     servei), verificar que no calgui res mes a la banda PINBAL:
--       SVDPVIAEWS01        -> SVDPVASCOIAEEXTWS01
--       SVDCSDBECASDANWS01  -> SVDCSDCONDICIONALTONIVELWS01
--   - La renumeracio de CORE_TRANSMISION.CLASETRAMITE no es desfa: nomes
--     afecta la classificacio mostrada de transmissions HISTORIQUES ja
--     existents, no impedeix que l'aplicacio funcioni.
--
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. ACL -- tornar a la nomenclatura de 1.4.44 les taules i les sequencies,
--    i refer els triggers d'ID (imprescindible):
--    - el JdbcAclService/JdbcMutableAclService de 1.4.44 consulta els noms de
--      taula ACL_* directament al codi;
--    - 1.4.44 obte l'ID inserit amb "SELECT ACL_SID_SEQ.CURRVAL" i
--      "SELECT ACL_CLASS_SEQ.CURRVAL" (application-context-model.xml);
--    - els INSERT de 1.4.44 no informen l'ID: l'assigna un trigger, que ha
--      d'apuntar a les sequencies ACL_*_SEQ. Els triggers PBL_ACL_*_IDGEN
--      apunten a PBL_ACL_*_SEQ i quedarien INVALID en renomenar-les.
--    Bloc idempotent: comprova l'estat de cada objecte abans d'actuar.
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
  rename_table('PBL_ACL_SID',             'ACL_SID');
  rename_table('PBL_ACL_CLASS',           'ACL_CLASS');
  rename_table('PBL_ACL_OBJECT_IDENTITY', 'ACL_OBJECT_IDENTITY');
  rename_table('PBL_ACL_ENTRY',           'ACL_ENTRY');

  rename_sequence('PBL_ACL_SID_SEQ',   'ACL_SID_SEQ');
  rename_sequence('PBL_ACL_CLASS_SEQ', 'ACL_CLASS_SEQ');
  rename_sequence('PBL_ACL_OID_SEQ',   'ACL_OID_SEQ');
  rename_sequence('PBL_ACL_ENTRY_SEQ', 'ACL_ENTRY_SEQ');

  -- Triggers de 2.0.1 i, si encara hi fossin (02c no executat), els antics ACL_*_TRG
  drop_trigger('PBL_ACL_SID_IDGEN');
  drop_trigger('PBL_ACL_CLASS_IDGEN');
  drop_trigger('PBL_ACL_OID_IDGEN');
  drop_trigger('PBL_ACL_ENTRY_IDGEN');
  drop_trigger('ACL_SID_TRG');
  drop_trigger('ACL_CLASS_TRG');
  drop_trigger('ACL_OID_TRG');
  drop_trigger('ACL_ENTRY_TRG');

  create_idgen_trigger('ACL_SID_IDGEN',   'ACL_SID',             'ACL_SID_SEQ');
  create_idgen_trigger('ACL_CLASS_IDGEN', 'ACL_CLASS',           'ACL_CLASS_SEQ');
  create_idgen_trigger('ACL_OID_IDGEN',   'ACL_OBJECT_IDENTITY', 'ACL_OID_SEQ');
  create_idgen_trigger('ACL_ENTRY_IDGEN', 'ACL_ENTRY',           'ACL_ENTRY_SEQ');
END;
/

-- Desfer el canvi de nom de classe fet a 03 (imprescindible: 1.4.44 cerca les
-- ACL de ProcedimentServei pel nom de classe antic)
UPDATE acl_class SET class = 'es.caib.pinbal.core.model.ProcedimentServei' WHERE class = 'es.caib.pinbal.persist.entity.ProcedimentServei';

COMMIT;

-- ----------------------------------------------------------------------------
-- 2. Plugins -- restaurar els noms de classe seleccionats (imprescindible: si
--    es deixen els noms de paquet nous, 1.4.44 no pot instanciar els plugins,
--    ja que aquestes classes no existeixen al seu codi)
-- ----------------------------------------------------------------------------
UPDATE pbl_config SET value = 'es.caib.pinbal.plugins.caib.DadesUsuariPluginJdbc' WHERE key = 'es.caib.pinbal.plugin.dades.usuari.class' AND value = 'es.caib.pinbal.plugin.usuari.DadesUsuariPluginJdbc';
UPDATE pbl_config SET value = 'es.caib.pinbal.plugins.caib.DadesUsuariPluginLdapCaib' WHERE key = 'es.caib.pinbal.plugin.dades.usuari.class' AND value = 'es.caib.pinbal.plugin.usuari.DadesUsuariPluginLdapCaib';
-- NOTA: si a PRE, ja sota 2.0.1, s'ha arribat a seleccionar el plugin nou
-- "DadesUsuariPluginKeycloak" (sense equivalent a 1.4.44), aquest UPDATE no
-- el troba i el deixa tal qual: cal decidir manualment quin plugin de dades
-- d'usuari de 1.4.44 activar (Jdbc o LdapCaib) abans d'arrencar.
UPDATE pbl_config SET value = 'es.caib.pinbal.plugin.caib.unitat.UnitatsOrganitzativesPluginDir3' WHERE type_code = 'UNITATS_CLASS';
UPDATE pbl_config SET value = 'es.caib.pinbal.plugins.caib.FirmaSimpleServidorPluginPortafib' WHERE type_code = 'FIRMA_SERVIDOR_CLASS';
UPDATE pbl_config SET value = 'es.caib.plugins.arxiu.caib.ArxiuPluginCaib' WHERE type_code = 'ARXIU_CLASS';

-- Restaurar tambe la llista de classes disponibles (PBL_CONFIG_TYPE): la
-- pantalla d'administracio de 1.4.44 valida el valor seleccionat contra
-- aquesta llista.
UPDATE pbl_config_type SET value = 'es.caib.pinbal.plugins.caib.DadesUsuariPluginJdbc,es.caib.pinbal.plugins.caib.DadesUsuariPluginLdapCaib' WHERE code = 'USUARIS_CLASS';
UPDATE pbl_config_type SET value = 'es.caib.pinbal.plugins.caib.FirmaServidorPluginPortafib,es.caib.pinbal.plugins.caib.FirmaSimpleServidorPluginPortafib' WHERE code = 'FIRMA_SERVIDOR_CLASS';
UPDATE pbl_config_type SET value = 'es.caib.plugins.arxiu.caib.ArxiuPluginCaib' WHERE code = 'ARXIU_CLASS';
UPDATE pbl_config_type SET value = 'es.caib.pinbal.plugin.caib.unitat.UnitatsOrganitzativesPluginDir3' WHERE code = 'UNITATS_CLASS';

COMMIT;

-- ----------------------------------------------------------------------------
-- 3. Signatura/Custodia -- restaurar l'estructura de configuracio esborrada
--    (imprescindible: a diferencia de 2.0.1, el codi 1.4.44 encara depen dels
--    plugins SignaturaPluginCaib i CustodiaPluginCaib -- no es van eliminar
--    del codi fins a la mateixa migracio cap a 2.0.1). Els valors "FILE"
--    es deixen buits perque es llegeixen del fitxer de propietats (recuperat
--    en desplegar de nou 1.4.44); els valors "DATABASE" es restauren amb el
--    seu valor real (no son secrets ni depenen de l'entorn).
-- ----------------------------------------------------------------------------
INSERT INTO pbl_config_group (code, description_key, position, parent_code) VALUES ('SIGNATURA', 'propietat.grup.plugins.signatura', 1, 'PLUGINS');
INSERT INTO pbl_config_group (code, description_key, position, parent_code) VALUES ('CUSTODIA', 'propietat.grup.plugins.custodia', 3, 'PLUGINS');

INSERT INTO pbl_config_type (code, value) VALUES ('SIGNATURA_CLASS', 'es.caib.pinbal.plugins.caib.SignaturaPluginCaib');
INSERT INTO pbl_config_type (code, value) VALUES ('CUSTODIA_CLASS', 'es.caib.pinbal.plugins.caib.CustodiaPluginCaib');

INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.signatura.class', 'es.caib.pinbal.plugins.caib.SignaturaPluginCaib', 'propietat.plugin.signatura.class', 'SIGNATURA', 0, 'DATABASE', 'SIGNATURA_CLASS');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.signatura.caib.certificat.alias', NULL, 'propietat.plugin.signatura.caib.certificat.alias', 'SIGNATURA', 1, 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.signatura.caib.certificat.password', NULL, 'propietat.plugin.signatura.caib.certificat.password', 'SIGNATURA', 2, 'FILE', 'PASS');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.signatura.caib.content.type', NULL, 'propietat.plugin.signatura.caib.content.type', 'SIGNATURA', 3, 'DATABASE', 'TEXT');

INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.custodia.class', 'es.caib.pinbal.plugins.caib.CustodiaPluginCaib', 'propietat.plugin.custodia.class', 'CUSTODIA', 0, 'DATABASE', 'CUSTODIA_CLASS');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.custodia.caib.url', NULL, 'propietat.plugin.custodia.caib.url', 'CUSTODIA', 1, 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.custodia.caib.username', NULL, 'propietat.plugin.custodia.caib.username', 'CUSTODIA', 2, 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.custodia.caib.password', NULL, 'propietat.plugin.custodia.caib.password', 'CUSTODIA', 3, 'FILE', 'PASS');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.custodia.caib.tipus.document', 'PBL_JUSTIFICANT_VERIFICACIO_DADES', 'propietat.plugin.custodia.caib.tipus.document', 'CUSTODIA', 4, 'DATABASE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.custodia.caib.verificacio.url.base', NULL, 'propietat.plugin.custodia.caib.verificacio.url.bas', 'CUSTODIA', 5, 'FILE', 'TEXT');

-- Aquestes 3 propietats pertanyen al grup FIRMA_SERVIDOR (que 03 no esborra,
-- nomes esborrava aquestes 3 files concretes): son FILE, valors recuperats
-- en desplegar de nou el fitxer de propietats de 1.4.44.
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_url', NULL, 'propietat.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_url', 'FIRMA_SERVIDOR', 1, 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_username', NULL, 'propietat.tasca.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_username', 'FIRMA_SERVIDOR', 2, 'FILE', 'TEXT');
INSERT INTO pbl_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.pinbal.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_password', NULL, 'propietat.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_password', 'FIRMA_SERVIDOR', 3, 'PASS', 'TEXT');

COMMIT;

-- ----------------------------------------------------------------------------
-- 4. SCSP -- restaurar la columna SSLFLAG (imprescindible: l'ORM de la
--    llibreria scsp-core de 1.4.44 mapeja aquesta columna a
--    CORE_REQ_CESIONARIOS_SERVICIOS; sense ella, qualsevol operacio sobre
--    aquesta entitat falla). Es recupera el valor des de
--    CORE_REQ_SSL_CESIONARIOS (creada per 01_update_scsp_PRE.sql), fent
--    coincidir per (servicio, claveprivada, organismo) ja que aquella taula
--    no conserva l'id original de CORE_REQ_CESIONARIOS_SERVICIOS.
-- ----------------------------------------------------------------------------
ALTER TABLE CORE_REQ_CESIONARIOS_SERVICIOS ADD SSLFLAG NUMBER(1,0) DEFAULT 0 NOT NULL;

UPDATE CORE_REQ_CESIONARIOS_SERVICIOS crs
SET SSLFLAG = 1
WHERE EXISTS (
    SELECT 1
    FROM CORE_REQ_SSL_CESIONARIOS s
    WHERE s.SERVICIO = crs.SERVICIO
      AND NVL(s.CLAVEPRIVADA, -1) = NVL(crs.CLAVEPRIVADA, -1)
      AND NVL(s.ORGANISMO, -1) = NVL(crs.ORGANISMO, -1)
);

-- Restaurar la versio de datamodel que 1.4.44 espera (era la vigent
-- immediatament abans d'aquesta migracio, primer pas de 01_update_scsp_PRE.sql)
UPDATE CORE_PARAMETRO_CONFIGURACION SET VALOR = '4.25.0' WHERE NOMBRE = 'version.datamodel.scsp';

COMMIT;

-- ============================================================================
-- Fi del downgrade. Antes d'arrencar 1.4.44:
--   - Desplegar de nou pinbal.ear 1.4.44 amb el jboss_system.properties /
--     pinbal.properties ORIGINALS (conservats abans de la pujada): d'aqui
--     surten els valors reals de signatura/custodia/firmaservidor.
--   - Revisar manualment els punts llistats a la capçalera d'aquest fitxer
--     (codcertificado renombrats, plugin de dades d'usuari si s'havia
--     canviat a Keycloak).
--   - Executar el pla de proves de regressio abans de donar el rollback per bo.
-- ============================================================================
