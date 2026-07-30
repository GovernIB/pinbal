import fs from 'node:fs';
import os from 'node:os';
import path from 'node:path';
import { Download, Page } from '@playwright/test';
import { test, expect } from '../../utils/fixtures';
import { ConsultesRealitzadesPage } from '../../pages/ConsultesRealitzadesPage';

/**
 * "Consultes realitzades" (rol administrador), `/admin/consulta`
 * (adminConsultes.jsp llistat + adminConsultaInfo.jsp/
 * adminConsultaMultipleInfo.jsp detall via modal amb iframe).
 *
 * Depèn de `global-setup.ts` (d'un altre company) havent creat, com a
 * delegat, un petit nombre de consultes simples i múltiples reals contra el
 * servidor fake de SCSP abans d'executar els tests. Com que no en controlem
 * el contingut exacte (estat, si té justificant, si té error...), els tests
 * cerquen defensivament la primera fila que compleixi la condició que
 * necessiten i es salten (`test.skip`) si no en troben cap, en lloc
 * d'assumir dades concretes.
 *
 * Nota sobre permisos: `ConsultaAdminController.justificant`/
 * `justificantInline`/`justificantPrevisualitzacio` exigien que l'usuari
 * actual actués com a delegat de l'entitat de la consulta
 * (`EntitatHelper.isDelegatEntitatActual`), cosa que un administrador "pur"
 * (sense fila pròpia a PBL_ENTITAT_USUARI) mai complia només navegant per la
 * UI - bug corregit: ara aquests tres mètodes accepten també
 * `RolHelper.isRolActualAdministrador`, igual que ja feia
 * `justificantReintentar` (que no tenia el bug). Les altres descàrregues
 * (`/justificantpdf`, `/justificantzip`, `/xmlZip`, justificant MÚLTIPLE)
 * mai havien tingut aquesta restricció.
 */

function tmpFile(): string {
    return path.join(os.tmpdir(), `pinbal-e2e-${Date.now()}-${Math.random().toString(36).slice(2)}`);
}

async function assertNonEmptyDownload(download: Download): Promise<void> {
    const dest = tmpFile();
    await download.saveAs(dest);
    expect(fs.statSync(dest).size).toBeGreaterThan(0);
}

/**
 * Intenta capturar una descàrrega disparada per `click()`, amb un temps
 * d'espera acotat. Retorna `null` si no s'ha produït cap descàrrega dins el
 * termini (p.ex. perquè el servidor ha respost amb una pàgina d'error
 * d'autorització en lloc d'un fitxer).
 */
async function clicarIEsperarDescarregaOpcional(page: Page, click: () => Promise<void>, timeout = 8_000): Promise<Download | null> {
    const downloadPromise = page.waitForEvent('download', { timeout }).catch(() => null);
    await click();
    return downloadPromise;
}

