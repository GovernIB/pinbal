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

    // select2 v4 aplica la classe "select2-container--open" a DOS nodes DIFERENTS mentre el
    // desplegable és obert: el propi widget clicat (el trigger inline) I el panell flotant que
    // hi apareix (posicionat "position:absolute", amb el camp de cerca i els resultats a dins) --
    // no és una condició de carrera, és el marcatge normal de select2. `.select2-container--open`
    // sol, doncs, sempre viola el "strict mode" quan hi ha un desplegable obert; cal escopir-se
    // al panell concret (`.select2-dropdown`, únic d'aquest node).
    const openDropdown = root.locator('.select2-dropdown');
    await expect(openDropdown).toBeVisible({ timeout: 10_000 });
    await openDropdown.locator('.select2-search__field').fill(searchText);

    // NOTA: en aquesta app els resultats es renderitzen amb role="treeitem" (dins un <ul
    // role="tree">), NO role="option" (el "llistbox"/"option" més habitual d'altres temes/
    // configuracions de select2) -- filtrar per [role="option"] no trobava mai res, per a cap
    // select "suggest" d'aquest codebase (confirmat inspeccionant l'HTML real del desplegable
    // després d'una cerca amb resultats). La classe `.select2-results__option` per si sola ja
    // identifica de manera fiable els resultats, sense dependre del rol ARIA concret.
    const options = openDropdown.locator('.select2-results__option');
    const targetOption = optionText ? options.filter({ hasText: optionText }).first() : options.first();
    await expect(targetOption).toBeVisible({ timeout: 15_000 });
    await targetOption.click();
}
