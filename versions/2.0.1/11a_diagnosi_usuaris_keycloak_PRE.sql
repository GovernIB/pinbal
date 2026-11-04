-- ============================================================================
-- PINBAL 2.0.1 -- Diagnosi previa a la migracio d'usuaris d'integracio ($xxx)
-- de SEYCON a Keycloak/Soffid ($xxx -> $xxx_pre).
--
-- NOMES LECTURA. Executar abans de 11b_migracio_usuaris_keycloak_PRE.sql per
-- revisar quins usuaris es migraran i quin volum de registres es tocara.
--

-- 1. Usuaris que es migraran, amb el codi nou i si ja existeix
SELECT u.codi                                   AS codi_antic,
       u.codi || '_pre'                         AS codi_nou,
       u.nom,
       u.nif,
       CASE WHEN n.codi IS NULL THEN 'NO' ELSE 'SI' END AS nou_ja_existeix,
       CASE WHEN LENGTH(u.codi || '_pre') > 64 THEN 'ERROR: >64 car.' END AS avis
  FROM pbl_usuari u
  LEFT JOIN pbl_usuari n ON n.codi = u.codi || '_pre'
 WHERE u.codi LIKE '$%'
   AND NOT REGEXP_LIKE(u.codi, '_pre$')
 ORDER BY u.codi;

-- 2. Volum per usuari: entitats, permisos ACL, consultes actives i historiques
SELECT u.codi,
       (SELECT COUNT(*) FROM pbl_entitat_usuari eu WHERE eu.usuari_id = u.codi)              AS entitats,
       (SELECT COUNT(*) FROM pbl_acl_entry e JOIN pbl_acl_sid s ON s.id = e.sid
         WHERE s.sid = u.codi AND s.principal = 1)                                          AS permisos_acl,
       (SELECT COUNT(*) FROM pbl_acl_object_identity o JOIN pbl_acl_sid s ON s.id = o.owner_sid
         WHERE s.sid = u.codi AND s.principal = 1)                                          AS acl_owner,
       (SELECT COUNT(*) FROM pbl_consulta c WHERE c.createdby_codi = u.codi)                AS consultes,
       (SELECT COUNT(*) FROM pbl_consulta_list c WHERE c.usuaricodi = u.codi)               AS consultes_list,
       (SELECT COUNT(*) FROM pbl_consulta_hist c WHERE c.createdby_codi = u.codi)           AS consultes_hist,
       (SELECT MAX(c.createddate) FROM pbl_consulta c WHERE c.createdby_codi = u.codi)      AS darrera_consulta
  FROM pbl_usuari u
 WHERE u.codi LIKE '$%'
   AND NOT REGEXP_LIKE(u.codi, '_pre$')
 ORDER BY u.codi;

-- 3. Usuaris nous ($xxx_pre) que ja tenen dades (es fara merge)
SELECT u.codi,
       (SELECT COUNT(*) FROM pbl_entitat_usuari eu WHERE eu.usuari_id = u.codi)              AS entitats,
       (SELECT COUNT(*) FROM pbl_acl_entry e JOIN pbl_acl_sid s ON s.id = e.sid
         WHERE s.sid = u.codi AND s.principal = 1)                                          AS permisos_acl,
       (SELECT COUNT(*) FROM pbl_consulta c WHERE c.createdby_codi = u.codi)                AS consultes
  FROM pbl_usuari u
 WHERE u.codi LIKE '$%'
   AND REGEXP_LIKE(u.codi, '_pre$')
 ORDER BY u.codi;

-- 4. Existeix la UK de NIF heretada dels scripts 1.x (PBL_USUNIF_UK)
SELECT constraint_name, constraint_type, status
  FROM user_constraints
 WHERE table_name = 'PBL_USUARI'
   AND constraint_type IN ('U', 'P');

-- 5. SIDs ACL amb '$' que NO tenen usuari a PBL_USUARI (permisos orfes)
SELECT s.id, s.sid, (SELECT COUNT(*) FROM pbl_acl_entry e WHERE e.sid = s.id) AS entries
  FROM pbl_acl_sid s
 WHERE s.principal = 1
   AND s.sid LIKE '$%'
   AND NOT EXISTS (SELECT 1 FROM pbl_usuari u WHERE u.codi = s.sid)
 ORDER BY s.sid;

-- 6. Propietats de configuracio amb usuaris '$' (credencials de PINBAL cap a altres sistemes: PortaFIB, Arxiu/ConCSV, DIR3...)
SELECT key, value, source_property
  FROM pbl_config
 WHERE value LIKE '$%'
 ORDER BY key;
