package es.caib.pinbal.client.recobriment.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DadaEspecifica extends DadaEspecificaBasic {
    // Dades exteses: codi, nom, tipus, format
    private String etiqueta;
    private String comentari;
    private String valorDefecte;
    private boolean obligatori;
    private boolean modificable;
    private String campCondicionant;
    private String valorCondicionant;
    private Validacio validacio;
    private String grup;
    /**
     * Quan el tipus és ENUM, indica el tipus d'enumerat. És el valor que s'ha
     * d'emprar com a codi d'enumerat a l'obtenció dels seus valors
     * (/serveis/{serveiCodi}/camps/{campCodi}/enumerat/{enumCodi}).
     */
    private EnumTipus enumTipus;
    private boolean visible;
    /** Ordre del camp dins el seu grup. */
    private int ordre;

    public enum EnumTipus {
        ENUM,
        PAIS,
        PROVINCIA,
        MUNICIPI_3,
        MUNICIPI_5
    }

}
