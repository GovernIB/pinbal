import { test, expect } from '../../utils/fixtures';
import { RepresentantUsuarisPage } from '../../pages/RepresentantUsuarisPage';
import { RepresentantUsuariPermisosPage } from '../../pages/RepresentantUsuariPermisosPage';
import { ProcedimentsPage } from '../../pages/ProcedimentsPage';
import { ProcedimentServeisPage } from '../../pages/ProcedimentServeisPage';
import { waitForInitialDataTableLoad } from '../../utils/datatable';
import { uniqueSuffix, USUARI_FIX_ACTIU_CODI, USUARI_FIX_INACTIU_CODI } from '../../utils/env';

/**
 * Usuaris (representant), `/representant/usuari` (`representantUsuaris.jsp`).
 *
 * LIMITACIÓ D'ENTORN IMPORTANT sobre el bullet "CRUD": el camp "codi" del
 * formulari de creació (`representantUsuariForm.jsp`) és un `pbl:inputSuggest`
 * (select2 amb cerca ajax) que consulta `/usuariajax/usuari/externs/{text}`
 * (`AjaxUsuariController` → `usuariService.getUsuarisExterns`), que delega en
 * `DadesUsuariPlugin` (implementacions reals: JDBC/LDAP/Keycloak contra un
 * directori corporatiu extern). Aquest plugin NO està configurat en aquest
 * entorn e2e (H2): `ConfigHelper` només llegeix `pbl_config`/l'`Environment`
 * de Spring i la classe que hauria de carregar-hi les propietats de BD
 * (`ReadDbPropertiesPostProcessor`) és no-op; no hi ha cap fila de
 * `pbl_config` sembrada per a la clau `es.caib.pinbal.plugin.dades.usuari.class`.
 * Per tant, cercar QUALSEVOL codi nou al desplegable de cerca de la pantalla
 * de crear falla sempre en aquest entorn.
 *
 * Ara bé, `UsuariServiceImpl.usuariActualitzarDades` només crida aquest
 * plugin quan l'usuari (pel `codi`) NO existeix encara o no està
 * `inicialitzat` a `pbl_usuari`; per als usuaris ja sembrats i inicialitzats
 * (`E2E_USER_ACTIU`/`E2E_USER_INACTIU`, `principal=0` i per tant no
 * protegits) l'alta/actualització es resol totalment contra la BD pròpia,
 * sense dependre del plugin. Així doncs, aquest test exercita el formulari i
 * el flux complet de "Crear"/afegir contra `E2E_USER_INACTIU`, injectant
 * directament el valor del `<select>` subjacent (workaround NOMÉS per
 * evitar el desplegable de cerca trencat, no per saltar-se cap validació de
 * servidor) i restaura l'estat original al final del test perquè no quedi
 * estat residual per a altres tests/execucions.
 */

const NOU_USUARI_CODI = USUARI_FIX_INACTIU_CODI;
const USUARI_ACTIU_CODI = USUARI_FIX_ACTIU_CODI;
/**
 * Usuari ja inicialitzat a `pbl_usuari` (evita el desplegable ajax trencat,
 * vegeu capçalera) però SENSE cap `pbl_entitat_usuari` seguit (a diferència
 * d'ACTIU/INACTIU/ALL_ROLES, ja vinculats a l'entitat E2E perquè els
 * necessiten altres tests d'aquest fitxer): l'únic candidat vàlid per
 * exercitar realment el flux "Crear/afegir" sense xocar amb un vincle ja
 * existent (vegeu `00_e2e_seed_data.yaml`, changeset `e2e-seed-usuaris-fixos`).
 */
const USUARI_PER_AFEGIR_CODI = 'E2E_USER_SENSE_ENTITAT';

/** Selecciona un valor al `<select>` "codi" evitant el desplegable de cerca ajax (vegeu nota de capçalera). */
async function seleccionarCodiUsuariSenseAjax(frame: import('@playwright/test').FrameLocator, codi: string): Promise<void> {
    await frame.locator('#codi').evaluate((el: HTMLSelectElement, valor: string) => {
        let opt = el.querySelector(`option[value="${valor}"]`) as HTMLOptionElement | null;
        if (!opt) {
            opt = document.createElement('option');
            opt.value = valor;
            opt.text = valor;
            el.appendChild(opt);
        }
        el.value = valor;
        el.dispatchEvent(new Event('change', { bubbles: true }));
    }, codi);
}

