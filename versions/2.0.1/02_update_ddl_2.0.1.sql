-- ============================================================================
-- PINBAL 2.0.1 -- Canvis DDL (Oracle)
--
-- Les taules ACL dels entorns existents es varen crear amb dos jocs de noms
-- diferents segons la versio de l'script d'instal·lacio:
--   - scripts/bbdd/1.x/spring_security_acl_creacio.sql (PRE, i probablement PRO):
--       FK ACL_OID_OID_FK, ACL_OID_SID_FK, ACL_ENTRY_OID_FK, ACL_ENTRY_SID_FK,
--       PK ACL_OID_PK, triggers ACL_*_TRG
--   - scripts/bbdd/1.4.41/01_pinbal_creacio_taules.sql:
--       FK ACL_OID_PARENT_FK, ACL_OID_OWNER_FK, ACL_ENTRY_OBJECT_FK, ACL_ENTRY_ACL_FK,
--       PK ACL_OBJECT_IDENTITY_PK, triggers ACL_*_IDGEN
-- Per aixo aquest script no depen dels noms antics: localitza cada restriccio
-- per taula + tipus + columnes i la renomena al nom PBL_ final (o la crea si
-- no existeix). Cada pas comprova l'estat abans d'actuar, per tant l'script es
-- pot tornar a executar sense errors (tambe sobre un esquema ja migrat).
-- ============================================================================
SET SERVEROUTPUT ON SIZE UNLIMITED
WHENEVER SQLERROR EXIT SQL.SQLCODE

