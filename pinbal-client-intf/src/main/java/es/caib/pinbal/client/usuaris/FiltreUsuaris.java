package es.caib.pinbal.client.usuaris;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiltreUsuaris {

    private String codi;
    private String nom;
    private String nif;
    private String departament;
    private Boolean isRepresentant;
    private Boolean isDelegat;
    private Boolean isAuditor;
    private Boolean isAplicacio;

}
