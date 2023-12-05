-- #289
UPDATE PBL_CONSULTA c SET c.entitat_id = (SELECT ppr.entitat_id FROM pbl_procediment_servei pps, pbl_procediment ppr WHERE pps.id = c.procserv_id AND ppr.id = pps.procediment_id);
UPDATE PBL_CONSULTA c SET c.servei_codi = (SELECT pps.servei_id FROM pbl_procediment_servei pps WHERE pps.id = c.procserv_id);
UPDATE PBL_CONSULTA c SET c.procediment_id = (SELECT pps.procediment_id FROM pbl_procediment_servei pps WHERE pps.id = c.procserv_id);

UPDATE PBL_CONSULTA_HIST c SET c.entitat_id = (SELECT ppr.entitat_id FROM pbl_procediment_servei pps, pbl_procediment ppr WHERE pps.id = c.procserv_id AND ppr.id = pps.procediment_id);
UPDATE PBL_CONSULTA_HIST c SET c.servei_codi = (SELECT pps.servei_id FROM pbl_procediment_servei pps WHERE pps.id = c.procserv_id);
UPDATE PBL_CONSULTA_HIST c SET c.procediment_id = (SELECT pps.procediment_id FROM pbl_procediment_servei pps WHERE pps.id = c.procserv_id);

UPDATE PBL_CONFIG SET key = 'es.caib.pinbal.plugin.arxiu.caib.csv_generation_definition' where key = 'es.caib.pinbal.plugin.arxiu.caib.csv.definicio';