DECLARE
  PROCEDURE exec_ddl(p_sql VARCHAR2) IS
  BEGIN
    DBMS_OUTPUT.PUT_LINE('  -> ' || p_sql);
    EXECUTE IMMEDIATE p_sql;
  END;

  FUNCTION table_exists(p_table VARCHAR2) RETURN BOOLEAN IS
    v_n NUMBER;
  BEGIN
    SELECT COUNT(*) INTO v_n FROM user_tables WHERE table_name = p_table;
    RETURN v_n > 0;
  END;

  FUNCTION column_exists(p_table VARCHAR2, p_column VARCHAR2) RETURN BOOLEAN IS
    v_n NUMBER;
  BEGIN
    SELECT COUNT(*) INTO v_n FROM user_tab_columns WHERE table_name = p_table AND column_name = p_column;
    RETURN v_n > 0;
  END;

  FUNCTION sequence_exists(p_sequence VARCHAR2) RETURN BOOLEAN IS
    v_n NUMBER;
  BEGIN
    SELECT COUNT(*) INTO v_n FROM user_sequences WHERE sequence_name = p_sequence;
    RETURN v_n > 0;
  END;

  FUNCTION constraint_columns(p_constraint VARCHAR2) RETURN VARCHAR2 IS
    v_cols VARCHAR2(4000);
  BEGIN
    SELECT LISTAGG(column_name, ',') WITHIN GROUP (ORDER BY position) INTO v_cols
      FROM user_cons_columns WHERE constraint_name = p_constraint;
    RETURN v_cols;
  END;

  PROCEDURE rename_table(p_old VARCHAR2, p_new VARCHAR2) IS
  BEGIN
    IF table_exists(p_old) AND NOT table_exists(p_new) THEN
      exec_ddl('ALTER TABLE ' || p_old || ' RENAME TO ' || p_new);
    ELSE
      DBMS_OUTPUT.PUT_LINE('  (taula ' || p_old || ' ja renomenada)');
    END IF;
  END;

  PROCEDURE rename_sequence(p_old VARCHAR2, p_new VARCHAR2) IS
  BEGIN
    IF sequence_exists(p_old) AND NOT sequence_exists(p_new) THEN
      exec_ddl('RENAME ' || p_old || ' TO ' || p_new);
    ELSE
      DBMS_OUTPUT.PUT_LINE('  (sequencia ' || p_old || ' ja renomenada)');
    END IF;
  END;

  PROCEDURE add_column(p_table VARCHAR2, p_column VARCHAR2, p_type VARCHAR2) IS
  BEGIN
    IF NOT column_exists(p_table, p_column) THEN
      exec_ddl('ALTER TABLE ' || p_table || ' ADD ' || p_column || ' ' || p_type);
    ELSE
      DBMS_OUTPUT.PUT_LINE('  (columna ' || p_table || '.' || p_column || ' ja existeix)');
    END IF;
  END;

  -- Deixa a p_table una restriccio de tipus p_type sobre p_cols amb el nom p_name:
  --   - si ja n'hi ha una amb aquest nom, no fa res;
  --   - si n'hi ha una equivalent amb un altre nom, la renomena (i el seu index, si te el mateix nom);
  --   - si no n'hi ha cap, la crea amb p_ddl.
  PROCEDURE ensure_constraint(p_table VARCHAR2, p_type VARCHAR2, p_cols VARCHAR2, p_name VARCHAR2, p_ddl VARCHAR2) IS
    v_n   NUMBER;
    v_old VARCHAR2(128);
  BEGIN
    SELECT COUNT(*) INTO v_n FROM user_constraints WHERE table_name = p_table AND constraint_name = p_name;
    IF v_n > 0 THEN
      DBMS_OUTPUT.PUT_LINE('  (restriccio ' || p_name || ' ja existeix)');
      RETURN;
    END IF;
    FOR r IN (SELECT constraint_name FROM user_constraints
               WHERE table_name = p_table AND constraint_type = p_type ORDER BY constraint_name) LOOP
      IF constraint_columns(r.constraint_name) = p_cols THEN
        v_old := r.constraint_name;
        EXIT;
      END IF;
    END LOOP;
    IF v_old IS NOT NULL THEN
      exec_ddl('ALTER TABLE ' || p_table || ' RENAME CONSTRAINT ' || v_old || ' TO ' || p_name);
      SELECT COUNT(*) INTO v_n FROM user_indexes WHERE table_name = p_table AND index_name = v_old;
      IF v_n > 0 THEN
        SELECT COUNT(*) INTO v_n FROM user_indexes WHERE index_name = p_name;
        IF v_n = 0 THEN
          exec_ddl('ALTER INDEX ' || v_old || ' RENAME TO ' || p_name);
        END IF;
      END IF;
    ELSIF p_ddl IS NOT NULL THEN
      exec_ddl(p_ddl);
    ELSE
      RAISE_APPLICATION_ERROR(-20001, 'No s''ha trobat la restriccio ' || p_type || ' sobre ' || p_table || '(' || p_cols || ')');
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
  -- --------------------------------------------------------------------------
  -- 1. Renombrat de les taules ACL amb el prefix PBL_ seguint l'estandard de
  --    nomenclatura dels projectes es.caib. Oracle mou amb la taula les seves
  --    restriccions, index i triggers.
  -- --------------------------------------------------------------------------
  DBMS_OUTPUT.PUT_LINE('1. Taules ACL');
  rename_table('ACL_SID',             'PBL_ACL_SID');
  rename_table('ACL_CLASS',           'PBL_ACL_CLASS');
  rename_table('ACL_OBJECT_IDENTITY', 'PBL_ACL_OBJECT_IDENTITY');
  rename_table('ACL_ENTRY',           'PBL_ACL_ENTRY');

  -- Columna class_id_type, requerida per spring-security-acl 5.x per donar
  -- suport a identificadors d'objecte no numerics (AclClassIdSupported)
  add_column('PBL_ACL_CLASS', 'CLASS_ID_TYPE', 'VARCHAR2(100)');

  -- --------------------------------------------------------------------------
  -- 2. Claus primaries, restriccions d'unicitat i claus foranes amb el prefix PBL_
  --    (es localitzen per columnes, independentment del nom que tinguin)
  -- --------------------------------------------------------------------------
  DBMS_OUTPUT.PUT_LINE('2. Restriccions ACL');
  ensure_constraint('PBL_ACL_SID',             'P', 'ID', 'PBL_ACL_SID_PK', NULL);
  ensure_constraint('PBL_ACL_CLASS',           'P', 'ID', 'PBL_ACL_CLASS_PK', NULL);
  ensure_constraint('PBL_ACL_OBJECT_IDENTITY', 'P', 'ID', 'PBL_ACL_OBJECT_IDENTITY_PK', NULL);
  ensure_constraint('PBL_ACL_ENTRY',           'P', 'ID', 'PBL_ACL_ENTRY_PK', NULL);

  ensure_constraint('PBL_ACL_SID', 'U', 'SID,PRINCIPAL', 'PBL_ACL_SID_UK',
    'ALTER TABLE pbl_acl_sid ADD CONSTRAINT pbl_acl_sid_uk UNIQUE (sid, principal)');
  ensure_constraint('PBL_ACL_CLASS', 'U', 'CLASS', 'PBL_ACL_CLASS_UK',
    'ALTER TABLE pbl_acl_class ADD CONSTRAINT pbl_acl_class_uk UNIQUE (class)');
  ensure_constraint('PBL_ACL_OBJECT_IDENTITY', 'U', 'OBJECT_ID_CLASS,OBJECT_ID_IDENTITY', 'PBL_ACL_OID_UK',
    'ALTER TABLE pbl_acl_object_identity ADD CONSTRAINT pbl_acl_oid_uk UNIQUE (object_id_class, object_id_identity)');
  ensure_constraint('PBL_ACL_ENTRY', 'U', 'ACL_OBJECT_IDENTITY,ACE_ORDER', 'PBL_ACL_ENTRY_UK',
    'ALTER TABLE pbl_acl_entry ADD CONSTRAINT pbl_acl_entry_uk UNIQUE (acl_object_identity, ace_order)');

  ensure_constraint('PBL_ACL_OBJECT_IDENTITY', 'R', 'PARENT_OBJECT', 'PBL_ACL_OID_PARENT_FK',
    'ALTER TABLE pbl_acl_object_identity ADD CONSTRAINT pbl_acl_oid_parent_fk FOREIGN KEY (parent_object) REFERENCES pbl_acl_object_identity (id)');
  ensure_constraint('PBL_ACL_OBJECT_IDENTITY', 'R', 'OBJECT_ID_CLASS', 'PBL_ACL_OID_CLASS_FK',
    'ALTER TABLE pbl_acl_object_identity ADD CONSTRAINT pbl_acl_oid_class_fk FOREIGN KEY (object_id_class) REFERENCES pbl_acl_class (id)');
  ensure_constraint('PBL_ACL_OBJECT_IDENTITY', 'R', 'OWNER_SID', 'PBL_ACL_OID_OWNER_FK',
    'ALTER TABLE pbl_acl_object_identity ADD CONSTRAINT pbl_acl_oid_owner_fk FOREIGN KEY (owner_sid) REFERENCES pbl_acl_sid (id)');
  ensure_constraint('PBL_ACL_ENTRY', 'R', 'ACL_OBJECT_IDENTITY', 'PBL_ACL_ENTRY_OBJECT_FK',
    'ALTER TABLE pbl_acl_entry ADD CONSTRAINT pbl_acl_entry_object_fk FOREIGN KEY (acl_object_identity) REFERENCES pbl_acl_object_identity (id)');
  ensure_constraint('PBL_ACL_ENTRY', 'R', 'SID', 'PBL_ACL_ENTRY_ACL_FK',
    'ALTER TABLE pbl_acl_entry ADD CONSTRAINT pbl_acl_entry_acl_fk FOREIGN KEY (sid) REFERENCES pbl_acl_sid (id)');

  -- --------------------------------------------------------------------------
  -- 3. Sequencies ACL amb el prefix PBL_ (el codi 2.0.1 fa PBL_ACL_*_SEQ.CURRVAL)
  -- --------------------------------------------------------------------------
  DBMS_OUTPUT.PUT_LINE('3. Sequencies ACL');
  rename_sequence('ACL_SID_SEQ',   'PBL_ACL_SID_SEQ');
  rename_sequence('ACL_CLASS_SEQ', 'PBL_ACL_CLASS_SEQ');
  rename_sequence('ACL_OID_SEQ',   'PBL_ACL_OID_SEQ');
  rename_sequence('ACL_ENTRY_SEQ', 'PBL_ACL_ENTRY_SEQ');

  -- --------------------------------------------------------------------------
  -- 4. Triggers d'assignacio d'ID. S'eliminen els antics (de qualsevol dels dos
  --    jocs de noms): han seguit la taula en el renombrat pero referencien les
  --    sequencies antigues, per tant quedarien INVALID i farien fallar els INSERT.
  -- --------------------------------------------------------------------------
  DBMS_OUTPUT.PUT_LINE('4. Triggers ACL');
  drop_trigger('ACL_SID_TRG');
  drop_trigger('ACL_CLASS_TRG');
  drop_trigger('ACL_OID_TRG');
  drop_trigger('ACL_ENTRY_TRG');
  drop_trigger('ACL_SID_IDGEN');
  drop_trigger('ACL_CLASS_IDGEN');
  drop_trigger('ACL_OID_IDGEN');
  drop_trigger('ACL_ENTRY_IDGEN');

  create_idgen_trigger('PBL_ACL_SID_IDGEN',   'PBL_ACL_SID',             'PBL_ACL_SID_SEQ');
  create_idgen_trigger('PBL_ACL_CLASS_IDGEN', 'PBL_ACL_CLASS',           'PBL_ACL_CLASS_SEQ');
  create_idgen_trigger('PBL_ACL_OID_IDGEN',   'PBL_ACL_OBJECT_IDENTITY', 'PBL_ACL_OID_SEQ');
  create_idgen_trigger('PBL_ACL_ENTRY_IDGEN', 'PBL_ACL_ENTRY',           'PBL_ACL_ENTRY_SEQ');

  -- --------------------------------------------------------------------------
  -- 5. Permisos per a l'usuari de l'aplicacio (els existents es conserven en
  --    el renombrat; es tornen a donar per si de cas)
  -- --------------------------------------------------------------------------
  DBMS_OUTPUT.PUT_LINE('5. Permisos');
  exec_ddl('GRANT INSERT, SELECT, UPDATE, DELETE ON pbl_acl_sid TO WWW_PINBAL');
  exec_ddl('GRANT INSERT, SELECT, UPDATE, DELETE ON pbl_acl_class TO WWW_PINBAL');
  exec_ddl('GRANT INSERT, SELECT, UPDATE, DELETE ON pbl_acl_object_identity TO WWW_PINBAL');
  exec_ddl('GRANT INSERT, SELECT, UPDATE, DELETE ON pbl_acl_entry TO WWW_PINBAL');
  exec_ddl('GRANT SELECT ON pbl_acl_sid_seq TO WWW_PINBAL');
  exec_ddl('GRANT SELECT ON pbl_acl_class_seq TO WWW_PINBAL');
  exec_ddl('GRANT SELECT ON pbl_acl_oid_seq TO WWW_PINBAL');
  exec_ddl('GRANT SELECT ON pbl_acl_entry_seq TO WWW_PINBAL');

  -- --------------------------------------------------------------------------
  -- 6. Columna justificanterror a pbl_consulta_list / pbl_consulta_hist_list
  -- --------------------------------------------------------------------------
  DBMS_OUTPUT.PUT_LINE('6. Columnes justificanterror');
  add_column('PBL_CONSULTA_LIST',      'JUSTIFICANTERROR', 'VARCHAR2(4000 CHAR)');
  add_column('PBL_CONSULTA_HIST_LIST', 'JUSTIFICANTERROR', 'VARCHAR2(4000 CHAR)');
