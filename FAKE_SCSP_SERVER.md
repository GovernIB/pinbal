# Fake SCSP local

Aquest mòdul crea un mini servidor HTTP local que exposa un `catch-all` sota `/servicios/*` i retorna els XML SOAP reals del fitxer [xml_peticions.txt](/Users/sion/git/pinbal/pinbal-scsp-fake/src/main/resources/xml_peticions.txt).

No substitueix `ClienteUnico` ni usa els mocks interns de Pinbal. La idea és apuntar els endpoints SCSP cap a aquest servidor i provar l'aplicació en funcionament.

## Què fa

- Accepta `POST` SOAP a qualsevol ruta, incloses rutes tipus `/servicios/...`
- Distingeix `Peticion` síncrona, `Peticion` asíncrona i `SolicitudRespuesta`
- Selecciona la resposta segons `CodigoCertificado` i tipus d'operació
- Carrega els exemples reals per a:
  - `Q2827003ATGSS001`
  - `SCDCPAJU`
  - `SVDDGTVEHICULOSANCWS01`
  - `SVDDGPCIWS02`

## Arrencada

Compilar:

```bash
mvn -pl pinbal-scsp-fake -am package -DskipTests=false
```

Executar:

```bash
java -jar pinbal-scsp-fake/target/pinbal-scsp-fake-1.4.44.jar
```

Per defecte escolta a `http://0.0.0.0:18080`.

Pots canviar host/port amb:

```bash
java -Dfake.scsp.host=127.0.0.1 -Dfake.scsp.port=8089 -jar pinbal-scsp-fake/target/pinbal-scsp-fake-1.4.44.jar
```

## Verificació

Estat del servidor:

```bash
curl http://127.0.0.1:18080/__fake-scsp
```

## Simular una resposta d'error

Independentment del servei consultat, si el document del titular de la
petició (`Titular/Documentacion`, en qualsevol dels dos formats que fan
servir els serveis coberts) coincideix amb `00000000ERR`, el fake sobreescriu
`Atributos/Estado/CodigoEstado` amb `9999` (que no comença per `00`), que és
el codi que Pinbal interpreta com a consulta en estat "Error". Útil per
provar el camí d'error sense dependre de dades reals. Es pot canviar el
document disparador amb `-Dfake.scsp.errorTriggerDoc=...`.

## Integració amb Pinbal

Has de fer que els endpoints SCSP que avui apunten a RedSARA apuntin al host i port del fake, mantenint el path `/servicios/...` que ja tenguis configurat.

Exemples:

- abans: `https://intermediacion.redsara.es/servicios/...`
- després: `http://127.0.0.1:18080/servicios/...` o `http://host.docker.internal:18080/servicios/...` si s'executa Pinbal amb docker compose

Si vols conservar literalment el host original, pots fer-ho amb `hosts` o amb un reverse proxy local.

Les URLs sincrona/asincrona de cada servei es guarden a la BD
(`core_servicio.urlsincrona`/`urlasincrona`), no en cap propietat global.
`scripts/scsp-fake/point-to-fake.sql` redirigeix cap al fake els quatre
serveis dels quals té exemples carregats (`Q2827003ATGSS001`, `SCDCPAJU`,
`SVDDGTVEHICULOSANCWS01`, `SVDDGPCIWS02`), fent primer una còpia de
seguretat de les URLs originals; `scripts/scsp-fake/restore-original-urls.sql`
les restaura. Tots dos són idempotents i pensats per executar-se amb
`sqlplus` (vegeu la capçalera de cada fitxer per als paràmetres).

## Arrencada automatitzada (app + fake + tests e2e)

`scripts/e2e/run-e2e.sh`, a l'arrel del repositori, encadena tot el cicle:
aixeca Pinbal amb `docker compose`/`docker-compose`/`podman-compose`,
compila i arrenca aquest fake, opcionalment executa els scripts SQL
anteriors (`--point-scsp-urls`) i executa la suite Playwright de
`pinbal-back/src/main/reactapp/pinbal-back/e2e/`. Vegeu
`scripts/e2e/run-e2e.sh --help` i el `e2e/README.md` del mòdul React.

## Limitacions

- Retorna XMLs reals però estàtics; no recalcula signatures ni xifrats
- No genera justificants PDF
- No implementa WSDL dinàmic ni validació d'esquemes
- Si el client depèn que `IdPeticion` o altres valors canviïn a cada resposta, hauràs d'afegir una capa de templating i assumir que la signatura del XML ja no coincidirà
