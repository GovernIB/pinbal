# Tests E2E (Playwright)

Aquesta suite prova la interfície **JSP** actual de PINBAL (`pinbal-back`,
servida per JBoss), no l'aplicació React d'aquest projecte. S'ha ubicat aquí
perquè és on ja hi ha l'eina de tooling Node/npm del mòdul, però els tests
naveguen directament a l'aplicació desplegada.

## Requisits

Cal un entorn PINBAL real en marxa (JBoss + base de dades + Keycloak), per
exemple aixecant-lo amb `docker-compose up` des de l'arrel del repositori
(vegeu `docker-compose.yml` i `.env` allà), o contra un entorn de
desenvolupament/proves ja existent.

### Arrencada automatitzada (recomanat)

`scripts/e2e/run-e2e.sh` (a l'arrel del repositori) automatitza tot el cicle:
aixeca l'aplicació amb `docker compose`/`docker-compose`/`podman-compose`,
arrenca el servidor fake de SCSP ([FAKE_SCSP_SERVER.md](../../../../../../FAKE_SCSP_SERVER.md))
i executa aquesta suite. Vegeu `scripts/e2e/run-e2e.sh --help` per a totes
les opcions (`--point-scsp-urls` per redirigir automàticament els serveis
SCSP coberts pel fake, `--down` per aturar-ho tot en acabar, etc.).

## Configuració

1. Copieu `e2e/.env.e2e.example` a `e2e/.env.e2e` i ompliu-lo:
   - `E2E_BASE_URL`: URL base de PINBAL (per defecte `http://localhost:8080/pinbalback`).
   - Credencials Keycloak per als rols que vulgueu provar
     (`E2E_ADMIN_*`, `E2E_DELEGAT_*`, `E2E_REPRESENTANT_*`, `E2E_AUDITOR_*`).
     Els tests d'un rol sense credencials configurades es marquen com a
     "skipped" automàticament (no fallen).
   - Opcionalment, `E2E_CONSULTA_SERVEI_CODI` (i `E2E_CONSULTA_PROCEDIMENT_ID`)
     per habilitar el test de consulta simple contra el fake de SCSP; vegeu
     "Cobertura actual i ampliació" més avall.
2. Instal·leu els navegadors de Playwright (només cal la primera vegada):
   ```bash
   npx playwright install chromium
   ```

## Executar els tests

```bash
npm run test:e2e            # execució headless, tots els navegadors configurats
npm run test:e2e:headed     # amb finestra de navegador visible (útil per depurar)
npm run test:e2e:ui         # mode interactiu de Playwright (recomanat en desenvolupament)
npm run test:e2e:report     # obre l'últim informe HTML generat
```

Per executar només un fitxer o un test concret:

```bash
npx playwright test e2e/tests/entitat-crud.spec.ts
npx playwright test -g "es pot filtrar per codi"
```

## Estructura

```
e2e/
  tests/            Especificacions (*.spec.ts), una per funcionalitat/pantalla
  utils/
    env.ts          Lectura de credencials/config per variables d'entorn
    auth.ts         login()/logout() contra el formulari de Keycloak
    fixtures.ts      Fixtures de Playwright (adminPage, delegatPage, ...) ja autenticades
    modal.ts        Helpers per a les modals amb iframe de PINBAL (webutil.modal.js)
    datatable.ts    Helpers per a les taules DataTables amb processament al servidor
    messages.ts     Helpers pels missatges d'èxit/error (#contingut-missatges)
```

### Patró de modal amb iframe

Els formularis de crear/editar de PINBAL s'obren en una modal Bootstrap que
carrega el contingut real dins un `<iframe>`; els botons ("Guardar",
"Cancel·lar") es clonen fora de l'iframe, al peu de la modal. Useu sempre:

```ts
await page.locator('#btNovaEntitat').click();
const frame = await modalFrame(page);           // FrameLocator del formulari
await frame.locator('#codi').fill('ENT01');
await clickModalFooterButtonById(page, 'btGuardarEntitat'); // botó FORA de l'iframe
await waitForModalClosed(page);
```

### Patró de DataTable (llistats)

Els llistats usen jQuery DataTables amb `serverSide: true`: cada filtratge o
canvi de pàgina dispara una petició `POST` a una URL que conté `/datatable`.
Per evitar condicions de carrera, envolteu l'acció que provoca la recàrrega:

```ts
await waitForDataTableReload(page, async () => {
    await page.locator('#filtrar').click();
});
```

## Identificadors afegits al codi JSP per facilitar les proves

S'han afegit els següents `id` (sense alterar cap comportament) perquè els
selectors dels tests no depenguin de textos traduïbles ni de l'estructura
CSS/select2:

| Fitxer | Element | id afegit |
|---|---|---|
| `decorators/default.jsp` | Enllaç "Configuració" del menú d'usuari | `menu_user_configuracio` |
| `decorators/default.jsp` | Enllaç "Desconnectar" del menú d'usuari | `menu_user_logout` |
| `entitatList.jsp` | Botó "Nova entitat" | `btNovaEntitat` |
| `entitatForm.jsp` | Botó "Guardar" | `btGuardarEntitat` |
| `entitatForm.jsp` | Enllaç "Cancel·lar" | `btCancelarEntitat` |
| `usuariForm.jsp` | Botó "Guardar" (configuració d'usuari) | `btGuardarUsuariConfig` |
| `usuariForm.jsp` | Enllaç "Tancar" (configuració d'usuari) | `btCancelarUsuariConfig` |

Els camps de formulari (`pbl:inputText`, `pbl:inputSelect`, `pbl:inputDate`)
ja generaven un `id` igual al `name` del camp abans d'aquesta suite, així
que la majoria de camps es poden seleccionar directament amb `#nomDelCamp`.

## Cobertura actual i ampliació

Coberts: login/logout, formulari de configuració d'usuari (patró de modal
genèric, disponible per a qualsevol rol), CRUD complet d'entitats (patró de
llistat/DataTable + modal, reutilitzable per a la resta de manteniments
d'administració: serveis, procediments, organs gestors...), el llistat i
filtratge de consultes, i la creació d'una consulta simple real (amb el
resultat d'error) contra el servidor fake de SCSP
(`consulta-simple.spec.ts`, vegeu més avall).

### Consulta simple contra el fake de SCSP

`consulta-simple.spec.ts` crea una consulta real amb el document titular
`00000000ERR` (vegeu `SCSP_FAKE_ERROR_TRIGGER_DOC` a `utils/env.ts`), que el
fake de SCSP interpreta com un disparador per tornar una resposta d'error
(independentment del servei), i comprova que la consulta apareix al llistat
amb l'estat "Error". És l'únic camí testejable de manera genèrica sense
dependre dels camps de "dades específiques" (que varien per servei), ja que
només depèn del document del titular.

Per habilitar-lo cal:
1. Que els serveis SCSP coberts pel fake estiguin redirigits cap a ell
   (`scripts/e2e/run-e2e.sh --point-scsp-urls`, o manualment amb
   `scripts/scsp-fake/point-to-fake.sql`; vegeu
   [FAKE_SCSP_SERVER.md](../../../../../../FAKE_SCSP_SERVER.md)).
2. `E2E_CONSULTA_SERVEI_CODI` amb el codi d'un d'aquests serveis
   (`Q2827003ATGSS001`, `SCDCPAJU`, `SVDDGTVEHICULOSANCWS01`,
   `SVDDGPCIWS02`) assignat a l'entitat del rol delegat, amb el camp de
   document del titular actiu. Si el servei triat requereix "dades
   específiques" obligatòries no emplenades pel test, la creació fallarà en
   validació; trieu un servei sense camps específics obligatoris o amplieu
   el test seguint el patró de `entitat-crud.spec.ts`
   (`frame.locator(...)` per als camps concrets).

Sense aquesta configuració, el test es marca com a "skipped".

Consultes múltiples (fitxer d'importació) segueixen sense cobrir, pel mateix
motiu de "dades específiques" variables per servei.
