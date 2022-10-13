-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 29/09/22 11:10
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.3.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.4.34/236.yaml::1660889406593-2::limit
ALTER TABLE pbl_servei_config ADD ini_dades_especifiques NUMBER(1) DEFAULT '0';

-- Changeset db/changelog/changes/1.4.35/242.yaml::1663840075438-1::limit
UPDATE pbl_procediment SET valor_camp_clasetramite = '0' WHERE valor_camp_clasetramite = 19;

UPDATE pbl_procediment SET valor_camp_clasetramite = '19' WHERE valor_camp_clasetramite = 2;

UPDATE pbl_procediment SET valor_camp_clasetramite = '20' WHERE valor_camp_clasetramite = 3;

UPDATE pbl_procediment SET valor_camp_clasetramite = '21' WHERE valor_camp_clasetramite = 4;

UPDATE pbl_procediment SET valor_camp_clasetramite = '22' WHERE valor_camp_clasetramite = 5;

UPDATE pbl_procediment SET valor_camp_clasetramite = '23' WHERE valor_camp_clasetramite = 6;

UPDATE pbl_procediment SET valor_camp_clasetramite = '24' WHERE valor_camp_clasetramite = 7;

UPDATE pbl_procediment SET valor_camp_clasetramite = '25' WHERE valor_camp_clasetramite = 8;

UPDATE pbl_procediment SET valor_camp_clasetramite = '26' WHERE valor_camp_clasetramite = 9;

UPDATE pbl_procediment SET valor_camp_clasetramite = '27' WHERE valor_camp_clasetramite = 10;

UPDATE pbl_procediment SET valor_camp_clasetramite = '28' WHERE valor_camp_clasetramite = 11;

UPDATE pbl_procediment SET valor_camp_clasetramite = '29' WHERE valor_camp_clasetramite = 12;

UPDATE pbl_procediment SET valor_camp_clasetramite = '30' WHERE valor_camp_clasetramite = 14;

UPDATE pbl_procediment SET valor_camp_clasetramite = '31' WHERE valor_camp_clasetramite = 15;

UPDATE pbl_procediment SET valor_camp_clasetramite = '32' WHERE valor_camp_clasetramite = 16;

UPDATE pbl_procediment SET valor_camp_clasetramite = '33' WHERE valor_camp_clasetramite = 18;

UPDATE pbl_procediment SET valor_camp_clasetramite = '34' WHERE valor_camp_clasetramite = 1;

UPDATE pbl_procediment SET valor_camp_clasetramite = '2' WHERE valor_camp_clasetramite = 13;

UPDATE pbl_procediment SET valor_camp_clasetramite = '3' WHERE valor_camp_clasetramite = 0;

UPDATE pbl_procediment SET valor_camp_clasetramite = '14' WHERE valor_camp_clasetramite = 17;

-- Changeset db/changelog/changes/1.4.35/243.yaml::1663840075438-2::limit
ALTER TABLE pbl_servei_config ADD use_auto_classe NUMBER(1) DEFAULT '1';

