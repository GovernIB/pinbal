import { test } from '@playwright/test';

export interface Credentials {
    username: string;
    password: string;
}

function readCredentials(envPrefix: string): Credentials | null {
    const username = process.env[`E2E_${envPrefix}_USERNAME`];
    const password = process.env[`E2E_${envPrefix}_PASSWORD`];
    if (!username || !password) return null;
    return { username, password };
}

/**
 * Credencials per rol, llegides de variables d'entorn (vegeu e2e/README.md):
 *   E2E_ADMIN_USERNAME / E2E_ADMIN_PASSWORD
 *   E2E_DELEGAT_USERNAME / E2E_DELEGAT_PASSWORD
 *   E2E_REPRESENTANT_USERNAME / E2E_REPRESENTANT_PASSWORD
 *   E2E_AUDITOR_USERNAME / E2E_AUDITOR_PASSWORD
 */
export const credentials = {
    admin: () => readCredentials('ADMIN'),
    delegat: () => readCredentials('DELEGAT'),
    representant: () => readCredentials('REPRESENTANT'),
    auditor: () => readCredentials('AUDITOR'),
};

/**
 * Salta el test (test.skip) si les credencials demanades no estan configurades.
 * Permet que la suite es pugui llistar/executar sense fallar en entorns on
 * encara no s'ha configurat un usuari d'aquell rol.
 */
export function requireCredentials(getter: () => Credentials | null, roleName: string): Credentials {
    const creds = getter();
    if (!creds) {
        test.skip(true, `Credencials no configurades per al rol "${roleName}". Vegeu e2e/README.md.`);
        throw new Error('unreachable: test.skip(true, ...) always throws');
    }
    return creds;
}

/** Sufix únic per a dades de prova (evita col·lisions entre execucions). */
export function uniqueSuffix(): string {
    return Date.now().toString(36) + Math.floor(Math.random() * 1000);
}

/**
 * Document del titular que el servidor fake de SCSP (pinbal-scsp-fake)
 * interpreta com a disparador d'una resposta d'error (CodigoEstado que no
 * comença per "00"), independentment del servei consultat. Vegeu
 * `fake.scsp.errorTriggerDoc` a FakeScspServer i FAKE_SCSP_SERVER.md.
 */
export const SCSP_FAKE_ERROR_TRIGGER_DOC = '00000000ERR';

export interface ConsultaSimpleConfig {
    /** Codi d'un servei assignat a l'entitat del rol delegat, amb el camp de document del titular actiu. */
    serveiCodi: string;
    /** Id numèric del procediment a seleccionar; si no es dona, s'agafa el primer disponible. */
    procedimentId?: string;
}

/**
 * Configuració opcional per al test de consulta simple contra el fake SCSP
 * (vegeu e2e/README.md). S'ha de definir E2E_CONSULTA_SERVEI_CODI amb el
 * codi d'un servei que el fake sàpiga respondre (Q2827003ATGSS001, SCDCPAJU,
 * SVDDGTVEHICULOSANCWS01 o SVDDGPCIWS02) i que estigui donat d'alta a
 * l'entorn de proves per a l'entitat del rol delegat.
 */
export function consultaSimpleConfig(): ConsultaSimpleConfig | null {
    const serveiCodi = process.env.E2E_CONSULTA_SERVEI_CODI;
    if (!serveiCodi) return null;
    return { serveiCodi, procedimentId: process.env.E2E_CONSULTA_PROCEDIMENT_ID };
}
