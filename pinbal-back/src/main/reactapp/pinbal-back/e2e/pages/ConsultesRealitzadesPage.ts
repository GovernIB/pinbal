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

    /** Localitzador de la fila del llistat que conté el text donat (p.ex. un scspPeticionId). */
    row(text: string): Locator {
        return this.rows().filter({ hasText: text });
    }

    /**
     * Filtra el llistat pel número de petició donat (`#scspPeticionId`) i n'espera la fila
     * corresponent. Retorna `null` si no apareix dins el termini (consulta de mostra no
     * sembrada/esborrada en aquest entorn) en lloc de deixar-ho a mans del cridant.
     *
     * Cal fer servir açò (en lloc d'escanejar `rows()`/les primeres N files) per a qualsevol
     * consulta de mostra sembrada amb un `scspPeticionId` conegut i estable
     * (p.ex. `PBL_E2E_SIMPLE_OK`): `rows()` només conté les files RENDERITZADES de la pàgina
     * actual del DataTable (10 per defecte), i amb prou consultes generades per altres tests
     * (`global-setup.ts`, altres specs) una consulta de mostra concreta pot acabar fora de la
     * primera pàgina, fent que qualsevol cerca sense filtrar no la trobi mai encara que existeixi.
     *
     * NOTA: `waitForDataTableReload` (dins `filtrar()`) només espera la resposta AJAX, no el
     * redraw del DataTable (que succeeix una mica després, en processar-ne el resultat) -- per
     * això aquí es fa `waitFor` (amb polling) en lloc d'un simple `.count()`.
     */
    async cercarPerPeticio(peticionId: string, timeout = 10_000): Promise<Locator | null> {
        await this.filtrar(async (form) => {
            await form.locator('#scspPeticionId').fill(peticionId);
        });
        const fila = this.row(peticionId);
        const trobat = await fila.first().waitFor({ state: 'visible', timeout }).then(() => true).catch(() => false);
        return trobat ? fila : null;
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

}
