import { test, expect } from '../../utils/fixtures';
import { clickModalFooterButton, clickModalFooterButtonById, modalFrame, waitForModalClosed } from '../../utils/modal';
import { waitForDataTableReload, waitForInitialDataTableLoad } from '../../utils/datatable';
import { expectSuccessMessage } from '../../utils/messages';
import { credentials, requireCredentials, uniqueSuffix, USUARI_FIX_ACTIU_CODI } from '../../utils/env';
import { login, logout } from '../../utils/auth';
import { AdminMaintenancePage } from '../../pages/AdminMaintenancePage';
import { uniqueTestCif } from '../../utils/cif';

/**
 * CRUD complet d'entitats (secció d'administració). Aquest flux exemplifica
 * el patró general de les pantalles d'administració de PINBAL (llistat amb
 * DataTable server-side + modal amb iframe per crear/editar), reutilitzable
 * per a la resta de manteniments (serveis, procediments, usuaris...).
 */
test.describe('Gestió d\'entitats (administrador)', () => {
    test('crear, editar, desactivar, activar i esborrar una entitat', async ({ adminPage: page }) => {
        const suffix = uniqueSuffix();
        const codi = `E2E${suffix}`.toUpperCase().slice(0, 20);
        const nomInicial = `Entitat E2E ${suffix}`;
        const nomModificat = `Entitat E2E ${suffix} modificada`;
        const cif = uniqueTestCif();
        const dir3 = `E${suffix}`.toUpperCase().slice(0, 9);

        await page.goto('entitat');
        await waitForInitialDataTableLoad(page);

        // --- Crear ---
        await page.locator('#btNovaEntitat').click();
        let frame = await modalFrame(page);
        await frame.locator('#codi').fill(codi);
        await frame.locator('#nom').fill(nomInicial);
        await frame.locator('#cif').fill(cif);
        await frame.locator('#unitatArrel').fill(dir3);
        await frame.locator('#tipus').selectOption('AJUNTAMENT', { force: true });

        await waitForDataTableReload(page, async () => {
            await clickModalFooterButtonById(page, 'btGuardarEntitat');
        });
        await waitForModalClosed(page);
        await expectSuccessMessage(page);

        const fila = () => page.locator('#table-entitats tbody tr', { hasText: codi });
        await expect(fila()).toBeVisible({ timeout: 15_000 });
        await expect(fila()).toContainText(nomInicial);

        // --- Editar ---
        await fila().locator('a.dropdown-toggle, button.dropdown-toggle').click();
        await fila().getByRole('link', { name: /modificar/i }).click();
        frame = await modalFrame(page);
        await frame.locator('#nom').fill(nomModificat);
        await waitForDataTableReload(page, async () => {
            await clickModalFooterButtonById(page, 'btGuardarEntitat');
        });
        await waitForModalClosed(page);
        await expectSuccessMessage(page);
        await expect(fila()).toContainText(nomModificat);

        // --- Desactivar ---
        await fila().locator('a.dropdown-toggle, button.dropdown-toggle').click();
        await waitForDataTableReload(page, async () => {
            await fila().getByRole('link', { name: /desactivar/i }).click();
        });
        await fila().locator('a.dropdown-toggle, button.dropdown-toggle').click();
        await expect(fila().getByRole('link', { name: /activar/i })).toBeVisible();

        // --- Activar ---
        await waitForDataTableReload(page, async () => {
            await fila().getByRole('link', { name: /^activar/i }).click();
        });

        // --- Esborrar (neteja de dades de prova; algunes instal·lacions poden tenir-ho desactivat) ---
        await fila().locator('a.dropdown-toggle, button.dropdown-toggle').click();
        const linkEsborrar = fila().getByRole('link', { name: /esborrar/i });
        if (await linkEsborrar.isVisible().catch(() => false)) {
            page.once('dialog', (dialog) => dialog.accept());
            await waitForDataTableReload(page, async () => {
                await linkEsborrar.click();
            });
            await expect(page.locator('#table-entitats tbody tr', { hasText: codi })).toHaveCount(0);
        }
    });

    test('el llistat es pot filtrar per codi i el filtre es pot netejar', async ({ adminPage: page }) => {
        await page.goto('entitat');
        await waitForInitialDataTableLoad(page);

        await waitForDataTableReload(page, async () => {
            await page.locator('#codi').fill('CODI_QUE_NO_HAURIA_D_EXISTIR_E2E');
            await page.locator('#form-filtre button[type="submit"]').click();
        });
        await expect(page.locator('#table-entitats td.dataTables_empty')).toBeVisible({ timeout: 15_000 });

        await waitForDataTableReload(page, async () => {
            await page.locator('#netejar-filtre').click();
        });
        await expect(page.locator('#codi')).toHaveValue('');
        await expect(page.locator('#table-entitats td.dataTables_empty')).toHaveCount(0);
    });
});

