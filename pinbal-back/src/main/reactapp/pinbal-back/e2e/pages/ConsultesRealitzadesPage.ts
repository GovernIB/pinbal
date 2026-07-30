import { Download, FrameLocator, Locator, Page, expect } from '@playwright/test';
import { waitForDataTableReload, waitForInitialDataTableLoad } from '../utils/datatable';
import { activeModal, modalFrame, waitForModalClosed } from '../utils/modal';

/**
 * Llistat "Consultes realitzades" del rol administrador (`/admin/consulta`,
 * adminConsultes.jsp). Segueix el mateix patró DataTable server-side +
 * modal amb iframe que `AdminMaintenancePage`, però no és una pantalla CRUD
 * (no hi ha "Nou"/"Guardar"/"Esborrar"), per això no n'estén la classe:
 * només reutilitza les mateixes convencions (taula `#table-consultes`,
 * formulari `#form-filtre`, modal amb iframe).
 */
export class ConsultesRealitzadesPage {
    constructor(private readonly page: Page) {}

    async goto(): Promise<void> {
        await this.page.goto('admin/consulta');
        await waitForInitialDataTableLoad(this.page);
    }

    /**
     * Files de dades del DataTable. Exclou explícitament la fila placeholder
     * que DataTables injecta quan no hi ha resultats (`<tr><td
     * class="dataTables_empty">...`): com que aquesta fila també és un
     * `tbody tr`, sense aquest filtre `rows()` la comptabilitzava com si fos
     * una fila de dades real (count=1) quan la taula estava buida, fent que
     * qualsevol codi que hi busqués un enllaç d'acció (p.ex. "Detalls")
     * s'esperés inútilment el timeout complet en lloc de detectar
     * correctament que no hi havia cap fila.
     */
    rows(): Locator {
        return this.page.locator('#table-consultes tbody tr').filter({ hasNot: this.page.locator('td.dataTables_empty') });
    }

    isEmpty(): Locator {
        return this.page.locator('#table-consultes td.dataTables_empty');
    }

    /** Omple el formulari de filtre (`#form-filtre`) i el sotmet, esperant el refresc del DataTable. */
    async filtrar(fillFn: (form: Locator) => Promise<void>): Promise<void> {
        const form = this.page.locator('#form-filtre');
        await fillFn(form);
        await waitForDataTableReload(this.page, async () => {
            await form.locator('#filtrar').click();
        });
    }

    async netejarFiltre(): Promise<void> {
        await waitForDataTableReload(this.page, async () => {
            await this.page.locator('#netejar-filtre').click();
        });
    }

    /** Checkbox (amagat, estilitzat via iosCheckbox) de "Veure històric". */
    private historicCheckbox(): Locator {
        return this.page.locator('#titolCheck');
    }

    /** Element clicable real del toggle "Veure històric" (el checkbox original queda amagat). */
    private historicToggle(): Locator {
        return this.page.locator('#ios-checkbox-titolCheck .ios-ui-select');
    }

    async isHistoricActiu(): Promise<boolean> {
        return this.historicCheckbox().isChecked();
    }

    /** Alterna entre "recents" i "històric", esperant el refresc del DataTable resultant. */
    async alternarHistoric(): Promise<void> {
        await waitForDataTableReload(this.page, async () => {
            await this.historicToggle().click();
        });
    }

    /** Clica "Exportar a Excel" i n'espera la descàrrega. */
    async exportarExcel(): Promise<Download> {
        const [download] = await Promise.all([
            this.page.waitForEvent('download'),
            this.page.getByRole('link', { name: /exportar a excel/i }).click(),
        ]);
        return download;
    }

    /** Obre la modal de detall d'una fila (botó "Detalls") i en retorna el FrameLocator. */
    async obrirDetall(row: Locator): Promise<FrameLocator> {
        await row.getByRole('link', { name: /detalls/i }).click();
        return modalFrame(this.page);
    }

    /** Tanca la modal de detall actualment oberta pel botó de tancar del capçal (fora de l'iframe). */
    async tancarDetall(): Promise<void> {
        await activeModal(this.page).locator('.modal-header .close').click();
        await waitForModalClosed(this.page);
    }

    /**
     * Cerca, entre les primeres `maxFiles` files del llistat, la primera
     * consulta SIMPLE (no múltiple) i n'obre el detall. Es distingeix una
     * consulta simple perquè `adminConsultaInfo.jsp` (a diferència de
     * `adminConsultaMultipleInfo.jsp`) renderitza la pestanya amb id
     * `#dadesGeneriquesTab`. Retorna `null` si no en troba cap.
     */
    async obrirPrimerDetallSimple(maxFiles = 15): Promise<{ row: Locator; frame: FrameLocator } | null> {
        const total = Math.min(await this.rows().count(), maxFiles);
        for (let i = 0; i < total; i++) {
            const row = this.rows().nth(i);
            const frame = await this.obrirDetall(row);
            if (await frame.locator('#dadesGeneriquesTab').count()) {
                return { row, frame };
            }
            await this.tancarDetall();
        }
        return null;
    }

    /**
     * Primera fila d'una consulta MÚLTIPLE amb el justificant (PDF concatenat)
     * descarregable. Locator buit (count 0) si no en troba cap.
     */
    filaAmbJustificantMultipleDescarregable(): Locator {
        return this.rows().filter({ has: this.page.locator('a.btn-justificant-multiple') }).first();
    }

    /**
     * Primera fila d'una consulta SIMPLE amb el justificant descarregable.
     * L'endpoint `/admin/consulta/{id}/justificant` exigia que
     * l'administrador actués com a delegat de l'entitat de la consulta
     * (`EntitatHelper.isDelegatEntitatActual`) - bug corregit: ara accepta
     * també `RolHelper.isRolActualAdministrador` (vegeu `ConsultaAdminController`).
     * Locator buit (count 0) si no en troba cap.
     */
    filaAmbJustificantSimpleDescarregable(): Locator {
        return this.rows().filter({ has: this.page.locator('a.btn-justificant') }).first();
    }

    /** Fila (qualsevol) amb l'enllaç de descàrrega del zip de missatges XML. */
    filaAmbXmlZip(): Locator {
        return this.rows().filter({ has: this.page.locator('a[href*="/xmlZip"]') }).first();
    }

    /** Fila amb el justificant en estat d'error (icona d'alerta + menú "Veure error"/"Re-intentar"). */
    filaAmbJustificantError(): Locator {
        return this.rows().filter({ has: this.page.locator('a.justificant-reintentar') }).first();
    }
}
