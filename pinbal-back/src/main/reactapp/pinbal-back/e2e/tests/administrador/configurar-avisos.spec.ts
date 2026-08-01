import { test, expect } from '../../utils/fixtures';
import { AvisosPage } from '../../pages/AvisosPage';
import { clickModalFooterButton, modalValidationErrors } from '../../utils/modal';
import { uniqueSuffix } from '../../utils/env';

/**
 * Gestió d'avisos (/avis, secció administrador).
 *
 * Dades de referència sembrades (00_e2e_seed_data.yaml):
 *  - "Avis E2E Actiu Vigent"  (actiu=true,  2024-01-01 -> 2099-12-31): actiu i NO caducat.
 *  - "Avis E2E Caducat"       (actiu=true,  2020-01-01 -> 2020-02-01): actiu PERÒ caducat.
 *  - "Avis E2E Inactiu"       (actiu=false, 2024-01-01 -> 2099-12-31): inactiu i NO caducat.
 *
 * La barra d'avisos del decorador (`#contingut-avisos`, present a totes les
 * pàgines) només mostra els avisos actius i NO caducats
 * (`AvisService.findActive()`); per això "Avis E2E Caducat" no hi ha de
 * sortir mai malgrat tenir `actiu=true`.
 *
 * Els tests que creen dades pròpies (CRUD, activar/desactivar individual i
 * massiu) fan servir un sufix únic i esborren les seves files en acabar per
 * no deixar contaminació entre execucions; els tests de només lectura (
 * llistat, barra d'avisos) es limiten a comprovar les files sembrades.
 *
 * `avuiDDMMYYYY()` ha de calcular el dia en UTC, NO en la zona horària local
 * de la màquina on corre Playwright: els contenidors (JBoss + Oracle) corren
 * en UTC (imatge base sense TZ configurada), mentre que aquest procés pot
 * córrer en una zona horària per davant d'UTC (p.ex. Europe/Madrid,
 * CEST=UTC+2). Durant les ~2 hores de cada dia en què ja és "demà" en hora
 * local però encara és "avui" en UTC (22:00-00:00 UTC), calcular la data amb
 * getDate()/getMonth()/getFullYear() (hora local) produïa una dataInici d'UN
 * DIA EN EL FUTUR respecte al rellotge del servidor -- AvisRepository.
 * findActive() (trunc(dataInici) <= trunc(ara-del-servidor)) descartava
 * legítimament l'avís perquè encara no havia arribat el seu dia d'inici
 * segons el servidor, encara que semblés "avui" localment. No és un bug de
 * l'aplicació (verificat amb SQL directe contra Oracle): és aquest test
 * calculant "avui" amb el rellotge equivocat.
 */
function avuiDDMMYYYY(): string {
    const d = new Date();
    const dd = String(d.getUTCDate()).padStart(2, '0');
    const mm = String(d.getUTCMonth() + 1).padStart(2, '0');
    return `${dd}/${mm}/${d.getUTCFullYear()}`;
}

