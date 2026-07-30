import { test, expect } from '../../utils/fixtures';
import { uniqueSuffix } from '../../utils/env';
import { expectSuccessMessage } from '../../utils/messages';
import { ServeisPage } from '../../pages/ServeisPage';
import { ServeiFormPage, ServeiFormFields } from '../../pages/ServeiFormPage';
import { ServeiCampPage } from '../../pages/ServeiCampPage';

/**
 * "Serveis" és la pantalla de manteniment més complexa de PINBAL: a
 * diferència de la resta (vegeu entitat-crud.spec.ts / AdminMaintenancePage),
 * el formulari de creació/edició és una pàgina completa (/servei/new,
 * /servei/{codi}), no una modal, i té sub-pantalles pròpies per a la gestió
 * de camps/grups/regles (/servei/{codi}/camp), fitxers XSD, previsualització
 * i justificant. Vegeu ServeisPage / ServeiFormPage / ServeiCampPage.
 *
 * Els valors dels selects obligatoris (emissor, claus de signatura/xifrat)
 * provenen de les dades sembrades a 00_e2e_seed_data.yaml
 * (core_emisor_certificado cif=P0700000A, core_clave_privada
 * alias=e2e-clau-privada-activa, core_clave_publica alias=e2e-clau-publica).
 * `scspTipoSeguridad` només accepta els literals "XMLSignature"/"WS-Security"
 * (llista fixa al controlador, no un enum de BD).
 */
function serveiFieldsBasics(suffix: string, codi: string, descripcio: string): ServeiFormFields {
    return {
        codi,
        descripcio,
        scspEmisorCif: 'P0700000A',
        scspVersionEsquema: 'V3',
        scspTipoSeguridad: 'WS-Security',
        scspClaveFirmaAlies: 'e2e-clau-privada-activa',
        scspClaveCifradoAlies: 'e2e-clau-publica',
        scspFechaAlta: '01/01/2024',
    };
}

test.describe('Gestió de serveis (administrador)', () => {
    test('CRUD complet, llistat i filtre', async ({ adminPage: page }) => {
        const suffix = uniqueSuffix();
        const codi = `E2ESRV${suffix}`.toUpperCase().slice(0, 30);
        const descripcioInicial = `Servei E2E ${suffix}`;
        const descripcioModificada = `${descripcioInicial} modificat`;

        const serveis = new ServeisPage(page);
        const form = new ServeiFormPage(page);

        // --- Crear ---
        await serveis.gotoNew();
        await form.fillRequired(serveiFieldsBasics(suffix, codi, descripcioInicial));
        await form.submitAndExpectBackToList();
        await expectSuccessMessage(page);

        await expect(serveis.row(codi)).toBeVisible({ timeout: 15_000 });
        await expect(serveis.row(codi)).toContainText(descripcioInicial);

        // --- Editar ---
        await serveis.gotoEdit(codi);
        await page.locator('#descripcio').fill(descripcioModificada);
        await form.submitAndExpectBackToList();
        await expectSuccessMessage(page);
        await expect(serveis.row(codi)).toContainText(descripcioModificada);

        // --- Filtre per codi ---
        await serveis.filtrar(async (p) => {
            await p.locator('#codi').fill(codi);
        });
        await expect(serveis.row(codi)).toBeVisible();
        await expect(serveis.rows()).toHaveCount(1);

        await serveis.filtrar(async (p) => {
            await p.locator('#codi').fill('CODI_QUE_NO_HAURIA_D_EXISTIR_E2E');
        });
        await expect(serveis.isEmpty()).toBeVisible({ timeout: 15_000 });

        await serveis.netejarFiltre();
        await expect(page.locator('#codi')).toHaveValue('');

        // --- Desactivar / activar ---
        await serveis.desactivar(codi);
        await serveis.row(codi).locator('a.dropdown-toggle, button.dropdown-toggle').click();
        await expect(serveis.row(codi).getByRole('link', { name: /^activar/i })).toBeVisible();
        await serveis.activar(codi);

        // --- Esborrar (neteja de dades de prova) ---
        await serveis.esborrar(codi);
        await expect(page.locator('#table-serveis tbody tr', { hasText: codi })).toHaveCount(0);
    });

    test('visualització de procediments associats a un servei', async ({ adminPage: page }) => {
        const serveis = new ServeisPage(page);
        await serveis.goto();
        await serveis.filtrar(async (p) => {
            await p.locator('#codi').fill('Q2827003ATGSS001');
        });
        await expect(serveis.row('Q2827003ATGSS001')).toBeVisible({ timeout: 15_000 });

        const modal = await serveis.obrirProcediments('Q2827003ATGSS001');
        await expect(modal).toContainText('E2EPROC01');
        await serveis.tancarProcediments();
    });

    test('formulari: fitxers XSD, gestió de grups, regles i previsualització', async ({ adminPage: page }) => {
        const suffix = uniqueSuffix();
        const codi = `E2ESRVX${suffix}`.toUpperCase().slice(0, 30);
        const serveis = new ServeisPage(page);
        const form = new ServeiFormPage(page);
        const camp = new ServeiCampPage(page);

        await serveis.gotoNew();
        await form.fillRequired(serveiFieldsBasics(suffix, codi, `Servei E2E formulari ${suffix}`));
        await form.submitAndExpectBackToList();

        // --- Activar gestió d'XSD i pujar un fitxer ---
        await serveis.gotoEdit(codi);
        await form.setActivaGestioXsd(true);
        await form.submitAndExpectBackToList();

        await serveis.gotoEdit(codi);
        await form.afegirXsd({
            name: 'e2e-schema.xsd',
            content:
                '<?xml version="1.0" encoding="UTF-8"?>'
                + '<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"/>',
        });
        await expect(page.locator('#arxiusXsd')).toContainText('e2e-schema.xsd', { timeout: 15_000 });

        // Esborrar el fitxer XSD (confirm() natiu + AJAX + recàrrega de pàgina)
        page.once('dialog', (dialog) => dialog.accept());
        await page.locator('#arxiusXsd tbody tr button').click();
        await page.waitForLoadState('load');
        await expect(page.locator('#arxiusXsd')).toHaveCount(0);

        // --- Gestió de grups de camps ---
        const grupNom = `Grup E2E ${suffix}`;
        const grupNomEditat = `${grupNom} editat`;
        await camp.goto(codi);
        await camp.afegirGrup(grupNom);
        await camp.editarGrup(grupNom, grupNomEditat);

        // --- Creació i aplicació d'una regla (referenciant el grup creat) ---
        const reglaNom = `Regla E2E ${suffix}`;
        await camp.afegirRegla({ nom: reglaNom, modificat: 'GRUPS', valor: grupNomEditat, accio: 'MOSTRAR' });
        await expect(page.locator('#taula-regles')).toContainText(reglaNom);

        await camp.editarReglaAccio(reglaNom, 'OCULTAR');
        await expect(page.locator('#taula-regles tbody tr', { hasText: reglaNom })).toContainText(/ocultar/i);

        await camp.esborrarRegla(reglaNom);
        await expect(page.locator('#taula-regles')).not.toContainText(reglaNom);

        // --- Previsualització del formulari ---
        await camp.obrirPreview();
        await camp.tancarPreview();

        // Neteja del grup creat
        await camp.esborrarUnicGrup();

        // --- Justificant ---
        await serveis.gotoJustificant(codi);
        await expect(page.locator('#table-servei-justificant')).toBeVisible();

        // --- Neteja del servei de prova ---
        await serveis.goto();
        await serveis.esborrar(codi);
    });
});
