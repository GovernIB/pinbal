/**
 * Generació d'un CIF sintàcticament vàlid per a proves.
 *
 * `EmissorCertCommand.cif` porta l'anotació `@DocumentIdentitat(documentTipus
 * = CIF)` (vegeu `es.caib.pinbal.back.validation.DocumentIdentitatValidator`),
 * que calcula un dígit/lletra de control i rebutja qualsevol valor que no el
 * compleixi. Per això no es pot fer servir un `uniqueSuffix()` arbitrari com
 * a CIF (a diferència del CIF d'entitat, que no porta aquesta validació):
 * cal calcular el caràcter de control amb el mateix algorisme que el
 * validador.
 */

/** Taula de conversió estàndard de dígit a lletra de control de NIF/CIF. */
const CONTROL_NUMERO_A_LETRA = 'JABCDEFGHI';

/**
 * Calcula un CIF vàlid de la forma `P` + `digits7` + lletra de control,
 * reproduint l'algorisme de `DocumentIdentitatValidator.validacioNif`. La
 * lletra `P` (organismes públics) exigeix que el caràcter de control sigui
 * sempre una lletra (mai un dígit), cosa que simplifica el càlcul.
 */
export function buildTestCif(digits7: string): string {
    if (!/^\d{7}$/.test(digits7)) {
        throw new Error(`buildTestCif necessita exactament 7 dígits, s'ha rebut "${digits7}"`);
    }
    let parA = 0;
    for (let i = 1; i < 7; i += 2) {
        parA += Number(digits7[i]);
    }
    let nonB = 0;
    for (let i = 0; i < 7; i += 2) {
        let doubled = 2 * Number(digits7[i]);
        if (doubled > 9) doubled = 1 + (doubled - 10);
        nonB += doubled;
    }
    const parcialC = parA + nonB;
    const digitE = parcialC % 10;
    const digitD = digitE > 0 ? 10 - digitE : 0;
    return `P${digits7}${CONTROL_NUMERO_A_LETRA[digitD]}`;
}

/** Deriva 7 dígits (únics entre execucions) a partir del rellotge i un factor aleatori. */
export function uniqueCifDigits(): string {
    const n = (Date.now() + Math.floor(Math.random() * 1000)) % 10_000_000;
    return String(n).padStart(7, '0');
}

/** CIF de prova únic i vàlid, a punt per emprar directament en un formulari. */
export function uniqueTestCif(): string {
    return buildTestCif(uniqueCifDigits());
}
