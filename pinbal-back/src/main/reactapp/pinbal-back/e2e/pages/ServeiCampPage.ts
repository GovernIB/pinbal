import { Page, expect } from '@playwright/test';
import { clickModalFooterButton, modalFrame, waitForModalClosed } from '../utils/modal';

/**
 * Pàgina `/servei/{codi}/camp`: gestió de grups de camps i regles d'un
 * servei. A diferència del formulari principal del servei, aquí conviuen
 * TRES mecanismes de modal diferents:
 *  - "Nou grup"/"Modificar grup": modal Bootstrap normal, embeguda
 *    directament a la pàgina (sense iframe), enviament per POST normal
 *    (recàrrega de pàgina).
 *  - "Nova regla"/editar regla: modal amb iframe (patró webutil.modal.js
 *    estàndard, vegeu e2e/utils/modal.ts), amb `data-refresh-pagina="true"`
 *    (en desar, tota la pàgina externa es recarrega).
 *  - Previsualització: modal Bootstrap normal amb el body carregat per AJAX.
 */
export class ServeiCampPage {
    constructor(private readonly page: Page) {}

    async goto(codi: string): Promise<void> {
        await this.page.goto(`servei/${codi}/camp`);
    }

    // --- Grups de camps (modal Bootstrap normal, sense iframe) ---

    async afegirGrup(nom: string): Promise<void> {
        const page = this.page;
        await page.locator('#boto-boto-grup-nou').click();
        await expect(page.locator('#modal-grup-form')).toBeVisible({ timeout: 15_000 });
        await page.locator('#modal-grup-input-nom').fill(nom);
        await page.locator('#modal-grup-boto-submit').click();
        await page.waitForLoadState('load');
        await expect(page.locator('legend', { hasText: nom })).toBeVisible({ timeout: 15_000 });
    }

    async editarGrup(nomActual: string, nomNou: string): Promise<void> {
        const page = this.page;
        await page.locator(`.boto-grup-editar[data-nom="${nomActual}"]`).click();
        await expect(page.locator('#modal-grup-form')).toBeVisible({ timeout: 15_000 });
        await page.locator('#modal-grup-input-nom').fill(nomNou);
        await page.locator('#modal-grup-boto-submit').click();
        await page.waitForLoadState('load');
        await expect(page.locator('legend', { hasText: nomNou })).toBeVisible({ timeout: 15_000 });
    }

    /** Esborra l'únic grup existent a la pàgina (accepta el confirm() natiu). */
    async esborrarUnicGrup(): Promise<void> {
        const page = this.page;
        page.once('dialog', (dialog) => dialog.accept());
        await page.locator('.confirm-esborrar-grup').click();
        await page.waitForLoadState('load');
    }

    // --- Regles (modal amb iframe, patró estàndard) ---

    async afegirRegla(opts: {
        nom: string;
        modificat: 'CAMPS' | 'ALGUN_CAMP' | 'GRUPS' | 'ALGUN_GRUP';
        valor: string;
        accio: 'MOSTRAR' | 'OCULTAR' | 'EDITAR' | 'BLOQUEJAR' | 'OBLIGATORI' | 'OPCIONAL';
    }): Promise<void> {
        const page = this.page;
        await page.locator('a[href*="/regla/new"]').click();
        const frame = await modalFrame(page);
        await frame.locator('#nom').fill(opts.nom);
        await frame.locator('#modificat').selectOption(opts.modificat, { force: true });
        // El canvi de "modificat" dispara una crida AJAX que omple les opcions
        // de modificatValor/afectatValor; cal esperar que arribi l'opció.
        await expect(frame.locator('#modificatValor option', { hasText: opts.valor })).toHaveCount(1, {
            timeout: 15_000,
        });
        await frame.locator('#modificatValor').selectOption(opts.valor, { force: true });
        await frame.locator('#afectatValor').selectOption(opts.valor, { force: true });
        await frame.locator('#accio').selectOption(opts.accio, { force: true });
        await clickModalFooterButton(page, /guardar/i);
        await waitForModalClosed(page);
    }

    /** Edita la regla identificada per `nom` obrint la seva modal des de la taula. */
    async editarReglaAccio(
        nom: string,
        novaAccio: 'MOSTRAR' | 'OCULTAR' | 'EDITAR' | 'BLOQUEJAR' | 'OBLIGATORI' | 'OPCIONAL',
    ): Promise<void> {
        const page = this.page;
        const fila = page.locator('#taula-regles tbody tr', { hasText: nom });
        await fila.locator('a.dropdown-toggle, button.dropdown-toggle').first().click();
        await fila.getByRole('link', { name: /modificar/i }).click();
        const frame = await modalFrame(page);
        await frame.locator('#accio').selectOption(novaAccio, { force: true });
        await clickModalFooterButton(page, /guardar/i);
        await waitForModalClosed(page);
    }

    async esborrarRegla(nom: string): Promise<void> {
        const page = this.page;
        const fila = page.locator('#taula-regles tbody tr', { hasText: nom });
        await fila.locator('a.dropdown-toggle, button.dropdown-toggle').first().click();
        page.once('dialog', (dialog) => dialog.accept());
        await fila.getByRole('link', { name: /esborrar/i }).click();
        await page.waitForLoadState('load');
    }

    // --- Previsualització (modal Bootstrap normal, body carregat per AJAX) ---

    async obrirPreview(): Promise<void> {
        const page = this.page;
        await page.locator('a[href*="/preview"]').click();
        const modal = page.locator('#modal-form-preview');
        await expect(modal).toBeVisible({ timeout: 15_000 });
        await expect(modal.locator('.modal-body')).not.toBeEmpty({ timeout: 15_000 });
    }

    async tancarPreview(): Promise<void> {
        const page = this.page;
        await page.locator('#modal-form-preview .modal-footer button').click();
        await expect(page.locator('#modal-form-preview')).not.toBeVisible();
    }
}
