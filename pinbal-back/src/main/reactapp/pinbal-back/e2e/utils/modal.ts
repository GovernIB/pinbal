import { FrameLocator, Locator, Page, expect } from '@playwright/test';

/**
 * PINBAL obre els formularis de crear/editar/detall dins una modal Bootstrap 3
 * que carrega el contingut real dins un <iframe> (js/webutil.modal.js).
 * Els botons del formulari ("Guardar", "Cancel·lar"...) es clonen al peu de
 * la modal (.modal-footer) i és allà on cal fer clic; els camps del
 * formulari, en canvi, viuen dins l'iframe.
 *
 * Flux típic d'ús en un test:
 *   await page.locator('#btNovaEntitat').click();
 *   const frame = await modalFrame(page);
 *   await frame.locator('#codi').fill('ENT01');
 *   await clickModalFooterButton(page, /guardar/i);
 *   await waitForModalClosed(page);
 */

/** Retorna el localizador de la modal Bootstrap actualment oberta. */
export function activeModal(page: Page): Locator {
    return page.locator('div.modal.in');
}

/**
 * Espera que la modal estigui oberta i que l'iframe intern hagi carregat
 * contingut, i en retorna el FrameLocator per interactuar amb el formulari.
 */
export async function modalFrame(page: Page): Promise<FrameLocator> {
    const modal = activeModal(page);
    await expect(modal).toBeVisible({ timeout: 15_000 });
    const iframe = modal.locator('iframe');
    await expect(iframe).toHaveAttribute('src', /.+/, { timeout: 15_000 });
    const frame = modal.frameLocator('iframe');
    // Espera que el <body> de l'iframe s'hagi renderitzat (evita interactuar amb un frame en blanc).
    await expect(frame.locator('body')).toBeVisible({ timeout: 15_000 });
    return frame;
}

/** Fa clic a un botó clonat al peu de la modal (fora de l'iframe), pel seu text visible. */
export async function clickModalFooterButton(page: Page, name: string | RegExp): Promise<void> {
    const modal = activeModal(page);
    await modal.locator('.modal-footer').getByRole('button', { name }).click();
}

/**
 * Fa clic a un botó clonat al peu de la modal pel seu `id` original (el clon
 * conserva l'id del botó real dins l'iframe). Preferible a
 * {@link clickModalFooterButton} quan el formulari té un id estable, ja que
 * no depèn del text (i, per tant, de l'idioma) del botó.
 */
export async function clickModalFooterButtonById(page: Page, id: string): Promise<void> {
    const modal = activeModal(page);
    await modal.locator(`.modal-footer #${id}`).click();
}

/** Espera que la modal actualment oberta es tanqui (èxit de l'acció del formulari). */
export async function waitForModalClosed(page: Page): Promise<void> {
    await expect(page.locator('div.modal.in')).toHaveCount(0, { timeout: 15_000 });
}

/**
 * Retorna els textos dels errors de validació mostrats dins el formulari
 * de la modal (classe .help-block, generada per <form:errors>).
 */
export async function modalValidationErrors(frame: FrameLocator): Promise<string[]> {
    return frame.locator('.help-block').allInnerTexts();
}
