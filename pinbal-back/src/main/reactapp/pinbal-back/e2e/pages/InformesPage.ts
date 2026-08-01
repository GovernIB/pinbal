import { Download, Page, expect } from '@playwright/test';

/**
 * Pàgina d'informes (rol administrador), `/informe` (informeList.jsp). Cada
 * informe es genera clicant un enllaç/botó "Generar" que dispara la
 * descàrrega directa d'un fitxer .xls (Content-Disposition "Inline" però
 * amb un content-type que el navegador no sap renderitzar, per la qual cosa
 * Chromium el tracta igualment com una descàrrega real).
 *
 * Nota important: el nom de fitxer de l'"Informe general d'estat" NO es
 * correspon amb el text mostrat a la UI: el fitxer generat és
 * `informeServeis.xls` (vegeu InformeGeneralEstatExcelView.java), igual que
 * el de l'informe "Serveis disponibles". Els tests ho comproven
 * explícitament.
 */
export class InformesPage {
    constructor(private readonly page: Page) {}

    async goto(): Promise<void> {
        await this.page.goto('informe');
        await expect(this.page.locator('body')).toBeVisible();
    }

    /** Localitza la "well" (secció) que conté el text donat (nom de l'informe). */
    private seccio(text: string | RegExp) {
        return this.page.locator('.well', { hasText: text });
    }

    /** Clica el "Generar" (enllaç directe) d'una secció d'informe i n'espera la descàrrega. */
    async generarViaEnllac(textSeccio: string | RegExp): Promise<Download> {
        const [download] = await Promise.all([
            this.page.waitForEvent('download'),
            this.seccio(textSeccio).getByRole('link', { name: /generar/i }).click(),
        ]);
        return download;
    }

    /**
     * Genera l'"Informe general d'estat": obre la modal de filtre de dates,
     * omple un interval i confirma. Retorna la descàrrega resultant.
     *
     * `#dataInici`/`#dataFi` (`informeList.jsp`) inicialitzen el seu propi
     * bootstrap-datepicker amb un `$('.datepicker').datepicker()` inline,
     * independent del `webutilDatepickerEval()` genèric — el mateix patró
     * (i el mateix problema) que `<pbl:inputDate>` a `estadistiques.jsp`
     * (vegeu `EstadistiquesPage.filtrar()`). `.fill()` només escriu el
     * `value` de l'input sense actualitzar el model intern del datepicker
     * (`$(el).data('datepicker').dates` queda buit), i el valor arriba buit
     * al servidor. Cal `pressSequentially` (tecla a tecla) perquè el
     * listener `keyup` del datepicker vagi construint el seu model alhora.
     */
    async generarInformeGeneralEstat(dataInici: string, dataFi: string): Promise<Download> {
        await this.seccio(/informe general d'estat/i).getByRole('button', { name: /generar/i }).click();

        const modal = this.page.locator('#modal-filtre-dates');
        await expect(modal).toBeVisible();
        await modal.locator('#dataInici').clear();
        await modal.locator('#dataInici').pressSequentially(dataInici);
        await modal.locator('#dataInici').press('Escape');
        await modal.locator('#dataFi').clear();
        await modal.locator('#dataFi').pressSequentially(dataFi);
        await modal.locator('#dataFi').press('Escape');

        const [download] = await Promise.all([
            this.page.waitForEvent('download'),
            modal.getByRole('button', { name: /generar/i }).click(),
        ]);
        return download;
    }
}
