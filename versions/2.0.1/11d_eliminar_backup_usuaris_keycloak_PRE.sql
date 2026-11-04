-- ============================================================================
-- PINBAL 2.0.1 -- Migracio d'usuaris d'integracio ($xxx -> $xxx_pre)
-- PAS 3 de 3: ELIMINAR les taules de backup *_BK.
-- ============================================================================
SET SERVEROUTPUT ON SIZE UNLIMITED

DECLARE
  TYPE t_taules IS TABLE OF VARCHAR2(30);
  c_taules CONSTANT t_taules := t_taules(
      'PBL_USUARI_BK', 'PBL_ENTITAT_USUARI_BK', 'PBL_ACL_SID_BK',
      'PBL_ACL_ENTRY_BK', 'PBL_ACL_OBJECT_IDENTITY_BK', 'PBL_CONSULTA_BK',
      'PBL_CONSULTA_LIST_BK', 'PBL_USUARI_KC_CTRL_BK');

  v_n          NUMBER;
  v_data       TIMESTAMP;
  v_usuaris    NUMBER;
  v_pendents   NUMBER := 0;

  PROCEDURE pendent(p_desc VARCHAR2, p_n NUMBER) IS
  BEGIN
    DBMS_OUTPUT.PUT_LINE('  ' || RPAD(p_desc, 45, '.') || ' ' || p_n);
    v_pendents := v_pendents + p_n;
  END;
BEGIN
  -- 1. Transaccio pendent
  IF DBMS_TRANSACTION.LOCAL_TRANSACTION_ID IS NOT NULL THEN
    RAISE_APPLICATION_ERROR(-20020,
        'La sessio te una transaccio pendent. Feu COMMIT o ROLLBACK de la migracio abans d''esborrar el backup.');
  END IF;

  -- 2. Registre d'execucio correcta de 11c
  SELECT COUNT(*) INTO v_n FROM user_tables WHERE table_name = 'PBL_USUARI_KC_CTRL_BK';
  IF v_n = 0 THEN
    RAISE_APPLICATION_ERROR(-20021,
        'No existeix la taula de control PBL_USUARI_KC_CTRL_BK. No s''ha fet el backup (11b) o ja s''ha esborrat.');
  END IF;
  EXECUTE IMMEDIATE 'SELECT COUNT(*) FROM pbl_usuari_kc_ctrl_bk' INTO v_n;
  IF v_n = 0 THEN
    RAISE_APPLICATION_ERROR(-20022,
        'No hi ha cap execucio correcta i confirmada de 11c_migracio_usuaris_keycloak_PRE.sql. No s''esborra el backup.');
  END IF;
  EXECUTE IMMEDIATE
      'SELECT data_execucio, usuaris FROM ('
      || ' SELECT * FROM pbl_usuari_kc_ctrl_bk ORDER BY data_execucio DESC) WHERE ROWNUM = 1'
      INTO v_data, v_usuaris;
  DBMS_OUTPUT.PUT_LINE('Darrera migracio confirmada: ' || TO_CHAR(v_data, 'DD/MM/YYYY HH24:MI:SS')
      || ' (' || v_usuaris || ' usuaris)');

  -- 3. No queda res per migrar
  DBMS_OUTPUT.PUT_LINE('Dades pendents de migrar dels usuaris $xxx antics:');
  SELECT COUNT(*) INTO v_n
    FROM pbl_entitat_usuari eu
   WHERE eu.usuari_id LIKE '$%' AND NOT REGEXP_LIKE(eu.usuari_id, '_pre$');
  pendent('PBL_ENTITAT_USUARI', v_n);
  SELECT COUNT(*) INTO v_n
    FROM pbl_acl_sid s
   WHERE s.principal = 1 AND s.sid LIKE '$%' AND NOT REGEXP_LIKE(s.sid, '_pre$')
     AND EXISTS (SELECT 1 FROM pbl_usuari u WHERE u.codi = s.sid);
  pendent('PBL_ACL_SID', v_n);
  SELECT COUNT(*) INTO v_n
    FROM pbl_consulta c
   WHERE c.createdby_codi LIKE '$%' AND NOT REGEXP_LIKE(c.createdby_codi, '_pre$');
  pendent('PBL_CONSULTA', v_n);
  SELECT COUNT(*) INTO v_n
    FROM pbl_consulta_list c
   WHERE c.usuaricodi LIKE '$%' AND NOT REGEXP_LIKE(c.usuaricodi, '_pre$');
  pendent('PBL_CONSULTA_LIST', v_n);
  IF v_pendents > 0 THEN
    RAISE_APPLICATION_ERROR(-20023,
        'Queden ' || v_pendents || ' registres d''usuaris antics sense migrar (o creats despres de la migracio per'
        || ' una aplicacio que encara usa l''usuari antic). Reviseu-ho i torneu a executar 11c. No s''esborra el backup.');
  END IF;

  -- Tot correcte: s'esborra el backup
  FOR i IN 1 .. c_taules.COUNT LOOP
    SELECT COUNT(*) INTO v_n FROM user_tables WHERE table_name = c_taules(i);
    IF v_n > 0 THEN
      EXECUTE IMMEDIATE 'DROP TABLE ' || c_taules(i);
      DBMS_OUTPUT.PUT_LINE('Esborrada ' || c_taules(i));
    ELSE
      DBMS_OUTPUT.PUT_LINE('No existeix ' || c_taules(i));
    END IF;
  END LOOP;
  DBMS_OUTPUT.PUT_LINE('');
  DBMS_OUTPUT.PUT_LINE('Backup eliminat.');
END;
/
