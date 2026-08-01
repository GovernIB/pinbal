import { test, expect } from '../../utils/fixtures';
import { waitForDataTableReload } from '../../utils/datatable';
import {
    consultaSimpleConfig,
    SCSP_FAKE_ERROR_TRIGGER_DOC,
    uniqueSuffix,
} from '../../utils/env';
import { modalFrame } from '../../utils/modal';

/**
 * Consultes simples (rol delegat): llistat, creació real contra el fake de
 * SCSP i detall (justificant/arxiu). Fusiona els antics
 * `consulta-llistat.spec.ts` (part simple) i `consulta-simple.spec.ts`.
 *
 * Les consultes múltiples es cobreixen a `consultes-multiples.spec.ts`.
 */
test.describe('Llistat de consultes simples (delegat)', () => {
    test('el llistat de consultes simples es carrega', async ({ delegatPage: page }) => {
        await waitForDataTableReload(page, async () => {
            await page.goto('consulta');
        });

        await expect(page.locator('#table-consultes')).toBeVisible();
        await expect(page.locator('#form-filtre')).toBeVisible();
    });

    test('es pot filtrar per nom del titular i netejar el filtre', async ({ delegatPage: page }) => {
        await waitForDataTableReload(page, async () => {
            await page.goto('consulta');
        });

        await waitForDataTableReload(page, async () => {
            await page.locator('#titularNom').fill('Titular que no hauria d\'existir E2E');
            await page.locator('#filtrar').click();
        });
        await expect(page.locator('#table-consultes td.dataTables_empty')).toBeVisible({ timeout: 15_000 });

        await waitForDataTableReload(page, async () => {
            await page.locator('#netejar-filtre').click();
        });
        await expect(page.locator('#titularNom')).toHaveValue('');
    });

    test('es pot filtrar per estat de la consulta', async ({ delegatPage: page }) => {
        await waitForDataTableReload(page, async () => {
            await page.goto('consulta');
        });

        await waitForDataTableReload(page, async () => {
            await page.locator('#estat').selectOption('Error', { force: true });
            await page.locator('#filtrar').click();
        });

        const rows = page.locator('#table-consultes tbody tr');
        const emptyState = page.locator('#table-consultes td.dataTables_empty');
        await expect(rows.or(emptyState).first()).toBeVisible({ timeout: 15_000 });
    });
});

/**
 * Crea una consulta simple real contra el servidor fake de SCSP
 * (pinbal-scsp-fake, vegeu FAKE_SCSP_SERVER.md) i comprova que apareix al
 * llistat amb l'estat esperat.
 *
 * Aquest test necessita:
 *   - Un entorn amb els serveis SCSP coberts pel fake redirigits cap a ell
 *     (scripts/e2e/run-e2e.sh, vegeu e2e/README.md).
 *   - E2E_CONSULTA_SERVEI_CODI configurat amb el codi d'un d'aquests serveis
 *     (Q2827003ATGSS001, SCDCPAJU, SVDDGTVEHICULOSANCWS01, SVDDGPCIWS02) que
 *     estigui assignat, amb el camp de document del titular actiu, a
 *     l'entitat del rol delegat de l'entorn. Opcionalment
 *     E2E_CONSULTA_PROCEDIMENT_ID per triar un procediment concret.
 * Si no es compleixen, el test es marca com a "skipped".
 */
