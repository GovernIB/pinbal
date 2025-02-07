package es.caib.pinbal.client.recobriment.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DadaEspecifica {

    private String codi;
    private String nom;
    private DadaTipusEnum tipus;
    private String format;
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
