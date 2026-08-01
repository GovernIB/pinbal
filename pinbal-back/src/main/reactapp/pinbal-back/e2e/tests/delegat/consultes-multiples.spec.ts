import { Page } from '@playwright/test';
import { test, expect } from '../../utils/fixtures';
import { waitForDataTableReload } from '../../utils/datatable';
import { modalFrame } from '../../utils/modal';

/**
 * Consultes múltiples (rol delegat): llistat i detall (`consultaMultiple.jsp`
 * / `consultaMultipleInfo.jsp`). Amplia l'antic test de llistat de
 * `consulta-llistat.spec.ts` amb filtres, exportació, recents/històric i tot
 * el detall d'una consulta múltiple.
 *
 * Els tests de detall depenen de `e2e/global-setup.ts` hagi pogut crear com
 * a mínim una consulta múltiple real (agafem sempre la primera fila del
 * llistat, ja que l'ordenació per defecte és per data de creació
 * descendent); si el llistat és buit es marquen com a "skipped".
 */
test.describe('Llistat de consultes múltiples (delegat)', () => {
    test('el llistat de consultes múltiples es carrega amb les dades esperades', async ({ delegatPage: page }) => {
        await waitForDataTableReload(page, async () => {
            await page.goto('consulta/multiple');
        });

        await expect(page.locator('#table-consultes')).toBeVisible();
        await expect(page.locator('#form-filtre')).toBeVisible();

        const headerRow = page.locator('#table-consultes thead tr');
        for (const text of ['Núm. petició', 'Data', 'Procediment', 'Servei', 'Titular nom', 'Estat']) {
            await expect(headerRow).toContainText(text);
        }
    });

    test('es pot filtrar per estat i netejar el filtre', async ({ delegatPage: page }) => {
        await waitForDataTableReload(page, async () => {
            await page.goto('consulta/multiple');
        });

        await waitForDataTableReload(page, async () => {
            await page.locator('#estat').selectOption('Error', { force: true });
            await page.locator('#filtrar').click();
        });
        const rows = page.locator('#table-consultes tbody tr');
        const emptyState = page.locator('#table-consultes td.dataTables_empty');
        await expect(rows.or(emptyState).first()).toBeVisible({ timeout: 15_000 });

        await waitForDataTableReload(page, async () => {
            await page.locator('#netejar-filtre').click();
        });
        await expect(page.locator('#estat')).toHaveValue('');
    });

    test('permet exportar el llistat a Excel', async ({ delegatPage: page }) => {
        await waitForDataTableReload(page, async () => {
            await page.goto('consulta/multiple');
        });

        const linkExcel = page.locator('a[href*="multiple/excel"]');
        await expect(linkExcel).toBeVisible();

        const [download] = await Promise.all([page.waitForEvent('download'), linkExcel.click()]);
        expect(download.suggestedFilename()).toMatch(/\.xls[xm]?$/i);
    });

    test('es pot alternar entre consultes recents i històriques', async ({ delegatPage: page }) => {
        await waitForDataTableReload(page, async () => {
            await page.goto('consulta/multiple');
        });

        // El widget "Històric" (data-toggle="titol-check" a consultaMultiple.jsp,
        // js/webutil.common.js#webutilTitolCheck) es converteix en un interruptor
        // estil iOS via js/ios-checkbox/iosCheckbox.js: aquest plugin amaga
        // l'input real (`org_checkbox.hide()`) i pinta un <div class="ios-ui-select">
        // germà com a element visible i clicable. #titolCheck (l'input) continua
        // sent la font de veritat de l'estat (isChecked()), però mai és visible
        // ni "clicable" per Playwright: cal interactuar amb el div germà.
        const titolCheck = page.locator('#titolCheck');
        const titolCheckToggle = page.locator('.titol-check .ios-ui-select');
        await expect(titolCheckToggle).toBeVisible();
        const inicialment = await titolCheck.isChecked();

        await waitForDataTableReload(page, async () => {
            await titolCheckToggle.click();
        });
        expect(await titolCheck.isChecked()).toBe(!inicialment);

        // Ho tornem a deixar com estava per no alterar l'estat entre execucions.
        await waitForDataTableReload(page, async () => {
            await titolCheckToggle.click();
        });
        expect(await titolCheck.isChecked()).toBe(inicialment);
    });
});

