import { Page } from '@playwright/test';
import { ScspMaintenancePage } from './ScspMaintenancePage';

/** Pàgina de manteniment "Emissors certificats" (`/scsp/emissorcert`, `core_emisor_certificado`). */
export class EmissorCertPage extends ScspMaintenancePage {
    constructor(page: Page) {
        super(page, { url: 'scsp/emissorcert', tableId: 'table-emisors' });
    }
}
