# Errors resolts arran de la incorporació dels jocs de proves (juliol-agost 2026)

Aquest document resumeix els errors detectats i corregits durant les
darreres dues setmanes, mentre s'anaven afegint els nous jocs de proves al
projecte: els tests unitaris (`ServeiBus`/`OrganGestor` i altres, 24-28 de
juliol) i, sobretot, la suite e2e amb Playwright contra un entorn real
(JBoss + H2/Oracle + Keycloak, 31 de juliol - 1 d'agost).

La majoria dels errors van sortir a la llum precisament perquè, fins ara,
ningú havia exercitat aquests fluxos de manera automatitzada i repetida.
El detall tècnic complet de cada un (traces, fitxers i línies exactes,
consultes SQL de verificació...) es manté a
[`pinbal-back/src/main/reactapp/pinbal-back/e2e/BUGS_APLICACIO.md`](../pinbal-back/src/main/reactapp/pinbal-back/e2e/BUGS_APLICACIO.md);
aquest document n'és un resum organitzat per a referència ràpida.

Es distingeixen tres tipus de problema:

- **Bugs reals de l'aplicació**: comportament incorrecte que un usuari real
  podria patir, independentment dels tests.
- **Fragilitats de l'entorn de proves**: configuració/infraestructura que
  calia ajustar perquè l'entorn e2e (efímer, sense els serveis externs
  reals) es comportés de manera fiable — no afecten un desplegament real.
- **Bugs dels propis tests**: la suite assumia un comportament o un
  marcatge HTML incorrecte; l'aplicació ja funcionava bé.

---

## 1. Bugs reals de l'aplicació

### Autenticació i sessió

- **"Desconnectar" trencava el següent login amb "Bad Request"**
  (`UsuariController.logout()`). El logout no tancava mai la sessió SSO a
  Keycloac ni la sessió local correctament (només buidava cookies "a mà",
  sense conèixer-ne el `path` real), deixant cookies fantasma que
  confonien el següent login. Fix: logout real contra Keycloak
  (`end_session_endpoint` amb `id_token_hint`) + invalidació neta de la
  sessió, sense manipular cookies manualment.
- **Delegat: `GET /usuari/configuracio` retornava 403.** El controlador
  cridava mètodes de servei reservats a Administrador/Representant/Auditor
  per a qualsevol altre rol, incloent el Delegat. Fix: usar les variants
  de servei ja pensades per al Delegat.
- **Representant: filtrar l'llistat d'òrgans gestors per codi retornava
  403.** El controlador cridava incondicionalment un mètode de servei
  només-Administrador per obtenir una entitat per defecte. Fix: només
  cridar-lo si l'usuari és administrador; en cas contrari, usar l'entitat
  ja activa del Representant.
- **La modal amb iframe no es tancava mai després de "Guardar"** en cap
  pantalla que la fa servir (`ModalHelper`, mecanisme general, no
  específic d'una pantalla). El mecanisme que detecta "és una petició de
  modal" depenia d'una segona passada dels interceptors de Spring que, de
  fet, mai s'arribava a produir. Fix: marcar-ho abans del `forward()`
  intern, no després.

### Consultes i justificants

- **Descàrrega de justificants: stack trace cru en lloc d'un missatge
  d'error net.** Un `catch (Exception)` massa ampli a
  `ConsultaAdminController` capturava també errors reals de generació i
  els convertia en una `ConsultaNotFoundException` no gestionada. Fix:
  capturar només l'excepció concreta que calia.
- **El Delegat veia el botó "Exportar a Excel" al llistat de consultes
  però no tenia permís per fer-lo servir** (403 silenciós). Decisió de
  producte: el Delegat ha de poder exportar el seu propi llistat; s'ha
  afegit aquest rol a la comprovació dels dos controladors implicats
  (consultes simples i múltiples).
- **Plugin d'arxiu digital real trencat per una migració de llibreria a
  mitges**: `PluginHelper` esperava la interfície v3 de
  `pluginsib-arxiu-api`, però la implementació real (`ArxiuPluginCaib`)
  encara era v2 — qualsevol intent d'activar-lo en qualsevol entorn hauria
  fallat amb `ClassCastException` en temps d'execució. Fix: completada la
  migració a v3 a tots els mòduls implicats (imports i dependència de
  `pom.xml`), sense canvis de lògica.
- **Gestió de camps/previsualització d'un servei petava si s'havia
  esborrat l'únic XSD de dades específiques** (`FileNotFoundException` no
  gestionat). A més, esborrar l'últim XSD des de la UI no desactivava el
  flag `activaGestioXsd`, deixant el servei en un estat inconsistent. Fix:
  comprovar l'existència del fitxer abans d'obrir-lo i recórrer al
  fallback de classpath si no hi és.
