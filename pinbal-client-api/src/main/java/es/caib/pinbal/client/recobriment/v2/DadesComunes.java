package es.caib.pinbal.client.recobriment.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DadesComunes {

    public enum Consentiment {
        Si,
        Ley
    }

    private String entitatCif;
    private String procedimentCodi;
    private String serveiCodi;
    private Funcionari funcionari;
    private String departament;
    private Consentiment consentiment;
    private String finalitat;

}
