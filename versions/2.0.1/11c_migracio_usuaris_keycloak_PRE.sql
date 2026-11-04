-- ============================================================================
-- PINBAL 2.0.1 -- Migracio d'usuaris d'integracio ($xxx -> $xxx_pre)
-- PAS 2 de 3: MIGRACIO.
-- ============================================================================
SET SERVEROUTPUT ON SIZE UNLIMITED

DECLARE
  -- ==================== PARAMETRES ====================
  c_sufix                CONSTANT VARCHAR2(10) := '_pre';   -- Nomes PRE (a PRO no canvien els usuaris)
  c_commit_per_usuari    CONSTANT BOOLEAN      := FALSE;   -- TRUE si el volum de consultes es molt gran
  -- ====================================================

  v_nou        pbl_usuari.codi%TYPE;
  v_sid_antic  pbl_acl_sid.id%TYPE;
  v_sid_nou    pbl_acl_sid.id%TYPE;
  v_n          NUMBER;
  v_usuaris    NUMBER := 0;

  PROCEDURE log(p_msg VARCHAR2) IS
  BEGIN
    DBMS_OUTPUT.PUT_LINE(p_msg);
  END;

  FUNCTION sid_id(p_sid VARCHAR2) RETURN NUMBER IS
    v_id pbl_acl_sid.id%TYPE;
  BEGIN
    SELECT id INTO v_id FROM pbl_acl_sid WHERE sid = p_sid AND principal = 1;
    RETURN v_id;
  EXCEPTION
    WHEN NO_DATA_FOUND THEN RETURN NULL;
  END;
