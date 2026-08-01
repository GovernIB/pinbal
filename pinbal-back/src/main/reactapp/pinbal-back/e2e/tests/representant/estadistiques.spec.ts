import { test, expect } from '../../utils/fixtures';
import { EstadistiquesPage } from '../../pages/EstadistiquesPage';
import { selectAjaxSuggestOption } from '../../utils/select2';

/**
 * Estadístiques (representant), `/estadistiques` — mateixa pantalla que fa
 * servir l'administrador (mateix JSP), reutilitzant `pages/EstadistiquesPage.ts`.
 * A diferència de l'administrador, el representant no veu mai el selector
 * d'entitat (sempre queda auto-seleccionada la seva pròpia), així que no cal
 * cridar `seleccionarEntitatSiCal`.
 *
 * IMPORTANT sobre les dates: `ConsultaServiceImpl.findEstadistiquesByFiltre`
 * fa servir una taula pre-agregada per "snapshot" (`pbl_explot_temps` +
 * `pbl_explot_consulta_fet`, acumulatiu): si `dataFi` no s'indica es fa
 * servir "ahir" i, si no hi ha snapshot per aquell dia, es genera en calent
 * (lent). Com que només hi ha UN snapshot sembrat (01/06/2024, vegeu
 * `pbl_explot_temps` id 900601 a `00_e2e_seed_data.yaml`), SEMPRE indiquem
 * `dataFi=01/06/2024` explícitament (fins i tot a la "primera" càrrega) per
 * evitar dependre d'aquest job. Per la mateixa raó NO fem servir `dataInici`
 * per a la comprovació "el filtre no canvia les dades" (la lògica és
 * acumulativa: dataInici=dataFi donaria zero, i qualsevol altra data sense
 * snapshot tornaria a disparar el job lent); en el seu lloc, per demostrar
 * que "modificar el filtre" no altera les dades quan el filtre coincideix
 * amb les dades existents, filtrem per servei/procediment (Q2827003ATGSS001 /
 * E2EPROC01, l'únic parell sembrat per a aquesta entitat) i comprovem que els
 * totals no canvien.
 */
test.describe('Estadístiques (representant)', () => {
    test('es mostren dades i filtrar per servei/procediment no altera els totals', async ({ representantPage: page }) => {
        const estadistiques = new EstadistiquesPage(page);
        await estadistiques.goto();

        await estadistiques.filtrar({ dataFi: '01/06/2024' });

        const totalsInicials = await estadistiques.totals();
        expect(totalsInicials).not.toBeNull();
        expect(totalsInicials!.recobrimentOk).toBe(15);
        expect(totalsInicials!.webOk).toBe(15);
        expect(totalsInicials!.recobrimentError).toBe(2);
        expect(totalsInicials!.webError).toBe(2);
        expect(totalsInicials!.total).toBe(34);

        // Filtram per exactament el servei/procediment de les dades sembrades:
        // com que és l'únic registre existent per a aquesta entitat, els totals
        // no haurien de canviar respecte als inicials. IMPORTANT: cal seleccionar
        // primer el procediment; en canviar-lo, el JS de la pàgina neteja la
        // selecció de servei (per refer la cerca acotada a aquest procediment).
        await selectAjaxSuggestOption(page, 'procediment', 'E2EPROC01', 'E2EPROC01');
        await selectAjaxSuggestOption(page, 'servei', 'Q2827003ATGSS001', 'Q2827003ATGSS001');
        // Vegeu el comentari a EstadistiquesPage.filtrar(): .fill() no actualitza
        // el model intern del bootstrap-datepicker propi de <pbl:inputDate>, i el
        // valor arriba buit al servidor; cal pressSequentially (tecla a tecla).
        await page.locator('#dataFi').clear();
        await page.locator('#dataFi').pressSequentially('01/06/2024');
        await page.locator('#dataFi').press('Escape');
        // waitForLoadState('load') es resoldria immediatament (la pàgina ja hi és, no espera cap
        // esdeveniment futur); cal waitForEvent('load'). Vegeu el comentari a EstadistiquesPage.filtrar().
        await Promise.all([
            page.waitForEvent('load'),
            page.locator('#filtrar').click(),
        ]);

        const totalsFiltrats = await estadistiques.totals();
        expect(totalsFiltrats).toEqual(totalsInicials);
    });
});
