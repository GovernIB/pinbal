import { chromium, FullConfig, Page } from '@playwright/test';
import { login } from './utils/auth';
import {
    credentials,
    consultaSimpleConfig,
    requireCredentials,
    SCSP_FAKE_ERROR_TRIGGER_DOC,
    SCSP_FAKE_SUCCESS_DOC,
    SCSP_FAKE_SUCCESS_DOC_2,
    uniqueSuffix,
} from './utils/env';
import { NovaConsultaPage } from './pages/NovaConsultaPage';

/**
 * Global setup de Playwright (vegeu
 * https://playwright.dev/docs/test-global-setup): s'executa un únic cop
 * abans de tota la suite. Aquí l'aprofitem per crear, a través de la UI real
 * (rol delegat), un petit conjunt de consultes simples i múltiples reals
 * contra el servidor fake de SCSP, de manera que els tests de llistat/detall/
 * descàrrega/estadístiques d'altres fitxers puguin assumir que ja existeixen
 * dades realistes en lloc de dependre només de files de BD creades a mà.
 *
 * És DELIBERADAMENT tolerant a fallades: qualsevol problema (credencials no
 * configurades, cap servei disponible amb els camps esperats, error de
 * validació...) es registra com a avís i es retorna, sense llançar cap
 * excepció. Una excepció aquí avortaria TOTA la suite, cosa molt pitjor que
 * disposar de menys dades de bootstrap.
 */

/**
 * Codis de servei coberts pel fake de SCSP (vegeu `SCSP_FAKE_ERROR_TRIGGER_DOC`
 * i `consultaSimpleConfig` a `utils/env.ts`). S'assumeix que, en l'entorn H2
 * de `scripts/e2e/run-e2e.sh`, aquests serveis ja estan donats d'alta per a
 * l'entitat del rol delegat i redirigits cap al fake.
 */
const DEFAULT_SERVEI_CODIS = ['Q2827003ATGSS001', 'SCDCPAJU', 'SVDDGTVEHICULOSANCWS01', 'SVDDGPCIWS02'];

/** Documents "normals" (NIF amb dígit de control vàlid) que el fake de SCSP respon amb èxit. */
const DOC_NORMAL_1 = SCSP_FAKE_SUCCESS_DOC;
const DOC_NORMAL_2 = SCSP_FAKE_SUCCESS_DOC_2;

interface ServeiCapabilities {
    hasSimpleDocument: boolean;
    hasMultipleFitxer: boolean;
    /**
     * Cert si el select `#procedimentId` del formulari té almenys una opció
     * per a l'usuari actual (vegeu `NovaConsultaPage.capacitats()`). Sense
     * cap opció, `seleccionarProcediment()` no pot fer res: normalment vol
     * dir que l'entorn té la fila de `pbl_procediment_servei` però no els
     * permisos ACL (`pbl_acl_*`) que calen perquè
     * `ProcedimentServiceImpl.findActiusAmbEntitatIServeiCodi` el retorni per
     * a aquest usuari.
     */
    hasProcediment: boolean;
}

function log(msg: string): void {
    console.log(`[global-setup] ${msg}`);
}

function warn(msg: string): void {
    console.warn(`[global-setup] AVÍS: ${msg}`);
}

/**
 * Visita el formulari de nova consulta d'un servei i detecta quines
 * modalitats permet, reutilitzant `NovaConsultaPage.capacitats()` (el mateix
 * mètode que fa servir `noves-consultes.spec.ts`).
 */
async function inspectServei(nova: NovaConsultaPage, serveiCodi: string): Promise<ServeiCapabilities | null> {
    try {
        await nova.goto(serveiCodi);
    } catch {
        return null;
    }
    const { formulariTrobat, teDocumentTitular, permetMultiple, teProcediment } = await nova.capacitats();
    if (!formulariTrobat) {
        // Servei no trobat, no assignat a l'entitat del delegat, o accés no autoritzat.
        return null;
    }
    return { hasSimpleDocument: teDocumentTitular, hasMultipleFitxer: permetMultiple, hasProcediment: teProcediment };
}

