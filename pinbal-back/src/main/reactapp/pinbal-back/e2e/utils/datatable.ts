import { Locator, Page } from '@playwright/test';

/**
 * Helpers per a les taules jQuery DataTables amb processament al servidor
 * (serverSide: true) que s'utilitzen a pràcticament tots els llistats de
 * PINBAL. Totes fan una petició a una URL que conté "/datatable".
 *
 * El mètode HTTP no és uniforme: la configuració per defecte de DataTables
 * 1.10 (`sServerMethod: "GET"`) s'utilitza tal qual a la majoria de llistats
 * (p.ex. `/entitat/datatable`, `/cache/datatable`, `/avis/datatable` són tots
 * `@GetMapping` al costat servidor i cap JSP sobreescriu `ajax.type`/`method`),
 * per això acceptam tant GET com POST aquí en lloc de donar per fet POST.
 */

export function dataTableRows(page: Page, tableId: string): Locator {
    return page.locator(`#${tableId} tbody tr`);
}

function isDataTableResponse(resp: import('@playwright/test').Response): boolean {
    return (
        resp.url().includes('/datatable') &&
        (resp.request().method() === 'GET' || resp.request().method() === 'POST') &&
        resp.status() === 200
    );
}

/**
 * Espera la resposta de la primera càrrega del DataTable després d'anar a
 * una pàgina de llistat. S'ha de cridar just després de `page.goto(...)`.
 */
export async function waitForInitialDataTableLoad(page: Page): Promise<void> {
    await page.waitForResponse(isDataTableResponse, { timeout: 20_000 });
}

/**
 * Executa `action` (p.ex. clicar "Filtrar" o "Netejar") i espera que el
 * DataTable hagi rebut la nova pàgina de resultats abans de continuar.
 * Evitar `waitForResponse` fora d'aquest patró és important per no crear
 * una condició de carrera entre l'acció i l'escolta de la resposta.
 */
export async function waitForDataTableReload(page: Page, action: () => Promise<void>): Promise<void> {
    const responsePromise = page.waitForResponse(isDataTableResponse, { timeout: 20_000 });
    await action();
    await responsePromise;
}
