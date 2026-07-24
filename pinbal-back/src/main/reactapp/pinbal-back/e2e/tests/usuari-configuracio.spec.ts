import { test, expect } from '../utils/fixtures';
import { clickModalFooterButtonById, modalFrame, waitForModalClosed } from '../utils/modal';
import { expectSuccessMessage } from '../utils/messages';

/**
 * La configuració d'usuari (menú d'usuari > "Configuració") és accessible
 * per a qualsevol rol autenticat, la qual cosa la fa un bon cas per validar
 * el patró general de modal amb iframe sense dependre de dades específiques
 * d'un rol concret (entitats, serveis SCSP...).
 */
test.describe('Configuració d\'usuari', () => {
    test('es pot obrir el formulari de configuració des del menú d\'usuari', async ({ delegatPage: page }) => {
        await page.locator('#menu_user').click();
        await page.locator('#menu_user_configuracio').click();

        const frame = await modalFrame(page);
        await expect(frame.locator('#idioma')).toBeVisible();
        await expect(frame.locator('#btGuardarUsuariConfig')).toBeVisible();
    });

    test('es pot canviar l\'idioma per defecte i es desa correctament', async ({ delegatPage: page }) => {
        await page.locator('#menu_user').click();
        await page.locator('#menu_user_configuracio').click();

        const frame = await modalFrame(page);
        const original = await frame.locator('#idioma').inputValue();
        const nou = original === 'CA' ? 'ES' : 'CA';

        await frame.locator('#idioma').selectOption(nou, { force: true });
        await clickModalFooterButtonById(page, 'btGuardarUsuariConfig');
        await waitForModalClosed(page);
        await expectSuccessMessage(page);

        // Restaura el valor original per no deixar efectes secundaris entre execucions de la suite.
        await page.locator('#menu_user').click();
        await page.locator('#menu_user_configuracio').click();
        const frameRestore = await modalFrame(page);
        await expect(frameRestore.locator('#idioma')).toHaveValue(nou);
        await frameRestore.locator('#idioma').selectOption(original, { force: true });
        await clickModalFooterButtonById(page, 'btGuardarUsuariConfig');
        await waitForModalClosed(page);
    });

    test('es pot cancel·lar sense desar canvis', async ({ delegatPage: page }) => {
        await page.locator('#menu_user').click();
        await page.locator('#menu_user_configuracio').click();

        const frame = await modalFrame(page);
        const original = await frame.locator('#idioma').inputValue();
        const nou = original === 'CA' ? 'ES' : 'CA';
        await frame.locator('#idioma').selectOption(nou, { force: true });

        await clickModalFooterButtonById(page, 'btCancelarUsuariConfig');
        await waitForModalClosed(page);

        // Reobrim el formulari i comprovem que el valor no ha canviat.
        await page.locator('#menu_user').click();
        await page.locator('#menu_user_configuracio').click();
        const frameAfter = await modalFrame(page);
        await expect(frameAfter.locator('#idioma')).toHaveValue(original);
        await clickModalFooterButtonById(page, 'btCancelarUsuariConfig');
        await waitForModalClosed(page);
    });
});
