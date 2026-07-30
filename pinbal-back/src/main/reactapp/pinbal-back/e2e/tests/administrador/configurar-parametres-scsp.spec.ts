import { test, expect } from '../../utils/fixtures';
import { ParamConfPage } from '../../pages/ParamConfPage';
import { uniqueSuffix } from '../../utils/env';

/**
 * Manteniment "Paràmetres SCSP" (`/scsp/paramconf`, taula
 * `core_parametro_configuracion`, clau primària = `nombre`).
 */
test.describe('Paràmetres SCSP (administrador)', () => {
    test('crear, editar i esborrar un paràmetre de configuració', async ({ adminPage: page }) => {
        const suffix = uniqueSuffix();
        const nom = `E2E_PARAM_CRUD_${suffix}`.toUpperCase().slice(0, 64);
        const valorInicial = `valor inicial ${suffix}`;
        const valorModificat = `valor modificat ${suffix}`;

        const paramConf = new ParamConfPage(page);
        await paramConf.goto();

        // --- Crear ---
        let frame = await paramConf.openNew();
        await frame.locator('#nom').fill(nom);
        await frame.locator('#valor').fill(valorInicial);
        await frame.locator('#descripcio').fill(`Descripció E2E ${suffix}`);
        await paramConf.save();

        await expect(paramConf.row(nom)).toBeVisible({ timeout: 15_000 });
        await expect(paramConf.row(nom)).toContainText(valorInicial);

        // --- Editar ---
        frame = await paramConf.openEdit(nom);
        await frame.locator('#valor').fill(valorModificat);
        await paramConf.save();
        await expect(paramConf.row(nom)).toContainText(valorModificat);

        // --- Esborrar (neteja de dades de prova) ---
        const esborrat = await paramConf.esborrar(nom);
        expect(esborrat).toBe(true);
        await expect(paramConf.row(nom)).toHaveCount(0);
    });

    test('el llistat mostra correctament les dades dels paràmetres', async ({ adminPage: page }) => {
        const paramConf = new ParamConfPage(page);
        await paramConf.goto();

        const filaU = paramConf.row('E2E_PARAM_U');
        await expect(filaU).toBeVisible({ timeout: 15_000 });
        await expect(filaU).toContainText('E2E_PARAM_U');
        await expect(filaU).toContainText('valor-u');

        const filaDos = paramConf.row('E2E_PARAM_DOS');
        await expect(filaDos).toBeVisible();
        await expect(filaDos).toContainText('E2E_PARAM_DOS');
        await expect(filaDos).toContainText('valor-dos');
    });

    test('no es pot editar el nom d\'un paràmetre existent', async ({ adminPage: page }) => {
        const paramConf = new ParamConfPage(page);
        await paramConf.goto();

        const frame = await paramConf.openEdit('E2E_PARAM_U');

        const campNom = frame.locator('#nom');
        await expect(campNom).toHaveValue('E2E_PARAM_U');
        // El camp "nom" es la clau primària: en modificació ha d'estar en
        // només-lectura (readonly), no un input editable normal.
        await expect(campNom).not.toBeEditable();

        // Es tanca sense desar, per no interferir amb altres tests que usen
        // aquesta mateixa fila sembrada.
        await paramConf.cancel();
    });
});
