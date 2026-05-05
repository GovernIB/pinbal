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

}
