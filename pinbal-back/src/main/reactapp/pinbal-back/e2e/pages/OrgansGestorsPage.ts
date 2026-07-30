import { Page } from '@playwright/test';
import { AdminMaintenancePage } from './AdminMaintenancePage';

/**
 * Llistat "Òrgans gestors" del representant (`/organgestor`, `organGestor.jsp`).
 * Pantalla només de lectura + filtre (no té CRUD des del rol representant: no
 * hi ha botó "Nou" ni accions d'edició per fila, vegeu `organGestor.jsp`);
 * l'única acció disponible és sincronitzar amb Dir3.
 */
export class OrgansGestorsPage extends AdminMaintenancePage {
    constructor(page: Page) {
        super(page, { url: 'organgestor', tableId: 'table-organs' });
    }

    /**
     * Clica "Actualitzar òrgans gestors de Dir3" (`#organgestor-boto-nou`,
     * enllaç ple `organgestor/sync/dir3`, sense ajax). En aquest entorn e2e
     * (H2, sense connectivitat real a Dir3) s'espera que fallarà amb un
     * missatge d'error controlat si l'entitat no té `unitatArrel` associada
     * (vegeu `OrganGestorController.syncDir3`) — l'objectiu d'aquest mètode
     * és només comprovar que l'acció existeix, és clicable, i que la pàgina
     * respon amb un missatge (èxit o error) sense trencar-se.
     */
    async actualitzarDir3(): Promise<void> {
        await this.page.locator('#organgestor-boto-nou').click();
    }
}
