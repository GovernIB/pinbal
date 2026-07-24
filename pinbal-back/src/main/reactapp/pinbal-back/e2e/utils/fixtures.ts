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
 */
type PinbalFixtures = {
    adminPage: Page;
    delegatPage: Page;
    representantPage: Page;
    auditorPage: Page;
};

export const test = base.extend<PinbalFixtures>({
    adminPage: async ({ page }, use) => {
        const creds = requireCredentials(credentials.admin, 'admin');
        await login(page, creds);
        await use(page);
    },
    delegatPage: async ({ page }, use) => {
        const creds = requireCredentials(credentials.delegat, 'delegat');
        await login(page, creds);
        await use(page);
    },
    representantPage: async ({ page }, use) => {
        const creds = requireCredentials(credentials.representant, 'representant');
        await login(page, creds);
        await use(page);
    },
    auditorPage: async ({ page }, use) => {
        const creds = requireCredentials(credentials.auditor, 'auditor');
        await login(page, creds);
        await use(page);
    },
});

export { expect } from '@playwright/test';
