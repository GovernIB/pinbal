import fs from 'node:fs';
import os from 'node:os';
import path from 'node:path';
import { Download } from '@playwright/test';
import { test, expect } from '../../utils/fixtures';
import { InformesPage } from '../../pages/InformesPage';

/**
 * Informes (rol administrador), `/informe` (informeList.jsp). Cada informe
 * es genera com una descàrrega directa d'un .xls; aquí només es comprova
 * que la descàrrega es produeix i que el fitxer resultant no és buit (no
 * s'inspecciona el contingut del .xls).
 *
 * Gotcha important assenyalat pel propietari del projecte: el fitxer
 * generat per l'"Informe general d'estat" es diu `informeServeis.xls`
 * (igual que el de "Serveis disponibles"), NO `informeGeneralEstat.xls` com
 * es podria esperar pel nom mostrat a la UI (vegeu
 * InformeGeneralEstatExcelView.java). Es comprova explícitament.
 */
async function assertNonEmptyDownload(download: Download): Promise<string> {
    const dest = path.join(os.tmpdir(), `pinbal-e2e-${Date.now()}-${Math.random().toString(36).slice(2)}`);
    await download.saveAs(dest);
    const stats = fs.statSync(dest);
    expect(stats.size).toBeGreaterThan(0);
    return download.suggestedFilename();
}

test.describe('Informes (administrador)', () => {
    test.beforeEach(async ({ adminPage: page }) => {
        const informes = new InformesPage(page);
        await informes.goto();
    });

    test('Procediments agrupats per entitat i departament es genera i no és buit', async ({ adminPage: page }) => {
        const informes = new InformesPage(page);
        const download = await informes.generarViaEnllac(/procediments agrupats per entitat i departament/i);
        await assertNonEmptyDownload(download);
    });

    test('Usuaris agrupats per entitat i departament es genera i no és buit', async ({ adminPage: page }) => {
        const informes = new InformesPage(page);
        const download = await informes.generarViaEnllac(/usuaris agrupats per entitat i departament/i);
        await assertNonEmptyDownload(download);
    });

    test('Serveis disponibles es genera i no és buit', async ({ adminPage: page }) => {
        const informes = new InformesPage(page);
        const download = await informes.generarViaEnllac(/serveis disponibles/i);
        await assertNonEmptyDownload(download);
    });

    test('Informe general d\'estat es genera i no és buit (el fitxer es diu InformeServeis)', async ({ adminPage: page }) => {
        const informes = new InformesPage(page);
        const download = await informes.generarInformeGeneralEstat('01/01/2020', '31/12/2030');
        const filename = await assertNonEmptyDownload(download);
        expect(filename.toLowerCase()).toContain('informeserveis');
    });

    test('Usuaris agrupats per entitat, òrgan gestor, procediment i servei es genera i no és buit', async ({ adminPage: page }) => {
        const informes = new InformesPage(page);
        const download = await informes.generarViaEnllac(/usuaris agrupats per entitat, òrgan gestor, procediment i servei/i);
        await assertNonEmptyDownload(download);
    });
});
