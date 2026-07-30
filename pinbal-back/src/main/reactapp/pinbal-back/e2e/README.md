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
aixeca l'aplicació amb `docker compose`/`docker-compose`/`podman-compose`
**contra una base de dades H2 efímera** (overlay `docker-compose.e2e.yml`,
sempre afegit; Keycloak segueix sent l'extern configurat a `.env`), arrenca
el servidor fake de SCSP ([FAKE_SCSP_SERVER.md](../../../../../../FAKE_SCSP_SERVER.md))
i executa aquesta suite. Vegeu `scripts/e2e/run-e2e.sh --help` per a totes
les opcions (`--down` per aturar-ho tot en acabar, etc.).

Com que la BD és H2 en memòria, l'esquema i les dades de proves es carreguen
de nou (via Liquibase) cada vegada que es crea el contenidor `h2`: l'estat es
reinicia amb cada execució neta (p. ex. després de `--down`), tret que es
reutilitzi la mateixa pila ja aixecada entre execucions. Les credencials de
`e2e/.env.e2e` (vegeu més avall) es reenvien automàticament com a paràmetres
de Liquibase perquè aquesta càrrega de dades creï els usuaris/entitats de
proves corresponents al Keycloak compartit; ja no cal cap pas manual
d'apuntar els serveis SCSP cap al fake (l'antiga opció `--point-scsp-urls` ha
desaparegut: era només per a l'ús contra un Oracle real).

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
npx playwright test e2e/tests/administrador/gestionar-entitats.spec.ts
npx playwright test -g "es pot filtrar per codi"
```

## Estructura

```
e2e/
  global-setup.ts   Crea, via la UI real (rol delegat), unes quantes consultes
                    simples/múltiples reals contra el fake de SCSP abans de
                    tota la suite, perquè els tests de llistat/detall/
                    descàrrega/estadístiques tinguin dades realistes
  tests/
    auth.spec.ts            Tests transversals (no lligats a un rol concret)
    administrador/          Configurar (propietats, cache, avisos, parametres
                             SCSP, emissors, claus), Gestionar (entitats,
                             serveis, organs gestors, integracions), informes,
                             estadistiques, consultes realitzades
    representant/           Usuaris, procediments (+ serveis per procediment),
                             organs gestors, estadistiques, informes
    delegat/                Consultes simples, consultes múltiples, noves
                             consultes
  pages/            Page Objects: encapsulen les URLs i selectors JSP de cada
                     pantalla darrere d'una interfície estable (goto/CRUD/
                     filtres). Els fitxers .spec.ts només criden mètodes
                     d'aquests objectes, mai selectors/URLs directament — quan
                     es migri una pantalla a React, només cal reescriure la
                     implementació interna del Page Object corresponent, no
                     els specs. `AdminMaintenancePage` és la base compartida
                     pel patró llistat+modal+accions per fila.
  utils/
    env.ts          Lectura de credencials/config per variables d'entorn
    auth.ts         login()/logout() contra el formulari de Keycloak
    fixtures.ts      Fixtures de Playwright (adminPage, delegatPage, ...) ja autenticades
    modal.ts        Helpers per a les modals amb iframe de PINBAL (webutil.modal.js)
    datatable.ts    Helpers per a les taules DataTables amb processament al servidor
    messages.ts     Helpers pels missatges d'èxit/error (#contingut-missatges)
    cif.ts          Genera un CIF/NIF amb dígit de control vàlid
    select2.ts      Helpers per als combos select2 (autocompletar/seleccionar opció)
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

La suite cobreix, seguint el patró de Page Object descrit a dalt, els tres
rols de l'aplicació (Administrador, Representant, Delegat) tal com es
descriuen al checklist funcional intern del projecte: manteniments de
"Configurar" i "Gestionar" (propietats, cache, avisos, paràmetres SCSP,
emissors, claus, entitats, serveis, òrgans gestors, integracions), informes,
estadístiques, consultes realitzades (Administrador); usuaris, procediments
i serveis per procediment, òrgans gestors, estadístiques, informes
(Representant); i consultes simples/múltiples i noves consultes (Delegat).
Les dades necessàries per a aquests tests es sembren automàticament a la BD
H2 del flux `scripts/e2e/run-e2e.sh` (cf.
`pinbal-persistence/src/main/resources/db/changelog/e2e/`) i, per a les
consultes realitzades, també via `global-setup.ts` (creació d'unes quantes
consultes reals contra el fake de SCSP abans de la suite).

Limitacions/gaps coneguts, documentats amb un comentari al test corresponent:
- Alguns fluxos dins un test es marquen com a `test.skip` quan depenen de
  dades que `global-setup.ts` no garanteix (p. ex. una consulta múltiple en
  un estat concret) en lloc de fallar de manera espúria.
- El cas "Data final invàlida" d'Avisos i el cas "Data de baixa amb hores" de
  Claus privades estan escrits seguint el comportament *desitjat* pel
  checklist; si l'aplicació encara no el implementa exactament així, el test
  fallarà de manera intencionada fins que es corregeixi (vegeu els
  comentaris a `configurar-avisos.spec.ts`/`configurar-claus-privades.spec.ts`).
- La descàrrega de justificants d'una consulta simple com a Administrador
  requereix que l'usuari tingui una fila `PBL_ENTITAT_USUARI` amb `delegat`
  per a l'entitat consultada (`EntitatHelper.isDelegatEntitatActual`); un
  compte purament `PBL_ADMIN` no la compleix mai, així que aquests casos es
  tracten com a "best effort" a `consultes-realitzades.spec.ts` en lloc de
  fallar — val la pena revisar amb el client si aquesta restricció és
  intencionada per a l'ús real de l'aplicació.

### Consulta simple contra el fake de SCSP

Els serveis SCSP coberts pel fake (`Q2827003ATGSS001`, `SCDCPAJU`,
`SVDDGTVEHICULOSANCWS01`, `SVDDGPCIWS02`) ja queden assignats a l'entitat del
rol delegat i redirigits cap al fake automàticament en carregar les dades de
e2e (H2). El document titular `00000000ERR` (`SCSP_FAKE_ERROR_TRIGGER_DOC` a
`utils/env.ts`) fa que el fake respongui sempre amb un error, independentment
del servei; s'usa per provar l'estat "Error" i els fluxos de reintent.
`E2E_CONSULTA_SERVEI_CODI`/`E2E_CONSULTA_PROCEDIMENT_ID` segueixen disponibles
per forçar un servei/procediment concret si cal.
