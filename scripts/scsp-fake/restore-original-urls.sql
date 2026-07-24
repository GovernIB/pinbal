-- Restaura les URLs SCSP originals guardades per point-to-fake.sql i
-- elimina la taula de còpia de seguretat.
--
-- Ús amb sqlplus:
--   sqlplus usuari/contrasenya@//host:port/servei @restore-original-urls.sql
--
-- Si no existeix cap còpia de seguretat (mai s'ha executat point-to-fake.sql,
-- o ja s'havia restaurat), no fa res.

WHENEVER SQLERROR CONTINUE
SET SERVEROUTPUT ON

DECLARE
    table_exists NUMBER;
BEGIN
    SELECT COUNT(*) INTO table_exists FROM user_tables WHERE table_name = 'CORE_SERVICIO_URL_BACKUP';
    IF table_exists = 0 THEN
        DBMS_OUTPUT.PUT_LINE('No hi ha cap còpia de seguretat de URLs SCSP (core_servicio_url_backup); res a restaurar.');
        RETURN;
    END IF;

    MERGE INTO core_servicio s
    USING core_servicio_url_backup b
    ON (s.id = b.id)
    WHEN MATCHED THEN UPDATE SET
        s.urlsincrona = b.urlsincrona,
        s.urlasincrona = b.urlasincrona;

    EXECUTE IMMEDIATE 'DROP TABLE core_servicio_url_backup';
    COMMIT;
    DBMS_OUTPUT.PUT_LINE('URLs SCSP originals restaurades.');
END;
/

PROMPT Estat actual dels serveis coberts pel fake:
SELECT codcertificado, urlsincrona, urlasincrona FROM core_servicio
WHERE codcertificado IN ('Q2827003ATGSS001', 'SCDCPAJU', 'SVDDGTVEHICULOSANCWS01', 'SVDDGPCIWS02');

EXIT
