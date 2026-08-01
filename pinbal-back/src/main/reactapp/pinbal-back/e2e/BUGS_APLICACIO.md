# Bugs de l'aplicació detectats via e2e (per revisar)

Aquest document recull problemes trobats en executar la suite Playwright
contra un entorn real (H2 + Keycloak extern) que **no són bugs dels tests**,
sinó comportament reproduïble de l'aplicació que caldria revisar.

## FIXAT — Descàrrega de justificants (simple i múltiple) des de "Consultes realitzades": stack trace cru en lloc de missatge d'error net

**On:** `ConsultaAdminController` — `getJustificantMultiplePdf()`, `getJustificantMultipleZip()`, `getJustificant()`, `getConsultaAdmin()`, `getConsultesFilles()`.

**Causa arrel confirmada:** aquests 5 mètodes privats fan `try { servei actiu } catch (Exception nfe) { servei històric }`, pensat per redirigir la cerca a l'històric quan la consulta no es troba a la taula activa. Però el `catch` era massa ampli: capturava **qualsevol** excepció (incloent `ScspException` real, p.ex. "No existe el mensaje de tipo 3, para el idpeticion ...") i la substituïa per una crida al servei històric, que al seu torn llançava `ConsultaNotFoundException` (la consulta és activa, no històrica) — excepció que **no es capturava enlloc** i acabava mostrant a l'usuari la pàgina d'error crua amb l'stack trace complet, en lloc del missatge net "Error al generar el justificant".

**Fix aplicat:** els 5 `catch (Exception nfe)` ara són `catch (ConsultaNotFoundException nfe)`, deixant passar qualsevol altra excepció cap al `catch` net del controlador.

**Verificat:** l'`ScspException` real (vegeu entrada següent) ara es mostra com a "Error al generar el justificant" en lloc de l'stack trace cru.

## FIXAT — Dades de mostra e2e: faltava el missatge SCSP de resposta (tipus 3) per a les consultes seed

**On:** `pinbal-persistence/.../db/changelog/e2e/01_e2e_seed_serveis.yaml`, taula `core_token_data`.

**Causa arrel:** les consultes de mostra (`PBL_E2E_SIMPLE_OK`, `PBL_E2E_SIMPLE_JUSTERR`, `PBL_E2E_MULTIPLE_01`) només sembraven el missatge `tipomensaje: 0` (Petición). En una consulta SCSP real, l'aplicació també genera i desa el missatge `tipomensaje: 3` (Respuesta) quan arriba la resposta — sense ell, `ClienteUnico.recuperaRespuesta()` no pot generar el justificant.

**Fix aplicat:** afegit un `insert` de `core_token_data` amb `tipomensaje: 3` per a cadascuna de les 3 consultes. Detalls no evidents del format (determinats per prova/error contra `scsp-core`/`scsp-beans` 5.0.11 descompilats):
- El missatge de tipus 3 (a diferència del tipus 0) ha d'anar embolcallat en un SOAP `Envelope/Body` (`MessageRecover.recoverMessage` el cerca explícitament per `local-name()`); el tipus 0 no en porta.
- El namespace de `<Respuesta>` és `http://intermediacion.redsara.es/scsp/esquemas/V3/respuesta` (`@XmlSchema` de `es.scsp.bean.common.respuesta` a `scsp-beans:5.0.11`) — **no** `http://www.map.es/scsp/esquemas/V2/respuesta`, que és el namespace antic (V2) que porta l'únic fixture de test disponible al repo (`pinbal-scsp/src/test/resources/.../resposta-justificant-pdf.xml`), obsolet des de l'actualització de llibreries SCSP a 5.11.
- `DatosEspecificos` és contingut lliure/genèric (`JustificantArbreHelperTest` ho confirma: `<DatosEspecificos><Ejercicio>2024</Ejercicio></DatosEspecificos>`, sense namespace) — **no** cal simular-hi un PDF incrustat.
- Ull amb la mida del literal SQL: Oracle limita els literals de cadena a 4000 bytes; amb dues `TransmisionDatos` (cas múltiple) cal vigilar el contingut de `DatosEspecificos` perquè no el superi.

**Verificat:** amb el fix, `ClienteUnico.recuperaRespuesta()` ja recupera i desxifra correctament el missatge, i `JustificantArbreHelper` genera l'arbre del justificant sense error (abans: `ScspException: No existe el mensaje de tipo 3...`; després d'un primer intent de format incorrecte: `No existe un nodo Respuesta...`; després: `No se pudo recuperar el mensaje` (JAXB, namespace incorrecte); amb el fix final: cap error d'SCSP/JAXB).

**Dues causes NOVES i INDEPENDENTS trobades en desbloquejar aquest camí:**

1. **FIXAT — Consulta múltiple:** `JustificantHelper.generar()` crida
   `ConversioTipusDocumentHelper.convertAmbServeiOpenOffice()`, que necessita
   un servei extern OpenOffice/LibreOffice de conversió de documents
   (`es.caib.pinbal.conversio.open.office.host`/`.port`), no configurat en
   aquest entorn e2e → `Integer.parseInt(null)` → `NumberFormatException`.
   **Fix:** `ConversioTipusDocumentHelper` ja suporta una via alternativa
   100% Java sense procés extern, via XDocReport/`odfdom-converter-pdf`
   (`fr.opensagres.odfdom.converter.pdf.PdfConverter`, ja dependència del
   projecte), seleccionable amb la propietat
   `es.caib.pinbal.conversio.tipus=xdocreport`
   (`ConversioTipusDocumentHelper.isConversioTipusXdocreport()`). Com que el
   justificant es genera sempre a partir d'una plantilla ODT
   (`JustificantHelper.PLANTILLA_ODT_RESOURCE`) i XDocReport només cobreix
   ODT→PDF, encaixa exactament amb aquest ús. Activat a
   `docker-compose.e2e.yml`/`docker-compose.e2e-oracle.yml` (només a
   l'entorn e2e; el comportament per defecte fora d'aquests overlays no
   canvia). **Verificat:** `consultes-realitzades.spec.ts` → "Descàrrega
   del justificant (consulta múltiple...)" passa (descàrrega real d'un PDF).

2. **FIXAT — Consulta simple, pas 1/3 (autorització a firmar):** la generació
   del justificant intenta signar amb una clau privada associada a
   l'entitat/servei (`HandlerAliasSelector.getSignatureClavePrivada`), i
   fallava amb `ScspException: Organismo no autorizado P0700000A ,al
   servicio Q2827003ATGSS001`. **Fix:** afegit el changeSet
   `e2e-seed-clau-privada-firma` a `01_e2e_seed_serveis.yaml`, que sembra una
   fila a `core_clave_privada` (alias `limit_pinbal`, apuntant a una entrada
   real del keystore `interoperabilitat.jks` muntat a l'entorn, vàlida fins
   2036) i una fila a `core_req_cesionarios_servicios` que autoritza
   l'organisme cessionari de mostra (`P0700000A`, id 900011) a signar
   peticions pel servei `Q2827003ATGSS001` (id 900701). **Verificat:**
   l'error "Organismo no autorizado" ja no apareix als logs del servidor.

3. **FIXAT — Consulta simple, pas 2/3 (firma en servidor):** un cop
   autoritzada la clau, `JustificantHelper.generarIFirmarJustificant()`
   crida `PluginHelper.firmaServidorFirmar()`, que necessita una classe
   configurada a `es.caib.pinbal.plugin.firmaservidor.class` — sense cap
   valor, `SistemaExternException: No està configurada la classe...`. La
   implementació real (`FirmaSimpleServidorPluginPortafib`) truca el
   portafirmes PortaFIB, un servei extern no disponible en aquest entorn
   e2e. **Fix:** nova classe
   `pinbal-plugin/.../plugin/firmaservidor/FirmaServidorPluginMock.java`
   (implementa `FirmaServidorPlugin`, retorna el mateix contingut rebut com
   si ja estigués firmat, sense contactar cap servei extern), activada via
   `es.caib.pinbal.plugin.firmaservidor.class=es.caib.pinbal.plugin.firmaservidor.FirmaServidorPluginMock`
   a ambdós overlays e2e. **Verificat:** l'error deixa d'aparèixer; el flux
   avança fins al desat a l'arxiu digital.

