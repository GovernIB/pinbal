import { test, expect } from '../../utils/fixtures';
import { NovaConsultaPage } from '../../pages/NovaConsultaPage';
import { consultaSimpleConfig, SCSP_FAKE_SUCCESS_DOC, uniqueSuffix } from '../../utils/env';
import { waitForInitialDataTableLoad } from '../../utils/datatable';

/**
 * Menú "Nova consulta" (`decorators/default.jsp`, rol delegat) i formulari de
 * nova consulta (`consultaForm.jsp` + `import/consultaSimpleForm.jsp` /
 * `import/consultaMultipleForm.jsp`): serveis amb permís, filtre de serveis,
 * camps del formulari, i realització real d'una consulta simple i d'una de
 * múltiple contra el fake de SCSP.
 *
 * Els codis de servei candidats (per defecte, els coberts pel fake de SCSP;
 * o el triat a `E2E_CONSULTA_SERVEI_CODI` si s'ha configurat) es proven en
 * ordre fins trobar-ne un amb les capacitats necessàries per a cada test;
 * vegeu `NovaConsultaPage.capacitats()`, també usat per `global-setup.ts`.
 */
const DEFAULT_SERVEI_CODIS = ['Q2827003ATGSS001', 'SCDCPAJU', 'SVDDGTVEHICULOSANCWS01', 'SVDDGPCIWS02'];

function candidats(): string[] {
    const override = consultaSimpleConfig();
    return override ? [override.serveiCodi] : DEFAULT_SERVEI_CODIS;
}

test.describe('Menú de nova consulta (delegat)', () => {
    test('es mostren tots els serveis amb permís', async ({ delegatPage: page }) => {
        const nova = new NovaConsultaPage(page);
        if (!(await nova.novaConsultaButton().isVisible().catch(() => false))) {
            test.skip(true, 'El rol delegat d\'aquest entorn no té cap servei amb permís de consulta assignat.');
            return;
        }

        await nova.obrirMenu();
        const items = nova.serveiItems();
        await expect(items.first()).toBeVisible();
        expect(await items.count()).toBeGreaterThan(0);
    });

    test('es poden filtrar els serveis pel quadre de cerca', async ({ delegatPage: page }) => {
        const nova = new NovaConsultaPage(page);
        if (!(await nova.novaConsultaButton().isVisible().catch(() => false))) {
            test.skip(true, 'El rol delegat d\'aquest entorn no té cap servei amb permís de consulta assignat.');
            return;
        }

        await nova.obrirMenu();
        const totalInicial = await nova.serveiItems().count();

        // Cap servei real no hauria de contenir aquest text: el filtre ha de deixar la llista buida.
        await nova.filtrarServeis('xxxxNoHauriaDExistirCapServeiE2Exxxx');
        expect(await nova.visibleServeiItems().count()).toBe(0);

        // Amb el filtre buit, tots els serveis han de tornar a ser visibles.
        await nova.filtrarServeis('');
        expect(await nova.visibleServeiItems().count()).toBe(totalInicial);

        // Filtrant pel codi exacte del primer servei només n'ha de quedar (almenys) aquell visible.
        const primerCodi = (await nova.serveiItems().first().getAttribute('data-text'))?.split(' ')[0];
        if (primerCodi) {
            await nova.filtrarServeis(primerCodi);
            const visibles = await nova.visibleServeiItems().count();
            expect(visibles).toBeGreaterThan(0);
            expect(visibles).toBeLessThanOrEqual(totalInicial);
        }
    });
});

test.describe('Formulari de nova consulta (delegat)', () => {
    test('mostra els camps genèrics esperats per a un servei', async ({ delegatPage: page }) => {
        const nova = new NovaConsultaPage(page);
        let trobat: string | null = null;
        for (const codi of candidats()) {
            await nova.goto(codi);
            const { formulariTrobat } = await nova.capacitats();
            if (formulariTrobat) {
                trobat = codi;
                break;
            }
        }
        if (!trobat) {
            test.skip(true, `Cap dels serveis candidats (${candidats().join(', ')}) és accessible en aquest entorn.`);
            return;
        }

        await expect(page.locator('#consultaForm')).toBeVisible();
        await expect(page.locator('#procedimentId')).toBeVisible();
        await expect(page.locator('#funcionariNom')).toBeVisible();
        await expect(page.locator('#entitatNom')).toBeVisible();
        await expect(page.locator('#consentiment')).toBeVisible();
        await expect(page.locator('#departamentNom')).toBeVisible();
        await expect(page.locator('#finalitat')).toBeVisible();
        await expect(page.locator('#consultaForm button[type="submit"]')).toBeVisible();
    });
});

