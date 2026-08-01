import { test, expect } from '../../utils/fixtures';
import { CachePage } from '../../pages/CachePage';
import { waitForDataTableReload } from '../../utils/datatable';

/**
 * Gestió de la cache de l'aplicació (/cache, secció administrador).
 *
 * No hi ha cap entitat de "cache" a la base de dades: les mides que es
 * mostren són en memòria (ehcache/Spring Cache), per la qual cosa és
 * possible que, en un entorn just arrencat, totes les caches tinguin mida 0.
 * Per cobrir el cas "buidar una cache amb mida > 0", si no en trobam cap
 * accedim primer com a delegat al llistat de consultes simples (`/consulta`)
 * per forçar que es carreguin (i, per tant, es popular alguna cache de
 * serveis/procediments/entitat).
 */
test.describe('Configurar cache (administrador)', () => {
    test('el llistat de caches es mostra correctament', async ({ adminPage: page, delegatPage }) => {
        const cache = new CachePage(page);
        await cache.goto();

        // Les caches (ehcache/Spring Cache) es registren dinàmicament al primer ús d'un mètode
        // @Cacheable, no n'hi ha cap de predeclarada: si aquest test és el primer de tota la
        // suite a executar-se (executant en paral·lel amb altres fitxers, l'ordre no és
        // determinista), el `CacheManager` pot no tenir encara cap cache registrada i el
        // llistat surt buit. Igual que als altres tests d'aquest fitxer, si això passa forcem
        // que se'n registri alguna accedint, com a delegat, al llistat de consultes simples.
        if ((await cache.rows().count()) === 0) {
            await waitForDataTableReload(delegatPage, async () => {
                await delegatPage.goto('consulta');
            });
            await cache.reload();
        }

        const count = await cache.rows().count();
        expect(count).toBeGreaterThan(0);

        const primera = cache.rows().first();
        await expect(primera).toBeVisible();
        // Columnes: codi, descripció, mida, accions. El codi mai és buit.
        expect((await cache.codiDe(primera)).length).toBeGreaterThan(0);
        // La mida ha de ser sempre un nombre (0 o més), mai text arbitrari.
        expect(Number.isNaN(await cache.midaDe(primera))).toBe(false);
    });

    test('buidar una cache amb mida > 0 posa la seva mida a 0', async ({ adminPage: page, delegatPage }) => {
        const cache = new CachePage(page);
        await cache.goto();

        let target = await cache.primeraAmbMida();
        if (!target) {
            // Poblam alguna cache accedint, com a delegat, al llistat de consultes simples.
            await waitForDataTableReload(delegatPage, async () => {
                await delegatPage.goto('consulta');
            });

            await cache.reload();
            target = await cache.primeraAmbMida();
        }

        test.skip(!target, 'Cap cache té mida > 0, ni tan sols després d\'accedir al llistat de consultes simples com a delegat.');
        if (!target) return;

        const codi = await cache.codiDe(target);
        await cache.buidar(target);

        const filaDesprés = cache.row(codi);
        await expect(filaDesprés).toBeVisible();
        expect(await cache.midaDe(filaDesprés)).toBe(0);
    });

    test('buidar totes les caches posa totes les mides a 0', async ({ adminPage: page, delegatPage }) => {
        const cache = new CachePage(page);
        await cache.goto();

        // Ens assegurem que almenys hi ha activitat prèvia a alguna cache,
        // per tal que aquest test sigui significatiu (si no, ja estarien totes a 0 abans d'actuar).
        if (!(await cache.primeraAmbMida())) {
            await waitForDataTableReload(delegatPage, async () => {
                await delegatPage.goto('consulta');
            });
            await cache.reload();
        }

        await cache.buidarTotes();

        // El botó de "buidar totes" és fora del DataTable i no en refresca sol el llistat: cal recarregar manualment.
        await cache.reload();

        const count = await cache.rows().count();
        for (let i = 0; i < count; i++) {
            expect(await cache.midaDe(cache.rows().nth(i))).toBe(0);
        }
    });
});
