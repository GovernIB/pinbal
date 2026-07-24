-- Redirigeix cap al servidor pinbal-scsp-fake les URLs (sincrona/asincrona)
-- dels serveis SCSP dels quals el fake té exemples de resposta carregats
-- (vegeu pinbal-scsp-fake/src/main/resources/xml_peticions.txt).
--
-- Idempotent: es pot executar diverses vegades seguides sense perdre les
-- URLs originals (només es fa una còpia de seguretat la primera vegada).
--
-- Ús amb sqlplus:
--   sqlplus usuari/contrasenya@//host:port/servei @point-to-fake.sql 'http://host.docker.internal:18080'
--
-- El primer paràmetre és la base de la URL del fake (sense barra final).
-- Normalment invocat des de scripts/e2e/run-e2e.sh amb --point-scsp-urls.

DEFINE fake_base = '&1'

WHENEVER SQLERROR CONTINUE
BEGIN
    EXECUTE IMMEDIATE '
        CREATE TABLE core_servicio_url_backup (
            id NUMBER PRIMARY KEY,
            codcertificado VARCHAR2(100),
            urlsincrona VARCHAR2(500),
            urlasincrona VARCHAR2(500)
        )';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -955 THEN -- ORA-00955: object already exists
            RAISE;
        END IF;
END;
/
WHENEVER SQLERROR EXIT SQL.SQLCODE

INSERT INTO core_servicio_url_backup (id, codcertificado, urlsincrona, urlasincrona)
SELECT s.id, s.codcertificado, s.urlsincrona, s.urlasincrona
FROM core_servicio s
WHERE s.codcertificado IN ('Q2827003ATGSS001', 'SCDCPAJU', 'SVDDGTVEHICULOSANCWS01', 'SVDDGPCIWS02')
  AND NOT EXISTS (SELECT 1 FROM core_servicio_url_backup b WHERE b.id = s.id);

UPDATE core_servicio
SET urlsincrona = '&fake_base' || '/servicios/' || codcertificado || '/sincrona',
    urlasincrona = '&fake_base' || '/servicios/' || codcertificado || '/asincrona'
WHERE codcertificado IN ('Q2827003ATGSS001', 'SCDCPAJU', 'SVDDGTVEHICULOSANCWS01', 'SVDDGPCIWS02');

COMMIT;

PROMPT Serveis SCSP redirigits cap al fake:
SELECT codcertificado, urlsincrona, urlasincrona FROM core_servicio
WHERE codcertificado IN ('Q2827003ATGSS001', 'SCDCPAJU', 'SVDDGTVEHICULOSANCWS01', 'SVDDGPCIWS02');

EXIT
