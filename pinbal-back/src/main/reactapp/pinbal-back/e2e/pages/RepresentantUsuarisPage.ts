import { FrameLocator, Page } from '@playwright/test';
import { AdminMaintenancePage } from './AdminMaintenancePage';
import { waitForDataTableReload } from '../utils/datatable';
import { clickModalFooterButton, modalFrame, waitForModalClosed } from '../utils/modal';
import { expectSuccessMessage } from '../utils/messages';

/**
 * Llistat "Usuaris" del representant (`/representant/usuari`,
 * `representantUsuaris.jsp`). Segueix el mateix patró de llistat+modal amb
 * iframe que `AdminMaintenancePage`, però ni el botó "Nou" ni el botó
 * "Guardar" del formulari tenen un `id` estable (a diferència de
 * `entitatForm.jsp`), així que cal localitzar-los per href/text.
 */
export class RepresentantUsuarisPage extends AdminMaintenancePage {
    constructor(page: Page) {
        super(page, { url: 'representant/usuari', tableId: 'table-users' });
    }

    async openNew(): Promise<FrameLocator> {
        await this.page.locator('a[href$="/representant/usuari/new"]').click();
        return modalFrame(this.page);
    }

    async save(): Promise<void> {
        await waitForDataTableReload(this.page, async () => {
            await clickModalFooterButton(this.page, /guardar|modificar/i);
        });
        await waitForModalClosed(this.page);
        await expectSuccessMessage(this.page);
    }

    /** Navega a la pantalla de permisos de l'usuari (no és una modal, és una pàgina pròpia). */
    async obrirPermisos(usuariCodi: string): Promise<void> {
        await this.row(usuariCodi).getByRole('link', { name: /permisos/i }).click();
    }
}
