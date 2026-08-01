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

/**
 * Retorna el localizador de la modal Bootstrap actualment oberta.
 *
 * NOTA: aquest `div.modal` sempre porta `aria-hidden="true"` fins i tot
 * mostrat (bug de `webutil.modal.js`/plantilla, no de Bootstrap: vegeu
 * `clickModalFooterButton`), així que cap locator basat en rol ARIA hi
 * funciona a dins; feu servir sempre selectors CSS/text.
 */
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
    // Just després d'assignar `src` (webutil.modal.js), l'iframe passa per un instant
    // transitori a "about:blank" abans que comenci la navegació real cap al contingut;
    // com que un <body> buit també és "visible", si el següent check es fes directament
    // sobre `frame.locator('body')` es podria satisfer immediatament contra aquest estat
    // transitori (el primer check síncron d'`expect().toBeVisible()` ja el dona per bo,
    // sense arribar mai a re-comprovar contra el contingut real un cop carregat). Cal
    // esperar explícitament que la navegació real hagi començat abans de mirar el body.
    await expect
        .poll(
            async () => {
                const handle = await iframe.elementHandle();
                const contentFrame = await handle?.contentFrame();
                return contentFrame?.url() ?? '';
            },
            { timeout: 15_000 },
        )
        .not.toBe('about:blank');
    const frame = modal.frameLocator('iframe');
    // Espera que el <body> de l'iframe s'hagi renderitzat (evita interactuar amb un frame en blanc).
    await expect(frame.locator('body')).toBeVisible({ timeout: 15_000 });
    return frame;
}

/**
 * Fa clic a un botó clonat al peu de la modal (fora de l'iframe), pel seu text
 * visible.
 *
 * NO es pot fer servir `getByRole('button', { name })` aquí: el `<div
 * class="modal">` generat per `webutil.modal.js` porta `aria-hidden="true"`
 * codificat a la plantilla inicial i mai s'actualitza a `false` en mostrar-se
 * (ni el propi `webutil.modal.js` ni el Bootstrap 3.3.6 empaquetat toquen
 * aquest atribut a `show`/`hide`; vegeu comentari a `activeModal`). Com que
 * `aria-hidden="true"` exclou tot el subarbre de l'accessibility tree, CAP
 * locator basat en rol ARIA (`getByRole`) hi troba mai res, encara que
 * l'element sigui perfectament visible i clicable amb un ratolí real. Per
 * això aquí es fa servir un selector CSS + `hasText` (no basat en ARIA).
 */
export async function clickModalFooterButton(page: Page, name: string | RegExp): Promise<void> {
    const modal = activeModal(page);
    await modal.locator('.modal-footer button, .modal-footer a.btn', { hasText: name }).click();
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
