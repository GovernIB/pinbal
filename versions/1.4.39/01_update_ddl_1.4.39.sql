-- #289
ALTER TABLE pbl_consulta ADD entitat_id NUMBER(38, 0);
ALTER TABLE pbl_consulta ADD servei_codi VARCHAR2(64 CHAR);
ALTER TABLE pbl_consulta ADD procediment_id NUMBER(38, 0);
ALTER TABLE pbl_consulta ADD CONSTRAINT pbl_consulta_proced_fk FOREIGN KEY (procediment_id) REFERENCES pbl_procediment (id);
ALTER TABLE pbl_consulta ADD CONSTRAINT pbl_consulta_entitat_fk FOREIGN KEY (entitat_id) REFERENCES pbl_entitat (id);

ALTER TABLE pbl_consulta_hist ADD entitat_id NUMBER(38, 0);
ALTER TABLE pbl_consulta_hist ADD servei_codi VARCHAR2(64 CHAR);
ALTER TABLE pbl_consulta_hist ADD procediment_id NUMBER(38, 0);
ALTER TABLE pbl_consulta_hist ADD CONSTRAINT pbl_consultah_proced_fk FOREIGN KEY (procediment_id) REFERENCES pbl_procediment (id);
ALTER TABLE pbl_consulta_hist ADD CONSTRAINT pbl_consultah_entitat_fk FOREIGN KEY (entitat_id) REFERENCES pbl_entitat (id);