import { FrameLocator, Page } from '@playwright/test';
import { AdminMaintenancePage } from './AdminMaintenancePage';
import { clickModalFooterButton, modalFrame, waitForModalClosed } from '../utils/modal';
import { waitForDataTableReload } from '../utils/datatable';
import { expectSuccessMessage } from '../utils/messages';

/**
 * Base per als manteniments de configuració SCSP de PINBAL ("Paràmetres
 * SCSP", "Emissors certificats", "Claus públiques" i "Claus privades", totes
 * sota `/scsp/...`). Segueixen el mateix patró general que
 * {@link AdminMaintenancePage} (llistat DataTable + modal amb iframe), però
 * a diferència d'altres manteniments (p.ex. `/entitat`) els seus JSP
 * (`paramConfList.jsp`, `emissorCertList.jsp`, `clauPublicaList.jsp`,
 * `clauPrivadaList.jsp` i els `*Form.jsp` corresponents) no posen `id` ni al
 * enllaç "Nou"/"Nova" ni al botó "Guardar" del formulari (només duen classes
 * Bootstrap), així que cal localitzar-los per atribut `href` i per text
 * respectivament en lloc de per `id`.
 */
export class ScspMaintenancePage extends AdminMaintenancePage {
    constructor(page: Page, config: { url: string; tableId: string }) {
        super(page, { url: config.url, tableId: config.tableId });
    }

    /** Obre la modal de creació (enllaç "Nou"/"Nova", sense id) i en retorna el FrameLocator del formulari. */
    async openNew(): Promise<FrameLocator> {
        await this.page.locator(`a[href$="${this.config.url}/new"]`).click();
        return modalFrame(this.page);
    }

    /**
     * Fa clic al botó "Guardar" de la modal oberta (sense id, localitzat pel
     * seu text) i espera que el DataTable es refresqui, la modal es tanqui i
     * aparegui el missatge d'èxit.
     */
    async save(): Promise<void> {
        await waitForDataTableReload(this.page, async () => {
            await clickModalFooterButton(this.page, /guardar/i);
        });
        await waitForModalClosed(this.page);
        await expectSuccessMessage(this.page);
    }

    /** Fa clic al botó "Tancar"/cancel·lar de la modal oberta, sense desar. */
    async cancel(): Promise<void> {
        await clickModalFooterButton(this.page, /tancar/i);
        await waitForModalClosed(this.page);
    }
}
