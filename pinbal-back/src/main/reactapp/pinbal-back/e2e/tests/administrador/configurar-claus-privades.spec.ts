import { test, expect } from '../../utils/fixtures';
import { ClauPrivadaPage } from '../../pages/ClauPrivadaPage';
import { uniqueSuffix } from '../../utils/env';

/**
 * Manteniment "Claus privades" (`/scsp/clauprivada`, taula
 * `core_clave_privada`).
 */
test.describe('Claus privades (administrador)', () => {
    test('crear, editar i esborrar una clau privada', async ({ adminPage: page }) => {
        const suffix = uniqueSuffix();
        const alies = `e2e-clau-privada-crud-${suffix}`;
        const nomInicial = `Clau Privada E2E CRUD ${suffix}`;
        const nomModificat = `${nomInicial} modificada`;
        const numSerie = `E2E-PRIV-CRUD-${suffix}`;

        const clauPrivada = new ClauPrivadaPage(page);
        await clauPrivada.goto();

        // --- Crear ---
        const frameNew = await clauPrivada.openNew();

        // La creació requereix seleccionar un "organisme cessionari" (camp
        // obligatori, ClauPrivadaCommand#organismeId és @NotNull). L'entorn
        // e2e no en sembra cap (CORE_ORGANISMO_CESIONARIO és buida), així
        // que si no n'hi ha cap disponible es fa "skip" en comptes de fallar.
        const opcionsOrganisme = frameNew.locator('#organismeId option:not([value=""])');
        test.skip(
            (await opcionsOrganisme.count()) === 0,
            'No hi ha cap organisme cessionari sembrat a la BD e2e; no es pot crear una clau privada nova (camp obligatori).',
        );

        await frameNew.locator('#alies').fill(alies);
        await frameNew.locator('#nom').fill(nomInicial);
        await frameNew.locator('#password').fill(`e2e-pass-${suffix}`);
        await frameNew.locator('#numSerie').fill(numSerie);
        await frameNew.locator('#dataAlta').fill('01/01/2024');
        await frameNew.locator('#dataAlta').evaluate(el => el.blur());
        await frameNew.locator('#organismeId').selectOption({ index: 1 }, { force: true });
        await clauPrivada.save();

        await expect(clauPrivada.row(alies)).toBeVisible({ timeout: 15_000 });
        await expect(clauPrivada.row(alies)).toContainText(nomInicial);
        await expect(clauPrivada.row(alies)).toContainText(numSerie);

        // --- Editar ---
        const frameEdit = await clauPrivada.openEdit(alies);
        await frameEdit.locator('#nom').fill(nomModificat);
        await clauPrivada.save();
        await expect(clauPrivada.row(alies)).toContainText(nomModificat);

        // --- Esborrar (neteja de dades de prova) ---
        const esborrat = await clauPrivada.esborrar(alies);
        expect(esborrat).toBe(true);
        await expect(clauPrivada.row(alies)).toHaveCount(0);
    });

    test('el llistat mostra correctament les dades de les claus privades sembrades', async ({ adminPage: page }) => {
        const clauPrivada = new ClauPrivadaPage(page);
        await clauPrivada.goto();

        const filaActiva = clauPrivada.row('e2e-clau-privada-activa');
        await expect(filaActiva).toBeVisible({ timeout: 15_000 });
        await expect(filaActiva).toContainText('Clau Privada E2E Activa');
        await expect(filaActiva).toContainText('E2E-PRIV-0001');
        await expect(filaActiva).toContainText('01/01/2024');

        const filaBaixa = clauPrivada.row('e2e-clau-privada-baixa');
        await expect(filaBaixa).toBeVisible();
        await expect(filaBaixa).toContainText('Clau Privada E2E Donada de Baixa');
        await expect(filaBaixa).toContainText('E2E-PRIV-0002');
        await expect(filaBaixa).toContainText('15/06/2023');
        await expect(filaBaixa).toContainText('20/03/2024');
    });
});
