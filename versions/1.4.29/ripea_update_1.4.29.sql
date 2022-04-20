
ALTER TABLE pbl_servei_config ADD max_peticions_min INTEGER;

ALTER TABLE pbl_consulta ADD finalitat VARCHAR2(250);

ALTER TABLE pbl_consulta ADD consentiment INTEGER;

ALTER TABLE pbl_consulta ADD expedient_id VARCHAR2(25);

ALTER TABLE pbl_consulta ADD dades_especifiques VARCHAR2(2048);

