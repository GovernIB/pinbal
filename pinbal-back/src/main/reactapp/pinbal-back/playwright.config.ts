import { defineConfig, devices } from '@playwright/test';
import { config as loadEnv } from 'dotenv';
import path from 'node:path';

/**
 * Configuració de Playwright per als tests E2E de PINBAL.
 *
 * Aquests tests exerciten la interfície JSP existent (servida per JBoss a
 * /pinbalback), no l'aplicació React d'aquest projecte. Necessiten un entorn
 * PINBAL real en marxa (JBoss + BD + Keycloak), per exemple via
 * `docker-compose up` a l'arrel del repositori. Vegeu e2e/README.md.
 */
loadEnv({ path: path.resolve(import.meta.dirname, 'e2e/.env.e2e'), quiet: true });

// Es força una barra final: com que baseURL té un path (/pinbalback), sense
// la barra final `page.goto('entitat')` (relatiu, SENSE barra inicial —
// vegeu els comentaris a cada spec/page-object) es resoldria segons les
// regles WHATWG de resolució d'URL retallant l'últim segment de path en
// lloc d'afegir-s'hi. Amb la barra final, "entitat" s'annexa correctament
// com a http://host:8080/pinbalback/entitat.
const baseURL = (process.env.E2E_BASE_URL ?? 'http://localhost:8080/pinbalback').replace(/\/*$/, '/');

export default defineConfig({
    testDir: './e2e/tests',
    globalSetup: './e2e/global-setup.ts',
    fullyParallel: false,
    forbidOnly: !!process.env.CI,
    retries: process.env.CI ? 1 : 0,
    workers: process.env.CI ? 1 : undefined,
    reporter: [['html', { open: 'never' }], ['list']],
    timeout: 45_000,
    expect: {
        timeout: 10_000,
    },
    use: {
        baseURL,
        trace: 'on-first-retry',
        screenshot: 'only-on-failure',
        video: 'retain-on-failure',
        actionTimeout: 10_000,
    },
    projects: [
        {
            name: 'chromium',
            use: { ...devices['Desktop Chrome'] },
        },
    ],
});