test.describe('Configurar avisos (administrador)', () => {
    test('el llistat d\'avisos es mostra correctament', async ({ adminPage: page }) => {
        const avisos = new AvisosPage(page);
        await avisos.goto();

        const vigent = avisos.row('Avis E2E Actiu Vigent');
        await expect(vigent).toBeVisible();
        await expect(vigent).toContainText('01/01/2024');

        const caducat = avisos.row('Avis E2E Caducat');
        await expect(caducat).toBeVisible();

        const inactiu = avisos.row('Avis E2E Inactiu');
        await expect(inactiu).toBeVisible();
    });

    test('la barra d\'avisos mostra els actius no caducats i amaga els caducats i els inactius', async ({ adminPage: page }) => {
        const avisos = new AvisosPage(page);
        await avisos.goto();

        await expect(avisos.barraAvisos()).toContainText('Avis E2E Actiu Vigent');
        await expect(avisos.barraAvisos()).not.toContainText('Avis E2E Caducat');
        await expect(avisos.barraAvisos()).not.toContainText('Avis E2E Inactiu');
    });

    test('CRUD complet d\'un avís', async ({ adminPage: page }) => {
        const avisos = new AvisosPage(page);
        const suffix = uniqueSuffix();
        const assumpte = `Avis E2E CRUD ${suffix}`;
        const assumpteModificat = `Avis E2E CRUD ${suffix} modificat`;

        await avisos.goto();

        // --- Crear ---
        let frame = await avisos.openNew();
        await frame.locator('#assumpte').fill(assumpte);
        await frame.locator('#missatge').fill('Missatge de prova e2e');
        await frame.locator('#dataInici').fill(avuiDDMMYYYY());
        await frame.locator('#avisNivell').selectOption('INFO', { force: true });
        await avisos.guardar();

        const fila = avisos.row(assumpte);
        await expect(fila).toBeVisible({ timeout: 15_000 });

        // --- Editar ---
        frame = await avisos.openEdit(assumpte);
        await frame.locator('#assumpte').fill(assumpteModificat);
        await avisos.guardar();

        const filaModificada = avisos.row(assumpteModificat);
        await expect(filaModificada).toBeVisible({ timeout: 15_000 });

        // --- Esborrar (neteja de dades de prova) ---
        await avisos.esborrar(assumpteModificat);
        await expect(avisos.row(assumpteModificat)).toHaveCount(0);
    });

    test('activar/desactivar individual actualitza la visibilitat a la barra d\'avisos', async ({ adminPage: page }) => {
        const avisos = new AvisosPage(page);
        const assumpte = `Avis E2E Toggle ${uniqueSuffix()}`;

        await avisos.goto();
        const frame = await avisos.openNew();
        await frame.locator('#assumpte').fill(assumpte);
        await frame.locator('#missatge').fill('Missatge de prova e2e (toggle actiu/inactiu)');
        await frame.locator('#dataInici').fill(avuiDDMMYYYY());
        await frame.locator('#avisNivell').selectOption('INFO', { force: true });
        await avisos.guardar();
        await expect(avisos.row(assumpte)).toBeVisible({ timeout: 15_000 });

        // No donem per fet l'estat inicial (actiu/inactiu) d'un avís acabat de crear: el comprovam dinàmicament.
        const actiuInicialment = await avisos.esActiu(assumpte);

        if (actiuInicialment) {
            await expect(avisos.barraAvisos()).toContainText(assumpte);

            await avisos.desactivar(assumpte);
            await expect(avisos.barraAvisos()).not.toContainText(assumpte);

            await avisos.activar(assumpte);
            await expect(avisos.barraAvisos()).toContainText(assumpte);
        } else {
            await expect(avisos.barraAvisos()).not.toContainText(assumpte);

            await avisos.activar(assumpte);
            await expect(avisos.barraAvisos()).toContainText(assumpte);

            await avisos.desactivar(assumpte);
            await expect(avisos.barraAvisos()).not.toContainText(assumpte);
        }

        // Neteja de dades de prova.
        await avisos.esborrar(assumpte);
        await expect(avisos.row(assumpte)).toHaveCount(0);
    });

    test('activar/desactivar massiu actualitza diversos avisos alhora', async ({ adminPage: page }) => {
        const avisos = new AvisosPage(page);
        const suffix = uniqueSuffix();
        const assumpteA = `Avis E2E Massiu A ${suffix}`;
        const assumpteB = `Avis E2E Massiu B ${suffix}`;

        await avisos.goto();
        for (const assumpte of [assumpteA, assumpteB]) {
            const frame = await avisos.openNew();
            await frame.locator('#assumpte').fill(assumpte);
            await frame.locator('#missatge').fill('Missatge de prova e2e (accio massiva)');
            await frame.locator('#dataInici').fill(avuiDDMMYYYY());
            await frame.locator('#avisNivell').selectOption('INFO', { force: true });
            await avisos.guardar();
            await expect(avisos.row(assumpte)).toBeVisible({ timeout: 15_000 });
        }

        await avisos.seleccionar(assumpteA);
        await avisos.seleccionar(assumpteB);

        await avisos.desactivarSeleccio();
        await expect(avisos.barraAvisos()).not.toContainText(assumpteA);
        await expect(avisos.barraAvisos()).not.toContainText(assumpteB);

        // NOTA: no cal tornar a seleccionar aquí. `avisList.jsp` manté
        // `selectedIds` com un Set persistent en JS que sobreviu al
        // `dt.ajax.reload()` de l'acció massiva (per poder fer accions
        // massives entre pàgines de la taula); el `rowCallback` torna a
        // marcar les files com a seleccionades a cada redraw. Com que el
        // clic sobre `.row-selector` alterna la selecció, tornar a clicar
        // aquí les desseleccionaria (deixant `activarSeleccio()` sense res
        // seleccionat).
        await avisos.activarSeleccio();
        await expect(avisos.barraAvisos()).toContainText(assumpteA);
        await expect(avisos.barraAvisos()).toContainText(assumpteB);

        // Neteja de dades de prova.
        await avisos.esborrar(assumpteA);
        await avisos.esborrar(assumpteB);
        await expect(avisos.row(assumpteA)).toHaveCount(0);
        await expect(avisos.row(assumpteB)).toHaveCount(0);
    });

    /**
     * Cas especial del checklist: "Data final --> Invalid date !!!". El
     * binder del formulari (`AvisController.initBinder`) fa servir
     * `CustomDateEditor` amb format "dd/MM/yyyy" sobre un `SimpleDateFormat`
     * que és lenient per defecte (no s'hi crida `setLenient(false)`); això
     * pot fer que dates "fora de rang" però numèricament versemblants (p.ex.
     * dia 32) es reinterpretin silenciosament en lloc de rebutjar-se -- molt
     * probablement l'origen del comportament que el client marca com a
     * sospitós. No podem confirmar aquest detall exacte sense l'aplicació en
     * marxa, així que aquí provem el cas inequívoc: un text que no és cap
     * data en absolut, que amb qualsevol lenient hauria de fallar el parseig
     * i produir un error de validació de binding (i, per tant, la modal NO
     * s'hauria de tancar ni de recarregar la pàgina).
     */
    test('una "Data final" amb un format invàlid es rebutja amb un error de validació', async ({ adminPage: page }) => {
        const avisos = new AvisosPage(page);
        const assumpte = `Avis E2E DataFinal Invalida ${uniqueSuffix()}`;

        await avisos.goto();
        const frame = await avisos.openNew();
        await frame.locator('#assumpte').fill(assumpte);
        await frame.locator('#missatge').fill('Missatge de prova e2e (data final invalida)');
        await frame.locator('#dataInici').fill(avuiDDMMYYYY());
        await frame.locator('#dataFinal').fill('no-es-una-data');
        await frame.locator('#avisNivell').selectOption('INFO', { force: true });

        const urlAbans = page.url();
        // No fem servir `avisos.guardar()` (que espera una navegació/recàrrega d'èxit): aquí esperem que es quedi a la mateixa pàgina, amb la modal oberta i un error de validació al formulari.
        await clickModalFooterButton(page, /guardar/i);

        await expect(page.locator('div.modal.in')).toBeVisible({ timeout: 10_000 });
        // Esperem que el formulari re-renderitzat dins l'iframe mostri l'error abans de llegir-lo.
        await expect(frame.locator('.help-block').first()).toBeVisible({ timeout: 10_000 });
        const errors = await modalValidationErrors(frame);
        expect(errors.length).toBeGreaterThan(0);
        expect(page.url()).toBe(urlAbans);
        await expect(avisos.row(assumpte)).toHaveCount(0);
    });
});
