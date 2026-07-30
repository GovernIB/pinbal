import { test as base, Page } from '@playwright/test';
import { login } from './auth';
import { credentials, requireCredentials } from './env';

/**
 * Fixtures que proporcionen una `Page` ja autenticada amb un usuari del rol
 * indicat. Si les credencials del rol no estan configurades (vegeu
 * e2e/README.md), el test es marca com a "skipped" en lloc de fallar.
 *
 * Ús:
 *   import { test, expect } from '../utils/fixtures';
 *   test('...', async ({ delegatPage }) => { ... });
 *
 * IMPORTANT: cada fixture crea el seu propi `BrowserContext` aïllat (via
 * `browser.newContext()`) en lloc de compartir la `page` integrada de
 * Playwright. Si totes depenguessin de la mateixa `page` (com abans), un
 * test que sol·licités dos rols alhora (p.ex. `{ adminPage, delegatPage }`)
 * faria login DUES VEGADES sobre la MATEIXA pestanya/sessió: la segona
 * sobreescriuria la primera. A més, forçar-hi un logout previ per
 * "corregir-ho" xoca amb un bug real de l'aplicació (`UsuariController.
 * logout()` neteja totes les cookies manualment en lloc de fer un logout
 * correcte contra Keycloak, cosa que corromp el seguiment de l'"state" OIDC
 * i provoca "Bad Request" / "state parameter invalid" al següent login;
 * vegeu BUGS_APLICACIO.md). Amb un context per rol, cada un té la seva
 * pròpia sessió/cookies real, com si fossin dues pestanyes de navegadors
 * diferents — sense necessitat de cap logout entremig.
 */
type PinbalFixtures = {
    adminPage: Page;
    delegatPage: Page;
    representantPage: Page;
    auditorPage: Page;
};

export const test = base.extend<PinbalFixtures>({
    adminPage: async ({ browser }, use) => {
        const creds = requireCredentials(credentials.admin, 'admin');
        const context = await browser.newContext();
        const page = await context.newPage();
        await login(page, creds);
        await use(page);
        await context.close();
    },
    delegatPage: async ({ browser }, use) => {
        const creds = requireCredentials(credentials.delegat, 'delegat');
        const context = await browser.newContext();
        const page = await context.newPage();
        await login(page, creds);
        await use(page);
        await context.close();
    },
    representantPage: async ({ browser }, use) => {
        const creds = requireCredentials(credentials.representant, 'representant');
        const context = await browser.newContext();
        const page = await context.newPage();
        await login(page, creds);
        await use(page);
        await context.close();
    },
    auditorPage: async ({ browser }, use) => {
        const creds = requireCredentials(credentials.auditor, 'auditor');
        const context = await browser.newContext();
        const page = await context.newPage();
        await login(page, creds);
        await use(page);
        await context.close();
    },
});

export { expect } from '@playwright/test';