4. **FIXAT — Consulta simple, pas 3/3 (arxiu digital), més un bug real de
   l'aplicació trobat i corregit pel camí:** un cop signat,
   `JustificantHelper.desarJustificantArxiu()` desa l'expedient/document a
   l'arxiu digital via `PluginHelper.getArxiuPlugin()`, que necessita una
   classe a `es.caib.pinbal.plugin.arxiu.class` — sense cap valor,
   `SistemaExternException: No està configurada la classe per al plugin
   d'arxiu digital`.

   **Bug real trobat en investigar-ho:** `PluginHelper` (a `pinbal-service`)
   fa `import es.caib.pluginsib.arxiu.api.*` i declara el camp
   `IArxiuPlugin arxiuPlugin` amb aquest tipus — és a dir, espera la
   interfície de la versió **3.0.0-SNAPSHOT** de `pluginsib-arxiu-api`
   (paquet `es.caib.pluginsib.arxiu.api`). Però la implementació real,
   `ArxiuPluginCaib` (a `pinbal-plugin`, truca el servei ConCSV/arxiu digital
   del CAIB), encara implementava la interfície de la versió **2.0.27**
   (paquet **diferent** `es.caib.plugins.arxiu.api`). És a dir, si
   `es.caib.pinbal.plugin.arxiu.class` s'hagués arribat a configurar amb
   `ArxiuPluginCaib` en qualsevol entorn (dev o real), el
   `(IArxiuPlugin) clazz.newInstance(...)` de `PluginHelper.getArxiuPlugin()`
   hauria llançat `ClassCastException` en temps d'execució — **el plugin
   d'arxiu digital real estava trencat**, no només "no configurat". Coherent
   amb el context d'aquesta migració JBoss EAP5/Java7 → EAP7/Java11 (moltes
   llibreries actualitzades): sembla una migració de `pluginsib-arxiu-api` a
   v3 deixada a mitges (`pinbal-service-intf` ja apuntava a
   `3.0.0-SNAPSHOT`, `pinbal-plugin` es va quedar a `2.0.27`).

   **Fix aplicat (migració completada, no revertida):** existeix una versió
   `3.0.0-SNAPSHOT` de `pluginsib-arxiu-caib` (el mateix `ArxiuPluginCaib`
   base, reempaquetat al nou paquet `es.caib.pluginsib.arxiu.*`), resoluble
   des del repositori Maven ja configurat al projecte
   (`https://governib.github.io/maven/maven/`, perfil `repositorigovernib` a
   `settings.xml`). S'ha:
   - Pujat la propietat `arxiu.api.version` (arrel `pom.xml`) de `2.0.27` a
     `3.0.0-SNAPSHOT` — afecta `pinbal-plugin-intf` (`pluginsib-arxiu-api`) i
     `pinbal-plugin` (`pluginsib-arxiu-caib`).
   - Actualitzat `pinbal-plugin-intf/.../plugin/arxiu/ArxiuPlugin.java` i
     `pinbal-plugin/.../plugin/arxiu/ArxiuPluginCaib.java` (+ el seu test)
     perquè importin `es.caib.pluginsib.arxiu.api.*` (v3) en lloc
     d'`es.caib.plugins.arxiu.api.*` (v2), i que `ArxiuPluginCaib` estengui
     `es.caib.pluginsib.arxiu.caib.ArxiuPluginCaib` (v3) en lloc
     d'`es.caib.plugins.arxiu.caib.ArxiuPluginCaib` (v2). Els dos paquets són
     estructuralment idèntics (mateixos camps/mètodes als DTOs i enums,
     mateixa firma dels mètodes NTI `toEnum(...)`) — només canvia el nom del
     paquet — així que la migració ha estat purament d'imports, sense
     canvis de lògica. **Verificat:** els 17 tests existents
     d'`ArxiuPluginCaibTest` (mockejant el servei ConCSV amb un
     `HttpServer` local) passen sense cap canvi.
   - Simplificat `pinbal-service-intf/pom.xml`: l'override manual de
     `pluginsib-arxiu-api` a `3.0.0-SNAPSHOT` (que ja hi era, d'una migració
     parcial anterior) ara usa `${arxiu.api.version}`, coincidint amb la
     resta de mòduls.

   Amb tots els mòduls ja a la mateixa versió, el mock parcial
   (`ArxiuPluginMock`, nova classe a
   `pinbal-plugin/.../plugin/arxiu/ArxiuPluginMock.java`, junt amb
   `ArxiuPluginCaib` i `FirmaServidorPluginMock` com correspon) implementa
   directament `ArxiuPlugin`/`IArxiuPlugin` (v3) sense cap conflicte de
   versions. Només tenen lògica real (mapa en memòria) els mètodes
   exercitats pel flux de desat de justificants: `expedientCrear`,
   `expedientConsulta` (cerca d'un expedient existent pel nom),
   `documentCrear`, `documentDetalls` i `documentImprimible`; la resta de la
   interfície (~30 mètodes: versions, tancament/reobertura d'expedients,
   carpetes, CSV, URLs de validació...) no s'usa en aquest flux i queda com
   a stub (`UnsupportedOperationException` o valor per defecte segur).
   Activada via
   `es.caib.pinbal.plugin.arxiu.class=es.caib.pinbal.plugin.arxiu.ArxiuPluginMock`
   a ambdós overlays e2e.

   **Verificat:** `mvn -pl pinbal-plugin -am test` (157 tests, inclosos els
   17 d'`ArxiuPluginCaibTest`) passa net amb cobertura JaCoCo complerta;
   descàrrega completa del justificant d'una consulta simple
   (`PBL_E2E_SIMPLE_OK`, id 901401) via `/admin/consulta/{id}/justificant`:
   `200 application/octet-stream`, tant la primera descàrrega (genera+firma+
   desa a l'arxiu) com descàrregues posteriors dins la mateixa vida del
   desplegament (recupera de l'arxiu mock en memòria).

**Nota operativa important (no és un bug, és un parany de l'entorn):** els
changeSets `e2e-seed-consultes-realitzades`/`-llistat` (que sembren les
consultes de mostra 901401 etc.) tenen una precondició
`not: changeLogPropertyDefined(e2eDelegatUsername, '')`. Si s'aixeca l'stack
amb `podman-compose ... up -d` directament (sense passar per
`scripts/e2e/run-e2e.sh`) en una shell on `E2E_ADMIN_USERNAME`/
`E2E_DELEGAT_USERNAME`/`E2E_REPRESENTANT_USERNAME`/`E2E_AUDITOR_USERNAME` no
estan exportades, aquestes variables arriben buides a
`SPRING_LIQUIBASE_PARAMETERS_E2E*USERNAME` i Liquibase salta silenciosament
aquests changeSets (`Marking ChangeSet ... ran despite precondition
failure`) — **sense cap error visible**, la consulta de mostra simplement no
existeix mai, i qualsevol prova contra `/admin/consulta/901401/...` falla
amb `ConsultaNotFoundException`. Cal exportar-les (valors a
`e2e/.env.e2e`: `pbl_admin`/`pbl_deleg`/`pbl_repres`/`pbl_audit`) abans de
`podman-compose up -d` quan no s'usa `run-e2e.sh`.

**Nota operativa 2:** `podman-compose up -d` NO recrea automàticament el
contenidor `pinbal` només perquè la imatge `docker.io/goib/pinbal:latest`
s'hagi reconstruït amb contingut nou (mateix tag) — cal `podman rm -f
pinbal2_pinbal_1` (o `--force-recreate`) abans de tornar a fer `up -d`
perquè el contenidor arrenqui realment amb la imatge nova.

## FIXAT — Tancar sessió ("Desconnectar") trencava el següent login amb "Bad Request"

**On:** `UsuariController.logout()` (`GET /usuari/logout`).

**Comportament anterior:** el mètode no feia un logout real contra
Keycloak (no cridava `request.logout()`); en lloc d'això, iterava totes
les cookies de la petició i les buidava manualment, sense notificar mai
l'adaptador de Keycloak de JBoss. **Confirmat als logs** d'aquella època,
just després de tornar a iniciar sessió després d'un "Desconnectar":

```
WARN [org.keycloak.adapters.OAuthRequestAuthenticator] state parameter invalid
WARN [org.keycloak.adapters.OAuthRequestAuthenticator] cookie:
WARN [org.keycloak.adapters.OAuthRequestAuthenticator] queryParam: <uuid>
```

**Causa arrel confirmada (en dues capes):**
1. Sense `request.logout()`, la sessió local de l'adaptador Keycloak mai
   s'invalidava correctament.
2. Fins i tot afegint `request.logout()` (local, a l'adaptador), Keycloak
   MANTÉ la sessió SSO pròpia (la seva pròpia cookie, independent de la de
   l'app): la següent visita a l'app hi torna a entrar silenciosament amb
   l'usuari anterior (sense mostrar mai el formulari de login), i com que
   això dispara un intercanvi OAuth nou "de sorpresa" mentre l'anterior
   encara no havia acabat de netejar el seu propi "state", Keycloak
   rebutjava el "callback" amb "state parameter invalid" → "Bad Request".
   La solució real requereix tancar també la sessió a Keycloak mateix (RP-
   initiated logout / `end_session_endpoint`), no només la sessió local.

**Fix aplicat:**
- `UsuariController.logout()` ara distingeix JBoss (`jboss.home.dir`
  present) de Spring Boot:
  - **Spring Boot:** delega en el `/logout` de Spring Security
    (`WebSecurityConfig`), que ja fa el flux OIDC complet via
    `OidcClientInitiatedLogoutSuccessHandler` — sense canvis, ja
    funcionava.
  - **JBoss:** crida `request.logout()` (tanca la sessió local a
    l'adaptador) i, a més, redirigeix explícitament l'"end_session_endpoint"
    de Keycloak (`{authUrl}/realms/{realm}/protocol/openid-connect/logout`)
    amb `post_logout_redirect_uri` i `id_token_hint` (l'ID token es llegeix
    de `KeycloakSecurityContext`, present com a atribut de la petició,
    ABANS de cridar `request.logout()`, que l'invalida). Sense
    `id_token_hint`, Keycloak >= 18 mostra una pàgina de confirmació
    ("Do you want to log out?") en lloc de tancar la sessió directament.
- Nova dependència `provided` a `pinbal-back/pom.xml`:
  `org.keycloak:keycloak-core` (versió gestionada pel
  `keycloak-adapter-bom` ja importat al POM arrel), només per al tipus
  `KeycloakSecurityContext`; ja disponible en temps d'execució via el
  mòdul `org.keycloak.keycloak-adapter-subsystem` de JBoss, no s'empaqueta
  al WAR.
- Noves propietats `es.caib.pinbal.auth.url`/`.realm`/`.clientid` a
  `docker-compose.yml` (secció "PROPIETATS", valors `${AUTH_URL}`/
  `${AUTH_REALM}`/`${AUTH_CLIENTID}` ja presents a `.env`), mateix patró ja
  usat per desenes d'altres propietats `es.caib.pinbal.*` d'aquest fitxer
  (p.ex. `es.caib.pinbal.recobriment.base.url`) — **no** a
  `standalone-openshift.xml` (es va descartar aquesta opció: aquest fitxer
  és una plantilla genèrica reutilitzable entre apps CAIB i no ha de portar
  propietats específiques de PINBAL), perquè el controlador pugui construir
  la URL de l'"end session endpoint".

**Verificat (primera volta):** rebuild+redeploy complet contra JBoss (EAP
7.2 + adaptador Keycloak) + `e2e/tests/auth.spec.ts` sencer (4/4 tests,
incloent "un usuari pot tancar sessió i torna a la pantalla de login")
passa. També verificat que `usuari-configuracio.spec.ts` test 1 (403
fixat més amunt) segueix passant.

**Segona volta — el fix anterior era INCOMPLET:** `auth.spec.ts` només
comprovava que després de `logout()` es tornava a mostrar el formulari de
login, mai que un login POSTERIOR (logout seguit de login immediat, com
fa `gestionar-entitats.spec.ts` per forçar que el delegat vegi canvis
d'accés a entitats sense obrir un browser context nou) funcionés. Amb
aquest escenari real, el "Bad Request" seguia reproduint-se (URL final
`/pinbalback/?state=...&session_state=...&code=...`, log de JBoss amb
`state parameter invalid` / `cookie: ` buit / `queryParam: <uuid>`).

**Causa arrel confirmada (inspeccionant cookies del navegador amb un test
de diagnòstic):** `UsuariController.logout()` conservava un bucle que
iterava totes les cookies de la petició i en reenviava una còpia amb
valor buit i `path=request.getContextPath()` ("/pinbalback"), suposadament
per "esborrar-les". Però `request.getCookies()` no exposa el path/domain
amb què cada cookie es va crear originalment (només el navegador ho sap),
i la cookie `OAuth_Token_Request_State` que l'adaptador de Keycloak
gestiona ell mateix es crea a path "/" (arrel), no a "/pinbalback". El
bucle, doncs, no l'esborrava: creava una SEGONA cookie fantasma amb el
mateix nom i valor buit a "/pinbalback", que convivia amb la real.
Confirmat amb `context.cookies()` de Playwright just després d'un logout:
dues cookies `OAuth_Token_Request_State` simultànies (`path=/pinbalback`,
valor buit; `path=/`, valor real). Al login immediatament posterior,
l'adaptador de Keycloak creava una NOVA cookia real a "/" amb el nou
"state", però en llegir-la de tornada al callback rebia totes dues
cookies del mateix nom (el navegador les envia juntes quan els paths
coincideixen com a prefix) i llegia la fantasma buida en lloc de la real
→ "state parameter invalid".

**Fix aplicat:** eliminat completament el bucle de reenviament de
cookies. En el seu lloc, `request.getSession(false)` + `session.
invalidate()` (guardat amb `try/catch(IllegalStateException)` per si
`request.logout()` ja l'ha invalidada) — suficient per forçar un
`JSESSIONID` nou al pròxim login; el tancament de la sessió SSO de
Keycloak ja el fa el redirect a l'"end session endpoint" que hi ha a
continuació. El paràmetre `HttpServletResponse response`, ja no usat, s'ha
tret de la signatura del mètode.

**Verificat:** test de diagnòstic amb `context.cookies()` confirma una
única cookie `OAuth_Token_Request_State` (path "/") després d'un cicle
logout+login; `UsuariControllerTest` (17 tests, `pinbal-back` sencer: 756
tests) actualitzat i en verd; `e2e/tests/auth.spec.ts` (4/4) i
`gestionar-entitats.spec.ts` sencer (6/6, incloent el test que va detectar
el problema) passen contra un desplegament real.

**Bugs de test relacionats, trobats i corregits pel camí** (a
`gestionar-entitats.spec.ts`, un cop el login ja no petava i el test va
poder arribar més lluny):
- `#menu_entitat + ul.dropdown-menu` és un dropdown de Bootstrap
  (`display:none` fins que es clica el toggle): el test comprovava la
  visibilitat d'un enllaç de dins sense obrir-lo primer. Fix: clic a
  `#menu_entitat` abans de l'asserció.
- Després del pas "vincula el delegat a l'entitat" (que navega
  `adminPage` a la subpàgina "Usuaris" de l'entitat), el test reutilitzava
  `filaE2EENT02` (una fila de `#table-entitats`, al llistat d'entitats)
  sense haver tornat abans al llistat — timeout perquè la taula no hi és
  a la subpàgina "Usuaris". Fix: `await entitats.goto()` abans de
  reprendre les accions sobre `filaE2EENT02`.

**Pendent, NO relacionat amb aquest bug (documentat més avall a la secció
pròpia sobre `usuari-configuracio.spec.ts`):** "es pot cancel·lar sense
desar canvis" encara falla per un problema de timing diferent (reobrir
`#menu_user` just després de tancar una modal 100% client-side).

## FIXAT — Representant: filtrar el llistat d'òrgans gestors per codi retornava 403

**On:** `POST /organgestor` (formulari de filtre de `organGestor.jsp`), rol Representant.

**Causa arrel confirmada:**
1. `organGestor.jsp` només renderitza el camp `#entitatId` del formulari de
   filtre dins `<c:if test="${isRolActualAdministrador}">` (línia 41) — el
   Representant mai veu ni envia aquest camp.
2. `OrganGestorController.post(...)`: si `command.getEntitatId() == null`
   (sempre el cas per al Representant), cridava incondicionalment
   `entitatService.findTopByTipus(EntitatTipusDto.GOVERN)` per obtenir una
   entitat per defecte.
3. `EntitatService.findTopByTipus(...)` porta
   `@PreAuthorize("hasRole('PBL_ADMIN')")` — **només Administrador**. El
   Representant rebia `AccessDeniedException` → 403 en lloc de filtrar el
   llistat.

**Fix aplicat:** `OrganGestorController.post(...)` ara només crida
`findTopByTipus` quan l'usuari és administrador
(`RolHelper.isRolActualAdministrador(request)`); en cas contrari usa
directament `EntitatHelper.getEntitatActual(request).getId()`, que ja és
l'única entitat a la qual el Representant té accés, evitant la crida
admin-only quan no cal.

**Verificat:** `e2e/tests/representant/organs-gestors.spec.ts` (2/2 tests,
incloent "el llistat es pot filtrar per codi i el filtre es pot netejar")
passa.

## DESCARTAT — Claus privades: "la data de baixa es mostra amb hores"

**Conclusió:** no era un bug de l'aplicació. El requisit real és que
`clauPrivadaList.jsp` mostri `dataAlta`/`dataBaixa` en format `DD/MM/YYYY`
(sense hora), que és exactament el comportament actual
(`moment(data).format('DD/MM/YYYY')`, targets [3,4] del `columnDefs`). El
test `e2e/tests/administrador/configurar-claus-privades.spec.ts` → "la data
de baixa es mostra amb hores" esperava incorrectament `HH:mm` al llistat;
s'ha eliminat (l'altre test del mateix fitxer ja cobreix el format
`DD/MM/YYYY` correcte per a `dataAlta` i `dataBaixa`).

## FIXAT — Sota H2, qualsevol intent de generar `idpeticion` "enverinava" el fil de JBoss i provocava fallades espúries en peticions NO relacionades

**Impacte (abans del fix):** aquesta era, amb tota probabilitat, la causa
arrel de bona part de les fallades "aleatòries"/no reproduïbles que
diversos agents van veure en aquesta sessió arreu de la suite (`#menu_user`
no visible després de login aparentment correcte, `NotFoundException`/
`NullPointerException` en mètodes que en altres moments funcionaven bé,
etc.), no només al flux de consulta múltiple.

**Fix aplicat:** `pinbal-scsp/src/main/java/es/scsp/common/core/IdGenerator.java`
(classe pròpia de PINBAL que sobreescriu la de la llibreria SCSP —
mecanisme `patch-scsp-local-repo.py`, no cal tocar cap jar de tercers) ara
embolica la crida a `secuenciaIdPeticionDao.next(prefix)` en un try/catch
que fa `sessionFactoryManager.getCurrentSession().getTransaction().rollback()`
explícitament abans de re-llançar `ScspException`, independentment de la
causa original de la fallada. **Verificat en calent**: després del fix,
cada intent fallit de generar `idpeticion` torna a començar net (log
"Function GETSECUENCIAIDPETICION not found" a l'inici de cada intent, no
ja "Transaction already active" heretat de l'anterior), i s'ha confirmat
que `es.scsp.common.dao.*` ja no apareix en cap traça d'un mètode NO
relacionat — el 403 de delegat a `/usuari/configuracio` documentat més
avall es manté igual (3/3 tests) abans i després d'aquest fix, confirmant
que aquell és un bug real i independent, no un símptoma d'aquest.

**Pendent (no corregit, i fora d'abast d'un fix de codi):** la causa
original que dispara aquest catch —el procediment Oracle
`GETSECUENCIAIDPETICION` no existeix sota H2— es manté; per tant, sota H2
la generació d'`idpeticion` (i, per extensió, la creació de qualsevol
consulta simple/múltiple real) **seguirà fallant sempre** amb un error net
i contingut (ja no enverina altres peticions). Vegeu l'entrada següent.

**Cadena completa (confirmada als logs en calent):**

1. `es.scsp.common.dao.SecuenciaIdPeticionDao.next(prefijo)` (llibreria
   `scsp-core:5.0.11`, usada per `ConsultaServiceImpl` per generar
   `idpeticion` en crear qualsevol consulta) executa
   `{ call GETSECUENCIAIDPETICION(?,?) }` via `CallableStatement` cru
   dins una transacció Hibernate manual (`session.beginTransaction()`).
2. Aquest procediment és un `CREATE OR REPLACE PROCEDURE` PL/SQL **només
   Oracle** (`db/changelog/init/05_initial_schema_trigger.yaml`, sense
   equivalent `dbms: h2`). Sota H2 la funció no existeix:
   ```
   ERROR [org.hibernate.engine.jdbc.spi.SqlExceptionHelper] Function "GETSECUENCIAIDPETICION" not found
   ERROR [es.scsp.common.dao.SecuenciaIdPeticionDao] Error generando idpeticion : org.hibernate.exception.GenericJDBCException
   ```
3. **Bug confirmat a la llibreria SCSP** (descompilat `SecuenciaIdPeticionDao.class`
   amb `javap`): el bloc `catch (Exception e)` d'aquest mètode NOMÉS fa
   `LOG.error(...)` i re-llança `ScspException` — **mai crida
   `transaction.rollback()`**. La transacció Hibernate (`current_session_context_class=thread`,
   vegeu `application-context-scsp.xml`) queda oberta i vinculada al fil
   de treball de JBoss que ha atès la petició.
4. Com que JBoss reutilitza fils del seu pool per peticions HTTP
   posteriors NO relacionades, la **següent petició atesa per aquell
   mateix fil** hereta la transacció "encallada": qualsevol altra crida a
   `es.scsp.common.dao.BaseDao` en aquell fil falla amb
   `java.lang.IllegalStateException: Transaction already active`, o —
   si en el mig el `Session`/`SessionFactory` queda en un estat
   inconsistent — amb una `NullPointerException` aparentment inexplicable
   com la vista a `findAmbCodiPerDelegat` (vegeu traça més avall). Això
   explica per què el MATEIX mètode de servei (`findAmbCodiPerDelegat`,
   cridat idènticament des de `GET /consulta/{codi}/new` i des de
   `GET /consulta/{codi}/plantilla/CSV`) unes vegades funciona i unes
   altres no: depèn de si el fil que atén la petició ha quedat "enverinat"
   per una generació d'`idpeticion` fallida anterior, no de cap problema
   de dades, ACL o injecció de dependències.

```
Caused by: java.lang.NullPointerException
	at es.scsp.common.dao.BaseDao.selectEquals(BaseDao.java:85)
	at es.scsp.common.dao.ServicioDao.select(ServicioDao.java:84)
	at es.caib.pinbal.scsp.ScspHelper.getServicioById(ScspHelper.java:456)
	at es.caib.pinbal.logic.service.ServeiServiceImpl.getServicioByCode(ServeiServiceImpl.java:2291)
	at es.caib.pinbal.logic.service.ServeiServiceImpl.findAmbCodiPerDelegat(ServeiServiceImpl.java:451)
...
ERROR [es.scsp.common.dao.BaseDao] Error basedao: java.lang.IllegalStateException: Transaction already active
```

**Per què és rellevant en producció (Oracle), no només en aquest entorn
e2e:** el bug de no fer `rollback()` a `SecuenciaIdPeticionDao.next(...)`
és de la llibreria SCSP i, per tant, existeix igual contra Oracle; sota
Oracle simplement gairebé mai es dispara perquè el procediment
`GETSECUENCIAIDPETICION` sí existeix i (llevat d'un error real de BD) no
falla. És a dir, si mai un entorn de producció tingués un problema
puntual de BD en aquest procediment concret (deadlock, taula bloquejada,
etc.), el mateix "enverinament de fil" es podria produir en calent i
manifestar-se com errors espuris NPE/"Transaction already active" en
peticions completament no relacionades fins que el fil es recicli.

**S'ha intentat un fix H2 (`CREATE ALIAS`) i s'ha descartat, confirmat
empíricament:** provat en aïllat (H2 1.4.193 embedded, `MODE=Oracle`) a
definir `GETSECUENCIAIDPETICION` com a `CREATE ALIAS ... FOR
"SecuenciaHelper.metode"` amb una classe Java pròpia. Resultat: H2 tracta
CADA `?` de `{ call NOM(?,?) }` com un argument IN real cap al mètode Java
(compta els dos signes d'interrogació com a 2 paràmetres reals, no com "1
IN + 1 OUT"), de manera que no hi ha cap forma de fer que
`registerOutParameter(2, ...)` + `getInt(2)` funcioni contra un `CREATE
ALIAS` d'H2 — aquesta versió d'H2 només suporta el patró de funció
`{ ? = call NOM(...) }` (valor de retorn assignat al primer `?`), no el
patró de procediment Oracle amb paràmetre `OUT` explícit que fa servir
`SecuenciaIdPeticionDao`. Com que el punt de crida viu dins el jar
`scsp-core:5.0.11` (no hi ha codi font al repositori per adaptar-lo), no
hi ha cap fix d'esquema/Liquibase viable per a aquest entorn H2; **calen
canvis al codi de PINBAL, no a les dades de prova.**

**Fix recomanat (codi de l'aplicació, no Liquibase):** que
`ConsultaServiceImpl`/`ConsultaMultipleServiceImpl` (o la pròpia
`SecuenciaIdPeticionDao` si es pot embolicar/decorar) capturin l'excepció
de la generació d'`idpeticion` i facin explícitament
`sessionFactory.getCurrentSession().getTransaction().rollback()` abans de
re-llançar, evitant que el fil de JBoss quedi enverinat. Això arregla el
problema independentment de si la causa original de la fallada és la
manca d'aquest procediment sota H2 (aquest entorn) o un problema real de
BD sota Oracle (producció).

**Test e2e afectat directament:**
`e2e/tests/delegat/noves-consultes.spec.ts` → "es pot realitzar una
consulta múltiple des del formulari" (falla intentant obtenir la plantilla
CSV). Probablement responsable, de rebot, d'altres fallades disperses i
no reproduïbles vistes per altres agents en aquesta sessió en àrees no
relacionades amb consultes; si es tornen a veure fallades amb
`IllegalStateException: Transaction already active` o NPE sense sentit
aparent a `es.scsp.common.dao.*`, la causa és aquesta, no un bug del test
ni de l'àrea funcional concreta.

## FIXAT — El delegat veia el botó "Exportar a Excel" al llistat de consultes (simples i múltiples), però no tenia permís per usar-lo

**On:** `consulta.jsp` (llistat de consultes simples) i `consultaMultiple.jsp`
(llistat de consultes múltiples), rol Delegat.

**Comportament anterior:** els controladors que serveixen aquestes rutes
(`ConsultaController`/`ConsultaMultipleController`, mètode `excel(...)`)
restringien l'accés explícitament a Administrador/Representant:

```java
if (!RolHelper.isRolActualAdministrador(request) && !EntitatHelper.isRepresentantEntitatActual(request))
    return "representantNoAutoritzat";
```

El Delegat veia el botó (el JSP el renderitza incondicionalment) però en
clicar-lo la petició tornava la vista `representantNoAutoritzat` en lloc
d'un Excel — sense cap descàrrega ni missatge d'error visible.

**Fix aplicat:** decisió de producte — el Delegat ha de poder exportar el
seu propi llistat. S'ha afegit `RolHelper.isRolActualDelegat(request)` a la
comprovació de rol d'ambdós mètodes `excel(...)`. La resta del mètode ja
usava sense condició `findSimplesByFiltrePaginatPerDelegat`/
`findMultiplesByFiltrePaginatPerDelegat` (`@PreAuthorize("isAuthenticated()")`
als dos serveis, sense restricció de rol), així que no calia cap altre canvi.

**Verificat:** `e2e/tests/delegat/consultes-multiples.spec.ts` → "permet
exportar el llistat a Excel" passa.

## Dades de mostra e2e (H2): falten permisos ACL sobre `pbl_procediment_servei`

Estrictament no és un bug de l'aplicació (el mecanisme de permisos ACL
funciona tal com està dissenyat), però és la causa arrel confirmada de
diverses fallades e2e del rol Delegat i probablement d'altres rols, així que
es documenta aquí perquè és rellevant per qui investigui altres suites
(`representant`/`administrador`) en paral·lel.

**Fet comprovat directament contra la BD H2 en marxa** (connexió JDBC amb
`h2-1.4.193` extret del contenidor `pinbal`, mateixa versió que empra
JBoss): les taules `pbl_acl_object_identity`, `pbl_acl_entry` i
`pbl_acl_class` estan **completament buides** (0 files), tot i que
`pbl_procediment_servei` sí té les dues files esperades (901001/901002,
`E2EPROC01` per a `Q2827003ATGSS001` i `SCDCPAJU`,
`00_e2e_seed_data.yaml`/`01_e2e_seed_serveis.yaml`).

`ProcedimentServiceImpl.findActiusAmbEntitatIServeiCodi` (invocat per
`ConsultaController.omplirModelPerMostrarFormulari` en carregar
`consulta/{servei}/new`) filtra els `ProcedimentServei` trobats per
`PermisosHelper.filterGrantedAll(..., ProcedimentServei.class, {READ},
aclService, auth)`: sense cap fila ACL que concedeixi permís READ sobre
l'objecte `ProcedimentServei` concret a l'usuari delegat, el mètode retorna
una llista buida encara que la relació procediment-servei existeixi. Efecte
observat directament al DOM: el select `#procedimentId` de
`consultaForm.jsp` es renderitza sense cap `<option>` per als 4 serveis
coberts pel fake SCSP (`Q2827003ATGSS001`, `SCDCPAJU`,
`SVDDGTVEHICULOSANCWS01`, `SVDDGPCIWS02`).

**Conseqüència en cadena:** `e2e/global-setup.ts` intenta seleccionar un
procediment (`NovaConsultaPage.seleccionarProcediment`) per crear consultes
de mostra abans de la suite; en no haver-hi cap opció, feia un
`selectOption()` que esgotava el timeout (abans del fix d'aquesta sessió,
~30s per crida) i acabava avortant silenciosament (el `try/catch` de
`globalSetup()` ho registra com a AVÍS i continua). Resultat: **cap consulta
simple ni múltiple de mostra s'arriba a crear mai** (comprovat: 0 files a
`PBL_CONSULTA`/`CORE_TRANSMISION`/`CORE_PETICION_RESPUESTA` a la BD en
marxa), la qual cosa explica també les fallades de llistat/detall
downstream a `consultes-multiples.spec.ts` (#1-#3 de la triatge original)
que depenen de trobar-hi almenys una fila.

**Fix aplicat en aquesta sessió (dins `e2e/`, no toca Liquibase):**
- `NovaConsultaPage.capacitats()` ara també retorna `teProcediment` (compta
  les `<option>` de `#procedimentId`).
- `NovaConsultaPage.seleccionarProcediment()` llança un error immediat i
  clar si el select no té cap opció, en lloc d'esperar tot el timeout
  d'acció sense cap missatge útil.
- `global-setup.ts` i `noves-consultes.spec.ts` (tests de consulta
  simple/múltiple) ara exigeixen `teProcediment` a més de
  `teDocumentTitular`/`permetMultiple` en triar servei candidat, i registren
  un avís/`test.skip` explícit distingint aquest cas (falta ACL) del cas
  "cap servei candidat és vàlid".

**Fix aplicat (Liquibase):** afegits a `01_e2e_seed_serveis.yaml` els
changesets `e2e-seed-acl-procediment-servei-delegat` i
`-representant`, amb les files `pbl_acl_class` / `pbl_acl_sid` /
`pbl_acl_object_identity` / `pbl_acl_entry` necessàries perquè delegat i
representant tinguin permís READ sobre els `ProcedimentServei`
901001/901002 (mateix esquema que generaria
`PermisosHelper.assignarPermisUsuari(...)` des de la UI real). Validat de
forma aïllada contra H2 (join manual `pbl_acl_object_identity` →
`pbl_acl_class` → `pbl_acl_entry` → `pbl_acl_sid`, 4 files amb
`mask=1 granting=1` per als 2 objectes × 2 sids). Pendent confirmar en
calent que `global-setup.ts` ja crea consultes de mostra un cop
reconstruït i reiniciat l'entorn.

## FIXAT — Delegat: `GET /pinbalback/usuari/configuracio` retornava 403

**On:** `e2e/tests/administrador/usuari-configuracio.spec.ts` (nom del
fitxer per herència històrica; els 3 tests fan servir `delegatPage`, no
`adminPage`), menú d'usuari > "Configuració".

**Causa arrel confirmada:** `UsuariController.emplenaModel(...)`, a la
branca `else` (qualsevol rol no Administrador/Superauditor, és a dir
Delegat i Representant/Auditor), cridava incondicionalment
`procedimentService.findAmbEntitat(entitatActual.getId())` i
`serveiService.findAmbEntitat(entitatActual.getId())`. Aquests dos mètodes
porten `@PreAuthorize("hasRole('PBL_ADMIN') or hasRole('PBL_REPRES') or
hasRole('PBL_AUDIT') or hasRole('PBL_SUPERAUD')")` — **no inclouen
`tothom`** (el rol J2EE amb què es mapeja el Delegat, vegeu
`RolHelper.ROLE_DELEG`). Per tant, per a qualsevol usuari amb rol Delegat,
Spring Security llançava `AccessDeniedException` en cridar
`emplenaModel(...)` des de `getConfiguracio()`, que `ExceptionTranslationFilter`
tradueix en `response.sendError(403)` sense poblar
`javax.servlet.error.exception` (per això no hi havia cap traça
d'excepció completa als logs, només `javax.servlet.error.status_code=403`
nu) — d'aquí la falsa pista de l'`AccessDeniedException` "silenciosa".

**Fix aplicat:** `UsuariController.emplenaModel(...)` ara distingeix
explícitament el cas Delegat (`RolHelper.isRolActualDelegat(request)`) i
crida les variants ja existents pensades per a aquest rol —
`procedimentService.findAmbEntitatPerDelegat(entitatId)` i
`serveiService.findPermesosAmbProcedimentPerDelegat(entitatId, null)`
(ambdues només `@PreAuthorize("isAuthenticated()")`, mateix patró que ja
feia servir `PinbalInterceptor.obtenirServeis()` per a la llista de
serveis del menú) — en lloc de les variants `findAmbEntitat(...)`
reservades a Admin/Representant/Auditor/Superauditor.

**Verificat:** rebuild+redeploy complet + `usuari-configuracio.spec.ts`
test 1 ("es pot obrir el formulari...") passa (abans: 403/"Forbidden" a
la modal; ara: formulari renderitzat amb les dades reals del Delegat).

## FIXAT (test) — `usuari-configuracio.spec.ts` comprovava `#btGuardarUsuariConfig` dins l'iframe

**On:** test 1 de `usuari-configuracio.spec.ts` (abans emmascarat pel 403
de dalt, mai s'havia arribat a executar aquesta línia amb èxit fins ara).

**Causa:** `frame.locator('#btGuardarUsuariConfig')` — `webutil.modal.js`
CLONA els botons del peu de formulari cap a `.modal-footer` (fora de
l'iframe; vegeu comentari de capçalera de `modal.ts`), així que l'element
original dins l'iframe no és el que cal comprovar/clicar. **Fix:** usar
`activeModal(page).locator('#btGuardarUsuariConfig')` (mateix patró que ja
fa servir `clickModalFooterButtonById`), no `frame.locator(...)`.

## FIXAT (majoritàriament) — Delegat: després de "Guardar" a `/usuari/configuracio`, la modal no es tancava (quedava amb el contingut redirigit a dins)

**On:** `ModalHelper` (`pinbal-back/.../helper/ModalHelper.java`) — bug
general d'infraestructura del mecanisme de modals amb iframe, no específic
d'`usuari/configuracio`.

**Causa arrel confirmada (amb DEBUG logging temporal a
`PinbalInterceptor.preHandle`/`ModalHelper.comprovarModalInterceptor`):**
el mecanisme antic marcava `ModalHelper.isModal(request) = true` en DUES
passades: (1) la petició original a `/modal/xxx` es reconeix pel prefix i
es fa `request.getRequestDispatcher("/xxx").forward(...)`, apuntant el path
sense prefix a un `Set` guardat a SESSIÓ; (2) s'esperava que
`PinbalInterceptor` es tornés a invocar per a la petició reenviada (path
`/xxx`, sense prefix), moment en què es comprovava el `Set` i es marcava
`isModal=true` a l'atribut de request. **Confirmat empíricament que aquesta
segona passada MAI passa**: el `forward()` intern arriba directament al
`@Controller` de destí (Spring SÍ que en resol correctament el handler)
però la cadena d'interceptors de Spring NO es torna a invocar per a aquest
`forward`. Resultat: `ModalHelper.isModal(request)` retornava sempre
`false` per a QUALSEVOL petició (GET o POST) rebuda via aquest mecanisme de
modal, fent que `BaseController.getModalControllerReturnValueSuccess(...)`
(usat per una dotzena de controladors, no només `UsuariController`) mai
prengués la branca que tanca la modal (`redirect:/modal/tancar`) i
retornés sempre la `url` "normal" en el seu lloc — la resposta es
mostrava, doncs, DINS del mateix iframe/modal en lloc de tancar-la.

**Fix aplicat:** `ModalHelper.comprovarModalInterceptor` ara marca
`isModal=true` (`request.setAttribute(...)`) ABANS de fer el `forward()`,
no després. Com que és el mateix objecte `HttpServletRequest`, l'atribut
sobreviu al forward amb normalitat (semàntica estàndard de Servlet), sense
dependre que l'interceptor es torni a invocar. S'ha eliminat tot el
mecanisme antic de `Set` guardat a sessió (`SESSION_ATTRIBUTE_REQUESTPATHSMAP`),
innecessari amb el nou enfocament.

**Verificat:** `usuari-configuracio.spec.ts` → "es pot canviar l'idioma
per defecte i es desa correctament" passa (abans: la modal quedava oberta
mostrant `/consulta` a dins). Suite `administrador`/`delegat` sencera
rellançada sense regressions noves respecte a l'estat previ.

**FIXAT (residual, era un bug del test, no de l'aplicació ni relacionat
amb la causa arrel d'aquest bug):** "es pot cancel·lar sense desar canvis"
fallava en un punt DIFERENT i posterior: després de tancar la modal via
"Cancel·lar" (tancament 100% client-side, `webutil.modal.js`, sense passar
mai pel servidor), el test tornava a clicar `#menu_user` per "reobrir" el
menú i `#menu_user_configuracio` no arribava a ser visible.

**Causa arrel confirmada** (inspeccionant la classe de l'`<li class=
"dropdown">` pare abans/després de tot el cicle): en clicar
`#menu_user_configuracio` (un enllaç DINS el `.dropdown-menu` ja obert) per
obrir la modal, Bootstrap NO tanca el dropdown — el seu listener global
només tanca en clicar-hi A FORA, mai en un clic intern al propi menú. La
classe `open` de l'`<li>` es queda posada mentre la modal està oberta i,
com que el tancament via "Cancel·lar" és purament client-side (no hi ha cap
recàrrega de pàgina que reconstrueixi el dropdown des de zero, a diferència
del flux "Guardar", que SÍ funcionava sempre — la seva navegació
`redirect:/` reinicialitza tot el DOM), la classe `open` continua posada
també DESPRÉS de tancar la modal. `#menu_user_configuracio`, doncs, ja és
visible en aquest punt. En tornar a clicar `#menu_user` "per reobrir-lo",
en realitat es tanca (toggle sobre un dropdown que Bootstrap encara
considera obert), deixant `#menu_user_configuracio` invisible just quan el
test hi vol fer clic.

**Fix aplicat (només al test):** `usuari-configuracio.spec.ts` — nova
funció `obrirMenuConfiguracio(page)` que NO assumeix l'estat del dropdown:
només clica `#menu_user` si `#menu_user_configuracio` encara no és visible.
Aplicada també als altres dos tests del fitxer (encara que el flux
"Guardar" no exhibia el bug, per robustesa i consistència).

**Verificat:** `usuari-configuracio.spec.ts` sencer (3/3) passa de manera
consistent en execucions repetides.

**Relacionat, NO tocat (fora d'abast d'aquesta sessió):** `NodecoHelper`
(`pinbal-back/.../helper/NodecoHelper.java`) implementa el mateix
mecanisme de "marcar a la segona passada" amb el mateix defecte estructural
(`isNodeco(request)` probablement sempre `false` pel mateix motiu). No se
n'ha trobat cap símptoma concret als tests actuals, però si algun dia se'n
detecta un, la causa i el fix són els mateixos que aquí.

## FIXAT — Infra: property `es.caib.pinbal.scsp.keystoreFile`/`keystorePass` no arribava mai via System property; `core_parametro_configuracion` es quedava amb el placeholder `'.'` inicial

**On:** arrencada de `pinbal-ejb.jar` (`ScspPropertyPlaceholderConfigurer`
→ `ScspCryptoFactoryBean`).

**Causa arrel:** `initial_data_scsp.sql` sembra
`core_parametro_configuracion.keystoreFile`/`keystorePass` amb el valor
placeholder `'.'`, esperant que `ScspPropertyPlaceholderConfigurer.
postProcessBeanFactory()` (`moveToDatabase(...)`) el sobreescrigui en
arrencar amb el valor real. El codi antic llegia el valor via
`System.getProperties()` (a través de `PropertyPlaceholderConfigurer.
resolvePlaceholder(property, props)`), però les variables d'entorn amb
punts al nom (`es.caib.pinbal.scsp.keystoreFile=...` a `docker-compose.yml`)
**mai es tradueixen a System properties `-D` reals de la JVM** (confirmat
inspeccionant `/proc/<pid>/cmdline`: no hi apareixia cap
`-Des.caib.pinbal.scsp.*`). Amb `valor='.'`, el constructor
d'`ScspCryptoFactoryBean` (`scsp-core:5.0.11`) fa
`path.substring("classpath:/".length() - 1)` sobre un string d'1 caràcter
→ `StringIndexOutOfBoundsException: String index out of range: -9`, que
fa fallar tot `pinbal-ejb.jar` (`EjbContextStartup`) i, en cascada, el
desplegament sencer de `pinbal.ear` (404 a tota l'aplicació).

**Fix aplicat:** `ScspPropertyPlaceholderConfigurer.moveToDatabase(...)`
(`pinbal-scsp/.../es/scsp/common/utils/ScspPropertyPlaceholderConfigurer.java`
— classe pròpia de PINBAL que sobreescriu la de la llibreria SCSP via el
mecanisme `patch-scsp-local-repo.py`) ara resol el valor primer via
`environment.getProperty(property)` (el `ConfigurableEnvironment` de
Spring, ja injectat via `EnvironmentAware`), que SÍ que comprova tant
`System.getProperties()` com `System.getenv()` (aquesta darrera és l'única
via real per la qual arriben aquestes propietats quan es desplega amb
docker-compose), abans de recórrer al comportament antic com a fallback.
**No calen canvis a `standalone-openshift.xml`** (les propietats
`es.caib.pinbal.scsp.keystoreFile`/`keystorePass` ja eren a
`docker-compose.yml` des d'abans d'aquesta sessió — el problema era que el
codi no les sabia trobar per aquesta via).

**Verificat:** reset manual de `core_parametro_configuracion` al
placeholder `'.'` original + rebuild+redeploy complet contra JBoss → el
desplegament arrenca net (`WFLYSRV0025`, sense errors) i la BD queda
automàticament amb els valors correctes
(`file:/opt/webapps/keystores/interoperabilitat.jks` / `tecnologies`)
sense cap intervenció manual.

## FIXAT — Infra: pool de connexions JBoss esgotat sota càrrega concurrent de Playwright

Trobat de forma repetida (179+ ocurrències durant una única execució) als
logs de l'aplicació:

```
javax.resource.ResourceException: IJ000655: No managed connections available within configured blocking timeout (30000 [ms])
```

**Causa:** `max-pool-size` dels 3 datasources (`pinbalDS`, `pinbalScspDS`,
`usuarisDS`) a `standalone-openshift.xml` era 10/5/10, insuficient quan
diverses suites Playwright (o diversos processos) executen en paral·lel
contra la mateixa instància.

**Fix aplicat:** els 3 valors `max-pool-size` s'han parametritzat amb
`${env.JBOSS_DB_POOL_MAX:10}` / `${env.JBOSS_DB_SCSP_POOL_MAX:5}` /
`${env.JBOSS_USER_DB_POOL_MAX:10}` (per defecte iguals als valors originals,
així que producció no canvia de comportament si no es defineix la
variable). `docker-compose.e2e.yml` ara els sobreescriu tots tres a `30`
específicament per a l'entorn e2e.

## FIXAT — Gestió de camps d'un servei (`/servei/{codi}/camp`) i previsualització (`/servei/{codi}/preview`) trencaven amb `ScspException`/`FileNotFoundException` si l'XSD de dades específiques s'havia esborrat

**On:** navegar a "Gestió de camps" (o obrir la previsualització) d'un
servei amb `activaGestioXsd=true` però sense cap fitxer XSD present al
disc — per exemple, després d'esborrar l'únic XSD pujat des del
formulari del servei sense haver-lo tornat a pujar.

**Reportat com:**
```
es.caib.pinbal.logic.intf.service.exception.ScspException: Error al
generar arbre de dades específiques per al servei (codi=...)
Caused by: ConsultaScspGeneracioException:
java.io.FileNotFoundException: /opt/eap/apps/pinbal/xsd/{codi}/datos-especificos.xsd
(No such file or directory)
  at es.caib.pinbal.scsp.XmlHelper.getPathPerServei(XmlHelper.java:377)
  at es.caib.pinbal.back.controller.ServeiController.serveiCamp(ServeiController.java:309)
```

**Dues causes arrel superposades, totes dues a `pinbal-scsp`:**

1. **`PropertiesHelper.getProperty(String)`** (`pinbal-scsp/.../PropertiesHelper.java`)
   només llegia `System.getProperty(key)`. La propietat
   `es.caib.pinbal.xsd.base.path` (definida a `docker-compose.yml` com a
   variable d'entorn amb punts al nom) **mai es tradueix a System property
   `-D` real de la JVM** — mateixa causa arrel que el bug de
   `keystoreFile`/`keystorePass` documentat més amunt
   (`ScspPropertyPlaceholderConfigurer`), aquí en una classe diferent i
   sense la capa intermèdia de BBDD. Amb el path base a `null`,
   `XmlHelper.getPathPerServei()` (línia 377) feia NPE en construir el
   path complet.
   **Fix:** `PropertiesHelper.getProperty()` ara fa fallback a
   `System.getenv(key)` quan `System.getProperty(key)` retorna `null`.

2. Un cop el path es resolia correctament, quedava exposat un segon bug
   real: `XmlHelper.getScspResourceInputStream()` (línia ~752) feia
   `FileUtils.openInputStream(fitxer)` directament, sense comprovar
   `fitxer.exists()`, quan `gestioXsdActiva=true`. Esborrar l'XSD des de
   la UI (`ServeiController.xsdDelete` → `ServeiServiceImpl.xsdDelete`)
   esborra el fitxer físic i la fila `ServeiXsd` de BBDD, però **no
   desactiva `activaGestioXsd`** — deixant el servei en un estat
   consistentment "gestió XSD activa" sense cap fitxer. Cap dels dos punts
   d'entrada de controller que criden `generarArbreDadesEspecifiques`
   (`ServeiController.serveiCamp` i `serveiPreview`) atrapaven l'excepció,
   així que acabava com a pàgina d'error sense gestionar.
   **Fix:** `XmlHelper.getScspResourceInputStream()` ara comprova
   `fitxer.exists()` abans d'obrir el stream; si no existeix, cau al
   mateix fallback de recurs de classpath que ja s'usava quan
   `gestioXsdActiva=false`, en lloc de deixar propagar el
   `FileNotFoundException`.

**Nota:** queda com a inconsistència de dades (no arreglada aquí, per no
ampliar l'abast d'aquest fix) que `xsdDelete` no reseteja
`activaGestioXsd` quan no queda cap XSD — el fix anterior ho fa inofensiu
(cau al fallback en lloc de petar), però idealment l'esborrat de l'últim
XSD també hauria de desactivar el flag.

**Verificat:** rebuild+redeploy complet contra JBoss + 4 execucions
consecutives en verd del test `formulari: fitxers XSD, gestió de grups,
regles i previsualització` de `gestionar-serveis.spec.ts`.

## FIXAT (test) — `ServeiCampPage.editarReglaAccio`/`esborrarRegla`: el botó "Accions" de la taula de regles no té classe `dropdown-toggle`

**On:** un cop arreglat el bug anterior, el test arribava més lluny però
petava a `editarReglaAccio` amb `TimeoutError` esperant
`a.dropdown-toggle, button.dropdown-toggle` dins la fila de la taula
`#taula-regles`.

**Causa:** el botó "Accions" d'aquesta taula concreta
(`serveiCamp.jsp:667`) es genera com
`<button class="btn btn-primary" data-toggle="dropdown">`, sense la
classe `dropdown-toggle` que sí que porten els desplegables equivalents a
la resta de l'aplicació (patró `a.dropdown-toggle, button.dropdown-toggle`
usat arreu a les Page Objects de l'e2e). No és un bug de l'aplicació,
només una divergència de marcatge en aquesta taula.

**Fix aplicat:** `ServeiCampPage.editarReglaAccio`/`esborrarRegla` ara
localitzen el botó per `[data-toggle="dropdown"]` en lloc de
`a.dropdown-toggle, button.dropdown-toggle`.

## FIXAT — Mateix patró de condició de carrera de `waitForInitialDataTableLoad` trobat també als tests del delegat i del representant

**On:** `consultes-simples.spec.ts` i `consultes-multiples.spec.ts` (delegat),
i `usuaris.spec.ts`/`procediments.spec.ts` (representant).

**Causa:** el mateix patró racy ja documentat i arreglat a
`gestionar-entitats.spec.ts` (`page.goto(...)`/`.click()` seguit d'una
crida SEPARADA a `waitForInitialDataTableLoad(page)`): la petició ajax del
DataTable es dispara a `$(document).ready()`, abans de l'esdeveniment
`load` del qual depèn `page.goto()`, així que en un servidor prou ràpid la
resposta pot arribar i completar-se abans que l'escolta s'hagi registrat.
Apareixia a 11 llocs més entre aquests 4 fitxers.

**Fix aplicat:** substituïts tots els parells `goto()`/`click()` +
`waitForInitialDataTableLoad(page)` per `waitForDataTableReload(page,
async () => { ... })`, que registra l'escolta ABANS de l'acció.

**Verificat:** múltiples execucions en verd de tots els fitxers afectats.

## FIXAT (test) — `RepresentantUsuariPermisosPage.afegirServei`: el botó de confirmació de la modal "Afegir" es clicava dins l'iframe, on queda amagat

**On:** el test "afegir, denegar accés, esborrar seleccionats i esborrar
tots" (`representant/usuaris.spec.ts`) fallava intentant fer clic al botó
de confirmació dins la modal "Afegir" (permisos d'usuari).

**Causa:** el mateix patró de `webutil.modal.js` documentat a
`e2e/utils/modal.ts` (`clickModalFooterButton`): els botons del peu de la
modal (`#modal-botons` dins l'iframe, aquí `.confirm-afegir-selected` a
`representantUsuariPermisForm.jsp:198-202`) es clonen al `.modal-footer`
de la pàgina pare en carregar l'iframe, i els originals dins l'iframe
queden amagats (`modalBotons.hide()`). El propi JSP ho evidencia
manipulant `parent.document` per activar/desactivar aquest clon en
(des)seleccionar files. El test feia clic al botó ORIGINAL dins l'iframe
(`frame.locator('#addSelectedForm button[type="submit"]')`), ja amagat.

**Fix aplicat:** `afegirServei` ara fa clic al clon dins
`activeModal(page).locator('.modal-footer .confirm-afegir-selected')`,
esperant abans que es desactivi la classe `disabled`.

## FIXAT (test) — `RepresentantUsuariPermisosPage.denegarAcces` cercava un `link`, però "Denegar accés" és un `<button type="submit">`

**On:** mateix test que l'anterior, un cop arreglat el bug de la modal;
`denegarAcces` no trobava mai l'element (`TimeoutError` esperant
`getByRole('link', { name: /denegar acc/i })`).

**Causa:** `representantUsuariPermis.jsp:157` genera "Denegar accés" com
un `<button type="submit">` dins un `<form>` normal (POST, sense ajax),
no com un `<a>`. `getByRole('link', ...)` no hi troba mai res.

**Fix aplicat:** `denegarAcces` ara cerca `getByRole('button', { name:
/denegar acc/i })`. Com que és un submit natiu (no ajax),
`locator.click()` ja espera la navegació resultant per defecte; no cal
`waitForEvent('load')` a part.

**Nota (pol·lució de dades, no un bug):** com que `USUARI_ACTIU_CODI` és
fix (no varia per execució), els permisos que aquest test afegeix
s'acumulen entre execucions que fallen abans d'arribar al pas final
"esborrar tots". S'ha afegit una neteja defensiva a l'inici del bloc
"Permisos" (`if (await permisos.rows().count()) await
permisos.esborrarTots();`) i, per la mateixa raó, al test "Gestió de
serveis per procediment" de `procediments.spec.ts` i a diversos tests de
`procediments.spec.ts` que comprovaven la visibilitat d'una fila
acabada de crear sense filtrar primer pel seu codi (mateix patró que la
nota de pol·lució de `gestionar-entitats.spec.ts`/`ConsultesRealitzadesPage`
documentada més amunt): s'ha afegit filtratge per `#codi` abans de
comprovar visibilitat a `CRUD complet...`, `filtre per codi`, `crear
fill...` i `Gestió de serveis per procediment` (tots quatre tests de
`procediments.spec.ts`).

## FIXAT — `procedimentServeiMigrar.jsp`: "No message found under code 'SCDCPAJU - Servei de consulta de padró de convivència'"

**On:** obrir la modal "Migrar" d'un servei dins la pantalla de gestió de
serveis d'un procediment (`/procediment/{id}/servei/{serveiCodi}/migrar`).

**Reportat com:**
```
javax.servlet.jsp.JspTagException: No message found under code
'SCDCPAJU - Servei de consulta de padró de convivència' for locale 'ca'.
```

**Causa arrel:** `procedimentServeiMigrar.jsp:33` feia servir
`optionTextKeyAttribute="codiNom"` al `<pbl:inputSelect>` del selector de
servei destí. Aquest atribut de la tag (`inputSelect.tag`) tracta el
valor de la propietat (`opt[optionTextKeyAttribute]`, aquí
`ServeiDto.getCodiNom()`) com un CODI d'i18n i el passa directament a
`<spring:message code="...">` — pensat per a opcions d'enum amb clau de
traducció real. Però `ServeiDto.getCodiNom()`
(`pinbal-service-intf/.../ServeiDto.java:155-157`) retorna un text ja
formatat (`codi + " - " + descripcio`), no una clau de missatge; cap
entrada així existeix al `messages*.properties`, d'on l'excepció. Totes
les altres 12 ocurrències de `codiNom` a la resta de JSPs de l'aplicació
fan servir correctament `optionTextAttribute` (text pla, sense
`spring:message`); aquesta era l'única amb `optionTextKeyAttribute`.

**Fix aplicat:** canviat `optionTextKeyAttribute="codiNom"` per
`optionTextAttribute="codiNom"` a `procedimentServeiMigrar.jsp:33`.

**Verificat:** rebuild+redeploy complet + 3 execucions consecutives en
verd del test "afegir, migrar, esborrar, codi addicional i permisos" de
`procediments.spec.ts`, més el fitxer complet (4/4 tests) 2 vegades.

## FIXAT (test) — `NovaConsultaPage.pujarFitxerMultiple`: la descàrrega de la plantilla CSV sempre donava 404

**On:** qualsevol flux que crea una consulta múltiple des de la UI real
(`e2e/global-setup.ts` sembrant dades de mostra, i el test "es pot
realitzar una consulta múltiple des del formulari" de
`noves-consultes.spec.ts`). Es manifestava com l'avís de global-setup
`no s'ha pogut completar la creació de dades de mostra (consultes
simples/múltiples)... Detall: No s'ha pogut descarregar la plantilla CSV
(404) per a Q2827003ATGSS001`, i indirectament com a la majoria de tests
de "Detall de consulta múltiple" saltant-se per manca de dades (aquest
era el "problema als tests que accedeixen al detall de les consultes"
reportat: no és un bug de la pantalla de detall en si, sinó que mai
arriba a haver-hi cap consulta múltiple per obrir).

**Causa arrel:** `pujarFitxerMultiple` (`e2e/pages/NovaConsultaPage.ts:199`)
feia `this.page.request.get('/consulta/{codi}/plantilla/CSV')` — **amb
barra inicial**. Quan una URL relativa comença per `/`, es resol contra
l'ARREL del host (substituint tot el path del `baseURL`), no contra el
`baseURL` sencer: `new URL('/consulta/...', 'http://localhost:8080/pinbalback/')`
dona `http://localhost:8080/consulta/...`, perdent el context path
`/pinbalback` de l'aplicació. La petició acabava fora de l'app (arrel del
JBoss/Undertow), que respon amb el 404 genèric del contenidor
(`<html><head><title>Error</title></head><body>404 - Not Found</body></html>`),
no cap error de l'aplicació — coherent amb "en l'aplicació es pot accedir
sense problema" (un usuari real mai fa aquesta petició en cru; hi arriba
sempre via navegació normal amb URL relativa sense barra inicial, com fa
la resta del codi e2e establert, p.ex. `page.goto('consulta')`).

**Fix aplicat:** treta la barra inicial: `consulta/{codi}/plantilla/CSV`.

**Verificat:** la petició ara torna 200 amb el CSV real de la plantilla
(confirmat amb una prova manual imprimint status/headers/body). Un cop
arreglat això, la creació de la consulta múltiple de mostra avança més
enllà (ja no hi ha excepció de descàrrega), però queda bloquejada per un
problema DIFERENT i no relacionat (vegeu següent secció).

## FIXAT — Infra: `ScspClientInterceptorException: Error general al verificar el certificado: Se produjo un error al comprobar el estado de revocacion del certificado` bloquejava TOTA consulta (simple i múltiple) real en aquest entorn e2e

**On:** enviar QUALSEVOL consulta real (simple o múltiple, rol delegat)
contra el fake SCSP. Es manifestava com l'avís crònic de
`global-setup.ts` "el formulari no ha redirigit al llistat" per a les 3
consultes simples de mostra I com l'excepció que bloquejava la consulta
múltiple (secció anterior, un cop arreglat el bug de la barra inicial).
No és un problema de creació de dades de mostra puntual: és el motiu pel
qual CAP consulta real (simple o múltiple) s'havia pogut crear mai en
aquest entorn durant tota la sessió — d'aquí la gran quantitat de tests
de "detall" que sempre acabaven saltant-se per manca de dades.

**Causa arrel** (investigat decompilant `scsp-core-5.0.11.jar` de
`local-repo/`, sense codi font disponible):
`es.scsp.common.interceptors.HandlerVerificarCertificado.validate()` és
un `ClientInterceptor` de Spring-WS que s'executa per a TOTA resposta
SOAP de SCSP (entrada i sortida, sync i async): extreu el certificat de
signatura WS-Security i crida
`revocationManager.comprobarCertificadoRevocacion(x509)`. Qualsevol
resultat que no sigui OK (o qualsevol excepció) es reempaqueta com
`ScspClientInterceptorException` amb exactament aquest missatge. El bean
`revocationManager` per defecte (`BasicRevocationManager`, autoregistrat
per `<context:component-scan base-package="es.scsp">` a
`application-context-scsp.xml`) fa una crida SOAP real a @firma si
`afirma.enabled=true` (sembrat així a `initial_data_scsp.sql`, amb
`afirma.url` sembrat com a placeholder `.` — mai funcional), i si no, com
que no hi ha `custom.cert.validation.class` configurat, retorna
directament `REVOCADO` (no hi ha CAP via de "no comprovar" amb aquest
bean per defecte). Si aquest mòdul (`core_modulo.nombre='ValidarCertificado'`,
sembrat actiu `activoentrada=activosalida=1`) està actiu, doncs, la
comprovació SEMPRE falla en aquest entorn, sense connectivitat real a un
OCSP/@firma vàlid possible.

**Fix aplicat:** nou cha`ngeSet` `e2e-disable-validar-certificado`
(`pinbal-persistence/.../db/changelog/e2e/01_e2e_seed_serveis.yaml`,
context `e2e` únicament, mai s'aplica a dev/producció) que desactiva el
mòdul `ValidarCertificado` (`UPDATE core_modulo SET activoentrada=0,
activosalida=0 WHERE nombre='ValidarCertificado'`).

**Verificat:** un cop aplicat, `global-setup.ts` crea correctament les
consultes simples "èxit 1"/"èxit 2" I la consulta múltiple (abans totes
fallaven); el test "es pot realitzar una consulta múltiple des del
formulari" de `noves-consultes.spec.ts` passa; el conjunt de tests de
"Detall de consulta" del delegat passa de 12 a 15 tests executats
realment (en lloc de saltar-se per manca de dades).

## FIXAT (test) — `modalFrame()`: condició de carrera genèrica en TOTA la suite, el `<body>` de l'iframe es podia donar per "visible" contra l'estat transitori "about:blank"

**On:** qualsevol ús de `modalFrame(page)` (dotzenes d'ocurrències arreu
de la suite). Es va detectar concretament als tests de "Detall de
consulta múltiple" del delegat, que es quedaven penjats 15000ms esperant
`div.modal.in iframe :>> body`.

**Causa arrel:** just després que `webutil.modal.js` assigni
`iframe.attr('src', ...)`, l'iframe passa per un instant transitori a
`about:blank` abans que comenci de veritat la navegació cap al contingut
real (confirmat empíricament registrant `framenavigated`/`request`/
`response` amb timestamps). Com que un `<body>` buit també és "visible"
per Playwright, el primer check SÍNCRON d'`expect(frame.locator('body')).
toBeVisible()` es podia satisfer immediatament contra aquest estat
transitori — sense arribar mai a re-comprovar contra el contingut real un
cop carregat, ja que un `expect()` que ja passa a la primera comprovació
no torna a repetir-se. El resultat era no-determinista: depenent de si el
check arrencava abans o després del "flash" d'`about:blank`, el test
podia continuar amb un frame buit (fallant més endavant de manera
confusa) o penjar-se esperant `body` si per algun motiu la navegació
trigava més del compte.

**Fix aplicat:** `modalFrame()` (`e2e/utils/modal.ts`) ara espera
explícitament, amb `expect.poll(...)`, que la `contentFrame()` real de
l'iframe hagi deixat de ser `about:blank` ABANS de comprovar la
visibilitat del `<body>`.

**Verificat:** múltiples execucions en verd dels tests de "Detall de
consulta múltiple" (delegat) que abans penjaven intermitentment.

## FIXAT (test) — Navegació interna d'un iframe ja obert (clic en un enllaç DINS del propi iframe): mateixa classe de condició de carrera, però NO coberta per `modalFrame()`

**On:** test "permet obrir el detall d'una sol·licitud..." de
`consultes-multiples.spec.ts` (delegat): després de fer clic a "Detalls"
d'una fila de `#solicituds` (un enllaç DINS de l'iframe ja carregat, que
en provoca una navegació interna cap a una pàgina de detall, NO una nova
modal via `webutil.modal.js`), l'enllaç "Tornar" no apareixia mai dins
del timeout, de manera intermitent.

**Diagnòstic:** confirmat DEFINITIVAMENT que NO és un bug de l'aplicació
ni de dades: 10 crides consecutives en cru (`page.request.get`) a
l'endpoint exacte retornen SEMPRE el mateix HTML (24316 bytes idèntics,
sempre amb l'enllaç "Tornar"). La flakiness és 100% del costat del
navegador/Playwright en la transició d'aquesta navegació interna de
l'iframe (mateixa família de problema que `modalFrame()`, però aquí no
n'hi havia cap protecció perquè el test no torna a cridar `modalFrame()`
per a aquesta segona navegació).

**Fix aplicat:** registrat `page.waitForResponse(...)` ABANS del clic a
"Detalls" (patró ja establert repetidament en aquesta sessió), esperant
la resposta HTTP real de `/modal/consulta/...` abans de continuar amb les
comprovacions sobre `frame`.

**Verificat:** 5/5 execucions consecutives en verd (abans, intermitent).

## FIXAT (test) — El mateix patró d'ajax sense esdeveniment de navegació esperable (`.load()`) al "Veure XML" d'una sol·licitud

**On:** mateix test que l'anterior i el test "permet veure el xml de
petició i resposta d'una sol·licitud concreta": el contingut de
`#missatgeXml` (omplert amb `$('#modal-missatge-xml .modal-body').
load(href)`, ajax pla, `consultaInfo.jsp:33`) es comprovava amb
`.not.toHaveValue('')` just després del clic, sense cap esdeveniment de
navegació que esperar.

**Fix aplicat:** registrat `page.waitForResponse(...)` (filtrant per
`/xmlPeticio`/`/xmlResposta` respectivament) ABANS de cada clic als
enllaços "Veure XML" d'una sol·licitud.

## FIXAT (test) — Diagnòstic corregit: `xmlResposta` no era un bug de `TransmisionDao`, era el mateix "cal apuntar a una consulta Tramitada concreta amb missatges" que als tests d'administrador

**On:** test "permet veure el xml de petició i resposta d'una
sol·licitud concreta" (delegat, `consultes-multiples.spec.ts`).

**Diagnosi ORIGINAL (INCORRECTA, deixada aquí per traçabilitat):** en el
moment en què es va detectar aquest fallo, `obrirPrimerDetallMultiple`
encara agafava "la primera fila" (`rows.first()`) sense filtrar per
`scspPeticionId`. Com que en aquell punt `global-setup.ts` ja creava amb
èxit una consulta múltiple real (un cop arreglat el bug del certificat,
secció anterior), "la primera" fila (ordenada per data descendent) era
SEMPRE la creada dinàmicament, no la sembrada `PBL_E2E_MULTIPLE_01`. Es
va arribar a decompilar `scsp-core-5.0.11.jar` (`TransmisionDao.select`)
sospitant un bug de la `Criteria` d'Hibernate, sense trobar-ne la causa
final.

**Diagnosi CORRECTA:** no hi ha cap bug a `TransmisionDao`. La consulta
múltiple creada DINÀMICAMENT per `global-setup.ts` (via el fake SCSP,
flux asíncron real) NO deixa `core_transmision.xmltransmision` poblat de
la mateixa manera que la consulta SEMBRADA per Liquibase (inserida
directament per SQL amb el valor ja fixat) — sigui per timing del
processament asíncron o per com el fake respon a aquest camp concret.
En filtrar `obrirPrimerDetallMultiple` explícitament per
`PBL_E2E_MULTIPLE_01` (vegeu la secció següent, arreglat pel mateix motiu
que als tests d'administrador: cal apuntar a una consulta Tramitada
concreta que tingui els missatges, no "la primera"), aquest test passa
de manera fiable (3/3 execucions consecutives), sense cap altre canvi.

**Lliçó:** en aquest entorn, QUALSEVOL test de detall que depengui de
missatges/sol·licituds/justificants ha d'apuntar sempre a una consulta de
mostra sembrada coneguda (`PBL_E2E_SIMPLE_OK`, `PBL_E2E_SIMPLE_JUSTERR`,
`PBL_E2E_MULTIPLE_01`...) i mai a "la primera fila"/"la fila filtrada per
document", ja que `global-setup.ts` ara crea consultes reals que poden
competir per aquesta posició sense tenir garantit el mateix estat
determinista (missatges, justificant, sol·licituds completes) que les
sembrades.

## FIXAT (test) — Mateixa lliçó aplicada als tests de detall del delegat: calia apuntar a consultes de mostra sembrades conegudes, no "la primera fila"/"filtrar per document"

**On:** `consultes-simples.spec.ts` (`obrirDetallConsultaTramitada`, test
"es pot reintentar i veure l'error...") i
`consultes-multiples.spec.ts` (`obrirPrimerDetallMultiple`), rol delegat.

**Causa arrel:** exactament el mateix problema documentat més amunt per a
`procediments.spec.ts`/`gestionar-entitats.spec.ts` (dades acumulades
que trenquen l'assumpció de "primera fila"), amb un matís nou: un cop
arreglat el bug del certificat (secció anterior), `global-setup.ts`
crea ara consultes REALS amb el mateix document normal de prova
(`SCSP_FAKE_SUCCESS_DOC = '12345678Z'`, coincident amb el
`titular_docnum` sembrat de `PBL_E2E_SIMPLE_OK`). El filtre per
`#titularDocument` que abans identificava inequívocament la consulta
sembrada ara pot trobar-ne més d'una, i quedar-se amb la més recent (la
creada dinàmicament) — que no té per què tenir justificant/missatges en
el mateix estat determinista que la sembrada. Igualment,
`obrirPrimerDetallMultiple` agafava "la primera fila" del llistat de
múltiples sense filtrar, la qual, un cop `global-setup.ts` crea consultes
múltiples reals, deixa de ser sempre `PBL_E2E_MULTIPLE_01`.

**Fix aplicat:** totes les funcions/tests afectats ara filtren
explícitament pel `#scspPeticionId` de la consulta de mostra sembrada
corresponent (`PBL_E2E_SIMPLE_OK`, `PBL_E2E_SIMPLE_JUSTERR`,
`PBL_E2E_MULTIPLE_01`) abans de comprovar la fila, seguint exactament el
mateix patró ja establert a `ConsultesRealitzadesPage.cercarPerPeticio`
(administrador). Addicionalment, es va detectar i corregir una condició
de carrera introduïda pel primer intent d'aquest mateix fix:
`waitForDataTableReload` només espera la resposta de xarxa de l'ajax
(confirmat amb `recordsFiltered:1` i les dades correctes ja a la
resposta), no que el DataTable hagi acabat de repintar el resultat al
DOM; comprovar la fila amb `.isVisible()` (sense esperar) just després
era una condició de carrera que feia saltar el test com "consulta no
trobada" encara que la fila hi fos. Substituït per
`locator.waitFor({ state: 'visible', timeout })`.

**Verificat:** 3 execucions consecutives en verd del fitxer complet de
tests del delegat (18 passats, 3 saltats per motius legítims i no
relacionats — no 21, ja que "vista prèvia"/"informació de l'arxiu" depenen
de capacitats de l'entorn (plugin d'arxiu) i "document disparador d'error"
d'una validació addicional del servei no coberta pel setup genèric).

## FIXAT — Pol·lució de dades: `E2EENT02` ("Entitat E2E Inactiva") es quedava ACTIVA amb el delegat vinculat, fent que TOTA la suite del delegat es quedés sense dades

**On:** qualsevol test del delegat (llistats de consultes simples/múltiples,
formulari de nova consulta...) després d'una execució completa de la
suite. Es manifestava com "el delegat no té permisos sobre serveis" / cap
dada als llistats, i el select `#procedimentId` del formulari de nova
consulta sense opcions per a CAP servei — símptomes que apuntaven a un
problema d'ACL, però la causa real era una altra.

**Causa arrel:** el test "si una entitat es desactiva, desapareix de les
entitats accessibles del rol, i viceversa"
(`gestionar-entitats.spec.ts`) activa temporalment `E2EENT02` i hi
vincula el delegat per provar el desplegable de canvi d'entitat, i
desactiva `E2EENT02` altra vegada al final com a neteja. Si el test
falla ABANS d'arribar a aquest últim pas (vegeu el bug següent), `E2EENT02`
es queda ACTIVA amb el delegat vinculat-hi. `EntitatHelper` (back)
selecciona l'entitat "actual" per sessió a l'índex 0 de la llista
d'entitats accessibles de l'usuari — NO segons el flag `principal` —
així que un cop `pbl_deleg` té dues entitats accessibles, un login nou
pot acabar amb `E2EENT02` (sense cap servei/procediment associat, vegeu
`pbl_entitat_servei`) com a entitat "actual" en lloc de la
"Entitat E2E Principal" (900001), deixant-ho tot buit per a qualsevol
pantalla que depengui de l'entitat de sessió.

**Fix aplicat (dades):** `UPDATE pbl_entitat SET activa = 0 WHERE codi =
'E2EENT02'` (restaura l'estat sembrat original).

**Fix aplicat (prevenció):** el bloc de codi del test que activa
`E2EENT02`/hi vincula el delegat ara està embolcallat en un
`try`/`finally`: la neteja (desactivar `E2EENT02`) s'executa SEMPRE,
encara que alguna assertion anterior falli — mateix patró que el fix
d'idioma més amunt. Abans, qualsevol fallada enmig del test deixava
`E2EENT02` activa indefinidament, trencant en cascada tots els tests
posteriors del delegat (de qualsevol fitxer, en qualsevol execució futura
de la suite) fins que algú ho detectés i reparés manualment.

## FIXAT (test) — El mateix test fallava perquè assumia que l'entitat sempre apareixeria DINS del desplegable, mai com a "entitat actual"

**On:** mateix test, a l'assertion `expect(#menu_entitat +
ul.dropdown-menu a, hasText: 'Entitat E2E Inactiva').toBeVisible()`.

**Causa arrel:** un cop el delegat té DUES entitats accessibles,
`EntitatHelper` (back) en tria una com "actual" per defecte (índex 0 de
la llista que retorna el backend, sense relació amb el flag `principal`)
— aquesta es mostra com el TEXT del propi `#menu_entitat` (el botó
commutador), mentre que el desplegable només llista les ALTRES entitats
(les que es poden triar, no la ja seleccionada). El test assumia que
`E2EENT02` sempre acabaria sent la "no actual" (i per tant dins del
desplegable), però confirmat empíricament (snapshot de la pàgina en
fallar) que pot acabar sent la "actual" en lloc seva — depèn de l'ordre
intern amb què el backend retorna la llista, no controlable des del
test.

**Fix aplicat:** nou helper `expectEntitatAccessible(page, nom)` que
comprova que `nom` és accessible de QUALSEVOL de les dues maneres (com a
text de `#menu_entitat` o com a opció dins el seu desplegable), en lloc
d'assumir sempre la segona.

**Verificat:** 3 execucions consecutives en verd (abans, fallava de
manera 100% reproduïble encara amb les dades netes).

## Configuració — `.env.e2e`: `E2E_CONSULTA_PROCEDIMENT_ID` ha de ser l'id NUMÈRIC del procediment, no el seu codi

**On:** `global-setup.ts` i els tests "es pot realitzar una consulta
simple/múltiple des del formulari" (`noves-consultes.spec.ts`), quan
`E2E_CONSULTA_SERVEI_CODI`/`E2E_CONSULTA_PROCEDIMENT_ID` estan definits a
`e2e/.env.e2e` (fitxer local, gitignored).

**Causa:** `consultaForm.jsp` genera el select `#procedimentId` amb
`optionValueAttribute="id"` (l'id numèric del procediment, p.ex. `900201`
per a `E2EPROC01`), no amb el seu codi. Amb
`E2E_CONSULTA_PROCEDIMENT_ID=E2EPROC01` (el codi, no l'id), cap `<option>`
hi coincideix mai i `NovaConsultaPage.seleccionarProcediment()` esgota el
timeout intentant seleccionar-la.

**Fix aplicat:** `e2e/.env.e2e` (local) corregit a
`E2E_CONSULTA_PROCEDIMENT_ID=900201`; `.env.e2e.example` ara documenta
explícitament que aquest valor és l'id numèric, no el codi.

## PENDENT (decisió d'usuari) — `E2E_CONSULTA_SERVEI_CODI=SCDCPAJU` no pot completar mai una consulta real en aquest entorn e2e

**On:** qualsevol enviament real de consulta (simple o múltiple) contra
`SCDCPAJU` amb el delegat, quan `.env.e2e` restringeix
`E2E_CONSULTA_SERVEI_CODI` a aquest únic candidat (en lloc dels 4 per
defecte, que comencen per `Q2827003ATGSS001`).

**Símptoma:** "La consulta ha retornat un error: [0904] S'ha produït un
error no esperat. Contacti amb el centre de suport." — SCDCPAJU sí que
es mostra correctament al formulari (document/procediment seleccionables,
`teProcediment`/`hasSimpleDocument` certs), però la submissió real sempre
falla.

**Causa probable:** `01_e2e_seed_serveis.yaml`, changeset
`e2e-seed-clau-privada-firma`, només autoritza l'organisme cessionari de
prova (900011) a firmar peticions per al servei `Q2827003ATGSS001` (id
900701, `core_req_cesionarios_servicios.servicio=900701`) — NO per a
`SCDCPAJU` (id 900702, `tiposeguridad=XMLSignature`, diferent del
`WS-Security` de Q2827003ATGSS001). Sense aquesta autorització de firma,
qualsevol intent real de consulta contra SCDCPAJU sembla condemnat a
fallar en aquest entorn, independentment del formulari que s'ompli.

**Estat:** RESOLT — l'usuari ha canviat `e2e/.env.e2e` a
`E2E_CONSULTA_SERVEI_CODI=Q2827003ATGSS001`
(`E2E_CONSULTA_PROCEDIMENT_ID=900201` ja és vàlid per a tots dos, ja que
el mateix procediment E2EPROC01 té ambdós serveis associats).

## FIXAT — Infra: `core_parametro_configuracion.keystoreFile`/`keystorePass` havien tornat al placeholder `'.'` (regressió del bug ja documentat més amunt), trencant TOTA consulta real (signatura invàlida)

**On:** qualsevol enviament real de consulta (simple o múltiple) contra
QUALSEVOL servei, amb QUALSEVOL document (fins i tot el document normal
de prova `SCSP_FAKE_SUCCESS_DOC`, que sempre havia funcionat). Descobert
investigant per què `èxit 1`/`èxit 2` a `global-setup.ts` havien
començat a fallar també (no només la consulta "error").

**Símptoma:** `es.scsp.common.exceptions.ScspClientInterceptorException:
Firma no válida PBL...`, amb aquesta traça més avall al log de JBoss:
```
ERROR [es.scsp.common.signature.ScspWssInterceptor] Error en la validacion de la securizacion de la respuesta.:
org.springframework.ws.client.support.destination.DestinationProvisionException: Error generando crypto;
nested exception is java.lang.StringIndexOutOfBoundsException: String index out of range: -9
```
— exactament el mateix `StringIndexOutOfBoundsException` documentat al
bug "Infra: property `es.caib.pinbal.scsp.keystoreFile`/`keystorePass`..."
més amunt en aquest mateix fitxer (`ScspCryptoFactoryBean` construït amb
`valor='.'`).

**Causa arrel:** l'Oracle d'aquest entorn e2e és efímer (sense volum
persistent, vegeu CLAUDE.md/`docker-compose.e2e-oracle.yml`): cada cop
que el CONTENEDOR ORACLE es recrea/reinicia, `initial_data_scsp.sql`
torna a sembrar `keystoreFile`/`keystorePass` amb el placeholder `'.'`.
`ScspPropertyPlaceholderConfigurer.moveToDatabase()` (el fix original,
vegeu més amunt) només corregeix aquesta fila UN COP, a l'arrencada de
`pinbal-ejb.jar`. Si l'Oracle es reinicia SENSE que el contenidor Pinbal
també ho faci alhora (p.ex. Oracle es reinicia sol per qualsevol motiu,
o algú el recrea per separat), Pinbal continua executant-se amb el fix ja
aplicat en memòria als beans YA CONSTRUÏTS, però la fila de BD queda de
nou al placeholder — i qualsevol NOVA petició SCSP que necessiti
reconstruir/recarregar la configuració de crypto (confirmat que passa
per petició, no només a l'arrencada) torna a fallar.

**Fix aplicat (dades):**
```sql
UPDATE core_parametro_configuracion SET valor = 'file:/opt/webapps/keystores/interoperabilitat.jks' WHERE nombre = 'keystoreFile';
UPDATE core_parametro_configuracion SET valor = 'tecnologies' WHERE nombre = 'keystorePass';
```
(valors llegits de `.env`: `SCSP_KEYSTORE_FILE`/`SCSP_KEYSTORE_PASS`,
injectats a `docker-compose.yml` com
`es.caib.pinbal.scsp.keystoreFile`/`keystorePass`). Confirmat que el fix
s'aplica a l'ACTE, sense reiniciar cap contenidor (una consulta real
enviada immediatament després de l'`UPDATE` ja funciona correctament).

**Nota operativa (no és un bug de codi, és una fragilitat d'aquest
entorn concret):** si torna a passar "Firma no válida"/consultes reals
que fallen totes de cop, comprovar primer aquestes dues files abans de
sospitar de cap altra cosa — és el primer que cal mirar quan l'Oracle
efímer d'aquest entorn s'ha pogut reiniciar independentment del
contenidor Pinbal.

## FIXAT (test) — El document disparador d'error del fake SCSP (`SCSP_FAKE_ERROR_TRIGGER_DOC = '00000000ERR'`) s'enviava sempre com a tipus 'NIF', rebutjat pel formulari abans d'arribar mai a SCSP

**On:** `global-setup.ts` (consulta simple "error", warning present a
TOTA execució de la suite des que existeix aquest fitxer) i el test
"una consulta amb el document disparador d'error queda en estat Error"
(`consultes-simples.spec.ts`, mai exercitat de veritat fins ara perquè
depenia de `E2E_CONSULTA_SERVEI_CODI`, no configurat abans a
`e2e/.env.e2e`).

**Causa arrel:** `'00000000ERR'` no és un NIF de format vàlid (11
caràcters, 3 lletres finals). `DocumentIdentitatValidator` (back) aplica
una validació de checksum estricta per a NIF/DNI/NIE/CIF, però per a
`Passaport` retorna sempre `true` sense cap comprovació de format
(`else { // Pasaporte \n return true; }`). Com que tant
`global-setup.ts` com el test del delegat enviaven aquest document
sempre amb `titularDocumentTipus='NIF'`, el formulari el rebutjava
sempre amb "Número de document invàlid" ABANS d'arribar mai a SCSP — la
consulta no es creava mai, independentment de qualsevol altra cosa
(entorn, servei, fake SCSP...).

**Fix aplicat:** `global-setup.ts` (`crearConsultaSimple`, nou paràmetre
`documentTipus`, usat amb `'Passaport'` només per a la crida amb
`SCSP_FAKE_ERROR_TRIGGER_DOC`) i
`consultes-simples.spec.ts` (mateix canvi, `'Passaport'` en lloc de
`'NIF'`) ara seleccionen `'Passaport'` per a aquest document concret.

**Comportament de l'aplicació (aclarit per l'usuari, NO és un bug):** un
cop el document arriba a SCSP amb el tipus correcte i la firma és vàlida
(fix anterior), la resposta simulada d'error (`CodigoEstado=9999`,
"Error simulat pel fake SCSP per a proves") es mostra com un MISSATGE
D'ERROR EN LÍNIA al mateix formulari (`La consulta ha retornat un error:
[9999]...`) i l'aplicació NO redirigeix al llistat — deixa el formulari
obert perquè l'usuari en pugui modificar les dades i reintentar (el que
generaria una consulta NOVA, no la mateixa). Inicialment vam interpretar
erròniament que la NO-redirecció volia dir que la consulta no s'havia
creat; NO és així: la consulta SÍ que es crea (amb `estat=Error`) i és
consultable normalment al llistat, senzillament cal anar-hi expressament
a comprovar-ho en lloc de confiar en cap redirecció automàtica (que aquí
mai es produeix, a diferència del cas d'èxit).

**Fix aplicat (test):** el test del delegat ja NO comprova ni depèn de
cap redirecció: després d'enviar el formulari, navega directament al
llistat de consultes simples i hi filtra pel document disparador
d'error, comprovant que la fila (la creada per aquesta mateixa execució)
mostra l'estat "Error". El `test.skip` condicionat a la redirecció s'ha
eliminat.

**Verificat:** 4 execucions consecutives en verd; el fitxer complet del
delegat passa de 20 passats / 1 saltat a **21 passats / 0 saltats**.

## FIXAT (test) — Els tests "vista prèvia del justificant"/"informació de l'arxiu" (delegat) sempre se saltaven: els botons viuen dins un tab de Bootstrap mai obert

**On:** `consultes-simples.spec.ts` (delegat), tests "es pot veure la
vista prèvia del justificant" i "es pot veure la informació de l'arxiu
del justificant, si està disponible". Es saltaven SEMPRE (100% de les
execucions), fent-se passar per "el justificant encara no disponible en
aquest entorn" — semblava una limitació de dades/entorn, però no ho era.

**Causa arrel:** `consultaInfo.jsp` (compartit pel detall del delegat)
organitza el contingut en tabs de Bootstrap (`<ul class="nav nav-tabs">`,
`data-toggle="tab"`): `#mostrarVistaPrevia` i `#justificantInfo` viuen
tots dos dins `<div class="tab-pane" id="descarregaJustificantsTab">`
(el tab "Justificant"), amagat per defecte — només el primer tab, "Dades
genèriques", és actiu en carregar la modal. `obrirDetallConsultaTramitada`
(el helper compartit pels tres tests d'aquest bloc) mai feia clic al tab
"Justificant" abans de comprovar la visibilitat d'aquests botons, així
que `.isVisible()` hi retornava sempre `false` — independentment de si
`PBL_E2E_SIMPLE_OK` tenia justificant disponible o no (que sí en té: el
tab EN SI mateix ja és condicional a
`justificantEstatOk`/`Pendent`/`Error` a la JSP, així que si no hi és,
tampoc caldria fer-hi res).

**Fix aplicat:** `obrirDetallConsultaTramitada` ara fa clic al tab
"Justificant" (`frame.getByRole('tab', { name: /justificant/i })`) just
abans de retornar el `frame`, només si el tab existeix (si no hi és, cap
canvi de comportament: els tests que en depenen continuaran saltant-se,
correctament, per manca real de justificant).

**Troballa addicional (mateix patró ja documentat per a l'administrador,
més amunt en aquest fitxer):** un cop el tab s'obre de veritat, el test
"vista prèvia del justificant" topava amb la mateixa violació de "strict
mode" entre `#pdf-container`/`#error-container` (conviuen sempre al DOM,
un amagat amb `display:none`). Arreglat amb el mateix pseudo-selector
`:visible` ja usat a `consultes-realitzades.spec.ts` (administrador).

**Verificat:** 3 execucions consecutives en verd d'ambdós tests (abans,
sempre saltats); el fitxer complet del delegat passa de 18 tests
executats (3 saltats) a 20 (només 1 saltat, l'anterior sobre el document
disparador d'error).

## FIXAT (test) — `configurar-cache.spec.ts`: "el llistat de caches es mostra correctament" fallava si era el primer test de tota la suite

**On:** `administrador/configurar-cache.spec.ts`, només en execucions de
tota la suite en paral·lel (mai en aïllat), i no sempre — depenia de
l'ordre d'execució dels workers.

**Causa arrel:** les caches (ehcache/Spring Cache) es registren de
manera DINÀMICA al `CacheManager` en el primer ús real d'un mètode
`@Cacheable` (`CacheHelper.getAllCaches()` -> `cacheManager.
getCacheNames()`), no n'hi ha cap predeclarada. Si aquest test s'executa
abans que cap altre camí de l'aplicació hagi disparat un `@Cacheable`,
el llistat surt buit.

**Fix aplicat (test):** igual que ja feien els altres dos tests
d'aquest mateix fitxer, si el llistat surt buit es força que se'n
registri alguna accedint, com a delegat, al llistat de consultes
simples (`/consulta`) abans de tornar a comprovar.

**Verificat:** estable en diverses execucions completes de la suite en
paral·lel.

## FIXAT (test) — `usuari-configuracio.spec.ts`: condició de carrera d'idioma entre tests de fitxers diferents corrent en paral·lel

**On:** `administrador/usuari-configuracio.spec.ts` ("es pot canviar
l'idioma per defecte..."), detectat perquè trencava en cascada tests
d'ALTRES fitxers que depenen de textos en català (p.ex. capçaleres de
taula), amb missatges com `Received string: "Núm. petición Fecha
Procedimiento..."` (castellà en lloc de català).

**Causa arrel:** el test canviava temporalment l'idioma de l'usuari
`delegatPage` a "ES", en feia la comprovació, i el restaurava a "CA"
dins un `finally`. El `finally` garanteix la restauració EVENTUAL, però
NO evita que un altre test —corrent en PARAL·LEL en un altre worker,
amb el MATEIX usuari delegat (`delegatPage` és compartit per 5+ fitxers
més)— l'observi TRANSITÒRIAMENT en castellà mentre encara no s'ha
restaurat. `pbl_usuari.idioma` és estat persistit i compartit entre
tots els tests que fan servir aquell usuari, no aïllat per test.

**Fix aplicat (test):** tot el fitxer es va canviar de `delegatPage` a
`auditorPage`, l'ÚNIC rol que cap altre fitxer de la suite fa servir
(verificat amb `grep -rl "auditorPage" e2e/tests/`), fent-lo servir com
a fixture EXCLUSIVA. Com que cap altre test fa login amb aquest usuari,
és impossible que n'observi l'idioma transitòriament canviat.

**Nota general (patró reutilitzable):** qualsevol test que muti estat
PERSISTIT i COMPARTIT per usuari (idioma, permisos, configuració...) —
encara que ho restauri sempre amb `finally`— és insegur sota execució
en paral·lel si un altre test pot fer login amb el mateix usuari
mentrestant. La solució no és "restaurar millor", és donar-li al test
un usuari/fixture que cap altre test toqui.

**Verificat:** varies execucions completes de la suite en paral·lel
sense cap fallada relacionada amb l'idioma.

## FIXAT (test) — Condició de carrera de permisos entre `representant/procediments.spec.ts` i `representant/usuaris.spec.ts` (mateix patró que l'anterior)

**On:** el test "Permisos" de `representant/usuaris.spec.ts` (afegir/
denegar/esborrar seleccionats/esborrar TOTS els permisos procediment-
servei), intermitent només en execucions completes en paral·lel.

**Causa arrel:** exactament el mateix patró que el bug d'idioma anterior
però amb permisos: tant aquest test com `representant/procediments.
spec.ts` (secció "Gestió de serveis per procediment") concedeixen/
esborren permisos per al mateix usuari fix compartit
(`E2E_USER_ACTIU`). L'acció "esborrar TOTS els permisos" d'aquest test
és global per usuari (esborra QUALSEVOL permís seu, no només els creats
per aquest test), així que si l'altre fitxer, corrent en paral·lel en
un altre worker, concedeix un permís just abans o durant, el recompte
esperat no quadra — o pitjor, "esborrar tots" esborra el permís que
l'altre test acaba de crear.

**Fix aplicat (dades + test):** nou usuari fix `E2E_USER_PERMISOS`,
sembrat via Liquibase (context `e2e`) EXCLUSIVAMENT per a aquest test
(vegeu `USUARI_FIX_PERMISOS_CODI` a `e2e/utils/env.ts` i el canvi de
`usuari` a `RepresentantUsuariPermisosPage` dins el test). Cap altre
fitxer el toca.

**ATENCIÓ — error propi durant aquest fix, documentat perquè no torni a
passar:** el primer intent va afegir les files noves (`pbl_usuari` +
`pbl_entitat_usuari`) com un `insert:` addicional DINS els changesets
`e2e-seed-usuaris-fixos`/`e2e-seed-entitat-usuari-fixos` JA EXISTENTS i
JA APLICATS. Liquibase calcula i desa un checksum de cada changeset
(per `id`+`author`+fitxer) en aplicar-lo, i el revalida a cada arrencada
posterior; modificar el contingut d'un changeset ja aplicat el fa
fallar amb `ValidationFailedException`, que en aquest projecte —com que
`SpringLiquibase` s'executa durant l'arrencada de l'EJB context
(`EjbContextStartup`)— trenca el desplegament SENCER (`pinbal-back.war`,
`pinbal-api-interna.war` i `pinbal-api-externa.war` deixen d'arrencar,
encara que JBoss mateix reporti "started (with errors)" en lloc de
petar del tot). **Regla:** per a QUALSEVOL dada nova a
`00_e2e_seed_data.yaml`, sempre un changeset NOU amb `id` propi —mai
afegir un `insert:` a un changeset existent que ja s'hagi pogut aplicar
en algun entorn.** El fix correcte va ser revertir els dos changesets al
seu contingut original i afegir un changeset nou (`e2e-seed-usuari-
permisos`) amb les dues insercions.

**Verificat:** el test "Permisos" passa de manera estable en execucions
completes de la suite en paral·lel; sense `ValidationFailedException` als
logs de JBoss en cap redesplegament posterior.

## FIXAT (test/infra) — Timeouts intermitents (`waitForResponse`/`waitForEvent('download')`) en execucions completes de la suite en paral·lel

**On:** diversos tests de llistat i de descàrrega de justificants
(p.ex. `administrador/gestionar-organs-gestors.spec.ts`, `administrador/
consultes-realitzades.spec.ts`), únicament en execucions completes en
paral·lel amb el nombre de workers per defecte de Playwright (un per
nucli — 8 en una màquina de 16 nuclis), MAI en aïllat ni amb pocs
workers. És l'origen més probable de l'observació original de l'usuari:
"amb `--ui` (interactiu, menys concurrència) tot acaba passant, sense
`--ui` (headless, paral·lelisme per defecte) algun test falla".

**Causa arrel:** aquest entorn e2e comparteix una ÚNICA instància real
de JBoss + Oracle entre TOTS els workers de Playwright — no n'hi ha una
per worker. Amb 8 workers fent peticions concurrents, algunes operacions
normals i correctes (càrrega d'un DataTable, generació d'un justificant
—renderitza una plantilla ODT i la firma amb la clau privada local—)
poden trigar, de manera intermitent, més dels 20s/10s de timeout
originalment previstos, sense que hi hagi cap bug ni de test ni
d'aplicació.

**Fix aplicat:**
- `playwright.config.ts`: `workers` limitat a `4` en local (abans,
  `undefined` = per nucli). Verificat empíricament que amb 4 workers la
  contenció desapareix gairebé del tot.
- `e2e/utils/datatable.ts`: `waitForInitialDataTableLoad`/
  `waitForDataTableReload` pugen de 20s a 30s de timeout, com a marge
  addicional per a la contenció residual que encara pot quedar amb 4
  workers.
- `consultes-realitzades.spec.ts`: els dos tests de "Descàrrega del
  justificant" (simple i múltiple) passen de 10s (per defecte,
  `actionTimeout`) a 30s explícits a `page.waitForEvent('download', ...)`.

**Verificat:** 5 execucions consecutives completes en verd (95/95) amb
la configuració per defecte (`npm run test:e2e`), després d'haver
reproduït la intermitència diverses vegades abans del fix.

## FIXAT (infra, no és bug de codi) — `core_parametro_configuracion.keystoreFile`/`keystorePass` tornen al placeholder `'.'` si l'Oracle efímer es recrea, trencant TOTA consulta real amb "Firma no válida"

**On:** reincidència del bug ja documentat més amunt ("Infra:
`core_parametro_configuracion.keystoreFile`/`keystorePass`..."),
provocada aquest cop per mi mateix en recrear el contenidor
`pinbal2_oracle_1` (Oracle, efímer, sense volum persistent) per separat
del contenidor `pinbal2_pinbal_1` per depurar un problema de Liquibase
no relacionat.

**Símptoma:** absolutament TOTA consulta real creada via formulari (no
només la del document disparador d'error) fallava amb
`ScspClientInterceptorException: Firma no válida PBL...` — incloent-hi
consultes que `global-setup.ts` reportava com a "creada correctament"
(el missatge de "correctament" només comprova que el formulari ha
redirigit, no que la consulta s'hagi persistit de veritat; amb aquest
bug actiu NO es persisteix cap fila nova a `pbl_consulta`, verificat per
consulta SQL directa). Simptomàticament indistingible d'una consulta
que falla per una altra causa: només es distingeix mirant el missatge
exacte i el log de JBoss.

**Causa arrel:** vegeu l'entrada original més amunt d'aquest fitxer.
Recordatori clau: el fix de `ScspPropertyPlaceholderConfigurer.
moveToDatabase()` només corregeix la fila un cop, a l'arrencada
del `pinbal-ejb.jar`. Si en algun moment es recrea NOMÉS el contenidor
Oracle (o abans, com aquí, si es recrea Oracle després d'haver-lo
reiniciat sol i Pinbal encara no ha tornat a arrencar amb l'Oracle nou),
la fila torna al placeholder `'.'` i Pinbal segueix executant-se sense
tornar a aplicar el fix.

**Fix aplicat (dades, sense reiniciar cap contenidor):**
```sql
UPDATE core_parametro_configuracion SET valor = 'file:/opt/webapps/keystores/interoperabilitat.jks' WHERE nombre = 'keystoreFile';
UPDATE core_parametro_configuracion SET valor = 'tecnologies' WHERE nombre = 'keystorePass';
COMMIT;
```

**Nota operativa (reforça la de l'entrada original):** en aquest entorn
concret, sempre que es recreï/reiniciï el contenidor Oracle, cal
recrear/reiniciar TAMBÉ el contenidor Pinbal a la vegada (`podman rm -f
pinbal2_pinbal_1 pinbal2_oracle_1` seguit d'un únic `podman-compose up
-d` amb els dos), no per separat — així `ScspPropertyPlaceholderConfigurer`
torna a aplicar el fix a l'arrencada. Si per qualsevol motiu s'han
desincronitzat, l'`UPDATE` SQL de dalt és la solució immediata sense
haver de reiniciar res.

## FIXAT (app, exposat per un test) — Condició de carrera real a `avisList.jsp`: seleccionar diversos avisos i aplicar una acció massiva just després pot ometre l'últim seleccionat

**On:** `administrador/configurar-avisos.spec.ts`, test "activar/
desactivar massiu actualitza diversos avisos alhora". Reproduït només
sota càrrega (execució completa de la suite en paral·lel, mai en
aïllat ni amb un sol worker) — símptoma d'una condició de carrera real
del client JS, no d'un bug de test.

**Causa arrel:** a `avisList.jsp`, cada clic sobre una fila
(`.row-selector`) actualitza la icona de manera SÍNCRONA però persisteix
la selecció al servidor amb `$.post('avis/selection/add', {ids: ...})`
"fire-and-forget" (no s'espera la resposta abans de deixar interactuar
l'usuari). Els botons d'acció massiva (`#bulk-enable`/`#bulk-disable`)
NO envien els ids seleccionats al cos de la petició — el servidor els
llegeix de la selecció ja persistida a sessió. Si l'usuari (o el test)
obre el menú d'accions massives i hi clica abans que la petició de
selecció de l'últim avís marcat hagi arribat al servidor, aquell avís
queda fora de l'acció massiva encara que el checkbox el mostri
seleccionat al client. Sota velocitat normal la finestra de la
condició de carrera és mil·lisegons i mai es veu; sota càrrega (diversos
workers de Playwright competint pel mateix JBoss) es torna visible.

**Fix aplicat (test, NO s'ha tocat `avisList.jsp`):**
`AvisosPage.seleccionar()` ara espera explícitament la resposta de
`avis/selection/add|remove` abans de continuar, eliminant la condició
de carrera al test. Es documenta aquí com a bug real de l'aplicació
—no només de test— perquè un usuari humà fent clics ràpids en un
entorn amb latència (xarxa lenta, servidor carregat) podria patir el
mateix problema; un fix complet a `avisList.jsp` faria que els botons
d'acció massiva esperessin totes les peticions de selecció pendents (o,
més senzill, que enviessin els ids seleccionats directament al cos de
la petició en lloc de dependre d'estat de sessió) abans de permetre
clicar-los.

**Verificat:** 3 execucions consecutives en verd del test aïllat amb 3
workers; 5 execucions consecutives completes en verd (95/95) de tota la
suite.
