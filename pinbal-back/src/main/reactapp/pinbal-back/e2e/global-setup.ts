import { Browser, chromium, FullConfig, Page } from '@playwright/test';
import { login } from './utils/auth';
import {
    credentials,
    consultaSimpleConfig,
    Credentials,
    requireCredentials,
    SCSP_FAKE_ERROR_TRIGGER_DOC,
    SCSP_FAKE_SUCCESS_DOC,
    SCSP_FAKE_SUCCESS_DOC_2,
    uniqueSuffix,
} from './utils/env';
import { NovaConsultaPage } from './pages/NovaConsultaPage';
import { clickModalFooterButtonById, modalFrame, waitForModalClosed } from './utils/modal';

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

/**
 * Força l'idioma de configuració de l'usuari actualment loguejat a català
 * (`#idioma` = 'CA'), mitjançant el mateix modal "Configuració" que usa
 * `usuari-configuracio.spec.ts`.
 *
 * Motivació: `pbl_usuari.idioma` és una preferència PERSISTENT per usuari, no
 * només de sessió; els 4 usuaris de rol (`E2E_ADMIN_USERNAME`, etc.) es
 * sembren amb `idioma='ca'` (vegeu `00_e2e_seed_data.yaml`), però un test que
 * el canviï temporalment (per provar precisament aquesta funcionalitat) i no
 * arribi a restaurar-lo -- per fallar a mig test, o per una condició de
 * carrera amb un altre test corrent en PARAL·LEL contra el MATEIX usuari
 * mentre l'idioma és temporalment un altre -- el deixa "encallat" per a la
 * resta de la sessió. Com que gairebé tots els altres tests d'aquesta suite
 * cerquen text en català, l'efecte és una cascada de fallades gens òbvies
 * (semblen problemes de dades/permisos, no d'idioma). Forçar-ho aquí, abans
 * de qualsevol altra cosa, fa que cada execució de la suite s'autorepari
 * independentment de l'estat que hagi deixat una execució anterior.
 */
async function ensureIdiomaCatala(page: Page, roleName: string): Promise<void> {
    try {
        if (!(await page.locator('#menu_user_configuracio').isVisible().catch(() => false))) {
            await page.locator('#menu_user').click();
        }
        await page.locator('#menu_user_configuracio').click();
        const frame = await modalFrame(page);
        const actual = await frame.locator('#idioma').inputValue();
        if (actual !== 'CA') {
            await frame.locator('#idioma').selectOption('CA', { force: true });
            await clickModalFooterButtonById(page, 'btGuardarUsuariConfig');
            await waitForModalClosed(page);
            log(`idioma del rol "${roleName}" corregit a català (era "${actual}").`);
        } else {
            await clickModalFooterButtonById(page, 'btCancelarUsuariConfig');
            await waitForModalClosed(page);
        }
    } catch (err) {
        warn(`no s'ha pogut comprovar/corregir l'idioma del rol "${roleName}": ${err instanceof Error ? err.message : String(err)}`);
    }
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
    documentTipus: string = 'NIF',
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
    await nova.emplenarTitularDocument(documentTipus, documentNum);
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

/**
 * Pas 0 del setup: força l'idioma a català per a tots els rols configurats,
 * abans de res més (vegeu el comentari d'`ensureIdiomaCatala`). Cada rol usa
 * el seu propi context/login, completament tolerant a fallades individuals:
 * mai avorta la resta del setup.
 */
async function corregirIdiomaTotsElsRols(browser: Browser, baseURL: string): Promise<void> {
    const rols: Array<{ name: string; getter: () => Credentials | null }> = [
        { name: 'admin', getter: credentials.admin },
        { name: 'delegat', getter: credentials.delegat },
        { name: 'representant', getter: credentials.representant },
        { name: 'auditor', getter: credentials.auditor },
    ];
    for (const rol of rols) {
        const rolCreds = rol.getter();
        if (!rolCreds) continue;
        const rolContext = await browser.newContext({ baseURL });
        try {
            const rolPage = await rolContext.newPage();
            await login(rolPage, rolCreds);
            await ensureIdiomaCatala(rolPage, rol.name);
        } catch (err) {
            warn(`no s'ha pogut iniciar sessió amb el rol "${rol.name}" per corregir-ne l'idioma: ${err instanceof Error ? err.message : String(err)}`);
        } finally {
            await rolContext.close();
        }
    }
}

/** Pas 1 del setup: crea consultes de mostra reals amb el rol delegat (vegeu la capçalera del fitxer). */
async function crearDadesDeMostra(browser: Browser, baseURL: string): Promise<void> {
    const creds = requireCredentials(credentials.delegat, 'delegat');

    const overrideConfig = consultaSimpleConfig();
    const candidats = overrideConfig ? [overrideConfig.serveiCodi] : DEFAULT_SERVEI_CODIS;
    const procedimentId = overrideConfig?.procedimentId;

    const context = await browser.newContext({ baseURL });
    try {
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
            // 'Passaport' i no 'NIF': el disparador d'error del fake SCSP (SCSP_FAKE_ERROR_TRIGGER_DOC
            // = '00000000ERR', vegeu FAKE_SCSP_SERVER.md) no és un NIF de format vàlid, i
            // DocumentIdentitatValidator aplica la validació de checksum NIF/DNI/NIE/CIF però
            // accepta qualsevol valor per a 'Passaport' sense cap comprovació de format. Amb
            // 'NIF' el formulari rebutja sempre el document abans d'arribar a SCSP ("Número de
            // document invàlid") -- causa arrel de l'avís "el formulari no ha redirigit al
            // llistat" que sortia SEMPRE per a la consulta "error" (vegeu e2e/BUGS_APLICACIO.md).
            await crearConsultaSimple(
                page,
                nova,
                serveiSimple,
                procedimentId,
                SCSP_FAKE_ERROR_TRIGGER_DOC,
                'error',
                'Passaport',
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
        await context.close();
    }
}

export default async function globalSetup(config: FullConfig): Promise<void> {
    const baseURL =
        (config.projects[0]?.use?.baseURL as string | undefined) ??
        process.env.E2E_BASE_URL ??
        'http://localhost:8080/pinbalback';

    const browser = await chromium.launch();
    try {
        try {
            await corregirIdiomaTotsElsRols(browser, baseURL);
        } catch (err) {
            warn(`pas de correcció d'idioma fallit inesperadament: ${err instanceof Error ? err.message : String(err)}`);
        }

        try {
            await crearDadesDeMostra(browser, baseURL);
        } catch (err) {
            warn(
                'no s\'ha pogut completar la creació de dades de mostra (consultes simples/múltiples). ' +
                    `La suite continuarà igualment amb menys dades de bootstrap. Detall: ${
                        err instanceof Error ? err.message : String(err)
                    }`,
            );
        }
    } finally {
        await browser.close();
    }
}
