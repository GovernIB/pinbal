import { test, expect } from '../../utils/fixtures';
import { ClauPublicaPage } from '../../pages/ClauPublicaPage';
import { uniqueSuffix } from '../../utils/env';

/**
 * Manteniment "Claus públiques" (`/scsp/claupublica`, taula
 * `core_clave_publica`).
 */
test.describe('Claus públiques (administrador)', () => {
    test('crear, editar i esborrar una clau pública', async ({ adminPage: page }) => {
        const suffix = uniqueSuffix();
        const alies = `e2e-clau-publica-crud-${suffix}`;
        const nomInicial = `Clau Publica E2E CRUD ${suffix}`;
        const nomModificat = `Clau Publica E2E CRUD ${suffix} modificada`;
        const numSerie = `E2E-PUB-CRUD-${suffix}`;

        const clauPublica = new ClauPublicaPage(page);
        await clauPublica.goto();

        // --- Crear ---
        let frame = await clauPublica.openNew();
        await frame.locator('#alies').fill(alies);
        await frame.locator('#nom').fill(nomInicial);
        await frame.locator('#numSerie').fill(numSerie);
        await frame.locator('#dataAlta').fill('01/01/2024');
        await clauPublica.save();

        await expect(clauPublica.row(alies)).toBeVisible({ timeout: 15_000 });
        await expect(clauPublica.row(alies)).toContainText(nomInicial);
        await expect(clauPublica.row(alies)).toContainText(numSerie);

        // --- Editar ---
        frame = await clauPublica.openEdit(alies);
        await frame.locator('#nom').fill(nomModificat);
        // El formulari s'introdueix en format dd/MM/yyyy (CustomDateEditor),
        // però el llistat el renderitza amb guionets (moment 'DD-MM-YYYY').
        await frame.locator('#dataBaixa').fill('31/12/2030');
        await clauPublica.save();
        await expect(clauPublica.row(alies)).toContainText(nomModificat);
        await expect(clauPublica.row(alies)).toContainText('31-12-2030');

        // --- Esborrar (neteja de dades de prova) ---
        const esborrat = await clauPublica.esborrar(alies);
        expect(esborrat).toBe(true);
        await expect(clauPublica.row(alies)).toHaveCount(0);
    });

    test('el llistat mostra correctament les dades de la clau pública sembrada', async ({ adminPage: page }) => {
        const clauPublica = new ClauPublicaPage(page);
        await clauPublica.goto();

        const fila = clauPublica.row('e2e-clau-publica');
        await expect(fila).toBeVisible({ timeout: 15_000 });
        await expect(fila).toContainText('Clau Publica E2E');
        await expect(fila).toContainText('E2E-PUB-0001');
        await expect(fila).toContainText('01-01-2024');
    });
});