/** Emplena, si són presents, els camps opcionals del titular (nom/cognoms) per minimitzar el risc de validacions no previstes. */
async function emplenarTitularOpcional(page: Page, suffix: string): Promise<void> {
    const nom = page.locator('#titularNom');
    if (await nom.count()) await nom.fill(`Titular${suffix}`);
    const llinatge1 = page.locator('#titularLlinatge1');
    if (await llinatge1.count()) await llinatge1.fill('E2E');
    const nomComplet = page.locator('#titularNomComplet');
    if (await nomComplet.count()) await nomComplet.fill(`Titular E2E ${suffix}`);
}

async function crearConsultaSimple(
    page: Page,
    nova: NovaConsultaPage,
    serveiCodi: string,
    procedimentId: string | undefined,
    documentNum: string,
    label: string,
): Promise<void> {
    const suffix = uniqueSuffix();
    await nova.goto(serveiCodi);
    await nova.anarATabSimple();
    await nova.seleccionarProcediment(procedimentId);
    await nova.emplenarDadesGeneriques({
        funcionariNom: `Funcionari E2E Setup ${suffix}`,
        departamentNom: 'Departament E2E Setup',
        finalitat: `Dada de prova generada per global-setup (${label} ${suffix})`,
    });
    await nova.emplenarTitularDocument('NIF', documentNum);
    await emplenarTitularOpcional(page, suffix);
    await nova.enviar();

    const urlPath = new URL(page.url()).pathname;
    const redirectedToList = urlPath.endsWith('/consulta') || urlPath.endsWith('/consulta/');
    if (!redirectedToList) {
        warn(
            `consulta simple "${label}" (${serveiCodi}): el formulari no ha redirigit al llistat (` +
                `possibles validacions addicionals de dades específiques del servei no cobertes per aquest setup). ` +
                'Es continua igualment; és possible que aquesta consulta en concret no s\'hagi creat.',
        );
    } else {
        log(`consulta simple "${label}" creada correctament (${serveiCodi}, document ${documentNum}).`);
    }
}

async function crearConsultaMultiple(
    page: Page,
    nova: NovaConsultaPage,
    serveiCodi: string,
    procedimentId: string | undefined,
): Promise<void> {
    const suffix = uniqueSuffix();
    await nova.goto(serveiCodi);
    await nova.anarATabMultiple();
    await nova.seleccionarProcediment(procedimentId);
    await nova.emplenarDadesGeneriques({
        funcionariNom: `Funcionari E2E Setup ${suffix}`,
        departamentNom: 'Departament E2E Setup',
        finalitat: `Consulta múltiple de prova generada per global-setup (${suffix})`,
    });
    await nova.pujarFitxerMultiple(serveiCodi, [
        { documentTipus: 'NIF', documentNum: DOC_NORMAL_1, nom: 'Titular', llinatge1: `E2E ${suffix}` },
    ]);
    await nova.enviar();

    const errorsFitxer = nova.errorsFitxer();
    if (await errorsFitxer.isVisible().catch(() => false)) {
        const text = await errorsFitxer.innerText().catch(() => '(sense detall)');
        warn(`consulta múltiple (${serveiCodi}): el fitxer no ha passat la validació: ${text}`);
        return;
    }
    const urlPath = new URL(page.url()).pathname;
    if (!urlPath.endsWith('/consulta/multiple') && !urlPath.endsWith('/consulta/multiple/')) {
        warn(
            `consulta múltiple (${serveiCodi}): el formulari no ha redirigit al llistat de múltiples; ` +
                'és possible que no s\'hagi creat correctament.',
        );
        return;
    }
    log(`consulta múltiple creada correctament (${serveiCodi}).`);
}