test.describe('Consulta simple (delegat)', () => {
    test('una consulta amb el document disparador d\'error queda en estat Error', async ({ delegatPage: page }) => {
        const config = consultaSimpleConfig();
        if (!config) {
            test.skip(true, 'E2E_CONSULTA_SERVEI_CODI no configurat. Vegeu e2e/README.md.');
            return;
        }

        const suffix = uniqueSuffix();

        await page.goto(`consulta/${config.serveiCodi}/new`);

        const titularDocumentNum = page.locator('#titularDocumentNum');
        if ((await titularDocumentNum.count()) === 0) {
            test.skip(true, `El servei ${config.serveiCodi} no té el camp de document del titular actiu; `
                + 'trieu un altre servei a E2E_CONSULTA_SERVEI_CODI.');
            return;
        }

        if (config.procedimentId) {
            await page.locator('#procedimentId').selectOption(config.procedimentId, { force: true });
        } else {
            await page.locator('#procedimentId').selectOption({ index: 0 }, { force: true });
        }

        await page.locator('#funcionariNom').fill(`Funcionari E2E ${suffix}`);
        await page.locator('#departamentNom').fill('Departament E2E');
        await page.locator('#finalitat').fill(`Prova e2e ${suffix}`);

        // 'Passaport' i no 'NIF': el disparador d'error del fake SCSP (SCSP_FAKE_ERROR_TRIGGER_DOC
        // = '00000000ERR') no és un NIF de format vàlid; DocumentIdentitatValidator aplica
        // checksum per a NIF/DNI/NIE/CIF però accepta qualsevol valor per a 'Passaport' sense cap
        // comprovació de format. Amb 'NIF' el formulari rebutja el document abans d'arribar a
        // SCSP ("Número de document invàlid") i la consulta mai es crea (vegeu e2e/BUGS_APLICACIO.md).
        const titularDocumentTipus = page.locator('#titularDocumentTipus');
        if (await titularDocumentTipus.count()) {
            await titularDocumentTipus.selectOption('Passaport', { force: true });
        }
        await titularDocumentNum.fill(SCSP_FAKE_ERROR_TRIGGER_DOC);

        await page.locator('#consultaForm button[type="submit"]').click();

        // A diferència d'una consulta amb èxit, quan SCSP retorna un CodigoEstado d'error
        // l'aplicació NO redirigeix al llistat: es queda al formulari mostrant l'error en línia
        // ("La consulta ha retornat un error: [9999]...") i deixa que l'usuari en modifiqui les
        // dades i reintenti (el que generaria una consulta NOVA, no la mateixa). Això NO vol dir
        // que la consulta no s'hagi creat: sí que es crea i és consultable al llistat amb
        // estat=Error -- cal anar-hi expressament a comprovar-ho, no confiar en cap redirecció.
        await waitForDataTableReload(page, async () => {
            await page.goto('consulta');
        });
        await waitForDataTableReload(page, async () => {
            await page.locator('#titularDocument').fill(SCSP_FAKE_ERROR_TRIGGER_DOC);
            await page.locator('#filtrar').click();
        });

        const fila = page.locator('#table-consultes tbody tr').first();
        await expect(fila).toBeVisible({ timeout: 15_000 });
        await expect(fila).toContainText('Error');
    });
});

/**
 * Detall d'una consulta simple: informació de l'arxiu, vista prèvia del
 * justificant, i reintent/vista d'error de la generació del justificant.
 *
 * Es cerca sempre la consulta de mostra sembrada `PBL_E2E_SIMPLE_OK`
 * (`scspPeticionId`, filtre `#scspPeticionId`) en lloc de filtrar per
 * document i agafar "la primera Tramitada": ara que `global-setup.ts` crea
 * consultes reals amb el mateix document normal de prova
 * (`SCSP_FAKE_SUCCESS_DOC`, coincident amb el `titular_docnum` sembrat de
 * `PBL_E2E_SIMPLE_OK`), aquell filtre podia trobar-ne més d'una i quedar-se
 * amb la més recent (la creada dinàmicament), que no té per què tenir
 * justificant/missatges en el mateix estat determinista que la sembrada
 * (mateix problema ja detectat als tests d'administrador: cal apuntar a una
 * consulta Tramitada concreta que tingui els missatges, no "la primera").
 */