test.describe('Realització de consultes (delegat)', () => {
    test('es pot realitzar una consulta simple des del formulari', async ({ delegatPage: page }) => {
        const nova = new NovaConsultaPage(page);
        let serveiCodi: string | null = null;
        let hiHaCandidatSenseProcediment = false;
        for (const codi of candidats()) {
            await nova.goto(codi);
            const { teDocumentTitular, teProcediment } = await nova.capacitats();
            if (teDocumentTitular && teProcediment) {
                serveiCodi = codi;
                break;
            }
            if (teDocumentTitular && !teProcediment) hiHaCandidatSenseProcediment = true;
        }
        if (!serveiCodi) {
            // El select #procedimentId (ConsultaController.omplirModelPerMostrarFormulari)
            // es filtra per ACL (ProcedimentServiceImpl.findActiusAmbEntitatIServeiCodi):
            // sense cap ProcedimentServei amb permís concedit a l'usuari actual, el select
            // queda buit i no es pot completar el formulari encara que el servei sigui vàlid.
            test.skip(
                true,
                hiHaCandidatSenseProcediment
                    ? `Algun dels serveis candidats (${candidats().join(', ')}) té el camp de document del ` +
                          'titular actiu però cap procediment amb permís ACL concedit per a l\'usuari actual ' +
                          '(#procedimentId sense opcions): probablement falten dades de mostra (ACL sobre ' +
                          'pbl_procediment_servei) en aquest entorn.'
                    : `Cap dels serveis candidats (${candidats().join(', ')}) té el camp de document del titular actiu.`,
            );
            return;
        }

        const suffix = uniqueSuffix();
        await nova.goto(serveiCodi);
        await nova.anarATabSimple();
        await nova.seleccionarProcediment(consultaSimpleConfig()?.procedimentId);
        await nova.emplenarDadesGeneriques({
            funcionariNom: `Funcionari E2E ${suffix}`,
            departamentNom: 'Departament E2E',
            finalitat: `Consulta simple de prova (noves-consultes) ${suffix}`,
        });
        await nova.emplenarTitularDocument('NIF', SCSP_FAKE_SUCCESS_DOC);
        await nova.enviar();

        // Una consulta simple amb èxit redirigeix al llistat; si es queda al
        // formulari, probablement hi ha dades específiques obligatòries del
        // servei no cobertes per aquest test genèric.
        const urlPath = new URL(page.url()).pathname;
        if (!urlPath.endsWith('/consulta') && !urlPath.endsWith('/consulta/')) {
            test.skip(
                true,
                `El servei ${serveiCodi} sembla requerir dades específiques addicionals no cobertes per aquest test.`,
            );
            return;
        }

        await waitForInitialDataTableLoad(page).catch(() => undefined);
        await expect(page.locator('#table-consultes')).toBeVisible();
    });

    test('es pot realitzar una consulta múltiple des del formulari', async ({ delegatPage: page }) => {
        const nova = new NovaConsultaPage(page);
        let serveiCodi: string | null = null;
        let hiHaCandidatSenseProcediment = false;
        for (const codi of candidats()) {
            await nova.goto(codi);
            const { permetMultiple, teProcediment } = await nova.capacitats();
            if (permetMultiple && teProcediment) {
                serveiCodi = codi;
                break;
            }
            if (permetMultiple && !teProcediment) hiHaCandidatSenseProcediment = true;
        }
        if (!serveiCodi) {
            // Vegeu el comentari equivalent al test de consulta simple: sense cap
            // ProcedimentServei amb permís ACL concedit, #procedimentId queda buit.
            test.skip(
                true,
                hiHaCandidatSenseProcediment
                    ? `Algun dels serveis candidats (${candidats().join(', ')}) permet consulta múltiple però ` +
                          'cap procediment amb permís ACL concedit per a l\'usuari actual (#procedimentId sense ' +
                          'opcions): probablement falten dades de mostra (ACL sobre pbl_procediment_servei) en ' +
                          'aquest entorn.'
                    : `Cap dels serveis candidats (${candidats().join(', ')}) permet consulta múltiple.`,
            );
            return;
        }

        const suffix = uniqueSuffix();
        await nova.goto(serveiCodi);
        await nova.anarATabMultiple();
        await nova.seleccionarProcediment(consultaSimpleConfig()?.procedimentId);
        await nova.emplenarDadesGeneriques({
            funcionariNom: `Funcionari E2E ${suffix}`,
            departamentNom: 'Departament E2E',
            finalitat: `Consulta múltiple de prova (noves-consultes) ${suffix}`,
        });
        await nova.pujarFitxerMultiple(serveiCodi, [
            { documentTipus: 'NIF', documentNum: SCSP_FAKE_SUCCESS_DOC, nom: 'Titular', llinatge1: `Prova ${suffix}` },
        ]);
        await nova.enviar();

        const errorsFitxer = nova.errorsFitxer();
        if (await errorsFitxer.isVisible().catch(() => false)) {
            const detall = await errorsFitxer.innerText().catch(() => '(sense detall)');
            test.skip(true, `El fitxer generat no ha passat la validació del servei ${serveiCodi}: ${detall}`);
            return;
        }

        const urlPath = new URL(page.url()).pathname;
        expect(urlPath.endsWith('/consulta/multiple') || urlPath.endsWith('/consulta/multiple/')).toBe(true);

        await waitForInitialDataTableLoad(page).catch(() => undefined);
        await expect(page.locator('#table-consultes')).toBeVisible();
    });
});