END;
/

WHENEVER SQLERROR CONTINUE

PROMPT
PROMPT === Verificacio: triggers de PBL_ACL_* (nomes els 4 PBL_ACL_*_IDGEN, ENABLED i VALID) ===
COLUMN table_name      FORMAT A26
COLUMN trigger_name    FORMAT A26
COLUMN constraint_name FORMAT A30
SELECT t.table_name, t.trigger_name, t.status AS enabled, o.status AS compilacio
  FROM user_triggers t
  JOIN user_objects o ON o.object_name = t.trigger_name AND o.object_type = 'TRIGGER'
 WHERE t.table_name LIKE 'PBL\_ACL\_%' ESCAPE '\'
 ORDER BY t.table_name, t.trigger_name;

PROMPT
PROMPT === Verificacio: restriccions de PBL_ACL_* (totes amb prefix PBL_) ===
SELECT table_name, constraint_name, constraint_type AS t, status
  FROM user_constraints
 WHERE table_name LIKE 'PBL\_ACL\_%' ESCAPE '\'
   AND constraint_type IN ('P','U','R')
 ORDER BY table_name, constraint_type, constraint_name;

PROMPT
PROMPT === Verificacio: objectes INVALID (ha de sortir buit) ===
SELECT object_type, object_name FROM user_objects WHERE status <> 'VALID';
