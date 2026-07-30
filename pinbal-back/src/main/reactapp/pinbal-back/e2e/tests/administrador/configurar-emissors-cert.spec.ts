import { test, expect } from '../../utils/fixtures';
import { EmissorCertPage } from '../../pages/EmissorCertPage';
import { uniqueSuffix } from '../../utils/env';
import { uniqueTestCif } from '../../utils/cif';

/**
 * Manteniment "Emissors certificats" (`/scsp/emissorcert`, taula
 * `core_emisor_certificado`).
 */
test.describe('Emissors certificats (administrador)', () => {
    test('crear, editar i esborrar un emissor certificat', async ({ adminPage: page }) => {
        const suffix = uniqueSuffix();
        const nomInicial = `Emissor E2E CRUD ${suffix}`;
        const nomModificat = `Emissor E2E CRUD ${suffix} modificat`;
        const cif = uniqueTestCif();

        const emissorCert = new EmissorCertPage(page);
        await emissorCert.goto();

        // --- Crear ---
        let frame = await emissorCert.openNew();
        await frame.locator('#nom').fill(nomInicial);
        await frame.locator('#cif').fill(cif);
        await emissorCert.save();

        await expect(emissorCert.row(cif)).toBeVisible({ timeout: 15_000 });
        await expect(emissorCert.row(cif)).toContainText(nomInicial);

        // --- Editar ---
        frame = await emissorCert.openEdit(cif);
        await frame.locator('#nom').fill(nomModificat);
        await frame.locator('#dataBaixa').fill('31/12/2030');
        await emissorCert.save();
        await expect(emissorCert.row(cif)).toContainText(nomModificat);
        await expect(emissorCert.row(cif)).toContainText('31/12/2030');

        // --- Esborrar (neteja de dades de prova) ---
        const esborrat = await emissorCert.esborrar(cif);
        expect(esborrat).toBe(true);
        await expect(emissorCert.row(cif)).toHaveCount(0);
    });

    test('el llistat mostra correctament les dades de l\'emissor sembrat', async ({ adminPage: page }) => {
        const emissorCert = new EmissorCertPage(page);
        await emissorCert.goto();

        const fila = emissorCert.row('P0700000A');
        await expect(fila).toBeVisible({ timeout: 15_000 });
        await expect(fila).toContainText('Emissor E2E');
        await expect(fila).toContainText('P0700000A');
    });
});
