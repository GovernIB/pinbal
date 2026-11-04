-- ============================================================================
-- PINBAL 2.0.1 -- Migracio d'usuaris d'integracio ($xxx -> $xxx_pre)
-- PAS 1 de 3: BACKUP de l'estat previ.
-- ============================================================================
SET SERVEROUTPUT ON SIZE UNLIMITED

DECLARE
  v_n NUMBER;

  PROCEDURE bk(p_taula VARCHAR2, p_select VARCHAR2) IS
    v_nom VARCHAR2(30) := p_taula || '_BK';
  BEGIN
    SELECT COUNT(*) INTO v_n FROM user_tables WHERE table_name = v_nom;
    IF v_n > 0 THEN
      DBMS_OUTPUT.PUT_LINE('Backup ' || v_nom || ': ja existeix, es conserva');
    ELSE
      EXECUTE IMMEDIATE 'CREATE TABLE ' || v_nom || ' AS ' || p_select;
      EXECUTE IMMEDIATE 'SELECT COUNT(*) FROM ' || v_nom INTO v_n;
      DBMS_OUTPUT.PUT_LINE('Backup ' || v_nom || ': ' || v_n || ' files');
    END IF;
  END;
BEGIN
  IF DBMS_TRANSACTION.LOCAL_TRANSACTION_ID IS NOT NULL THEN
    RAISE_APPLICATION_ERROR(-20010,
        'La sessio te una transaccio pendent. Feu COMMIT o ROLLBACK abans (el DDL del backup la confirmaria).');
  END IF;

  bk('PBL_USUARI',              'SELECT * FROM pbl_usuari');
  bk('PBL_ENTITAT_USUARI',      'SELECT * FROM pbl_entitat_usuari');
  bk('PBL_ACL_SID',             'SELECT * FROM pbl_acl_sid');
  bk('PBL_ACL_ENTRY',           'SELECT * FROM pbl_acl_entry');
  bk('PBL_ACL_OBJECT_IDENTITY', 'SELECT * FROM pbl_acl_object_identity');
  bk('PBL_CONSULTA',            'SELECT id, createdby_codi FROM pbl_consulta WHERE createdby_codi LIKE ''$%''');
  bk('PBL_CONSULTA_LIST',       'SELECT id, usuaricodi FROM pbl_consulta_list WHERE usuaricodi LIKE ''$%''');

  SELECT COUNT(*) INTO v_n FROM user_tables WHERE table_name = 'PBL_USUARI_KC_CTRL_BK';
  IF v_n = 0 THEN
    EXECUTE IMMEDIATE 'CREATE TABLE pbl_usuari_kc_ctrl_bk ('
        || ' data_execucio        TIMESTAMP NOT NULL,'
        || ' usuaris              NUMBER    NOT NULL)';
    DBMS_OUTPUT.PUT_LINE('Taula de control PBL_USUARI_KC_CTRL_BK: creada');
  ELSE
    DBMS_OUTPUT.PUT_LINE('Taula de control PBL_USUARI_KC_CTRL_BK: ja existeix, es conserva');
  END IF;

  DBMS_OUTPUT.PUT_LINE('');
  DBMS_OUTPUT.PUT_LINE('Backup fet. Seguent pas: 11c_migracio_usuaris_keycloak_PRE.sql');
END;
/
