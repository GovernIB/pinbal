-- 290 Permetre als usuaris definir dades per defecte
ALTER TABLE pbl_usuari ADD procediment_id NUMBER(38, 0);
ALTER TABLE pbl_usuari ADD servei_codi VARCHAR2(64 CHAR);
ALTER TABLE pbl_usuari ADD entitat_id NUMBER(38, 0);
ALTER TABLE pbl_usuari ADD departament VARCHAR2(250 CHAR);
ALTER TABLE pbl_usuari ADD finalitat VARCHAR2(250 CHAR);