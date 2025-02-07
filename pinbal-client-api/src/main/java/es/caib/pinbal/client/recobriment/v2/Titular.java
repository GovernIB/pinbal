package es.caib.pinbal.client.recobriment.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Titular {

    public enum DocumentTipus {
        CIF,
        CSV,
        DNI,
        NIE,
        NIF,
        Pasaporte,
        NumeroIdentificacion,
        Otros
    }

    private DocumentTipus documentTipus;
    private String documentNumero;
    private String nom;
    private String llinatge1;
    private String llinatge2;
    private String nomComplet;

}
