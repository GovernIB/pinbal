-- ============================================================================
-- PINBAL 2.0.1 -- Migracio d'usuaris d'integracio ($xxx) de SEYCON a Keycloak
--
--   NOMES PRE: $xxx -> $xxx_pre
--   A PRO els usuaris d'integracio NO canvien: aquest script NO s'ha d'executar a PRO.
--
-- Per a cada usuari de PBL_USUARI que comenca per '$' i encara no acaba en
-- _pre:
--   1. PBL_USUARI          Crea l'usuari nou copiant les dades de l'antic
--                          (si ja existeix, el reaprofita). El NIF es mou a
--                          l'usuari nou perque a PRE pot existir la UK
--                          PBL_USUNIF_UK heretada dels scripts 1.x.
--   2. PBL_ENTITAT_USUARI  Mou les relacions entitat-usuari (rols representant,
--                          delegat, auditor, aplicacio, departament...). Si el
--                          nou ja estava a l'entitat, es fusionen els rols.
--   3. PBL_ACL_*           Mou tots els permisos ACL (procediment-servei...):
--                          si el SID nou no existeix es renomena el SID antic;
--                          si ja existeix, es mouen les entrades no duplicades
--                          i la propietat (owner_sid) i s'esborra el SID antic.
--   4. PBL_CONSULTA        Passa la propietat (CREATEDBY_CODI) de les consultes
--      PBL_CONSULTA_LIST   actives a l'usuari nou. Necessari perque l'aplicacio
--                          pugui recuperar justificants i respostes de consultes
--                          fetes amb l'usuari antic (es valida el creador).
--   5. PBL_CONSULTA_HIST   Opcional (c_actualitzar_historic). Per defecte NO:
--      PBL_CONSULTA_HIST_LIST  l'historic conserva l'usuari antic. Vegeu AVIS.
--
-- NO es toca (es conserva l'usuari antic com a dada historica):
--   - PBL_USUARI antic (referenciat per FK des de l'historic i l'auditoria)
--   - Columnes d'auditoria CREATEDBY/LASTMODIFIEDBY de la resta de taules
--   - PBL_EXPLOT_CONSULTA_DIM (estadistiques: les noves consultes generaran
--     dimensions amb l'usuari nou)
--   - PBL_CONFIG (credencials de PINBAL cap a altres sistemes, vegeu 11a)
--
-- AVIS historic: RecobrimentServiceImpl.getJustificant/getJustificantImprimible
--   (API REST v2) cerquen tambe a PBL_CONSULTA_HIST i validen que l'usuari
--   autenticat sigui el creador. Si l'historic conserva l'usuari antic, una
--   aplicacio que demani el justificant d'una consulta ja passada a l'historic
--   rebra AccessDenegatException. Si es vol evitar, posar
--   c_actualitzar_historic := TRUE.
--
-- Execucio:
--   - Revisar abans la sortida de 11a_diagnosi_usuaris_keycloak_PRE.sql.
--   - Nomes a PRE.
--   - Preferiblement amb l'aplicacio aturada; si no, reiniciar-la despres
--     (cache ACL i cache d'usuaris).
--   - L'script NO fa COMMIT (excepte si c_commit_per_usuari = TRUE). Revisar
--     la sortida i executar COMMIT o ROLLBACK.
--   - Idempotent: es pot tornar a executar. Els usuaris antics es tornen a
--     seleccionar (no s'esborren), pero ja no tenen res per moure i els
--     comptadors surten a 0.
-- ============================================================================
SET SERVEROUTPUT ON SIZE UNLIMITED

DECLARE
  -- ==================== PARAMETRES ====================
  c_sufix                CONSTANT VARCHAR2(10) := '_pre';   -- Nomes PRE (a PRO no canvien els usuaris)
  c_actualitzar_historic CONSTANT BOOLEAN      := FALSE;    -- TRUE: tambe PBL_CONSULTA_HIST(_LIST)
  c_commit_per_usuari    CONSTANT BOOLEAN      := TRUE;    -- TRUE si el volum de consultes es molt gran
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
        UPDATE pbl_entitat_usuari
           SET representant = GREATEST(representant, eu.representant),
               delegat      = GREATEST(delegat,      eu.delegat),
               auditor      = GREATEST(auditor,      eu.auditor),
               aplicacio    = GREATEST(aplicacio,    eu.aplicacio),
               actiu        = GREATEST(actiu,        eu.actiu),
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

    -- ---------------------------------------------------------------
    -- 5. Historic (opcional)
    -- ---------------------------------------------------------------
    IF c_actualitzar_historic THEN
      UPDATE pbl_consulta_hist SET createdby_codi = v_nou WHERE createdby_codi = u.codi;
      log('  PBL_CONSULTA_HIST: ' || SQL%ROWCOUNT);
      UPDATE pbl_consulta_hist_list SET usuaricodi = v_nou WHERE usuaricodi = u.codi;
      log('  PBL_CONSULTA_HIST_LIST: ' || SQL%ROWCOUNT);
    END IF;

    IF c_commit_per_usuari THEN
      COMMIT;
      log('  COMMIT');
    END IF;
  END LOOP;

  log('');
  log('Usuaris processats: ' || v_usuaris);
  IF NOT c_commit_per_usuari THEN
    log('Revisar la sortida i executar COMMIT o ROLLBACK.');
  END IF;
END;
/
