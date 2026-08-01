# Oracle Free per a scripts/e2e/run-e2e.sh --db=oracle

Contenidor efímer d'Oracle Database Free (imatge
`docker.io/gvenzl/oracle-free:23-slim-faststart`) fet servir com a
alternativa a H2 per executar la suite Playwright de e2e/ contra un motor de
BD més proper al de producció (Oracle).

## Per què aquesta imatge

- **Pública**: no cal cap `docker login` ni compte, a diferència de la imatge
  oficial `container-registry.oracle.com/database/free`. Millor per a
  companys/CI que no tenen credencials Oracle configurades.
- **Més lleugera**: ~5 GB enfront dels ~10 GB de la imatge oficial `:latest`.
- **"faststart"**: porta una base de dades ja pre-creada dins seu (com la
  imatge oficial), de manera que fins i tot la primera vegada que es crea el
  volum arrenca "healthy" en segons — verificat empíricament, no una
  estimació.
- Mateix motor real (Oracle Database Free 23ai) i mateixa PDB per defecte
  (`FREEPDB1`) que fa servir habitualment l'entorn de desenvolupament local
  (vegeu `DB_URL` a `.env`): els dos només difereixen en com empaqueten la
  imatge, no en el comportament SQL.

## Creació de l'usuari `pinbal2`

No cal cap script d'inicialització propi: `APP_USER`/`APP_USER_PASSWORD`
(vegeu `docker-compose.e2e-oracle.yml`) és el mecanisme oficial d'aquesta
imatge per crear l'esquema d'aplicació, amb el rol modern
`DB_DEVELOPER_ROLE` + quota il·limitada al tablespace `USERS`. Verificat
manualment que és suficient per a tot el que fa Liquibase aquí: `CREATE
TABLE`/`SEQUENCE`/`TRIGGER`/`INDEX`/`VIEW` hi funcionen sense privilegis
addicionals.

La contrasenya és fixa i sense cap valor real (`E2eOraclePwd_1`): aquesta
base de dades només viu dins el contenidor efímer d'aquest overlay, mai
exposada fora de la xarxa de docker compose de l'entorn local (mateix esperit
que les credencials `sa`/`sa` de l'overlay H2, `docker-compose.e2e.yml`).

## Sense volum persistent (a propòsit)

A diferència d'una primera versió d'aquest overlay, el contenidor `oracle`
NO munta cap volum a `/opt/oracle/oradata`. Motiu, verificat empíricament:
aquesta imatge/tag no sap represendre el servei si es RECREA el contenidor
(encara que sigui un contenidor nou net) reutilitzant les dades d'un volum
d'un contenidor anterior — falla sempre amb `ORA-01078: failure in
processing system parameters / could not open parameter file
.../dbs/initFREE.ora`, tant si es recrea via aquest mateix overlay
(`--force-recreate`) com amb `podman run` directe. Només un *restart* del
MATEIX contenidor (sense recrear-lo, `podman/docker restart`) funciona amb
un volum.

Com que arrencar de zero és igual de ràpid (uns segons, BD pre-creada dins
la imatge — vegeu amunt), no calia perseguir la persistència entre
contenidors: cada contenidor nou parteix sempre d'un estat net, igual que
H2. `scripts/e2e/run-e2e.sh --down` + una nova execució sempre donen una BD
Oracle completament nova.

## Diferències respecte a H2

Les taules `SC_WL_*` (sistema extern "Seycon", consultat via
`usuarisDS`/`JBOSS_USER_DB_URL`) NO es creen ni aquí ni a l'overlay H2: cap
test e2e actual n'exercita el codi que les consulta.