test.describe('Detall de consulta simple (delegat)', () => {
    async function obrirDetallConsultaTramitada(page: import('@playwright/test').Page) {
        await waitForDataTableReload(page, async () => {
            await page.goto('consulta');
        });
        await waitForDataTableReload(page, async () => {
            await page.locator('#scspPeticionId').fill('PBL_E2E_SIMPLE_OK');
            await page.locator('#filtrar').click();
        });

        // waitForDataTableReload només espera la resposta de xarxa de l'ajax del DataTable,
        // no que aquest hagi acabat de pintar el resultat al DOM; un `.isVisible()` (sense
        // esperar) just després és una condició de carrera que fa saltar el test encara que
        // la fila existeixi (confirmat: la resposta ajax ja porta `recordsFiltered:1` amb les
        // dades correctes, però el redraw del DataTable és una mica posterior). Cal `waitFor`.
        const fila = page.locator('#table-consultes tbody tr', { hasText: 'PBL_E2E_SIMPLE_OK' }).first();
        const trobada = await fila.waitFor({ state: 'visible', timeout: 10_000 }).then(() => true).catch(() => false);
        if (!trobada) {
            test.skip(
                true,
                'No es troba la consulta de mostra "PBL_E2E_SIMPLE_OK" (sembrada per Liquibase, context e2e); '
                    + 'vegeu 01_e2e_seed_serveis.yaml i e2e/BUGS_APLICACIO.md.',
            );
            return null;
        }

        await fila.getByRole('link', { name: /detalls/i }).click();
        const frame = await modalFrame(page);

        // #mostrarVistaPrevia i #justificantInfo viuen dins el tab-pane "Justificant"
        // (#descarregaJustificantsTab, consultaInfo.jsp), amagat per defecte (Bootstrap tabs:
        // només el primer tab, "Dades genèriques", és actiu en carregar). Sense clicar aquest
        // tab abans, `.isVisible()` sobre aquests botons és sempre fals encara que hi siguin al
        // DOM -- confirmat que és la causa real dels "skip" d'aquests dos tests, no manca de
        // dades (el tab només es renderitza si `justificantEstatOk`/`Pendent`/`Error`, així que
        // si no hi és, tampoc hi és per manca de justificant real, cas que sí cal saltar).
        const tabJustificant = frame.getByRole('tab', { name: /justificant/i });
        if (await tabJustificant.isVisible().catch(() => false)) {
            await tabJustificant.click();
        }
        return frame;
    }

    test('es pot veure la vista prèvia del justificant', async ({ delegatPage: page }) => {
        const frame = await obrirDetallConsultaTramitada(page);
        if (!frame) return;

        const botoVistaPrevia = frame.locator('#mostrarVistaPrevia');
        if (!(await botoVistaPrevia.isVisible().catch(() => false))) {
            test.skip(true, 'El justificant d\'aquesta consulta encara no està disponible per previsualitzar.');
            return;
        }

        await botoVistaPrevia.click();
        // #pdf-container i #error-container conviuen SEMPRE al DOM (un dels dos amb
        // display:none): `.or()` els troba tots dos i viola el "strict mode". El
        // pseudo-selector :visible sí que filtra pel que realment es mostra (mateix fix
        // aplicat a consultes-realitzades.spec.ts, administrador).
        await expect(frame.locator('#pdf-container:visible, #error-container:visible')).toBeVisible({ timeout: 15_000 });
    });

    test('es pot veure la informació de l\'arxiu del justificant, si està disponible', async ({ delegatPage: page }) => {
        const frame = await obrirDetallConsultaTramitada(page);
        if (!frame) return;

        const botoArxiuInfo = frame.locator('#justificantInfo');
        if (!(await botoArxiuInfo.isVisible().catch(() => false))) {
            test.skip(
                true,
                'Aquesta consulta no té justificant arxivat a l\'ARXIU digital (arxiuDocumentUuid buit); '
                    + 'el plugin d\'arxiu no està configurat o el justificant no s\'ha custodiat en aquest entorn.',
            );
            return;
        }

        await botoArxiuInfo.click();
        await expect(frame.locator('#arxiuDetall')).not.toBeEmpty({ timeout: 15_000 });
    });

    test('es pot reintentar i veure l\'error d\'una consulta amb error en la generació del justificant', async ({ delegatPage: page }) => {
        await waitForDataTableReload(page, async () => {
            await page.goto('consulta');
        });

        // Aquesta funcionalitat només és visible per a files "Tramitada" amb el
        // justificant en estat d'error; no hi ha manera coneguda de forçar aquest
        // estat concret des del fake de SCSP (que només controla el resultat de la
        // consulta, no el de la generació posterior del PDF), així que fem servir la
        // consulta de mostra sembrada `PBL_E2E_SIMPLE_JUSTERR` (Liquibase, context
        // e2e) en lloc d'escanejar "la primera fila amb aquest estat": amb prou
        // consultes generades per altres tests, la primera fila del llistat sense
        // filtrar pot no ser-ho (mateix problema detectat als tests d'administrador).
        await waitForDataTableReload(page, async () => {
            await page.locator('#scspPeticionId').fill('PBL_E2E_SIMPLE_JUSTERR');
            await page.locator('#filtrar').click();
        });
        const filaAmbErrorJustificant = page.locator('#table-consultes tbody tr', { hasText: 'PBL_E2E_SIMPLE_JUSTERR' }).first();
        const trobada = await filaAmbErrorJustificant.waitFor({ state: 'visible', timeout: 10_000 }).then(() => true).catch(() => false);

        if (!trobada) {
            test.skip(
                true,
                'No es troba la consulta de mostra "PBL_E2E_SIMPLE_JUSTERR" (sembrada per Liquibase, context e2e); '
                    + 'vegeu 01_e2e_seed_serveis.yaml i e2e/BUGS_APLICACIO.md.',
            );
            return;
        }

        await filaAmbErrorJustificant.locator('a.dropdown-toggle, button.dropdown-toggle').click();
        await expect(filaAmbErrorJustificant.getByRole('link', { name: /veure error/i })).toBeVisible();
        await expect(filaAmbErrorJustificant.getByRole('link', { name: /re-?intentar/i })).toBeVisible();

        await filaAmbErrorJustificant.getByRole('link', { name: /veure error/i }).click();
        await expect(page.locator('.modal.in textarea, .modal.show textarea').first()).toBeVisible({ timeout: 10_000 });
    });
});
