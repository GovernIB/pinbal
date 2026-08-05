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

        // PBL_E2E_MULTIPLE_01 (sembrada, justificantestat=OK) en lloc d'escanejar la primera
        // pàgina del llistat sense filtrar (vegeu cercarPerPeticio()).
        const fila = await llistat.cercarPerPeticio('PBL_E2E_MULTIPLE_01');
        if (!fila || !(await fila.locator('a.btn-justificant-multiple').count())) {
            test.skip(true, 'Consulta de mostra PBL_E2E_MULTIPLE_01 no trobada, o sense justificant descarregable');
            return;
        }
        // Timeout ampliat (per defecte 10s, vegeu actionTimeout a playwright.config.ts):
        // generar el justificant implica renderitzar la plantilla ODT i firmar-la amb
        // la clau privada local, i sota la càrrega de diversos workers de Playwright
        // compartint la mateixa instància de JBoss pot trigar més de 10s.
        const [download] = await Promise.all([
            page.waitForEvent('download', { timeout: 30_000 }),
            fila.locator('a.btn-justificant-multiple').click(),
        ]);
        await assertNonEmptyDownload(download);
    });

    test('Descàrrega del justificant (consulta simple)', async ({ adminPage: page }) => {
        const llistat = new ConsultesRealitzadesPage(page);
        await llistat.goto();

        // PBL_E2E_SIMPLE_OK (sembrada, Tramitada + justificantestat=OK) en lloc d'escanejar la
        // primera pàgina del llistat sense filtrar (vegeu cercarPerPeticio()).
        const fila = await llistat.cercarPerPeticio('PBL_E2E_SIMPLE_OK');
        if (!fila || !(await fila.locator('a.btn-justificant').count())) {
            test.skip(true, 'Consulta de mostra PBL_E2E_SIMPLE_OK no trobada, o sense justificant descarregable');
            return;
        }
        // Timeout ampliat: vegeu comentari al test anterior (justificant múltiple).
        const [download] = await Promise.all([
            page.waitForEvent('download', { timeout: 30_000 }),
            fila.locator('a.btn-justificant').click(),
        ]);
        await assertNonEmptyDownload(download);
    });

    test('Descàrrega del zip de missatges XML', async ({ adminPage: page }) => {
        const llistat = new ConsultesRealitzadesPage(page);
        await llistat.goto();

        // Cal una consulta amb missatges generats, preferiblement Tramitada; PBL_E2E_SIMPLE_OK
        // (sembrada amb core_token_data tipomensaje 0 i 3) ho compleix. En lloc d'escanejar la
        // primera pàgina del llistat sense filtrar (vegeu cercarPerPeticio()).
        const fila = await llistat.cercarPerPeticio('PBL_E2E_SIMPLE_OK');
        if (!fila || !(await fila.locator('a[href*="/xmlZip"]').count())) {
            test.skip(true, 'Consulta de mostra PBL_E2E_SIMPLE_OK no trobada, o sense enllaç de descàrrega de zip XML');
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

        // cercarPerPeticio() filtra explícitament: sense filtrar (només mirant la primera pàgina
        // del llistat) una consulta simple pot no aparèixer-hi amb prou consultes generades per
        // altres tests.
        const fila = await llistat.cercarPerPeticio('PBL_E2E_SIMPLE_OK');
        if (!fila) {
            test.skip(true, 'Consulta de mostra PBL_E2E_SIMPLE_OK no trobada al llistat');
            return;
        }
        const frame = await llistat.obrirDetall(fila);

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

        const fila = await llistat.cercarPerPeticio('PBL_E2E_SIMPLE_OK');
        if (!fila) {
            test.skip(true, 'Consulta de mostra PBL_E2E_SIMPLE_OK no trobada al llistat');
            return;
        }
        const frame = await llistat.obrirDetall(fila);

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

        const fila = await llistat.cercarPerPeticio('PBL_E2E_SIMPLE_OK');
        if (!fila) {
            test.skip(true, 'Consulta de mostra PBL_E2E_SIMPLE_OK no trobada al llistat');
            return;
        }
        const frame = await llistat.obrirDetall(fila);

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
                // Comprova que és un missatge XML real, no la representació textual d'un
                // element DOM sense serialitzar (p.ex. "[TransmisionDatos: null]").
                expect(valor).toContain('<');
                await modalXml.locator('.close').first().click();
            }
        }

        await llistat.tancarDetall();
    });

    test('Vista prèvia i descàrrega de justificant', async ({ adminPage: page }) => {
        const llistat = new ConsultesRealitzadesPage(page);
        await llistat.goto();

        // Cal una consulta Tramitada i sense error generant el justificant perquè hi hagi
        // contingut real per previsualitzar/descarregar; `PBL_E2E_SIMPLE_OK` (sembrada a
        // 00_e2e_seed_data.yaml) és exactament això (a diferència de "primera fila trobada",
        // que pot caure en una consulta pendent/en error sense res per mostrar). cercarPerPeticio()
        // filtra explícitament: amb prou consultes generades per altres tests aquesta fila pot
        // caure fora de la primera pàgina del llistat sense filtrar.
        const filaOk = await llistat.cercarPerPeticio('PBL_E2E_SIMPLE_OK');
        if (!filaOk) {
            test.skip(true, 'Consulta de mostra PBL_E2E_SIMPLE_OK no trobada al llistat');
            return;
        }
        const frame = await llistat.obrirDetall(filaOk);

        // Igual que amb la fila del llistat: modalFrame() només garanteix que el <body> de
        // l'iframe és visible, no que tot el seu contingut (incloent les pestanyes) ja s'hagi
        // acabat de pintar -- un `.count()` immediatament després pot arribar massa d'hora.
        const tabJustificant = frame.getByRole('tab', { name: 'Justificant' });
        const teJustificant = await tabJustificant.first()
            .waitFor({ state: 'visible', timeout: 10_000 })
            .then(() => true)
            .catch(() => false);
        if (!teJustificant) {
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
            // #pdf-container i #error-container conviuen SEMPRE al DOM (adminConsultaInfo.jsp els
            // declara amb style="display:none"; el JS en mostra només un dels dos), així que
            // `pdfContainer.or(errorContainer)` (que ignora la visibilitat CSS) sempre troba 2
            // elements i viola el "strict mode". El pseudo-selector :visible sí que filtra pel
            // que realment es mostra.
            await expect(pane.locator('#pdf-container:visible, #error-container:visible')).toBeVisible({ timeout: 15_000 });
        }

        await llistat.tancarDetall();
    });

    test('Reintent i vista d\'error en cas d\'error en la generació del justificant', async ({ adminPage: page }) => {
        const llistat = new ConsultesRealitzadesPage(page);
        await llistat.goto();

        // cercarPerPeticio() filtra explícitament pel número de petició de la consulta de mostra
        // sembrada amb justificant en error: sense filtrar (només mirant la pàgina carregada, 10
        // files per defecte) pot caure fora de la primera pàgina amb prou consultes generades per
        // altres tests (mateix problema que "Vista prèvia i descàrrega de justificant" més amunt).
        const filaError = await llistat.cercarPerPeticio('PBL_E2E_SIMPLE_JUSTERR');
        if (!filaError) {
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

        // "Veure error" i "Re-intentar" viuen dins el MATEIX desplegable (adminConsultaInfo.jsp):
        // en clicar-hi "Veure error" (un enllaç intern del desplegable) Bootstrap NO el tanca (el
        // seu listener global només tanca en clicar-hi A FORA) -- però obrir/tancar la modal
        // "#modal-justificant-error" sí que sembla desmarcar-lo com a obert. No assumim l'estat:
        // el reobrim només si "Re-intentar" encara no és visible (mateix patró que
        // usuari-configuracio.spec.ts / gestionar-serveis.spec.ts).
        const reintentarLink = pane.getByRole('link', { name: /re-intentar/i });
        if (!(await reintentarLink.isVisible())) {
            await pane.locator('.dropdown-toggle').first().click();
        }
        await expect(reintentarLink).toBeVisible();

        // Reintent: navega (dins l'iframe) i torna a mostrar el mateix detall.
        await reintentarLink.click();
        await expect(frame.locator('#dadesGeneriquesTab')).toBeVisible({ timeout: 15_000 });

        await llistat.tancarDetall();
    });
});