- **`idpeticion` "enverinava" el fil de JBoss per a peticions NO
  relacionades**: la llibreria SCSP no fa `rollback()` de la transacció
  quan falla la generació d'un `idpeticion`; el fil de JBoss reutilitzat
  per la següent petició hereta la transacció encallada i falla amb
  `IllegalStateException`/`NullPointerException` aparentment inexplicables
  i sense relació amb la petició real. Rellevant també en producció
  (Oracle) si mai hi ha un problema puntual de BD en aquest procediment.
  Fix: capturar-ho i fer `rollback()` explícit abans de re-llançar.

### Formularis i textos

- **Modal "Migrar" d'un servei petava amb `No message found under
  code...`**: `procedimentServeiMigrar.jsp` feia servir per error
  `optionTextKeyAttribute` (tracta el text com una clau d'i18n) en lloc
  d'`optionTextAttribute` (text pla) per a un camp que no és cap clau de
  traducció.
- **La plantilla CSV de consulta múltiple donava sempre 404** en
  descarregar-la des del formulari real: la crida es feia amb una URL que
  començava per `/`, perdent el context path de l'aplicació
  (`/pinbalback`) en resoldre-la.

---

## 2. Fragilitats de l'entorn de proves (infraestructura/configuració)

Cap d'aquests és un bug de codi de l'aplicació en si, però van impedir que
qualsevol consulta real es pogués completar en aquest entorn fins que es
van detectar i corregir:

- **`keystoreFile`/`keystorePass` no arribaven mai via System property**:
  les variables d'entorn amb punts al nom (`es.caib.pinbal.scsp.*`) no es
  tradueixen mai a `-D` reals de la JVM; calia llegir-les també via
  `System.getenv()`. Sense això, `core_parametro_configuracion` es quedava
  amb el placeholder `'.'` inicial i **tot** el desplegament fallava en
  arrencar (`StringIndexOutOfBoundsException`).
  - **Regressió trobada dues vegades** durant aquestes dues setmanes:
    com que l'Oracle d'aquest entorn és efímer (sense volum persistent),
    si es recrea el contenidor Oracle sense recrear alhora el de Pinbal,
    la fila torna al placeholder i **totes** les consultes reals tornen a
    fallar amb "Firma no válida". Solució ràpida documentada (`UPDATE`
    SQL directe) i recordatori operatiu: recrear sempre els dos
    contenidors junts.
- **Pool de connexions JBoss esgotat sota càrrega concurrent** (179+
  ocurrències d'`IJ000655: No managed connections available`): el
  `max-pool-size` per defecte (10/5/10) era insuficient amb diverses
  suites Playwright executant-se en paral·lel. Parametritzat via variables
  d'entorn i pujat a 30 específicament per a l'entorn e2e.
- **Validació de certificat SCSP bloquejava TOTA consulta real**: el
  mòdul `ValidarCertificado` fa una comprovació de revocació que, sense
  connectivitat real a @firma/OCSP, sempre retorna "revocat". Desactivat
  només per al context `e2e`.
- **Faltaven dades de mostra necessàries** per completar el flux complet
  de generació de justificant (missatge SCSP de resposta, autorització de
  firma per organisme cessionari, permisos ACL sobre
  `pbl_procediment_servei` per a delegat/representant) — sense elles,
  cap consulta de mostra s'arribava a crear ni cap justificant es podia
  generar, emmascarant altres problemes reals darrere de "no hi ha dades".
- **Timeouts intermitents només en execucions completes en paral·lel**
  (mai en aïllat): l'entorn comparteix una única instància de JBoss +
  Oracle entre tots els workers de Playwright; amb el nombre de workers
  per defecte (un per nucli), algunes operacions normals (càrrega d'un
  llistat, generació d'un justificant firmat) podien trigar més del
  previst. Corregit limitant els workers en local i pujant lleugerament
  els timeouts de xarxa afectats.

---

## 3. Bugs dels propis tests (no de l'aplicació)

La majoria són condicions de carrera pròpies de Playwright/del patró de
modals amb iframe de l'aplicació, o suposicions incorrectes sobre el
marcatge HTML:

- **Condicions de carrera genèriques del patró de modal amb iframe**:
  comprovar la visibilitat de l'`<body>` d'un iframe just després
  d'assignar-li `src` es podia satisfer contra l'estat transitori
  `about:blank`, abans que el contingut real hagués carregat. Corregit al
  helper comú `modalFrame()` i, on calia, esperant explícitament la
  resposta de xarxa abans de continuar.
- **Condició de carrera en carregar un llistat (DataTable)**: la petició
  ajax es dispara abans de l'esdeveniment `load` de la navegació, de
  manera que en un servidor prou ràpid l'escolta es podia registrar tard.
  Corregit registrant l'escolta abans de navegar/fer clic, en lloc de
  després.
- **Botons/enllaços localitzats amb el selector equivocat** en diverses
  pantalles (un botó "Accions" sense la classe CSS esperada, "Denegar
  accés" és un botó de formulari i no un enllaç, un botó de confirmació
  clonat fora de l'iframe pel mecanisme de modals...).
- **Tests que assumien "la primera fila" o "l'entitat sempre és dins del
  desplegable"** en lloc de filtrar per un identificador conegut: un cop
  l'entorn va començar a generar dades reals (consultes creades pel propi
  `global-setup`), aquestes assumpcions van deixar de ser vàlides.
  Corregit filtrant sempre per l'identificador de la consulta/entitat de
  mostra coneguda.
- **Pol·lució de dades entre execucions**: alguns tests deixaven estat
  persistit "brut" si fallaven a mig camí (una entitat de prova activada,
  permisos concedits no netejats), trencant en cascada altres tests
  posteriors. Corregit envoltant la neteja en `try`/`finally` i afegint
  neteges defensives a l'inici dels tests afectats.
- **Condicions de carrera entre fitxers de test diferents corrent en
  PARAL·LEL**, mutant el mateix estat persistit i compartit (idioma d'un
  usuari, permisos d'un usuari fix): encara que cada test restaurava el
  valor original en un `finally`, un altre test podia observar-lo
  transitòriament canviat mentre corria en paral·lel. Corregit donant als
  tests afectats un usuari/fixture exclusiu que cap altre fitxer fa
  servir.
- **Condició de carrera real de l'aplicació, exposada per un test**: a la
  gestió d'avisos, seleccionar diversos avisos i aplicar de seguida una
  acció massiva podia ometre l'últim seleccionat, perquè la selecció es
  persisteix al servidor amb una petició "fire-and-forget" que l'acció
  massiva no espera. Corregit al test esperant explícitament aquesta
  petició; es documenta com a possible millora pendent a l'aplicació
  mateixa.
- **Errors puntuals de disseny del test** (per exemple: el disparador
  d'error del fake SCSP s'enviava sempre amb un tipus de document que el
  formulari rebutjava abans d'arribar mai a SCSP; tests que se saltaven
  sempre perquè els botons vivien dins una pestanya de Bootstrap mai
  oberta).

---

## Resultat

Amb totes aquestes correccions aplicades, la suite completa de proves e2e
(95 tests) s'executa de manera consistent i reproduïble tant en mode
interactiu (`--ui`) com en mode headless amb l'execució en paral·lel per
defecte, sense fallades intermitents, verificat amb diverses execucions
consecutives completes en verd.

Per al detall tècnic exacte de cada punt (fitxers, línies, traces,
consultes de verificació), vegeu
[`e2e/BUGS_APLICACIO.md`](../pinbal-back/src/main/reactapp/pinbal-back/e2e/BUGS_APLICACIO.md).
