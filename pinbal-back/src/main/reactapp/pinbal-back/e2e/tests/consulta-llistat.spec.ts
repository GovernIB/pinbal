import { test, expect } from '../utils/fixtures';
import { waitForDataTableReload, waitForInitialDataTableLoad } from '../utils/datatable';

/**
 * Llistat de consultes simples/múltiples realitzades (rol delegat). La
 * creació d'una consulta real depèn del servei SCSP escollit (camps de
 * "dades específiques" dinàmics per servei), per la qual cosa no es cobreix
 * aquí; vegeu e2e/README.md per ampliar-ho amb els serveis disponibles al
 * vostre entorn (mock-scsp-db).
 */
test.describe('Llistat de consultes (delegat)', () => {
    test('el llistat de consultes simples es carrega', async ({ delegatPage: page }) => {
        await page.goto('/consulta');
        await waitForInitialDataTableLoad(page);

        await expect(page.locator('#table-consultes')).toBeVisible();
        await expect(page.locator('#form-filtre')).toBeVisible();
    });

    test('es pot filtrar per nom del titular i netejar el filtre', async ({ delegatPage: page }) => {
        await page.goto('/consulta');
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
        await page.goto('/consulta');
        await waitForInitialDataTableLoad(page);

        await waitForDataTableReload(page, async () => {
            await page.locator('#estat').selectOption('Error', { force: true });
            await page.locator('#filtrar').click();
        });

        const rows = page.locator('#table-consultes tbody tr');
        const emptyState = page.locator('#table-consultes td.dataTables_empty');
        await expect(rows.or(emptyState).first()).toBeVisible({ timeout: 15_000 });
    });

    test('el llistat de consultes múltiples es carrega', async ({ delegatPage: page }) => {
        await page.goto('/consulta/multiple');

        await expect(page.locator('body')).toBeVisible();
    });
});
