package es.caib.pinbal.client.recobriment.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DadaEspecificaBasic {

    /** Valor de 'format' per a un camp FILE que conté un fitxer binari. */
    public static final String FORMAT_FILE_BINARI = "BINARI";
    /** Valor de 'format' per a un camp FILE que conté un fitxer XML. */
    public static final String FORMAT_FILE_XML = "XML";

    protected String codi;
    protected String nom;
    protected DadaTipusEnum tipus;
    /**
     * Format del camp. Depèn del tipus:
     * <ul>
     *     <li>DATE: patró de la data (p. ex. dd/MM/yyyy).</li>
     *     <li>FILE: {@value #FORMAT_FILE_BINARI} o {@value #FORMAT_FILE_XML}. En tots dos casos el valor
     *     s'ha d'enviar a les dades específiques de la sol·licitud codificat en base64. Els fitxers XML
     *     s'han de correspondre amb l'esquema del servei, i s'insereixen com a nodes XML a la petició.</li>
     * </ul>
     */
    protected String format;

}
