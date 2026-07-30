import { Page } from '@playwright/test';
import { ScspMaintenancePage } from './ScspMaintenancePage';

/** Pàgina de manteniment "Claus públiques" (`/scsp/claupublica`, `core_clave_publica`). */
export class ClauPublicaPage extends ScspMaintenancePage {
    constructor(page: Page) {
        super(page, { url: 'scsp/claupublica', tableId: 'table-claus' });
    }
}
