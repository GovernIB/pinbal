import { Page } from '@playwright/test';
import { test, expect } from '../../utils/fixtures';
import { waitForInitialDataTableLoad, waitForDataTableReload } from '../../utils/datatable';
import { clickModalFooterButton, modalFrame, waitForModalClosed } from '../../utils/modal';

/**
 * "Integracions": monitoratge de trucades a sistemes externs (SCSP,
 * Recobriment, Keycloak...). El llistat es organitza en pestanyes (una per
 * "tipus d'integració", `codi`), i CADA pestanya té el seu propi DataTable
 * (mateix id `table-entitats` reutilitzat de la resta de manteniments — no
 * és un error de còpia, és així a integracioList.jsp). No hi ha dades
 * sembrades per aquesta taula a l'entorn e2e (no hi ha cap changelog de
 * pbl_integracio_accio a 00_e2e_seed_data.yaml): els registres només
 * apareixen quan el sistema fa trucades reals (SCSP, Recobriment...), cosa
 * que no es pot garantir en aquest entorn. Per això, "detall i esborrat" es
 * prova de manera resilient: es recorren totes les pestanyes buscant-ne una
 * amb dades; si cap no en té, el test es salta explícitament amb un motiu
 * clar en lloc de fallar per manca de dades.
 */

/** Retorna els hrefs de totes les pestanyes ("tipus d'integració") del llistat. */
async function tabHrefs(page: Page): Promise<string[]> {
    return page.locator('ul.nav-tabs li a').evaluateAll((links) =>
        links.map((a) => (a as HTMLAnchorElement).getAttribute('href') || '').filter(Boolean),
    );
}

test.describe('Integracions (administrador)', () => {
    test('llistat: dades correctes i filtres', async ({ adminPage: page }) => {
        await page.goto('integracio');
        await waitForInitialDataTableLoad(page);

        await expect(page.locator('#table-entitats')).toBeVisible();

        // Filtrar per una descripció que no existeix ha de deixar el llistat buit.
        await waitForDataTableReload(page, async () => {
            await page.locator('#descripcio').fill('DESCRIPCIO_QUE_NO_HAURIA_D_EXISTIR_E2E');
            // NOTA: integracioList.jsp té DOS botons amb id="filtrar" (un d'ocult, sense
            // etiqueta, usat per un altre patró JS; i el visible amb el text "Filtrar"), cosa
            // que fa que `#filtrar` no sigui únic. Es localitza pel rol/nom accessible en lloc
            // de l'id per evitar la violació de "strict mode" (l'element ocult, a més, queda
            // exclòs de l'arbre d'accessibilitat pel seu `display:none`).
            await page.getByRole('button', { name: /filtrar/i }).click();
        });
        await expect(page.locator('#table-entitats td.dataTables_empty')).toBeVisible({ timeout: 15_000 });

        // Netejar el filtre torna a mostrar l'estat inicial (formulari net).
        await waitForDataTableReload(page, async () => {
            await page.locator('#netejarFiltre').click();
        });
        await expect(page.locator('#descripcio')).toHaveValue('');

        // Filtrar per idPeticio inexistent també ha de deixar el llistat buit.
        await waitForDataTableReload(page, async () => {
            await page.locator('#idPeticio').fill('ID_PETICIO_QUE_NO_EXISTEIX_E2E');
            await page.getByRole('button', { name: /filtrar/i }).click();
        });
        await expect(page.locator('#table-entitats td.dataTables_empty')).toBeVisible({ timeout: 15_000 });
        await waitForDataTableReload(page, async () => {
            await page.locator('#netejarFiltre').click();
        });
    });

    test('visualització de detall i esborrat', async ({ adminPage: page }) => {
        await page.goto('integracio');
        await waitForInitialDataTableLoad(page);

        const hrefs = await tabHrefs(page);
        let filaAmbDades = page.locator('#table-entitats tbody tr').first();
        let trobat = false;

        for (const href of hrefs.length ? hrefs : ['/integracio']) {
            await page.goto(href);
            await waitForInitialDataTableLoad(page);
            const buida = await page.locator('#table-entitats td.dataTables_empty').isVisible().catch(() => false);
            if (!buida && (await page.locator('#table-entitats tbody tr').count()) > 0) {
                filaAmbDades = page.locator('#table-entitats tbody tr').first();
                trobat = true;
                break;
            }
        }

        test.skip(
            !trobat,
            'Cap pestanya d\'integracions té registres en aquest entorn (no hi ha seed de '
                + 'pbl_integracio_accio; els registres només es generen amb trucades reals a '
                + 'sistemes externs). No es pot provar detall/esborrat sense dades.',
        );
        if (!trobat) return;

        // --- Detall (modal amb iframe) ---
        await filaAmbDades.getByRole('link', { name: /detalls/i }).click();
        const frame = await modalFrame(page);
        await expect(frame.locator('body')).not.toBeEmpty();
        // El botó "Tancar" (data-modal-cancel="true") es clona al peu de la modal, fora de
        // l'iframe (webutil.modal.js); l'original de dins l'iframe no és visible. Vegeu el
        // comentari de clickModalFooterButton a utils/modal.ts.
        await clickModalFooterButton(page, /tancar/i);
        await waitForModalClosed(page);

        // --- Esborrat (acció a nivell de pestanya, sense confirmació) ---
        await waitForDataTableReload(page, async () => {
            await page.locator('#btnDelete').click();
        });
        await expect(page.locator('#table-entitats td.dataTables_empty')).toBeVisible({ timeout: 15_000 });
    });
});
