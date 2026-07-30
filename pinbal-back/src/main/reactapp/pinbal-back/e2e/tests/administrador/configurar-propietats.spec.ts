import { test, expect } from '../../utils/fixtures';
import { PropietatsPage } from '../../pages/PropietatsPage';

/**
 * Propietats de configuració de l'aplicació (/config, secció administrador).
 *
 * Dades de referència sembrades (00_e2e_seed_data.yaml, grup GENERAL):
 *  - e2e.test.editable.text     (TEXT, "valor editable", editable)
 *  - e2e.test.editable.bool     (BOOL, "true", editable)
 *  - e2e.test.editable.password (PASS, "secret-e2e-value", editable)
 *  - e2e.test.readonly.text     (TEXT, "valor no editable", NO editable:
 *    source_property != 'DATABASE')
 *
 * Aquesta pantalla no té un botó de "crear"/"esborrar" propietat: les files
 * les gestiona `PinbalPropertySourceLoader`/el seed, i la UI només permet
 * visualitzar-les i, per a les editables, modificar-ne el valor. Per això el
 * "CRUD" d'aquesta pantalla es cobreix aquí com a Read + Update (no hi ha
 * Create/Delete exposats).
 */
test.describe('Configurar propietats (administrador)', () => {
    test('el llistat de propietats es mostra correctament, agrupat per pestanyes', async ({ adminPage: page }) => {
        const propietats = new PropietatsPage(page);
        await propietats.goto();

        // La clau tècnica es mostra sempre en cru, independentment de si la
        // descripció es pot resoldre com a missatge de Spring.
        await expect(propietats.keyLabel('e2e.test.editable.text')).toContainText('e2e.test.editable.text');
        await expect(propietats.keyLabel('e2e.test.editable.bool')).toContainText('e2e.test.editable.bool');
        await expect(propietats.keyLabel('e2e.test.editable.password')).toContainText('e2e.test.editable.password');
        await expect(propietats.keyLabel('e2e.test.readonly.text')).toContainText('e2e.test.readonly.text');

        await expect(propietats.valueInput('e2e.test.editable.text')).toHaveValue('valor editable');
        await expect(propietats.valueInput('e2e.test.editable.bool')).toBeChecked();
        await expect(propietats.valueInput('e2e.test.readonly.text')).toHaveValue('valor no editable');
    });

    test('no es visualitza mai el valor real d\'una propietat de tipus password', async ({ adminPage: page }) => {
        const propietats = new PropietatsPage(page);
        await propietats.goto();

        const valor = await propietats.valueInput('e2e.test.editable.password').inputValue();
        expect(valor).not.toBe('secret-e2e-value');
        expect(valor).not.toContain('secret');
        // ConfigServiceImpl.processPropertyValues substitueix sempre el valor de tipus PASS per asteriscs abans d'arribar a la vista.
        expect(valor).toMatch(/^\*+$/);
    });

    test('una propietat editable es pot modificar i persisteix el nou valor (i es restaura en acabar)', async ({ adminPage: page }) => {
        const propietats = new PropietatsPage(page);
        const key = 'e2e.test.editable.text';
        const original = 'valor editable';
        const nou = `valor editat e2e ${Date.now()}`;

        await propietats.goto();
        await expect(propietats.valueInput(key)).toHaveValue(original);

        await propietats.saveTextValue(key, nou);
        // Missatge de "Propietat editada satisfactoriament" (config.controller.edit.ok), en un div propi de la propietat que s'autoelimina al cap de pocs segons.
        await expect(propietats.inlineMessage(key)).toBeVisible({ timeout: 5_000 });
        await expect(propietats.inlineMessage(key)).toContainText(/editad/i);

        // Recarreguem la pantalla i comprovem que el nou valor ha persistit.
        await propietats.goto();
        await expect(propietats.valueInput(key)).toHaveValue(nou);

        // Restaurem el valor original per no deixar contaminació entre execucions.
        await propietats.saveTextValue(key, original);
        await expect(propietats.inlineMessage(key)).toBeVisible({ timeout: 5_000 });

        await propietats.goto();
        await expect(propietats.valueInput(key)).toHaveValue(original);
    });

    test('una propietat editable de tipus booleà es pot commutar i persisteix el nou valor (i es restaura en acabar)', async ({ adminPage: page }) => {
        const propietats = new PropietatsPage(page);
        const key = 'e2e.test.editable.bool';

        await propietats.goto();
        const original = await propietats.valueInput(key).isChecked();

        await propietats.setBoolValue(key, !original);
        await expect(propietats.inlineMessage(key)).toBeVisible({ timeout: 5_000 });

        await propietats.goto();
        expect(await propietats.valueInput(key).isChecked()).toBe(!original);

        // Restaurem el valor original.
        await propietats.setBoolValue(key, original);
        await propietats.goto();
        expect(await propietats.valueInput(key).isChecked()).toBe(original);
    });

    test('una propietat no editable no es pot modificar des de la UI (input deshabilitat i sense botó de guardar)', async ({ adminPage: page }) => {
        const propietats = new PropietatsPage(page);
        const key = 'e2e.test.readonly.text';

        await propietats.goto();

        await expect(propietats.valueInput(key)).toBeDisabled();
        await expect(propietats.saveButton(key)).toHaveCount(0);
    });

    /**
     * No existeix cap camp de cerca/filtre dedicat a la pantalla de
     * propietats (config.jsp només agrupa per pestanyes de grup, sense cap
     * `<input>` de cerca ni DataTable): la manera d'assegurar-se que es pot
     * "cercar"/localitzar una propietat concreta és a través d'aquesta
     * organització per grups. Aquest test verifica que les propietats de
     * prova són efectivament localitzables dins la seva pestanya de grup.
     */
    test('les propietats es poden localitzar dins la seva pestanya de grup (no hi ha cerca dedicada a aquesta pantalla)', async ({ adminPage: page }) => {
        const propietats = new PropietatsPage(page);
        await propietats.goto();

        for (const key of [
            'e2e.test.editable.text',
            'e2e.test.editable.bool',
            'e2e.test.editable.password',
            'e2e.test.readonly.text',
        ]) {
            await expect(propietats.keyLabel(key)).toBeVisible();
        }
    });
});
