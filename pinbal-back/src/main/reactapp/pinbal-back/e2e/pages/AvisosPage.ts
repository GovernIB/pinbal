import { FrameLocator, Locator, Page, expect } from '@playwright/test';
import { waitForDataTableReload } from '../utils/datatable';
import { clickModalFooterButton, modalFrame } from '../utils/modal';
import { expectSuccessMessage } from '../utils/messages';

/**
 * Pantalla de manteniment d'avisos (/avis, secció administrador).
 *
 * No estén `AdminMaintenancePage`: avisList.jsp/avisForm.jsp segueixen un
 * patró significativament diferent de la resta de manteniments (entitats,
 * usuaris...), tal com es desprèn de llegir-los conjuntament amb
 * webutil.modal.js i AvisController:
 *  - Els botons "Nova avís" i "Modificar" no tenen `id` propi (a diferència
 *    de p.ex. `#btNovaEntitat`); es localitzen pel seu rol/text.
 *  - El formulari es desa amb `data-refresh-pagina="true"`, que fa que
 *    `webutil.modal.js` faci un `window.location.reload()` complet en
 *    desar-se amb èxit, en lloc del flux ajax habitual (tancar modal +
 *    refrescar `#contingut-missatges` + `fnDraw` del DataTable). Per això
 *    cal esperar una navegació completa, no només el tancament de la modal.
 *  - Els enllaços d'activar/desactivar individuals (`avis/{id}/enable` i
 *    `.../disable`) NO tenen `data-toggle="ajax"`: són una navegació normal
 *    (redirecció del servidor cap a `/avis` amb el missatge flash), no una
 *    petició ajax amb `fnDraw`.
 *  - L'acció d'esborrar individual sí que intercepta el clic amb un
 *    `confirm()` natiu (classe `confirm-esborrar`), però tampoc és ajax:
 *    si es confirma, la navegació per defecte de l'enllaç continua.
 *  - Les accions massives (`avis/selected/enable|disable|delete`) sí que són
 *    ajax real (`$.post`), i refresquen el DataTable via `dt.ajax.reload()`,
 *    però el missatge de resultat es mostra en un `#avis-alerts` propi de la
 *    pantalla (JS inline), NO al `#contingut-missatges` global.
 *  - La barra d'avisos del decorador (`#contingut-avisos`, present a
 *    qualsevol pàgina de l'aplicació, no només a `/avis`) només mostra els
 *    avisos amb `actiu=true` i no caducats (`AvisService.findActive()`,
 *    filtrat per data_final), independentment de si aquesta pàgina concreta
 *    és `/avis`.
 */
export class AvisosPage {
    constructor(private readonly page: Page) {}

    /**
     * Navega al llistat i espera la primera càrrega del DataTable.
     *
     * Registram l'escolta de la resposta ABANS de navegar (via
     * `waitForDataTableReload`): fer-ho després de `page.goto(...)` crea una
     * condició de carrera (la petició ajax de `$(document).ready(...)` es
     * pot completar abans que l'escolta s'hagi registrat si el servidor
     * respon prou ràpid), que és el que provocava el `TimeoutError:
     * page.waitForResponse` intermitent d'aquest llistat.
     */
    async goto(): Promise<void> {
        await waitForDataTableReload(this.page, async () => {
            await this.page.goto('avis');
        });
    }

    rows(): Locator {
        return this.page.locator('#table-avisos tbody tr');
    }

    row(assumpte: string): Locator {
        return this.page.locator('#table-avisos tbody tr', { hasText: assumpte });
    }

    /** Contingut de la barra d'avisos del decorador (present a totes les pàgines de l'aplicació). */
    barraAvisos(): Locator {
        return this.page.locator('#contingut-avisos');
    }

    async openNew(): Promise<FrameLocator> {
        await this.page.getByRole('link', { name: /nou av[íi]s/i }).click();
        return modalFrame(this.page);
    }

    async openEdit(assumpte: string): Promise<FrameLocator> {
        const fila = this.row(assumpte);
        await fila.locator('a.dropdown-toggle, button.dropdown-toggle').click();
        await fila.getByRole('link', { name: /modificar/i }).click();
        return modalFrame(this.page);
    }

    /**
     * Fa clic a "Guardar" al peu de la modal i espera la recàrrega completa de
     * pàgina (`data-refresh-pagina="true"`). Registram l'escolta del
     * DataTable ABANS de clicar (mateixa raó que a {@link goto}: si s'espera
     * després que la navegació/recàrrega ja hagi acabat, es pot perdre la
     * resposta ajax i esgotar el timeout encara que tot hagi anat bé).
     */
    async guardar(): Promise<void> {
        await waitForDataTableReload(this.page, async () => {
            await Promise.all([
                this.page.waitForURL(/\/avis(?:\?.*)?$/, { timeout: 20_000 }),
                clickModalFooterButton(this.page, /guardar/i),
            ]);
        });
        await expectSuccessMessage(this.page);
    }

