import { test, expect } from '../../utils/fixtures';
import { AdminMaintenancePage } from '../../pages/AdminMaintenancePage';
import { waitForInitialDataTableLoad } from '../../utils/datatable';

/**
 * "Òrgans gestors": pantalla de només llistat (sense modal de creació —
 * els òrgans gestors es donen d'alta via la sincronització amb Dir3, no
 * manualment). Reutilitza AdminMaintenancePage per al llistat/filtre.
 */
test.describe('Òrgans gestors (administrador)', () => {
    test('l\'acció "Actualitzar Dir3" es pot invocar i no trenca la pàgina', async ({ adminPage: page }) => {
        const organs = new AdminMaintenancePage(page, { url: 'organgestor', tableId: 'table-organs' });
        await organs.goto();

        const botoDir3 = page.locator('#organgestor-boto-nou');
        await expect(botoDir3).toBeVisible();

        // NOTA: aquesta acció crida un servei web Dir3 extern real
        // (OrganGestorController -> "organgestor/sync/dir3"). En aquest entorn
        // e2e (H2, sense connectivitat real a Dir3) no hi ha manera de garantir
        // una sincronització amb èxit; el que es comprova és que l'acció és
        // present, és clicable, i que —tant si Dir3 respon com si l'intent
        // falla— el backend gestiona el resultat de manera controlada: redirigeix
        // de tornada al llistat (no un error 500/pàgina blanca) amb un missatge
        // d'alerta (èxit o error) i el DataTable es torna a inicialitzar
        // correctament. S'utilitza un timeout ampliat perquè la crida real a
        // Dir3 pot trigar a fer timeout de connexió en aquest entorn.
        const datatableReloadPromise = page.waitForResponse(
            (resp) => resp.url().includes('/organgestor/datatable') && resp.status() === 200,
            { timeout: 60_000 },
        );
        await botoDir3.click();
        await datatableReloadPromise;

        await expect(page.locator('#table-organs')).toBeVisible();
        await expect(page.locator('#contingut-missatges .alert')).toBeVisible({ timeout: 15_000 });
    });

    test('el llistat mostra l\'òrgan gestor sembrat i es pot filtrar per codi', async ({ adminPage: page }) => {
        const organs = new AdminMaintenancePage(page, { url: 'organgestor', tableId: 'table-organs' });
        await organs.goto();
        await waitForInitialDataTableLoad(page);

        await expect(organs.row('E2EOG01')).toBeVisible({ timeout: 15_000 });

        await organs.filtrar(async (p) => {
            await p.locator('#codi').fill('E2EOG01');
        });
        await expect(organs.row('E2EOG01')).toBeVisible();
        await expect(organs.rows()).toHaveCount(1);

        await organs.filtrar(async (p) => {
            await p.locator('#codi').fill('');
            await p.locator('#nom').fill('CODI_QUE_NO_HAURIA_D_EXISTIR_E2E');
        });
        await expect(organs.isEmpty()).toBeVisible({ timeout: 15_000 });

        await organs.netejarFiltre();
        await expect(page.locator('#nom')).toHaveValue('');
        await expect(organs.row('E2EOG01')).toBeVisible({ timeout: 15_000 });
    });
});
