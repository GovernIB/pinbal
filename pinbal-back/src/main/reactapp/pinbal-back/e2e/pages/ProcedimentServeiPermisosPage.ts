import { FrameLocator, Page } from '@playwright/test';
import { AdminMaintenancePage } from './AdminMaintenancePage';
import { waitForDataTableReload } from '../utils/datatable';
import { clickModalFooterButton, modalFrame, waitForModalClosed } from '../utils/modal';
import { expectSuccessMessage } from '../utils/messages';
import { selectAjaxSuggestOption } from '../utils/select2';

/**
 * Permisos d'un servei concret dins d'un procediment
 * (`/procediment/{procedimentId}/servei/{serveiCodi}/permis`,
 * `procedimentServeiPermisos.jsp`): llistat (amb filtre reutilitzable via
 * `AdminMaintenancePage`) + alta ("Nou permís", modal amb cerca ajax
 * d'usuaris de l'entitat) + denegar accés (enllaç ple per fila, no menú
 * desplegable).
 */
export class ProcedimentServeiPermisosPage extends AdminMaintenancePage {
    constructor(page: Page, procedimentId: string | number, serveiCodi: string) {
        super(page, {
            url: `procediment/${procedimentId}/servei/${serveiCodi}/permis`,
            tableId: 'table-serveis-permis',
        });
    }

    async openNew(): Promise<FrameLocator> {
        await this.page.locator('a[href$="/permis/new"]').click();
        return modalFrame(this.page);
    }

    async seleccionarUsuari(frame: FrameLocator, usuariCodi: string): Promise<void> {
        await selectAjaxSuggestOption(frame, 'usuariCodi', usuariCodi, usuariCodi);
    }

    async save(): Promise<void> {
        await waitForDataTableReload(this.page, async () => {
            await clickModalFooterButton(this.page, /guardar/i);
        });
        await waitForModalClosed(this.page);
        await expectSuccessMessage(this.page);
    }

    /** Denega l'accés d'una fila (enllaç ple, no menú desplegable). */
    async denegarAcces(usuariCodi: string): Promise<void> {
        await waitForDataTableReload(this.page, async () => {
            await this.row(usuariCodi).getByRole('link', { name: /denegar/i }).click();
        });
    }
}
