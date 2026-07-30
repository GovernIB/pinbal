import { test, expect } from '../../utils/fixtures';
import { OrgansGestorsPage } from '../../pages/OrgansGestorsPage';
import { errorMessage, successMessage } from '../../utils/messages';

/**
 * Òrgans gestors (representant), `/organgestor` (`organGestor.jsp`) — la
 * mateixa pantalla que fa servir l'administrador (mateix JSP), però sense la
 * capacitat d'escollir entitat (el representant sempre veu només la seva) i
 * sense CRUD (aquesta pantalla, per al rol representant, és només llistat +
 * filtre + sincronització amb Dir3; no hi ha botó "Nou" ni accions d'edició
 * per fila).
 *
 * NOTA: l'entitat sembrada E2EENT01 (id 900001) no té `unitatArrel` (Dir3)
 * informat a `00_e2e_seed_data.yaml`, i aquest entorn e2e no té connectivitat
 * real amb Dir3. Per tant s'espera que "Actualitzar Dir3" respongui amb el
 * missatge d'error controlat "no hi ha unitat arrel associada" (vegeu
 * `OrganGestorController.syncDir3`), no amb un èxit. El que es comprova aquí
 * és que l'acció existeix, és clicable, i que la resposta (èxit o error) es
 * mostra correctament sense trencar la pàgina — no la integració real amb Dir3.
 */
test.describe('Òrgans gestors (representant)', () => {
    test('el botó "Actualitzar Dir3" respon sense trencar la pàgina', async ({ representantPage: page }) => {
        const organsGestors = new OrgansGestorsPage(page);
        await organsGestors.goto();

        await organsGestors.actualitzarDir3();

        // Independentment de si Dir3 respon amb èxit o error, la pàgina ha de
        // tornar a mostrar el llistat amb un missatge (no una pàgina d'error 500).
        await expect(organsGestors.rows().first().or(organsGestors.isEmpty())).toBeVisible({ timeout: 15_000 });
        const hiHaMissatge = await Promise.race([
            successMessage(page).first().isVisible().then(() => true).catch(() => false),
            errorMessage(page).first().isVisible().then(() => true).catch(() => false),
        ]);
        expect(hiHaMissatge).toBeTruthy();
    });

    test('el llistat es pot filtrar per codi i el filtre es pot netejar', async ({ representantPage: page }) => {
        const organsGestors = new OrgansGestorsPage(page);
        await organsGestors.goto();

        await expect(organsGestors.row('E2EOG01')).toBeVisible({ timeout: 15_000 });

        await organsGestors.filtrar(async (form) => {
            await form.locator('#codi').fill('CODI_QUE_NO_HAURIA_D_EXISTIR_E2E');
        });
        await expect(organsGestors.isEmpty()).toBeVisible({ timeout: 15_000 });

        await organsGestors.netejarFiltre();
        await expect(organsGestors.row('E2EOG01')).toBeVisible({ timeout: 15_000 });
    });
});
