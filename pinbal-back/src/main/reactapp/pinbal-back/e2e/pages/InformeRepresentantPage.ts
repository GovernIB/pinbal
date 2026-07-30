import { Locator, Page } from '@playwright/test';
import { AdminMaintenancePage } from './AdminMaintenancePage';
import { waitForDataTableReload } from '../utils/datatable';
import { selectAjaxSuggestOption } from '../utils/select2';

/**
 * Informe "Usuaris agrupats per òrgan gestor, procediment i servei" del
 * representant (`/informeRepresentant`, `informeRepresentant.jsp`).
 *
 * NO té relació amb `pages/InformesPage.ts` (aquell cobreix `/informe`,
 * el llistat d'informes Excel descarregables del rol administrador — una
 * pantalla completament diferent, tot i el nom semblant).
 *
 * El llistat és un DataTable server-side normal (reutilitzable via
 * `AdminMaintenancePage`), però els tres camps de filtre (òrgan gestor,
 * procediment, servei) són selects "suggest" amb cerca ajax
 * (`pbl:inputSuggest`), no camps de text ni selects estàtics.
 */
export class InformeRepresentantPage extends AdminMaintenancePage {
    constructor(page: Page) {
        super(page, { url: 'informeRepresentant', tableId: 'table-informe-representant' });
    }

    async filtrarPerProcediment(procedimentCodi: string): Promise<void> {
        await selectAjaxSuggestOption(this.page, 'procedimentId', procedimentCodi, procedimentCodi);
        await waitForDataTableReload(this.page, async () => {
            await this.page.locator('#form-filtre button[type="submit"]').click();
        });
    }

    async filtrarPerServei(serveiCodi: string): Promise<void> {
        await selectAjaxSuggestOption(this.page, 'serveiCodi', serveiCodi, serveiCodi);
        await waitForDataTableReload(this.page, async () => {
            await this.page.locator('#form-filtre button[type="submit"]').click();
        });
    }

    columnaServeiCodi(): Locator {
        return this.page.locator('#table-informe-representant tbody tr td:nth-child(5)');
    }
}
