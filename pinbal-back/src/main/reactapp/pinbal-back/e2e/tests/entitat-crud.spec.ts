import { test, expect } from '../utils/fixtures';
import { clickModalFooterButtonById, modalFrame, waitForModalClosed } from '../utils/modal';
import { waitForDataTableReload, waitForInitialDataTableLoad } from '../utils/datatable';
import { expectSuccessMessage } from '../utils/messages';
import { uniqueSuffix } from '../utils/env';

/**
 * CRUD complet d'entitats (secció d'administració). Aquest flux exemplifica
 * el patró general de les pantalles d'administració de PINBAL (llistat amb
 * DataTable server-side + modal amb iframe per crear/editar), reutilitzable
 * per a la resta de manteniments (serveis, procediments, usuaris...).
 */
test.describe('Gestió d\'entitats (administrador)', () => {
    test('crear, editar, desactivar, activar i esborrar una entitat', async ({ adminPage: page }) => {
        const suffix = uniqueSuffix();
        const codi = `E2E${suffix}`.toUpperCase().slice(0, 20);
        const nomInicial = `Entitat E2E ${suffix}`;
        const nomModificat = `Entitat E2E ${suffix} modificada`;
        const cif = `Q${suffix}`.toUpperCase().slice(0, 15);
        const dir3 = `E${suffix}`.toUpperCase().slice(0, 15);

        await page.goto('/entitat');
        await waitForInitialDataTableLoad(page);

        // --- Crear ---
        await page.locator('#btNovaEntitat').click();
        let frame = await modalFrame(page);
        await frame.locator('#codi').fill(codi);
        await frame.locator('#nom').fill(nomInicial);
        await frame.locator('#cif').fill(cif);
        await frame.locator('#unitatArrel').fill(dir3);
        await frame.locator('#tipus').selectOption('AJUNTAMENT', { force: true });

        await waitForDataTableReload(page, async () => {
            await clickModalFooterButtonById(page, 'btGuardarEntitat');
        });
        await waitForModalClosed(page);
        await expectSuccessMessage(page);

        const fila = () => page.locator('#table-entitats tbody tr', { hasText: codi });
        await expect(fila()).toBeVisible({ timeout: 15_000 });
        await expect(fila()).toContainText(nomInicial);

        // --- Editar ---
        await fila().locator('a.dropdown-toggle').click();
        await fila().getByRole('link', { name: /modificar/i }).click();
        frame = await modalFrame(page);
        await frame.locator('#nom').fill(nomModificat);
        await waitForDataTableReload(page, async () => {
            await clickModalFooterButtonById(page, 'btGuardarEntitat');
        });
        await waitForModalClosed(page);
        await expectSuccessMessage(page);
        await expect(fila()).toContainText(nomModificat);

        // --- Desactivar ---
        await fila().locator('a.dropdown-toggle').click();
        await waitForDataTableReload(page, async () => {
            await fila().getByRole('link', { name: /desactivar/i }).click();
        });
        await fila().locator('a.dropdown-toggle').click();
        await expect(fila().getByRole('link', { name: /activar/i })).toBeVisible();

        // --- Activar ---
        await waitForDataTableReload(page, async () => {
            await fila().getByRole('link', { name: /^activar/i }).click();
        });

        // --- Esborrar (neteja de dades de prova; algunes instal·lacions poden tenir-ho desactivat) ---
        await fila().locator('a.dropdown-toggle').click();
        const linkEsborrar = fila().getByRole('link', { name: /esborrar/i });
        if (await linkEsborrar.isVisible().catch(() => false)) {
            page.once('dialog', (dialog) => dialog.accept());
            await waitForDataTableReload(page, async () => {
                await linkEsborrar.click();
            });
            await expect(page.locator('#table-entitats tbody tr', { hasText: codi })).toHaveCount(0);
        }
    });

    test('el llistat es pot filtrar per codi i el filtre es pot netejar', async ({ adminPage: page }) => {
        await page.goto('/entitat');
        await waitForInitialDataTableLoad(page);

        await waitForDataTableReload(page, async () => {
            await page.locator('#codi').fill('CODI_QUE_NO_HAURIA_D_EXISTIR_E2E');
            await page.locator('#form-filtre button[type="submit"]').click();
        });
        await expect(page.locator('#table-entitats td.dataTables_empty')).toBeVisible({ timeout: 15_000 });

        await waitForDataTableReload(page, async () => {
            await page.locator('#netejar-filtre').click();
        });
        await expect(page.locator('#codi')).toHaveValue('');
        await expect(page.locator('#table-entitats td.dataTables_empty')).toHaveCount(0);
    });
});
