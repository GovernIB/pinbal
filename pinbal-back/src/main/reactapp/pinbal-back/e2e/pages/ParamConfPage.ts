import { Page } from '@playwright/test';
import { ScspMaintenancePage } from './ScspMaintenancePage';

/** Pàgina de manteniment "Paràmetres SCSP" (`/scsp/paramconf`, `core_parametro_configuracion`). */
export class ParamConfPage extends ScspMaintenancePage {
    constructor(page: Page) {
        super(page, { url: 'scsp/paramconf', tableId: 'table-params' });
    }
}
