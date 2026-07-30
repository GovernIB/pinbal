import { FrameLocator, Locator, Page } from '@playwright/test';
import { waitForDataTableReload } from '../utils/datatable';
import { clickModalFooterButtonById, modalFrame, waitForModalClosed } from '../utils/modal';
import { expectSuccessMessage } from '../utils/messages';

/**
 * Base per a les pantalles de manteniment JSP de PINBAL: totes segueixen el
 * mateix patró (llistat amb DataTable server-side + formulari en una modal
 * amb iframe + accions per fila en un menú desplegable). Concentrar aquest
 * patró aquí és el que permet que, quan aquestes pantalles es migrin a
 * React, només calgui reescriure aquesta classe base (i les subclasses que
 * sobreescriuen selectors concrets) sense tocar cap fitxer .spec.ts.
 *
 * Les subclasses només haurien d'afegir mètodes específics del domini
 * (p.ex. `activarMassiu`); el CRUD genèric ja el proporciona aquesta classe.
 */
export class AdminMaintenancePage {
    constructor(
        protected readonly page: Page,
        protected readonly config: {
            /** Path relatiu (p.ex. '/entitat') on viu el llistat. */
            url: string;
            /** id de la taula DataTable del llistat (p.ex. 'table-entitats'). */
            tableId: string;
            /** id del botó "Nou/Nova" que obre la modal de creació. */
            newButtonId?: string;
            /** id del botó "Guardar" dins el peu de la modal. */
            saveButtonId?: string;
        },
    ) {}

    /**
     * Navega al llistat i espera la primera càrrega del DataTable.
     *
     * Important: registram l'escolta de la resposta ABANS de navegar (via
     * `waitForDataTableReload`), no després. Fer `page.goto(...)` i tot
     * seguit `page.waitForResponse(...)` és una condició de carrera: la
     * petició ajax de `$(document).ready(...)` es dispara abans de l'esdeveniment
     * `load` (del qual depèn `page.goto`), així que en un servidor prou ràpid
     * la resposta pot arribar i completar-se abans que l'escolta s'hagi
     * registrat, deixant `waitForResponse` esperant una resposta que ja ha
     * passat fins a esgotar el timeout.
     */
    async goto(): Promise<void> {
        await waitForDataTableReload(this.page, async () => {
            await this.page.goto(this.config.url);
        });
    }

    /** Localitzador de la fila del llistat que conté el text donat. */
    row(text: string): Locator {
        return this.page.locator(`#${this.config.tableId} tbody tr`, { hasText: text });
    }

    rows(): Locator {
        return this.page.locator(`#${this.config.tableId} tbody tr`);
    }

    isEmpty(): Locator {
        return this.page.locator(`#${this.config.tableId} td.dataTables_empty`);
    }

    /** Obre la modal de creació ("Nou"/"Nova") i en retorna el FrameLocator del formulari. */
    async openNew(): Promise<FrameLocator> {
        if (!this.config.newButtonId) {
            throw new Error('newButtonId no configurat per a aquesta pàgina');
        }
        await this.page.locator(`#${this.config.newButtonId}`).click();
        return modalFrame(this.page);
    }

    /** Obre el menú d'accions d'una fila i fa clic a l'enllaç amb el text/patró donat. */
    async openRowAction(text: string, actionName: string | RegExp): Promise<void> {
        const fila = this.row(text);
        await fila.locator('a.dropdown-toggle, button.dropdown-toggle').click();
        await fila.getByRole('link', { name: actionName }).click();
    }

    /** Obre el formulari de modificació d'una fila (menú d'accions > "Modificar") i en retorna el frame. */
    async openEdit(text: string): Promise<FrameLocator> {
        await this.openRowAction(text, /modificar/i);
        return modalFrame(this.page);
    }

    /**
     * Fa clic al botó "Guardar" de la modal oberta i espera que el DataTable
     * es refresqui, la modal es tanqui i aparegui el missatge d'èxit.
     */
    async save(): Promise<void> {
        if (!this.config.saveButtonId) {
            throw new Error('saveButtonId no configurat per a aquesta pàgina');
        }
        await waitForDataTableReload(this.page, async () => {
            await clickModalFooterButtonById(this.page, this.config.saveButtonId!);
        });
        await waitForModalClosed(this.page);
        await expectSuccessMessage(this.page);
    }

    /** Desactiva la fila que conté `text` (menú d'accions > "Desactivar"), esperant el refresc del llistat. */
    async desactivar(text: string): Promise<void> {
        const fila = this.row(text);
        await fila.locator('a.dropdown-toggle, button.dropdown-toggle').click();
        await waitForDataTableReload(this.page, async () => {
            await fila.getByRole('link', { name: /desactivar/i }).click();
        });
    }

    /** Activa la fila que conté `text` (menú d'accions > "Activar"), esperant el refresc del llistat. */
    async activar(text: string): Promise<void> {
        const fila = this.row(text);
        await fila.locator('a.dropdown-toggle, button.dropdown-toggle').click();
        await waitForDataTableReload(this.page, async () => {
            await fila.getByRole('link', { name: /^activar/i }).click();
        });
    }

    /**
     * Esborra la fila que conté `text` (menú d'accions > "Esborrar"), acceptant
     * el diàleg de confirmació nadiu del navegador. No falla si l'acció
     * "esborrar" no és present (algunes instal·lacions la desactiven).
     */
    async esborrar(text: string): Promise<boolean> {
        const fila = this.row(text);
        await fila.locator('a.dropdown-toggle, button.dropdown-toggle').click();
        const linkEsborrar = fila.getByRole('link', { name: /esborrar/i });
        if (!(await linkEsborrar.isVisible().catch(() => false))) {
            return false;
        }
        this.page.once('dialog', (dialog) => dialog.accept());
        await waitForDataTableReload(this.page, async () => {
            await linkEsborrar.click();
        });
        return true;
    }

    /**
     * Executa `fillFn` sobre el formulari de filtre (`#form-filtre`) i en
     * fa submit, esperant el refresc del DataTable.
     */
    async filtrar(fillFn: (form: Locator) => Promise<void>): Promise<void> {
        const form = this.page.locator('#form-filtre');
        await fillFn(form);
        await waitForDataTableReload(this.page, async () => {
            await form.locator('button[type="submit"]').click();
        });
    }

    /** Neteja el filtre fent clic a "#netejar-filtre", esperant el refresc del DataTable. */
    async netejarFiltre(): Promise<void> {
        await waitForDataTableReload(this.page, async () => {
            await this.page.locator('#netejar-filtre').click();
        });
    }
}
