import { test, expect } from '../../utils/fixtures';
import { InformeRepresentantPage } from '../../pages/InformeRepresentantPage';
import { ProcedimentServeiPermisosPage } from '../../pages/ProcedimentServeiPermisosPage';
import { USUARI_FIX_ACTIU_CODI } from '../../utils/env';

/**
 * Informe "Usuaris agrupats per òrgan gestor, procediment i servei"
 * (representant), `/informeRepresentant`.
 *
 * `ConsultaServiceImpl.informeUsuarisEntitatOrganProcedimentServei` només
 * retorna files per a combinacions procediment+servei amb almenys un PERMÍS
 * explícit concedit a un usuari (no n'hi ha prou que el servei estigui
 * afegit al procediment). Com que les dades sembrades no inclouen cap permís
 * (només l'associació procediment-servei E2EPROC01/Q2827003ATGSS001), aquest
 * test en concedeix un a l'usuari `E2E_USER_ACTIU` com a pas previ (via la
 * pantalla de permisos del propi procediment, ja provada de manera
 * independent a `procediments.spec.ts`) i el revoca al final per no deixar
 * estat residual.
 */
test.describe('Informe de representant', () => {
    test('mostra les dades del permís concedit i es pot filtrar per procediment/servei', async ({ representantPage: page }) => {
        // --- Preparació: concedim un permís perquè hi hagi alguna fila a l'informe ---
        const permisos = new ProcedimentServeiPermisosPage(page, 900201, 'Q2827003ATGSS001');
        await permisos.goto();
        const frame = await permisos.openNew();
        await permisos.seleccionarUsuari(frame, USUARI_FIX_ACTIU_CODI);
        await permisos.save();

        try {
            const informe = new InformeRepresentantPage(page);
            await informe.goto();

            // --- Visualització correcta de dades al llistat ---
            const fila = informe.row('E2EPROC01').filter({ hasText: 'Q2827003ATGSS001' }).filter({ hasText: USUARI_FIX_ACTIU_CODI });
            await expect(fila).toBeVisible({ timeout: 15_000 });

            // --- Filtre ---
            await informe.filtrarPerProcediment('E2EPROC01');
            await expect(informe.row('E2EPROC01')).toHaveCount(await informe.rows().count());
            await expect(fila).toBeVisible({ timeout: 15_000 });

            await informe.netejarFiltre();
            await informe.filtrarPerServei('Q2827003ATGSS001');
            const filesFiltradesPerServei = informe.rows();
            await expect(filesFiltradesPerServei.first()).toBeVisible({ timeout: 15_000 });
            const count = await filesFiltradesPerServei.count();
            for (let i = 0; i < count; i++) {
                await expect(filesFiltradesPerServei.nth(i)).toContainText('Q2827003ATGSS001');
            }
        } finally {
            // --- Neteja: revocam el permís concedit per no deixar estat residual ---
            await permisos.goto();
            await permisos.denegarAcces(USUARI_FIX_ACTIU_CODI);
        }
    });
});
