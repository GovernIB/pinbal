import { test, expect } from '../../utils/fixtures';
import { EstadistiquesPage } from '../../pages/EstadistiquesPage';

/**
 * Estadístiques de consultes (rol administrador), `/estadistiques`.
 *
 * Les dades es calculen sobre una taula pre-agregada
 * (`pbl_explot_consulta_dim`/`pbl_explot_consulta_fet`), sembrada de forma
 * determinista i independent de qualsevol job batch (vegeu
 * `00_e2e_seed_data.yaml`, cometari "e2e-seed-explotacio"):
 *   - entitat E2EENT01 (id 900001, nom "Entitat E2E Principal")
 *   - snapshot de dades ("pbl_explot_temps") per a la data 01/06/2024
 *   - fets: num_rec_ok=12, num_rec_mass_ok=3, num_rec_error=2 (i els
 *     mateixos valors per a "web"), la resta a 0.
 *
 * El controlador (`EstadistiquesController`/`ConsultaServiceImpl`) suma els
 * comptadors "ok" i "mass_ok" en una sola xifra ("numRecobrimentOk" /
 * "numWebUIOk"), per la qual cosa el total mostrat a la UI per a
 * "Recobriment Ok" / "Web Ok" és 12+3=15 (superset que inclou els 12
 * sembrats), i el d'"Error" és 2+0=2. Per evitar qualsevol dependència
 * d'aquesta suma exacta (i de possible altra explotació sembrada en un
 * futur), els tests només comproven que el total mostrat és, com a mínim,
 * el valor sembrat (>=12 ok, >=2 error) i que els totals de la taula
 * quadren internament (el total general és la suma de les 4 columnes).
 *
 * Important: si no s'indica `dataFi` explícitament el controlador agafa
 * "ahir" i, en no trobar-hi cap snapshot, dispara en calent el job de
 * generació de dades d'explotació sobre les consultes reals (lent i no
 * determinista). Per evitar-ho, tots els filtres d'aquest fitxer fixen
 * `dataFi` a `01/06/2024` (que sí existeix, vegeu `EstadistiquesPage`) o a
 * una data futura llunyana (que talla immediatament sense disparar el job,
 * ja que el controlador només el dispara per a dates passades sense
 * snapshot).
 */
const ENTITAT_SEED = 'Entitat E2E Principal';
const DATA_FI_SEED = '01/06/2024';

test.describe('Estadístiques (administrador)', () => {
    test('es mostren dades per a l\'entitat filtrada i quadren amb els valors sembrats', async ({ adminPage: page }) => {
        const estadistiques = new EstadistiquesPage(page);
        await estadistiques.goto();
        await estadistiques.seleccionarEntitatSiCal(ENTITAT_SEED);

        await estadistiques.filtrar({ dataFi: DATA_FI_SEED });

        const totals = await estadistiques.totals();
        expect(totals, 'la taula d\'estadístiques hauria de mostrar-se amb la data sembrada').not.toBeNull();
        expect(totals!.recobrimentOk).toBeGreaterThanOrEqual(12);
        expect(totals!.webOk).toBeGreaterThanOrEqual(12);
        expect(totals!.recobrimentError).toBeGreaterThanOrEqual(2);
        expect(totals!.webError).toBeGreaterThanOrEqual(2);
        // Consistència interna: el total general és la suma de les 4 columnes (vegeu estadistiquesTaula.jsp).
        expect(totals!.total).toBe(
            totals!.recobrimentOk + totals!.webOk + totals!.recobrimentError + totals!.webError,
        );
    });

    test('un filtre impossible no retorna les dades inicials', async ({ adminPage: page }) => {
        const estadistiques = new EstadistiquesPage(page);
        await estadistiques.goto();
        await estadistiques.seleccionarEntitatSiCal(ENTITAT_SEED);

        // Baseline: amb la data sembrada, hi ha dades.
        await estadistiques.filtrar({ dataFi: DATA_FI_SEED });
        const totalsInicials = await estadistiques.totals();
        expect(totalsInicials).not.toBeNull();

        // Filtre "impossible": una data de fi futura molt llunyana, per a la
        // qual no existeix cap snapshot d'explotació (i que el controlador
        // NO intenta generar en calent per no ser una data passada), de
        // manera que la consulta no ha de trobar cap fet i la taula de
        // resultats no s'ha de mostrar.
        await estadistiques.filtrar({ dataFi: '31/12/2099' });
        const totalsFiltreImpossible = await estadistiques.totals();
        expect(totalsFiltreImpossible).toBeNull();
    });
});
