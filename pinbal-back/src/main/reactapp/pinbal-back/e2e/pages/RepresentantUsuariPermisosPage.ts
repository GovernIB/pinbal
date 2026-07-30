import { FrameLocator, Locator, Page, expect } from '@playwright/test';
import { modalFrame } from '../utils/modal';

/**
 * Pantalla de permisos (procediment+servei) d'un usuari concret del
 * representant (`/representant/usuari/{codi}/permis`,
 * `representantUsuariPermis.jsp` + `representantUsuariPermisForm.jsp`).
 *
 * A diferència dels llistats "de manteniment", aquesta pantalla es
 * renderitza sencera al servidor (no és un DataTable): tant "Esborrar tots"
 * com "Esborrar seleccionats" com el formulari "Afegir" (que és una modal
 * amb iframe, però amb `data-refresh-pagina="true"`) acaben fent una
 * navegació completa de la pàgina, no una recàrrega ajax.
 */
export class RepresentantUsuariPermisosPage {
    constructor(
        private readonly page: Page,
        private readonly usuariCodi: string,
    ) {}

    async goto(): Promise<void> {
        await this.page.goto(`representant/usuari/${this.usuariCodi}/permis`);
    }

    rows(): Locator {
        return this.page.locator('#permisos tbody tr');
    }

    row(procedimentCodi: string, serveiCodi: string): Locator {
        return this.rows().filter({ hasText: procedimentCodi }).filter({ hasText: serveiCodi });
    }

    /** Obre la modal "Afegir" (permet triar un procediment i, després, un servei disponible). */
    async obrirAfegir(): Promise<FrameLocator> {
        await this.page.locator(`a[href$="/representant/usuari/${this.usuariCodi}/permis/afegir"]`).click();
        return modalFrame(this.page);
    }

    /**
     * Selecciona un procediment i un servei disponible dins la modal "Afegir"
     * (oberta prèviament amb {@link obrirAfegir}) i confirma l'alta. Com que
     * la modal té `data-refresh-pagina="true"`, l'èxit provoca una recàrrega
     * completa de la pàgina pare: cal registrar l'espera de l'esdeveniment
     * `load` ABANS de fer clic perquè no hi hagi una condició de carrera amb
     * el tancament/recàrrega.
     */
    async afegirServei(frame: FrameLocator, procedimentId: string, serveiCodis: string | string[]): Promise<void> {
        await frame.locator('#select-procediment').selectOption(procedimentId, { force: true });
        for (const serveiCodi of Array.isArray(serveiCodis) ? serveiCodis : [serveiCodis]) {
            const fila = frame.locator('#permisosBody tr').filter({ hasText: serveiCodi });
            await expect(fila).toBeVisible({ timeout: 15_000 });
            await fila.locator('.selectorpare').click();
        }

        const reloaded = this.page.waitForEvent('load', { timeout: 20_000 });
        await frame.locator('#addSelectedForm button[type="submit"]').click();
        await reloaded;
    }

    /** Denega l'accés d'una fila concreta (enllaç ple, provoca navegació completa). */
    async denegarAcces(procedimentCodi: string, serveiCodi: string): Promise<void> {
        await this.row(procedimentCodi, serveiCodi).getByRole('link', { name: /denegar acc/i }).click();
    }

    async seleccionar(procedimentCodi: string, serveiCodi: string): Promise<void> {
        await this.row(procedimentCodi, serveiCodi).locator('.selectorpare').click();
    }

    seleccioCount(): Locator {
        return this.page.locator('#seleccioCount');
    }

    async esborrarSeleccionats(): Promise<void> {
        this.page.once('dialog', (dialog) => dialog.accept());
        await this.page.locator('.confirm-esborrar-selected').click();
    }

    async esborrarTots(): Promise<void> {
        this.page.once('dialog', (dialog) => dialog.accept());
        await this.page.getByRole('link', { name: /esborrar tots els permisos/i }).click();
    }
}
