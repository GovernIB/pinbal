package es.caib.pinbal.client.procediments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcedimentBasic {

    private String codi;
    private String nom;
    private boolean actiu;
    private String organGestorDir3;
}
