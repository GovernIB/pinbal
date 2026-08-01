import { test, expect } from '../../utils/fixtures';
import { ProcedimentsPage } from '../../pages/ProcedimentsPage';
import { ProcedimentServeisPage } from '../../pages/ProcedimentServeisPage';
import { ProcedimentServeiPermisosPage } from '../../pages/ProcedimentServeiPermisosPage';
import { waitForDataTableReload } from '../../utils/datatable';
import { uniqueSuffix, USUARI_FIX_ACTIU_CODI } from '../../utils/env';

/**
 * Procediments (representant), `/procediment` (`procedimentList.jsp`).
 *
 * "Crear fill": el codi `procedimentList.jsp` NO té cap concepte de
 * jerarquia pare/fill entre procediments com a tal (`Procediment` no té cap
 * FK a un altre procediment). El botó "Crear fill" (`comu.boto.crear.fill`,
 * visible només si el procediment té `codiSia` informat, vegeu
 * `template-accions`) obre `ProcedimentController.cloneGet`
 * (`/procediment/{id}/clone`), que preomple un formulari de creació NOU amb
 * `codi`/`nom` prefixats "CLON - " i `codiSiaOrigen` apuntant al codiSia del
 * procediment origen. És a dir: "crear fill" = clonar/derivar un procediment
 * nou a partir d'un existent amb `codiSia`, no un fill jeràrquic real. Aquest
 * test crea primer un procediment amb `codiSia` informat perquè l'acció
 * estigui disponible (els procediments sembrats E2EPROC01/E2EPROC02 no en
 * tenen).
 *
 * L'organ gestor sembrat E2EOG01 (id 900301) s'utilitza com a únic organ
 * gestor disponible per a l'entitat E2E.
 */
const ORGAN_GESTOR_ID = '900301';

