import { Locator, Page } from '@playwright/test';
import { waitForDataTableReload } from '../utils/datatable';
import { expectSuccessMessage } from '../utils/messages';

/**
 * Pantalla de gestió de la cache de l'aplicació (/cache).
 *
 * Particularitats determinades llegint cacheList.jsp / CacheController /
 * webutil.common.js ($.fn.webutilAjax):
 *  - L'acció de buidar una cache concreta (per fila) té `data-toggle="ajax"`
 *    i viu dins el `.dataTables_wrapper`, per la qual cosa l'èxit fa una
 *    crida `dataTable().fnDraw()` que refresca sola el llistat (per això es
 *    pot fer amb `waitForDataTableReload`).
 *  - El botó "Buidar totes les caches" (capçalera) també té `data-toggle=
 *    "ajax"` però és fora del `.dataTables_wrapper`, així que el `fnDraw()`
 *    NO s'executa per a ell: cal recarregar la pàgina manualment per veure
 *    el llistat actualitzat (és el que demana explícitament el checklist:
 *    "Refrescar i comprovar que totes queden a 0").
 */
export class CachePage {
    constructor(private readonly page: Page) {}

    /**
     * Navega/recarrega i espera la primera càrrega del DataTable, registrant
     * l'escolta de la resposta ABANS de navegar (via `waitForDataTableReload`).
     * Fer-ho després de `page.goto`/`page.reload` és una condició de carrera:
     * la petició ajax de `$(document).ready(...)` es pot completar abans que
     * l'escolta s'hagi registrat si el servidor respon prou ràpid, deixant
     * `waitForResponse` esperant una resposta que ja ha passat.
     */
    async goto(): Promise<void> {
        await waitForDataTableReload(this.page, async () => {
            await this.page.goto('cache');
        });
    }

    async reload(): Promise<void> {
        await waitForDataTableReload(this.page, async () => {
            await this.page.reload();
        });
    }

    rows(): Locator {
        return this.page.locator('#table-caches tbody tr');
    }

    row(codi: string): Locator {
        return this.page.locator('#table-caches tbody tr', { hasText: codi });
    }

    private midaCell(row: Locator): Locator {
        return row.locator('td').nth(2);
    }

    async midaDe(row: Locator): Promise<number> {
        const text = (await this.midaCell(row).innerText()).trim();
        return text === '' ? 0 : Number(text);
    }

    async codiDe(row: Locator): Promise<string> {
        return (await row.locator('td').first().innerText()).trim();
    }

    /** Retorna la primera fila amb mida > 0, o `null` si cap cache té contingut actualment. */
    async primeraAmbMida(): Promise<Locator | null> {
        const count = await this.rows().count();
        for (let i = 0; i < count; i++) {
            const row = this.rows().nth(i);
            if ((await this.midaDe(row)) > 0) {
                return row;
            }
        }
        return null;
    }

    /** Buida la cache d'una fila concreta; el llistat es refresca automàticament (fnDraw). */
    async buidar(row: Locator): Promise<void> {
        await waitForDataTableReload(this.page, async () => {
            await row.getByRole('link', { name: /buidar/i }).click();
        });
    }

    /**
     * Buida totes les caches (botó de capçalera). Mostra un `confirm()`
     * natiu del navegador (`data-confirm`, gestionat per webutilConfirmEval)
     * abans de llançar la crida ajax.
     */
    async buidarTotes(): Promise<void> {
        this.page.once('dialog', (dialog) => dialog.accept());
        await this.page.locator('a[href*="/cache/all/buidar"]').click();
        await expectSuccessMessage(this.page);
    }
}