test.describe('Usuaris (representant)', () => {
    test('CRUD (afegir/editar) i visualització correcta de dades al llistat', async ({ representantPage: page }) => {
        const suffix = uniqueSuffix();
        const departament1 = `Departament E2E ${suffix}`;
        const departament2 = `Departament E2E ${suffix} modificat`;

        const usuaris = new RepresentantUsuarisPage(page);
        await usuaris.goto();

        // --- Crear/afegir ---
        // NOTA: NOU_USUARI_CODI (E2E_USER_INACTIU) ja té un pbl_entitat_usuari
        // sembrat per a aquesta mateixa entitat (necessari pel test de
        // filtre més avall), així que "afegir-lo" de nou xocaria amb el
        // vincle existent. Feim servir USUARI_PER_AFEGIR_CODI, l'únic usuari
        // ja inicialitzat però encara SENSE cap vincle d'entitat.
        let frame = await usuaris.openNew();
        await seleccionarCodiUsuariSenseAjax(frame, USUARI_PER_AFEGIR_CODI);
        await frame.locator('#departament').fill(departament1);
        await frame.locator('#rolDelegat').check();
        await frame.locator('#rolAplicacio').check();
        await usuaris.save();

        const fila = () => usuaris.row(USUARI_PER_AFEGIR_CODI);
        await expect(fila()).toBeVisible({ timeout: 15_000 });
        // --- Visualització correcta de dades al llistat ---
        await expect(fila()).toContainText(departament1);
        await expect(fila().getByText(/deleg/i)).toBeVisible();
        await expect(fila().getByText(/aplic/i)).toBeVisible();

        // --- Editar ---
        frame = await usuaris.openEdit(USUARI_PER_AFEGIR_CODI);
        await frame.locator('#departament').fill(departament2);
        await usuaris.save();
        await expect(fila()).toContainText(departament2);

        // --- Neteja: aquesta pantalla no ofereix "esborrar" per al representant
        // (vegeu template-accions a representantUsuaris.jsp: només "Modificar" i
        // "Activar"/"Desactivar"), així que no es pot desfer del tot el vincle
        // creat via UI. Deixam-lo desactivat (l'únic estat "net" disponible); el
        // test només es pot tornar a executar contra un entorn e2e fresc (BD H2
        // efímera), ja que un segon "Crear/afegir" pel mateix usuari xocaria amb
        // aquest vincle ja existent.
        await usuaris.desactivar(USUARI_PER_AFEGIR_CODI);
    });

    test('filtre per codi i per estat actiu/inactiu', async ({ representantPage: page }) => {
        const usuaris = new RepresentantUsuarisPage(page);
        await usuaris.goto();

        // NOTA: el filtre per defecte (sense aplicar cap, `UsuariFiltreCommand.actiu`
        // = `UsuariEstatEnum.ACTIU`) només mostra usuaris actius, així que
        // NOU_USUARI_CODI (E2E_USER_INACTIU) NO ha de ser visible encara aquí.
        await expect(usuaris.row(USUARI_ACTIU_CODI)).toBeVisible({ timeout: 15_000 });

        // Filtre per codi
        await usuaris.filtrar(async (form) => {
            await form.locator('#codi').fill(USUARI_ACTIU_CODI);
        });
        await expect(usuaris.row(USUARI_ACTIU_CODI)).toBeVisible({ timeout: 15_000 });
        await expect(usuaris.row(NOU_USUARI_CODI)).toHaveCount(0);
        await usuaris.netejarFiltre();

        // Filtre per estat actiu
        await expect(page.locator('#actiu option[value="ACTIU"]')).toBeAttached({ timeout: 10_000 });
        await usuaris.filtrar(async (form) => {
            await form.locator('#actiu').selectOption('ACTIU', { force: true });
        });
        await expect(usuaris.row(USUARI_ACTIU_CODI)).toBeVisible({ timeout: 15_000 });
        await expect(usuaris.row(NOU_USUARI_CODI)).toHaveCount(0);
        await usuaris.netejarFiltre();

        // Filtre per estat inactiu
        await usuaris.filtrar(async (form) => {
            await form.locator('#actiu').selectOption('INACTIU', { force: true });
        });
        await expect(usuaris.row(NOU_USUARI_CODI)).toBeVisible({ timeout: 15_000 });
        await expect(usuaris.row(USUARI_ACTIU_CODI)).toHaveCount(0);
        await usuaris.netejarFiltre();
    });

    test('activar/desactivar', async ({ representantPage: page }) => {
        const usuaris = new RepresentantUsuarisPage(page);
        await usuaris.goto();

        const fila = () => usuaris.row(USUARI_ACTIU_CODI);
        await expect(fila()).toBeVisible({ timeout: 15_000 });
        await expect(fila().locator('i.fa-check, i.fas.fa-check, svg.fa-check')).toBeVisible();

        await usuaris.desactivar(USUARI_ACTIU_CODI);
        // El filtre per defecte (actiu=ACTIU) amaga la fila un cop desactivada,
        // així que cal seleccionar l'opció buida (mostra tots els estats,
        // `emptyOption="true"` a representantUsuaris.jsp) per tornar-la a veure
        // i poder-la reactivar.
        await usuaris.filtrar(async (form) => {
            await form.locator('#actiu').selectOption('', { force: true });
        });
        await expect(fila().locator('i.fa-check, i.fas.fa-check, svg.fa-check')).toHaveCount(0);

        // Restaurem l'estat original (actiu=1, tal com el sembra 00_e2e_seed_data.yaml).
        await usuaris.activar(USUARI_ACTIU_CODI);
        await expect(fila().locator('i.fa-check, i.fas.fa-check, svg.fa-check')).toBeVisible();
    });

    test.describe('Permisos', () => {
        test('afegir, denegar accés, esborrar seleccionats i esborrar tots', async ({ representantPage: page }) => {
            const suffix = uniqueSuffix();
            const codiProcediment = `E2EU${suffix}`.toUpperCase().slice(0, 20);

            // --- Preparació: procediment propi amb dos serveis, per no interferir amb altres tests ---
            const procediments = new ProcedimentsPage(page);
            await procediments.goto();
            const frameProc = await procediments.openNew();
            await frameProc.locator('#codi').fill(codiProcediment);
            await frameProc.locator('#nom').fill(`Procediment permisos usuari E2E ${suffix}`);
            await frameProc.locator('#organGestorId').selectOption('900301', { force: true });
            await procediments.save();
            await expect(procediments.row(codiProcediment)).toBeVisible({ timeout: 15_000 });

            await procediments.obrirServeis(codiProcediment);
            await waitForInitialDataTableLoad(page);
            const procedimentId = new URL(page.url()).pathname.match(/\/procediment\/(\d+)\/servei/)?.[1];
            expect(procedimentId).toBeTruthy();

            const serveisPage = new ProcedimentServeisPage(page, procedimentId!);
            for (const codiServei of ['Q2827003ATGSS001', 'SCDCPAJU']) {
                const frameServei = await serveisPage.openNew();
                await frameServei.locator('#serveiCodi').selectOption(codiServei, { force: true });
                await serveisPage.save();
                await expect(serveisPage.row(codiServei)).toBeVisible({ timeout: 15_000 });
            }

            // --- Permisos: afegir (els dos serveis d'un sol cop) ---
            const permisos = new RepresentantUsuariPermisosPage(page, USUARI_ACTIU_CODI);
            await permisos.goto();
            const frameAfegir = await permisos.obrirAfegir();
            await permisos.afegirServei(frameAfegir, procedimentId!, ['Q2827003ATGSS001', 'SCDCPAJU']);

            await expect(permisos.row(codiProcediment, 'Q2827003ATGSS001')).toBeVisible({ timeout: 15_000 });
            await expect(permisos.row(codiProcediment, 'SCDCPAJU')).toBeVisible({ timeout: 15_000 });

            // --- Denegar accés (un dels dos) ---
            await permisos.denegarAcces(codiProcediment, 'Q2827003ATGSS001');
            await expect(permisos.row(codiProcediment, 'Q2827003ATGSS001')).toHaveCount(0);
            await expect(permisos.row(codiProcediment, 'SCDCPAJU')).toBeVisible({ timeout: 15_000 });

            // --- Esborrar seleccionats: tornam a afegir el primer i esborram els dos via selecció ---
            const frameAfegir2 = await permisos.obrirAfegir();
            await permisos.afegirServei(frameAfegir2, procedimentId!, 'Q2827003ATGSS001');
            await expect(permisos.row(codiProcediment, 'Q2827003ATGSS001')).toBeVisible({ timeout: 15_000 });

            await permisos.seleccionar(codiProcediment, 'Q2827003ATGSS001');
            await permisos.seleccionar(codiProcediment, 'SCDCPAJU');
            await expect(permisos.seleccioCount()).toHaveText('2');
            await permisos.esborrarSeleccionats();
            await expect(permisos.rows()).toHaveCount(0);

            // --- Esborrar tots: afegim els dos de nou i els esborram tots de cop ---
            const frameAfegir3 = await permisos.obrirAfegir();
            await permisos.afegirServei(frameAfegir3, procedimentId!, ['Q2827003ATGSS001', 'SCDCPAJU']);
            await expect(permisos.rows()).toHaveCount(2);

            await permisos.esborrarTots();
            await expect(permisos.rows()).toHaveCount(0);
        });
    });
});
