package es.caib.pinbal.client.recobriment.v2;

import es.caib.pinbal.client.recobriment.v2.DadesComunes.Consentiment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Solicitant {

    private String identificador;
    private String nom;

    private String unitatTramitadora;
    private String codiUnitatTramitadora;
    private String procedimentCodi;
    private String procedimentNom;
    private Boolean automatitzat;
    private String classeTramit;
    private String finalitat;
    private Consentiment consentiment;
    private Funcionari funcionari;
}
