import { FrameLocator, Page, expect } from '@playwright/test';

/**
 * PINBAL utilitza select2 (v4, tema bootstrap) tant per a selects "estàtics"
 * (`pbl:inputSelect`, `data-toggle="select2"`, les `<option>` ja existeixen al
 * DOM en carregar la pàgina) com per a selects amb cerca "suggest" contra un
 * endpoint ajax (`pbl:inputSuggest`, `data-toggle="suggest"`, vegeu
 * webutil.common.js): en aquest segon cas les `<option>` es generen dinàmicament
 * a mesura que s'escriu, així que `selectOption()` no serveix (no hi ha
 * `<option>` prèvia per triar) i cal interactuar amb el desplegable.
 *
 * Per als selects "estàtics" no cal aquest helper: n'hi ha prou amb
 * `page.locator('#nomCamp').selectOption(valor, { force: true })` (el
 * `force` evita l'error de visibilitat, ja que select2 amaga el <select>
 * original amb CSS).
 *
 * Rep un `Page` o un `FrameLocator`: els formularis oberts dins la modal amb
 * iframe (vegeu `utils/modal.ts`) executen select2 dins el `document` propi
 * de l'iframe (el desplegable NO es renderitza al DOM de la pàgina pare), així
 * que cal passar-hi el `FrameLocator` del formulari en aquests casos.
 */
export async function selectAjaxSuggestOption(
    root: Page | FrameLocator,
    selectId: string,
    searchText: string,
    optionText?: string | RegExp,
): Promise<void> {
    await root.locator(`#${selectId}`).locator('xpath=following-sibling::span[contains(@class, "select2")][1]').click();

    const openDropdown = root.locator('.select2-container--open');
    await expect(openDropdown).toBeVisible({ timeout: 10_000 });
    await openDropdown.locator('.select2-search__field').fill(searchText);

    const options = openDropdown.locator('.select2-results__option[role="option"]');
    const targetOption = optionText ? options.filter({ hasText: optionText }).first() : options.first();
    await expect(targetOption).toBeVisible({ timeout: 15_000 });
    await targetOption.click();
}
