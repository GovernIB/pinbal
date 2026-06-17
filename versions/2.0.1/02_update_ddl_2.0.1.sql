-- Canviar noms de taules de ACS per afegir el prefix PBL
-- Eliminació de les claus foranes de acl_entry i acl_object_identity com a pas previ al renomenat de les taules ACL
ALTER TABLE acl_entry DROP CONSTRAINT acl_entry_object_fk;
ALTER TABLE acl_entry DROP CONSTRAINT acl_entry_acl_fk;
ALTER TABLE acl_object_identity DROP CONSTRAINT acl_oid_parent_fk;
ALTER TABLE acl_object_identity DROP CONSTRAINT acl_oid_class_fk;
ALTER TABLE acl_object_identity DROP CONSTRAINT acl_oid_owner_fk;

-- Eliminació de les restriccions d'unicitat de les taules ACL com a pas previ al renomenat
ALTER TABLE acl_sid DROP CONSTRAINT acl_sid_uk DROP INDEX;
ALTER TABLE acl_class DROP CONSTRAINT acl_class_uk DROP INDEX;
ALTER TABLE acl_object_identity DROP CONSTRAINT acl_oid_uk DROP INDEX;
ALTER TABLE acl_entry DROP CONSTRAINT acl_entry_uk DROP INDEX;

-- Renomenat de les taules ACL amb el prefix PBL_ seguint l'estàndard de nomenclatura dels projectes es.caib
ALTER TABLE acl_sid RENAME TO pbl_acl_sid;
ALTER TABLE acl_class RENAME TO pbl_acl_class;
ALTER TABLE acl_object_identity RENAME TO pbl_acl_object_identity;
ALTER TABLE acl_entry RENAME TO pbl_acl_entry;

-- Afegit de la columna class_id_type a pbl_acl_class, requerida per spring-security-acl 5.x per donar suport a identificadors d'objecte no numèrics (AclClassIdSupported)
ALTER TABLE pbl_acl_class ADD class_id_type VARCHAR2(100);

-- Recreació de les restriccions d'unicitat de les taules ACL amb el prefix PBL_
ALTER TABLE pbl_acl_sid ADD CONSTRAINT pbl_acl_sid_uk UNIQUE (sid, principal);
ALTER TABLE pbl_acl_class ADD CONSTRAINT pbl_acl_class_uk UNIQUE (class);
ALTER TABLE pbl_acl_object_identity ADD CONSTRAINT pbl_acl_oid_uk UNIQUE (object_id_class, object_id_identity);
ALTER TABLE pbl_acl_entry ADD CONSTRAINT pbl_acl_entry_uk UNIQUE (acl_object_identity, ace_order);

-- Recreació de les claus foranes de les taules ACL amb el prefix PBL_
ALTER TABLE pbl_acl_object_identity ADD CONSTRAINT pbl_acl_oid_parent_fk FOREIGN KEY (parent_object) REFERENCES pbl_acl_object_identity (id);
ALTER TABLE pbl_acl_object_identity ADD CONSTRAINT pbl_acl_oid_class_fk FOREIGN KEY (object_id_class) REFERENCES pbl_acl_class (id);
ALTER TABLE pbl_acl_object_identity ADD CONSTRAINT pbl_acl_oid_owner_fk FOREIGN KEY (owner_sid) REFERENCES pbl_acl_sid (id);
ALTER TABLE pbl_acl_entry ADD CONSTRAINT pbl_acl_entry_object_fk FOREIGN KEY (acl_object_identity) REFERENCES pbl_acl_object_identity (id);
ALTER TABLE pbl_acl_entry ADD CONSTRAINT pbl_acl_entry_acl_fk FOREIGN KEY (sid) REFERENCES pbl_acl_sid (id);

-- Renombrat de les seqüències ACL amb el prefix PBL_ (Oracle 12c+ i PostgreSQL)
RENAME acl_sid_seq TO pbl_acl_sid_seq;
RENAME acl_class_seq TO pbl_acl_class_seq;
RENAME acl_oid_seq TO pbl_acl_oid_seq;
RENAME acl_entry_seq TO pbl_acl_entry_seq;

-- Renombrat de les claus primàries de les taules ACL amb el prefix PBL_ (Oracle i PostgreSQL)
ALTER TABLE pbl_acl_sid RENAME CONSTRAINT acl_sid_pk TO pbl_acl_sid_pk;
ALTER TABLE pbl_acl_class RENAME CONSTRAINT acl_class_pk TO pbl_acl_class_pk;
ALTER TABLE pbl_acl_object_identity RENAME CONSTRAINT acl_object_identity_pk TO pbl_acl_object_identity_pk;
ALTER TABLE pbl_acl_entry RENAME CONSTRAINT acl_entry_pk TO pbl_acl_entry_pk;

-- Eliminació dels triggers Oracle de les taules ACL antigues i creació dels nous per a les taules i seqüències PBL_ACL_
DROP TRIGGER acl_sid_idgen;
DROP TRIGGER acl_class_idgen;
DROP TRIGGER acl_oid_idgen;
DROP TRIGGER acl_entry_idgen;

CREATE OR REPLACE TRIGGER pbl_acl_sid_idgen BEFORE INSERT ON pbl_acl_sid FOR EACH ROW BEGIN SELECT pbl_acl_sid_seq.NEXTVAL INTO :NEW.ID FROM DUAL; END;
CREATE OR REPLACE TRIGGER pbl_acl_class_idgen BEFORE INSERT ON pbl_acl_class FOR EACH ROW BEGIN SELECT pbl_acl_class_seq.NEXTVAL INTO :NEW.ID FROM DUAL; END;
CREATE OR REPLACE TRIGGER pbl_acl_oid_idgen BEFORE INSERT ON pbl_acl_object_identity FOR EACH ROW BEGIN SELECT pbl_acl_oid_seq.NEXTVAL INTO :NEW.ID FROM DUAL; END;
CREATE OR REPLACE TRIGGER pbl_acl_entry_idgen BEFORE INSERT ON pbl_acl_entry FOR EACH ROW BEGIN SELECT pbl_acl_entry_seq.NEXTVAL INTO :NEW.ID FROM DUAL; END;

GRANT INSERT, SELECT, UPDATE, DELETE ON pbl_acl_sid TO WWW_PINBAL;
GRANT INSERT, SELECT, UPDATE, DELETE ON pbl_acl_class TO WWW_PINBAL;
GRANT INSERT, SELECT, UPDATE, DELETE ON pbl_acl_object_identity TO WWW_PINBAL;
GRANT INSERT, SELECT, UPDATE, DELETE ON pbl_acl_entry TO WWW_PINBAL;