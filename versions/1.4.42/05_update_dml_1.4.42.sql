-- Error API Client PINBAL Recobriment pre 1.4.42
UPDATE PBL_CONSULTA SET dades_especifiques = dades_espedifiques_aux where dades_espedifiques_aux is not null;
UPDATE PBL_CONSULTA_HIST SET dades_especifiques = dades_espedifiques_aux where dades_espedifiques_aux is not null;