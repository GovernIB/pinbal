import { FrameLocator, Page } from '@playwright/test';
import { AdminMaintenancePage } from './AdminMaintenancePage';
import { waitForDataTableReload } from '../utils/datatable';
import { clickModalFooterButton, modalFrame, waitForModalClosed } from '../utils/modal';
import { expectSuccessMessage } from '../utils/messages';

/**
 * Llistat "Procediments" del representant (`/procediment`,
 * `procedimentList.jsp`). Igual que `RepresentantUsuarisPage`, el botó "Nou"
 * i el botó "Guardar" del formulari no tenen `id`, així que se sobreescriuen
 * `openNew`/`save`. Els enllaços "Activar"/"Desactivar"/"Esborrar" d'aquesta
 * pantalla NO són ajax (són `<a href="procediment/{id}/enable">` normals que
 * provoquen una redirecció completa de tornada al llistat), però com que
 * `AdminMaintenancePage.activar/desactivar/esborrar` només esperen la
 * següent resposta POST que contingui "/datatable" (i el llistat recarregat
 * després de la redirecció torna a disparar aquesta petició en inicialitzar
 * el DataTable), els mètodes heretats funcionen igualment sense sobreescriure'ls.
 */
export class ProcedimentsPage extends AdminMaintenancePage {
    constructor(page: Page) {
        super(page, { url: 'procediment', tableId: 'table-procediments' });
    }

    async openNew(): Promise<FrameLocator> {
        await this.page.locator('a[href$="/procediment/new"]').click();
        return modalFrame(this.page);
    }

    async save(): Promise<void> {
        await waitForDataTableReload(this.page, async () => {
            await clickModalFooterButton(this.page, /guardar|modificar/i);
        });
        await waitForModalClosed(this.page);
        await expectSuccessMessage(this.page);
    }

    /**
     * Obre la modal "Crear fill" (clonar) d'una fila. Aquesta acció només és
     * visible per a procediments amb `codiSia` informat (vegeu
     * `template-accions` a `procedimentList.jsp`: `{{#codiSia}}`).
     */
    async crearFill(text: string): Promise<FrameLocator> {
        await this.openRowAction(text, /crear fill/i);
        return modalFrame(this.page);
    }

    /** Comprova si la fila té disponible l'acció "Crear fill" (cal tenir codiSia informat). */
    async teCrearFillDisponible(text: string): Promise<boolean> {
        const fila = this.row(text);
        await fila.locator('a.dropdown-toggle, button.dropdown-toggle').click();
        const disponible = await fila.getByRole('link', { name: /crear fill/i }).isVisible().catch(() => false);
        // Tanca el menú desplegable (clicant el mateix "toggle") perquè no
        // interfereixi amb accions posteriors (p.ex. tornar a obrir el menú).
        await fila.locator('a.dropdown-toggle, button.dropdown-toggle').click();
        return disponible;
    }

    /** Obre la pantalla de "Gestió de serveis" (no és una modal) d'una fila. */
    async obrirServeis(text: string): Promise<void> {
        await this.row(text).getByRole('link', { name: /serveis/i }).click();
    }
}
