import { FrameLocator, Locator, Page, expect } from '@playwright/test';
import { AdminMaintenancePage } from './AdminMaintenancePage';
import { waitForDataTableReload } from '../utils/datatable';
import { clickModalFooterButton, modalFrame, waitForModalClosed } from '../utils/modal';
import { expectSuccessMessage } from '../utils/messages';

/**
 * Gestió de serveis d'un procediment concret (`/procediment/{id}/servei`,
 * `procedimentServeis.jsp`): llistat de serveis afegits al procediment, amb
 * accions per afegir/esborrar, migrar a un altre servei, editar el "codi de
 * procediment addicional" (inline, no modal) i navegar als permisos.
 */
export class ProcedimentServeisPage extends AdminMaintenancePage {
    constructor(page: Page, procedimentId: string | number) {
        super(page, { url: `procediment/${procedimentId}/servei`, tableId: 'table-serveis' });
    }

    async openNew(): Promise<FrameLocator> {
        await this.page.locator('a[href$="/servei/new"]').click();
        return modalFrame(this.page);
    }

    async save(): Promise<void> {
        await waitForDataTableReload(this.page, async () => {
            await clickModalFooterButton(this.page, /guardar/i);
        });
        await waitForModalClosed(this.page);
        await expectSuccessMessage(this.page);
    }

    /** Esborra (desactiva) un servei del procediment, acceptant el diàleg de confirmació natiu. */
    async esborrar(serveiCodi: string): Promise<boolean> {
        this.page.once('dialog', (dialog) => dialog.accept());
        await waitForDataTableReload(this.page, async () => {
            await this.row(serveiCodi).getByRole('link', { name: /esborrar/i }).click();
        });
        return true;
    }

    /** Obre la modal de migració d'un servei i selecciona el servei de destí. */
    async migrar(serveiCodiOrigen: string, serveiCodiDesti: string): Promise<void> {
        await this.row(serveiCodiOrigen).getByRole('link', { name: /migrar/i }).click();
        const frame = await modalFrame(this.page);
        await frame.locator('#serveiCodiDesti').selectOption(serveiCodiDesti, { force: true });
        await waitForDataTableReload(this.page, async () => {
            await clickModalFooterButton(this.page, /migrar/i);
        });
        await waitForModalClosed(this.page);
        await expectSuccessMessage(this.page);
    }

    /** Edita, in-line (sense modal), el "codi de procediment addicional" d'un servei. */
    async editarCodiAddicional(serveiCodi: string, valor: string): Promise<void> {
        const input = this.page.locator(`#procedimentCodi_${serveiCodi}`);
        const botoEditar = this.page.locator(`button.edit-codi-procediment[data-codi-servei="${serveiCodi}"]`);
        await botoEditar.click();
        await expect(input).toBeEnabled();
        await input.fill(valor);
        const responsePromise = this.page.waitForResponse(
            (resp) => resp.url().includes('/procedimentCodi') && resp.status() === 200,
        );
        await botoEditar.click();
        await responsePromise;
    }

    codiAddicionalInput(serveiCodi: string): Locator {
        return this.page.locator(`#procedimentCodi_${serveiCodi}`);
    }

    /** Navega (pàgina completa, no modal) a la pantalla de permisos d'un servei. */
    async obrirPermisos(serveiCodi: string): Promise<void> {
        await this.row(serveiCodi).getByRole('link', { name: /permisos/i }).click();
    }
}