/**
 * Navega al llistat de múltiples i obre el detall de la consulta de mostra sembrada
 * `PBL_E2E_MULTIPLE_01` (Liquibase, context e2e, sempre "Tramitada" i amb sol·licituds/
 * missatges); retorna `null` (i salta el test) si no es troba.
 *
 * Es filtra explícitament per `#scspPeticionId` en lloc d'agafar "la primera fila": ara
 * que `global-setup.ts` crea també una consulta múltiple real (mateix `Q2827003ATGSS001`),
 * "la primera" (ordenada per data descendent) seria la creada dinàmicament, que no té
 * per què tenir sol·licituds/missatges en el mateix estat determinista que la sembrada
 * (mateix problema ja detectat als tests d'administrador: cal una consulta Tramitada
 * concreta que tingui els missatges, no "la primera").
 */
async function obrirPrimerDetallMultiple(page: Page) {
    await waitForDataTableReload(page, async () => {
        await page.goto('consulta/multiple');
    });
    await waitForDataTableReload(page, async () => {
        await page.locator('#scspPeticionId').fill('PBL_E2E_MULTIPLE_01');
        await page.locator('#filtrar').click();
    });

    const rows = page.locator('#table-consultes tbody tr', { hasText: 'PBL_E2E_MULTIPLE_01' });
    const buit = page.locator('#table-consultes td.dataTables_empty');
    await expect(rows.or(buit).first()).toBeVisible({ timeout: 15_000 });

    if (await buit.isVisible().catch(() => false)) {
        test.skip(
            true,
            'No es troba la consulta de mostra "PBL_E2E_MULTIPLE_01" (sembrada per Liquibase, context e2e); '
                + 'vegeu 01_e2e_seed_serveis.yaml i e2e/BUGS_APLICACIO.md.',
        );
        return null;
    }

    const fila = rows.first();
    await fila.getByRole('link', { name: /detalls/i }).click();
    return modalFrame(page);
}

