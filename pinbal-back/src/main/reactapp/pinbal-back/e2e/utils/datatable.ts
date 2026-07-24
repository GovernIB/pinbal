import { Locator, Page } from '@playwright/test';

/**
 * Helpers per a les taules jQuery DataTables amb processament al servidor
 * (serverSide: true) que s'utilitzen a pràcticament tots els llistats de
 * PINBAL. Totes fan una petició POST a una URL que conté "/datatable".
 */

export function dataTableRows(page: Page, tableId: string): Locator {
    return page.locator(`#${tableId} tbody tr`);
}

/**
 * Espera la resposta de la primera càrrega del DataTable després d'anar a
 * una pàgina de llistat. S'ha de cridar just després de `page.goto(...)`.
 */
export async function waitForInitialDataTableLoad(page: Page): Promise<void> {
    await page.waitForResponse(
        (resp) => resp.url().includes('/datatable') && resp.request().method() === 'POST' && resp.status() === 200,
        { timeout: 20_000 },
    );
}

/**
 * Executa `action` (p.ex. clicar "Filtrar" o "Netejar") i espera que el
 * DataTable hagi rebut la nova pàgina de resultats abans de continuar.
 * Evitar `waitForResponse` fora d'aquest patró és important per no crear
 * una condició de carrera entre l'acció i l'escolta de la resposta.
 */
export async function waitForDataTableReload(page: Page, action: () => Promise<void>): Promise<void> {
    const responsePromise = page.waitForResponse(
        (resp) => resp.url().includes('/datatable') && resp.request().method() === 'POST' && resp.status() === 200,
        { timeout: 20_000 },
    );
    await action();
    await responsePromise;
}
