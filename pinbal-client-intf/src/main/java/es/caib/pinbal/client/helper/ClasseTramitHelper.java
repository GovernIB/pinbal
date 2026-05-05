package es.caib.pinbal.client.helper;

public class ClasseTramitHelper {

    public static String getClasseTramitById(Short id) {
        if (id == null) return null;

        switch (id) {
            case 0: return  "Proves";
            case 2: return  "Recursos Humans";
            case 3: return  "Tributari";
            case 14: return "Sancionador";
            case 19: return "Afiliació i cotització a la Seguretat Social";
            case 20: return "Autoritzacions, llicències, concessions i homologacions";
            case 21: return "Ajudes, Beques i Subvencions";
            case 22: return "Certificats";
            case 23: return "Contractació pública";
            case 24: return "Convenis de Col·laboració i Comunicacions administratives";
            case 25: return "Gestió Econòmica i Patrimonial";
            case 26: return "Declaracions i comunicacions dels interessats";
            case 27: return "Inspectora";
            case 28: return "Premis";
            case 29: return "Prestacions";
            case 30: return "Registres i Censos";
            case 31: return "Responsabilitat patrimonial i altres sol·licituds d'indemnització";
            case 32: return "Revisió d'Actes administratius i Recursos";
            case 33: return "Suggeriments, Queixes, Denúncies i Informació a la ciutadania";
            case 34: return "Duaner";
            case 99: return "Resolució d'incidències";
            default: return null;
        }
    }

}
