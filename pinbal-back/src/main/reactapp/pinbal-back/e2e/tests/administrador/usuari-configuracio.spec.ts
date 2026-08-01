import { Page } from '@playwright/test';
import { test, expect } from '../../utils/fixtures';
import { activeModal, clickModalFooterButtonById, modalFrame, waitForModalClosed } from '../../utils/modal';
import { expectSuccessMessage } from '../../utils/messages';

/**
 * La configuració d'usuari (menú d'usuari > "Configuració") és accessible
 * per a qualsevol rol autenticat, la qual cosa la fa un bon cas per validar
 * el patró general de modal amb iframe sense dependre de dades específiques
 * d'un rol concret (entitats, serveis SCSP...).
 */

/**
 * Obre el menú d'usuari (`#menu_user`, dropdown de Bootstrap) i n'espera
 * l'enllaç "Configuració".
 *
 * NO es pot assumir que el dropdown estigui tancat: en clicar
 * `#menu_user_configuracio` (dins el dropdown) per obrir la modal de
 * configuració, Bootstrap NO tanca el dropdown (el seu listener global
 * només tanca en clicar-hi A FORA, mai en un clic intern al propi
 * `.dropdown-menu`) — la classe `open` de l'`<li>` es queda posada mentre
 * la modal està oberta i, si la modal es tanca de manera 100% client-side
 * ("Cancel·lar", sense recarregar la pàgina), també DESPRÉS. Si en aquest
 * punt es torna a clicar `#menu_user` "per reobrir-lo", en realitat es
 * tanca (toggle), deixant `#menu_user_configuracio` invisible just quan el
 * test hi vol fer clic. Confirmat empíricament inspeccionant la classe de
 * l'`<li>` pare abans/després de tot el cicle obrir-modal/cancel·lar.
 */
async function obrirMenuConfiguracio(page: Page): Promise<void> {
    if (!(await page.locator('#menu_user_configuracio').isVisible())) {
        await page.locator('#menu_user').click();
    }
    await expect(page.locator('#menu_user_configuracio')).toBeVisible();
}

test.describe('Configuració d\'usuari', () => {
    test('es pot obrir el formulari de configuració des del menú d\'usuari', async ({ auditorPage: page }) => {
        await obrirMenuConfiguracio(page);
        await page.locator('#menu_user_configuracio').click();

        const frame = await modalFrame(page);
        await expect(frame.locator('#idioma')).toBeVisible();
        // El botó es clona al peu de la modal (fora de l'iframe); vegeu modal.ts.
        await expect(activeModal(page).locator('#btGuardarUsuariConfig')).toBeVisible();
    });

    test('es pot canviar l\'idioma per defecte i es desa correctament', async ({ auditorPage: page }) => {
        await obrirMenuConfiguracio(page);
        await page.locator('#menu_user_configuracio').click();

        const frame = await modalFrame(page);
        const original = await frame.locator('#idioma').inputValue();
        const nou = original === 'CA' ? 'ES' : 'CA';

        // La restauració del valor original va dins un `finally`: si alguna assertion
        // d'aquest bloc falla (p.ex. un canvi al patró de la modal), l'idioma de
        // l'usuari es queda "encallat" al valor de prova i trenca en cascada qualsevol
        // altre test que depengui de textos en català (mateix efecte que vam trobar i
        // reparar manualment a la BD un cop ja passat — vegeu e2e/BUGS_APLICACIO.md).
        // Es fa servir `auditorPage` (no `delegatPage`/`adminPage`) deliberadament: és
        // l'ÚNIC rol que cap altre fitxer de la suite usa, així que no hi ha manera que
        // un altre test, corrent en PARAL·LEL en un altre worker, faci login amb el
        // MATEIX usuari just en la finestra en què l'idioma és temporalment "ES" (una
        // condició de carrera real i observada: el `finally` garanteix que ES RESTAURA,
        // però no evita que un altre test l'observi TRANSITÒRIAMENT canviat mentrestant).
        try {
            await frame.locator('#idioma').selectOption(nou, { force: true });
            await clickModalFooterButtonById(page, 'btGuardarUsuariConfig');
            await waitForModalClosed(page);
            await expectSuccessMessage(page);

            await obrirMenuConfiguracio(page);
            await page.locator('#menu_user_configuracio').click();
            const frameRestore = await modalFrame(page);
            await expect(frameRestore.locator('#idioma')).toHaveValue(nou);
        } finally {
            // Si una modal ha quedat oberta d'un pas anterior fallit, la tanquem abans
            // de tornar-la a obrir per restaurar (best-effort: no volem que un error
            // aquí amagui l'error original del test).
            await activeModal(page).locator('.modal-header .close').click().catch(() => undefined);
            await waitForModalClosed(page).catch(() => undefined);

            await obrirMenuConfiguracio(page);
            await page.locator('#menu_user_configuracio').click();
            const frameFinal = await modalFrame(page);
            if ((await frameFinal.locator('#idioma').inputValue()) !== original) {
                await frameFinal.locator('#idioma').selectOption(original, { force: true });
                await clickModalFooterButtonById(page, 'btGuardarUsuariConfig');
                await waitForModalClosed(page);
            } else {
                await clickModalFooterButtonById(page, 'btCancelarUsuariConfig');
                await waitForModalClosed(page);
            }
        }
    });

    test('es pot cancel·lar sense desar canvis', async ({ auditorPage: page }) => {
        await obrirMenuConfiguracio(page);
        await page.locator('#menu_user_configuracio').click();

        const frame = await modalFrame(page);
        const original = await frame.locator('#idioma').inputValue();
        const nou = original === 'CA' ? 'ES' : 'CA';
        await frame.locator('#idioma').selectOption(nou, { force: true });

        await clickModalFooterButtonById(page, 'btCancelarUsuariConfig');
        await waitForModalClosed(page);

        // Reobrim el formulari i comprovem que el valor no ha canviat.
        await obrirMenuConfiguracio(page);
        await page.locator('#menu_user_configuracio').click();
        const frameAfter = await modalFrame(page);
        await expect(frameAfter.locator('#idioma')).toHaveValue(original);
        await clickModalFooterButtonById(page, 'btCancelarUsuariConfig');
        await waitForModalClosed(page);
    });
});
