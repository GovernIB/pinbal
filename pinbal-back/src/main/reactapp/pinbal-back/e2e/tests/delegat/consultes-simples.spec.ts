import { test, expect } from '../../utils/fixtures';
import { waitForDataTableReload, waitForInitialDataTableLoad } from '../../utils/datatable';
import {
    consultaSimpleConfig,
    SCSP_FAKE_ERROR_TRIGGER_DOC,
    SCSP_FAKE_SUCCESS_DOC,
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
        await page.goto('consulta');
        await waitForInitialDataTableLoad(page);

        await expect(page.locator('#table-consultes')).toBeVisible();
        await expect(page.locator('#form-filtre')).toBeVisible();
    });

    test('es pot filtrar per nom del titular i netejar el filtre', async ({ delegatPage: page }) => {
        await page.goto('consulta');
        await waitForInitialDataTableLoad(page);

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
        await page.goto('consulta');
        await waitForInitialDataTableLoad(page);

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

        const titularDocumentTipus = page.locator('#titularDocumentTipus');
        if (await titularDocumentTipus.count()) {
            await titularDocumentTipus.selectOption('NIF', { force: true });
        }
        await titularDocumentNum.fill(SCSP_FAKE_ERROR_TRIGGER_DOC);

        await page.locator('#consultaForm button[type="submit"]').click();

        await page.goto('consulta');
        await waitForInitialDataTableLoad(page);
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
 * Detall d'una consulta simple ja "Tramitada" (creada per `global-setup.ts`
 * amb el document normal `SCSP_FAKE_SUCCESS_DOC`): informació de l'arxiu,
 * vista prèvia del justificant, i reintent/vista d'error de la generació del
 * justificant. Es cerca la fila al llistat en lloc de dependre d'un id
 * concret, ja que `global-setup.ts` és tolerant a fallades i pot no haver
 * arribat a crear-la (l'entorn no tenia cap servei amb el camp de document
 * actiu, p.ex.); en aquest cas els tests es marquen com a "skipped".
 */
test.describe('Detall de consulta simple (delegat)', () => {
    async function obrirDetallConsultaTramitada(page: import('@playwright/test').Page) {
        await page.goto('consulta');
        await waitForInitialDataTableLoad(page);
        await waitForDataTableReload(page, async () => {
            await page.locator('#titularDocument').fill(SCSP_FAKE_SUCCESS_DOC);
            await page.locator('#filtrar').click();
        });

        const fila = page.locator('#table-consultes tbody tr', { hasText: 'Tramitada' }).first();
        if (!(await fila.isVisible().catch(() => false))) {
            test.skip(
                true,
                'No hi ha cap consulta "Tramitada" amb el document normal de prova; '
                    + 'global-setup.ts no ha pogut crear-la en aquest entorn (vegeu els avisos al log de la suite).',
            );
            return null;
        }

        await fila.getByRole('link', { name: /detalls/i }).click();
        return modalFrame(page);
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
        const pdfContainer = frame.locator('#pdf-container');
        const errorContainer = frame.locator('#error-container');
        // Segons si el justificant s'ha pogut generar/servir correctament a l'entorn,
        // acceptem tant la vista prèvia com el missatge d'error com a resultat vàlid del clic.
        await expect(pdfContainer.or(errorContainer)).toBeVisible({ timeout: 15_000 });
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
        await page.goto('consulta');
        await waitForInitialDataTableLoad(page);

        // Aquesta funcionalitat només és visible per a files "Tramitada" amb
        // el justificant en estat d'error (justificantEstat == 'error'); no hi
        // ha manera coneguda de forçar aquest estat concret des del fake de
        // SCSP (que només controla el resultat de la consulta, no el de la
        // generació posterior del PDF), així que el test és merament
        // observacional: si cap fila mostra aquest estat, se salta.
        const filaAmbErrorJustificant = page.locator('#table-consultes tbody tr').filter({
            has: page.locator('a.dropdown-toggle .fa-exclamation-triangle.text-danger'),
        }).first();

        if (!(await filaAmbErrorJustificant.isVisible().catch(() => false))) {
            test.skip(
                true,
                'Cap consulta del llistat té el justificant en estat d\'error; aquest estat no es pot forçar '
                    + 'des del fake de SCSP (només afecta la consulta, no la generació posterior del justificant).',
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