    /**
     * Retorna `true` si la fila mostra actualment l'acció "Desactivar" (és a
     * dir, l'avís és actiu). Obre i torna a tancar el menú d'accions perquè
     * quedi en el mateix estat (tancat) en què es troben els altres mètodes
     * d'aquesta classe quan comencen.
     */
    async esActiu(assumpte: string): Promise<boolean> {
        const fila = this.row(assumpte);
        await fila.locator('a.dropdown-toggle, button.dropdown-toggle').click();
        const actiu = await fila.getByRole('link', { name: /^desactivar/i }).isVisible();
        await fila.locator('a.dropdown-toggle, button.dropdown-toggle').click();
        return actiu;
    }

    /** Activa una fila (navegació completa, no ajax) i espera que el llistat es torni a carregar. */
    async activar(assumpte: string): Promise<void> {
        const fila = this.row(assumpte);
        await fila.locator('a.dropdown-toggle, button.dropdown-toggle').click();
        await waitForDataTableReload(this.page, async () => {
            await Promise.all([
                this.page.waitForURL(/\/avis(?:\?.*)?$/, { timeout: 20_000 }),
                fila.getByRole('link', { name: /^activar/i }).click(),
            ]);
        });
        await expectSuccessMessage(this.page);
    }

    /** Desactiva una fila (navegació completa, no ajax) i espera que el llistat es torni a carregar. */
    async desactivar(assumpte: string): Promise<void> {
        const fila = this.row(assumpte);
        await fila.locator('a.dropdown-toggle, button.dropdown-toggle').click();
        await waitForDataTableReload(this.page, async () => {
            await Promise.all([
                this.page.waitForURL(/\/avis(?:\?.*)?$/, { timeout: 20_000 }),
                fila.getByRole('link', { name: /^desactivar/i }).click(),
            ]);
        });
        await expectSuccessMessage(this.page);
    }

    /** Esborra una fila (confirmació nativa + navegació completa) i espera que el llistat es torni a carregar. */
    async esborrar(assumpte: string): Promise<void> {
        const fila = this.row(assumpte);
        await fila.locator('a.dropdown-toggle, button.dropdown-toggle').click();
        this.page.once('dialog', (dialog) => dialog.accept());
        await waitForDataTableReload(this.page, async () => {
            await Promise.all([
                this.page.waitForURL(/\/avis(?:\?.*)?$/, { timeout: 20_000 }),
                fila.getByRole('link', { name: /esborrar/i }).click(),
            ]);
        });
        await expectSuccessMessage(this.page);
    }

    // --- Selecció / accions massives ---

    private rowSelector(assumpte: string): Locator {
        return this.row(assumpte).locator('.row-selector');
    }

    /**
     * Marca/desmarca la fila. `avisList.jsp` actualitza la icona de manera
     * SÍNCRONA però persisteix la selecció al servidor amb un `$.post(
     * 'avis/selection/add|remove', ...)` "fire-and-forget" (sense esperar
     * la resposta abans de deixar interactuar l'usuari). Les accions
     * massives (`#bulk-enable`/`#bulk-disable`) no envien els ids
     * seleccionats al cos de la petició: el servidor els llegeix de la
     * selecció ja persistida a sessió. Si s'obre el menú d'accions
     * massives abans que aquesta petició de selecció hagi arribat al
     * servidor (condició de carrera real, observada sota càrrega —
     * diversos workers de Playwright competint pel mateix JBoss), l'avís
     * seleccionat just abans queda fora de l'acció massiva. S'espera aquí
     * la resposta per eliminar la condició de carrera al test.
     */
    async seleccionar(assumpte: string): Promise<void> {
        const responsePromise = this.page.waitForResponse((resp) => /\/avis\/selection\/(add|remove)(\?|$)/.test(resp.url()));
        await this.rowSelector(assumpte).click();
        await responsePromise;
    }

    /**
     * Missatge de resultat de les accions massives (viu en `#avis-alerts`,
     * no al `#contingut-missatges` global). Els missatges s'acumulen a
     * `#avis-alerts` sense eliminar els anteriors, així que si es fan
     * diverses accions massives en un mateix test cal quedar-se amb
     * l'últim (`.last()`) per evitar una violació de "strict mode" quan
     * n'hi ha més d'un visible alhora.
     */
    missatgeMassiu(): Locator {
        return this.page.locator('#avis-alerts .alert-success').last();
    }

    private async obrirMenuAccionsMassives(): Promise<void> {
        await this.page.locator('a.dropdown-toggle, button.dropdown-toggle').filter({ has: this.page.locator('#seleccioCountAdd') }).click();
    }

    async activarSeleccio(): Promise<void> {
        await this.obrirMenuAccionsMassives();
        await waitForDataTableReload(this.page, async () => {
            await this.page.locator('#bulk-enable').click();
        });
        await expect(this.missatgeMassiu()).toBeVisible({ timeout: 10_000 });
    }

    async desactivarSeleccio(): Promise<void> {
        await this.obrirMenuAccionsMassives();
        await waitForDataTableReload(this.page, async () => {
            await this.page.locator('#bulk-disable').click();
        });
        await expect(this.missatgeMassiu()).toBeVisible({ timeout: 10_000 });
    }
}
