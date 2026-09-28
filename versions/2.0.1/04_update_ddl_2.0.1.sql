-- Afegeix la FK amb ON DELETE SET NULL: esborrar una ClauPrivada no ha de bloquejar-se ni deixar referències òrfenes, i el camp ja es tracta com a opcional a la resta del codi
ALTER TABLE core_req_cesionarios_servicios ADD CONSTRAINT fk_clave_priv_reqcesionaris FOREIGN KEY (claveprivada) REFERENCES core_clave_privada (id) ON DELETE SET NULL;