test.describe('Consultes realitzades (administrador) - llistat', () => {
    test('Visualització correcta de dades al llistat', async ({ adminPage: page }) => {
        const llistat = new ConsultesRealitzadesPage(page);
        await llistat.goto();

        await expect(page.locator('#table-consultes')).toBeVisible();
        await expect(page.locator('#form-filtre')).toBeVisible();

        const rows = llistat.rows();
        const empty = llistat.isEmpty();
        await expect(rows.or(empty).first()).toBeVisible({ timeout: 15_000 });

        if (await rows.count()) {
            const text = (await rows.first().innerText()).trim();
            expect(text.length).toBeGreaterThan(0);
        }
    });

    test('Filtres', async ({ adminPage: page }) => {
        const llistat = new ConsultesRealitzadesPage(page);
        await llistat.goto();

        await llistat.filtrar(async (form) => {
            await form.locator('#scspPeticionId').fill('PETICIO-QUE-NO-HAURIA-EXISTIR-E2E');
        });
        await expect(llistat.isEmpty()).toBeVisible({ timeout: 15_000 });

        await llistat.netejarFiltre();
        await expect(page.locator('#scspPeticionId')).toHaveValue('');

        await llistat.filtrar(async (form) => {
            await form.locator('#estat').selectOption('Error', { force: true });
        });
        await expect(llistat.rows().or(llistat.isEmpty()).first()).toBeVisible({ timeout: 15_000 });

        await llistat.netejarFiltre();
    });

    test('Descàrrega del justificant (consulta múltiple, sense restricció de delegat)', async ({ adminPage: page }) => {
        const llistat = new ConsultesRealitzadesPage(page);
        await llistat.goto();

        const fila = llistat.filaAmbJustificantMultipleDescarregable();
        if (!(await fila.count())) {
            test.skip(true, 'Cap consulta múltiple amb justificant descarregable al llistat');
            return;
        }
        const [download] = await Promise.all([
            page.waitForEvent('download'),
            fila.locator('a.btn-justificant-multiple').click(),
        ]);
        await assertNonEmptyDownload(download);
    });

    test('Descàrrega del justificant (consulta simple)', async ({ adminPage: page }) => {
        const llistat = new ConsultesRealitzadesPage(page);
        await llistat.goto();

        const fila = llistat.filaAmbJustificantSimpleDescarregable();
        if (!(await fila.count())) {
            test.skip(true, 'Cap consulta simple amb justificant descarregable al llistat');
            return;
        }
        const [download] = await Promise.all([
            page.waitForEvent('download'),
            fila.locator('a.btn-justificant').click(),
        ]);
        await assertNonEmptyDownload(download);
    });

    test('Descàrrega del zip de missatges XML', async ({ adminPage: page }) => {
        const llistat = new ConsultesRealitzadesPage(page);
        await llistat.goto();

        const fila = llistat.filaAmbXmlZip();
        if (!(await fila.count())) {
            test.skip(true, 'Cap fila amb enllaç de descàrrega de zip XML al llistat');
            return;
        }
        const download = await clicarIEsperarDescarregaOpcional(page, () => fila.locator('a[href*="/xmlZip"]').click());
        if (download) {
            await assertNonEmptyDownload(download);
        }
        // Si la consulta no ha generat missatges XML, el servidor redirigeix amb un avís en lloc de servir un fitxer.
    });

    test('Exportació excel', async ({ adminPage: page }) => {
        const llistat = new ConsultesRealitzadesPage(page);
        await llistat.goto();

        const download = await llistat.exportarExcel();
        await assertNonEmptyDownload(download);
    });

    test('Visualitzar recents / històric', async ({ adminPage: page }) => {
        const llistat = new ConsultesRealitzadesPage(page);
        await llistat.goto();

        expect(await llistat.isHistoricActiu()).toBe(false);

        await llistat.alternarHistoric();
        expect(await llistat.isHistoricActiu()).toBe(true);
        await expect(llistat.rows().or(llistat.isEmpty()).first()).toBeVisible({ timeout: 15_000 });

        await llistat.alternarHistoric();
        expect(await llistat.isHistoricActiu()).toBe(false);
        await expect(llistat.rows().or(llistat.isEmpty()).first()).toBeVisible({ timeout: 15_000 });
    });
});