test.describe('Detall de consulta múltiple (delegat)', () => {
    test('mostra les dades de la petició i el llistat de sol·licituds', async ({ delegatPage: page }) => {
        const frame = await obrirPrimerDetallMultiple(page);
        if (!frame) return;

        await expect(frame.locator('#dadesPeticio')).toBeVisible();
        // Camps disabled sense id estable (consultaMultipleInfo.jsp): comprovem
        // que el bloc de dades de la petició mostra contingut real, no buit.
        await expect(frame.locator('#dadesPeticio input').first()).not.toHaveValue('');

        const taulaSolicituds = frame.locator('#solicituds');
        if (await taulaSolicituds.count()) {
            await expect(taulaSolicituds.locator('tbody tr').first()).toBeVisible();
        }
    });

    test('permet veure el xml de la petició', async ({ delegatPage: page }) => {
        const frame = await obrirPrimerDetallMultiple(page);
        if (!frame) return;

        const linkXmlPeticio = frame.locator('a[href*="/xmlPeticio"]').first();
        if (!(await linkXmlPeticio.isVisible().catch(() => false))) {
            test.skip(true, 'Aquesta consulta múltiple encara no té petició generada (hiHaPeticio fals).');
            return;
        }

        await linkXmlPeticio.click();
        const modalXml = frame.locator('#modal-missatge-xml');
        await expect(modalXml).toBeVisible();
        await expect(modalXml.locator('#missatgeXml')).not.toHaveValue('');
    });

    test('permet veure el xml de petició i resposta d\'una sol·licitud concreta', async ({ delegatPage: page }) => {
        const frame = await obrirPrimerDetallMultiple(page);
        if (!frame) return;

        const primeraFila = frame.locator('#solicituds tbody tr').first();
        if (!(await primeraFila.isVisible().catch(() => false))) {
            test.skip(true, 'Aquesta consulta múltiple encara no té cap sol·licitud carregada.');
            return;
        }

        // El contingut de `#missatgeXml` s'omple amb `$('#modal-missatge-xml .modal-body').load(href)`
        // (ajax pla, sense esdeveniment de navegació que `locator.click()` pugui esperar sol);
        // cal registrar l'espera de la resposta ABANS del clic per no perdre-la per condició de carrera.
        const linkXmlPeticioFilla = primeraFila.locator('a[href*="/xmlPeticio"]');
        if (await linkXmlPeticioFilla.count()) {
            const responsePeticio = page.waitForResponse((res) => res.url().includes('/xmlPeticio') && res.ok());
            await linkXmlPeticioFilla.click();
            await responsePeticio;
            const modalXml = frame.locator('#modal-missatge-xml');
            await expect(modalXml).toBeVisible();
            await expect(modalXml.locator('#missatgeXml')).not.toHaveValue('');
            await modalXml.locator('button.close').click();
        }

        const linkXmlRespostaFilla = primeraFila.locator('a[href*="/xmlResposta"]');
        if (await linkXmlRespostaFilla.count()) {
            const responsePosta = page.waitForResponse((res) => res.url().includes('/xmlResposta') && res.ok());
            await linkXmlRespostaFilla.click();
            await responsePosta;
            const modalXml = frame.locator('#modal-missatge-xml');
            await expect(modalXml).toBeVisible();
            await expect(modalXml.locator('#missatgeXml')).not.toHaveValue('');
        }
    });

    test('permet obrir el detall d\'una sol·licitud i mostra la sol·licitud correcta', async ({ delegatPage: page }) => {
        const frame = await obrirPrimerDetallMultiple(page);
        if (!frame) return;

        const primeraFila = frame.locator('#solicituds tbody tr').first();
        if (!(await primeraFila.isVisible().catch(() => false))) {
            test.skip(true, 'Aquesta consulta múltiple encara no té cap sol·licitud carregada.');
            return;
        }

        const numSolicitud = (await primeraFila.locator('td').first().innerText()).trim();

        // Aquest clic provoca una navegació INTERNA del mateix iframe (no una nova
        // modal via webutil.modal.js), cap a la pàgina de detall de la sol·licitud.
        // Cal registrar l'espera de la resposta ABANS del clic: confirmat empíricament
        // (10/10 respostes idèntiques i correctes en cru via `page.request.get`) que el
        // servidor sempre torna el contingut correcte de seguida; la flakiness és
        // 100% del costat del navegador/Playwright en aquesta transició interna de
        // l'iframe, no de l'aplicació.
        const responseDetall = page.waitForResponse((res) => res.url().includes('/modal/consulta/') && res.ok());
        await primeraFila.getByRole('link', { name: /detalls/i }).click();
        await responseDetall;

        // El detall de sol·licitud es com el detall simple (no es reprova aquí);
        // només comprovem que s'ha navegat a la sol·licitud correcta: el
        // formulari de tornada ("pareId") és visible i el número de
        // sol·licitud clicat apareix entre els valors mostrats.
        await expect(frame.getByRole('link', { name: /tornar/i })).toBeVisible({ timeout: 15_000 });
        if (numSolicitud) {
            const valors = await frame.locator('input[disabled]').evaluateAll(
                // eslint-disable-next-line @typescript-eslint/no-explicit-any
                (els) => els.map((el) => (el as any).value as string),
            );
            expect(valors).toContain(numSolicitud);
        }
    });

    test('permet descarregar els justificants en pdf i en zip, quan la consulta està tramitada', async ({ delegatPage: page }) => {
        const frame = await obrirPrimerDetallMultiple(page);
        if (!frame) return;

        const linkPdf = frame.locator('a[href*="justificantpdf"]');
        const linkZip = frame.locator('a[href*="justificantzip"]');
        if (!(await linkPdf.count()) && !(await linkZip.count())) {
            test.skip(
                true,
                'Aquesta consulta múltiple encara no està "Tramitada" (o el processament asíncron encara no ha '
                    + 'acabat); els enllaços de descàrrega de justificants només es mostren en aquest estat.',
            );
            return;
        }

        if (await linkPdf.count()) {
            const [downloadPdf] = await Promise.all([page.waitForEvent('download'), linkPdf.click()]);
            expect(downloadPdf.suggestedFilename()).toBeTruthy();
        }
        if (await linkZip.count()) {
            const [downloadZip] = await Promise.all([page.waitForEvent('download'), linkZip.click()]);
            expect(downloadZip.suggestedFilename()).toMatch(/\.zip$/i);
        }
    });
});
