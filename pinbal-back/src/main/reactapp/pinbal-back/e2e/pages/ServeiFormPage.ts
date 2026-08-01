import { Page, expect } from '@playwright/test';

/**
 * Camps obligatoris (per validació de backend, vegeu `ServeiCommand`) del
 * formulari de pàgina completa `/servei/new` i `/servei/{codi}` (NO és una
 * modal, a diferència de la resta de manteniments; vegeu ServeisPage).
 *
 * Alguns camps són obligatoris només a nivell de backend encara que la JSP
 * no els marqui amb `required="true"` (scspFechaAlta, scspNumeroMaximoReenvios,
 * scspMaxSolicitudesPeticion, scspTimeout): cal omplir-los sempre o el
 * `POST /servei/save` fallarà la validació.
 */
export interface ServeiFormFields {
    codi: string;
    descripcio: string;
    /** Valor de l'atribut `cif` de l'emissor SCSP (seed e2e: `P0700000A`). */
    scspEmisorCif: string;
    scspVersionEsquema: string;
    /** Únics valors reals acceptats pel controlador: "XMLSignature" o "WS-Security". */
    scspTipoSeguridad: 'XMLSignature' | 'WS-Security';
    /** Alies de la clau privada (seed e2e: `e2e-clau-privada-activa`). */
    scspClaveFirmaAlies: string;
    /** Alies de la clau pública (seed e2e: `e2e-clau-publica`). */
    scspClaveCifradoAlies: string;
    /** Format dd/mm/yyyy. */
    scspFechaAlta: string;
    scspNumeroMaximoReenvios?: string;
    scspMaxSolicitudesPeticion?: string;
    scspTimeout?: string;
    /**
     * Almenys una de `scspUrlSincrona`/`scspUrlAsincrona` és obligatòria
     * (`ServeiUrlValidator`, validació creuada de `ServeiCommand`): sense
     * cap de les dues el `POST /servei/save` respon amb error de validació
     * i el formulari no torna al llistat.
     */
    scspUrlSincrona?: string;
    scspUrlAsincrona?: string;
}

export class ServeiFormPage {
    constructor(private readonly page: Page) {}

    async fillRequired(fields: ServeiFormFields): Promise<void> {
        const page = this.page;
        await page.locator('#codi').fill(fields.codi);
        await page.locator('#descripcio').fill(fields.descripcio);
        await page.locator('#scspEmisor').selectOption(fields.scspEmisorCif, { force: true });
        await page.locator('#scspVersionEsquema').fill(fields.scspVersionEsquema);
        await page.locator('#scspTipoSeguridad').selectOption(fields.scspTipoSeguridad, { force: true });
        await page.locator('#scspClaveFirma').selectOption(fields.scspClaveFirmaAlies, { force: true });
        await page.locator('#scspClaveCifrado').selectOption(fields.scspClaveCifradoAlies, { force: true });
        await page.locator('#scspFechaAlta').fill(fields.scspFechaAlta);
        await page.locator('#scspUrlSincrona').fill(fields.scspUrlSincrona ?? 'http://localhost:18080/scsp/sincrona');
        await page.locator('#scspNumeroMaximoReenvios').fill(fields.scspNumeroMaximoReenvios ?? '0');
        await page.locator('#scspMaxSolicitudesPeticion').fill(fields.scspMaxSolicitudesPeticion ?? '0');
        await page.locator('#scspTimeout').fill(fields.scspTimeout ?? '60');
    }

    /** Fa clic al botó "Guardar" del formulari (POST /servei/save) i espera tornar al llistat. */
    async submitAndExpectBackToList(): Promise<void> {
        await this.page.locator('form button[type="submit"]').click();
        await this.page.waitForURL(/\/servei(\?.*)?$/, { timeout: 15_000 });
    }

    /** Activa/desactiva la casella "Activa gestió XSD" (només visible en edició). */
    async setActivaGestioXsd(checked: boolean): Promise<void> {
        const checkbox = this.page.locator('#activaGestioXsd');
        if ((await checkbox.isChecked()) !== checked) {
            await checkbox.click({ force: true });
        }
    }

    /**
     * Obre la modal (Bootstrap simple, no iframe) per pujar un nou fitxer
     * XSD, l'emplena i l'envia. El servidor respon per AJAX amb un JSON
     * `{error: boolean, ...}`; si tot va bé la pàgina es recarrega tota
     * sola (`location.reload()` al JS de serveiForm.jsp).
     *
     * IMPORTANT: `fitxer.name` (el nom del fitxer que es puja) NO és el nom
     * amb què queda desat ni mostrat: `ServeiServiceImpl.xsdCreate()`
     * ignora `nomArxiu` i sempre reanomena el fitxer a un nom fix segons el
     * "tipus" triat (`ServeiXsdHelper.getXsdTipusNom()`: PETICIO ->
     * "peticion.xsd", RESPOSTA -> "respuesta.xsd"...) — és a propòsit (la
     * integració SCSP llegeix aquests fitxers per path/nom fix, no pel nom
     * que li doni l'administrador). Aquesta funció sempre selecciona la
     * primera opció real del `<select>` (`{index: 1}`, ja que l'índex 0 és
     * el placeholder buit), que es correspon amb el primer valor de
     * `XsdTipusEnumDto` (PETICIO) -> "peticion.xsd". Retorna aquest nom
     * final perquè el test no l'hagi de conèixer/duplicar.
     */
    async afegirXsd(fitxer: { name: string; content: string }): Promise<string> {
        const page = this.page;
        await page.locator('a[href*="/xsd/new"]').click();
        const modal = page.locator('#modal-xsd-form');
        await expect(modal).toBeVisible({ timeout: 15_000 });
        await expect(modal.locator('#tipus')).toBeVisible({ timeout: 15_000 });
        await modal.locator('#tipus').selectOption({ index: 1 }, { force: true });
        await modal.locator('#nomArxiu').setInputFiles({
            name: fitxer.name,
            mimeType: 'text/xml',
            buffer: Buffer.from(fitxer.content, 'utf-8'),
        });
        // La resposta és processada per JS: si no hi ha errors fa location.reload(). Cal
        // registrar el listener del 'load' ABANS del clic: waitForLoadState('load'), en canvi, es
        // resoldria immediatament (la pàgina actual ja hi és, no espera cap esdeveniment futur).
        const responsePromise = page.waitForResponse(
            (resp) => resp.url().includes('/xsd/save') && resp.request().method() === 'POST',
            { timeout: 15_000 },
        );
        const loadPromise = page.waitForEvent('load');
        await page.locator('#modal-boto-submit-xsd').click();
        await responsePromise;
        await loadPromise;
        return 'peticion.xsd';
    }
}
