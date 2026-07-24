import { test, expect } from '../utils/fixtures';
import { waitForDataTableReload, waitForInitialDataTableLoad } from '../utils/datatable';
import { consultaSimpleConfig, SCSP_FAKE_ERROR_TRIGGER_DOC, uniqueSuffix } from '../utils/env';

/**
 * Crea una consulta simple real contra el servidor fake de SCSP
 * (pinbal-scsp-fake, vegeu FAKE_SCSP_SERVER.md) i comprova que apareix al
 * llistat amb l'estat esperat.
 *
 * Aquest test necessita:
 *   - Un entorn amb els serveis SCSP coberts pel fake redirigits cap a ell
 *     (scripts/e2e/run-e2e.sh --point-scsp-urls, o manualment amb
 *     scripts/scsp-fake/point-to-fake.sql).
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

        await page.goto(`/consulta/${config.serveiCodi}/new`);

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

        await page.goto('/consulta');
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
