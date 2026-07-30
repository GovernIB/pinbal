import { Page } from '@playwright/test';
import { ScspMaintenancePage } from './ScspMaintenancePage';

/** Pàgina de manteniment "Claus privades" (`/scsp/clauprivada`, `core_clave_privada`). */
export class ClauPrivadaPage extends ScspMaintenancePage {
    constructor(page: Page) {
        super(page, { url: 'scsp/clauprivada', tableId: 'table-claus' });
    }
}