test.describe('Gestió de procediments (representant)', () => {
    test('CRUD complet, visualització correcta al llistat i activar/desactivar', async ({ representantPage: page }) => {
        const suffix = uniqueSuffix();
        const codi = `E2EP${suffix}`.toUpperCase().slice(0, 20);
        const nom = `Procediment E2E ${suffix}`;
        const nomModificat = `Procediment E2E ${suffix} modificat`;

        const procediments = new ProcedimentsPage(page);
        await procediments.goto();

        // --- Crear ---
        let frame = await procediments.openNew();
        await frame.locator('#codi').fill(codi);
        await frame.locator('#nom').fill(nom);
        await frame.locator('#organGestorId').selectOption(ORGAN_GESTOR_ID, { force: true });
        await procediments.save();
        // Amb molts procediments E2E acumulats d'execucions anteriors, el nou registre
        // pot no aparèixer a la primera pàgina del DataTable; filtram pel seu codi.
        await procediments.filtrar(async (form) => {
            await form.locator('#codi').fill(codi);
        });

        const fila = () => procediments.row(codi);
        await expect(fila()).toBeVisible({ timeout: 15_000 });
        // --- Visualització correcta de dades al llistat ---
        await expect(fila()).toContainText(codi);
        await expect(fila()).toContainText(nom);
        // NOTA: Font Awesome 5 (webjar carregat a la pàgina) substitueix en
        // temps real cada <i class="fa-..."> per un <svg class="...fa-...">
        // (deixant l'<i> original només com a comentari HTML), així que cal
        // acceptar tant l'element original (per si l'substitució encara no
        // s'ha aplicat) com l'SVG resultant.
        await expect(fila().locator('i.fa-check, svg.fa-check')).toBeVisible();

        // --- Editar ---
        frame = await procediments.openEdit(codi);
        await frame.locator('#nom').fill(nomModificat);
        await procediments.save();
        await expect(fila()).toContainText(nomModificat);

        // --- Desactivar / Activar ---
        await procediments.desactivar(codi);
        await expect(fila().locator('i.fa-check, svg.fa-check')).toHaveCount(0);
        await procediments.activar(codi);
        await expect(fila().locator('i.fa-check, svg.fa-check')).toBeVisible();

        // --- Esborrar (neteja; pot no estar disponible segons configuració) ---
        await procediments.esborrar(codi);
    });

    test('filtre per codi', async ({ representantPage: page }) => {
        const procediments = new ProcedimentsPage(page);
        await procediments.goto();

        // Amb molts procediments E2E acumulats d'execucions anteriors, E2EPROC01 pot no
        // aparèixer a la primera pàgina sense filtrar; filtram pel seu codi explícitament.
        await procediments.filtrar(async (form) => {
            await form.locator('#codi').fill('E2EPROC01');
        });
        await expect(procediments.row('E2EPROC01')).toBeVisible({ timeout: 15_000 });

        await procediments.filtrar(async (form) => {
            await form.locator('#codi').fill('CODI_QUE_NO_HAURIA_D_EXISTIR_E2E');
        });
        await expect(procediments.isEmpty()).toBeVisible({ timeout: 15_000 });

        // "Netejar filtre" reinicialitza el formulari; comprovam això directament (el
        // llistat sense filtrar pot tenir E2EPROC01 en qualsevol pàgina, no necessàriament
        // la primera, així que no en comprovam la visibilitat aquí).
        await procediments.netejarFiltre();
        await expect(page.locator('#codi')).toHaveValue('');
    });

    test('crear fill (clonar un procediment amb codiSia)', async ({ representantPage: page }) => {
        const suffix = uniqueSuffix();
        const codiOrigen = `E2EF${suffix}`.toUpperCase().slice(0, 15);
        const nomOrigen = `Procediment origen E2E ${suffix}`;
        const codiSia = `SIA${suffix}`.toUpperCase().slice(0, 20);

        const procediments = new ProcedimentsPage(page);
        await procediments.goto();

        // Cream el procediment origen, amb codiSia informat (requisit per a "Crear fill").
        let frame = await procediments.openNew();
        await frame.locator('#codi').fill(codiOrigen);
        await frame.locator('#nom').fill(nomOrigen);
        await frame.locator('#organGestorId').selectOption(ORGAN_GESTOR_ID, { force: true });
        await frame.locator('#codiSia').fill(codiSia);
        await procediments.save();
        // Amb molts procediments E2E acumulats d'execucions anteriors, el nou registre
        // pot no aparèixer a la primera pàgina del DataTable; filtram pel seu codi.
        await procediments.filtrar(async (form) => {
            await form.locator('#codi').fill(codiOrigen);
        });
        await expect(procediments.row(codiOrigen)).toBeVisible({ timeout: 15_000 });

        expect(await procediments.teCrearFillDisponible(codiOrigen)).toBeTruthy();

        frame = await procediments.crearFill(codiOrigen);
        // El formulari de clonació preomple codi/nom amb el prefix "CLON - ".
        await expect(frame.locator('#codi')).toHaveValue(`CLON - ${codiOrigen}`);
        await expect(frame.locator('#nom')).toHaveValue(`CLON - ${nomOrigen}`);
        const codiFill = `${codiOrigen}F`.slice(0, 20);
        await frame.locator('#codi').fill(codiFill);
        await procediments.save();

        await procediments.filtrar(async (form) => {
            await form.locator('#codi').fill(codiFill);
        });
        await expect(procediments.row(codiFill)).toBeVisible({ timeout: 15_000 });
    });

    test.describe('Gestió de serveis per procediment', () => {
        test('afegir, migrar, esborrar, codi addicional i permisos', async ({ representantPage: page }) => {
            const suffix = uniqueSuffix();
            const codiProcediment = `E2ES${suffix}`.toUpperCase().slice(0, 20);
            const nomProcediment = `Procediment serveis E2E ${suffix}`;

            const procediments = new ProcedimentsPage(page);
            await procediments.goto();
            const frame = await procediments.openNew();
            await frame.locator('#codi').fill(codiProcediment);
            await frame.locator('#nom').fill(nomProcediment);
            await frame.locator('#organGestorId').selectOption(ORGAN_GESTOR_ID, { force: true });
            await procediments.save();
            // Amb molts procediments E2E acumulats d'execucions anteriors, el nou registre
            // pot no aparèixer a la primera pàgina del DataTable; filtram pel seu codi.
            await procediments.filtrar(async (form) => {
                await form.locator('#codi').fill(codiProcediment);
            });
            await expect(procediments.row(codiProcediment)).toBeVisible({ timeout: 15_000 });

            await waitForDataTableReload(page, async () => {
                await procediments.obrirServeis(codiProcediment);
            });
            const procedimentId = new URL(page.url()).pathname.match(/\/procediment\/(\d+)\/servei/)?.[1];
            expect(procedimentId).toBeTruthy();

            const serveis = new ProcedimentServeisPage(page, procedimentId!);

            // --- Visualització correcta de dades al llistat (buit inicialment) ---
            await expect(serveis.isEmpty()).toBeVisible({ timeout: 15_000 });

            // --- Afegir ---
            const frameAfegir = await serveis.openNew();
            await frameAfegir.locator('#serveiCodi').selectOption('Q2827003ATGSS001', { force: true });
            await serveis.save();
            await expect(serveis.row('Q2827003ATGSS001')).toBeVisible({ timeout: 15_000 });

            // --- Codi addicional ---
            await serveis.editarCodiAddicional('Q2827003ATGSS001', `PROC-${suffix}`);
            await expect(serveis.codiAddicionalInput('Q2827003ATGSS001')).toHaveValue(`PROC-${suffix}`);
            await expect(serveis.codiAddicionalInput('Q2827003ATGSS001')).toBeDisabled();

            // --- Migrar ---
            await serveis.migrar('Q2827003ATGSS001', 'SCDCPAJU');
            await expect(serveis.row('SCDCPAJU')).toBeVisible({ timeout: 15_000 });

            // --- Permisos: Nou permís / Denegar accés / Filtre / Visualització ---
            await waitForDataTableReload(page, async () => {
                await serveis.obrirPermisos('SCDCPAJU');
            });
            const permisos = new ProcedimentServeiPermisosPage(page, procedimentId!, 'SCDCPAJU');
            await expect(permisos.isEmpty()).toBeVisible({ timeout: 15_000 });

            const framePermis = await permisos.openNew();
            await permisos.seleccionarUsuari(framePermis, USUARI_FIX_ACTIU_CODI);
            await permisos.save();
            await expect(permisos.row(USUARI_FIX_ACTIU_CODI)).toBeVisible({ timeout: 15_000 });

            // Filtre
            await permisos.filtrar(async (form) => {
                await form.locator('#codi').fill('CODI_QUE_NO_HAURIA_D_EXISTIR_E2E');
            });
            await expect(permisos.isEmpty()).toBeVisible({ timeout: 15_000 });
            await permisos.netejarFiltre();
            await expect(permisos.row(USUARI_FIX_ACTIU_CODI)).toBeVisible({ timeout: 15_000 });

            // Denegar accés
            await permisos.denegarAcces(USUARI_FIX_ACTIU_CODI);
            await expect(permisos.isEmpty()).toBeVisible({ timeout: 15_000 });

            // --- Esborrar servei (tornam al llistat de serveis del procediment) ---
            await waitForDataTableReload(page, async () => {
                await page.goto(`procediment/${procedimentId}/servei`);
            });
            await serveis.esborrar('SCDCPAJU');
        });
    });
});