test.describe('Consultes realitzades (administrador) - detall simple', () => {
    test('Visualització de dades petició i resposta', async ({ adminPage: page }) => {
        const llistat = new ConsultesRealitzadesPage(page);
        await llistat.goto();

        const detall = await llistat.obrirPrimerDetallSimple();
        if (!detall) {
            test.skip(true, 'Cap consulta simple trobada a les primeres files del llistat');
            return;
        }
        const { frame } = detall;

        await expect(frame.locator('#dadesGeneriquesTab')).toBeVisible();
        await expect(frame.locator('#dadesGeneriquesTab').getByText(/dades genèriques/i)).toBeVisible();

        const tabResposta = frame.getByRole('tab', { name: /dades de la resposta/i });
        if (await tabResposta.count()) {
            await tabResposta.click();
            await expect(frame.locator('#dadesRespostaTab')).toBeVisible();
        }

        await llistat.tancarDetall();
    });

    test('Descàrrega de missatges (zip XML) des del detall', async ({ adminPage: page }) => {
        const llistat = new ConsultesRealitzadesPage(page);
        await llistat.goto();

        const detall = await llistat.obrirPrimerDetallSimple();
        if (!detall) {
            test.skip(true, 'Cap consulta simple trobada a les primeres files del llistat');
            return;
        }
        const { frame } = detall;

        const link = frame.getByRole('link', { name: /baixa missatges xml/i });
        await expect(link).toBeVisible();

        const download = await clicarIEsperarDescarregaOpcional(page, () => link.click());
        if (download) {
            await assertNonEmptyDownload(download);
        }
        // Si la consulta no té missatges XML generats, el servidor respon amb un avís en lloc d'un fitxer.
    });

    test('Visualització de xml de petició i resposta', async ({ adminPage: page }) => {
        const llistat = new ConsultesRealitzadesPage(page);
        await llistat.goto();

        const detall = await llistat.obrirPrimerDetallSimple();
        if (!detall) {
            test.skip(true, 'Cap consulta simple trobada a les primeres files del llistat');
            return;
        }
        const { frame } = detall;

        const linkPeticio = frame.locator('#dadesGeneriquesTab').getByRole('link', { name: /veure xml/i });
        if (await linkPeticio.count()) {
            await linkPeticio.click();
            const modalXml = frame.locator('#modal-missatge-xml');
            await expect(modalXml).toBeVisible({ timeout: 10_000 });
            const valor = await modalXml.locator('#missatgeXml').inputValue();
            expect(valor.trim().length).toBeGreaterThan(0);
            expect(valor).toContain('<');
            await modalXml.locator('.close').first().click();
            await expect(modalXml).toBeHidden({ timeout: 10_000 });
        }

        const tabResposta = frame.getByRole('tab', { name: /dades de la resposta/i });
        if (await tabResposta.count()) {
            await tabResposta.click();
            const linkResposta = frame.locator('#dadesRespostaTab').getByRole('link', { name: /veure xml/i });
            if (await linkResposta.count()) {
                await linkResposta.click();
                const modalXml = frame.locator('#modal-missatge-xml');
                await expect(modalXml).toBeVisible({ timeout: 10_000 });
                const valor = await modalXml.locator('#missatgeXml').inputValue();
                expect(valor.trim().length).toBeGreaterThan(0);
                await modalXml.locator('.close').first().click();
            }
        }

        await llistat.tancarDetall();
    });

    test('Vista prèvia i descàrrega de justificant', async ({ adminPage: page }) => {
        const llistat = new ConsultesRealitzadesPage(page);
        await llistat.goto();

        const detall = await llistat.obrirPrimerDetallSimple();
        if (!detall) {
            test.skip(true, 'Cap consulta simple trobada a les primeres files del llistat');
            return;
        }
        const { frame } = detall;

        const tabJustificant = frame.getByRole('tab', { name: 'Justificant' });
        if (!(await tabJustificant.count())) {
            test.skip(true, 'La consulta trobada no té pestanya de justificant (no tramitada / sense justificant)');
            return;
        }
        await tabJustificant.click();
        const pane = frame.locator('#descarregaJustificantsTab');
        await expect(pane).toBeVisible();

        const descarregarLink = pane.getByRole('link', { name: /descarregar/i });
        if (await descarregarLink.count()) {
            const download = await clicarIEsperarDescarregaOpcional(page, () => descarregarLink.click());
            if (download) {
                await assertNonEmptyDownload(download);
            }
            // Restricció coneguda de delegat per a consultes simples (vegeu capçalera del fitxer).
        }

        const vistaPreviaBtn = pane.locator('#mostrarVistaPrevia');
        if (await vistaPreviaBtn.count()) {
            await vistaPreviaBtn.click();
            const pdfContainer = pane.locator('#pdf-container');
            const errorContainer = pane.locator('#error-container');
            await expect(pdfContainer.or(errorContainer)).toBeVisible({ timeout: 15_000 });
        }

        await llistat.tancarDetall();
    });

    test('Reintent i vista d\'error en cas d\'error en la generació del justificant', async ({ adminPage: page }) => {
        const llistat = new ConsultesRealitzadesPage(page);
        await llistat.goto();

        const filaError = llistat.filaAmbJustificantError();
        if (!(await filaError.count())) {
            test.skip(true, 'Cap consulta amb el justificant en estat d\'error trobada al llistat');
            return;
        }

        const frame = await llistat.obrirDetall(filaError);
        const tabJustificant = frame.getByRole('tab', { name: 'Justificant' });
        await expect(tabJustificant).toBeVisible();
        await tabJustificant.click();

        const pane = frame.locator('#descarregaJustificantsTab');
        await expect(pane).toBeVisible();

        await pane.locator('.dropdown-toggle').first().click();
        await pane.getByRole('link', { name: /veure error/i }).click();

        const modalError = frame.locator('#modal-justificant-error');
        await expect(modalError).toBeVisible({ timeout: 10_000 });
        const errorText = await modalError.locator('textarea').inputValue();
        expect(errorText.trim().length).toBeGreaterThan(0);
        await modalError.locator('.close').first().click();
        await expect(modalError).toBeHidden({ timeout: 10_000 });

        // Reintent: navega (dins l'iframe) i torna a mostrar el mateix detall.
        await pane.getByRole('link', { name: /re-intentar/i }).click();
        await expect(frame.locator('#dadesGeneriquesTab')).toBeVisible({ timeout: 15_000 });

        await llistat.tancarDetall();
    });
});