/**
 * Sub-parts de "1. Entitats" del checklist que NO cobreix el describe anterior
 * (CRUD/llistat/filtre de l'entitat en si mateixa): gestió d'usuaris
 * d'entitat, visualització/activació de serveis d'entitat, i la regla
 * transversal de visibilitat d'entitats desactivades.
 *
 * Nota important sobre "codi" d'usuari: el camp de l'autocompletat
 * (`pbl:inputSuggest`, servei /usuariajax/usuari/extern(s)) delega en un
 * plugin extern de dades d'usuari (`DadesUsuariPlugin`) que NO està
 * configurat a l'entorn e2e/H2 (cap fila a pbl_config per la clau
 * `es.caib.pinbal.plugin.dades.usuari.class`), així que la consulta sempre
 * falla. Per això, per crear un nou vincle entitat-usuari cal reutilitzar un
 * `codi` que ja existeixi a pbl_usuari i estigui "inicialitzat" (evita la
 * crida al plugin): s'usa l'usuari sembrat a 00_e2e_seed_data.yaml
 * (E2E_USER_ACTIU), injectant l'opció al <select> subjacent per Playwright
 * ja que el desplegable de suggeriments no pot funcionar sense el plugin.
 */
test.describe('Gestió d\'entitats (administrador): usuaris i serveis', () => {
    test('gestió d\'usuaris d\'entitat: CRUD, filtre, activar/desactivar, fer principal', async ({ adminPage: page }) => {
        const suffix = uniqueSuffix();
        const codiEntitat = `E2EU${suffix}`.toUpperCase().slice(0, 20);
        const usuariCodi = USUARI_FIX_ACTIU_CODI;

        // Crea una entitat pròpia (evita tocar E2EENT01/E2EENT02, compartides amb altres tests).
        await page.goto('entitat');
        await waitForInitialDataTableLoad(page);
        await page.locator('#btNovaEntitat').click();
        let frame = await modalFrame(page);
        await frame.locator('#codi').fill(codiEntitat);
        await frame.locator('#nom').fill(`Entitat E2E usuaris ${suffix}`);
        await frame.locator('#cif').fill(uniqueTestCif());
        await frame.locator('#unitatArrel').fill(`E${suffix}`.toUpperCase().slice(0, 9));
        await frame.locator('#tipus').selectOption('AJUNTAMENT', { force: true });
        await waitForDataTableReload(page, async () => {
            await clickModalFooterButtonById(page, 'btGuardarEntitat');
        });
        await waitForModalClosed(page);
        await expectSuccessMessage(page);

        const filaEntitat = page.locator('#table-entitats tbody tr', { hasText: codiEntitat });
        await expect(filaEntitat).toBeVisible({ timeout: 15_000 });

        // Navega a la gestió d'usuaris d'aquesta entitat.
        await filaEntitat.getByRole('link', { name: /usuaris/i }).click();
        await waitForInitialDataTableLoad(page);

        // --- Crear ---
        await page.locator('a[href*="/usuari/new"]').click();
        const ufFrame = await modalFrame(page);
        // Injecta l'opció manualment: l'autocompletat real no pot resoldre-la (vegeu comentari de capçalera).
        await ufFrame.locator('#codi').evaluate((el: HTMLSelectElement, codi: string) => {
            const opt = document.createElement('option');
            opt.value = codi;
            opt.text = codi;
            el.appendChild(opt);
        }, usuariCodi);
        await ufFrame.locator('#codi').selectOption(usuariCodi, { force: true });
        await ufFrame.locator('#departament').fill('Departament E2E inicial');
        await ufFrame.locator('#actiu').check();
        await ufFrame.locator('#rolDelegat').check();
        await clickModalFooterButton(page, /guardar/i);
        await waitForModalClosed(page);
        await expectSuccessMessage(page);

        const filaUsuari = page.locator('#table-users tbody tr', { hasText: usuariCodi });
        await expect(filaUsuari).toBeVisible({ timeout: 15_000 });

        // --- Editar ---
        await filaUsuari.locator('a.dropdown-toggle, button.dropdown-toggle').click();
        await filaUsuari.getByRole('link', { name: /modificar/i }).click();
        const editFrame = await modalFrame(page);
        await editFrame.locator('#departament').fill('Departament E2E editat');
        await clickModalFooterButton(page, /guardar|modificar/i);
        await waitForModalClosed(page);
        await expectSuccessMessage(page);
        await expect(filaUsuari).toContainText('Departament E2E editat');

        // --- Filtre ---
        await waitForDataTableReload(page, async () => {
            await page.locator('#codi').fill(usuariCodi);
            await page.locator('#form-filtre button[type="submit"]').click();
        });
        await expect(filaUsuari).toBeVisible();
        await waitForDataTableReload(page, async () => {
            await page.locator('#codi').fill('CODI_QUE_NO_EXISTEIX_E2E');
            await page.locator('#form-filtre button[type="submit"]').click();
        });
        await expect(page.locator('#table-users td.dataTables_empty')).toBeVisible({ timeout: 15_000 });
        await waitForDataTableReload(page, async () => {
            await page.locator('#netejar-filtre').click();
        });
        await expect(filaUsuari).toBeVisible();

        // --- Activar / Desactivar ---
        await filaUsuari.locator('a.dropdown-toggle, button.dropdown-toggle').click();
        await waitForDataTableReload(page, async () => {
            await filaUsuari.getByRole('link', { name: /desactivar/i }).click();
        });
        await filaUsuari.locator('a.dropdown-toggle, button.dropdown-toggle').click();
        await expect(filaUsuari.getByRole('link', { name: /^activar/i })).toBeVisible();
        await waitForDataTableReload(page, async () => {
            await filaUsuari.getByRole('link', { name: /^activar/i }).click();
        });

        // --- Fer principal / Desfer principal ---
        await filaUsuari.locator('a.dropdown-toggle, button.dropdown-toggle').click();
        await waitForDataTableReload(page, async () => {
            await filaUsuari.getByRole('link', { name: /^fer principal/i }).click();
        });
        await filaUsuari.locator('a.dropdown-toggle, button.dropdown-toggle').click();
        await expect(filaUsuari.getByRole('link', { name: /^desfer principal/i })).toBeVisible();
        await waitForDataTableReload(page, async () => {
            await filaUsuari.getByRole('link', { name: /^desfer principal/i }).click();
        });

        // --- Neteja: no es pot esborrar un vincle entitat-usuari (no hi ha aquesta acció a la UI),
        // així que es desactiva l'entitat de prova sencera.
        await page.goto('entitat');
        await waitForInitialDataTableLoad(page);
        await filaEntitat.locator('a.dropdown-toggle, button.dropdown-toggle').click();
        await waitForDataTableReload(page, async () => {
            await filaEntitat.getByRole('link', { name: /desactivar/i }).click();
        });
    });

    test('gestió de serveis d\'entitat: visualització i activar/desactivar', async ({ adminPage: page }) => {
        await page.goto('entitat/900001/servei');
        await waitForInitialDataTableLoad(page);

        const codisEsperats = ['Q2827003ATGSS001', 'SCDCPAJU', 'SVDDGTVEHICULOSANCWS01', 'SVDDGPCIWS02'];
        for (const codi of codisEsperats) {
            await expect(page.locator('#table-serveis tbody tr', { hasText: codi })).toBeVisible({ timeout: 15_000 });
        }

        const filaServei = page.locator('#table-serveis tbody tr', { hasText: 'SVDDGPCIWS02' });
        await expect(filaServei.getByRole('link', { name: /desactivar/i })).toBeVisible();

        // Desactivar (confirm() natiu).
        page.once('dialog', (dialog) => dialog.accept());
        await waitForDataTableReload(page, async () => {
            await filaServei.getByRole('link', { name: /desactivar/i }).click();
        });
        await expect(filaServei.getByRole('link', { name: /^activar/i })).toBeVisible();

        // Reactivar (deixa l'estat com estava).
        await waitForDataTableReload(page, async () => {
            await filaServei.getByRole('link', { name: /^activar/i }).click();
        });
        await expect(filaServei.getByRole('link', { name: /desactivar/i })).toBeVisible();
    });

    test('si una entitat es desactiva, desapareix de les entitats accessibles del rol, i viceversa', async ({
        adminPage,
        delegatPage,
    }) => {
        const entitats = new AdminMaintenancePage(adminPage, { url: 'entitat', tableId: 'table-entitats' });
        await entitats.goto();

        const filaE2EENT02 = entitats.row('E2EENT02');
        await expect(filaE2EENT02).toBeVisible({ timeout: 15_000 });

        // Assegura que E2EENT02 estigui activa abans de vincular-hi el delegat (el seed la crea inactiva).
        await filaE2EENT02.locator('a.dropdown-toggle, button.dropdown-toggle').click();
        const activarLink = filaE2EENT02.getByRole('link', { name: /^activar/i });
        if (await activarLink.isVisible().catch(() => false)) {
            await waitForDataTableReload(adminPage, async () => {
                await activarLink.click();
            });
        } else {
            await adminPage.keyboard.press('Escape');
        }

        // Vincula el delegat a E2EENT02 (900002) si encara no hi té accés.
        await filaE2EENT02.getByRole('link', { name: /usuaris/i }).click();
        await waitForInitialDataTableLoad(adminPage);
        const delegatCodi = requireCredentials(credentials.delegat, 'delegat').username;
        const filaDelegat = adminPage.locator('#table-users tbody tr', { hasText: delegatCodi });
        if ((await filaDelegat.count()) === 0) {
            await adminPage.locator('a[href*="/usuari/new"]').click();
            const frame = await modalFrame(adminPage);
            await frame.locator('#codi').evaluate((el: HTMLSelectElement, codi: string) => {
                const opt = document.createElement('option');
                opt.value = codi;
                opt.text = codi;
                el.appendChild(opt);
            }, delegatCodi);
            await frame.locator('#codi').selectOption(delegatCodi, { force: true });
            await frame.locator('#actiu').check();
            await frame.locator('#rolDelegat').check();
            await clickModalFooterButton(adminPage, /guardar/i);
            await waitForModalClosed(adminPage);
            await expectSuccessMessage(adminPage);
        }

        // La llista d'entitats accessibles es cacheja a la sessió HTTP: cal refer login
        // perquè el delegat vegi el canvi (un simple reload no la refresca).
        const delegatCreds = requireCredentials(credentials.delegat, 'delegat');
        await logout(delegatPage);
        await login(delegatPage, delegatCreds);
        await expect(delegatPage.locator('#menu_entitat')).toBeVisible({ timeout: 15_000 });
        await expect(
            delegatPage.locator('#menu_entitat + ul.dropdown-menu a', { hasText: 'Entitat E2E Inactiva' }),
        ).toBeVisible({ timeout: 15_000 });

        // Desactiva l'entitat i comprova que desapareix (l'usuari torna a tenir una única entitat accessible).
        await filaE2EENT02.locator('a.dropdown-toggle, button.dropdown-toggle').click();
        await waitForDataTableReload(adminPage, async () => {
            await filaE2EENT02.getByRole('link', { name: /desactivar/i }).click();
        });
        await logout(delegatPage);
        await login(delegatPage, delegatCreds);
        await expect(delegatPage.locator('#menu_entitat')).not.toBeVisible({ timeout: 15_000 });

        // Reactiva i comprova que reapareix.
        await filaE2EENT02.locator('a.dropdown-toggle, button.dropdown-toggle').click();
        await waitForDataTableReload(adminPage, async () => {
            await filaE2EENT02.getByRole('link', { name: /^activar/i }).click();
        });
        await logout(delegatPage);
        await login(delegatPage, delegatCreds);
        await expect(delegatPage.locator('#menu_entitat')).toBeVisible({ timeout: 15_000 });
        await expect(
            delegatPage.locator('#menu_entitat + ul.dropdown-menu a', { hasText: 'Entitat E2E Inactiva' }),
        ).toBeVisible({ timeout: 15_000 });

        // Neteja: deixa E2EENT02 com estava originalment (inactiva, segons el seed).
        await filaE2EENT02.locator('a.dropdown-toggle, button.dropdown-toggle').click();
        await waitForDataTableReload(adminPage, async () => {
            await filaE2EENT02.getByRole('link', { name: /desactivar/i }).click();
        });
    });
});
