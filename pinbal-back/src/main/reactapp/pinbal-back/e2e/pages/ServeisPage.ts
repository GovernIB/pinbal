import { Locator, Page, expect } from '@playwright/test';
import { AdminMaintenancePage } from './AdminMaintenancePage';

/**
 * Llistat de "Serveis" (administració). A diferència de la resta de
 * manteniments (vegeu AdminMaintenancePage), el formulari de creació/edició
 * NO és una modal: "Nou servei" i "Modificar" naveguen a una pàgina completa
 * (/servei/new, /servei/{codi}). Les accions d'activar/desactivar/esborrar
 * del llistat, en canvi, sí que segueixen el patró habitual (enllaç del menú
 * desplegable + recàrrega de la pàgina, que reactiva el DataTable), així que
 * es reutilitzen els mètodes heretats d'AdminMaintenancePage.
 */
export class ServeisPage extends AdminMaintenancePage {
    constructor(page: Page) {
        super(page, { url: 'servei', tableId: 'table-serveis' });
    }

    /** Navega a la pàgina completa de creació d'un servei nou. */
    async gotoNew(): Promise<void> {
        await this.page.goto('servei/new');
    }

    /** Navega a la pàgina completa de modificació d'un servei existent. */
    async gotoEdit(codi: string): Promise<void> {
        await this.page.goto(`servei/${codi}`);
    }

    /** Navega a la pàgina de gestió de camps/grups/regles d'un servei. */
    async gotoCamp(codi: string): Promise<void> {
        await this.page.goto(`servei/${codi}/camp`);
    }

    /** Navega a la pàgina de gestió del justificant d'un servei. */
    async gotoJustificant(codi: string): Promise<void> {
        await this.page.goto(`servei/${codi}/justificant`);
    }

    /**
     * Obre la modal (carregada per AJAX, no iframe) amb el llistat de
     * procediments associats a un servei, fent clic al botó "Procediments"
     * de la seva fila.
     */
    async obrirProcediments(codi: string): Promise<Locator> {
        const modal = this.page.locator('#modal-procediment-list');
        await this.row(codi).getByRole('link', { name: /procediments/i }).click();
        await expect(modal).toBeVisible({ timeout: 15_000 });
        await expect(modal.locator('.modal-body')).not.toBeEmpty({ timeout: 15_000 });
        return modal;
    }

    /** Tanca la modal de procediments oberta amb {@link obrirProcediments}. */
    async tancarProcediments(): Promise<void> {
        await this.page.locator('#modal-procediment-list .modal-footer button').click();
        await expect(this.page.locator('#modal-procediment-list')).not.toBeVisible();
    }
}