BEGIN
  SELECT COUNT(*) INTO v_n
    FROM user_tables
   WHERE table_name IN ('PBL_USUARI_BK', 'PBL_ENTITAT_USUARI_BK', 'PBL_ACL_SID_BK',
                        'PBL_ACL_ENTRY_BK', 'PBL_ACL_OBJECT_IDENTITY_BK', 'PBL_CONSULTA_BK',
                        'PBL_CONSULTA_LIST_BK', 'PBL_USUARI_KC_CTRL_BK');
  IF v_n < 8 THEN
    RAISE_APPLICATION_ERROR(-20001,
        'Falten taules de backup *_BK (' || v_n || ' de 8). Executeu abans 11b_backup_usuaris_keycloak_PRE.sql.');
  END IF;

  FOR u IN (SELECT *
              FROM pbl_usuari
             WHERE codi LIKE '$%'
               AND NOT REGEXP_LIKE(codi, '_pre$')
             ORDER BY codi) LOOP

    v_nou := u.codi || c_sufix;
    v_usuaris := v_usuaris + 1;
    log('==== ' || u.codi || ' -> ' || v_nou);

    IF LENGTH(v_nou) > 64 THEN
      RAISE_APPLICATION_ERROR(-20002, 'Codi nou massa llarg (>64): ' || v_nou);
    END IF;

    -- ---------------------------------------------------------------
    -- 1. PBL_USUARI
    -- ---------------------------------------------------------------
    SELECT COUNT(*) INTO v_n FROM pbl_usuari WHERE codi = v_nou;
    IF v_n = 0 THEN
      IF u.nif IS NOT NULL THEN
        UPDATE pbl_usuari SET nif = NULL WHERE codi = u.codi;
      END IF;
      INSERT INTO pbl_usuari (
          codi, nom, nif, email, idioma, inicialitzat, version,
          procediment_id, servei_codi, entitat_id, departament, finalitat, num_elements_pagina)
      VALUES (
          v_nou, u.nom, u.nif, u.email, u.idioma, u.inicialitzat, 0,
          u.procediment_id, u.servei_codi, u.entitat_id, u.departament, u.finalitat, u.num_elements_pagina);
      log('  usuari creat');
    ELSE
      log('  usuari ja existent, es reaprofita');
    END IF;

    -- ---------------------------------------------------------------
    -- 2. PBL_ENTITAT_USUARI
    -- ---------------------------------------------------------------
    FOR eu IN (SELECT * FROM pbl_entitat_usuari WHERE usuari_id = u.codi) LOOP
      SELECT COUNT(*) INTO v_n
        FROM pbl_entitat_usuari
       WHERE entitat_id = eu.entitat_id AND usuari_id = v_nou;
      IF v_n = 0 THEN
        UPDATE pbl_entitat_usuari SET usuari_id = v_nou WHERE id = eu.id;
        log('  entitat ' || eu.entitat_id || ': relacio moguda');
      ELSE
        -- NVL: els rols admeten NULL i GREATEST(NULL, 1) retorna NULL (es perdria el rol)
        UPDATE pbl_entitat_usuari
           SET representant = GREATEST(NVL(representant, 0), NVL(eu.representant, 0)),
               delegat      = GREATEST(NVL(delegat,      0), NVL(eu.delegat,      0)),
               auditor      = GREATEST(NVL(auditor,      0), NVL(eu.auditor,      0)),
               aplicacio    = GREATEST(NVL(aplicacio,    0), NVL(eu.aplicacio,    0)),
               actiu        = GREATEST(NVL(actiu,        0), NVL(eu.actiu,        0)),
               departament  = NVL(departament, eu.departament)
         WHERE entitat_id = eu.entitat_id AND usuari_id = v_nou;
        DELETE FROM pbl_entitat_usuari WHERE id = eu.id;
        log('  entitat ' || eu.entitat_id || ': rols fusionats amb la relacio existent');
      END IF;
    END LOOP;

    -- ---------------------------------------------------------------
    -- 3. ACL
    -- ---------------------------------------------------------------
    v_sid_antic := sid_id(u.codi);
    IF v_sid_antic IS NOT NULL THEN
      v_sid_nou := sid_id(v_nou);
      IF v_sid_nou IS NULL THEN
        UPDATE pbl_acl_sid SET sid = v_nou WHERE id = v_sid_antic;
        SELECT COUNT(*) INTO v_n FROM pbl_acl_entry WHERE sid = v_sid_antic;
        log('  ACL: SID renomenat (' || v_n || ' permisos)');
      ELSE
        DELETE FROM pbl_acl_entry e
         WHERE e.sid = v_sid_antic
           AND EXISTS (SELECT 1 FROM pbl_acl_entry e2
                        WHERE e2.sid = v_sid_nou
                          AND e2.acl_object_identity = e.acl_object_identity
                          AND e2.mask = e.mask
                          AND e2.granting = e.granting);
        log('  ACL: ' || SQL%ROWCOUNT || ' permisos duplicats eliminats');
        UPDATE pbl_acl_entry SET sid = v_sid_nou WHERE sid = v_sid_antic;
        log('  ACL: ' || SQL%ROWCOUNT || ' permisos moguts al SID existent');
        UPDATE pbl_acl_object_identity SET owner_sid = v_sid_nou WHERE owner_sid = v_sid_antic;
        DELETE FROM pbl_acl_sid WHERE id = v_sid_antic;
      END IF;
    ELSE
      log('  ACL: sense permisos');
    END IF;

    -- ---------------------------------------------------------------
    -- 4. Propietat de les consultes actives
    -- ---------------------------------------------------------------
    UPDATE pbl_consulta SET createdby_codi = v_nou WHERE createdby_codi = u.codi;
    log('  PBL_CONSULTA: ' || SQL%ROWCOUNT);
    UPDATE pbl_consulta_list SET usuaricodi = v_nou WHERE usuaricodi = u.codi;
    log('  PBL_CONSULTA_LIST: ' || SQL%ROWCOUNT);

    IF c_commit_per_usuari THEN
      COMMIT;
      log('  COMMIT');
    END IF;
  END LOOP;

  -- Registre de control (dins la transaccio: nomes queda si es fa COMMIT)
  EXECUTE IMMEDIATE
      'INSERT INTO pbl_usuari_kc_ctrl_bk (data_execucio, usuaris) VALUES (SYSTIMESTAMP, :1)'
      USING v_usuaris;

  log('');
  log('Usuaris processats: ' || v_usuaris);
  IF c_commit_per_usuari THEN
    COMMIT;
    log('COMMIT');
  ELSE
    log('Revisar la sortida i executar COMMIT o ROLLBACK.');
  END IF;
END;
/
