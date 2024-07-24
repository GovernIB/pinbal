package es.caib.pinbal.core.dto;

public enum JustificantEstat {
    PENDENT, // 0 - Hi ha justificant però encara no s'ha pogut generar/custodiar
    OK, // 1 - Hi ha justificant i ja està generat i custodiat
    ERROR, // 2 - Hi ha justificant però hi ha hagut errors al generar o custodiar
    NO_DISPONIBLE, // 3 - Aquesta consulta no te justificant associat
    OK_NO_CUSTODIA // 4 - Hi ha justificant i la custòdia està deshabilitada
}