export default async function globalSetup(config: FullConfig): Promise<void> {
    try {
        const creds = requireCredentials(credentials.delegat, 'delegat');

        const baseURL =
            (config.projects[0]?.use?.baseURL as string | undefined) ??
            process.env.E2E_BASE_URL ??
            'http://localhost:8080/pinbalback';

        const overrideConfig = consultaSimpleConfig();
        const candidats = overrideConfig ? [overrideConfig.serveiCodi] : DEFAULT_SERVEI_CODIS;
        const procedimentId = overrideConfig?.procedimentId;

        const browser = await chromium.launch();
        try {
            const context = await browser.newContext({ baseURL });
            const page = await context.newPage();
            await login(page, creds);
            const nova = new NovaConsultaPage(page);

            const capabilitats = new Map<string, ServeiCapabilities>();
            for (const codi of candidats) {
                const info = await inspectServei(nova, codi);
                if (info) capabilitats.set(codi, info);
            }

            // Cal tant el camp de document del titular com almenys una opció
            // real al select de procediment: sense procediment seleccionable
            // `seleccionarProcediment()` no pot fer res (vegeu el comentari a
            // `ServeiCapabilities.hasProcediment`).
            const serveiSimple = candidats.find(
                (codi) => capabilitats.get(codi)?.hasSimpleDocument && capabilitats.get(codi)?.hasProcediment,
            );
            if (serveiSimple) {
                await crearConsultaSimple(page, nova, serveiSimple, procedimentId, DOC_NORMAL_1, 'èxit 1');
                await crearConsultaSimple(page, nova, serveiSimple, procedimentId, DOC_NORMAL_2, 'èxit 2');
                await crearConsultaSimple(
                    page,
                    nova,
                    serveiSimple,
                    procedimentId,
                    SCSP_FAKE_ERROR_TRIGGER_DOC,
                    'error',
                );
            } else {
                const teDocumentSenseProcediment = candidats.some(
                    (codi) => capabilitats.get(codi)?.hasSimpleDocument && !capabilitats.get(codi)?.hasProcediment,
                );
                warn(
                    (teDocumentSenseProcediment
                        ? 'algun dels serveis candidats té el camp de document del titular actiu però ' +
                          'el select de procediment (#procedimentId) no té cap opció per a l\'usuari actual ' +
                          '(probablement falta l\'ACL de pbl_procediment_servei per a aquest usuari/entitat ' +
                          'a les dades de mostra); '
                        : 'cap dels serveis candidats té el camp de document del titular actiu; ') +
                        'no es crearà cap consulta simple de mostra. Candidats provats: ' +
                        candidats.join(', '),
                );
            }

            const serveiMultiple = candidats.find(
                (codi) => capabilitats.get(codi)?.hasMultipleFitxer && capabilitats.get(codi)?.hasProcediment,
            );
            if (serveiMultiple) {
                await crearConsultaMultiple(page, nova, serveiMultiple, procedimentId);
            } else {
                const permetMultipleSenseProcediment = candidats.some(
                    (codi) => capabilitats.get(codi)?.hasMultipleFitxer && !capabilitats.get(codi)?.hasProcediment,
                );
                warn(
                    (permetMultipleSenseProcediment
                        ? 'algun dels serveis candidats permet consulta múltiple però el select de procediment ' +
                          '(#procedimentId) no té cap opció per a l\'usuari actual (probablement falta l\'ACL de ' +
                          'pbl_procediment_servei per a aquest usuari/entitat a les dades de mostra); '
                        : 'cap dels serveis candidats permet consulta múltiple; ') +
                        `no es crearà cap consulta múltiple de mostra. Candidats provats: ${candidats.join(', ')}`,
                );
            }
        } finally {
            await browser.close();
        }
    } catch (err) {
        warn(
            'no s\'ha pogut completar la creació de dades de mostra (consultes simples/múltiples). ' +
                `La suite continuarà igualment amb menys dades de bootstrap. Detall: ${
                    err instanceof Error ? err.message : String(err)
                }`,
        );
    }
}
